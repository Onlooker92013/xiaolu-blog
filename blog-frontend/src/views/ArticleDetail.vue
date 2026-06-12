<template>
  <div class="article-page" v-if="article">
    <!-- 主内容区 -->
    <div class="article-main">
      <h1>{{ displayTitle }}</h1>
      <div class="meta">
        <span>📅 {{ article.createdAt?.substring(0, 10) }}</span>
        <span v-if="article.updatedAt && article.updatedAt !== article.createdAt">🔄 {{ article.updatedAt?.substring(0, 10) }}</span>
        <a v-if="article.categoryId" class="meta-link"
          @click.prevent="router.push(`/articles?categoryId=${article.categoryId}`)">
          📁 {{ article.categoryName }}
        </a>
        <span>👁 {{ article.viewCount }} 阅读</span>
        <span>⏱ 约 {{ readingTime }} 分钟</span>
      </div>
      <div class="meta-tags" v-if="article.tags?.length">
        🏷
        <a v-for="(tag, i) in article.tags" :key="i"
          class="tag-link" @click.prevent="router.push(`/articles?tag=${encodeURIComponent(tag)}`)">
          {{ tag }}
        </a>
      </div>

      <div class="article-content" v-html="renderedContent"></div>

      <!-- 前后导航 -->
      <div class="article-nav">
        <a v-if="nav.prev" class="nav-link prev"
          @click.prevent="router.push(`/article/${nav.prev.id}`)">
          <span class="nav-label">← 上一篇</span>
          <span class="nav-title">{{ nav.prev.title }}</span>
        </a>
        <span v-else class="nav-link disabled">← 已经是第一篇了</span>

        <a v-if="nav.next" class="nav-link next"
          @click.prevent="router.push(`/article/${nav.next.id}`)">
          <span class="nav-label">下一篇 →</span>
          <span class="nav-title">{{ nav.next.title }}</span>
        </a>
        <span v-else class="nav-link disabled">已经是最新一篇了 →</span>
      </div>

      <!-- 评论区 -->
      <CommentSection :article-id="article.id" />
    </div>

    <!-- 右侧栏 -->
    <aside class="article-sidebar">
      <!-- 作者卡片 -->
      <div class="sidebar-card author-card">
        <el-avatar :size="56" src="https://i.postimg.cc/y8Q59mgL/images-2.jpg" />
        <div class="author-name">🦌 小鹿</div>
        <div class="author-bio">生活明朗，万物可爱</div>
      </div>

      <!-- 文章目录 -->
      <TocAside :content="renderedContent" />

      <!-- 相关推荐 -->
      <div class="sidebar-card" v-if="related.length">
        <h4>📖 相关推荐</h4>
        <router-link v-for="r in related" :key="r.id" :to="'/article/' + r.id" class="related-item">
          <div class="related-title">{{ locale === 'zh-CN' ? r.title : (r.titleEn || r.title) }}</div>
          <div class="related-meta">{{ r.createdAt?.substring(0, 10) }}</div>
        </router-link>
      </div>

      <!-- 最新文章 -->
      <div class="sidebar-card" v-if="latestArticles.length">
        <h4>🆕 最新文章</h4>
        <router-link v-for="a in latestArticles" :key="a.id" :to="'/article/' + a.id" class="related-item">
          <div class="related-title">{{ locale === 'zh-CN' ? a.title : (a.titleEn || a.title) }}</div>
          <div class="related-meta">{{ a.createdAt?.substring(0, 10) }}</div>
        </router-link>
      </div>
    </aside>
  </div>
  <div v-else style="text-align:center;padding:60px">{{ $t('common.loading') }}</div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import api from '@/api'
import CommentSection from '@/components/CommentSection.vue'
import TocAside from '@/components/TocAside.vue'
import { marked } from 'marked'
import hljs from 'highlight.js'
import 'highlight.js/styles/github-dark.css'

const route = useRoute()
const router = useRouter()
const { locale } = useI18n()
const article = ref<any>(null)
const nav = ref<{ prev: any; next: any }>({ prev: null, next: null })
const related = ref<any[]>([])
const latestArticles = ref<any[]>([])

const isZh = computed(() => locale.value === 'zh-CN')
const displayTitle = computed(() => isZh.value ? article.value?.title : (article.value?.titleEn || article.value?.title))
const displayContent = computed(() => isZh.value ? article.value?.content : (article.value?.contentEn || article.value?.content))
const readingTime = computed(() => {
  if (!article.value?.content) return 1
  const cnChars = (article.value.content.match(/[一-鿿]/g) || []).length
  const words = article.value.content.split(/\s+/).length
  return Math.max(1, Math.ceil((cnChars / 400) + (words / 200)))
})

// Marked: highlight + heading IDs + code block copy/collapse
let codeBlockIndex = 0
marked.use({
  highlight(code: string, lang: string) {
    if (lang && hljs.getLanguage(lang)) return hljs.highlight(code, { language: lang }).value
    return hljs.highlightAuto(code).value
  },
  hooks: {
    postprocess(html: string) {
      // 1. Heading IDs
      html = html.replace(/<h([1-3])>(.*?)<\/h\1>/g, (_: string, level: string, text: string) => {
        const id = text.replace(/<[^>]*>/g, '').replace(/[^\w一-鿿\s-]/g, '').trim().replace(/\s+/g, '-').toLowerCase() || 'heading'
        return `<h${level} id="${id}">${text}</h${level}>`
      })
      // 2. Code blocks: wrap with copy + collapse (with or without language)
      html = html.replace(/<pre><code(?: class="language-(\w*)")?>([\s\S]*?)<\/code><\/pre>/g, (_, lang, code) => {
        const label = lang || 'code'
        const idx = ++codeBlockIndex
        const langAttr = lang ? ` class="language-${lang}"` : ''
        return `<div class="code-block-wrapper">
          <div class="code-block-header">
            <span class="code-lang">${label}</span>
            <div class="code-block-actions">
              <button class="code-btn code-copy-btn" data-code-id="${idx}" title="复制代码">📋 复制</button>
              <button class="code-btn code-fold-btn" data-fold-id="${idx}" title="折叠代码">▲ 收起</button>
            </div>
          </div>
          <div class="code-block-body" data-fold-body="${idx}">
            <pre><code${langAttr}>${code}</code></pre>
          </div>
        </div>`
      })
      return html
    }
  }
})

// Global click handler for code buttons
const handleCodeBlockClick = (e: MouseEvent) => {
  const target = e.target as HTMLElement
  // Copy
  if (target.classList.contains('code-copy-btn')) {
    const wrapper = target.closest('.code-block-wrapper')
    const code = wrapper?.querySelector('code')?.textContent || ''
    navigator.clipboard.writeText(code).then(() => {
      target.textContent = '✅ 已复制'
      setTimeout(() => { target.textContent = '📋 复制' }, 2000)
    })
  }
  // Fold
  if (target.classList.contains('code-fold-btn')) {
    const id = target.dataset.foldId
    const body = document.querySelector(`[data-fold-body="${id}"]`) as HTMLElement
    if (body) {
      const hidden = body.style.display === 'none'
      body.style.display = hidden ? '' : 'none'
      target.textContent = hidden ? '▲ 收起' : '▼ 展开'
    }
  }
}

const renderedContent = computed(() => displayContent.value ? marked(displayContent.value) : '')

const fetchArticle = async () => {
  const id = Number(route.params.id)
  const [detailRes, navRes, relatedRes, latestRes] = await Promise.all([
    api.get(`/articles/${id}`),
    api.get(`/articles/${id}/navigation`),
    api.get(`/articles/${id}/related`),
    api.get('/articles?pageSize=5')
  ])
  article.value = (detailRes as any).data
  nav.value = (navRes as any).data
  related.value = (relatedRes as any).data
  latestArticles.value = ((latestRes as any).data.list || []).filter((a: any) => a.id !== id).slice(0, 5)
  document.title = displayTitle.value + ' - 小鹿博客'
}

watch(locale, () => {
  if (article.value) document.title = displayTitle.value + ' - 小鹿博客'
})

// 关键：监听路由参数变化，重新加载文章
watch(() => route.params.id, () => {
  fetchArticle()
  window.scrollTo({ top: 0, behavior: 'smooth' })
})

onMounted(() => {
  fetchArticle()
  document.addEventListener('click', handleCodeBlockClick)
})
onUnmounted(() => {
  document.removeEventListener('click', handleCodeBlockClick)
})
</script>

<style scoped>
.article-page { display: flex; gap: 32px; max-width: 1100px; margin: 0 auto; padding: 24px 0; position: relative; }
.article-main { flex: 1; min-width: 0; background: var(--bg-card); border-radius: 16px; padding: 36px; border: 1px solid var(--border-color); z-index: 0; }
.article-main h1 { font-size: 1.9rem; font-weight: 800; line-height: 1.4; }

.meta { display: flex; gap: 16px; flex-wrap: wrap; font-size: 0.88rem; color: var(--text-muted); margin: 14px 0; }
.meta-link { color: var(--accent); text-decoration: none; }
.meta-link:hover { text-decoration: underline; }
.meta-tags { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; margin-bottom: 20px; font-size: 0.88rem; color: var(--text-muted); }
.tag-link {
  display: inline-block; padding: 3px 14px; border-radius: 14px;
  font-size: 0.82rem; background: var(--tag-bg); color: var(--tag-text);
  text-decoration: none; transition: all 0.2s;
}
.tag-link:hover { background: var(--accent); color: #fff; }

/* Sidebar */
.article-sidebar {
  width: 280px; flex-shrink: 0;
  position: sticky; top: 80px;
  max-height: calc(100vh - 100px);
  overflow-y: auto;
  display: flex; flex-direction: column; gap: 16px;
  /* hide scrollbar */
  scrollbar-width: none;
  -ms-overflow-style: none;
  padding-bottom: 20px;
}
.article-sidebar::-webkit-scrollbar { display: none; }

.sidebar-card,
.article-sidebar :deep(.toc-card) {
  flex-shrink: 0;
}
.sidebar-card {
  background: var(--bg-card); border: 1px solid var(--border-color);
  border-radius: 14px; padding: 20px;
}
.sidebar-card h4 { font-size: 0.95rem; font-weight: 700; margin-bottom: 12px; color: var(--text-primary); }

.author-card { text-align: center; padding: 24px 20px; }
.author-name { font-weight: 700; font-size: 1rem; margin-top: 10px; }
.author-bio { font-size: 0.82rem; color: var(--text-muted); margin-top: 4px; }

.related-item {
  display: block; padding: 8px 0; border-bottom: 1px solid var(--border-color);
  text-decoration: none; color: inherit; cursor: pointer;
  position: relative; z-index: 5; pointer-events: auto;
  transition: opacity 0.15s;
}
.related-item:last-child { border-bottom: none; }
.related-item:hover { opacity: 0.7; }
.related-title { font-size: 0.88rem; font-weight: 500; color: var(--text-primary); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.related-meta { font-size: 0.78rem; color: var(--text-muted); margin-top: 2px; }

/* Article content */
.article-content { font-size: 1.05rem; line-height: 1.85; color: var(--text-primary); }
.article-content h1, .article-content h2, .article-content h3 { margin: 1.5em 0 0.5em; }
.article-content p { margin: 0.8em 0; }
.article-content pre { background: var(--code-bg); border-radius: 10px; padding: 18px; overflow-x: auto; margin: 16px 0; }
.article-content code { font-family: 'Fira Code', 'Consolas', monospace; font-size: 0.9em; }
.article-content img { border-radius: 10px; margin: 16px 0; max-width: 100%; }
.article-content blockquote {
  border-left: 4px solid var(--accent); padding: 8px 16px; margin: 16px 0;
  background: var(--tag-bg); border-radius: 0 10px 10px 0;
}
.article-content table { border-collapse: collapse; width: 100%; margin: 16px 0; }
.article-content th, .article-content td { border: 1px solid var(--border-color); padding: 8px 12px; text-align: left; }
.article-content th { background: var(--tag-bg); font-weight: 600; }

/* Navigation */
.article-nav { display: flex; justify-content: space-between; gap: 16px; margin-top: 36px; padding-top: 20px; border-top: 1px solid var(--border-color); }
.nav-link { flex: 1; max-width: 48%; display: flex; flex-direction: column; gap: 4px; padding: 14px 18px; border-radius: 12px; background: var(--bg-primary); border: 1px solid var(--border-color); text-decoration: none; color: var(--text-primary); transition: all 0.2s; }
.nav-link:hover { border-color: var(--accent); }
.nav-link.disabled { color: var(--text-muted); cursor: default; align-items: center; justify-content: center; }
.nav-label { font-size: 0.78rem; color: var(--text-muted); }
.nav-title { font-size: 0.9rem; font-weight: 500; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 100%; }
.nav-link.next { text-align: right; }

/* Comments */
.comments-section { margin-top: 36px; padding-top: 24px; border-top: 1px solid var(--border-color); }
.comments-section h3 { font-size: 1.1rem; font-weight: 700; margin-bottom: 12px; }
.comments-placeholder { text-align: center; padding: 40px 20px; color: var(--text-muted); background: var(--bg-primary); border-radius: 12px; }
.comments-placeholder .hint { font-size: 0.85rem; margin-top: 6px; opacity: 0.7; }

/* Code block wrapper */
.article-content :deep(.code-block-wrapper) {
  border-radius: 10px; overflow: hidden; margin: 16px 0;
  border: 1px solid var(--border-color);
}
.article-content :deep(.code-block-header) {
  display: flex; justify-content: space-between; align-items: center;
  padding: 8px 14px; background: var(--bg-secondary);
  border-bottom: 1px solid var(--border-color);
  font-size: 0.82rem; color: var(--text-secondary);
}
.article-content :deep(.code-lang) { font-weight: 600; text-transform: uppercase; }
.article-content :deep(.code-block-actions) { display: flex; gap: 6px; }
.article-content :deep(.code-btn) {
  padding: 2px 10px; border-radius: 6px; border: 1px solid var(--border-color);
  background: var(--bg-card); color: var(--text-secondary);
  font-size: 0.78rem; cursor: pointer; transition: all 0.15s;
}
.article-content :deep(.code-btn:hover) { background: var(--accent); color: #fff; border-color: var(--accent); }
.article-content :deep(.code-block-body) { transition: max-height 0.3s; }
.article-content :deep(.code-block-body pre) { margin: 0; border-radius: 0; }

@media (max-width: 900px) {
  .article-page { flex-direction: column; }
  .article-sidebar { width: 100%; position: static; max-height: none; padding: 0 0 24px 0; }
  .article-main { padding: 20px; }
  .article-nav { flex-direction: column; }
  .nav-link { max-width: 100%; }
}
</style>
