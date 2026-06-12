<template>
  <transition name="back-top">
    <button v-show="visible" class="back-to-top" @click="scrollToTop" title="回到顶部">
      <el-icon :size="20"><ArrowUpBold /></el-icon>
    </button>
  </transition>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { ArrowUpBold } from '@element-plus/icons-vue'

const visible = ref(false)

let ticking = false
const onScroll = () => {
  if (!ticking) {
    requestAnimationFrame(() => {
      visible.value = window.scrollY > 400
      ticking = false
    })
    ticking = true
  }
}

const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => window.addEventListener('scroll', onScroll, { passive: true }))
onUnmounted(() => window.removeEventListener('scroll', onScroll))
</script>

<style scoped>
.back-to-top {
  position: fixed; bottom: 36px; right: 36px; z-index: 999;
  width: 44px; height: 44px; border-radius: 50%;
  background: var(--bg-card); color: var(--text-secondary);
  border: 1px solid var(--border-color);
  cursor: pointer; display: flex; align-items: center; justify-content: center;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  transition: transform 0.2s, box-shadow 0.2s, background 0.2s, color 0.2s;
}
.back-to-top:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.14);
  color: var(--accent);
  border-color: var(--accent);
}

.back-top-enter-active { transition: all 0.3s ease; }
.back-top-leave-active { transition: all 0.2s ease; }
.back-top-enter-from, .back-top-leave-to { opacity: 0; transform: translateY(12px) scale(0.9); }

@media (max-width: 768px) {
  .back-to-top { bottom: 20px; right: 16px; width: 40px; height: 40px; }
}
</style>
