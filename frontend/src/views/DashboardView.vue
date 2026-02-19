<template>
  <div class="dashboard-root">

    <!-- ===== TOP BAR ===== -->
    <div class="topbar">
      <div class="search-wrap" @click.stop>
        <div class="search-bar" :class="{ 'search-active': searchQuery.length > 0 }">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#9CA3AF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
          </svg>
          <input
            class="search-input"
            placeholder="Search requests..."
            v-model="searchQuery"
            @keydown.escape="searchQuery = ''"
          />
          <button v-if="searchQuery" class="search-clear" @click="searchQuery = ''">
            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round">
              <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
          <span v-else class="search-kbd">⌘K</span>
        </div>
        <!-- Search Results Dropdown -->
        <div v-if="searchQuery.length > 0" class="search-dropdown">
          <template v-if="searchResults.length > 0">
            <div
              v-for="r in searchResults"
              :key="r.id"
              class="search-result-item"
              @mousedown.prevent="goToRequest(r.id)"
            >
              <span class="sr-dot" :class="`sr-dot-${(r.status||'').toLowerCase().replace('_','-')}`"></span>
              <div class="sr-info">
                <div class="sr-title">{{ r.title }}</div>
                <div class="sr-meta">{{ r.category }} · {{ r.priority }}</div>
              </div>
              <span class="sr-id">#{{ r.id }}</span>
            </div>
          </template>
          <div v-else class="search-empty">No results for "{{ searchQuery }}"</div>
        </div>
      </div>
      <div class="topbar-right">
        <!-- Mail: goes to requests page -->
        <button
          class="icon-btn"
          title="Go to requests"
          @click="router.push(authStore.isTenant ? '/my-requests' : '/dashboard')"
        >
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#6B7280" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
            <polyline points="22,6 12,13 2,6"/>
          </svg>
        </button>

        <!-- Bell: notifications dropdown -->
        <div class="icon-btn-wrap" @click.stop>
          <button
            class="icon-btn"
            :class="{ 'icon-btn-active': notifOpen }"
            title="Notifications"
            @click="notifOpen = !notifOpen; profileOpen = false"
          >
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#6B7280" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
              <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
            </svg>
            <span v-if="notifCount > 0" class="notif-dot"></span>
          </button>

          <Transition name="dropdown">
            <div v-if="notifOpen" class="notif-dropdown">
              <div class="notif-hdr">
                <span class="notif-hdr-title">Notifications</span>
                <span v-if="notifCount > 0" class="notif-badge">{{ notifCount }}</span>
              </div>
              <div class="notif-list">
                <div
                  v-for="r in recentNotifications"
                  :key="r.id"
                  class="notif-item"
                  @click="goToRequest(r.id); notifOpen = false"
                >
                  <span class="sr-dot" :class="`sr-dot-${r.status?.toLowerCase().replace('_','-')}`"></span>
                  <div class="notif-info">
                    <div class="notif-title">{{ r.title }}</div>
                    <div class="notif-meta">{{ formatStatus(r.status) }} · #{{ r.id }}</div>
                  </div>
                  <span class="notif-date">{{ formatDate(r.createdAt) }}</span>
                </div>
                <div v-if="notifCount === 0" class="notif-empty">
                  <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#D1D5DB" stroke-width="1.5" stroke-linecap="round"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/></svg>
                  All caught up!
                </div>
              </div>
              <div v-if="notifCount > 0" class="notif-footer" @click="router.push('/dashboard'); notifOpen = false">
                View all requests →
              </div>
            </div>
          </Transition>
        </div>
        <div class="user-profile-wrap" @click.stop>
          <div class="user-profile" :class="{ open: profileOpen }" @click="profileOpen = !profileOpen">
            <div class="user-avatar-circle">{{ initials }}</div>
            <div class="user-profile-info">
              <div class="user-profile-name">{{ authStore.user?.fullName }}</div>
              <div class="user-profile-email">{{ authStore.user?.role }}</div>
            </div>
            <svg class="profile-chevron" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <polyline points="6 9 12 15 18 9"/>
            </svg>
          </div>
          <!-- Profile Dropdown -->
          <Transition name="dropdown">
            <div v-if="profileOpen" class="profile-dropdown">
              <div class="pd-header">
                <div class="pd-big-av">{{ initials }}</div>
                <div class="pd-header-info">
                  <div class="pd-name">{{ authStore.user?.fullName }}</div>
                  <div class="pd-email">{{ authStore.user?.email }}</div>
                  <span class="pd-role">{{ authStore.user?.role }}</span>
                </div>
              </div>
              <div class="pd-sep"></div>
              <button class="pd-item" @click="navigateTo('/settings')">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><circle cx="12" cy="12" r="3"/><path d="M19.07 4.93a10 10 0 0 1 0 14.14M4.93 4.93a10 10 0 0 0 0 14.14"/></svg>
                Settings
              </button>
              <button v-if="authStore.isStaffOrAdmin" class="pd-item" @click="navigateTo('/team')">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                Team
              </button>
              <div class="pd-sep"></div>
              <button class="pd-item pd-item-danger" @click="handleSignOut">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
                Sign Out
              </button>
            </div>
          </Transition>
        </div>
      </div>
    </div>

    <!-- ===== PAGE CONTENT ===== -->
    <div class="page-content">

      <!-- Page Header -->
      <div class="dash-header">
        <div>
          <h1 class="dash-title">Dashboard</h1>
          <p class="dash-subtitle">Plan, prioritize, and resolve your building maintenance requests.</p>
        </div>
        <div class="dash-actions">
          <button class="btn-add-request" @click="router.push('/requests/new')" v-if="authStore.isTenant">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
              <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
            </svg>
            Add Request
          </button>
          <button class="btn-export" @click="exportCSV">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
              <polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/>
            </svg>
            Export CSV
          </button>
        </div>
      </div>

      <!-- ===== STATS ROW ===== -->
      <div class="stats-row" v-if="stats">
        <!-- Total (dark green) -->
        <div class="scard scard-dark">
          <div class="scard-inner">
            <div class="scard-label">Total Requests</div>
            <div class="scard-value">{{ totalRequests }}</div>
            <div class="scard-trend">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="18 15 12 9 6 15"/></svg>
              Increased from last month
            </div>
          </div>
          <button class="scard-arrow scard-arrow-white">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="7" y1="17" x2="17" y2="7"/><polyline points="7 7 17 7 17 17"/></svg>
          </button>
        </div>

        <!-- Resolved -->
        <div class="scard scard-light">
          <div class="scard-inner">
            <div class="scard-label">Resolved</div>
            <div class="scard-value dark">{{ stats.totalResolved ?? 0 }}</div>
            <div class="scard-trend dark">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="#16A34A" stroke-width="2.5"><polyline points="18 15 12 9 6 15"/></svg>
              Increased from last month
            </div>
          </div>
          <button class="scard-arrow">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#374151" stroke-width="2.5"><line x1="7" y1="17" x2="17" y2="7"/><polyline points="7 7 17 7 17 17"/></svg>
          </button>
        </div>

        <!-- In Progress -->
        <div class="scard scard-light">
          <div class="scard-inner">
            <div class="scard-label">In Progress</div>
            <div class="scard-value dark">{{ stats.totalInProgress ?? 0 }}</div>
            <div class="scard-trend dark">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="#16A34A" stroke-width="2.5"><polyline points="18 15 12 9 6 15"/></svg>
              Increased from last month
            </div>
          </div>
          <button class="scard-arrow">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#374151" stroke-width="2.5"><line x1="7" y1="17" x2="17" y2="7"/><polyline points="7 7 17 7 17 17"/></svg>
          </button>
        </div>

        <!-- Open -->
        <div class="scard scard-light">
          <div class="scard-inner">
            <div class="scard-label">Pending</div>
            <div class="scard-value dark">{{ stats.totalOpen ?? 0 }}</div>
            <div class="scard-trend gray">Under Review</div>
          </div>
          <button class="scard-arrow">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#374151" stroke-width="2.5"><line x1="7" y1="17" x2="17" y2="7"/><polyline points="7 7 17 7 17 17"/></svg>
          </button>
        </div>
      </div>

      <!-- Skeleton stats if loading -->
      <div class="stats-row" v-else>
        <div class="scard scard-dark scard-skeleton"></div>
        <div class="scard scard-light scard-skeleton"></div>
        <div class="scard scard-light scard-skeleton"></div>
        <div class="scard scard-light scard-skeleton"></div>
      </div>

      <!-- ===== MIDDLE ROW ===== -->
      <div class="mid-row">

        <!-- Request Analytics -->
        <div class="card analytics-card">
          <h3 class="section-title">Request Analytics</h3>
          <Bar :data="weeklyChartData" :options="weeklyChartOptions" style="max-height: 180px;" />
          <div class="analytics-days">
            <span v-for="d in ['S','M','T','W','T','F','S']" :key="d">{{ d }}</span>
          </div>
        </div>

        <!-- Reminders -->
        <div class="card reminder-card">
          <h3 class="section-title">Reminders</h3>
          <div class="reminder-content">
            <div class="reminder-tag">
              <span class="reminder-dot-live"></span>
              Upcoming
            </div>
            <div class="reminder-title">Building Safety<br>Inspection</div>
            <div class="reminder-time">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
              10:00 am – 12:00 pm
            </div>
            <button class="btn-view-cal" @click="router.push('/calendar')">
              View Calendar
              <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/></svg>
            </button>
          </div>
        </div>

        <!-- Recent Requests Panel -->
        <div class="card requests-panel">
          <div class="panel-head">
            <h3 class="section-title" style="margin:0">Requests</h3>
            <button class="btn-new-tag" @click="router.push('/my-requests')">+ New</button>
          </div>
          <div class="req-list">
            <div
              v-for="req in recentRequests"
              :key="req.id"
              class="req-list-item"
              @click="router.push(`/requests/${req.id}`)"
            >
              <div class="req-cat-icon" :class="`cat-${(req.category || 'general').toLowerCase()}`">
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z"/>
                </svg>
              </div>
              <div class="req-list-info">
                <div class="req-list-title">{{ req.title }}</div>
                <div class="req-list-due">Due date: {{ formatDate(req.createdAt) }}</div>
              </div>
            </div>
            <div v-if="recentRequests.length === 0" class="req-empty">No requests yet</div>
          </div>
        </div>

      </div><!-- end mid-row -->

      <!-- ===== BOTTOM ROW ===== -->
      <div class="bot-row">

        <!-- Team / Staff Assignments -->
        <div class="card team-card">
          <div class="panel-head">
            <h3 class="section-title" style="margin:0">Staff on Duty</h3>
            <button class="btn-new-tag" @click="router.push('/team')">View All</button>
          </div>
          <div class="team-list">
            <div v-for="member in staffOnDuty" :key="member.name" class="team-item">
              <div class="team-avatar">{{ member.initials }}</div>
              <div class="team-info">
                <div class="team-name">{{ member.name }}</div>
                <div class="team-task">Working on <em>{{ member.task }}</em></div>
              </div>
              <span class="team-badge" :class="`badge-${member.status.toLowerCase().replace('_','-')}`">
                {{ formatStatus(member.status) }}
              </span>
            </div>
            <div v-if="staffOnDuty.length === 0" class="team-empty">No staff assigned yet</div>
          </div>
        </div>

        <!-- Progress Gauge -->
        <div class="card progress-card">
          <h3 class="section-title">Request Progress</h3>
          <div class="gauge-wrap">
            <Doughnut :data="progressChartData" :options="progressOptions" style="max-height: 160px;" />
            <div class="gauge-center">
              <div class="gauge-pct">{{ resolvedPercent }}%</div>
              <div class="gauge-label">Resolved</div>
            </div>
          </div>
          <div class="progress-legend">
            <span class="legend-item"><i class="legend-dot dot-resolved"></i>Resolved</span>
            <span class="legend-item"><i class="legend-dot dot-progress"></i>In Progress</span>
            <span class="legend-item"><i class="legend-dot dot-open"></i>Open</span>
          </div>
        </div>

      </div><!-- end bot-row -->

    </div><!-- end page-content -->
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { Bar, Doughnut } from 'vue-chartjs'
import {
  Chart as ChartJS,
  CategoryScale, LinearScale, BarElement,
  ArcElement, Title, Tooltip, Legend
} from 'chart.js'
// StatusBadge imported but used in bot-row only
import { useAuthStore } from '../stores/authStore'
import { useRequestStore } from '../stores/requestStore'
import api from '../services/api'

ChartJS.register(CategoryScale, LinearScale, BarElement, ArcElement, Title, Tooltip, Legend)

const router = useRouter()
const authStore = useAuthStore()
const requestStore = useRequestStore()

const stats        = ref(null)
const searchQuery  = ref('')
const profileOpen  = ref(false)
const notifOpen    = ref(false)

// ===== Computed =====
const initials = computed(() => {
  const name = authStore.user?.fullName || ''
  return name.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase()
})

const totalRequests = computed(() => {
  if (!stats.value) return 0
  return (stats.value.totalOpen ?? 0)
    + (stats.value.totalAssigned ?? 0)
    + (stats.value.totalInProgress ?? 0)
    + (stats.value.totalResolved ?? 0)
    + (stats.value.totalClosed ?? 0)
})

const recentRequests = computed(() => requestStore.requests.slice(0, 5))

const recentNotifications = computed(() =>
  [...requestStore.requests]
    .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
    .filter(r => ['OPEN', 'ASSIGNED', 'IN_PROGRESS'].includes(r.status))
    .slice(0, 6)
)

const notifCount = computed(() => recentNotifications.value.length)

const searchResults = computed(() => {
  const q = searchQuery.value.trim().toLowerCase()
  if (!q) return []
  return requestStore.requests.filter(r =>
    r.title?.toLowerCase().includes(q) ||
    r.category?.toLowerCase().includes(q) ||
    r.priority?.toLowerCase().includes(q) ||
    r.status?.toLowerCase().replace('_', ' ').includes(q) ||
    String(r.id).includes(q)
  ).slice(0, 6)
})

const staffOnDuty = computed(() => {
  const seen = new Set()
  return requestStore.requests
    .filter(r => r.assignedToName && !seen.has(r.assignedToName) && seen.add(r.assignedToName))
    .slice(0, 4)
    .map(r => ({
      name: r.assignedToName,
      task: r.title,
      status: r.status,
      initials: r.assignedToName.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase()
    }))
})

// ===== Weekly Chart =====
const weeklyChartData = computed(() => ({
  labels: ['', '', '', '', '', '', ''],
  datasets: [{
    data: [4, 8, 11, 7, 10, 6, 3],
    backgroundColor: [
      'rgba(22,101,52,0.12)',
      'rgba(22,101,52,0.2)',
      '#166534',
      '#22C55E',
      'rgba(22,101,52,0.2)',
      'rgba(22,101,52,0.12)',
      'rgba(22,101,52,0.08)',
    ],
    borderRadius: 30,
    borderSkipped: false,
    barThickness: 22,
  }]
}))

const weeklyChartOptions = {
  responsive: true,
  plugins: {
    legend: { display: false },
    tooltip: {
      callbacks: {
        label: ctx => `${Math.round(ctx.raw / 11 * 100)}% of peak`
      }
    }
  },
  scales: {
    x: { display: false, grid: { display: false } },
    y: { display: false, beginAtZero: true, grid: { display: false } }
  }
}

// ===== Progress Doughnut =====
const resolvedPercent = computed(() => {
  if (!stats.value) return 0
  const total = totalRequests.value
  if (total === 0) return 0
  return Math.round(((stats.value.totalResolved ?? 0) + (stats.value.totalClosed ?? 0)) / total * 100)
})

const progressChartData = computed(() => ({
  datasets: [{
    data: [
      (stats.value?.totalResolved ?? 0) + (stats.value?.totalClosed ?? 1),
      (stats.value?.totalInProgress ?? 0) + (stats.value?.totalAssigned ?? 0),
      stats.value?.totalOpen ?? 1,
    ],
    backgroundColor: ['#166534', '#4ADE80', 'rgba(22,101,52,0.12)'],
    borderWidth: 0,
    hoverOffset: 4,
  }]
}))

const progressOptions = {
  responsive: true,
  cutout: '72%',
  rotation: -90,
  circumference: 180,
  plugins: { legend: { display: false }, tooltip: { enabled: false } }
}

// ===== Search & Profile =====
function goToRequest(id) {
  searchQuery.value = ''
  router.push(`/requests/${id}`)
}

function navigateTo(path) {
  profileOpen.value = false
  router.push(path)
}

function handleSignOut() {
  authStore.logout()
  router.push('/login')
}

function handleDocClick() {
  profileOpen.value = false
  notifOpen.value   = false
}

// ===== CSV Export =====
function exportCSV() {
  const headers = ['ID', 'Title', 'Category', 'Priority', 'Status', 'Building', 'Assigned To', 'Created']
  const rows = requestStore.requests.map(r => [
    r.id, r.title, r.category, r.priority, r.status,
    r.buildingName || '', r.assignedToName || 'Unassigned', formatDate(r.createdAt)
  ])
  const csvContent = [headers, ...rows]
    .map(row => row.map(cell => `"${String(cell ?? '').replace(/"/g, '""')}"`).join(','))
    .join('\n')
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const url  = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href     = url
  link.download = `fixflow-requests-${new Date().toISOString().split('T')[0]}.csv`
  link.click()
  URL.revokeObjectURL(url)
}

// ===== Data Loading =====
onMounted(async () => {
  document.addEventListener('click', handleDocClick)
  await Promise.all([loadStats(), requestStore.fetchAllRequests()])
})

onUnmounted(() => {
  document.removeEventListener('click', handleDocClick)
})

async function loadStats() {
  try {
    const { data } = await api.get('/dashboard/stats')
    stats.value = data
  } catch (e) {
    console.error('Failed to load stats', e)
  }
}

function formatDate(dateStr) {
  if (!dateStr) return '—'
  return new Date(dateStr).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
}

function formatStatus(status) {
  const map = {
    OPEN: 'Open', ASSIGNED: 'Assigned', IN_PROGRESS: 'In Progress',
    RESOLVED: 'Completed', CLOSED: 'Closed'
  }
  return map[status] || status
}
</script>

<style scoped>
/* ===== Root ===== */
.dashboard-root {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

/* ===== TOP BAR ===== */
.topbar {
  margin: calc(-1 * var(--content-py)) calc(-1 * var(--content-px)) 0;
  padding: 0 var(--content-px);
  height: 64px;
  background: #FFFFFF;
  border-bottom: 1px solid #E5E7EB;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  position: sticky;
  top: 0;
  z-index: 50;
  box-shadow: 0 1px 0 #E5E7EB;
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  background: #F9FAFB;
  border: 1px solid #E5E7EB;
  border-radius: 10px;
  padding: 8px 14px;
  min-width: 260px;
}

.search-input {
  border: none;
  background: transparent;
  outline: none;
  font-size: 14px;
  color: #6B7280;
  flex: 1;
  font-family: inherit;
}

.search-kbd {
  font-size: 11px;
  color: #9CA3AF;
  background: #E5E7EB;
  padding: 2px 6px;
  border-radius: 5px;
  font-weight: 600;
  white-space: nowrap;
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.icon-btn {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  border: 1px solid #E5E7EB;
  background: #FFFFFF;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.18s;
  position: relative;
}

.icon-btn:hover { background: #F9FAFB; }

.notif-dot {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #EF4444;
  border: 1.5px solid white;
}

/* Search wrap & dropdown */
.search-wrap { position: relative; }
.search-bar.search-active { border-color: #166534; }

.search-clear {
  background: none; border: none; cursor: pointer;
  color: #9CA3AF; display: flex; align-items: center; padding: 0;
  transition: color 0.15s;
}
.search-clear:hover { color: #374151; }

.search-dropdown {
  position: absolute;
  top: calc(100% + 6px);
  left: 0; right: 0;
  background: white;
  border: 1px solid #E5E7EB;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
  z-index: 200;
  overflow: hidden;
}

.search-result-item {
  display: flex; align-items: center; gap: 10px;
  padding: 10px 14px;
  cursor: pointer;
  transition: background 0.15s;
  border-bottom: 1px solid #F3F4F6;
}
.search-result-item:last-child { border-bottom: none; }
.search-result-item:hover { background: #F9FAFB; }

.sr-dot {
  width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0;
}
.sr-dot-open        { background: #9CA3AF; }
.sr-dot-assigned    { background: #F59E0B; }
.sr-dot-in-progress { background: #F97316; }
.sr-dot-resolved    { background: #22C55E; }
.sr-dot-closed      { background: #166534; }

.sr-info { flex: 1; min-width: 0; }
.sr-title { font-size: 13px; font-weight: 600; color: #111827; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.sr-meta  { font-size: 11px; color: #9CA3AF; margin-top: 1px; text-transform: capitalize; }
.sr-id    { font-size: 11px; color: #9CA3AF; font-weight: 600; flex-shrink: 0; }

.search-empty {
  padding: 16px 14px;
  font-size: 13px; color: #9CA3AF; text-align: center;
}

/* User profile + dropdown */
.user-profile-wrap { position: relative; }

.user-profile {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 10px;
  border-radius: 10px;
  border: 1px solid #E5E7EB;
  background: #FFFFFF;
  cursor: pointer;
  transition: all 0.18s;
  user-select: none;
}
.user-profile:hover, .user-profile.open { background: #F9FAFB; border-color: #D1D5DB; }

.profile-chevron {
  color: #9CA3AF; flex-shrink: 0;
  transition: transform 0.2s;
}
.user-profile.open .profile-chevron { transform: rotate(180deg); }

/* Profile dropdown */
.profile-dropdown {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  width: 240px;
  background: white;
  border: 1px solid #E5E7EB;
  border-radius: 14px;
  box-shadow: 0 12px 36px rgba(0,0,0,0.14);
  z-index: 300;
  overflow: hidden;
}

.pd-header {
  display: flex; align-items: center; gap: 12px;
  padding: 14px 16px;
}
.pd-big-av {
  width: 40px; height: 40px; border-radius: 12px;
  background: linear-gradient(135deg, #166534, #22C55E);
  color: white; font-size: 14px; font-weight: 800;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.pd-header-info { min-width: 0; }
.pd-name  { font-size: 13px; font-weight: 700; color: #111827; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.pd-email { font-size: 11px; color: #9CA3AF; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; margin-top: 1px; }
.pd-role  { font-size: 10px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.06em; background: #F0FDF4; color: #166534; padding: 1px 7px; border-radius: 20px; display: inline-block; margin-top: 4px; }

.pd-sep { height: 1px; background: #F3F4F6; }

.pd-item {
  display: flex; align-items: center; gap: 10px;
  width: 100%; padding: 11px 16px;
  background: none; border: none; cursor: pointer;
  font-size: 13px; font-weight: 500; color: #374151;
  text-align: left; transition: background 0.15s; font-family: inherit;
}
.pd-item:hover { background: #F9FAFB; }
.pd-item-danger { color: #DC2626; }
.pd-item-danger:hover { background: #FEF2F2; }

/* Transition */
.dropdown-enter-active, .dropdown-leave-active { transition: opacity 0.15s, transform 0.15s; }
.dropdown-enter-from, .dropdown-leave-to { opacity: 0; transform: translateY(-6px); }

/* Notification dropdown */
.icon-btn-wrap { position: relative; }

.icon-btn-active { background: #F0FDF4; border-color: #BBF7D0; }
.icon-btn-active svg { stroke: #166534; }

.notif-dropdown {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  width: 300px;
  background: white;
  border: 1px solid #E5E7EB;
  border-radius: 14px;
  box-shadow: 0 12px 36px rgba(0,0,0,0.13);
  z-index: 300;
  overflow: hidden;
}

.notif-hdr {
  display: flex; align-items: center; justify-content: space-between;
  padding: 13px 16px 11px;
  border-bottom: 1px solid #F3F4F6;
}
.notif-hdr-title { font-size: 13px; font-weight: 700; color: #111827; }
.notif-badge {
  background: #166534; color: white;
  font-size: 10px; font-weight: 800;
  padding: 2px 7px; border-radius: 20px;
}

.notif-list { max-height: 280px; overflow-y: auto; }

.notif-item {
  display: flex; align-items: flex-start; gap: 10px;
  padding: 11px 16px;
  border-bottom: 1px solid #F9FAFB;
  cursor: pointer; transition: background 0.15s;
}
.notif-item:last-child { border-bottom: none; }
.notif-item:hover { background: #F9FAFB; }
.notif-item .sr-dot { margin-top: 4px; flex-shrink: 0; }

.notif-info { flex: 1; min-width: 0; }
.notif-title { font-size: 13px; font-weight: 600; color: #111827; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.notif-meta  { font-size: 11px; color: #9CA3AF; margin-top: 1px; }
.notif-date  { font-size: 10px; color: #9CA3AF; flex-shrink: 0; white-space: nowrap; margin-top: 2px; }

.notif-empty {
  display: flex; flex-direction: column; align-items: center; gap: 8px;
  padding: 28px 16px;
  font-size: 13px; color: #9CA3AF;
}

.notif-footer {
  padding: 11px 16px;
  font-size: 12px; font-weight: 600; color: #166534;
  border-top: 1px solid #F3F4F6;
  cursor: pointer; text-align: center;
  transition: background 0.15s;
}
.notif-footer:hover { background: #F0FDF4; }

.user-avatar-circle {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: linear-gradient(135deg, #166534, #22C55E);
  color: white;
  font-size: 12px;
  font-weight: 800;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.user-profile-name {
  font-size: 13px;
  font-weight: 700;
  color: #111827;
  line-height: 1.2;
}

.user-profile-email {
  font-size: 11px;
  color: #9CA3AF;
}

/* ===== PAGE CONTENT ===== */
.page-content {
  padding-top: 24px;
}

/* ===== HEADER ===== */
.dash-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 24px;
}

.dash-title {
  font-size: 28px;
  font-weight: 800;
  color: #111827;
  letter-spacing: -0.03em;
  line-height: 1;
}

.dash-subtitle {
  font-size: 14px;
  color: #9CA3AF;
  margin-top: 6px;
}

.dash-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.btn-add-request {
  display: flex;
  align-items: center;
  gap: 7px;
  padding: 10px 20px;
  background: #166534;
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.18s;
  font-family: inherit;
}

.btn-add-request:hover { background: #14532D; transform: translateY(-1px); }

.btn-export {
  padding: 10px 20px;
  background: transparent;
  color: #374151;
  border: 1.5px solid #D1D5DB;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.18s;
  font-family: inherit;
}

.btn-export:hover { border-color: #166534; color: #166534; }

/* ===== STATS ROW ===== */
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
  margin-bottom: 20px;
}

.scard {
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  position: relative;
  min-height: 120px;
  transition: transform 0.2s;
}

.scard:hover { transform: translateY(-2px); }

.scard-dark {
  background: linear-gradient(145deg, #166534 0%, #0F3D22 100%);
  box-shadow: 0 8px 24px rgba(22, 101, 52, 0.3);
}

.scard-light {
  background: #FFFFFF;
  border: 1px solid #E5E7EB;
  box-shadow: 0 1px 8px rgba(0,0,0,0.05);
}

.scard-skeleton {
  animation: pulse 1.5s infinite;
  min-height: 120px;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.scard-inner { flex: 1; }

.scard-label {
  font-size: 13px;
  font-weight: 600;
  color: rgba(255,255,255,0.75);
  margin-bottom: 10px;
}

.scard-light .scard-label {
  color: #6B7280;
}

.scard-value {
  font-size: 36px;
  font-weight: 800;
  color: #FFFFFF;
  letter-spacing: -0.03em;
  line-height: 1;
  margin-bottom: 10px;
}

.scard-value.dark { color: #111827; }

.scard-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  font-weight: 600;
  color: rgba(255,255,255,0.65);
}

.scard-trend.dark {
  color: #16A34A;
}

.scard-trend.gray {
  color: #9CA3AF;
}

.scard-arrow {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: 1.5px solid rgba(255,255,255,0.3);
  background: transparent;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.18s;
  flex-shrink: 0;
}

.scard-arrow:hover { background: rgba(255,255,255,0.15); }

.scard-light .scard-arrow {
  border-color: #D1D5DB;
  color: #374151;
}

.scard-light .scard-arrow:hover { background: #F9FAFB; }

/* ===== MIDDLE ROW ===== */
.mid-row {
  display: grid;
  grid-template-columns: 1fr 0.55fr 0.55fr;
  gap: 14px;
  margin-bottom: 14px;
}

.section-title {
  font-size: 15px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 16px;
}

/* Analytics */
.analytics-card {
  padding: 20px 20px 8px;
}

.analytics-days {
  display: flex;
  justify-content: space-between;
  padding: 6px 8px 0;
}

.analytics-days span {
  font-size: 11px;
  font-weight: 600;
  color: #9CA3AF;
  width: 22px;
  text-align: center;
}

/* Reminders */
.reminder-card {
  display: flex;
  flex-direction: column;
}

.reminder-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.reminder-title {
  font-size: 18px;
  font-weight: 800;
  color: #111827;
  line-height: 1.3;
}

.reminder-time {
  font-size: 12px;
  color: #6B7280;
}

.reminder-tag {
  display: flex; align-items: center; gap: 6px;
  font-size: 11px; font-weight: 700; text-transform: uppercase;
  letter-spacing: 0.06em; color: #166534;
}
.reminder-dot-live {
  width: 7px; height: 7px; border-radius: 50%;
  background: #22C55E;
  animation: blink 1.8s ease-in-out infinite;
}
@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.35; }
}

.reminder-time {
  display: flex; align-items: center; gap: 5px;
}

.btn-view-cal {
  margin-top: auto;
  display: flex; align-items: center; justify-content: center; gap: 7px;
  padding: 11px 0;
  background: #F0FDF4; color: #166534;
  border: 1.5px solid #BBF7D0; border-radius: 12px;
  font-size: 13px; font-weight: 700; cursor: pointer;
  transition: all 0.18s; font-family: inherit;
}
.btn-view-cal:hover { background: #166534; color: white; border-color: #166534; }

/* Requests Panel */
.requests-panel {
  display: flex;
  flex-direction: column;
}

.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}

.btn-new-tag {
  padding: 5px 12px;
  border: 1.5px solid #D1D5DB;
  border-radius: 20px;
  background: transparent;
  color: #374151;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.18s;
  font-family: inherit;
}

.btn-new-tag:hover { border-color: #166534; color: #166534; }

.req-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex: 1;
}

.req-list-item {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 4px 0;
  transition: opacity 0.15s;
}

.req-list-item:hover { opacity: 0.75; }

.req-cat-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  color: white;
}

.cat-hvac     { background: linear-gradient(135deg, #3B82F6, #1D4ED8); }
.cat-electrical { background: linear-gradient(135deg, #F59E0B, #D97706); }
.cat-plumbing { background: linear-gradient(135deg, #06B6D4, #0891B2); }
.cat-general  { background: linear-gradient(135deg, #8B5CF6, #7C3AED); }
.cat-structural { background: linear-gradient(135deg, #EF4444, #DC2626); }

.req-list-info { flex: 1; min-width: 0; }

.req-list-title {
  font-size: 13px;
  font-weight: 600;
  color: #111827;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.req-list-due {
  font-size: 11px;
  color: #9CA3AF;
  margin-top: 1px;
}

.req-empty {
  font-size: 13px;
  color: #9CA3AF;
  text-align: center;
  padding: 24px 0;
}

/* ===== BOTTOM ROW ===== */
.bot-row {
  display: grid;
  grid-template-columns: 1.4fr 1fr;
  gap: 14px;
}

/* Team */
.team-card { display: flex; flex-direction: column; }

.team-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-top: 4px;
}

.team-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.team-avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: linear-gradient(135deg, #166534, #22C55E);
  color: white;
  font-size: 12px;
  font-weight: 800;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.team-info { flex: 1; min-width: 0; }

.team-name {
  font-size: 13px;
  font-weight: 700;
  color: #111827;
}

.team-task {
  font-size: 11px;
  color: #9CA3AF;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.team-task em { font-style: normal; color: #6B7280; }

.team-badge {
  font-size: 10px;
  font-weight: 700;
  padding: 3px 9px;
  border-radius: 20px;
  white-space: nowrap;
  flex-shrink: 0;
}

.badge-resolved, .badge-completed { background: #D1FAE5; color: #065F46; }
.badge-in-progress, .badge-assigned { background: #FEF3C7; color: #92400E; }
.badge-open { background: #F3F4F6; color: #374151; }
.badge-closed { background: #E5E7EB; color: #6B7280; }

.team-empty {
  font-size: 13px;
  color: #9CA3AF;
  text-align: center;
  padding: 20px 0;
}

/* Progress */
.progress-card { display: flex; flex-direction: column; }

.gauge-wrap {
  position: relative;
  display: flex;
  justify-content: center;
  margin-bottom: 8px;
}

.gauge-center {
  position: absolute;
  bottom: 8px;
  left: 50%;
  transform: translateX(-50%);
  text-align: center;
}

.gauge-pct {
  font-size: 22px;
  font-weight: 800;
  color: #111827;
  line-height: 1;
}

.gauge-label {
  font-size: 11px;
  color: #9CA3AF;
  margin-top: 2px;
}

.progress-legend {
  display: flex;
  justify-content: center;
  gap: 14px;
  flex-wrap: wrap;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 11px;
  color: #6B7280;
  font-weight: 500;
}

.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  display: inline-block;
  flex-shrink: 0;
}

.dot-resolved { background: #166534; }
.dot-progress { background: #4ADE80; }
.dot-open     { background: rgba(22,101,52,0.12); border: 1.5px solid #D1D5DB; }


/* ===== RESPONSIVE ===== */
@media (max-width: 1200px) {
  .mid-row { grid-template-columns: 1fr 1fr; }
  .requests-panel { grid-column: 1 / -1; }
}

@media (max-width: 900px) {
  .stats-row { grid-template-columns: repeat(2, 1fr); }
  .mid-row   { grid-template-columns: 1fr; }
  .bot-row   { grid-template-columns: 1fr; }
}

@media (max-width: 768px) {
  /* On mobile App.vue shows its own topbar — hide the in-page topbar */
  .topbar { display: none; }

  .dash-header { flex-direction: column; align-items: flex-start; gap: 12px; }
  .dash-title  { font-size: clamp(22px, 6vw, 28px); }
  .dash-actions { width: 100%; }
  .btn-add-request, .btn-export { flex: 1; justify-content: center; }

  .stats-row { grid-template-columns: repeat(2, 1fr); gap: 10px; }
  .scard { min-height: 100px; padding: 16px; }
  .scard-value { font-size: 28px; }

  .mid-row { grid-template-columns: 1fr; gap: 10px; }
  .bot-row  { grid-template-columns: 1fr; gap: 10px; }
}

@media (max-width: 480px) {
  .stats-row { grid-template-columns: 1fr 1fr; gap: 8px; }
  .scard { padding: 14px; min-height: 90px; }
  .scard-value { font-size: 24px; }
  .scard-label { font-size: 11px; }
  .scard-trend { font-size: 10px; }
  .scard-arrow { width: 26px; height: 26px; }

  .dash-actions { flex-direction: column; }

  .search-bar { min-width: unset; flex: 1; }
  .search-kbd  { display: none; }
  .user-profile-email { display: none; }
}

@media (max-width: 380px) {
  .stats-row { grid-template-columns: 1fr; }
}
</style>
