<template>
  <div>
    <div class="toolbar">
      <el-select v-model="filterArticleId" placeholder="按文章筛选" clearable style="width:280px" @change="fetchData">
        <el-option v-for="a in articles" :key="a.id" :label="a.title" :value="a.id" />
      </el-select>
    </div>

    <el-table :data="list" border stripe v-loading="loading" style="width:100%">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="文章" min-width="160" show-overflow-tooltip>
        <template #default="{row}">{{ row.articleTitle }}</template>
      </el-table-column>
      <el-table-column prop="guestName" label="评论者" width="100" />
      <el-table-column prop="content" label="内容" min-width="250" show-overflow-tooltip />
      <el-table-column label="类型" width="80">
        <template #default="{row}">{{ row.parentId ? '回复' : '评论' }}</template>
      </el-table-column>
      <el-table-column label="时间" width="140">
        <template #default="{row}">{{ row.createdAt?.substring(0, 16)?.replace('T', ' ') }}</template>
      </el-table-column>
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{row}">
          <el-popconfirm title="确认删除此评论？子回复也会一并删除" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrap">
      <el-pagination background layout="prev, pager, next" :total="total"
        v-model:current-page="page" :page-size="pageSize" @current-change="fetchData" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '@/api'
import { articleApi } from '@/api/endpoints'
import { ElMessage } from 'element-plus'

const list = ref<any[]>([])
const articles = ref<any[]>([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)
const filterArticleId = ref<number | null>(null)

const fetchData = async () => {
  loading.value = true
  const params: any = { page: page.value, pageSize: pageSize.value }
  if (filterArticleId.value) params.articleId = filterArticleId.value
  const res = await api.get('/admin/comments', { params }) as any
  list.value = res.data.list
  total.value = res.data.total
  loading.value = false
}

const handleDelete = async (id: number) => {
  await api.delete(`/admin/comments/${id}`)
  ElMessage.success('已删除')
  fetchData()
}

onMounted(async () => {
  fetchData()
  try { articles.value = ((await articleApi.adminList({ page: 1, pageSize: 999 })) as any).data.list } catch {}
})
</script>

<style scoped>
.toolbar { margin-bottom: 16px; }
.pagination-wrap { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
