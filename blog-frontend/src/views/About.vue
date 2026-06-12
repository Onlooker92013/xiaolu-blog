<template>
  <div class="about-page">
    <div class="about-hero">
      <el-avatar :size="96" :src="settings.avatar_url" class="about-avatar" />
      <h1>🦌 {{ settings.site_name || '小鹿' }}</h1>
      <p class="about-tagline">{{ settings.site_slogan }}</p>
      <div class="about-social">
        <a href="https://github.com/Onlooker92013" target="_blank" rel="noopener" class="social-link" title="GitHub">
          <el-icon :size="20"><svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 0a12 12 0 0 0-3.38 23.38c.6.12.82-.26.82-.57l-.01-2.24c-3.34.73-4.04-1.43-4.04-1.43-.55-1.4-1.34-1.77-1.34-1.77-1.1-.74.08-.73.08-.73 1.22.09 1.87 1.25 1.87 1.25 1.08 1.86 2.84 1.3 3.53 1 .11-.8.44-1.3.8-1.6-2.67-.3-5.47-1.33-5.47-5.93 0-1.31.47-2.38 1.24-3.22-.12-.3-.54-1.52.12-3.18 0 0 1.01-.32 3.3 1.23a11.5 11.5 0 0 1 6 0c2.28-1.55 3.28-1.23 3.28-1.23.66 1.66.24 2.88.12 3.18a4.6 4.6 0 0 1 1.24 3.22c0 4.61-2.81 5.63-5.48 5.92.43.37.8 1.1.8 2.22v3.3c0 .31.22.69.82.57A12 12 0 0 0 12 0z"/></svg></el-icon>
        </a>
        <a href="mailto&#58;admin@xiaolu.college" class="social-link" title="Email">
          <el-icon :size="20"><Message /></el-icon>
        </a>
      </div>
    </div>

    <div class="about-cards">
      <div class="about-card">
        <div class="about-card-icon">👋</div>
        <h3>关于我</h3>
        <p>{{ settings.about_bio }}</p>
      </div>
      <div class="about-card">
        <div class="about-card-icon">💻</div>
        <h3>技术栈</h3>
        <div class="skill-tags">
          <span class="skill-tag" v-for="s in skills" :key="s">{{ s }}</span>
        </div>
      </div>
    </div>

    <div class="about-card guestbook-card">
      <div class="about-card-icon">💬</div>
      <h3>留言板</h3>
      <CommentSection :article-id="0" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Message } from '@element-plus/icons-vue'
import api from '@/api'
import CommentSection from '@/components/CommentSection.vue'

const settings = ref({})
const skills = computed(() => (settings.value.about_skills || '').split(',').map(s => s.trim()).filter(Boolean))

onMounted(async () => {
  try {
    const res = await api.get('/settings')
    if (res.data) settings.value = res.data
  } catch (e) { /* ignore */ }
})
</script>

<style scoped>
.about-page { max-width: 700px; margin: 0 auto; }
.about-hero { text-align: center; padding: 40px 20px 32px; }
.about-avatar { border: 4px solid var(--border-color); box-shadow: 0 4px 20px rgba(0,0,0,0.08); }
.about-hero h1 { font-size: 1.8rem; font-weight: 800; margin-top: 16px; color: var(--text-primary); }
.about-tagline { font-size: 1.05rem; color: var(--text-secondary); margin-top: 4px; }
.about-social { display: flex; gap: 10px; justify-content: center; margin-top: 16px; }
.social-link { width: 40px; height: 40px; border-radius: 50%; background: var(--bg-card); border: 1px solid var(--border-color); display: flex; align-items: center; justify-content: center; color: var(--text-muted); transition: all 0.2s; }
.social-link:hover { color: var(--accent); border-color: var(--accent); transform: translateY(-2px); }
.about-cards { display: flex; flex-direction: column; gap: 16px; }
.about-card { background: var(--bg-card); border: 1px solid var(--border-color); border-radius: 16px; padding: 24px; transition: border-color 0.2s; }
.about-card:hover { border-color: var(--accent-light); }
.about-card-icon { font-size: 1.6rem; margin-bottom: 8px; }
.about-card h3 { font-size: 1.1rem; font-weight: 700; margin-bottom: 8px; color: var(--text-primary); }
.about-card p { color: var(--text-secondary); line-height: 1.8; font-size: 0.95rem; white-space: pre-line; }
.skill-tags { display: flex; flex-wrap: wrap; gap: 8px; margin-top: 4px; }
.skill-tag { font-size: 0.82rem; padding: 4px 14px; border-radius: 20px; background: var(--tag-bg); color: var(--tag-text); border: 1px solid transparent; }
.skill-tag:hover { border-color: var(--accent); }
.twikoo-wrap { margin-top: 8px; }
@media (max-width: 768px) { .about-hero { padding: 24px 16px; } .about-hero h1 { font-size: 1.5rem; } .about-card { padding: 18px; } }
</style>
