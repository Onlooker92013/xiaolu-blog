<template>
  <div class="comment-section" ref="sectionRef">
    <div class="comment-header">
      <h3>💬 评论 ({{ total }})</h3>
      <el-button type="primary" round size="small" @click="toggleForm">
        {{ formOpen ? '收起' : '写评论' }}
      </el-button>
    </div>

    <!-- 发表评论 - 悬浮卡片 -->
    <transition name="slide-form">
      <div v-if="formOpen" class="comment-form">
        <div class="form-row">
          <el-input v-model="guestName" placeholder="你的昵称" size="default" maxlength="20">
            <template #prefix><el-icon><User /></el-icon></template>
          </el-input>
        </div>
        <div class="form-row">
          <el-input v-model="newComment" type="textarea" :rows="3" placeholder="写下你的想法..." maxlength="500" show-word-limit resize="none" ref="inputRef" />
        </div>
        <div class="form-actions">
          <span class="form-hint">💡 文明发言，友善交流</span>
          <el-button type="primary" @click="postComment" :loading="posting" round>
            <el-icon><Promotion /></el-icon> 发布
          </el-button>
        </div>
      </div>
    </transition>

    <!-- 评论列表 -->
    <div class="comment-list" v-if="comments.length">
      <transition-group name="comment-fade">
        <div v-for="c in comments" :key="c.id" class="comment-item">
          <div class="comment-avatar">{{ (c.guestName || '匿名')[0] }}</div>
          <div class="comment-body">
            <div class="comment-author">{{ c.guestName || '匿名用户' }}
              <span class="comment-time">{{ formatTime(c.createdAt) }}</span>
            </div>
            <div class="comment-content">{{ c.content }}</div>
            <div class="comment-actions">
              <el-button text size="small" @click="showReply(c)">💬 回复</el-button>
            </div>
            <!-- 回复框 -->
            <transition name="slide-form">
              <div v-if="replyTo === c.id" class="reply-form">
                <el-input v-model="replyName" placeholder="你的昵称" size="small" maxlength="20" style="margin-bottom:6px">
                  <template #prefix><el-icon><User /></el-icon></template>
                </el-input>
                <el-input v-model="replyContent" type="textarea" :rows="2" placeholder="回复 {{ c.guestName || '匿名' }}..." size="small" resize="none" />
                <div class="reply-actions">
                  <el-button size="small" type="primary" @click="postReply(c.id)" :loading="replying" round>回复</el-button>
                  <el-button size="small" @click="replyTo = null" round>取消</el-button>
                </div>
              </div>
            </transition>
            <!-- 子回复 -->
            <div v-for="r in getReplies(c.id)" :key="r.id" class="comment-item reply-item">
              <div class="comment-avatar small">{{ (r.guestName || '匿')[0] }}</div>
              <div class="comment-body">
                <div class="comment-author">{{ r.guestName || '匿名用户' }}
                  <span class="comment-time">{{ formatTime(r.createdAt) }}</span>
                </div>
                <div class="comment-content">{{ r.content }}</div>
              </div>
            </div>
          </div>
        </div>
      </transition-group>
    </div>
    <el-empty v-else description="暂无评论，来写第一条吧" :image-size="60" />

    <div v-if="total > comments.length" style="text-align:center;margin-top:16px">
      <el-button @click="loadMore" :loading="loading" round>加载更多评论</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import api from '@/api'
import { ElMessage } from 'element-plus'
import { User, Promotion } from '@element-plus/icons-vue'

const props = defineProps<{ articleId: number }>()

const sectionRef = ref()
const inputRef = ref()
const comments = ref<any[]>([])
const replies = ref<Record<number, any[]>>({})
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const posting = ref(false)
const replying = ref(false)
const formOpen = ref(false)

const guestName = ref(localStorage.getItem('commentName') || '')
const newComment = ref('')
const replyTo = ref<number | null>(null)
const replyName = ref(localStorage.getItem('commentName') || '')
const replyContent = ref('')

const formatTime = (t: string) => {
  if (!t) return ''
  return t.substring(0, 16).replace('T', ' ')
}

const toggleForm = () => {
  formOpen.value = !formOpen.value
  if (formOpen.value) {
    nextTick(() => {
      sectionRef.value?.scrollIntoView({ behavior: 'smooth', block: 'start' })
      setTimeout(() => inputRef.value?.focus?.(), 300)
    })
  }
}

const fetchComments = async () => {
  loading.value = true
  const res = await api.get(`/comments/article/${props.articleId}?page=${page.value}&pageSize=${pageSize.value}`) as any
  comments.value = page.value === 1 ? res.data.list : [...comments.value, ...res.data.list]
  total.value = res.data.total
  loading.value = false
  for (const c of comments.value) {
    if (!replies.value[c.id]) {
      const r = await api.get(`/comments/replies/${c.id}`) as any
      replies.value[c.id] = r.data || []
    }
  }
}

const getReplies = (parentId: number) => replies.value[parentId] || []

const loadMore = () => { page.value++; fetchComments() }

const showReply = (c: any) => {
  replyTo.value = c.id
  replyName.value = localStorage.getItem('commentName') || ''
  replyContent.value = ''
  nextTick(() => { /* focus handled by browser */ })
}

const postComment = async () => {
  if (!newComment.value.trim()) return
  posting.value = true
  try {
    const name = guestName.value.trim() || '匿名'
    await api.post('/comments', { articleId: props.articleId, content: newComment.value, guestName: name })
    localStorage.setItem('commentName', name)
    newComment.value = ''
    page.value = 1
    await fetchComments()
    ElMessage.success('评论发布成功 ✨')
  } finally { posting.value = false }
}

const postReply = async (parentId: number) => {
  if (!replyContent.value.trim()) return
  replying.value = true
  try {
    const name = replyName.value.trim() || '匿名'
    await api.post('/comments', { articleId: props.articleId, content: replyContent.value, parentId, guestName: name })
    localStorage.setItem('commentName', name)
    replyTo.value = null
    replyContent.value = ''
    const r = await api.get(`/comments/replies/${parentId}`) as any
    replies.value[parentId] = r.data || []
    total.value++
    ElMessage.success('回复成功')
  } finally { replying.value = false }
}

onMounted(fetchComments)
</script>

<style scoped>
.comment-section { margin-top: 36px; padding-top: 24px; border-top: 1px solid var(--border-color); scroll-margin-top: 80px; }
.comment-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.comment-header h3 { font-size: 1.1rem; font-weight: 700; margin: 0; }

.comment-form {
  margin-bottom: 24px; padding: 20px;
  background: var(--bg-card); border: 1px solid var(--border-color);
  border-radius: 16px; box-shadow: 0 4px 24px rgba(0,0,0,0.06);
  transition: box-shadow 0.3s;
}
.comment-form:focus-within { box-shadow: 0 4px 24px rgba(66,90,239,0.12); border-color: var(--accent); }
.form-row { margin-bottom: 10px; }
.form-actions { display: flex; justify-content: space-between; align-items: center; margin-top: 4px; }
.form-hint { font-size: 0.8rem; color: var(--text-muted); }

.comment-list { display: flex; flex-direction: column; }
.comment-item { display: flex; gap: 12px; padding: 14px 0; border-bottom: 1px solid var(--border-color); }
.comment-item:last-child { border-bottom: none; }
.reply-item { border-bottom: none; padding: 8px 0 8px 12px; opacity: 0.92; border-left: 2px solid var(--border-color); margin-top: 6px; }
.comment-avatar {
  width: 38px; height: 38px; border-radius: 50%; background: var(--accent); color: #fff;
  display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 0.95rem; flex-shrink: 0;
}
.comment-avatar.small { width: 28px; height: 28px; font-size: 0.75rem; }
.comment-body { flex: 1; min-width: 0; }
.comment-author { font-weight: 600; font-size: 0.9rem; color: var(--text-primary); }
.comment-time { font-weight: 400; font-size: 0.75rem; color: var(--text-muted); margin-left: 8px; }
.comment-content { margin-top: 4px; font-size: 0.93rem; line-height: 1.65; color: var(--text-secondary); word-break: break-word; }
.comment-actions { margin-top: 4px; }

.reply-form { margin-top: 10px; padding: 12px; background: var(--bg-primary); border-radius: 10px; border: 1px solid var(--border-color); }
.reply-actions { margin-top: 6px; display: flex; gap: 8px; }

/* Transitions */
.slide-form-enter-active { transition: all 0.35s ease; }
.slide-form-leave-active { transition: all 0.2s ease; }
.slide-form-enter-from { opacity: 0; transform: translateY(-12px); max-height: 0; }
.slide-form-leave-to { opacity: 0; transform: translateY(-8px); max-height: 0; }
.comment-fade-enter-active { transition: all 0.4s ease; }
.comment-fade-enter-from { opacity: 0; transform: translateX(-16px); }

/* Dark mode */
.comment-form :deep(.el-input__wrapper),
.comment-form :deep(.el-textarea__inner),
.reply-form :deep(.el-input__wrapper),
.reply-form :deep(.el-textarea__inner) {
  background-color: var(--bg-card) !important;
  box-shadow: 0 0 0 1px var(--border-color) inset !important;
}
.comment-form :deep(.el-input__inner),
.comment-form :deep(.el-textarea__inner),
.reply-form :deep(.el-input__inner),
.reply-form :deep(.el-textarea__inner) {
  color: var(--text-primary) !important;
}
.comment-form :deep(.el-input__wrapper:hover),
.comment-form :deep(.el-textarea__inner:hover),
.reply-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--accent) inset !important;
}
.comment-form :deep(.el-input__inner::placeholder),
.comment-form :deep(.el-textarea__inner::placeholder),
.reply-form :deep(.el-input__inner::placeholder),
.reply-form :deep(.el-textarea__inner::placeholder) {
  color: var(--text-muted) !important;
}
</style>
