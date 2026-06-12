<template>
  <div>
    <!-- 工具栏 -->
    <div class="toolbar">
      <el-button type="primary" @click="openDialog()">
        <el-icon><Plus /></el-icon>新建文章
      </el-button>
      <el-select v-model="statusFilter" placeholder="筛选状态" clearable style="width:120px" @change="fetchData">
        <el-option label="已发布" value="PUBLISHED" />
        <el-option label="草稿" value="DRAFT" />
      </el-select>
    </div>

    <!-- 表格 -->
    <el-table :data="list" border stripe v-loading="loading" style="width:100%">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
      <el-table-column prop="categoryName" label="分类" width="100" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{row}">
          <el-tag :type="row.status==='PUBLISHED'?'success':'info'" size="small">
            {{ row.status === 'PUBLISHED' ? '已发布' : '草稿' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="viewCount" label="阅读" width="70" />
      <el-table-column label="创建时间" width="110">
        <template #default="{row}">{{ row.createdAt?.substring(0, 10) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{row}">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-popconfirm title="确认删除?" @confirm="handleDelete(row.id)">
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

    <!-- 编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="editing?.id ? '编辑文章' : '新建文章'"
      width="900px" top="20px" destroy-on-close>
      <el-form :model="form" label-position="top">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="中文标题"><el-input v-model="form.title" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="英文标题"><el-input v-model="form.titleEn" /></el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="封面图">
          <div style="display:flex;gap:12px;align-items:center">
            <el-input v-model="form.coverImage" placeholder="图片URL" style="flex:1" />
            <el-upload :action="uploadUrl" :headers="uploadHeaders" :on-success="onCoverUpload" :show-file-list="false">
              <el-button>上传</el-button>
            </el-upload>
          </div>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="分类">
              <el-select v-model="form.categoryId" placeholder="选择" clearable>
                <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="标签">
              <el-select v-model="form.tagIds" multiple placeholder="选择">
                <el-option v-for="t in tags" :key="t.id" :label="t.name" :value="t.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item label="发布"><el-switch v-model="form.isPublished" /></el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item label="置顶"><el-switch v-model="form.isTop" /></el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="摘要"><el-input v-model="form.summary" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="中文内容 (Markdown)">
          <el-input v-model="form.content" type="textarea" :rows="12" />
        </el-form-item>
        <el-form-item label="英文内容 (Markdown)">
          <el-input v-model="form.contentEn" type="textarea" :rows="6" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { articleApi, categoryApi, tagApi } from '@/api/endpoints'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const userStore = useUserStore()
const list = ref<any[]>([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const statusFilter = ref('')

const categories = ref<any[]>([])
const tags = ref<any[]>([])
const dialogVisible = ref(false)
const editing = ref<any>(null)
const saving = ref(false)
const form = reactive({
  title: '', titleEn: '', content: '', contentEn: '', summary: '', summaryEn: '',
  coverImage: '', categoryId: null as any, tagIds: [] as number[], isPublished: true, isTop: false
})

const uploadUrl = '/api/admin/upload'
const uploadHeaders = { Authorization: `Bearer ${userStore.token}` }
const onCoverUpload = (res: any) => { form.coverImage = res.data.url }

const fetchData = async () => {
  loading.value = true
  const res = await articleApi.adminList({ page: page.value, pageSize: pageSize.value, status: statusFilter.value || undefined }) as any
  list.value = res.data.list
  total.value = res.data.total
  loading.value = false
}

const openDialog = (row?: any) => {
  editing.value = row
  if (row) Object.assign(form, {
    title: row.title, titleEn: row.titleEn || '', content: row.content || '',
    contentEn: row.contentEn || '', summary: row.summary || '', summaryEn: row.summaryEn || '',
    coverImage: row.coverImage || '', categoryId: row.categoryId, tagIds: [],
    isPublished: row.status === 'PUBLISHED', isTop: row.isTop === 1
  })
  else Object.assign(form, { title: '', titleEn: '', content: '', contentEn: '', summary: '', summaryEn: '',
    coverImage: '', categoryId: null, tagIds: [], isPublished: true, isTop: false })
  dialogVisible.value = true
}

const handleSave = async () => {
  saving.value = true
  try {
    const data = {
      title: form.title, titleEn: form.titleEn,
      content: form.content, contentEn: form.contentEn,
      summary: form.summary, summaryEn: form.summaryEn,
      coverImage: form.coverImage, categoryId: form.categoryId,
      tagIds: form.tagIds, status: form.isPublished ? 'PUBLISHED' : 'DRAFT',
      isTop: form.isTop ? 1 : 0
    }
    if (editing.value?.id) { await articleApi.update(editing.value.id, data); ElMessage.success('已更新') }
    else { await articleApi.create(data); ElMessage.success('已创建') }
    dialogVisible.value = false
    fetchData()
  } finally { saving.value = false }
}

const handleDelete = async (id: number) => {
  await articleApi.delete(id)
  ElMessage.success('已删除')
  fetchData()
}

onMounted(async () => {
  fetchData()
  categories.value = ((await categoryApi.adminList()) as any).data
  tags.value = ((await tagApi.adminList()) as any).data
})
</script>

<style scoped>
.toolbar { display: flex; gap: 12px; margin-bottom: 16px; }
.pagination-wrap { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
