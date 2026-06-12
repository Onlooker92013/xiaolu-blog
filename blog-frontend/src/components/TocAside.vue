<template>
  <div class="toc-card" v-if="headings.length > 1">
    <h4>📑 文章目录</h4>
    <nav class="toc-nav">
      <a v-for="h in headings" :key="h.id"
        :class="['toc-item', 'toc-' + h.level, { active: activeId === h.id }]"
        :style="{ paddingLeft: (h.level - 1) * 14 + 'px' }"
        :href="'#' + h.id"
        @click.prevent="scrollTo(h.id)"
      >{{ h.text }}</a>
    </nav>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, onUnmounted } from 'vue'

const props = defineProps<{ content: string }>()

interface Heading { id: string; text: string; level: number }
const headings = ref<Heading[]>([])
const activeId = ref('')

const parseHeadings = (html: string) => {
  const result: Heading[] = []
  const regex = /<h([1-3])[^>]*?id="([^"]*)"[^>]*>(.*?)<\/h[1-3]>/gi
  let match
  while ((match = regex.exec(html)) !== null) {
    result.push({ level: parseInt(match[1]), id: match[2], text: match[3].replace(/<[^>]*>/g, '') })
  }
  return result
}

const scrollTo = (id: string) => {
  const el = document.getElementById(id)
  if (el) { el.scrollIntoView({ behavior: 'smooth', block: 'start' }); activeId.value = id }
}

let observer: IntersectionObserver | null = null

onMounted(() => {
  watch(() => props.content, (html) => {
    if (html) {
      headings.value = parseHeadings(html)
      setTimeout(() => {
        observer?.disconnect()
        observer = new IntersectionObserver(
          (entries) => {
            for (const e of entries) {
              if (e.isIntersecting) { activeId.value = e.target.id; break }
            }
          },
          { rootMargin: '-80px 0px -70% 0px' }
        )
        document.querySelectorAll('.article-content h1[id], .article-content h2[id], .article-content h3[id]')
          .forEach(el => observer?.observe(el))
      }, 500)
    }
  }, { immediate: true })

  onUnmounted(() => observer?.disconnect())
})
</script>

<style scoped>
.toc-card {
  background: var(--bg-card); border: 1px solid var(--border-color);
  border-radius: 14px; padding: 16px 20px; flex-shrink: 0;
}
.toc-card h4 { font-size: 0.95rem; font-weight: 700; margin: 0 0 10px 0; color: var(--text-primary); }
.toc-nav { display: flex; flex-direction: column; gap: 2px; }
.toc-item {
  display: block; padding: 5px 8px; border-radius: 6px;
  font-size: 0.85rem; color: var(--text-muted); text-decoration: none;
  border-left: 2px solid transparent; transition: all 0.2s;
  overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
}
.toc-item:hover { color: var(--accent); background: var(--tag-bg); }
.toc-item.active { color: var(--accent); border-left-color: var(--accent); background: var(--tag-bg); font-weight: 500; }
.toc-1 { font-weight: 600; font-size: 0.9rem; color: var(--text-primary); }
.toc-2 { font-size: 0.85rem; }
.toc-3 { font-size: 0.8rem; opacity: 0.85; }
</style>
