<template>
  <div>
    <div class="toolbar">
      <el-button type="primary" @click="openDialog()">
        <el-icon><Plus /></el-icon>新建标签
      </el-button>
    </div>

    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" min-width="150" />
      <el-table-column prop="slug" label="Slug" min-width="150" />
      <el-table-column label="操作" width="160">
        <template #default="{row}">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editing?.id ? '编辑标签' : '新建标签'" width="500px">
      <el-form :model="form">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="Slug"><el-input v-model="form.slug" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { tagApi } from '@/api/endpoints'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const list = ref<any[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const editing = ref<any>(null)
const form = reactive({ name: '', slug: '' })

const fetchData = async () => {
  loading.value = true
  list.value = ((await tagApi.adminList()) as any).data
  loading.value = false
}

const openDialog = (row?: any) => {
  editing.value = row
  Object.assign(form, row ? { name: row.name, slug: row.slug } : { name: '', slug: '' })
  dialogVisible.value = true
}

const handleSave = async () => {
  if (editing.value?.id) { await tagApi.update(editing.value.id, form) }
  else { await tagApi.create(form) }
  ElMessage.success('已保存')
  dialogVisible.value = false
  fetchData()
}

const handleDelete = async (id: number) => {
  await tagApi.delete(id)
  ElMessage.success('已删除')
  fetchData()
}

onMounted(fetchData)
</script>

<style scoped>
.toolbar { margin-bottom: 16px; }
</style>
