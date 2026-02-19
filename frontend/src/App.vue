<template>
  <div id="app-root">
    <!-- Sidebar is shown for authenticated pages, not on login/register -->
    <Navbar v-if="authStore.isLoggedIn" />
    <main :class="{ 'with-sidebar': authStore.isLoggedIn }">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { RouterView } from 'vue-router'
import Navbar from './components/Navbar.vue'
import { useAuthStore } from './stores/authStore'

const authStore = useAuthStore()
</script>

<style>
/* ===== Global CSS Variables ===== */
:root {
  --primary: #6C63FF;
  --primary-dark: #5A52E0;
  --primary-light: #8B85FF;
  --accent: #6C63FF;
  --accent2: #FF6B6B;
  --success: #10B981;
  --warning: #F59E0B;
  --danger: #EF4444;
  --gray: #94A3B8;
  --light-bg: #F0F2FF;
  --white: #FFFFFF;
  --text: #1E1B4B;
  --text-light: #64748B;
  --border: #E8E8FF;
  --shadow: 0 2px 12px rgba(108, 99, 255, 0.08);
  --shadow-md: 0 8px 32px rgba(108, 99, 255, 0.15);
  --shadow-lg: 0 16px 48px rgba(108, 99, 255, 0.2);
  --radius: 12px;
  --radius-lg: 16px;
  --sidebar-width: 256px;
  --gradient: linear-gradient(135deg, #6C63FF 0%, #5A52E0 100%);
  --gradient-warm: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
  --gradient-success: linear-gradient(135deg, #10B981 0%, #059669 100%);
  --gradient-warning: linear-gradient(135deg, #F59E0B 0%, #D97706 100%);
  --gradient-dark: linear-gradient(135deg, #1E1B4B 0%, #312E81 100%);
}

/* ===== Reset & Base ===== */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  background-color: var(--light-bg);
  color: var(--text);
  font-size: 14px;
  line-height: 1.6;
}

#app-root {
  display: flex;
  min-height: 100vh;
}

main {
  flex: 1;
  min-height: 100vh;
  background-color: var(--light-bg);
}

main.with-sidebar {
  margin-left: var(--sidebar-width);
  padding: 28px 32px;
}

/* ===== Shared Components ===== */
.card {
  background: var(--white);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow);
  padding: 24px;
  border: 1px solid rgba(108, 99, 255, 0.06);
}

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
  transition: all 0.2s ease;
  text-decoration: none;
  letter-spacing: 0.01em;
}

.btn-primary {
  background: var(--gradient);
  color: var(--white);
  box-shadow: 0 4px 14px rgba(108, 99, 255, 0.35);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(108, 99, 255, 0.45);
}

.btn-primary:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.btn-success {
  background: var(--gradient-success);
  color: var(--white);
  box-shadow: 0 4px 14px rgba(16, 185, 129, 0.3);
}

.btn-success:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(16, 185, 129, 0.4);
}

.btn-danger {
  background: linear-gradient(135deg, #EF4444, #DC2626);
  color: var(--white);
  box-shadow: 0 4px 14px rgba(239, 68, 68, 0.3);
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
  box-shadow: 0 4px 14px rgba(108, 99, 255, 0.3);
}

.btn-sm {
  padding: 6px 14px;
  font-size: 13px;
}

/* ===== Form Elements ===== */
.form-group {
  margin-bottom: 18px;
}

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
  color: var(--text);
  background: var(--white);
  transition: all 0.2s ease;
  outline: none;
}

.form-control:focus {
  border-color: var(--primary);
  box-shadow: 0 0 0 4px rgba(108, 99, 255, 0.1);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

/* ===== Table ===== */
.table-wrapper {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

thead th {
  text-align: left;
  padding: 12px 16px;
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: var(--text-light);
  background: #F8F7FF;
  border-bottom: 2px solid var(--border);
}

tbody td {
  padding: 14px 16px;
  border-bottom: 1px solid var(--border);
  vertical-align: middle;
  font-size: 14px;
}

tbody tr:hover {
  background: linear-gradient(90deg, #F8F7FF, #FAFAFF);
  cursor: pointer;
}

tbody tr:last-child td {
  border-bottom: none;
}

/* ===== Page Header ===== */
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 28px;
}

.page-title {
  font-size: 24px;
  font-weight: 800;
  color: var(--text);
  letter-spacing: -0.02em;
}

.page-subtitle {
  font-size: 14px;
  color: var(--text-light);
  margin-top: 3px;
}

/* ===== Alert / Error ===== */
.alert {
  padding: 13px 16px;
  border-radius: 10px;
  margin-bottom: 16px;
  font-size: 14px;
  font-weight: 500;
}

.alert-error {
  background: #FEF2F2;
  color: #991B1B;
  border: 1px solid #FCA5A5;
}

.alert-success {
  background: #ECFDF5;
  color: #065F46;
  border: 1px solid #6EE7B7;
}

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

/* ===== Responsive ===== */
@media (max-width: 768px) {
  main.with-sidebar {
    margin-left: 0;
    padding: 16px;
  }
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>
