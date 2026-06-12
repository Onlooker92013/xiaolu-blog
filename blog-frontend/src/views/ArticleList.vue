<template>
  <div>
    <div class="page-header"><h1>📝 {{ $t('nav.articles') }}</h1></div>

    <div style="display:flex;gap:12px;flex-wrap:wrap;margin-bottom:20px;align-items:center">
      <el-button v-for="cat in categories" :key="cat.id" size="small" round
        :type="selectedCategory === cat.id ? 'primary' : 'default'"
        @click="selectedCategory = selectedCategory === cat.id ? null : cat.id; page = 1; fetchArticles()">
        {{ cat.name }}
      </el-button>
      <el-tag v-if="selectedTag" type="primary" size="small" closable
        @close="selectedTag = null; page = 1; fetchArticles()">
        标签: {{ selectedTag }}
      </el-tag>
    </div>

    <ArticleCard v-for="a in articles" :key="a.id" :article="a" />
    <el-empty v-if="!articles.length" :description="$t('article.noArticle')" />
    <div class="pagination">
      <el-pagination
        background layout="prev, pager, next" :total="total"
        :page-size="pageSize" v-model:current-page="page"
        @current-change="fetchArticles" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { articleApi, categoryApi } from '@/api/endpoints'
import ArticleCard from '@/components/ArticleCard.vue'

const route = useRoute()
const articles = ref<any[]>([])
const categories = ref<any[]>([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(5)
const selectedCategory = ref<number | null>(null)
const selectedTag = ref<string | null>(null)

const fetchArticles = async () => {
  const params: any = { page: page.value, pageSize: pageSize.value }
  if (selectedCategory.value) params.categoryId = selectedCategory.value
  if (selectedTag.value) params.tag = selectedTag.value
  const res = await articleApi.list(params) as any
  articles.value = res.data.list
  total.value = res.data.total
}

onMounted(async () => {
  categories.value = ((await categoryApi.list()) as any).data
  if (route.query.categoryId) selectedCategory.value = Number(route.query.categoryId)
  if (route.query.tag) selectedTag.value = route.query.tag as string
  fetchArticles()
})
</script>
