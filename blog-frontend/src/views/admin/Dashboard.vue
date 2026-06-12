<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <div class="stat-grid">
      <div class="stat-card">
        <div class="stat-icon" style="background:#e8f4fd">📝</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.publishedArticles || 0 }}</div>
          <div class="stat-label">已发布文章</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background:#fff3e0">📄</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.draftArticles || 0 }}</div>
          <div class="stat-label">草稿</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background:#e8f5e9">👁</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalViews || 0 }}</div>
          <div class="stat-label">总阅读量</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background:#fce4ec">💬</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.commentCount || 0 }}</div>
          <div class="stat-label">评论数</div>
        </div>
      </div>
    </div>

    <div class="stat-grid" style="grid-template-columns: repeat(4,1fr)">
      <div class="stat-card sm">
        <div class="stat-icon small" style="background:#ede7f6">📁</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.categoryCount || 0 }}</div>
          <div class="stat-label">分类</div>
        </div>
      </div>
      <div class="stat-card sm">
        <div class="stat-icon small" style="background:#e0f2f1">🏷</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.tagCount || 0 }}</div>
          <div class="stat-label">标签</div>
        </div>
      </div>
      <div class="stat-card sm">
        <div class="stat-icon small" style="background:#fff8e1">👥</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.userCount || 0 }}</div>
          <div class="stat-label">用户</div>
        </div>
      </div>
      <div class="stat-card sm">
        <div class="stat-icon small" style="background:#fbe9e7">⏳</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.pendingFriends || 0 }}</div>
          <div class="stat-label">待审核友链</div>
        </div>
      </div>
    </div>

    <!-- 阅读排行 -->
    <el-card shadow="never" class="dash-section" v-if="stats.topRead?.length">
      <template #header><h3>🏆 阅读排行 Top5</h3></template>
      <div class="rank-list">
        <div v-for="(a, i) in stats.topRead" :key="a.id" class="rank-item">
          <span class="rank-num" :class="'rank-' + (i + 1)">{{ i + 1 }}</span>
          <span class="rank-title">{{ a.title }}</span>
          <span class="rank-views">{{ a.views }} 阅读</span>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '@/api'

const stats = ref<any>({})

onMounted(async () => {
  const res = await api.get('/admin/dashboard') as any
  stats.value = res.data
})
</script>

<style scoped>
.stat-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 16px; }
.stat-card {
  background: var(--bg-card); border: 1px solid var(--border-color);
  border-radius: 14px; padding: 20px; display: flex; align-items: center; gap: 16px;
  transition: all 0.2s;
}
.stat-card:hover { border-color: var(--accent); box-shadow: 0 2px 12px rgba(0,0,0,0.04); }
.stat-card.sm { padding: 14px 18px; }
.stat-icon {
  width: 52px; height: 52px; border-radius: 14px;
  display: flex; align-items: center; justify-content: center; font-size: 1.5rem;
}
.stat-icon.small { width: 40px; height: 40px; border-radius: 10px; font-size: 1.2rem; }
.stat-value { font-size: 1.6rem; font-weight: 800; color: var(--text-primary); line-height: 1; }
.stat-card.sm .stat-value { font-size: 1.2rem; }
.stat-label { font-size: 0.82rem; color: var(--text-muted); margin-top: 4px; }

.dash-section { margin-top: 20px; }
.dash-section h3 { font-size: 1rem; font-weight: 700; }

.rank-list { display: flex; flex-direction: column; gap: 8px; }
.rank-item { display: flex; align-items: center; gap: 12px; padding: 8px 0; border-bottom: 1px solid var(--border-color); }
.rank-item:last-child { border-bottom: none; }
.rank-num {
  width: 26px; height: 26px; border-radius: 8px; display: flex; align-items: center; justify-content: center;
  font-weight: 700; font-size: 0.85rem; background: var(--tag-bg); color: var(--text-muted);
}
.rank-1 { background: #ffd700; color: #fff; }
.rank-2 { background: #c0c0c0; color: #fff; }
.rank-3 { background: #cd7f32; color: #fff; }
.rank-title { flex: 1; font-size: 0.9rem; color: var(--text-primary); }
.rank-views { font-size: 0.82rem; color: var(--text-muted); flex-shrink: 0; }

@media (max-width: 1024px) {
  .stat-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 640px) {
  .stat-grid { grid-template-columns: 1fr; }
}
</style>
