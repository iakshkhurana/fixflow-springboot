<template>
  <div>
    <!-- Hero Banner -->
    <div class="dashboard-hero">
      <div>
        <div class="hero-greeting">Hello, {{ authStore.user?.fullName?.split(' ')[0] }} 👋</div>
        <div class="hero-subtitle">Here's what's happening with your buildings today.</div>
      </div>
      <div class="hero-badge" v-if="stats">
        <div class="hero-badge-num">{{ (stats.totalOpen ?? 0) + (stats.totalAssigned ?? 0) + (stats.totalInProgress ?? 0) }}</div>
        <div class="hero-badge-label">Active Requests</div>
      </div>
    </div>

    <!-- Stats Overview -->
    <StatsOverview :stats="stats" />

    <!-- Charts Row -->
    <div class="charts-row" v-if="stats">
      <div class="card chart-card">
        <h3 class="chart-title">Requests by Category</h3>
        <Bar v-if="categoryChartData" :data="categoryChartData" :options="chartOptions" />
      </div>

      <div class="card chart-card">
        <h3 class="chart-title">Open by Priority</h3>
        <Bar v-if="priorityChartData" :data="priorityChartData" :options="chartOptions" />
      </div>
    </div>

    <!-- Requests Table -->
    <div class="card">
      <div class="table-header">
        <h3 class="table-title">All Requests</h3>
        <div class="filters">
          <select v-model="filterStatus" class="form-control filter-select" @change="applyFilter">
            <option value="">All Statuses</option>
            <option v-for="s in statuses" :key="s" :value="s">{{ s }}</option>
          </select>
          <select v-model="filterPriority" class="form-control filter-select">
            <option value="">All Priorities</option>
            <option v-for="p in priorities" :key="p" :value="p">{{ p }}</option>
          </select>
        </div>
      </div>

      <div v-if="requestStore.loading" class="loading">Loading requests...</div>
      <div v-else-if="filteredRequests.length === 0" class="empty-table">
        No requests match the selected filters.
      </div>
      <div v-else class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th>#</th>
              <th>Title</th>
              <th>Category</th>
              <th>Priority</th>
              <th>Status</th>
              <th>Building</th>
              <th>Assigned To</th>
              <th>Created</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="req in filteredRequests"
              :key="req.id"
              @click="router.push(`/requests/${req.id}`)"
            >
              <td class="id-cell">#{{ req.id }}</td>
              <td class="title-cell">{{ req.title }}</td>
              <td><span class="category-tag">{{ req.category }}</span></td>
              <td>
                <span class="priority-badge" :class="`priority-${req.priority?.toLowerCase()}`">
                  {{ req.priority }}
                </span>
              </td>
              <td><StatusBadge :status="req.status" /></td>
              <td>{{ req.buildingName }}</td>
              <td>{{ req.assignedToName || '—' }}</td>
              <td>{{ formatDate(req.createdAt) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Bar } from 'vue-chartjs'
import {
  Chart as ChartJS,
  CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend
} from 'chart.js'
import StatsOverview from '../components/StatsOverview.vue'
import StatusBadge from '../components/StatusBadge.vue'
import { useAuthStore } from '../stores/authStore'
import { useRequestStore } from '../stores/requestStore'
import api from '../services/api'

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend)

const router = useRouter()
const authStore = useAuthStore()
const requestStore = useRequestStore()

const stats = ref(null)
const filterStatus = ref('')
const filterPriority = ref('')

const statuses = ['OPEN', 'ASSIGNED', 'IN_PROGRESS', 'RESOLVED', 'CLOSED']
const priorities = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL']

const chartOptions = {
  responsive: true,
  plugins: { legend: { display: false } },
  scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } }
}

const categoryColors = ['#6C63FF', '#10B981', '#F59E0B', '#EF4444', '#FF6B6B', '#06B6D4']

const categoryChartData = computed(() => {
  if (!stats.value?.categoryBreakdown) return null
  return {
    labels: stats.value.categoryBreakdown.map(c => c.category),
    datasets: [{
      data: stats.value.categoryBreakdown.map(c => c.count),
      backgroundColor: categoryColors,
      borderRadius: 6
    }]
  }
})

const priorityChartData = computed(() => {
  if (!stats.value?.priorityBreakdown) return null
  const colorMap = { LOW: '#10B981', MEDIUM: '#F59E0B', HIGH: '#F97316', CRITICAL: '#EF4444' }
  return {
    labels: stats.value.priorityBreakdown.map(p => p.priority),
    datasets: [{
      data: stats.value.priorityBreakdown.map(p => p.count),
      backgroundColor: stats.value.priorityBreakdown.map(p => colorMap[p.priority] || '#7F8C8D'),
      borderRadius: 6
    }]
  }
})

const filteredRequests = computed(() => {
  return requestStore.requests.filter(r => {
    const statusMatch = !filterStatus.value || r.status === filterStatus.value
    const priorityMatch = !filterPriority.value || r.priority === filterPriority.value
    return statusMatch && priorityMatch
  })
})

onMounted(async () => {
  await Promise.all([
    loadStats(),
    requestStore.fetchAllRequests()
  ])
})

async function loadStats() {
  try {
    const { data } = await api.get('/dashboard/stats')
    stats.value = data
  } catch (e) {
    console.error('Failed to load dashboard stats', e)
  }
}

function applyFilter() {
  requestStore.fetchAllRequests(filterStatus.value || null)
}

function formatDate(dateStr) {
  if (!dateStr) return '—'
  return new Date(dateStr).toLocaleDateString('en-US', {
    month: 'short', day: 'numeric', year: 'numeric'
  })
}
</script>

<style scoped>
.dashboard-hero {
  background: var(--gradient-dark);
  border-radius: var(--radius-lg);
  padding: 28px 32px;
  margin-bottom: 28px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: relative;
  overflow: hidden;
}

.dashboard-hero::before {
  content: '';
  position: absolute;
  top: -60px;
  right: -60px;
  width: 200px;
  height: 200px;
  border-radius: 50%;
  background: rgba(108, 99, 255, 0.3);
}

.dashboard-hero::after {
  content: '';
  position: absolute;
  bottom: -40px;
  right: 100px;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: rgba(255, 107, 107, 0.15);
}

.hero-greeting {
  font-size: 26px;
  font-weight: 800;
  color: #fff;
  letter-spacing: -0.02em;
}

.hero-subtitle {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  margin-top: 4px;
}

.hero-badge {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 12px;
  padding: 12px 20px;
  text-align: center;
  color: white;
  position: relative;
  z-index: 1;
}

.hero-badge-num {
  font-size: 32px;
  font-weight: 800;
  line-height: 1;
}

.hero-badge-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  margin-top: 2px;
}

.charts-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 24px;
}

.chart-card {
  padding: 24px;
}

.chart-title {
  font-size: 15px;
  font-weight: 700;
  color: var(--text);
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.chart-title::before {
  content: '';
  width: 4px;
  height: 16px;
  background: var(--gradient);
  border-radius: 2px;
  display: inline-block;
}

.table-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.table-title {
  font-size: 15px;
  font-weight: 700;
  color: var(--text);
  display: flex;
  align-items: center;
  gap: 8px;
}

.table-title::before {
  content: '';
  width: 4px;
  height: 16px;
  background: var(--gradient);
  border-radius: 2px;
  display: inline-block;
}

.filters {
  display: flex;
  gap: 8px;
}

.filter-select {
  width: auto;
  min-width: 130px;
  font-size: 13px;
  padding: 7px 12px;
}

.id-cell {
  font-size: 12px;
  color: var(--text-light);
  font-weight: 600;
}

.title-cell {
  max-width: 220px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-weight: 600;
  color: var(--text);
}

.category-tag {
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  color: var(--primary);
  background: rgba(108, 99, 255, 0.1);
  padding: 4px 10px;
  border-radius: 20px;
}

.priority-badge {
  font-size: 11px;
  font-weight: 700;
  padding: 4px 10px;
  border-radius: 20px;
  text-transform: uppercase;
  letter-spacing: 0.03em;
}

.priority-low      { background: #ECFDF5; color: #065F46; }
.priority-medium   { background: #FFFBEB; color: #92400E; }
.priority-high     { background: #FFF7ED; color: #C2410C; }
.priority-critical { background: #FEF2F2; color: #991B1B; }

.empty-table {
  text-align: center;
  padding: 60px 40px;
  color: var(--text-light);
  font-size: 15px;
}

@media (max-width: 900px) {
  .charts-row { grid-template-columns: 1fr; }
  .dashboard-hero { flex-direction: column; gap: 16px; text-align: center; }
}
</style>
