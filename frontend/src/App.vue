<template>
  <div id="app-root">

    <!-- Mobile Top Bar (shown only on small screens, authenticated) -->
    <div v-if="authStore.isLoggedIn" class="mobile-topbar">
      <div class="mobile-logo">
        <svg width="28" height="28" viewBox="0 0 38 38" fill="none">
          <defs>
            <linearGradient id="m-logo-grad" x1="0" y1="0" x2="38" y2="38" gradientUnits="userSpaceOnUse">
              <stop offset="0%" stop-color="#1B7A42"/>
              <stop offset="100%" stop-color="#0F4D29"/>
            </linearGradient>
          </defs>
          <rect width="38" height="38" rx="11" fill="url(#m-logo-grad)"/>
          <g transform="translate(7, 7)">
            <path d="M20.24 5.76a4.5 4.5 0 0 1-6.14 6.14L5.76 20.24a3 3 0 0 1-4.24-4.24l8.34-8.34a4.5 4.5 0 0 1 6.14-6.14l-2.47 2.47 2.47 2.47 2.24-2.24z" stroke="white" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" fill="none"/>
          </g>
        </svg>
        <span>FixFlow</span>
      </div>
      <button class="hamburger" @click="sidebarOpen = !sidebarOpen" :class="{ active: sidebarOpen }" aria-label="Toggle navigation">
        <span></span>
        <span></span>
        <span></span>
      </button>
    </div>

    <!-- Sidebar overlay (mobile) -->
    <Transition name="fade">
      <div
        v-if="authStore.isLoggedIn && sidebarOpen"
        class="sidebar-overlay"
        @click="sidebarOpen = false"
      ></div>
    </Transition>

    <!-- Sidebar -->
    <Navbar
      v-if="authStore.isLoggedIn"
      :is-open="sidebarOpen"
      @close="sidebarOpen = false"
    />

    <!-- Main Content -->
    <main :class="{ 'with-sidebar': authStore.isLoggedIn }">
      <RouterView />
    </main>

  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { RouterView, useRoute } from 'vue-router'
import Navbar from './components/Navbar.vue'
import { useAuthStore } from './stores/authStore'

const authStore = useAuthStore()
const route = useRoute()
const sidebarOpen = ref(false)

// Close sidebar on route change (mobile UX)
watch(() => route.path, () => { sidebarOpen.value = false })
</script>

<style>
/* ===== CSS Custom Properties ===== */
:root {
  /* Colors */
  --primary:          #166534;
  --primary-dark:     #14532D;
  --primary-light:    #16A34A;
  --accent:           #166534;
  --accent2:          #22C55E;
  --success:          #16A34A;
  --warning:          #F59E0B;
  --danger:           #EF4444;
  --gray:             #9CA3AF;
  --light-bg:         #F3F4F6;
  --white:            #FFFFFF;
  --text:             #111827;
  --text-light:       #6B7280;
  --border:           #E5E7EB;

  /* Shadows */
  --shadow:           0 1px 8px  rgba(0,0,0,0.06);
  --shadow-md:        0 4px 24px rgba(0,0,0,0.09);
  --shadow-lg:        0 12px 40px rgba(0,0,0,0.13);

  /* Shape */
  --radius:           12px;
  --radius-lg:        16px;

  /* Layout */
  --sidebar-width:    260px;
  --mobile-topbar-h:  56px;
  --content-px:       32px;
  --content-py:       28px;

  /* Gradients */
  --gradient:         linear-gradient(135deg, #166534 0%, #14532D 100%);
  --gradient-warm:    linear-gradient(135deg, #F59E0B 0%, #D97706 100%);
  --gradient-success: linear-gradient(135deg, #16A34A 0%, #15803D 100%);
  --gradient-warning: linear-gradient(135deg, #F59E0B 0%, #D97706 100%);
  --gradient-dark:    linear-gradient(135deg, #1B6B3A 0%, #0F3D22 100%);
}

/* ===== Reset ===== */
*, *::before, *::after {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html { scroll-behavior: smooth; }

body {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  background-color: var(--light-bg);
  color: var(--text);
  font-size: 14px;
  line-height: 1.6;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

#app-root {
  display: flex;
  min-height: 100vh;
}

/* ===== Mobile Top Bar ===== */
.mobile-topbar {
  display: none;
}

/* ===== Sidebar Overlay ===== */
.sidebar-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  z-index: 150;
  backdrop-filter: blur(2px);
}

/* ===== Hamburger ===== */
.hamburger {
  width: 38px;
  height: 38px;
  background: transparent;
  border: none;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 5px;
  padding: 6px;
  border-radius: 8px;
  transition: background 0.2s;
}

.hamburger:hover { background: rgba(0,0,0,0.06); }

.hamburger span {
  display: block;
  width: 20px;
  height: 2px;
  background: #374151;
  border-radius: 2px;
  transition: all 0.25s ease;
  transform-origin: center;
}

.hamburger.active span:nth-child(1) { transform: translateY(7px) rotate(45deg); }
.hamburger.active span:nth-child(2) { opacity: 0; transform: scaleX(0); }
.hamburger.active span:nth-child(3) { transform: translateY(-7px) rotate(-45deg); }

/* ===== Main Content ===== */
main {
  flex: 1;
  min-height: 100vh;
  background-color: var(--light-bg);
}

main.with-sidebar {
  margin-left: var(--sidebar-width);
  padding: var(--content-py) var(--content-px);
}

/* ===== Fade Transition ===== */
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

/* ===== Shared Card ===== */
.card {
  background: var(--white);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow);
  padding: 20px;
  border: 1px solid var(--border);
}

/* ===== Buttons ===== */
.btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 9px 18px;
  border-radius: 10px;
  border: none;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  font-family: inherit;
  transition: all 0.2s ease;
  text-decoration: none;
  letter-spacing: 0.01em;
  white-space: nowrap;
}

.btn-primary {
  background: var(--gradient);
  color: var(--white);
  box-shadow: 0 4px 14px rgba(22,101,52,0.35);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(22,101,52,0.45);
}

.btn-primary:disabled { opacity: 0.7; cursor: not-allowed; }

.btn-success {
  background: var(--gradient-success);
  color: var(--white);
  box-shadow: 0 4px 14px rgba(22,163,74,0.3);
}

.btn-success:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(22,163,74,0.4);
}

.btn-danger {
  background: linear-gradient(135deg, #EF4444, #DC2626);
  color: var(--white);
  box-shadow: 0 4px 14px rgba(239,68,68,0.3);
}

.btn-outline {
  background: transparent;
  color: var(--primary);
  border: 2px solid var(--primary);
}

.btn-outline:hover {
  background: var(--gradient);
  color: var(--white);
  border-color: transparent;
  box-shadow: 0 4px 14px rgba(22,101,52,0.3);
}

.btn-sm { padding: 6px 14px; font-size: 13px; }

/* ===== Forms ===== */
.form-group { margin-bottom: 18px; }

.form-group label {
  display: block;
  font-weight: 600;
  margin-bottom: 7px;
  color: var(--text);
  font-size: 13px;
}

.form-control {
  width: 100%;
  padding: 11px 14px;
  border: 2px solid var(--border);
  border-radius: 10px;
  font-size: 14px;
  font-family: inherit;
  color: var(--text);
  background: var(--white);
  transition: all 0.2s ease;
  outline: none;
  appearance: none;
  -webkit-appearance: none;
}

.form-control:focus {
  border-color: var(--primary);
  box-shadow: 0 0 0 4px rgba(22,101,52,0.1);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

/* ===== Tables ===== */
.table-wrapper { overflow-x: auto; -webkit-overflow-scrolling: touch; }

table { width: 100%; border-collapse: collapse; min-width: 500px; }

thead th {
  text-align: left;
  padding: 12px 16px;
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: var(--text-light);
  background: #F9FAFB;
  border-bottom: 2px solid var(--border);
  white-space: nowrap;
}

tbody td {
  padding: 14px 16px;
  border-bottom: 1px solid var(--border);
  vertical-align: middle;
  font-size: 14px;
}

tbody tr:hover { background: #F0FDF4; cursor: pointer; }
tbody tr:last-child td { border-bottom: none; }

/* ===== Page Headers ===== */
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 28px;
  flex-wrap: wrap;
}

.page-title {
  font-size: clamp(20px, 4vw, 26px);
  font-weight: 800;
  color: var(--text);
  letter-spacing: -0.02em;
}

.page-subtitle {
  font-size: 14px;
  color: var(--text-light);
  margin-top: 3px;
}

/* ===== Alerts ===== */
.alert {
  padding: 13px 16px;
  border-radius: 10px;
  margin-bottom: 16px;
  font-size: 14px;
  font-weight: 500;
}

.alert-error  { background: #FEF2F2; color: #991B1B; border: 1px solid #FCA5A5; }
.alert-success{ background: #F0FDF4; color: #166534; border: 1px solid #86EFAC; }

/* ===== Loading ===== */
.loading {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  color: var(--text-light);
  font-size: 15px;
  gap: 10px;
}

/* ===== Scrollbar ===== */
::-webkit-scrollbar { width: 6px; height: 6px; }
::-webkit-scrollbar-track { background: transparent; }
::-webkit-scrollbar-thumb { background: #D1D5DB; border-radius: 3px; }
::-webkit-scrollbar-thumb:hover { background: #9CA3AF; }

/* ===== RESPONSIVE ===== */
@media (max-width: 1024px) {
  :root { --content-px: 24px; }
}

@media (max-width: 768px) {
  :root {
    --content-px: 16px;
    --content-py: 16px;
  }

  /* Show mobile topbar */
  .mobile-topbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 16px;
    height: var(--mobile-topbar-h);
    background: #FFFFFF;
    border-bottom: 1px solid var(--border);
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 100;
    box-shadow: 0 1px 8px rgba(0,0,0,0.06);
  }

  .mobile-logo {
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 17px;
    font-weight: 800;
    color: #111827;
    letter-spacing: -0.02em;
  }

  /* Main content shifts down for mobile topbar */
  main.with-sidebar {
    margin-left: 0 !important;
    padding-top: calc(var(--mobile-topbar-h) + var(--content-py));
  }

  .form-row { grid-template-columns: 1fr; }
}

@media (max-width: 480px) {
  :root { --content-px: 12px; }
}
</style>
