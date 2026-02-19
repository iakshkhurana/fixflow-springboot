<template>
  <div>
    <div class="page-header">
      <div>
        <h1 class="page-title">Analytics</h1>
        <p class="page-subtitle">Insights and trends across all maintenance requests</p>
      </div>
      <button class="btn-export-a" @click="exportCSV">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round">
          <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
          <polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/>
        </svg>
        Export CSV
      </button>
    </div>

    <div v-if="loading" class="loading">Loading analytics...</div>
    <div v-else>

      <!-- KPI Row -->
      <div class="kpi-row">
        <div class="kpi-card">
          <div class="kpi-icon kpi-green">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round"><path d="M22 12h-4l-3 9L9 3l-3 9H2"/></svg>
          </div>
          <div>
            <div class="kpi-value">{{ totalRequests }}</div>
            <div class="kpi-label">Total Requests</div>
          </div>
        </div>
        <div class="kpi-card">
          <div class="kpi-icon kpi-emerald">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round"><polyline points="20 6 9 17 4 12"/></svg>
          </div>
          <div>
            <div class="kpi-value">{{ resolvedTotal }}</div>
            <div class="kpi-label">Resolved</div>
          </div>
        </div>
        <div class="kpi-card">
          <div class="kpi-icon kpi-amber">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
          </div>
          <div>
            <div class="kpi-value">{{ stats?.totalInProgress ?? 0 }}</div>
            <div class="kpi-label">In Progress</div>
          </div>
        </div>
        <div class="kpi-card">
          <div class="kpi-icon kpi-red">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/></svg>
          </div>
          <div>
            <div class="kpi-value">{{ criticalCount }}</div>
            <div class="kpi-label">Critical Open</div>
          </div>
        </div>
        <div class="kpi-card">
          <div class="kpi-icon kpi-blue">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>
          </div>
          <div>
            <div class="kpi-value">{{ resolutionRate }}%</div>
            <div class="kpi-label">Resolution Rate</div>
          </div>
        </div>
      </div>

      <!-- Charts Row 1 -->
      <div class="chart-grid-2">
        <div class="card">
          <h3 class="chart-title">
            <span class="title-bar"></span>Requests by Category
          </h3>
          <Bar v-if="categoryData" :data="categoryData" :options="barOpts" style="max-height:220px" />
          <div v-else class="no-data">No category data</div>
        </div>
        <div class="card">
          <h3 class="chart-title">
            <span class="title-bar"></span>Requests by Priority
          </h3>
          <Bar v-if="priorityData" :data="priorityData" :options="barOpts" style="max-height:220px" />
          <div v-else class="no-data">No priority data</div>
        </div>
      </div>

      <!-- Charts Row 2 -->
      <div class="chart-grid-2">
        <!-- Status Distribution Doughnut -->
        <div class="card status-chart-card">
          <h3 class="chart-title">
            <span class="title-bar"></span>Status Distribution
          </h3>
          <div class="donut-wrap">
            <Doughnut :data="statusData" :options="donutOpts" style="max-height:200px" />
          </div>
          <div class="status-legend">
            <div v-for="(item, i) in statusLegend" :key="i" class="legend-row-h">
              <i class="legend-sq" :style="`background:${item.color}`"></i>
              <span class="leg-label">{{ item.label }}</span>
              <span class="leg-val">{{ item.value }}</span>
            </div>
          </div>
        </div>

        <!-- Top Assigned Staff -->
        <div class="card">
          <h3 class="chart-title">
            <span class="title-bar"></span>Staff Workload
          </h3>
          <div class="staff-list">
            <div v-for="(s, i) in staffWorkload" :key="i" class="staff-row">
              <div class="staff-av">{{ s.initials }}</div>
              <div class="staff-info">
                <div class="staff-name">{{ s.name }}</div>
                <div class="staff-bar-wrap">
                  <div class="staff-bar" :style="`width:${s.pct}%`"></div>
                </div>
              </div>
              <span class="staff-count">{{ s.count }}</span>
            </div>
            <div v-if="staffWorkload.length === 0" class="no-data">No assignments yet</div>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Bar, Doughnut } from 'vue-chartjs'
import {
  Chart as ChartJS, CategoryScale, LinearScale, BarElement,
  ArcElement, Title, Tooltip, Legend
} from 'chart.js'
import { useRequestStore } from '../stores/requestStore'
import api from '../services/api'

ChartJS.register(CategoryScale, LinearScale, BarElement, ArcElement, Title, Tooltip, Legend)

const requestStore = useRequestStore()
const stats  = ref(null)
const loading = ref(true)

onMounted(async () => {
  try {
    const [{ data: s }] = await Promise.all([
      api.get('/dashboard/stats'),
      requestStore.fetchAllRequests()
    ])
    stats.value = s
  } catch (e) { console.error(e) }
  finally { loading.value = false }
})

const requests = computed(() => requestStore.requests)

const totalRequests = computed(() => {
  if (!stats.value) return 0
  return (stats.value.totalOpen ?? 0) + (stats.value.totalAssigned ?? 0) +
    (stats.value.totalInProgress ?? 0) + (stats.value.totalResolved ?? 0) + (stats.value.totalClosed ?? 0)
})

const resolvedTotal = computed(() => (stats.value?.totalResolved ?? 0) + (stats.value?.totalClosed ?? 0))

const criticalCount = computed(() =>
  requests.value.filter(r => r.priority === 'CRITICAL' && r.status !== 'RESOLVED' && r.status !== 'CLOSED').length
)

const resolutionRate = computed(() => {
  const t = totalRequests.value
  return t === 0 ? 0 : Math.round((resolvedTotal.value / t) * 100)
})

// Category chart
const categoryData = computed(() => {
  if (!stats.value?.categoryBreakdown?.length) return null
  const colors = ['#166534','#22C55E','#4ADE80','#86EFAC','#F59E0B','#EF4444']
  return {
    labels: stats.value.categoryBreakdown.map(c => c.category),
    datasets: [{
      data: stats.value.categoryBreakdown.map(c => c.count),
      backgroundColor: colors,
      borderRadius: 8, borderSkipped: false
    }]
  }
})

// Priority chart
const priorityData = computed(() => {
  if (!stats.value?.priorityBreakdown?.length) return null
  const colorMap = { LOW: '#22C55E', MEDIUM: '#F59E0B', HIGH: '#F97316', CRITICAL: '#EF4444' }
  return {
    labels: stats.value.priorityBreakdown.map(p => p.priority),
    datasets: [{
      data: stats.value.priorityBreakdown.map(p => p.count),
      backgroundColor: stats.value.priorityBreakdown.map(p => colorMap[p.priority] || '#9CA3AF'),
      borderRadius: 8, borderSkipped: false
    }]
  }
})

// Status donut
const statusData = computed(() => ({
  labels: ['Open','Assigned','In Progress','Resolved','Closed'],
  datasets: [{
    data: [
      stats.value?.totalOpen ?? 0,
      stats.value?.totalAssigned ?? 0,
      stats.value?.totalInProgress ?? 0,
      stats.value?.totalResolved ?? 0,
      stats.value?.totalClosed ?? 0
    ],
    backgroundColor: ['#374151','#F59E0B','#F97316','#166534','#9CA3AF'],
    borderWidth: 0, hoverOffset: 4
  }]
}))

const statusLegend = computed(() => [
  { label: 'Open',        value: stats.value?.totalOpen ?? 0,       color: '#374151' },
  { label: 'Assigned',    value: stats.value?.totalAssigned ?? 0,   color: '#F59E0B' },
  { label: 'In Progress', value: stats.value?.totalInProgress ?? 0, color: '#F97316' },
  { label: 'Resolved',    value: stats.value?.totalResolved ?? 0,   color: '#166534' },
  { label: 'Closed',      value: stats.value?.totalClosed ?? 0,     color: '#9CA3AF' },
])

// Staff workload
const staffWorkload = computed(() => {
  const map = {}
  requests.value.forEach(r => {
    if (!r.assignedToName) return
    if (!map[r.assignedToName]) map[r.assignedToName] = { name: r.assignedToName, count: 0 }
    map[r.assignedToName].count++
  })
  const arr = Object.values(map).sort((a, b) => b.count - a.count).slice(0, 6)
  const max = arr[0]?.count || 1
  return arr.map(s => ({
    ...s,
    pct: Math.round((s.count / max) * 100),
    initials: s.name.split(' ').map(n => n[0]).join('').slice(0,2).toUpperCase()
  }))
})

const barOpts = {
  responsive: true,
  plugins: { legend: { display: false } },
  scales: { y: { beginAtZero: true, ticks: { stepSize: 1 }, grid: { color: '#F3F4F6' } }, x: { grid: { display: false } } }
}

const donutOpts = {
  responsive: true,
  cutout: '65%',
  plugins: { legend: { display: false } }
}

function exportCSV() {
  const rows = requests.value.map(r => [r.id, r.title, r.category, r.priority, r.status, r.buildingName || '', r.assignedToName || 'Unassigned'])
  const csv = [['ID','Title','Category','Priority','Status','Building','Assigned To'], ...rows]
    .map(r => r.map(c => `"${String(c ?? '').replace(/"/g,'""')}"`).join(',')).join('\n')
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `fixflow-analytics-${new Date().toISOString().split('T')[0]}.csv`
  link.click()
}
</script>

<style scoped>
.kpi-row {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 14px;
  margin-bottom: 20px;
}

.kpi-card {
  background: var(--white);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  padding: 18px;
  display: flex;
  align-items: center;
  gap: 14px;
  transition: transform 0.2s;
}

.kpi-card:hover { transform: translateY(-2px); box-shadow: var(--shadow-md); }

.kpi-icon {
  width: 42px; height: 42px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}

.kpi-green   { background: linear-gradient(135deg, #166534, #14532D); }
.kpi-emerald { background: linear-gradient(135deg, #059669, #047857); }
.kpi-amber   { background: linear-gradient(135deg, #D97706, #B45309); }
.kpi-red     { background: linear-gradient(135deg, #DC2626, #B91C1C); }
.kpi-blue    { background: linear-gradient(135deg, #2563EB, #1D4ED8); }

.kpi-value { font-size: 26px; font-weight: 800; color: #111827; letter-spacing: -0.02em; }
.kpi-label { font-size: 12px; color: #6B7280; margin-top: 2px; }

.chart-grid-2 {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 16px;
}

.chart-title {
  font-size: 15px; font-weight: 700; color: #111827;
  margin-bottom: 16px;
  display: flex; align-items: center; gap: 8px;
}

.title-bar {
  display: inline-block; width: 4px; height: 16px;
  background: var(--gradient); border-radius: 2px; flex-shrink: 0;
}

.no-data {
  text-align: center; padding: 40px; color: #9CA3AF; font-size: 13px;
}

/* Status chart */
.status-chart-card { display: flex; flex-direction: column; gap: 12px; }
.donut-wrap { display: flex; justify-content: center; }

.status-legend { display: flex; flex-direction: column; gap: 8px; }

.legend-row-h {
  display: flex; align-items: center; gap: 8px; font-size: 13px;
}

.legend-sq { width: 10px; height: 10px; border-radius: 3px; flex-shrink: 0; }
.leg-label { flex: 1; color: #374151; }
.leg-val { font-weight: 700; color: #111827; }

/* Staff workload */
.staff-list { display: flex; flex-direction: column; gap: 12px; }

.staff-row { display: flex; align-items: center; gap: 10px; }

.staff-av {
  width: 34px; height: 34px; border-radius: 50%;
  background: linear-gradient(135deg, #166534, #22C55E);
  color: white; font-size: 11px; font-weight: 800;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}

.staff-info { flex: 1; min-width: 0; }
.staff-name { font-size: 13px; font-weight: 600; color: #111827; margin-bottom: 4px; }

.staff-bar-wrap {
  height: 6px; background: #F3F4F6; border-radius: 3px; overflow: hidden;
}

.staff-bar { height: 100%; background: linear-gradient(90deg, #166534, #22C55E); border-radius: 3px; transition: width 0.4s; }

.staff-count { font-size: 13px; font-weight: 700; color: #166534; }

.btn-export-a {
  display: flex; align-items: center; gap: 7px;
  padding: 10px 18px; border: 1.5px solid #D1D5DB;
  border-radius: 10px; background: white; color: #374151;
  font-size: 14px; font-weight: 600; cursor: pointer;
  transition: all 0.18s; font-family: inherit;
}

.btn-export-a:hover { border-color: var(--primary); color: var(--primary); }

@media (max-width: 1200px) { .kpi-row { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 900px)  { .kpi-row { grid-template-columns: repeat(2, 1fr); } .chart-grid-2 { grid-template-columns: 1fr; } }
@media (max-width: 480px)  { .kpi-row { grid-template-columns: 1fr 1fr; } }
</style>
