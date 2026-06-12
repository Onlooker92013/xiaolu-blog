<template>
  <div>
    <div class="page-header"><h1>🏷 {{ $t('nav.tags') }}</h1></div>
    <div style="display:flex;gap:12px;flex-wrap:wrap">
      <el-tag v-for="tag in tags" :key="tag.id" size="large" style="cursor:pointer;font-size:0.95rem"
        @click="router.push('/articles?tag=' + encodeURIComponent(tag.name))">
        {{ tag.name }}
      </el-tag>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ref, onMounted } from 'vue'
import { tagApi } from '@/api/endpoints'

const router = useRouter()
const tags = ref<any[]>([])

onMounted(async () => {
  tags.value = ((await tagApi.list()) as any).data
})
</script>
