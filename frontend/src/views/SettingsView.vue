<template>
  <div>
    <div class="page-header">
      <div>
        <h1 class="page-title">Settings</h1>
        <p class="page-subtitle">Manage your account and preferences</p>
      </div>
    </div>

    <div class="settings-grid">

      <!-- Profile Card -->
      <div class="card settings-card">
        <div class="card-heading">
          <span class="heading-bar"></span>
          <h2 class="card-title">Profile</h2>
        </div>

        <div class="profile-avatar-row">
          <div class="big-avatar">{{ initials }}</div>
          <div>
            <div class="profile-name">{{ authStore.user?.fullName }}</div>
            <div class="profile-email">{{ authStore.user?.email }}</div>
            <span class="role-chip">{{ authStore.user?.role }}</span>
          </div>
        </div>

        <div class="info-grid">
          <div class="info-item">
            <div class="info-label">Full Name</div>
            <div class="info-value">{{ authStore.user?.fullName }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">Email Address</div>
            <div class="info-value">{{ authStore.user?.email }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">Role</div>
            <div class="info-value">{{ authStore.user?.role }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">Account Status</div>
            <div class="info-value status-active">Active</div>
          </div>
        </div>
      </div>

      <!-- Change Password Card -->
      <div class="card settings-card">
        <div class="card-heading">
          <span class="heading-bar"></span>
          <h2 class="card-title">Change Password</h2>
        </div>

        <form @submit.prevent="handlePasswordChange" class="pw-form">
          <div class="field">
            <label class="field-label">Current Password</label>
            <div class="input-wrap">
              <input
                :type="showCurrent ? 'text' : 'password'"
                v-model="pwForm.current"
                class="field-input"
                placeholder="Enter current password"
                required
              />
              <button type="button" class="eye-btn" @click="showCurrent = !showCurrent">
                <svg v-if="!showCurrent" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/>
                </svg>
                <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
                  <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"/><path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19"/><line x1="1" y1="1" x2="23" y2="23"/>
                </svg>
              </button>
            </div>
          </div>

          <div class="field">
            <label class="field-label">New Password</label>
            <div class="input-wrap">
              <input
                :type="showNew ? 'text' : 'password'"
                v-model="pwForm.newPw"
                class="field-input"
                placeholder="At least 8 characters"
                minlength="8"
                required
              />
              <button type="button" class="eye-btn" @click="showNew = !showNew">
                <svg v-if="!showNew" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/>
                </svg>
                <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
                  <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"/><path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19"/><line x1="1" y1="1" x2="23" y2="23"/>
                </svg>
              </button>
            </div>
          </div>

          <div class="field">
            <label class="field-label">Confirm New Password</label>
            <div class="input-wrap">
              <input
                :type="showConfirm ? 'text' : 'password'"
                v-model="pwForm.confirm"
                class="field-input"
                placeholder="Repeat new password"
                required
              />
              <button type="button" class="eye-btn" @click="showConfirm = !showConfirm">
                <svg v-if="!showConfirm" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/>
                </svg>
                <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
                  <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"/><path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19"/><line x1="1" y1="1" x2="23" y2="23"/>
                </svg>
              </button>
            </div>
          </div>

          <!-- Strength indicator -->
          <div v-if="pwForm.newPw" class="strength-row">
            <div class="strength-bars">
              <div class="s-bar" :class="strength >= 1 ? `s-${strengthLabel}` : ''"></div>
              <div class="s-bar" :class="strength >= 2 ? `s-${strengthLabel}` : ''"></div>
              <div class="s-bar" :class="strength >= 3 ? `s-${strengthLabel}` : ''"></div>
              <div class="s-bar" :class="strength >= 4 ? `s-${strengthLabel}` : ''"></div>
            </div>
            <span class="strength-text" :class="`st-${strengthLabel}`">{{ strengthLabel }}</span>
          </div>

          <div v-if="pwError" class="alert-inline alert-error">{{ pwError }}</div>
          <div v-if="pwSuccess" class="alert-inline alert-success">{{ pwSuccess }}</div>

          <button type="submit" class="btn btn-primary btn-full" :disabled="saving">
            {{ saving ? 'Saving…' : 'Update Password' }}
          </button>
        </form>
      </div>

      <!-- Preferences Card -->
      <div class="card settings-card">
        <div class="card-heading">
          <span class="heading-bar"></span>
          <h2 class="card-title">Preferences</h2>
        </div>

        <div class="pref-list">
          <div class="pref-item">
            <div class="pref-info">
              <div class="pref-title">Email Notifications</div>
              <div class="pref-sub">Receive updates when your request status changes</div>
            </div>
            <label class="toggle">
              <input type="checkbox" v-model="prefs.emailNotif" />
              <span class="toggle-track"></span>
            </label>
          </div>
          <div class="pref-item">
            <div class="pref-info">
              <div class="pref-title">Assignment Alerts</div>
              <div class="pref-sub">Notify when a request is assigned to you</div>
            </div>
            <label class="toggle">
              <input type="checkbox" v-model="prefs.assignAlert" />
              <span class="toggle-track"></span>
            </label>
          </div>
          <div class="pref-item">
            <div class="pref-info">
              <div class="pref-title">Compact View</div>
              <div class="pref-sub">Show more items per page with reduced spacing</div>
            </div>
            <label class="toggle">
              <input type="checkbox" v-model="prefs.compact" />
              <span class="toggle-track"></span>
            </label>
          </div>
        </div>
      </div>

      <!-- Danger Zone -->
      <div class="card settings-card danger-card">
        <div class="card-heading">
          <span class="heading-bar heading-bar-red"></span>
          <h2 class="card-title card-title-red">Danger Zone</h2>
        </div>
        <p class="danger-desc">Once you sign out, you will need to log in again to access the system.</p>
        <button class="btn btn-danger" @click="handleLogout">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
            <polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/>
          </svg>
          Sign Out
        </button>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/authStore'
import api from '../services/api'

const authStore = useAuthStore()
const router    = useRouter()

const initials = computed(() => {
  const name = authStore.user?.fullName || ''
  return name.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase()
})

// ── Password form ──────────────────────────────────────────
const pwForm = reactive({ current: '', newPw: '', confirm: '' })
const showCurrent = ref(false)
const showNew     = ref(false)
const showConfirm = ref(false)
const saving      = ref(false)
const pwError     = ref('')
const pwSuccess   = ref('')

const strength = computed(() => {
  const p = pwForm.newPw
  let s = 0
  if (p.length >= 8)          s++
  if (/[A-Z]/.test(p))        s++
  if (/[0-9]/.test(p))        s++
  if (/[^A-Za-z0-9]/.test(p)) s++
  return s
})

const strengthLabel = computed(() => {
  const s = strength.value
  if (s <= 1) return 'weak'
  if (s === 2) return 'fair'
  if (s === 3) return 'good'
  return 'strong'
})

async function handlePasswordChange() {
  pwError.value   = ''
  pwSuccess.value = ''

  if (pwForm.newPw !== pwForm.confirm) {
    pwError.value = 'New passwords do not match.'
    return
  }
  if (pwForm.newPw.length < 8) {
    pwError.value = 'Password must be at least 8 characters.'
    return
  }

  saving.value = true
  try {
    await api.post('/auth/change-password', {
      currentPassword: pwForm.current,
      newPassword:     pwForm.newPw
    })
    pwSuccess.value = 'Password updated successfully.'
    pwForm.current  = ''
    pwForm.newPw    = ''
    pwForm.confirm  = ''
  } catch (e) {
    pwError.value = e.response?.data?.error || 'Failed to update password. Please check your current password.'
  } finally {
    saving.value = false
  }
}

// ── Preferences (local only — no backend endpoint) ─────────
const prefs = reactive({
  emailNotif:  true,
  assignAlert: true,
  compact:     false,
})

// ── Logout ─────────────────────────────────────────────────
function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.settings-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.settings-card {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* Card heading */
.card-heading {
  display: flex;
  align-items: center;
  gap: 10px;
}
.heading-bar {
  display: inline-block;
  width: 4px; height: 18px;
  background: var(--gradient);
  border-radius: 2px; flex-shrink: 0;
}
.heading-bar-red { background: linear-gradient(135deg, #DC2626, #991B1B); }
.card-title { font-size: 15px; font-weight: 700; color: #111827; }
.card-title-red { color: #DC2626; }

/* Profile */
.profile-avatar-row {
  display: flex;
  align-items: center;
  gap: 16px;
}
.big-avatar {
  width: 60px; height: 60px; border-radius: 16px;
  background: linear-gradient(135deg, #166534, #22C55E);
  color: white; font-size: 20px; font-weight: 800;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}
.profile-name  { font-size: 16px; font-weight: 700; color: #111827; }
.profile-email { font-size: 13px; color: #6B7280; margin: 3px 0; }
.role-chip {
  font-size: 10px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.06em;
  padding: 2px 10px; border-radius: 20px;
  background: #F0FDF4; color: #166534;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}
.info-label { font-size: 11px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.06em; color: #9CA3AF; margin-bottom: 4px; }
.info-value  { font-size: 14px; font-weight: 500; color: #111827; }
.status-active { color: #166534; font-weight: 700; }

/* Password form */
.pw-form { display: flex; flex-direction: column; gap: 14px; }

.field { display: flex; flex-direction: column; gap: 6px; }
.field-label { font-size: 12px; font-weight: 600; color: #374151; }

.input-wrap { position: relative; }
.field-input {
  width: 100%; padding: 10px 40px 10px 12px;
  border: 1px solid var(--border); border-radius: 8px;
  font-size: 14px; background: white; color: #111827;
  transition: border-color 0.18s;
  box-sizing: border-box;
}
.field-input:focus { outline: none; border-color: var(--primary); box-shadow: 0 0 0 3px rgba(22,101,52,0.1); }

.eye-btn {
  position: absolute; right: 10px; top: 50%; transform: translateY(-50%);
  background: none; border: none; cursor: pointer; color: #9CA3AF;
  display: flex; align-items: center; padding: 0;
  transition: color 0.18s;
}
.eye-btn:hover { color: #374151; }

/* Strength */
.strength-row { display: flex; align-items: center; gap: 10px; }
.strength-bars { display: flex; gap: 4px; flex: 1; }
.s-bar {
  height: 4px; flex: 1; border-radius: 2px;
  background: #E5E7EB; transition: background 0.3s;
}
.s-weak   { background: #EF4444; }
.s-fair   { background: #F59E0B; }
.s-good   { background: #3B82F6; }
.s-strong { background: #16A34A; }

.strength-text { font-size: 11px; font-weight: 700; text-transform: capitalize; min-width: 40px; text-align: right; }
.st-weak   { color: #EF4444; }
.st-fair   { color: #F59E0B; }
.st-good   { color: #3B82F6; }
.st-strong { color: #16A34A; }

/* Alerts */
.alert-inline {
  padding: 10px 14px;
  border-radius: 8px;
  font-size: 13px; font-weight: 500;
}
.alert-error   { background: #FEF2F2; color: #991B1B; border: 1px solid #FCA5A5; }
.alert-success { background: #F0FDF4; color: #166534; border: 1px solid #86EFAC; }

.btn-full { width: 100%; justify-content: center; }

/* Preferences */
.pref-list { display: flex; flex-direction: column; gap: 0; }
.pref-item {
  display: flex; align-items: center; gap: 12px;
  padding: 14px 0;
  border-bottom: 1px solid var(--border);
}
.pref-item:last-child { border-bottom: none; }
.pref-info { flex: 1; }
.pref-title { font-size: 14px; font-weight: 600; color: #111827; }
.pref-sub   { font-size: 12px; color: #6B7280; margin-top: 2px; }

/* Toggle */
.toggle { position: relative; display: inline-block; width: 44px; height: 24px; flex-shrink: 0; cursor: pointer; }
.toggle input { opacity: 0; width: 0; height: 0; }
.toggle-track {
  position: absolute; inset: 0;
  background: #D1D5DB; border-radius: 12px;
  transition: background 0.2s;
}
.toggle-track::after {
  content: ''; position: absolute;
  width: 18px; height: 18px; border-radius: 50%;
  background: white; top: 3px; left: 3px;
  transition: transform 0.2s;
  box-shadow: 0 1px 3px rgba(0,0,0,0.2);
}
.toggle input:checked ~ .toggle-track { background: var(--primary); }
.toggle input:checked ~ .toggle-track::after { transform: translateX(20px); }

/* Danger Zone */
.danger-card { border-color: #FCA5A5; }
.danger-desc { font-size: 13px; color: #6B7280; }
.btn-danger {
  display: inline-flex; align-items: center; gap: 8px;
  padding: 10px 20px; border-radius: 10px;
  background: #FEF2F2; color: #DC2626;
  border: 1px solid #FCA5A5; cursor: pointer;
  font-size: 14px; font-weight: 600; transition: all 0.18s;
}
.btn-danger:hover { background: #DC2626; color: white; border-color: #DC2626; }

@media (max-width: 900px) {
  .settings-grid { grid-template-columns: 1fr; }
  .info-grid { grid-template-columns: 1fr 1fr; }
}

@media (max-width: 480px) {
  .info-grid { grid-template-columns: 1fr; }
  .profile-avatar-row { flex-direction: column; align-items: flex-start; }
}
</style>
