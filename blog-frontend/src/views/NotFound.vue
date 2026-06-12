<template>
  <div style="text-align:center;padding:80px 20px">
    <h1 style="font-size:4rem;color:var(--text-muted)">404</h1>
    <p style="margin:16px 0;color:var(--text-secondary)">页面未找到</p>
    <el-button type="primary" @click="router.push('/')">返回首页</el-button>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
const router = useRouter()
</script>
