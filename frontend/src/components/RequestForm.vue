<template>
  <div class="form-card card">
    <h3 class="form-title">New Maintenance Request</h3>

    <div v-if="error" class="alert alert-error">{{ error }}</div>

    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label>Title *</label>
        <input
          v-model="form.title"
          class="form-control"
          placeholder="e.g. Broken AC in Room 305"
          minlength="5"
          required
        />
        <span v-if="form.title && form.title.length < 5" class="field-error">
          Title must be at least 5 characters
        </span>
      </div>

      <div class="form-group">
        <label>Description *</label>
        <textarea
          v-model="form.description"
          class="form-control"
          rows="3"
          placeholder="Describe the issue in detail..."
          required
        ></textarea>
      </div>

      <div class="form-row">
        <div class="form-group">
          <label>Category *</label>
          <select v-model="form.category" class="form-control" required>
            <option value="">Select category</option>
            <option v-for="cat in categories" :key="cat" :value="cat">{{ cat }}</option>
          </select>
        </div>

        <div class="form-group">
          <label>Priority *</label>
          <select v-model="form.priority" class="form-control" required>
            <option value="">Select priority</option>
            <option v-for="p in priorities" :key="p" :value="p">{{ p }}</option>
          </select>
        </div>
      </div>

      <div class="form-group">
        <label>Building *</label>
        <select v-model="form.buildingId" class="form-control" required>
          <option value="">Select building</option>
          <option v-for="b in buildings" :key="b.id" :value="b.id">{{ b.name }}</option>
        </select>
      </div>

      <div class="form-row">
        <div class="form-group">
          <label>Floor Number</label>
          <input
            v-model.number="form.floorNumber"
            type="number"
            class="form-control"
            placeholder="e.g. 3"
            min="1"
          />
        </div>

        <div class="form-group">
          <label>Room Number</label>
          <input
            v-model="form.roomNumber"
            class="form-control"
            placeholder="e.g. 305"
          />
        </div>
      </div>

      <div class="form-actions">
        <button type="button" class="btn btn-outline" @click="$emit('cancel')">
          Cancel
        </button>
        <button type="submit" class="btn btn-primary" :disabled="submitting">
          {{ submitting ? 'Submitting...' : 'Submit Request' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../services/api'

const emit = defineEmits(['submitted', 'cancel'])

const categories = ['PLUMBING', 'ELECTRICAL', 'HVAC', 'GENERAL', 'ELEVATOR', 'STRUCTURAL']
const priorities = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL']

const buildings = ref([])
const submitting = ref(false)
const error = ref('')

const form = ref({
  title: '',
  description: '',
  category: '',
  priority: '',
  buildingId: '',
  floorNumber: null,
  roomNumber: ''
})

onMounted(async () => {
  try {
    const { data } = await api.get('/buildings')
    buildings.value = data
  } catch (e) {
    error.value = 'Failed to load buildings'
  }
})

async function handleSubmit() {
  if (form.value.title.length < 5) {
    error.value = 'Title must be at least 5 characters'
    return
  }

  submitting.value = true
  error.value = ''

  try {
    const { data } = await api.post('/requests', {
      ...form.value,
      floorNumber: form.value.floorNumber || null,
      roomNumber: form.value.roomNumber || null
    })
    emit('submitted', data)
    // Reset form
    Object.assign(form.value, {
      title: '', description: '', category: '', priority: '',
      buildingId: '', floorNumber: null, roomNumber: ''
    })
  } catch (e) {
    error.value = e.response?.data?.error || 'Failed to submit request'
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.form-card {
  border: 1.5px solid var(--border);
}

.form-title {
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 20px;
  color: var(--text);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 8px;
}

textarea.form-control {
  resize: vertical;
  min-height: 80px;
}

.field-error {
  font-size: 12px;
  color: var(--danger);
  margin-top: 4px;
  display: block;
}
</style>
