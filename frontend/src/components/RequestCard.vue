<template>
  <div class="request-card" @click="$emit('click', request.id)">
    <div class="card-header">
      <span class="category-tag">{{ request.category }}</span>
      <StatusBadge :status="request.status" />
    </div>

    <h3 class="card-title">{{ request.title }}</h3>

    <p class="card-description">{{ truncate(request.description, 100) }}</p>

    <div class="card-meta">
      <span class="meta-item">
        <span class="meta-icon">🏢</span>
        {{ request.buildingName }}
      </span>
      <span v-if="request.floorNumber" class="meta-item">
        <span class="meta-icon">🏗</span>
        Floor {{ request.floorNumber }}{{ request.roomNumber ? ` · Rm ${request.roomNumber}` : '' }}
      </span>
    </div>

    <div class="card-footer">
      <span class="priority-badge" :class="`priority-${request.priority?.toLowerCase()}`">
        {{ request.priority }}
      </span>
      <span class="date">{{ formatDate(request.createdAt) }}</span>
    </div>
  </div>
</template>

<script setup>
import StatusBadge from './StatusBadge.vue'

defineProps({
  request: { type: Object, required: true }
})

defineEmits(['click'])

function truncate(text, maxLen) {
  if (!text) return ''
  return text.length > maxLen ? text.slice(0, maxLen) + '...' : text
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('en-US', {
    month: 'short', day: 'numeric', year: 'numeric'
  })
}
</script>

<style scoped>
.request-card {
  background: var(--white);
  border-radius: var(--radius);
  padding: 20px;
  box-shadow: var(--shadow);
  cursor: pointer;
  transition: all 0.2s;
  border: 1.5px solid transparent;
}

.request-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
  border-color: var(--accent);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.category-tag {
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: var(--accent);
  background: rgba(46, 134, 193, 0.08);
  padding: 3px 10px;
  border-radius: 20px;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 8px;
  line-height: 1.4;
}

.card-description {
  font-size: 13px;
  color: var(--text-light);
  margin-bottom: 14px;
  line-height: 1.5;
}

.card-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 14px;
}

.meta-item {
  font-size: 12px;
  color: var(--text-light);
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 12px;
  border-top: 1px solid var(--border);
}

.priority-badge {
  font-size: 11px;
  font-weight: 600;
  padding: 3px 10px;
  border-radius: 20px;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.priority-low      { background: #f0fdf4; color: #166534; }
.priority-medium   { background: #fffbeb; color: #92400e; }
.priority-high     { background: #fff7ed; color: #c2410c; }
.priority-critical { background: #fef2f2; color: #991b1b; }

.date {
  font-size: 12px;
  color: var(--text-light);
}
</style>
