import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/authStore'

const routes = [
  // ── Public ──
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginView.vue'),
    meta: { guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/RegisterView.vue'),
    meta: { guest: true }
  },

  // ── Tenant ──
  {
    path: '/my-requests',
    name: 'MyRequests',
    component: () => import('../views/MyRequestsView.vue'),
    meta: { requiresAuth: true, role: 'TENANT' }
  },

  // ── Staff / Admin ──
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/DashboardView.vue'),
    meta: { requiresAuth: true, role: 'STAFF_OR_ADMIN' }
  },
  {
    path: '/analytics',
    name: 'Analytics',
    component: () => import('../views/AnalyticsView.vue'),
    meta: { requiresAuth: true, role: 'STAFF_OR_ADMIN' }
  },
  {
    path: '/team',
    name: 'Team',
    component: () => import('../views/TeamView.vue'),
    meta: { requiresAuth: true, role: 'STAFF_OR_ADMIN' }
  },

  // ── All authenticated ──
  {
    path: '/requests/:id',
    name: 'RequestDetail',
    component: () => import('../views/RequestDetailView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/calendar',
    name: 'Calendar',
    component: () => import('../views/CalendarView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('../views/SettingsView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/help',
    name: 'Help',
    component: () => import('../views/HelpView.vue'),
    meta: { requiresAuth: true }
  },

  // ── Root redirect ──
  { path: '/', redirect: '/login' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const auth = useAuthStore()

  if (to.meta.requiresAuth && !auth.isLoggedIn) return next('/login')
  if (to.meta.guest && auth.isLoggedIn) return next(auth.isTenant ? '/my-requests' : '/dashboard')
  if (to.meta.role === 'TENANT' && !auth.isTenant) return next('/dashboard')
  if (to.meta.role === 'STAFF_OR_ADMIN' && !auth.isStaffOrAdmin) return next('/my-requests')

  next()
})

export default router
