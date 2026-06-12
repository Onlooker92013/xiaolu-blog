<template>
  <div :class="['app-container', themeClass]">
    <!-- 前台布局：显示导航 + 页脚 + 返回顶部 -->
    <template v-if="!isAdminRoute">
      <Navbar />
      <main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
      <Footer />
      <BackToTop />
    </template>

    <!-- 后台布局：纯净管理面板，自带侧栏 + 顶栏 -->
    <router-view v-else v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAppStore } from './stores/app'
import Navbar from './components/Navbar.vue'
import Footer from './components/Footer.vue'
import BackToTop from './components/BackToTop.vue'

const route = useRoute()
const appStore = useAppStore()
const themeClass = computed(() => appStore.isDark ? 'dark' : 'light')
const isAdminRoute = computed(() => route.path.startsWith('/admin'))
</script>
