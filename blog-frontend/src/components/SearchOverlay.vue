<template>
  <Teleport to="body">
    <transition name="search-fade">
      <div v-if="visible" class="search-overlay" @click.self="close">
        <div class="search-panel">
          <div class="search-input-wrap">
            <el-icon :size="20" class="search-icon"><Search /></el-icon>
            <input
              ref="inputRef"
              v-model="keyword"
              class="search-input"
              :placeholder="placeholder"
              @keyup.enter="handleSearch"
              @keyup.esc="close"
            />
            <el-button text @click="close" class="close-btn">
              <el-icon :size="20"><Close /></el-icon>
            </el-button>
          </div>

          <div class="search-results" v-if="keyword && results.length">
            <div
              v-for="item in results"
              :key="item.id"
              class="search-result-item"
              @click="goTo(item.id)"
            >
              <span class="result-title" v-html="highlightTitle(item)" />
              <span class="result-date">{{ item.createdAt?.substring(0, 10) }}</span>
            </div>
          </div>
          <div class="search-empty" v-else-if="keyword && searched">
            <el-empty description="未找到相关文章" :image-size="80" />
          </div>
          <div class="search-hint" v-else>
            <span>输入关键词搜索文章标题和内容</span>
          </div>
        </div>
      </div>
    </transition>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, nextTick, watch } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Close } from '@element-plus/icons-vue'
import { searchApi } from '@/api/endpoints'

const props = defineProps<{ visible: boolean; placeholder?: string }>()
const emit = defineEmits<{ (e: 'close'): void }>()

const router = useRouter()
const inputRef = ref<HTMLInputElement>()
const keyword = ref('')
const results = ref<any[]>([])
const searched = ref(false)

const close = () => {
  keyword.value = ''
  results.value = []
  searched.value = false
  emit('close')
}

const handleSearch = async () => {
  if (!keyword.value.trim()) return
  searched.value = true
  try {
    const res = await searchApi.search(keyword.value.trim(), 1) as any
    results.value = res.data.list
  } catch { results.value = [] }
}

const goTo = (id: number) => {
  close()
  router.push(`/article/${id}`)
}

const highlightTitle = (item: any) => {
  const title = item.title || ''
  if (!keyword.value) return title
  const re = new RegExp(`(${keyword.value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')})`, 'gi')
  return title.replace(re, '<em style="color:var(--accent);font-style:normal">$1</em>')
}

watch(() => props.visible, (val) => {
  if (val) nextTick(() => inputRef.value?.focus())
})
</script>

<style scoped>
.search-overlay {
  position: fixed;
  inset: 0;
  z-index: 9999;
  background: rgba(0, 0, 0, 0.35);
  display: flex;
  justify-content: center;
  padding-top: 12vh;
  backdrop-filter: blur(4px);
}
.search-panel {
  width: 600px;
  max-width: 90vw;
  max-height: 70vh;
  background: var(--bg-card);
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}
.search-input-wrap {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-color);
  gap: 12px;
}
.search-icon { color: var(--text-muted); flex-shrink: 0; }
.search-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 1.05rem;
  background: transparent;
  color: var(--text-primary);
}
.search-input::placeholder { color: var(--text-muted); }
.close-btn { flex-shrink: 0; }

.search-results { flex: 1; overflow-y: auto; padding: 8px 0; }
.search-result-item {
  padding: 12px 20px;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: background 0.15s;
}
.search-result-item:hover { background: var(--tag-bg); }
.result-title { font-size: 0.95rem; color: var(--text-primary); flex: 1; }
.result-date { font-size: 0.8rem; color: var(--text-muted); margin-left: 16px; flex-shrink: 0; }

.search-empty,
.search-hint {
  padding: 40px 20px;
  text-align: center;
  color: var(--text-muted);
  font-size: 0.9rem;
}

/* Transition */
.search-fade-enter-active { transition: all 0.25s ease; }
.search-fade-leave-active { transition: all 0.2s ease; }
.search-fade-enter-from { opacity: 0; }
.search-fade-enter-from .search-panel { transform: translateY(-30px); opacity: 0; }
.search-fade-leave-to { opacity: 0; }
.search-fade-leave-to .search-panel { transform: translateY(-20px); opacity: 0; }
</style>
