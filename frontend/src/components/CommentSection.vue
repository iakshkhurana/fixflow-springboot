<template>
  <div class="comment-section">
    <h3 class="section-title">Comments ({{ comments.length }})</h3>

    <!-- Comment list -->
    <div v-if="comments.length === 0" class="empty-comments">
      No comments yet. Be the first to add an update.
    </div>

    <div class="comment-list">
      <div v-for="comment in comments" :key="comment.id" class="comment">
        <div class="comment-avatar">{{ initials(comment.userName) }}</div>
        <div class="comment-body">
          <div class="comment-header">
            <span class="comment-author">{{ comment.userName }}</span>
            <span class="comment-date">{{ formatDate(comment.createdAt) }}</span>
          </div>
          <p class="comment-text">{{ comment.text }}</p>
        </div>
      </div>
    </div>

    <!-- Add comment form -->
    <div class="add-comment">
      <textarea
        v-model="newComment"
        class="form-control"
        rows="2"
        placeholder="Add a comment or update..."
      ></textarea>
      <button
        class="btn btn-primary btn-sm"
        :disabled="!newComment.trim() || submitting"
        @click="submitComment"
      >
        {{ submitting ? 'Posting...' : 'Add Comment' }}
      </button>
    </div>

    <div v-if="error" class="alert alert-error" style="margin-top: 8px">{{ error }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../services/api'

const props = defineProps({
  requestId: { type: [String, Number], required: true }
})

const comments = ref([])
const newComment = ref('')
const submitting = ref(false)
const error = ref('')

onMounted(fetchComments)

async function fetchComments() {
  try {
    const { data } = await api.get(`/requests/${props.requestId}/comments`)
    comments.value = data
  } catch (e) {
    error.value = 'Failed to load comments'
  }
}

async function submitComment() {
  if (!newComment.value.trim()) return
  submitting.value = true
  error.value = ''
  try {
    const { data } = await api.post(`/requests/${props.requestId}/comments`, {
      text: newComment.value.trim()
    })
    comments.value.push(data)
    newComment.value = ''
  } catch (e) {
    error.value = e.response?.data?.error || 'Failed to post comment'
  } finally {
    submitting.value = false
  }
}

function initials(name) {
  return (name || '?').split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase()
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString('en-US', {
    month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'
  })
}
</script>

<style scoped>
.comment-section {
  margin-top: 8px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  color: var(--text);
}

.empty-comments {
  text-align: center;
  padding: 24px;
  color: var(--text-light);
  font-size: 13px;
  background: var(--light-bg);
  border-radius: 8px;
  margin-bottom: 16px;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 20px;
}

.comment {
  display: flex;
  gap: 12px;
}

.comment-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--accent);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  flex-shrink: 0;
}

.comment-body {
  flex: 1;
  background: var(--light-bg);
  border-radius: 10px;
  padding: 12px 14px;
}

.comment-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 6px;
}

.comment-author {
  font-size: 13px;
  font-weight: 600;
  color: var(--text);
}

.comment-date {
  font-size: 12px;
  color: var(--text-light);
}

.comment-text {
  font-size: 14px;
  color: var(--text);
  line-height: 1.5;
  white-space: pre-wrap;
}

.add-comment {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.add-comment .btn {
  align-self: flex-end;
}
</style>
