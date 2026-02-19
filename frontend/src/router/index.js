import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/authStore'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginView.vue'),
    meta: { guest: true }  // Redirect to dashboard if already logged in
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/RegisterView.vue'),
    meta: { guest: true }
  },
  {
    path: '/my-requests',
    name: 'MyRequests',
    component: () => import('../views/MyRequestsView.vue'),
    meta: { requiresAuth: true, role: 'TENANT' }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/DashboardView.vue'),
    meta: { requiresAuth: true, role: 'STAFF_OR_ADMIN' }
  },
  {
    path: '/requests/:id',
    name: 'RequestDetail',
    component: () => import('../views/RequestDetailView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard — runs before every route change
router.beforeEach((to, from, next) => {
  const auth = useAuthStore()

  // Not logged in → redirect to login
  if (to.meta.requiresAuth && !auth.isLoggedIn) {
    return next('/login')
  }

  // Already logged in → redirect away from guest pages
  if (to.meta.guest && auth.isLoggedIn) {
    return next(auth.isTenant ? '/my-requests' : '/dashboard')
  }

  // Role check — tenants shouldn't access dashboard and vice versa
  if (to.meta.role === 'TENANT' && !auth.isTenant) {
    return next('/dashboard')
  }
  if (to.meta.role === 'STAFF_OR_ADMIN' && !auth.isStaffOrAdmin) {
    return next('/my-requests')
  }

  next()
})

export default router
