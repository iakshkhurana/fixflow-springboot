<template>
  <div>
    <div class="page-header">
      <div>
        <h1 class="page-title">Team</h1>
        <p class="page-subtitle">Staff assignments and workload overview</p>
      </div>
    </div>

    <div v-if="loading" class="loading">Loading team data...</div>
    <div v-else>

      <!-- Summary cards -->
      <div class="team-summary">
        <div class="sum-card">
          <div class="sum-icon sum-green">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
          </div>
          <div class="sum-value">{{ staffList.length }}</div>
          <div class="sum-label">Total Staff</div>
        </div>
        <div class="sum-card">
          <div class="sum-icon sum-amber">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
          </div>
          <div class="sum-value">{{ assignedRequests }}</div>
          <div class="sum-label">Active Assignments</div>
        </div>
        <div class="sum-card">
          <div class="sum-icon sum-emerald">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round"><polyline points="20 6 9 17 4 12"/></svg>
          </div>
          <div class="sum-value">{{ resolvedCount }}</div>
          <div class="sum-label">Resolved This Month</div>
        </div>
        <div class="sum-card">
          <div class="sum-icon sum-blue">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round"><rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/></svg>
          </div>
          <div class="sum-value">{{ avgLoad }}</div>
          <div class="sum-label">Avg Requests / Staff</div>
        </div>
      </div>

      <!-- Staff Cards Grid -->
      <div class="staff-grid">
        <div v-for="staff in staffWithStats" :key="staff.id" class="staff-card card">
          <div class="staff-card-top">
            <div class="staff-big-av">{{ staff.initials }}</div>
            <div class="staff-card-info">
              <div class="staff-card-name">{{ staff.fullName }}</div>
              <div class="staff-card-email">{{ staff.email }}</div>
              <span class="role-badge">{{ staff.role }}</span>
            </div>
          </div>

          <div class="staff-stats">
            <div class="staff-stat">
              <div class="ss-val">{{ staff.assigned }}</div>
              <div class="ss-label">Assigned</div>
            </div>
            <div class="staff-stat-divider"></div>
            <div class="staff-stat">
              <div class="ss-val">{{ staff.inProgress }}</div>
              <div class="ss-label">In Progress</div>
            </div>
            <div class="staff-stat-divider"></div>
            <div class="staff-stat">
              <div class="ss-val ss-green">{{ staff.resolved }}</div>
              <div class="ss-label">Resolved</div>
            </div>
          </div>

          <!-- Active requests for this staff member -->
          <div v-if="staff.activeReqs.length > 0" class="active-reqs">
            <div class="active-label">Active Requests</div>
            <div
              v-for="req in staff.activeReqs.slice(0, 3)"
              :key="req.id"
              class="active-req-item"
              @click="router.push(`/requests/${req.id}`)"
            >
              <span class="req-status-dot" :class="`dot-${req.status?.toLowerCase().replace('_','-')}`"></span>
              <span class="req-name">{{ req.title }}</span>
              <span class="req-priority" :class="`p-${req.priority?.toLowerCase()}`">{{ req.priority }}</span>
            </div>
            <button v-if="staff.activeReqs.length > 3" class="view-more-btn" @click="router.push('/dashboard')">
              +{{ staff.activeReqs.length - 3 }} more
            </button>
          </div>
          <div v-else class="no-active">No active assignments</div>
        </div>
      </div>

      <!-- Unassigned Requests -->
      <div class="card unassigned-card" v-if="unassignedRequests.length > 0">
        <h3 class="section-h">
          <span class="title-bar"></span>
          Unassigned Requests
          <span class="badge-count">{{ unassignedRequests.length }}</span>
        </h3>
        <div class="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>#</th><th>Title</th><th>Category</th><th>Priority</th><th>Building</th><th>Created</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="req in unassignedRequests" :key="req.id" @click="router.push(`/requests/${req.id}`)">
                <td class="id-cell">#{{ req.id }}</td>
                <td class="title-cell">{{ req.title }}</td>
                <td><span class="cat-tag">{{ req.category }}</span></td>
                <td><span class="pri-badge" :class="`p-${req.priority?.toLowerCase()}`">{{ req.priority }}</span></td>
                <td>{{ req.buildingName }}</td>
                <td>{{ formatDate(req.createdAt) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useRequestStore } from '../stores/requestStore'
import api from '../services/api'

const router = useRouter()
const requestStore = useRequestStore()

const staffList = ref([])
const loading   = ref(true)

onMounted(async () => {
  try {
    const [{ data: staff }] = await Promise.all([
      api.get('/users/staff'),
      requestStore.fetchAllRequests()
    ])
    staffList.value = staff
  } catch (e) { console.error(e) }
  finally { loading.value = false }
})

const requests = computed(() => requestStore.requests)

const assignedRequests = computed(() =>
  requests.value.filter(r => ['ASSIGNED','IN_PROGRESS'].includes(r.status)).length
)

const resolvedCount = computed(() =>
  requests.value.filter(r => ['RESOLVED','CLOSED'].includes(r.status)).length
)

const avgLoad = computed(() => {
  if (!staffList.value.length) return 0
  return Math.round(assignedRequests.value / staffList.value.length * 10) / 10
})

const staffWithStats = computed(() =>
  staffList.value.map(s => {
    const myReqs = requests.value.filter(r => r.assignedToName === s.fullName)
    const active = myReqs.filter(r => ['ASSIGNED','IN_PROGRESS'].includes(r.status))
    return {
      ...s,
      initials: s.fullName.split(' ').map(n => n[0]).join('').slice(0,2).toUpperCase(),
      assigned:   myReqs.filter(r => r.status === 'ASSIGNED').length,
      inProgress: myReqs.filter(r => r.status === 'IN_PROGRESS').length,
      resolved:   myReqs.filter(r => ['RESOLVED','CLOSED'].includes(r.status)).length,
      activeReqs: active
    }
  })
)

const unassignedRequests = computed(() =>
  requests.value.filter(r => !r.assignedToName && r.status === 'OPEN')
)

function formatDate(d) {
  if (!d) return '—'
  return new Date(d).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
}
</script>

<style scoped>
.team-summary {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
  margin-bottom: 20px;
}

.sum-card {
  background: var(--white);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  padding: 18px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  transition: transform 0.2s;
}
.sum-card:hover { transform: translateY(-2px); box-shadow: var(--shadow-md); }

.sum-icon {
  width: 40px; height: 40px; border-radius: 11px;
  display: flex; align-items: center; justify-content: center;
}
.sum-green   { background: linear-gradient(135deg, #166534, #14532D); }
.sum-amber   { background: linear-gradient(135deg, #D97706, #B45309); }
.sum-emerald { background: linear-gradient(135deg, #059669, #047857); }
.sum-blue    { background: linear-gradient(135deg, #2563EB, #1D4ED8); }

.sum-value { font-size: 28px; font-weight: 800; color: #111827; letter-spacing: -0.02em; }
.sum-label { font-size: 12px; color: #6B7280; }

/* Staff cards */
.staff-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(min(320px, 100%), 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.staff-card { display: flex; flex-direction: column; gap: 14px; }

.staff-card-top {
  display: flex; align-items: center; gap: 14px;
}

.staff-big-av {
  width: 50px; height: 50px; border-radius: 14px;
  background: linear-gradient(135deg, #166534, #22C55E);
  color: white; font-size: 16px; font-weight: 800;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}

.staff-card-info { flex: 1; min-width: 0; }
.staff-card-name { font-size: 15px; font-weight: 700; color: #111827; }
.staff-card-email { font-size: 12px; color: #9CA3AF; margin: 2px 0; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

.role-badge {
  font-size: 10px; font-weight: 700; text-transform: uppercase;
  padding: 2px 8px; border-radius: 20px;
  background: #F0FDF4; color: #166534; letter-spacing: 0.05em;
}

.staff-stats {
  display: flex; align-items: center;
  background: #F9FAFB; border-radius: 10px; padding: 12px;
}

.staff-stat { flex: 1; text-align: center; }
.ss-val { font-size: 20px; font-weight: 800; color: #111827; }
.ss-green { color: #166534; }
.ss-label { font-size: 11px; color: #9CA3AF; margin-top: 2px; }
.staff-stat-divider { width: 1px; height: 32px; background: var(--border); }

/* Active requests */
.active-reqs { display: flex; flex-direction: column; gap: 6px; }
.active-label { font-size: 11px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.06em; color: #9CA3AF; margin-bottom: 4px; }

.active-req-item {
  display: flex; align-items: center; gap: 8px;
  padding: 8px 10px; border-radius: 8px;
  border: 1px solid var(--border); cursor: pointer; transition: all 0.18s;
}
.active-req-item:hover { border-color: var(--primary); background: #F0FDF4; }

.req-status-dot { width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0; }
.dot-assigned   { background: #F59E0B; }
.dot-in-progress { background: #F97316; }
.dot-open       { background: #9CA3AF; }

.req-name { flex: 1; font-size: 12px; font-weight: 500; color: #374151; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

.req-priority, .pri-badge {
  font-size: 10px; font-weight: 700; padding: 2px 6px;
  border-radius: 20px; text-transform: uppercase;
}
.p-critical { background: #FEF2F2; color: #991B1B; }
.p-high     { background: #FFF7ED; color: #C2410C; }
.p-medium   { background: #FFFBEB; color: #92400E; }
.p-low      { background: #F0FDF4; color: #166534; }

.view-more-btn {
  font-size: 12px; color: var(--primary); font-weight: 600;
  background: none; border: none; cursor: pointer; padding: 0; text-align: left;
}

.no-active { font-size: 13px; color: #9CA3AF; text-align: center; padding: 12px 0; }

/* Unassigned */
.unassigned-card { }

.section-h {
  font-size: 15px; font-weight: 700; color: #111827;
  margin-bottom: 16px; display: flex; align-items: center; gap: 8px;
}

.title-bar { display: inline-block; width: 4px; height: 16px; background: var(--gradient); border-radius: 2px; flex-shrink: 0; }

.badge-count {
  background: #FEF3C7; color: #92400E;
  font-size: 11px; font-weight: 700; padding: 2px 8px; border-radius: 20px;
}

.id-cell { font-size: 12px; color: var(--text-light); font-weight: 600; }
.title-cell { font-weight: 600; max-width: 200px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

.cat-tag {
  font-size: 11px; font-weight: 700; text-transform: uppercase;
  color: var(--primary); background: #F0FDF4; padding: 3px 8px; border-radius: 20px;
}

@media (max-width: 1024px) { .team-summary { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 600px)  { .team-summary { grid-template-columns: 1fr 1fr; } .page-header { flex-direction: column; } }
@media (max-width: 400px)  { .team-summary { grid-template-columns: 1fr; } }
</style>
