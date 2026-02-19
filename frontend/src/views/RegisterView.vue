<template>
  <div class="auth-page">
    <!-- Left branding panel -->
    <div class="auth-left">
      <div class="auth-branding">
        <div class="brand-logo">
          <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z"/>
          </svg>
        </div>
        <div class="brand-name">FixFlow</div>
        <div class="brand-desc">Join thousands of building managers and residents using FixFlow.</div>
        <div class="brand-features">
          <div class="brand-feature">
            <div class="feature-dot" style="background: rgba(108,99,255,0.3)">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
            </div>
            Submit requests in under 60 seconds
          </div>
          <div class="brand-feature">
            <div class="feature-dot" style="background: rgba(16,185,129,0.3)">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2"><path d="M18 20V10"/><path d="M12 20V4"/><path d="M6 20v-6"/></svg>
            </div>
            Real-time status tracking
          </div>
          <div class="brand-feature">
            <div class="feature-dot" style="background: rgba(245,158,11,0.3)">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
            </div>
            Direct communication with staff
          </div>
        </div>
      </div>
    </div>

    <!-- Right register form -->
    <div class="auth-right">
      <div class="auth-card">
        <div class="auth-header">
          <h2 class="auth-title">Create account</h2>
          <p class="auth-subtitle">Get started with FixFlow for free</p>
        </div>

        <div v-if="error" class="alert alert-error">{{ error }}</div>

        <form @submit.prevent="handleRegister">
          <div class="form-group">
            <label>Full Name *</label>
            <input
              v-model="form.fullName"
              class="form-control"
              placeholder="John Smith"
              required
            />
          </div>

          <div class="form-group">
            <label>Email Address *</label>
            <input
              v-model="form.email"
              type="email"
              class="form-control"
              placeholder="you@example.com"
              required
            />
          </div>

          <div class="form-group">
            <label>Password *</label>
            <input
              v-model="form.password"
              type="password"
              class="form-control"
              placeholder="At least 6 characters"
              minlength="6"
              required
            />
          </div>

          <div class="form-group">
            <label>Role *</label>
            <select v-model="form.role" class="form-control" required>
              <option value="">Select role</option>
              <option value="TENANT">Tenant (report issues)</option>
              <option value="STAFF">Staff (fix issues)</option>
            </select>
          </div>

          <!-- Building selection only shown for tenants -->
          <div v-if="form.role === 'TENANT'" class="form-group">
            <label>Building *</label>
            <select v-model="form.buildingId" class="form-control" required>
              <option value="">Select your building</option>
              <option v-for="b in buildings" :key="b.id" :value="b.id">{{ b.name }}</option>
            </select>
          </div>

          <button type="submit" class="btn btn-primary btn-full" :disabled="loading">
            {{ loading ? 'Creating account...' : 'Create Account' }}
          </button>
        </form>

        <div class="auth-footer">
          Already have an account?
          <RouterLink to="/login">Sign in</RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/authStore'
import api from '../services/api'

const authStore = useAuthStore()
const router = useRouter()

const buildings = ref([])
const loading = ref(false)
const error = ref('')

const form = ref({
  fullName: '',
  email: '',
  password: '',
  role: '',
  buildingId: ''
})

onMounted(async () => {
  try {
    const { data } = await api.get('/buildings')
    buildings.value = data
  } catch (e) {
    // Buildings load failure is non-critical at this point
    console.warn('Failed to load buildings for registration')
  }
})

async function handleRegister() {
  if (form.value.password.length < 6) {
    error.value = 'Password must be at least 6 characters'
    return
  }

  loading.value = true
  error.value = ''

  try {
    const payload = {
      fullName: form.value.fullName,
      email: form.value.email,
      password: form.value.password,
      role: form.value.role,
      buildingId: form.value.buildingId || null
    }

    await authStore.register(payload)

    // Auto-redirect based on role after registration
    router.push(authStore.isTenant ? '/my-requests' : '/dashboard')
  } catch (e) {
    error.value = e.response?.data?.error || 'Registration failed. Please try again.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  background: linear-gradient(135deg, #1E1B4B 0%, #312E81 50%, #4338CA 100%);
}

.auth-left {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
  position: relative;
}

.auth-left::before {
  content: '';
  position: absolute;
  top: -80px;
  left: -80px;
  width: 300px;
  height: 300px;
  border-radius: 50%;
  background: rgba(108, 99, 255, 0.2);
}

.auth-left::after {
  content: '';
  position: absolute;
  bottom: -60px;
  right: 60px;
  width: 200px;
  height: 200px;
  border-radius: 50%;
  background: rgba(255, 107, 107, 0.15);
}

.auth-branding {
  position: relative;
  z-index: 1;
  text-align: center;
  color: white;
}

.brand-logo {
  width: 72px;
  height: 72px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
}

.brand-name {
  font-size: 36px;
  font-weight: 900;
  letter-spacing: -0.03em;
  margin-bottom: 12px;
}

.brand-desc {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.65);
  line-height: 1.6;
  max-width: 280px;
  margin: 0 auto;
}

.brand-features {
  margin-top: 36px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.brand-feature {
  display: flex;
  align-items: center;
  gap: 12px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 12px 16px;
  text-align: left;
  color: rgba(255, 255, 255, 0.85);
  font-size: 14px;
  font-weight: 500;
}

.feature-dot {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.auth-right {
  width: 480px;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px 40px;
  overflow-y: auto;
}

.auth-card {
  width: 100%;
  max-width: 380px;
}

.auth-header {
  margin-bottom: 28px;
}

.auth-title {
  font-size: 26px;
  font-weight: 800;
  color: var(--text);
  letter-spacing: -0.02em;
  margin-bottom: 6px;
}

.auth-subtitle {
  font-size: 14px;
  color: var(--text-light);
}

.btn-full {
  width: 100%;
  justify-content: center;
  padding: 13px;
  font-size: 15px;
  margin-top: 8px;
}

.auth-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: var(--text-light);
}

.auth-footer a {
  color: var(--primary);
  font-weight: 600;
  text-decoration: none;
}

@media (max-width: 800px) {
  .auth-page { flex-direction: column; }
  .auth-left { display: none; }
  .auth-right { width: 100%; min-height: 100vh; }
}
</style>
