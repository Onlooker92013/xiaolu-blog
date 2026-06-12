<template>
  <div>
    <div class="page-header"><h1>📦 {{ $t('nav.archives') }}</h1></div>
    <el-timeline>
      <el-timeline-item
        v-for="item in timeline" :key="item.yearMonth"
        :timestamp="item.yearMonth" placement="top" color="var(--accent)">
        <div v-for="a in item.articles" :key="a.id" style="margin-bottom:12px">
          <a style="font-weight:500;cursor:pointer;text-decoration:none;color:var(--text-primary)"
            @click.prevent="router.push(`/article/${a.id}`)">
            {{ locale === 'zh-CN' ? a.title : (a.titleEn || a.title) }}
          </a>
          <span style="color:var(--text-muted);font-size:0.85rem;margin-left:8px">
            {{ a.createdAt?.substring(0, 10) }}
          </span>
        </div>
      </el-timeline-item>
    </el-timeline>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { articleApi } from '@/api/endpoints'

const router = useRouter()

const { locale } = useI18n()

interface TimelineItem {
  yearMonth: string
  articles: any[]
}

const timeline = ref<TimelineItem[]>([])

onMounted(async () => {
  const res = await articleApi.list({ page: 1, pageSize: 999 }) as any
  const articles = res.data.list

  const map = new Map<string, any[]>()
  articles.forEach((a: any) => {
    const ym = a.createdAt?.substring(0, 7) || 'Unknown'
    if (!map.has(ym)) map.set(ym, [])
    map.get(ym)!.push(a)
  })

  timeline.value = Array.from(map.entries())
    .sort((a, b) => b[0].localeCompare(a[0]))
    .map(([yearMonth, articles]) => ({ yearMonth, articles }))
})
</script>
