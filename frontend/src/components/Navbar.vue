<template>
  <aside class="sidebar" :class="{ 'is-open': isOpen }">

    <!-- Logo -->
    <div class="sidebar-logo">
      <div class="logo-home" @click="goHome" role="button" tabindex="0" @keydown.enter="goHome">
        <div class="logo-mark">
          <svg width="34" height="34" viewBox="0 0 34 34" fill="none">
            <rect width="34" height="34" rx="10" fill="#0F3D22"/>
            <rect x="9" y="9" width="4" height="16" rx="2" fill="white"/>
            <rect x="9" y="9" width="14" height="4" rx="2" fill="white"/>
            <rect x="9" y="16" width="10" height="3.5" rx="1.75" fill="white"/>
            <circle cx="25" cy="8" r="3" fill="#4ADE80"/>
          </svg>
        </div>
        <span class="logo-wordmark"><span class="lw-fix">Fix</span><span class="lw-flow">Flow</span></span>
      </div>
      <button class="sidebar-close" @click="$emit('close')" aria-label="Close menu">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round">
          <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
        </svg>
      </button>
    </div>

    <!-- Navigation -->
    <nav class="sidebar-nav">

      <!-- ── MENU ── -->
      <div class="nav-section-label">MENU</div>

      <RouterLink v-if="authStore.isStaffOrAdmin" to="/dashboard" class="nav-item" active-class="active">
        <span class="nav-icon">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/>
            <rect x="14" y="14" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/>
          </svg>
        </span>
        Dashboard
      </RouterLink>

      <RouterLink v-if="authStore.isTenant" to="/my-requests" class="nav-item" active-class="active">
        <span class="nav-icon">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
            <polyline points="14 2 14 8 20 8"/>
            <line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/>
          </svg>
        </span>
        My Requests
        <span v-if="activeRequestCount > 0" class="nav-badge">{{ activeRequestCount }}</span>
      </RouterLink>

      <RouterLink to="/calendar" class="nav-item" active-class="active">
        <span class="nav-icon">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/>
            <line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/>
          </svg>
        </span>
        Calendar
      </RouterLink>

      <RouterLink v-if="authStore.isStaffOrAdmin" to="/analytics" class="nav-item" active-class="active">
        <span class="nav-icon">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/>
            <line x1="6" y1="20" x2="6" y2="14"/>
          </svg>
        </span>
        Analytics
      </RouterLink>

      <RouterLink v-if="authStore.isStaffOrAdmin" to="/team" class="nav-item" active-class="active">
        <span class="nav-icon">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
            <circle cx="9" cy="7" r="4"/>
            <path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/>
          </svg>
        </span>
        Team
      </RouterLink>

      <!-- ── GENERAL ── -->
      <div class="nav-section-label" style="margin-top:14px">GENERAL</div>

      <RouterLink to="/settings" class="nav-item" active-class="active">
        <span class="nav-icon">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="3"/>
            <path d="M19.07 4.93a10 10 0 0 1 0 14.14M4.93 4.93a10 10 0 0 0 0 14.14"/>
          </svg>
        </span>
        Settings
      </RouterLink>

      <RouterLink to="/help" class="nav-item" active-class="active">
        <span class="nav-icon">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10"/>
            <path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"/><line x1="12" y1="17" x2="12.01" y2="17"/>
          </svg>
        </span>
        Help
      </RouterLink>

    </nav>

    <!-- User Profile Card -->
    <div class="user-card">
      <div class="user-avatar">{{ initials }}</div>
      <div class="user-info">
        <div class="user-name">{{ authStore.user?.fullName }}</div>
        <div class="user-role">{{ authStore.user?.role }}</div>
      </div>
      <button class="logout-btn" @click="handleLogout" title="Sign out">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
          <polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/>
        </svg>
      </button>
    </div>

  </aside>
</template>

<script setup>
import { computed } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/authStore'
import { useRequestStore } from '../stores/requestStore'

const props = defineProps({ isOpen: { type: Boolean, default: false } })
defineEmits(['close'])

const authStore   = useAuthStore()
const requestStore = useRequestStore()
const router       = useRouter()

const initials = computed(() => {
  const name = authStore.user?.fullName || ''
  return name.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase()
})

const activeRequestCount = computed(() =>
  requestStore.requests.filter(r => ['OPEN', 'ASSIGNED', 'IN_PROGRESS'].includes(r.status)).length
)

function goHome() {
  router.push(authStore.isTenant ? '/my-requests' : '/dashboard')
}

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Outfit:wght@800&display=swap');

.sidebar {
  width: var(--sidebar-width);
  background: #FFFFFF;
  height: 100vh;
  position: fixed;
  left: 0; top: 0;
  display: flex;
  flex-direction: column;
  z-index: 200;
  border-right: 1px solid #E5E7EB;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

/* ── Logo ── */
.sidebar-logo {
  display: flex;
  align-items: center;
  gap: 11px;
  padding: 18px 18px 14px;
  border-bottom: 1px solid #F3F4F6;
  flex-shrink: 0;
}

.logo-home {
  display: flex; align-items: center; gap: 11px;
  flex: 1; cursor: pointer; min-width: 0;
  border-radius: 8px;
  transition: opacity 0.15s;
}
.logo-home:hover { opacity: 0.8; }
.logo-home:focus-visible { outline: 2px solid #22C55E; outline-offset: 3px; }

.logo-mark { flex-shrink: 0; display: flex; }

.logo-wordmark {
  font-family: 'Outfit', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  font-size: 22px;
  font-weight: 800;
  letter-spacing: -0.04em;
  line-height: 1;
}
.lw-fix  { color: #111827; }
.lw-flow { color: #166534; }

.sidebar-close {
  display: none;
  width: 30px; height: 30px;
  border: none; background: #F3F4F6;
  border-radius: 8px; cursor: pointer;
  align-items: center; justify-content: center;
  color: #6B7280; flex-shrink: 0; transition: background 0.2s;
}
.sidebar-close:hover { background: #E5E7EB; color: #111827; }

/* ── Nav ── */
.sidebar-nav { flex: 1; padding: 14px 12px; overflow-y: auto; }

.nav-section-label {
  font-size: 10px; font-weight: 700; text-transform: uppercase;
  letter-spacing: 0.12em; color: #9CA3AF;
  padding: 0 10px; margin-bottom: 6px; margin-top: 4px;
}

.nav-item {
  display: flex; align-items: center; gap: 10px;
  padding: 10px 12px; border-radius: 10px;
  color: #6B7280; text-decoration: none;
  font-size: 14px; font-weight: 500;
  transition: all 0.18s ease;
  margin-bottom: 2px; position: relative;
  border: none; background: transparent;
  cursor: pointer; width: 100%; text-align: left; font-family: inherit;
}

.nav-item:hover { background: #F9FAFB; color: #111827; }

.nav-item.active {
  background: #F0FDF4; color: #166534; font-weight: 600;
}

.nav-item.active::before {
  content: '';
  position: absolute; left: 0; top: 8px; bottom: 8px;
  width: 3px; background: #166534; border-radius: 0 3px 3px 0;
}

.nav-item.active .nav-icon { color: #166534; }

.nav-icon { width: 20px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }

.nav-badge {
  margin-left: auto; background: #166534; color: white;
  font-size: 10px; font-weight: 700; padding: 2px 7px;
  border-radius: 20px; min-width: 22px; text-align: center;
}

/* ── User Card ── */
.user-card {
  display: flex; align-items: center; gap: 10px;
  padding: 14px 16px; border-top: 1px solid #E5E7EB;
  flex-shrink: 0;
}

.user-avatar {
  width: 36px; height: 36px; border-radius: 50%;
  background: linear-gradient(135deg, #166534, #22C55E);
  color: white; font-size: 12px; font-weight: 800;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}

.user-info { flex: 1; min-width: 0; }
.user-name { font-size: 13px; font-weight: 700; color: #111827; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.user-role { font-size: 11px; color: #9CA3AF; text-transform: uppercase; letter-spacing: 0.05em; }

.logout-btn {
  width: 32px; height: 32px; border-radius: 8px;
  border: 1px solid #E5E7EB; background: transparent;
  color: #9CA3AF; cursor: pointer; display: flex;
  align-items: center; justify-content: center;
  transition: all 0.18s; flex-shrink: 0;
}
.logout-btn:hover { background: #FEF2F2; color: #DC2626; border-color: #FCA5A5; }

/* ── Mobile ── */
@media (max-width: 768px) {
  .sidebar { transform: translateX(-100%); height: 100dvh; }
  .sidebar.is-open { transform: translateX(0); box-shadow: var(--shadow-lg); }
  .sidebar-close { display: flex; }
}
</style>
