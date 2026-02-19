<template>
  <div class="auth-page">
    <!-- Sunburst rays overlay -->
    <div class="sunburst"></div>

    <!-- Centered content -->
    <div class="auth-container">
      <!-- Yellow badge -->
      <div class="auth-badge">FixFlow Login</div>

      <!-- Big heading -->
      <h1 class="auth-heading">Welcome to FixFlow!</h1>

      <!-- Form card -->
      <div class="auth-card">
        <div v-if="error" class="login-error">{{ error }}</div>

        <form @submit.prevent="handleLogin">
          <div class="field-group">
            <label class="field-label">Email Address</label>
            <input
              v-model="email"
              type="email"
              class="field-input"
              placeholder="you@example.com"
              required
              autocomplete="email"
            />
          </div>

          <div class="field-group">
            <label class="field-label">Password</label>
            <input
              v-model="password"
              type="password"
              class="field-input"
              placeholder="••••••••••"
              required
              autocomplete="current-password"
            />
          </div>

          <div class="field-row">
            <label class="remember-me">
              <input type="checkbox" v-model="rememberMe" />
              <span>Remember me</span>
            </label>
            <RouterLink to="/register" class="forgot-link">Forgot password?</RouterLink>
          </div>

          <button type="submit" class="sign-in-btn" :disabled="loading">
            {{ loading ? 'Signing in...' : 'Sign In' }}
          </button>
        </form>

        <div class="or-divider">
          <span class="or-line"></span>
          <span class="or-text">or</span>
          <span class="or-line"></span>
        </div>

        <!-- Demo credentials as secondary action -->
        <div class="demo-btn">
          <span class="demo-icon">🔑</span>
          <div class="demo-text">
            <span class="demo-label">Demo accounts</span>
            <span class="demo-creds">admin@fixflow.com / staff@fixflow.com / tenant@fixflow.com · password: <b>role + 123</b></span>
          </div>
        </div>

        <div class="auth-footer">
          Don't have an account? <RouterLink to="/register">Create one</RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/authStore'

const authStore = useAuthStore()
const router = useRouter()

const email = ref('')
const password = ref('')
const loading = ref(false)
const error = ref('')
const rememberMe = ref(false)

async function handleLogin() {
  loading.value = true
  error.value = ''

  try {
    await authStore.login(email.value, password.value)
    if (authStore.isTenant) {
      router.push('/my-requests')
    } else {
      router.push('/dashboard')
    }
  } catch (e) {
    error.value = e.response?.data?.error || 'Invalid email or password'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* ── Background ── */
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  background:
    radial-gradient(ellipse at 10% 65%, rgba(134, 239, 172, 0.45) 0%, transparent 52%),
    radial-gradient(ellipse at 88% 88%, rgba(187, 247, 208, 0.4) 0%, transparent 48%),
    radial-gradient(ellipse at 90% 8%,  rgba(220, 252, 231, 0.5) 0%, transparent 42%),
    #f8faf9;
  padding: 40px 20px;
}

/* ── Sunburst rays ── */
.sunburst {
  position: absolute;
  inset: 0;
  background-image: repeating-conic-gradient(
    from 0deg at 50% -8%,
    rgba(22, 101, 52, 0.03) 0deg 3.6deg,
    transparent 3.6deg 7.2deg
  );
  pointer-events: none;
}

/* ── Centered wrapper ── */
.auth-container {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  max-width: 520px;
}

/* ── Yellow badge ── */
.auth-badge {
  display: inline-block;
  background: #FEF08A;
  color: #713F12;
  font-size: 13px;
  font-weight: 600;
  padding: 6px 18px;
  border-radius: 999px;
  margin-bottom: 20px;
  letter-spacing: 0.01em;
}

/* ── Large heading ── */
.auth-heading {
  font-size: clamp(28px, 6vw, 48px);
  font-weight: 800;
  color: #1a1a1a;
  letter-spacing: -0.03em;
  text-align: center;
  margin-bottom: 32px;
  line-height: 1.1;
}

/* ── Card ── */
.auth-card {
  width: 100%;
  background: rgba(255, 255, 255, 0.82);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 255, 255, 0.9);
  border-radius: 24px;
  padding: clamp(20px, 5vw, 36px) clamp(20px, 5vw, 36px) 28px;
  box-shadow: 0 4px 32px rgba(22, 101, 52, 0.08);
}

/* ── Error ── */
.login-error {
  background: #FEF2F2;
  color: #991B1B;
  border: 1px solid #FCA5A5;
  border-radius: 10px;
  padding: 11px 14px;
  font-size: 14px;
  margin-bottom: 16px;
}

/* ── Fields ── */
.field-group {
  margin-bottom: 16px;
}

.field-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #1a1a1a;
  margin-bottom: 8px;
}

.field-input {
  width: 100%;
  padding: 13px 18px;
  border: 1.5px solid #e8e3f0;
  border-radius: 999px;
  font-size: 14px;
  color: #1a1a1a;
  background: #ffffff;
  outline: none;
  transition: border-color 0.2s, box-shadow 0.2s;
  box-sizing: border-box;
}

.field-input:focus {
  border-color: #c4b5fd;
  box-shadow: 0 0 0 3px rgba(196, 181, 253, 0.25);
}

.field-input::placeholder {
  color: #c4b5d8;
}

/* ── Remember me / Forgot ── */
.field-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 12px 0 20px;
  padding: 0 4px;
}

.remember-me {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #6b7280;
  cursor: pointer;
}

.remember-me input[type="checkbox"] {
  width: 16px;
  height: 16px;
  border-radius: 4px;
  accent-color: #7c3aed;
  cursor: pointer;
}

.forgot-link {
  font-size: 13px;
  color: #1a1a1a;
  font-weight: 500;
  text-decoration: none;
}

.forgot-link:hover {
  text-decoration: underline;
}

/* ── Sign in button ── */
.sign-in-btn {
  width: 100%;
  padding: 14px;
  background: #1a1a1a;
  color: #ffffff;
  border: none;
  border-radius: 999px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s, transform 0.15s;
  letter-spacing: 0.01em;
}

.sign-in-btn:hover:not(:disabled) {
  background: #2d2d2d;
  transform: translateY(-1px);
}

.sign-in-btn:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

/* ── OR divider ── */
.or-divider {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 22px 0;
}

.or-line {
  flex: 1;
  height: 1px;
  background: rgba(0, 0, 0, 0.1);
}

.or-text {
  font-size: 13px;
  color: #9ca3af;
  white-space: nowrap;
}

/* ── Demo credentials button ── */
.demo-btn {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 13px 20px;
  background: #ffffff;
  border: 1.5px solid #e8e3f0;
  border-radius: 999px;
  cursor: default;
  box-sizing: border-box;
}

.demo-icon {
  font-size: 18px;
  flex-shrink: 0;
}

.demo-text {
  display: flex;
  flex-direction: column;
  line-height: 1.3;
}

.demo-label {
  font-size: 13px;
  font-weight: 600;
  color: #1a1a1a;
}

.demo-creds {
  font-size: 11px;
  color: #9ca3af;
}

.demo-creds b {
  color: #6b7280;
}

/* ── Footer link ── */
.auth-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 13px;
  color: #9ca3af;
}

.auth-footer a {
  color: #166534;
  font-weight: 600;
  text-decoration: none;
  margin-left: 4px;
}

.auth-footer a:hover { text-decoration: underline; }

/* ── Demo Credentials ── */
.demo-creds { font-size: clamp(10px, 2.5vw, 11px); }

/* ── Responsive ── */
@media (max-width: 480px) {
  .auth-page { padding: 24px 12px; align-items: flex-start; padding-top: 40px; }
  .auth-badge { margin-bottom: 14px; }
  .auth-heading { margin-bottom: 20px; }
  .field-row { flex-direction: column; gap: 10px; align-items: flex-start; }
  .demo-btn { flex-direction: column; align-items: flex-start; gap: 8px; padding: 14px; }
}

.field-input:focus { border-color: #86EFAC; box-shadow: 0 0 0 3px rgba(22,101,52,0.1); }

.remember-me input[type="checkbox"] { accent-color: #166534; }
</style>
