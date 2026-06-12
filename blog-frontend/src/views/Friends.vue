<template>
  <div>
    <div class="page-header">
      <h1>🔗 {{ $t('nav.friends') }}</h1>
      <p class="page-subtitle">欢迎交换友链，常来串门 ~</p>
    </div>

    <!-- 友链卡片 -->
    <div class="friends-grid">
      <!-- 申请入口卡片 -->
      <div class="friend-card apply-card" @click="dialogVisible = true">
        <div class="apply-icon">+</div>
        <div class="friend-name">申请友链</div>
        <div class="friend-desc">点击这里加入友链</div>
      </div>

      <a v-for="f in friends" :key="f.id" :href="f.url" target="_blank" rel="noopener" class="friend-card">
        <img :src="f.avatar" :alt="f.name" class="friend-avatar"
          @error="(e:any) => e.target.src='data:image/svg+xml,<svg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 100 100%22><rect fill=%22%23ddd%22 width=%22100%22 height=%22100%22/><text x=%2250%22 y=%2260%22 text-anchor=%22middle%22 fill=%22%23999%22 font-size=%2216%22>?</text></svg>'" />
        <div class="friend-name">{{ f.name }}</div>
        <div class="friend-desc">{{ f.description }}</div>
      </a>
    </div>

    <el-empty v-if="!friends.length" description="暂无友链，来当第一个！" />

    <!-- 申请对话框 -->
    <el-dialog v-model="dialogVisible" title="✍️ 申请友链" width="460px" :align-center="true" destroy-on-close>
      <el-form :model="form" label-position="top" class="apply-form">
        <el-form-item label="网站名称">
          <el-input v-model="form.name" placeholder="你的网站叫什么？" maxlength="20" show-word-limit />
        </el-form-item>
        <el-form-item label="网站地址">
          <el-input v-model="form.url" placeholder="https://..." />
        </el-form-item>
        <el-form-item label="头像地址">
          <el-input v-model="form.avatar" placeholder="头像图片URL">
            <template #prefix>
              <div class="avatar-preview" v-if="form.avatar">
                <img :src="form.avatar" @error="(e:any) => e.target.style.display='none'" />
              </div>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="一句话介绍">
          <el-input v-model="form.description" placeholder="简介一下你的网站" maxlength="30" show-word-limit type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitApply" :loading="submitting">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { friendApi } from '@/api/endpoints'
import { ElMessage } from 'element-plus'

const friends = ref<any[]>([])
const dialogVisible = ref(false)
const submitting = ref(false)
const form = ref({ name: '', url: '', avatar: '', description: '' })

onMounted(async () => {
  friends.value = ((await friendApi.list()) as any).data
})

const submitApply = async () => {
  if (!form.value.name || !form.value.url) {
    ElMessage.warning('请填写网站名称和地址')
    return
  }
  submitting.value = true
  try {
    await friendApi.apply(form.value)
    ElMessage.success('友链申请已提交，审核通过后展示~')
    dialogVisible.value = false
    form.value = { name: '', url: '', avatar: '', description: '' }
  } finally { submitting.value = false }
}
</script>

<style scoped>
.page-subtitle {
  color: var(--text-secondary);
  font-size: 0.95rem;
  margin-top: 4px;
}

.friends-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.friend-card {
  background: var(--bg-card);
  border-radius: 16px;
  padding: 28px 20px;
  text-align: center;
  box-shadow: var(--shadow);
  transition: transform 0.25s, box-shadow 0.25s;
  display: block;
  text-decoration: none;
  color: inherit;
}
.friend-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-hover);
}

.friend-avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 14px;
  border: 3px solid var(--border-color);
  transition: border-color 0.25s;
}
.friend-card:hover .friend-avatar {
  border-color: var(--accent);
}

.friend-name {
  font-weight: 600;
  font-size: 1rem;
  color: var(--text-primary);
  margin-bottom: 4px;
}
.friend-desc {
  font-size: 0.85rem;
  color: var(--text-muted);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 申请卡片 */
.apply-card {
  cursor: pointer;
  border: 2px dashed var(--border-color);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 170px;
  background: transparent;
  box-shadow: none;
}
.apply-card:hover {
  border-color: var(--accent);
  background: var(--tag-bg);
}
.apply-icon {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  border: 3px dashed var(--text-muted);
  color: var(--text-muted);
  font-size: 2rem;
  font-weight: 300;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 14px;
  transition: all 0.25s;
}
.apply-card:hover .apply-icon {
  border-color: var(--accent);
  color: var(--accent);
}

/* 头像预览 */
.avatar-preview {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  overflow: hidden;
}
.avatar-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.apply-form {
  padding: 4px 0;
}
</style>
