import { defineStore } from 'pinia'
import api from '../services/api'

/**
 * Auth store — manages the logged-in user's session.
 * Persists to localStorage so the user stays logged in after page refresh.
 */
export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('user')) || null,
    token: localStorage.getItem('token') || null,
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    isTenant: (state) => state.user?.role === 'TENANT',
    isStaff: (state) => state.user?.role === 'STAFF',
    isAdmin: (state) => state.user?.role === 'ADMIN',
    isStaffOrAdmin: (state) => ['STAFF', 'ADMIN'].includes(state.user?.role),
  },

  actions: {
    async login(email, password) {
      const { data } = await api.post('/auth/login', { email, password })
      this._setSession(data)
    },

    async register(payload) {
      const { data } = await api.post('/auth/register', payload)
      this._setSession(data)
    },

    logout() {
      this.token = null
      this.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    },

    // Internal helper to set auth state + persist to localStorage
    _setSession(data) {
      this.token = data.token
      this.user = data.user
      localStorage.setItem('token', data.token)
      localStorage.setItem('user', JSON.stringify(data.user))
    }
  }
})
