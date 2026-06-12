<template>
  <footer class="footer">
    <div class="footer-inner">
      <div class="footer-brand">
        <span class="footer-logo">🦌</span>
        <span>小鹿博客</span>
      </div>
      <div class="footer-links">
        <a href="https://github.com/Onlooker92013" target="_blank" rel="noopener" title="GitHub">
          <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M12 0C5.37 0 0 5.37 0 12c0 5.31 3.435 9.795 8.205 11.385.6.105.825-.255.825-.57 0-.285-.015-1.23-.015-2.235-3.015.555-3.795-.735-4.035-1.41-.135-.345-.72-1.41-1.23-1.695-.42-.225-1.02-.78-.015-.795.945-.015 1.62.87 1.845 1.23 1.08 1.815 2.805 1.305 3.495.99.105-.78.42-1.305.765-1.605-2.67-.3-5.46-1.335-5.46-5.925 0-1.305.465-2.385 1.23-3.225-.12-.3-.54-1.53.12-3.18 0 0 1.005-.315 3.3 1.23.96-.27 1.98-.405 3-.405s2.04.135 3 .405c2.295-1.56 3.3-1.23 3.3-1.23.66 1.65.24 2.88.12 3.18.765.84 1.23 1.905 1.23 3.225 0 4.605-2.805 5.625-5.475 5.925.435.375.81 1.095.81 2.22 0 1.605-.015 2.895-.015 3.3 0 .315.225.69.825.57A12.02 12.02 0 0024 12c0-6.63-5.37-12-12-12z"/></svg>
        </a>
        <a href="/rss" target="_blank" title="RSS 订阅">
          <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><circle cx="6.18" cy="17.82" r="2.18"/><path d="M4 4.44v2.83c7.03 0 12.73 5.7 12.73 12.73h2.83c0-8.59-6.97-15.56-15.56-15.56zm0 5.66v2.83c3.9 0 7.07 3.17 7.07 7.07h2.83c0-5.47-4.43-9.9-9.9-9.9z"/></svg>
        </a>
        <button class="footer-theme-btn" @click="appStore.toggleDark()" :title="appStore.isDark ? '浅色模式' : '深色模式'">
          <el-icon :size="16"><Sunny v-if="appStore.isDark" /><Moon v-else /></el-icon>
        </button>
      </div>
    </div>
    <div class="footer-bottom">
      <span>© {{ currentYear }} 小鹿 · 生活明朗，万物可爱</span>
      <span class="footer-sep">·</span>
      <span>已运行 {{ daysOnline }} 天</span>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAppStore } from '@/stores/app'
import { Sunny, Moon } from '@element-plus/icons-vue'

const appStore = useAppStore()
const currentYear = new Date().getFullYear()
const startDate = new Date('2021-04-01')
const daysOnline = computed(() => Math.floor((Date.now() - startDate.getTime()) / 86400000))
</script>

<style scoped>
.footer {
  border-top: 1px solid var(--border-color);
  background: var(--bg-card);
  padding: 32px 24px 24px;
  margin-top: 60px;
}
.footer-inner {
  max-width: 1100px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.footer-brand {
  display: flex; align-items: center; gap: 8px;
  font-weight: 600; color: var(--text-primary);
}
.footer-logo { font-size: 1.3rem; }
.footer-links {
  display: flex; align-items: center; gap: 12px;
}
.footer-links a,
.footer-theme-btn {
  display: flex; align-items: center; justify-content: center;
  width: 34px; height: 34px; border-radius: 50%;
  color: var(--text-muted); background: var(--bg-primary);
  transition: all 0.2s; border: none; cursor: pointer;
}
.footer-links a:hover,
.footer-theme-btn:hover {
  color: var(--accent); background: var(--tag-bg);
  transform: translateY(-1px);
}
.footer-bottom {
  max-width: 1100px; margin: 0 auto;
  text-align: center; color: var(--text-muted); font-size: 0.85rem;
  display: flex; justify-content: center; gap: 6px;
}
.footer-sep { opacity: 0.4; }
@media (max-width: 768px) {
  .footer-inner { flex-direction: column; gap: 16px; }
}
</style>
