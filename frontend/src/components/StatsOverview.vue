<template>
  <div class="stats-grid">
    <div class="stat-card stat-open">
      <div class="stat-icon-wrap">
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/>
        </svg>
      </div>
      <div class="stat-info">
        <div class="stat-value">{{ stats?.totalOpen ?? 0 }}</div>
        <div class="stat-label">Open</div>
      </div>
      <div class="stat-trend">New</div>
    </div>

    <div class="stat-card stat-assigned">
      <div class="stat-icon-wrap">
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
          <circle cx="12" cy="7" r="4"/>
        </svg>
      </div>
      <div class="stat-info">
        <div class="stat-value">{{ stats?.totalAssigned ?? 0 }}</div>
        <div class="stat-label">Assigned</div>
      </div>
      <div class="stat-trend">Active</div>
    </div>

    <div class="stat-card stat-inprogress">
      <div class="stat-icon-wrap">
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="12" cy="12" r="10"/>
          <polyline points="12 6 12 12 16 14"/>
        </svg>
      </div>
      <div class="stat-info">
        <div class="stat-value">{{ stats?.totalInProgress ?? 0 }}</div>
        <div class="stat-label">In Progress</div>
      </div>
      <div class="stat-trend">Working</div>
    </div>

    <div class="stat-card stat-resolved">
      <div class="stat-icon-wrap">
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="20 6 9 17 4 12"/>
        </svg>
      </div>
      <div class="stat-info">
        <div class="stat-value">{{ stats?.totalResolved ?? 0 }}</div>
        <div class="stat-label">Resolved</div>
      </div>
      <div class="stat-trend">Done</div>
    </div>

    <div class="stat-card stat-closed">
      <div class="stat-icon-wrap">
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
          <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
        </svg>
      </div>
      <div class="stat-info">
        <div class="stat-value">{{ stats?.totalClosed ?? 0 }}</div>
        <div class="stat-label">Closed</div>
      </div>
      <div class="stat-trend">Final</div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  stats: { type: Object, default: null }
})
</script>

<style scoped>
.stats-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
  margin-bottom: 28px;
}

.stat-card {
  border-radius: var(--radius-lg);
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 14px;
  transition: transform 0.25s ease, box-shadow 0.25s ease;
  position: relative;
  overflow: hidden;
  color: white;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: -30px;
  right: -30px;
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-lg);
}

.stat-open       { background: linear-gradient(135deg, #6C63FF 0%, #5A52E0 100%); box-shadow: 0 8px 24px rgba(108, 99, 255, 0.35); }
.stat-assigned   { background: linear-gradient(135deg, #FF6B6B 0%, #EE4B4B 100%); box-shadow: 0 8px 24px rgba(255, 107, 107, 0.35); }
.stat-inprogress { background: linear-gradient(135deg, #F59E0B 0%, #D97706 100%); box-shadow: 0 8px 24px rgba(245, 158, 11, 0.35); }
.stat-resolved   { background: linear-gradient(135deg, #10B981 0%, #059669 100%); box-shadow: 0 8px 24px rgba(16, 185, 129, 0.35); }
.stat-closed     { background: linear-gradient(135deg, #64748B 0%, #475569 100%); box-shadow: 0 8px 24px rgba(100, 116, 139, 0.3); }

.stat-icon-wrap {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  backdrop-filter: blur(4px);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 800;
  color: white;
  line-height: 1;
  letter-spacing: -0.02em;
}

.stat-label {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 4px;
  font-weight: 500;
}

.stat-trend {
  font-size: 10px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  padding: 3px 8px;
  border-radius: 20px;
  position: absolute;
  top: 14px;
  right: 14px;
}

@media (max-width: 1100px) {
  .stats-grid { grid-template-columns: repeat(3, 1fr); }
}

@media (max-width: 700px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
