<template>
  <div>
    <div class="page-header"><h1>🔍 {{ $t('nav.search') }}</h1></div>
    <el-input v-model="keyword" placeholder="搜索文章..." size="large" clearable @keyup.enter="search" @clear="results=[]">
      <template #append><el-button @click="search" :icon="Search">搜索</el-button></template>
    </el-input>

    <div style="margin-top:24px">
      <ArticleCard v-for="a in results" :key="a.id" :article="a" />
      <el-empty v-if="searched && !results.length" description="未找到相关文章" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { searchApi } from '@/api/endpoints'
import ArticleCard from '@/components/ArticleCard.vue'

const keyword = ref('')
const results = ref<any[]>([])
const searched = ref(false)

const search = async () => {
  if (!keyword.value.trim()) return
  searched.value = true
  const res = await searchApi.search(keyword.value, 1) as any
  results.value = res.data.list
}
</script>
