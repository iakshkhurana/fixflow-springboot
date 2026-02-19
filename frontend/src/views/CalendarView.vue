<template>
  <div class="cal-root">
    <!-- Header -->
    <div class="page-header">
      <div>
        <h1 class="page-title">Calendar</h1>
        <p class="page-subtitle">View maintenance requests scheduled by date</p>
      </div>
      <div class="cal-nav">
        <button class="nav-btn" @click="prevMonth">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><polyline points="15 18 9 12 15 6"/></svg>
        </button>
        <span class="cal-month-label">{{ monthLabel }}</span>
        <button class="nav-btn" @click="nextMonth">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><polyline points="9 18 15 12 9 6"/></svg>
        </button>
      </div>
    </div>

    <div class="cal-layout">
      <!-- Calendar Grid -->
      <div class="card cal-card">
        <!-- Day headers -->
        <div class="cal-grid-head">
          <div v-for="d in ['Sun','Mon','Tue','Wed','Thu','Fri','Sat']" :key="d" class="cal-day-name">{{ d }}</div>
        </div>

        <!-- Day cells -->
        <div class="cal-grid">
          <div
            v-for="cell in calCells"
            :key="cell.key"
            class="cal-cell"
            :class="{
              'other-month': !cell.inMonth,
              'today': cell.isToday,
              'selected': selectedDay === cell.dateStr,
              'has-events': cell.inMonth && cell.requests.length > 0
            }"
            @click="cell.inMonth && selectDay(cell)"
          >
            <span class="cell-num">{{ cell.day }}</span>
            <div class="cell-dots" v-if="cell.inMonth && cell.requests.length">
              <span
                v-for="(req, i) in cell.requests.slice(0, 3)"
                :key="i"
                class="event-dot"
                :class="`dot-${getPriorityColor(req.priority)}`"
              ></span>
              <span v-if="cell.requests.length > 3" class="dot-more">+{{ cell.requests.length - 3 }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Side Panel: selected day events -->
      <div class="card side-panel">
        <h3 class="panel-title">
          {{ selectedDay ? formatPanelDate(selectedDay) : 'Select a date' }}
        </h3>
        <div v-if="!selectedDay" class="panel-empty">
          <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#D1D5DB" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/>
          </svg>
          <p>Click any day to see requests</p>
        </div>
        <div v-else-if="selectedRequests.length === 0" class="panel-empty">
          <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#D1D5DB" stroke-width="1.5" stroke-linecap="round">
            <circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>
          </svg>
          <p>No requests on this day</p>
        </div>
        <div v-else class="panel-list">
          <div
            v-for="req in selectedRequests"
            :key="req.id"
            class="panel-item"
            @click="router.push(`/requests/${req.id}`)"
          >
            <div class="panel-item-dot" :class="`dot-${getPriorityColor(req.priority)}`"></div>
            <div class="panel-item-body">
              <div class="panel-item-title">{{ req.title }}</div>
              <div class="panel-item-meta">
                <span class="badge-cat">{{ req.category }}</span>
                <span class="badge-status" :class="`st-${req.status?.toLowerCase()}`">{{ req.status?.replace('_',' ') }}</span>
              </div>
            </div>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#9CA3AF" stroke-width="2" stroke-linecap="round"><polyline points="9 18 15 12 9 6"/></svg>
          </div>
        </div>

        <!-- Legend -->
        <div class="cal-legend">
          <span class="legend-row"><i class="event-dot dot-critical"></i>Critical</span>
          <span class="legend-row"><i class="event-dot dot-high"></i>High</span>
          <span class="legend-row"><i class="event-dot dot-medium"></i>Medium</span>
          <span class="legend-row"><i class="event-dot dot-low"></i>Low</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useRequestStore } from '../stores/requestStore'
import { useAuthStore } from '../stores/authStore'
import api from '../services/api'

const router = useRouter()
const requestStore = useRequestStore()
const authStore = useAuthStore()

const today = new Date()
const viewYear  = ref(today.getFullYear())
const viewMonth = ref(today.getMonth()) // 0-based
const selectedDay  = ref(null)
const allRequests  = ref([])

onMounted(async () => {
  try {
    if (authStore.isStaffOrAdmin) {
      await requestStore.fetchAllRequests()
      allRequests.value = requestStore.requests
    } else {
      const { data } = await api.get('/requests/my')
      allRequests.value = data
    }
  } catch (e) { console.error(e) }
})

const monthLabel = computed(() => {
  return new Date(viewYear.value, viewMonth.value, 1)
    .toLocaleDateString('en-US', { month: 'long', year: 'numeric' })
})

function prevMonth() {
  if (viewMonth.value === 0) { viewMonth.value = 11; viewYear.value-- }
  else viewMonth.value--
  selectedDay.value = null
}

function nextMonth() {
  if (viewMonth.value === 11) { viewMonth.value = 0; viewYear.value++ }
  else viewMonth.value++
  selectedDay.value = null
}

// Build calendar cells
const calCells = computed(() => {
  const firstDay = new Date(viewYear.value, viewMonth.value, 1)
  const lastDay  = new Date(viewYear.value, viewMonth.value + 1, 0)
  const cells = []
  const startOffset = firstDay.getDay()

  // Previous month padding
  for (let i = startOffset - 1; i >= 0; i--) {
    const d = new Date(viewYear.value, viewMonth.value, -i)
    cells.push({ key: `p${d.toDateString()}`, day: d.getDate(), inMonth: false, dateStr: '', isToday: false, requests: [] })
  }

  // Current month
  for (let d = 1; d <= lastDay.getDate(); d++) {
    const date = new Date(viewYear.value, viewMonth.value, d)
    const dateStr = toDateStr(date)
    const isToday = dateStr === toDateStr(today)
    const reqs = allRequests.value.filter(r => r.createdAt && toDateStr(new Date(r.createdAt)) === dateStr)
    cells.push({ key: dateStr, day: d, inMonth: true, dateStr, isToday, requests: reqs })
  }

  // Next month padding to fill 6 rows
  const remaining = 42 - cells.length
  for (let d = 1; d <= remaining; d++) {
    cells.push({ key: `n${d}`, day: d, inMonth: false, dateStr: '', isToday: false, requests: [] })
  }

  return cells
})

function selectDay(cell) {
  selectedDay.value = selectedDay.value === cell.dateStr ? null : cell.dateStr
}

const selectedRequests = computed(() => {
  if (!selectedDay.value) return []
  return allRequests.value.filter(r => r.createdAt && toDateStr(new Date(r.createdAt)) === selectedDay.value)
})

function toDateStr(d) {
  return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`
}

function formatPanelDate(str) {
  return new Date(str + 'T12:00:00').toLocaleDateString('en-US', { weekday: 'long', month: 'long', day: 'numeric' })
}

function getPriorityColor(p) {
  return { CRITICAL: 'critical', HIGH: 'high', MEDIUM: 'medium', LOW: 'low' }[p] || 'medium'
}
</script>

<style scoped>
.cal-root { display: flex; flex-direction: column; }

.cal-nav {
  display: flex;
  align-items: center;
  gap: 12px;
}

.nav-btn {
  width: 36px; height: 36px;
  border: 1.5px solid var(--border);
  border-radius: 9px; background: var(--white);
  cursor: pointer; display: flex;
  align-items: center; justify-content: center;
  color: #374151; transition: all 0.18s;
}
.nav-btn:hover { background: #F0FDF4; border-color: var(--primary); color: var(--primary); }

.cal-month-label { font-size: 15px; font-weight: 700; color: #111827; min-width: 160px; text-align: center; }

.cal-layout {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 16px;
}

/* Calendar grid */
.cal-card { padding: 0; overflow: hidden; }

.cal-grid-head {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  background: #F9FAFB;
  border-bottom: 1px solid var(--border);
}

.cal-day-name {
  padding: 10px 0;
  text-align: center;
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  color: #9CA3AF;
}

.cal-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
}

.cal-cell {
  min-height: 80px;
  padding: 8px;
  border-right: 1px solid #F3F4F6;
  border-bottom: 1px solid #F3F4F6;
  cursor: pointer;
  transition: background 0.15s;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.cal-cell:nth-child(7n) { border-right: none; }
.cal-cell:hover:not(.other-month) { background: #F0FDF4; }
.cal-cell.other-month { background: #FAFAFA; cursor: default; }
.cal-cell.selected { background: #DCFCE7; }

.cell-num {
  font-size: 13px;
  font-weight: 600;
  color: #374151;
  width: 24px; height: 24px;
  display: flex; align-items: center; justify-content: center;
  border-radius: 50%;
}

.other-month .cell-num { color: #D1D5DB; }
.today .cell-num { background: var(--primary); color: white; }
.selected .cell-num { font-weight: 800; }

.cell-dots {
  display: flex;
  flex-wrap: wrap;
  gap: 2px;
  align-items: center;
}

.event-dot {
  width: 7px; height: 7px;
  border-radius: 50%;
  display: inline-block;
  flex-shrink: 0;
}

.dot-critical { background: #EF4444; }
.dot-high     { background: #F97316; }
.dot-medium   { background: #F59E0B; }
.dot-low      { background: #22C55E; }

.dot-more { font-size: 9px; color: #9CA3AF; font-weight: 700; }

/* Side Panel */
.side-panel { display: flex; flex-direction: column; gap: 12px; height: fit-content; }

.panel-title { font-size: 15px; font-weight: 700; color: #111827; }

.panel-empty {
  display: flex; flex-direction: column;
  align-items: center; justify-content: center;
  gap: 10px; padding: 32px 0;
  color: #9CA3AF; font-size: 13px; text-align: center;
}

.panel-list { display: flex; flex-direction: column; gap: 8px; }

.panel-item {
  display: flex; align-items: center; gap: 10px;
  padding: 10px 12px;
  border: 1px solid var(--border);
  border-radius: 10px; cursor: pointer;
  transition: all 0.18s;
}
.panel-item:hover { border-color: var(--primary); background: #F0FDF4; }

.panel-item-dot { width: 10px; height: 10px; border-radius: 50%; flex-shrink: 0; }

.panel-item-body { flex: 1; min-width: 0; }
.panel-item-title { font-size: 13px; font-weight: 600; color: #111827; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.panel-item-meta { display: flex; gap: 6px; margin-top: 4px; flex-wrap: wrap; }

.badge-cat, .badge-status {
  font-size: 10px; font-weight: 700; padding: 2px 7px;
  border-radius: 20px; text-transform: uppercase; letter-spacing: 0.04em;
}
.badge-cat    { background: #F0FDF4; color: #166534; }
.st-open      { background: #F3F4F6; color: #374151; }
.st-assigned  { background: #FEF3C7; color: #92400E; }
.st-in_progress { background: #FFF7ED; color: #C2410C; }
.st-resolved  { background: #D1FAE5; color: #065F46; }
.st-closed    { background: #E5E7EB; color: #6B7280; }

.cal-legend {
  display: flex; gap: 12px; flex-wrap: wrap;
  padding-top: 12px;
  border-top: 1px solid var(--border);
  margin-top: auto;
}

.legend-row {
  display: flex; align-items: center; gap: 5px;
  font-size: 11px; color: #6B7280;
}

@media (max-width: 900px) {
  .cal-layout { grid-template-columns: 1fr; }
  .cal-cell { min-height: 60px; }
}

@media (max-width: 600px) {
  .cal-cell { min-height: 44px; padding: 4px; }
  .cell-num { font-size: 11px; width: 20px; height: 20px; }
  .cal-day-name { font-size: 9px; padding: 6px 0; }
  .page-header { flex-direction: column; align-items: flex-start; gap: 12px; }
}
</style>
