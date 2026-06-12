<template>
  <nav class="navbar">
    <div class="navbar-inner">
      <router-link to="/" class="navbar-brand">
        <span class="brand-icon">🦌</span>
        <span class="brand-text">小鹿博客</span>
      </router-link>

      <div :class="['navbar-links', { open: mobileOpen }]">
        <router-link to="/" @click="mobileOpen = false">
          <el-icon><HomeFilled /></el-icon>{{ $t('nav.home') }}
        </router-link>
        <router-link to="/articles" @click="mobileOpen = false">
          <el-icon><Reading /></el-icon>{{ $t('nav.articles') }}
        </router-link>
        <router-link to="/archives" @click="mobileOpen = false">
          <el-icon><Clock /></el-icon>{{ $t('nav.archives') }}
        </router-link>
        <router-link to="/categories" @click="mobileOpen = false">
          <el-icon><Folder /></el-icon>{{ $t('nav.categories') }}
        </router-link>
        <router-link to="/tags" @click="mobileOpen = false">
          <el-icon><PriceTag /></el-icon>{{ $t('nav.tags') }}
        </router-link>
        <router-link to="/friends" @click="mobileOpen = false">
          <el-icon><Link /></el-icon>{{ $t('nav.friends') }}
        </router-link>
        <router-link to="/about" @click="mobileOpen = false">
          <el-icon><User /></el-icon>{{ $t('nav.about') }}
        </router-link>
      </div>

      <div class="navbar-actions">
        <el-tooltip :content="currentLang === 'zh-CN' ? 'Switch to English' : '切换到中文'" placement="bottom">
          <button class="nav-btn" @click="toggleLang">
            <span class="lang-badge">{{ currentLang === 'zh-CN' ? 'EN' : '中' }}</span>
          </button>
        </el-tooltip>
        <el-tooltip :content="appStore.isDark ? '浅色模式' : '深色模式'" placement="bottom">
          <button class="nav-btn" @click="appStore.toggleDark()">
            <el-icon :size="18"><Sunny v-if="appStore.isDark" /><Moon v-else /></el-icon>
          </button>
        </el-tooltip>
        <el-tooltip content="搜索" placement="bottom">
          <button class="nav-btn" @click="showSearch = true">
            <el-icon :size="18"><Search /></el-icon>
          </button>
        </el-tooltip>
        <button class="navbar-mobile-toggle" @click="mobileOpen = !mobileOpen">
          <el-icon :size="20"><Menu v-if="!mobileOpen" /><Close v-else /></el-icon>
        </button>
      </div>
    </div>
  </nav>

  <SearchOverlay :visible="showSearch" @close="showSearch = false" />
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAppStore } from '@/stores/app'
import { HomeFilled, Reading, Clock, Folder, PriceTag, Link, User, Sunny, Moon, Search, Menu, Close } from '@element-plus/icons-vue'
import SearchOverlay from './SearchOverlay.vue'

const { locale } = useI18n()
const appStore = useAppStore()
const mobileOpen = ref(false)
const showSearch = ref(false)

const currentLang = computed(() => locale.value)

const toggleLang = () => {
  const newLang = locale.value === 'zh-CN' ? 'en-US' : 'zh-CN'
  locale.value = newLang
  localStorage.setItem('lang', newLang)
}
</script>

<style scoped>
.navbar {
  position: fixed; top: 0; left: 0; right: 0; z-index: 1000;
  background: var(--navbar-bg);
  backdrop-filter: blur(12px) saturate(180%);
  -webkit-backdrop-filter: blur(12px) saturate(180%);
  border-bottom: 1px solid var(--border-color);
  height: 60px;
}
.navbar-inner {
  max-width: 1100px; height: 100%; margin: 0 auto;
  display: flex; align-items: center; gap: 32px;
  padding: 0 24px;
}
.navbar-brand {
  display: flex; align-items: center; gap: 8px;
  font-size: 1.2rem; font-weight: 700; color: var(--text-primary);
  text-decoration: none; flex-shrink: 0;
}
.brand-icon { font-size: 1.4rem; }
.brand-text {
  background: linear-gradient(135deg, var(--accent) 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.navbar-links { display: flex; gap: 4px; flex: 1; }
.navbar-links a {
  display: flex; align-items: center; gap: 4px;
  padding: 6px 12px; border-radius: 8px;
  color: var(--text-secondary); font-size: 0.9rem;
  text-decoration: none; transition: all 0.2s;
}
.navbar-links a:hover {
  color: var(--accent); background: var(--tag-bg);
}
.navbar-links a.router-link-active {
  color: var(--accent); background: var(--tag-bg); font-weight: 500;
}

.navbar-actions { display: flex; align-items: center; gap: 6px; flex-shrink: 0; }
.nav-btn {
  width: 36px; height: 36px; border-radius: 10px;
  border: 1px solid var(--border-color);
  background: var(--bg-secondary);
  color: var(--text-secondary);
  cursor: pointer; display: flex; align-items: center; justify-content: center;
  transition: all 0.2s;
}
.nav-btn:hover {
  border-color: var(--accent); color: var(--accent);
  background: var(--tag-bg);
}
.lang-badge {
  font-size: 0.72rem; font-weight: 700; letter-spacing: 0.5px;
}

.navbar-mobile-toggle {
  display: none; background: none; border: none; color: var(--text-primary);
  cursor: pointer; padding: 4px;
}

@media (max-width: 768px) {
  .navbar-inner { gap: 12px; padding: 0 12px; }
  .navbar-links {
    display: none; position: fixed; top: 60px; left: 0; right: 0;
    background: var(--bg-card); flex-direction: column; padding: 8px;
    border-bottom: 1px solid var(--border-color);
    box-shadow: 0 8px 24px rgba(0,0,0,0.08);
  }
  .navbar-links.open { display: flex; }
  .navbar-links a {
    padding: 10px 14px; border-radius: 10px; font-size: 0.95rem;
  }
  .navbar-mobile-toggle { display: block; }
}
</style>
