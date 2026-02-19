import { defineStore } from 'pinia'
import api from '../services/api'

/**
 * Request store — manages maintenance request data and API calls.
 * Separates API logic from components, making components cleaner.
 */
export const useRequestStore = defineStore('requests', {
  state: () => ({
    requests: [],
    currentRequest: null,
    stats: null,
    loading: false,
    error: null,
  }),

  actions: {
    // Staff/Admin: fetch all requests, optionally filtered by status
    async fetchAllRequests(status = null) {
      this.loading = true
      this.error = null
      try {
        const params = status ? { status } : {}
        const { data } = await api.get('/requests', { params })
        this.requests = data
      } catch (e) {
        this.error = e.response?.data?.error || 'Failed to load requests'
      } finally {
        this.loading = false
      }
    },

    // Tenant: fetch only their own requests
    async fetchMyRequests() {
      this.loading = true
      this.error = null
      try {
        const { data } = await api.get('/requests/my')
        this.requests = data
      } catch (e) {
        this.error = e.response?.data?.error || 'Failed to load your requests'
      } finally {
        this.loading = false
      }
    },

    // Any authenticated user: get a single request by ID
    async fetchById(id) {
      this.loading = true
      this.error = null
      try {
        const { data } = await api.get(`/requests/${id}`)
        this.currentRequest = data
      } catch (e) {
        this.error = e.response?.data?.error || 'Request not found'
      } finally {
        this.loading = false
      }
    },

    // Tenant: create a new request
    async createRequest(payload) {
      const { data } = await api.post('/requests', payload)
      this.requests.unshift(data) // Add to the top of the list
      return data
    },

    // Staff/Admin: advance request through the workflow
    async updateStatus(id, status) {
      const { data } = await api.patch(`/requests/${id}/status`, { status })
      this.currentRequest = data
      // Also update in the list if present
      const idx = this.requests.findIndex(r => r.id === id)
      if (idx !== -1) this.requests[idx] = data
      return data
    },

    // Admin: assign a staff member to a request
    async assignStaff(id, staffId) {
      const { data } = await api.patch(`/requests/${id}/assign`, { staffId })
      this.currentRequest = data
      return data
    },

    // Dashboard stats for charts
    async fetchStats() {
      try {
        const { data } = await api.get('/dashboard/stats')
        this.stats = data
      } catch (e) {
        this.error = e.response?.data?.error || 'Failed to load stats'
      }
    },

    // Fetch all buildings (for dropdowns)
    async fetchBuildings() {
      const { data } = await api.get('/buildings')
      return data
    },

    // Fetch staff users (for assign dropdown)
    async fetchStaffUsers() {
      const { data } = await api.get('/users/staff')
      return data
    }
  }
})
