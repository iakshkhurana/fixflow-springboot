<template>
  <div>
    <!-- Page Header -->
    <div class="page-header">
      <div>
        <h1 class="page-title">My Requests</h1>
        <p class="page-subtitle">Track the status of your maintenance requests</p>
      </div>
      <button class="btn btn-primary" @click="showForm = !showForm">
        {{ showForm ? '✕ Cancel' : '+ New Request' }}
      </button>
    </div>

    <!-- New Request Form (toggle) -->
    <div v-if="showForm" style="margin-bottom: 24px">
      <RequestForm @submitted="handleRequestSubmitted" @cancel="showForm = false" />
    </div>

    <!-- Loading state -->
    <div v-if="loading" class="loading">Loading your requests...</div>

    <!-- Error state -->
    <div v-else-if="error" class="alert alert-error">{{ error }}</div>

    <!-- Empty state -->
    <div v-else-if="requests.length === 0" class="empty-state card">
      <div class="empty-icon">📋</div>
      <h3>No requests yet</h3>
      <p>Click "New Request" to report a maintenance issue in your building.</p>
    </div>

    <!-- Request Cards Grid -->
    <div v-else class="cards-grid">
      <RequestCard
        v-for="request in requests"
        :key="request.id"
        :request="request"
        @click="navigateToDetail"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import RequestCard from '../components/RequestCard.vue'
import RequestForm from '../components/RequestForm.vue'
import api from '../services/api'

const router = useRouter()

const requests = ref([])
const loading = ref(true)
const error = ref('')
const showForm = ref(false)

onMounted(fetchMyRequests)

async function fetchMyRequests() {
  loading.value = true
  error.value = ''
  try {
    const { data } = await api.get('/requests/my')
    requests.value = data
  } catch (e) {
    error.value = e.response?.data?.error || 'Failed to load your requests'
  } finally {
    loading.value = false
  }
}

function handleRequestSubmitted(newRequest) {
  requests.value.unshift(newRequest)
  showForm.value = false
}

function navigateToDetail(id) {
  router.push(`/requests/${id}`)
}
</script>

<style scoped>
.cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(min(320px, 100%), 1fr));
  gap: 16px;
}

.empty-state {
  text-align: center;
  padding: 60px 24px;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
}

.empty-state p {
  color: var(--text-light);
}

@media (max-width: 480px) {
  .page-header  { flex-direction: column; align-items: flex-start; }
  .page-header .btn { width: 100%; justify-content: center; }
}
</style>
