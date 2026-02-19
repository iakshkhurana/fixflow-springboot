<template>
  <div>
    <!-- Back button -->
    <button class="btn btn-outline btn-sm back-btn" @click="router.back()">
      ← Back
    </button>

    <div v-if="loading" class="loading">Loading request details...</div>
    <div v-else-if="error" class="alert alert-error">{{ error }}</div>

    <div v-else-if="request">
      <!-- Header -->
      <div class="detail-header card">
        <div class="detail-title-row">
          <h1 class="detail-title">{{ request.title }}</h1>
          <StatusBadge :status="request.status" />
        </div>
        <div class="detail-meta">
          <span class="category-tag">{{ request.category }}</span>
          <span class="priority-badge" :class="`priority-${request.priority?.toLowerCase()}`">
            {{ request.priority }}
          </span>
          <span class="meta-text">🏢 {{ request.buildingName }}</span>
          <span v-if="request.floorNumber" class="meta-text">
            Floor {{ request.floorNumber }}{{ request.roomNumber ? ` · Room ${request.roomNumber}` : '' }}
          </span>
        </div>
      </div>

      <!-- Details Grid -->
      <div class="detail-grid">
        <!-- Left: Description + Comments -->
        <div class="detail-left">
          <div class="card">
            <h3 class="section-label">Description</h3>
            <p class="description-text">{{ request.description || 'No description provided.' }}</p>
          </div>

          <div class="card">
            <CommentSection :requestId="request.id" />
          </div>
        </div>

        <!-- Right: Info + Actions -->
        <div class="detail-right">
          <!-- Request Info -->
          <div class="card info-card">
            <h3 class="section-label">Request Info</h3>
            <div class="info-row">
              <span class="info-key">Status</span>
              <StatusBadge :status="request.status" />
            </div>
            <div class="info-row">
              <span class="info-key">Submitted by</span>
              <span class="info-val">{{ request.createdByName }}</span>
            </div>
            <div class="info-row">
              <span class="info-key">Assigned to</span>
              <span class="info-val">{{ request.assignedToName || 'Unassigned' }}</span>
            </div>
            <div class="info-row">
              <span class="info-key">Created</span>
              <span class="info-val">{{ formatDate(request.createdAt) }}</span>
            </div>
            <div v-if="request.resolvedAt" class="info-row">
              <span class="info-key">Resolved</span>
              <span class="info-val">{{ formatDate(request.resolvedAt) }}</span>
            </div>
          </div>

          <!-- Actions Card -->
          <div class="card action-card">
            <h3 class="section-label">Actions</h3>

            <!-- Admin: Assign Staff -->
            <div v-if="authStore.isAdmin && request.status !== 'CLOSED'" class="action-group">
              <label class="action-label">Assign to Staff</label>
              <div class="assign-row">
                <select v-model="selectedStaffId" class="form-control">
                  <option value="">Select staff member</option>
                  <option v-for="s in staffList" :key="s.id" :value="s.id">
                    {{ s.fullName }}
                  </option>
                </select>
                <button
                  class="btn btn-primary btn-sm"
                  :disabled="!selectedStaffId || assigning"
                  @click="handleAssign"
                >
                  {{ assigning ? '...' : 'Assign' }}
                </button>
              </div>
            </div>

            <!-- Status Workflow Buttons -->
            <div class="workflow-buttons">
              <!-- OPEN → ASSIGNED (staff/admin only, done via assign) -->
              <!-- ASSIGNED → IN_PROGRESS (staff/admin) -->
              <button
                v-if="request.status === 'ASSIGNED' && authStore.isStaffOrAdmin"
                class="btn btn-primary"
                :disabled="updating"
                @click="handleStatusUpdate('IN_PROGRESS')"
              >
                🔨 Start Work
              </button>

              <!-- IN_PROGRESS → RESOLVED (staff/admin) -->
              <button
                v-if="request.status === 'IN_PROGRESS' && authStore.isStaffOrAdmin"
                class="btn btn-success"
                :disabled="updating"
                @click="handleStatusUpdate('RESOLVED')"
              >
                ✅ Mark as Resolved
              </button>

              <!-- RESOLVED → CLOSED (staff/admin can close) -->
              <button
                v-if="request.status === 'RESOLVED' && authStore.isStaffOrAdmin"
                class="btn btn-outline"
                :disabled="updating"
                @click="handleStatusUpdate('CLOSED')"
              >
                🔒 Close Request
              </button>
            </div>

            <div v-if="actionError" class="alert alert-error" style="margin-top: 12px">
              {{ actionError }}
            </div>
            <div v-if="actionSuccess" class="alert alert-success" style="margin-top: 12px">
              {{ actionSuccess }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import StatusBadge from '../components/StatusBadge.vue'
import CommentSection from '../components/CommentSection.vue'
import { useAuthStore } from '../stores/authStore'
import api from '../services/api'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const request = ref(null)
const staffList = ref([])
const selectedStaffId = ref('')
const loading = ref(true)
const error = ref('')
const actionError = ref('')
const actionSuccess = ref('')
const updating = ref(false)
const assigning = ref(false)

onMounted(async () => {
  await fetchRequest()
  if (authStore.isAdmin) {
    loadStaff()
  }
})

async function fetchRequest() {
  loading.value = true
  error.value = ''
  try {
    const { data } = await api.get(`/requests/${route.params.id}`)
    request.value = data
  } catch (e) {
    error.value = e.response?.data?.error || 'Request not found'
  } finally {
    loading.value = false
  }
}

async function loadStaff() {
  try {
    const { data } = await api.get('/users/staff')
    staffList.value = data
  } catch (e) {
    console.warn('Could not load staff list')
  }
}

async function handleStatusUpdate(newStatus) {
  updating.value = true
  actionError.value = ''
  actionSuccess.value = ''
  try {
    const { data } = await api.patch(`/requests/${request.value.id}/status`, { status: newStatus })
    request.value = data
    actionSuccess.value = `Status updated to ${newStatus}`
    setTimeout(() => { actionSuccess.value = '' }, 3000)
  } catch (e) {
    actionError.value = e.response?.data?.error || 'Failed to update status'
  } finally {
    updating.value = false
  }
}

async function handleAssign() {
  if (!selectedStaffId.value) return
  assigning.value = true
  actionError.value = ''
  actionSuccess.value = ''
  try {
    const { data } = await api.patch(`/requests/${request.value.id}/assign`, {
      staffId: selectedStaffId.value
    })
    request.value = data
    actionSuccess.value = 'Staff assigned successfully'
    setTimeout(() => { actionSuccess.value = '' }, 3000)
  } catch (e) {
    actionError.value = e.response?.data?.error || 'Failed to assign staff'
  } finally {
    assigning.value = false
  }
}

function formatDate(dateStr) {
  if (!dateStr) return '—'
  return new Date(dateStr).toLocaleString('en-US', {
    month: 'short', day: 'numeric', year: 'numeric',
    hour: '2-digit', minute: '2-digit'
  })
}
</script>

<style scoped>
.back-btn {
  margin-bottom: 16px;
}

.detail-header {
  margin-bottom: 16px;
}

.detail-title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 12px;
}

.detail-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--text);
  line-height: 1.3;
}

.detail-meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.category-tag {
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  color: var(--accent);
  background: rgba(46, 134, 193, 0.08);
  padding: 3px 10px;
  border-radius: 20px;
}

.priority-badge {
  font-size: 11px;
  font-weight: 600;
  padding: 3px 10px;
  border-radius: 20px;
  text-transform: uppercase;
}

.priority-low      { background: #f0fdf4; color: #166534; }
.priority-medium   { background: #fffbeb; color: #92400e; }
.priority-high     { background: #fff7ed; color: #c2410c; }
.priority-critical { background: #fef2f2; color: #991b1b; }

.meta-text {
  font-size: 13px;
  color: var(--text-light);
}

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 16px;
}

.detail-left, .detail-right {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.section-label {
  font-size: 13px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  color: var(--text-light);
  margin-bottom: 14px;
}

.description-text {
  color: var(--text);
  line-height: 1.7;
  white-space: pre-wrap;
}

.info-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid var(--border);
}

.info-row:last-child {
  border-bottom: none;
}

.info-key {
  font-size: 13px;
  color: var(--text-light);
}

.info-val {
  font-size: 13px;
  font-weight: 500;
  color: var(--text);
}

.action-group {
  margin-bottom: 16px;
}

.action-label {
  font-size: 13px;
  font-weight: 500;
  color: var(--text);
  display: block;
  margin-bottom: 8px;
}

.assign-row {
  display: flex;
  gap: 8px;
}

.assign-row .form-control {
  flex: 1;
  font-size: 13px;
}

.workflow-buttons {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.workflow-buttons .btn {
  justify-content: center;
}

@media (max-width: 900px) {
  .detail-grid { grid-template-columns: 1fr; }
}

@media (max-width: 600px) {
  .detail-title-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  .detail-title { font-size: 17px; }
  .detail-meta  { gap: 8px; }
  .assign-row   { flex-direction: column; }
  .assign-row .btn { width: 100%; justify-content: center; }
}

@media (max-width: 480px) {
  .back-btn { font-size: 13px; padding: 6px 12px; }
}
</style>
