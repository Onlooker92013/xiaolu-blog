<template>
  <div class="home-page">
    <!-- 主内容区 -->
    <div class="home-main">
      <!-- Hero -->
      <div class="hero">
        <ParticleBg />
        <div class="hero-content">
          <h1 class="hero-title">🦌 小鹿博客</h1>
          <p class="hero-subtitle">{{ $t('home.greeting') }}</p>
          <div class="hero-stats">
            <span>📝 {{ stats.articles }} 篇文章</span>
            <span>📁 {{ stats.categories }} 个分类</span>
            <span>🏷 {{ stats.tags }} 个标签</span>
          </div>
        </div>
      </div>

      <!-- 分类导航 -->
      <div class="section" v-if="categories.length">
        <div class="section-header">
          <h2>📂 文章分类</h2>
          <router-link to="/categories" class="section-more">查看全部 →</router-link>
        </div>
        <div class="category-grid">
          <router-link v-for="cat in categories" :key="cat.id" :to="`/articles?categoryId=${cat.id}`" class="category-card">
            <span class="cat-icon">{{ CAT_ICONS[cat.slug] || '📄' }}</span>
            <div><div class="cat-name">{{ cat.name }}</div><div class="cat-desc">{{ cat.description }}</div></div>
          </router-link>
        </div>
      </div>

      <!-- 最新文章 -->
      <div class="section">
        <div class="section-header"><h2>📝 最新文章</h2></div>
        <ArticleCard v-for="a in articles" :key="a.id" :article="a" />
        <div class="pagination-wrap">
          <el-pagination background layout="prev, pager, next" :total="total"
            :page-size="pageSize" v-model:current-page="page" @current-change="fetchArticles" />
        </div>
      </div>
    </div>

    <!-- 右侧栏 -->
    <aside class="home-sidebar">
      <!-- 作者卡片 -->
      <div class="sidebar-card author-card">
        <el-avatar :size="64" src="https://i.postimg.cc/y8Q59mgL/images-2.jpg" />
        <div class="author-name">🦌 小鹿</div>
        <div class="author-bio">生活明朗，万物可爱</div>
        <div class="author-stats">
          <div class="stat-item">
            <div class="stat-num">{{ stats.articles }}</div><div class="stat-lbl">文章</div>
          </div>
          <div class="stat-item">
            <div class="stat-num">{{ stats.categories }}</div><div class="stat-lbl">分类</div>
          </div>
          <div class="stat-item">
            <div class="stat-num">{{ stats.tags }}</div><div class="stat-lbl">标签</div>
          </div>
        </div>
      </div>

      <!-- 推荐文章 -->
      <div class="sidebar-card" v-if="topArticles.length">
        <h4>📌 推荐文章</h4>
        <router-link v-for="a in topArticles.slice(0, 5)" :key="a.id"
          :to="'/article/' + a.id" class="side-article-item">
          <div class="side-article-title">{{ locale === 'zh-CN' ? a.title : (a.titleEn || a.title) }}</div>
          <div class="side-article-meta">{{ a.createdAt?.substring(0, 10) }} · {{ a.viewCount }} 阅读</div>
        </router-link>
      </div>

      <!-- 阅读排行 -->
      <div class="sidebar-card" v-if="topRead.length">
        <h4>🏆 阅读排行</h4>
        <div v-for="(a, i) in topRead.slice(0, 5)" :key="a.id" class="side-list-item">
          <span class="side-rank" :class="'r'+(i+1)">{{ i+1 }}</span>
          <router-link :to="'/article/' + a.id" class="side-link">{{ a.title }}</router-link>
        </div>
      </div>

      <!-- 标签云 -->
      <div class="sidebar-card" v-if="allTags.length">
        <h4>🏷 标签云</h4>
        <div class="tag-cloud">
          <router-link v-for="t in allTags" :key="t.id"
            :to="'/articles?tag=' + encodeURIComponent(t.name)" class="cloud-tag">
            {{ t.name }}
          </router-link>
        </div>
      </div>
    </aside>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { articleApi, categoryApi, tagApi } from '@/api/endpoints'
import ArticleCard from '@/components/ArticleCard.vue'
import ParticleBg from '@/components/ParticleBg.vue'

const router = useRouter()
const { locale } = useI18n()
const CAT_ICONS: Record<string, string> = { frontend: '💻', college: '🎓', life: '🌈', backend: '☕', tech: '⚡', design: '🎨', ai: '🤖' }

const articles = ref<any[]>([])
const topArticles = ref<any[]>([])
const categories = ref<any[]>([])
const allTags = ref<any[]>([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(window.innerWidth <= 768 ? 5 : 8)
const stats = reactive({ articles: 0, categories: 0, tags: 0 })
const topRead = ref<any[]>([])

const fetchArticles = async () => {
  const res = await articleApi.list({ page: page.value, pageSize: pageSize.value }) as any
  articles.value = res.data.list
  total.value = res.data.total
}

const handleResize = () => {
  const newSize = window.innerWidth <= 768 ? 5 : 8
  if (pageSize.value !== newSize) {
    pageSize.value = newSize
    page.value = 1
    fetchArticles()
  }
}

onMounted(async () => {
  window.addEventListener('resize', handleResize)
  fetchArticles()
  try { topArticles.value = (await articleApi.top() as any).data } catch {}
  try {
    categories.value = (await categoryApi.list() as any).data
    stats.categories = categories.value.length
  } catch {}
  try { allTags.value = (await tagApi.list() as any).data; stats.tags = allTags.value.length } catch {}
  try {
    const countRes = await articleApi.list({ page: 1, pageSize: 1 }) as any
    stats.articles = countRes.data.total
  } catch {}
  // Top read (public API)
  try {
    const res = await fetch('/api/articles/top-read')
    const data = await res.json()
    if (data.data) topRead.value = data.data
  } catch {}
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.home-page { display: flex; gap: 32px; max-width: 1100px; margin: 0 auto; padding: 24px 0; position: relative; }
.home-main { flex: 1; min-width: 0; z-index: 0; }
.home-sidebar { width: 280px; flex-shrink: 0; z-index: 1; }

/* Hero */
.hero {
  text-align: center; padding: 48px 20px 40px; margin-bottom: 28px;
  position: relative; overflow: hidden; border-radius: 18px;
  background: var(--bg-card); border: 1px solid var(--border-color);
}
.hero canvas { position: absolute; inset: 0; z-index: 0; }
.hero-content { position: relative; z-index: 1; }
.hero-title { font-size: 2.2rem; font-weight: 800; }
.hero-subtitle { font-size: 1.05rem; color: var(--text-secondary); margin-top: 6px; }
.hero-stats { display: flex; gap: 20px; justify-content: center; margin-top: 16px; color: var(--text-muted); font-size: 0.88rem; }

.section { margin-bottom: 28px; }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; }
.section-header h2 { font-size: 1.2rem; font-weight: 700; }
.section-more { color: var(--accent); font-size: 0.85rem; }

.category-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 12px; }
.category-card {
  display: flex; align-items: center; gap: 12px; background: var(--bg-card);
  border: 1px solid var(--border-color); border-radius: 14px; padding: 16px 18px;
  text-decoration: none; color: inherit; transition: all 0.2s;
}
.category-card:hover { border-color: var(--accent); box-shadow: 0 3px 12px rgba(66,90,239,0.08); transform: translateY(-1px); }
.cat-icon { font-size: 1.6rem; }
.cat-name { font-weight: 600; font-size: 0.9rem; }
.cat-desc { font-size: 0.78rem; color: var(--text-muted); }

.pagination-wrap { display: flex; justify-content: center; margin-top: 20px; }

/* Sidebar */
.sidebar-card { background: var(--bg-card); border: 1px solid var(--border-color); border-radius: 14px; padding: 20px; margin-bottom: 16px; }
.sidebar-card h4 { font-size: 0.95rem; font-weight: 700; margin-bottom: 12px; }

.author-card { text-align: center; padding: 24px 20px; }
.author-name { font-weight: 700; font-size: 1rem; margin-top: 10px; }
.author-bio { font-size: 0.8rem; color: var(--text-muted); margin-top: 4px; }
.author-stats { display: flex; justify-content: center; gap: 20px; margin-top: 14px; padding-top: 12px; border-top: 1px solid var(--border-color); }
.stat-item { text-align: center; }
.stat-num { font-size: 1.15rem; font-weight: 700; color: var(--accent); }
.stat-lbl { font-size: 0.72rem; color: var(--text-muted); }

.side-article-item {
  display: block; padding: 8px 0; border-bottom: 1px solid var(--border-color);
  text-decoration: none; color: inherit; cursor: pointer;
  position: relative; z-index: 5; pointer-events: auto;
  transition: opacity 0.15s;
}
.side-article-item:last-child { border-bottom: none; }
.side-article-item:hover { opacity: 0.7; }
.side-article-title { font-size: 0.85rem; font-weight: 500; color: var(--text-primary); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.side-article-meta { font-size: 0.75rem; color: var(--text-muted); margin-top: 2px; }

.side-list-item { display: flex; align-items: center; gap: 10px; padding: 6px 0; }
.side-rank {
  width: 22px; height: 22px; border-radius: 6px; display: flex; align-items: center;
  justify-content: center; font-size: 0.75rem; font-weight: 700; background: var(--tag-bg); color: var(--text-muted); flex-shrink: 0;
}
.side-rank.r1 { background: #ffd700; color: #fff; }
.side-rank.r2 { background: #c0c0c0; color: #fff; }
.side-rank.r3 { background: #cd7f32; color: #fff; }
.side-link { font-size: 0.85rem; color: var(--text-primary); text-decoration: none; cursor: pointer; position: relative; z-index: 5; pointer-events: auto; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; flex: 1; }
.side-link:hover { color: var(--accent); }

.tag-cloud { display: flex; flex-wrap: wrap; gap: 8px; }
.cloud-tag {
  display: inline-block; padding: 4px 12px; border-radius: 14px; font-size: 0.8rem;
  background: var(--tag-bg); color: var(--tag-text); text-decoration: none; cursor: pointer;
  position: relative; z-index: 5; pointer-events: auto; transition: all 0.2s;
}
.cloud-tag:hover { background: var(--accent); color: #fff; }

@media (max-width: 900px) {
  .home-page { flex-direction: column; }
  .home-sidebar { width: 100%; order: -1; }
  .hero { padding: 32px 16px; }
  .hero-title { font-size: 1.6rem; }
}
</style>
