<template>
  <span class="badge" :class="`badge-${statusClass}`">{{ label }}</span>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  status: { type: String, required: true }
})

const statusMap = {
  OPEN:        { label: 'Open',        cls: 'open' },
  ASSIGNED:    { label: 'Assigned',    cls: 'assigned' },
  IN_PROGRESS: { label: 'In Progress', cls: 'in-progress' },
  RESOLVED:    { label: 'Resolved',    cls: 'resolved' },
  CLOSED:      { label: 'Closed',      cls: 'closed' },
}

const statusClass = computed(() => statusMap[props.status]?.cls || 'open')
const label = computed(() => statusMap[props.status]?.label || props.status)
</script>

<style scoped>
.badge {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
}

.badge-open        { background: #e8edf2; color: #5a6475; }
.badge-assigned    { background: #dbeafe; color: #1d4ed8; }
.badge-in-progress { background: #fef3c7; color: #b45309; }
.badge-resolved    { background: #d1fae5; color: #065f46; }
.badge-closed      { background: #f3f4f6; color: #6b7280; }
</style>
