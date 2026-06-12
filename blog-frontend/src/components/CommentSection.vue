<template>
  <div class="comment-section">
    <h3>💬 评论 ({{ total }})</h3>

    <!-- 发表评论 -->
    <div class="comment-form">
      <el-input v-model="guestName" placeholder="你的昵称" size="default" style="margin-bottom:8px" maxlength="20" />
      <el-input v-model="newComment" type="textarea" :rows="3" placeholder="写下你的想法..." maxlength="500" show-word-limit />
      <el-button type="primary" @click="postComment" :loading="posting" style="margin-top:8px">发表评论</el-button>
    </div>

    <!-- 评论列表 -->
    <div class="comment-list" v-if="comments.length">
      <div v-for="c in comments" :key="c.id" class="comment-item">
        <div class="comment-avatar">{{ (c.guestName || '匿名')[0] }}</div>
        <div class="comment-body">
          <div class="comment-author">{{ c.guestName || '匿名用户' }}
            <span class="comment-time">{{ formatTime(c.createdAt) }}</span>
          </div>
          <div class="comment-content">{{ c.content }}</div>
          <div class="comment-actions">
            <el-button text size="small" @click="showReply(c)">回复</el-button>
          </div>
          <!-- 回复框 -->
          <div v-if="replyTo === c.id" class="reply-form">
            <el-input v-model="replyName" placeholder="你的昵称" size="small" style="margin-bottom:6px" maxlength="20" />
            <el-input v-model="replyContent" type="textarea" :rows="2" placeholder="写下回复..." size="small" />
            <div style="margin-top:4px">
              <el-button size="small" type="primary" @click="postReply(c.id)" :loading="replying">回复</el-button>
              <el-button size="small" @click="replyTo = null">取消</el-button>
            </div>
          </div>
          <!-- 子回复 -->
          <div v-for="r in getReplies(c.id)" :key="r.id" class="comment-item reply-item" style="margin-left:16px">
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
    </div>
    <el-empty v-else description="暂无评论，来写第一条吧" :image-size="60" />

    <div v-if="total > pageSize" style="text-align:center;margin-top:16px">
      <el-button @click="loadMore" :loading="loading">加载更多</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '@/api'
import { ElMessage } from 'element-plus'

const props = defineProps<{ articleId: number }>()

const comments = ref<any[]>([])
const replies = ref<Record<number, any[]>>({})
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const posting = ref(false)
const replying = ref(false)

const guestName = ref(localStorage.getItem('commentName') || '')
const newComment = ref('')
const replyTo = ref<number | null>(null)
const replyName = ref(localStorage.getItem('commentName') || '')
const replyContent = ref('')

const formatTime = (t: string) => {
  if (!t) return ''
  return t.substring(0, 16).replace('T', ' ')
}

const fetchComments = async () => {
  loading.value = true
  const res = await api.get(`/comments/article/${props.articleId}?page=${page.value}&pageSize=${pageSize.value}`) as any
  comments.value = res.data.list
  total.value = res.data.total
  loading.value = false
  // Fetch replies
  for (const c of comments.value) {
    if (!replies.value[c.id]) {
      const r = await api.get(`/comments/replies/${c.id}`) as any
      replies.value[c.id] = r.data || []
    }
  }
}

const getReplies = (parentId: number) => replies.value[parentId] || []

const loadMore = () => {
  page.value++
  fetchComments()
}

const showReply = (c: any) => {
  replyTo.value = c.id
  replyName.value = localStorage.getItem('commentName') || ''
  replyContent.value = ''
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
    ElMessage.success('评论成功')
  } finally { posting.value = false }
}

const postReply = async (parentId: number) => {
  if (!replyContent.value.trim()) return
  replying.value = true
  try {
    const name = replyName.value.trim() || '匿名'
    await api.post('/comments', {
      articleId: props.articleId,
      content: replyContent.value,
      parentId,
      guestName: name
    })
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
.comment-section { margin-top: 36px; padding-top: 24px; border-top: 1px solid var(--border-color); }
.comment-section h3 { font-size: 1.1rem; font-weight: 700; margin-bottom: 16px; }
.comment-form { margin-bottom: 24px; padding: 16px; background: var(--bg-primary); border-radius: 12px; }
.comment-list { display: flex; flex-direction: column; gap: 0; }
.comment-item { display: flex; gap: 12px; padding: 12px 0; border-bottom: 1px solid var(--border-color); }
.comment-item:last-child { border-bottom: none; }
.reply-item { border-bottom: none; padding: 8px 0; opacity: 0.9; }
.comment-avatar {
  width: 36px; height: 36px; border-radius: 50%; background: var(--accent); color: #fff;
  display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 0.9rem; flex-shrink: 0;
}
.comment-avatar.small { width: 28px; height: 28px; font-size: 0.75rem; }
.comment-body { flex: 1; min-width: 0; }
.comment-author { font-weight: 600; font-size: 0.9rem; color: var(--text-primary); }
.comment-time { font-weight: 400; font-size: 0.78rem; color: var(--text-muted); margin-left: 8px; }
.comment-content { margin-top: 4px; font-size: 0.93rem; line-height: 1.6; color: var(--text-secondary); word-break: break-word; }
.comment-actions { margin-top: 4px; }
.reply-form { margin-top: 8px; padding: 8px; background: var(--bg-primary); border-radius: 8px; }
</style>
