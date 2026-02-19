<template>
  <div>
    <div class="page-header">
      <div>
        <h1 class="page-title">Help Center</h1>
        <p class="page-subtitle">Everything you need to get the most out of FixFlow</p>
      </div>
    </div>

    <!-- Quick nav pills -->
    <div class="quick-nav">
      <button
        v-for="section in sections"
        :key="section.id"
        class="qn-pill"
        :class="{ active: activeSection === section.id }"
        @click="scrollTo(section.id)"
      >
        {{ section.label }}
      </button>
    </div>

    <!-- Request Lifecycle -->
    <div id="lifecycle" class="card help-card" ref="lifecycleRef">
      <div class="card-heading">
        <span class="heading-bar"></span>
        <h2 class="card-title">Request Lifecycle</h2>
      </div>
      <p class="help-intro">Every maintenance request moves through a defined workflow — no step can be skipped.</p>

      <div class="lifecycle-flow">
        <div class="lf-step">
          <div class="lf-dot dot-open"></div>
          <div class="lf-label">OPEN</div>
          <div class="lf-desc">Tenant submits request</div>
        </div>
        <div class="lf-arrow">→</div>
        <div class="lf-step">
          <div class="lf-dot dot-assigned"></div>
          <div class="lf-label">ASSIGNED</div>
          <div class="lf-desc">Admin assigns to staff</div>
        </div>
        <div class="lf-arrow">→</div>
        <div class="lf-step">
          <div class="lf-dot dot-inprogress"></div>
          <div class="lf-label">IN PROGRESS</div>
          <div class="lf-desc">Staff starts work</div>
        </div>
        <div class="lf-arrow">→</div>
        <div class="lf-step">
          <div class="lf-dot dot-resolved"></div>
          <div class="lf-label">RESOLVED</div>
          <div class="lf-desc">Work completed</div>
        </div>
        <div class="lf-arrow">→</div>
        <div class="lf-step">
          <div class="lf-dot dot-closed"></div>
          <div class="lf-label">CLOSED</div>
          <div class="lf-desc">Tenant confirms</div>
        </div>
      </div>
    </div>

    <!-- Roles -->
    <div id="roles" class="card help-card" ref="rolesRef">
      <div class="card-heading">
        <span class="heading-bar"></span>
        <h2 class="card-title">Roles & Permissions</h2>
      </div>
      <div class="role-cards">
        <div class="role-card">
          <div class="role-icon ri-tenant">T</div>
          <div class="role-name">Tenant</div>
          <ul class="role-perms">
            <li>Submit maintenance requests</li>
            <li>Track own request status in real time</li>
            <li>Add comments to own requests</li>
            <li>View request history</li>
          </ul>
        </div>
        <div class="role-card">
          <div class="role-icon ri-staff">S</div>
          <div class="role-name">Staff</div>
          <ul class="role-perms">
            <li>View all open maintenance requests</li>
            <li>Advance status (IN_PROGRESS → RESOLVED)</li>
            <li>Add internal comments and notes</li>
            <li>Access dashboard analytics</li>
          </ul>
        </div>
        <div class="role-card">
          <div class="role-icon ri-admin">A</div>
          <div class="role-name">Admin</div>
          <ul class="role-perms">
            <li>All staff permissions</li>
            <li>Assign requests to staff members</li>
            <li>Full analytics and reporting</li>
            <li>Manage team workload (Team view)</li>
          </ul>
        </div>
      </div>
    </div>

    <!-- FAQ -->
    <div id="faq" class="card help-card" ref="faqRef">
      <div class="card-heading">
        <span class="heading-bar"></span>
        <h2 class="card-title">Frequently Asked Questions</h2>
      </div>
      <div class="faq-list">
        <div
          v-for="(item, i) in faqs"
          :key="i"
          class="faq-item"
          :class="{ open: openFaq === i }"
        >
          <button class="faq-q" @click="openFaq = openFaq === i ? -1 : i">
            <span>{{ item.q }}</span>
            <svg class="faq-chevron" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round">
              <polyline points="6 9 12 15 18 9"/>
            </svg>
          </button>
          <div class="faq-a" v-show="openFaq === i">{{ item.a }}</div>
        </div>
      </div>
    </div>

    <!-- Keyboard Shortcuts -->
    <div id="shortcuts" class="card help-card" ref="shortcutsRef">
      <div class="card-heading">
        <span class="heading-bar"></span>
        <h2 class="card-title">Tips & Shortcuts</h2>
      </div>
      <div class="shortcuts-grid">
        <div class="shortcut-item" v-for="tip in tips" :key="tip.label">
          <div class="shortcut-label">{{ tip.label }}</div>
          <div class="shortcut-desc">{{ tip.desc }}</div>
        </div>
      </div>
    </div>

    <!-- Contact / Support -->
    <div id="contact" class="card help-card" ref="contactRef">
      <div class="card-heading">
        <span class="heading-bar"></span>
        <h2 class="card-title">Still Need Help?</h2>
      </div>
      <p class="help-intro">Reach out to your building administrator or the FixFlow support team.</p>
      <div class="contact-grid">
        <div class="contact-item">
          <div class="contact-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
              <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
              <polyline points="22,6 12,13 2,6"/>
            </svg>
          </div>
          <div>
            <div class="contact-title">Email Support</div>
            <div class="contact-sub">support@fixflow.com</div>
          </div>
        </div>
        <div class="contact-item">
          <div class="contact-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
              <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07A19.5 19.5 0 0 1 4.69 12 19.79 19.79 0 0 1 1.61 3.36 2 2 0 0 1 3.59 1h3a2 2 0 0 1 2 1.72c.127.96.361 1.903.7 2.81a2 2 0 0 1-.45 2.11L7.91 8.96a16 16 0 0 0 6.13 6.13l.86-.87a2 2 0 0 1 2.11-.45c.907.339 1.85.573 2.81.7A2 2 0 0 1 22 16.92z"/>
            </svg>
          </div>
          <div>
            <div class="contact-title">Phone</div>
            <div class="contact-sub">Mon–Fri, 9am–5pm</div>
          </div>
        </div>
        <div class="contact-item">
          <div class="contact-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
              <circle cx="12" cy="12" r="10"/>
              <path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"/><line x1="12" y1="17" x2="12.01" y2="17"/>
            </svg>
          </div>
          <div>
            <div class="contact-title">Documentation</div>
            <div class="contact-sub">docs.fixflow.com</div>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref } from 'vue'

const activeSection = ref('lifecycle')
const openFaq = ref(-1)

const sections = [
  { id: 'lifecycle', label: 'Request Lifecycle' },
  { id: 'roles',     label: 'Roles & Permissions' },
  { id: 'faq',       label: 'FAQ' },
  { id: 'shortcuts', label: 'Tips' },
  { id: 'contact',   label: 'Contact' },
]

const lifecycleRef  = ref(null)
const rolesRef      = ref(null)
const faqRef        = ref(null)
const shortcutsRef  = ref(null)
const contactRef    = ref(null)

const refMap = { lifecycle: lifecycleRef, roles: rolesRef, faq: faqRef, shortcuts: shortcutsRef, contact: contactRef }

function scrollTo(id) {
  activeSection.value = id
  refMap[id]?.value?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

const faqs = [
  {
    q: 'How do I submit a maintenance request?',
    a: 'Go to "My Requests" in the sidebar, then click "+ New Request". Fill in the title, category, priority, description, and your floor/room number, then click Submit. Your request will immediately appear as OPEN and be visible to building staff.'
  },
  {
    q: 'Can I edit a request after submitting it?',
    a: 'Submitted requests cannot be edited to preserve the audit trail. If you need to add more detail, open the request and post a comment. Staff can see all comments in real time.'
  },
  {
    q: 'Why hasn\'t my request been assigned yet?',
    a: 'OPEN requests are visible to administrators who assign them based on staff availability and priority. High and Critical priority requests are generally actioned first. You can add a comment to flag urgency.'
  },
  {
    q: 'How do I know when my request is resolved?',
    a: 'The status badge on your "My Requests" page updates in real time. When staff mark it RESOLVED, the badge turns green. You can also view the full comment thread on the request detail page.'
  },
  {
    q: 'As staff, how do I advance a request status?',
    a: 'Open the request detail page. You will see a status dropdown or action buttons depending on the current state. ASSIGNED requests can be moved to IN_PROGRESS, and IN_PROGRESS can be moved to RESOLVED. Each transition is validated server-side.'
  },
  {
    q: 'As admin, how do I assign a request to staff?',
    a: 'Open any OPEN request. In the detail view you will see an "Assign To" dropdown listing all staff members. Select a staff member and click Assign. The request moves to ASSIGNED status and the staff member is notified.'
  },
  {
    q: 'What do the priority levels mean?',
    a: 'LOW — Non-urgent, cosmetic or minor issues. MEDIUM — Affects comfort but not safety. HIGH — Significantly impacts daily operations. CRITICAL — Safety hazard or building-wide impact — addressed immediately.'
  },
  {
    q: 'Can I export my data?',
    a: 'Yes. On the Dashboard page, click "Export Data" to download a CSV file of all requests including status, priority, assignment, and timestamps. The Analytics page also has a dedicated export button.'
  },
]

const tips = [
  { label: 'Use Priority Wisely',    desc: 'Only mark requests Critical for genuine safety hazards. Overuse reduces the signal for staff triage.' },
  { label: 'Detailed Descriptions',  desc: 'Include the exact location, when the issue started, and any relevant photos in your description to speed up resolution.' },
  { label: 'Check the Calendar',     desc: 'The Calendar view shows all requests by date — useful for spotting recurring issues in the same area.' },
  { label: 'Comment for Updates',    desc: 'Instead of creating a new request, add a comment to an existing one to provide additional information to staff.' },
  { label: 'Team View for Admins',   desc: 'Use the Team page to monitor staff workload before assigning new requests to avoid overloading individual members.' },
  { label: 'Analytics for Trends',   desc: 'The Analytics page shows category and priority breakdowns — identify systemic issues before they escalate.' },
]
</script>

<style scoped>
/* Quick nav */
.quick-nav {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}
.qn-pill {
  padding: 7px 16px; border-radius: 20px;
  border: 1px solid var(--border); background: white;
  font-size: 13px; font-weight: 500; color: #6B7280;
  cursor: pointer; transition: all 0.18s;
}
.qn-pill:hover { border-color: var(--primary); color: var(--primary); background: #F0FDF4; }
.qn-pill.active { background: var(--primary); color: white; border-color: var(--primary); font-weight: 700; }

/* Help cards */
.help-card {
  display: flex;
  flex-direction: column;
  gap: 18px;
  margin-bottom: 20px;
  scroll-margin-top: 80px;
}
.help-intro { font-size: 14px; color: #6B7280; line-height: 1.6; }

/* Card heading */
.card-heading { display: flex; align-items: center; gap: 10px; }
.heading-bar  { display: inline-block; width: 4px; height: 18px; background: var(--gradient); border-radius: 2px; flex-shrink: 0; }
.card-title   { font-size: 15px; font-weight: 700; color: #111827; }

/* Lifecycle flow */
.lifecycle-flow {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 6px;
  padding: 20px;
  background: #F9FAFB;
  border-radius: 12px;
}
.lf-step { display: flex; flex-direction: column; align-items: center; gap: 6px; flex: 1; min-width: 80px; }
.lf-dot {
  width: 16px; height: 16px; border-radius: 50%;
}
.dot-open       { background: #9CA3AF; }
.dot-assigned   { background: #F59E0B; }
.dot-inprogress { background: #F97316; }
.dot-resolved   { background: #22C55E; }
.dot-closed     { background: #166534; }

.lf-label { font-size: 10px; font-weight: 800; text-transform: uppercase; letter-spacing: 0.06em; color: #374151; text-align: center; }
.lf-desc  { font-size: 11px; color: #9CA3AF; text-align: center; }
.lf-arrow { font-size: 18px; color: #D1D5DB; flex-shrink: 0; }

/* Roles */
.role-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
}
.role-card {
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 18px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.role-icon {
  width: 42px; height: 42px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  font-size: 16px; font-weight: 900; color: white;
}
.ri-tenant { background: linear-gradient(135deg, #2563EB, #1D4ED8); }
.ri-staff  { background: linear-gradient(135deg, #D97706, #B45309); }
.ri-admin  { background: linear-gradient(135deg, #166534, #14532D); }

.role-name { font-size: 14px; font-weight: 700; color: #111827; }
.role-perms {
  list-style: none; padding: 0; margin: 0;
  display: flex; flex-direction: column; gap: 6px;
}
.role-perms li {
  font-size: 12px; color: #6B7280;
  padding-left: 16px; position: relative;
}
.role-perms li::before {
  content: '✓';
  position: absolute; left: 0;
  color: #166534; font-weight: 700; font-size: 11px;
}

/* FAQ */
.faq-list { display: flex; flex-direction: column; }
.faq-item { border-bottom: 1px solid var(--border); }
.faq-item:last-child { border-bottom: none; }

.faq-q {
  width: 100%; display: flex; align-items: center; justify-content: space-between; gap: 12px;
  padding: 15px 0;
  background: none; border: none; cursor: pointer; text-align: left;
  font-size: 14px; font-weight: 600; color: #111827;
  transition: color 0.18s;
}
.faq-q:hover { color: var(--primary); }
.faq-chevron { transition: transform 0.25s; flex-shrink: 0; color: #9CA3AF; }
.faq-item.open .faq-chevron { transform: rotate(180deg); }
.faq-item.open .faq-q { color: var(--primary); }

.faq-a {
  font-size: 13px; color: #6B7280; line-height: 1.7;
  padding-bottom: 16px;
}

/* Tips grid */
.shortcuts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(min(260px, 100%), 1fr));
  gap: 14px;
}
.shortcut-item {
  background: #F9FAFB;
  border-radius: 10px;
  padding: 14px 16px;
  border: 1px solid var(--border);
}
.shortcut-label { font-size: 13px; font-weight: 700; color: #111827; margin-bottom: 4px; }
.shortcut-desc  { font-size: 12px; color: #6B7280; line-height: 1.5; }

/* Contact */
.contact-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(min(220px, 100%), 1fr));
  gap: 14px;
}
.contact-item {
  display: flex; align-items: center; gap: 14px;
  padding: 16px; border: 1px solid var(--border); border-radius: 12px;
  background: #F9FAFB;
}
.contact-icon {
  width: 40px; height: 40px; border-radius: 10px;
  background: #F0FDF4; color: var(--primary);
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.contact-title { font-size: 13px; font-weight: 700; color: #111827; }
.contact-sub   { font-size: 12px; color: #9CA3AF; margin-top: 2px; }

/* Responsive */
@media (max-width: 900px) {
  .role-cards { grid-template-columns: 1fr; }
}
@media (max-width: 600px) {
  .lifecycle-flow { flex-direction: column; }
  .lf-arrow { transform: rotate(90deg); }
}
</style>
