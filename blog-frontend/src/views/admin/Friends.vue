<template>
  <div>
    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" width="150" />
      <el-table-column prop="url" label="网址" min-width="200" show-overflow-tooltip />
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{row}">
          <el-tag :type="row.status==='APPROVED'?'success':'warning'" size="small">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{row}">
          <el-button v-if="row.status==='PENDING'" size="small" type="success" @click="handleApprove(row.id)">通过</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { friendApi } from '@/api/endpoints'
import { ElMessage } from 'element-plus'

const list = ref<any[]>([])
const loading = ref(false)

const fetchData = async () => {
  loading.value = true
  list.value = ((await friendApi.adminList()) as any).data
  loading.value = false
}

const handleApprove = async (id: number) => {
  await friendApi.approve(id)
  ElMessage.success('已通过审核')
  fetchData()
}

const handleDelete = async (id: number) => {
  await friendApi.delete(id)
  ElMessage.success('已删除')
  fetchData()
}

onMounted(fetchData)
</script>
