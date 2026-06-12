<template>
  <div>
    <div class="page-header"><h1>📁 {{ $t('nav.categories') }}</h1></div>
    <div style="display:grid;grid-template-columns:repeat(auto-fill,minmax(200px,1fr));gap:16px">
      <el-card v-for="cat in categories" :key="cat.id" shadow="hover" @click="router.push(`/articles?categoryId=${cat.id}`)" style="cursor:pointer">
        <div style="text-align:center">
          <div style="font-size:1.1rem;font-weight:600">{{ cat.name }}</div>
          <div style="color:var(--text-muted);font-size:0.85rem;margin-top:4px">{{ cat.description || '' }}</div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ref, onMounted } from 'vue'
import { categoryApi } from '@/api/endpoints'

const router = useRouter()
const categories = ref<any[]>([])

onMounted(async () => {
  categories.value = ((await categoryApi.list()) as any).data
})
</script>
