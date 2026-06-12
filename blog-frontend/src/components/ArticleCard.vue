<template>
  <article class="article-card" @click="router.push(`/article/${article.id}`)">
    <div class="card-body">
      <div class="card-meta-top">
        <span v-if="article.isTop" class="badge-top">📌 置顶</span>
        <router-link v-if="article.categoryId" :to="'/articles?categoryId=' + article.categoryId"
          class="badge-category" @click.stop>
          {{ article.categoryName }}
        </router-link>
      </div>
      <h2 class="card-title">{{ displayTitle }}</h2>
      <p class="card-summary">{{ displaySummary }}</p>
      <div class="card-footer">
        <div class="card-info">
          <span class="info-item">📅 {{ article.createdAt?.substring(0, 10) }}</span>
          <span class="info-sep">·</span>
          <span class="info-item">👁 {{ article.viewCount || 0 }}</span>
        </div>
        <div class="card-tag-row" v-if="article.tags?.length">
          <router-link v-for="tag in article.tags?.slice(0, 3)" :key="tag"
            :to="'/articles?tag=' + encodeURIComponent(tag)"
            class="card-tag" @click.stop>
            {{ tag }}
          </router-link>
        </div>
        <span class="read-more">阅读 →</span>
      </div>
    </div>
    <div class="card-cover" v-if="article.coverImage">
      <img :src="article.coverImage" :alt="displayTitle" loading="lazy" />
    </div>
  </article>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'

const router = useRouter()
const props = defineProps<{ article: any }>()
const { locale } = useI18n()

const isZh = computed(() => locale.value === 'zh-CN')
const displayTitle = computed(() => isZh.value ? props.article.title : (props.article.titleEn || props.article.title))
const displaySummary = computed(() => isZh.value ? props.article.summary : (props.article.summaryEn || props.article.summary))
</script>

<style scoped>
.article-card {
  display: flex; gap: 20px;
  background: var(--bg-card); border: 1px solid var(--border-color);
  border-radius: 14px; padding: 0; margin-bottom: 16px;
  overflow: hidden; cursor: pointer;
  transition: all 0.25s;
}
.article-card:hover {
  border-color: var(--accent);
  box-shadow: 0 4px 20px rgba(0,0,0,0.06);
  transform: translateY(-1px);
}
.card-body { flex: 1; padding: 22px 24px; display: flex; flex-direction: column; }
.card-meta-top { display: flex; gap: 8px; margin-bottom: 10px; }
.badge-top { font-size: 0.78rem; color: var(--accent); font-weight: 500; }
.badge-category {
  font-size: 0.75rem; color: var(--accent); background: var(--tag-bg);
  padding: 2px 10px; border-radius: 12px; text-decoration: none; transition: all 0.2s;
}
.badge-category:hover { background: var(--accent); color: #fff; }
.card-title { font-size: 1.2rem; font-weight: 700; color: var(--text-primary); margin-bottom: 8px; line-height: 1.4; }
.card-summary {
  font-size: 0.93rem; color: var(--text-secondary); line-height: 1.65;
  flex: 1; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical;
  overflow: hidden;
}
.card-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 14px; }
.card-info { display: flex; align-items: center; flex-wrap: wrap; gap: 2px; font-size: 0.82rem; color: var(--text-muted); }
.info-item { white-space: nowrap; }
.info-sep { margin: 0 2px; opacity: 0.4; }
.card-tag-row { display: flex; gap: 6px; flex-wrap: wrap; }
.card-tag {
  font-size: 0.75rem; padding: 2px 10px; border-radius: 12px;
  background: var(--tag-bg); color: var(--tag-text); text-decoration: none; cursor: pointer; transition: all 0.2s;
}
.card-tag:hover { background: var(--accent); color: #fff; }
.read-more { font-size: 0.85rem; color: var(--accent); font-weight: 500; white-space: nowrap; flex-shrink: 0; }
.read-more:hover { text-decoration: underline; }

.card-cover {
  width: 200px; flex-shrink: 0;
  border-left: 1px solid var(--border-color);
}
.card-cover img { width: 100%; height: 100%; object-fit: cover; }

@media (max-width: 640px) {
  .article-card { flex-direction: column-reverse; }
  .card-cover { width: 100%; height: 160px; border-left: none; border-bottom: 1px solid var(--border-color); }
  .card-body { padding: 16px; }
  .card-title { font-size: 1.05rem; }
}
</style>
