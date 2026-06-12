<template>
  <div class="admin-layout" :class="{ dark: appStore.isDark }">
    <!-- 左侧导航 -->
    <aside class="admin-sidebar" :class="{ collapsed }">
      <div class="sidebar-header">
        <span class="sidebar-logo">🦌 博客后台</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        :collapse="collapsed"
        background-color="transparent"
        text-color="var(--admin-text)"
        active-text-color="var(--accent)"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataBoard /></el-icon><span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/admin/articles">
          <el-icon><Document /></el-icon><span>文章管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/categories">
          <el-icon><Folder /></el-icon><span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/tags">
          <el-icon><PriceTag /></el-icon><span>标签管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/friends">
          <el-icon><Link /></el-icon><span>友链管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/comments">
          <el-icon><ChatDotRound /></el-icon><span>评论管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/settings">
          <el-icon><Setting /></el-icon><span>个人设置</span>
        </el-menu-item>
      </el-menu>
    </aside>

    <!-- 右侧内容区 -->
    <main class="admin-main">
      <header class="admin-topbar">
        <div class="topbar-left">
          <el-button text @click="collapsed = !collapsed" style="color:var(--admin-text);font-size:18px">
            <el-icon :size="20"><Fold v-if="!collapsed" /><Expand v-else /></el-icon>
          </el-button>
          <h2>{{ pageTitle }}</h2>
        </div>
        <div class="topbar-right">
          <span class="topbar-user"><el-icon><UserFilled /></el-icon> {{ userStore.username }}</span>
          <el-button text @click="appStore.toggleDark()" style="color:var(--admin-text)">
            <el-icon :size="18"><Sunny v-if="appStore.isDark" /><Moon v-else /></el-icon>
          </el-button>
          <el-tooltip content="返回前台" placement="bottom">
            <el-button text @click="router.push('/')" style="color:var(--admin-text)">
              <el-icon :size="18"><HomeFilled /></el-icon>
            </el-button>
          </el-tooltip>
          <el-button text @click="logout" style="color:#f56c6c">
            <el-icon :size="18"><SwitchButton /></el-icon> 退出
          </el-button>
        </div>
      </header>
      <div class="admin-content">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'
import { DataBoard, Document, Folder, PriceTag, Link, Setting, ChatDotRound, Fold, Expand, Sunny, Moon, SwitchButton, HomeFilled, UserFilled } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()
const collapsed = ref(false)

const activeMenu = computed(() => route.path)

const pageTitle = computed(() => {
  const map: Record<string, string> = {
    '/admin/dashboard': '仪表盘',
    '/admin/articles': '文章管理',
    '/admin/categories': '分类管理',
    '/admin/tags': '标签管理',
    '/admin/friends': '友链管理',
    '/admin/comments': '评论管理',
    '/admin/settings': '个人设置'
  }
  return map[route.path] || '管理后台'
})

const logout = () => {
  userStore.logout()
  router.push('/admin/login')
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: var(--bg-primary);
}
.admin-layout.dark {
  --admin-text: #bbb;
}

.admin-sidebar {
  width: 220px;
  background: var(--bg-card);
  border-right: 1px solid var(--border-color);
  display: flex;
  flex-direction: column;
  position: fixed;
  top: 0; left: 0; bottom: 0;
  z-index: 100;
  transition: width 0.3s;
}
.admin-sidebar.collapsed { width: 64px; }
.admin-sidebar.collapsed .sidebar-logo { display: none; }
.admin-sidebar.collapsed :deep(.el-menu-item span) { display: none; }

.sidebar-header {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  border-bottom: 1px solid var(--border-color);
}
.sidebar-logo {
  font-size: 1.15rem;
  font-weight: 700;
  color: var(--text-primary);
  white-space: nowrap;
}

.admin-main {
  flex: 1;
  margin-left: 220px;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  transition: margin-left 0.3s;
}
.admin-sidebar.collapsed ~ .admin-main { margin-left: 64px; }

.admin-topbar {
  height: 60px;
  background: var(--bg-card);
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  position: sticky;
  top: 0;
  z-index: 50;
}
.topbar-left { display: flex; align-items: center; gap: 8px; }
.topbar-left h2 { font-size: 1.1rem; font-weight: 600; color: var(--text-primary); }
.topbar-right { display: flex; align-items: center; gap: 4px; }
.topbar-user { color: var(--text-secondary); font-size: 0.9rem; display: flex; align-items: center; gap: 4px; margin-right: 8px; }

.admin-content {
  flex: 1;
  padding: 24px;
  background: var(--bg-primary);
  color: var(--text-primary);
}

/* Element Plus menu dark mode overrides */
.admin-layout.dark :deep(.el-menu) {
  --el-menu-bg-color: transparent;
  --el-menu-text-color: #bbb;
  --el-menu-hover-bg-color: rgba(255,255,255,0.05);
  --el-menu-active-color: var(--accent);
}
.admin-layout :deep(.el-menu) {
  border-right: none;
}
.admin-layout :deep(.el-menu-item) {
  color: var(--text-secondary);
  transition: all 0.2s;
}
.admin-layout :deep(.el-menu-item:hover) {
  color: var(--accent);
  background: var(--tag-bg);
}
.admin-layout :deep(.el-menu-item.is-active) {
  color: var(--accent);
  background: var(--tag-bg);
  font-weight: 500;
}

.admin-layout { --admin-text: #666; }

@media (max-width: 768px) {
  .admin-sidebar { width: 64px; }
  .admin-sidebar .sidebar-logo { display: none; }
  .admin-sidebar :deep(.el-menu-item span) { display: none; }
  .admin-main { margin-left: 64px; }
}
</style>
