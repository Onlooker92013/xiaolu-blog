<template>
  <div class="settings-page">
    <div class="settings-cards">
      <!-- 个人信息 -->
      <el-card shadow="never">
        <template #header><h3>👤 个人信息</h3></template>
        <el-form :model="profile" label-width="100px" style="max-width:500px">
          <el-form-item label="用户名">
            <el-input v-model="profile.username" disabled />
          </el-form-item>
          <el-form-item label="角色">
            <el-tag>{{ profile.role }}</el-tag>
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="profile.email" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item label="头像URL">
            <el-input v-model="profile.avatar" placeholder="头像图片地址" />
            <div v-if="profile.avatar" style="margin-top:8px">
              <el-avatar :size="60" :src="profile.avatar" />
            </div>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="saveProfile" :loading="saving">保存</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 修改密码 -->
      <el-card shadow="never" style="margin-top:20px">
        <template #header><h3>🔒 修改密码</h3></template>
        <el-form :model="pwdForm" label-width="100px" style="max-width:400px" ref="pwdRef" :rules="pwdRules">
          <el-form-item label="原密码" prop="oldPassword">
            <el-input v-model="pwdForm.oldPassword" type="password" show-password />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="pwdForm.newPassword" type="password" show-password />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="changePassword" :loading="changing">修改密码</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import api from '@/api'
import { ElMessage } from 'element-plus'

const profile = reactive({ username: '', role: '', email: '', avatar: '' })
const saving = ref(false)
const changing = ref(false)
const pwdRef = ref()

const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: (_: any, v: string, cb: any) => v === pwdForm.newPassword ? cb() : cb('两次密码不一致'), trigger: 'blur' }
  ]
}

onMounted(async () => {
  const res = await api.get('/admin/profile') as any
  if (res.data) Object.assign(profile, { password: undefined, ...res.data })
})

const saveProfile = async () => {
  saving.value = true
  try {
    await api.put('/admin/profile', { email: profile.email, avatar: profile.avatar })
    ElMessage.success('个人信息已更新')
  } finally { saving.value = false }
}

const changePassword = async () => {
  const valid = await pwdRef.value?.validate().catch(() => false)
  if (!valid) return
  changing.value = true
  try {
    await api.post('/admin/change-password', {
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword
    })
    ElMessage.success('密码修改成功')
    pwdForm.oldPassword = ''
    pwdForm.newPassword = ''
    pwdForm.confirmPassword = ''
  } catch { ElMessage.error('密码修改失败，请检查原密码') }
  finally { changing.value = false }
}
</script>

<style scoped>
.settings-cards { max-width: 600px; }
.settings-page :deep(.el-card__header) { padding: 16px 20px; }
.settings-page :deep(.el-card__header h3) { font-size: 0.95rem; font-weight: 700; margin: 0; }
</style>
