<template>
  <div class="about-page">
    <!-- 头像区 -->
    <div class="about-hero">
      <el-avatar :size="96" src="https://i.postimg.cc/y8Q59mgL/images-2.jpg" class="about-avatar" />
      <h1>🦌 小鹿</h1>
      <p class="about-tagline">生活明朗，万物可爱</p>
      <div class="about-social">
        <a href="https://github.com/xiaolu" target="_blank" rel="noopener" class="social-link" title="GitHub">
          <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor"><path d="M12 0C5.37 0 0 5.37 0 12c0 5.31 3.435 9.795 8.205 11.385.6.105.825-.255.825-.57 0-.285-.015-1.23-.015-2.235-3.015.555-3.795-.735-4.035-1.41-.135-.345-.72-1.41-1.23-1.695-.42-.225-1.02-.78-.015-.795.945-.015 1.62.87 1.845 1.23 1.08 1.815 2.805 1.305 3.495.99.105-.78.42-1.305.765-1.605-2.67-.3-5.46-1.335-5.46-5.925 0-1.305.465-2.385 1.23-3.225-.12-.3-.54-1.53.12-3.18 0 0 1.005-.315 3.3 1.23.96-.27 1.98-.405 3-.405s2.04.135 3 .405c2.295-1.56 3.3-1.23 3.3-1.23.66 1.65.24 2.88.12 3.18.765.84 1.23 1.905 1.23 3.225 0 4.605-2.805 5.625-5.475 5.925.435.375.81 1.095.81 2.22 0 1.605-.015 2.895-.015 3.3 0 .315.225.69.825.57A12.02 12.02 0 0024 12c0-6.63-5.37-12-12-12z"/></svg>
        </a>
        <a href="mailto:admin@xiaolu.college" class="social-link" title="Email">
          <el-icon :size="20"><Message /></el-icon>
        </a>
      </div>
    </div>

    <!-- 介绍卡片 -->
    <div class="about-cards">
      <div class="about-card">
        <div class="about-card-icon">👋</div>
        <h3>关于我</h3>
        <p>一名热爱技术的大学生，喜欢前端开发和全栈技术。这里是我的个人博客，记录学习心得和生活日常。希望在这里能遇到志同道合的朋友，一起学习、一起成长。</p>
      </div>
      <div class="about-card">
        <div class="about-card-icon">💻</div>
        <h3>技术栈</h3>
        <div class="skill-tags">
          <span class="skill-tag">Vue.js</span>
          <span class="skill-tag">Java</span>
          <span class="skill-tag">SpringBoot</span>
          <span class="skill-tag">MySQL</span>
          <span class="skill-tag">Redis</span>
          <span class="skill-tag">Docker</span>
          <span class="skill-tag">HTML/CSS</span>
          <span class="skill-tag">JavaScript</span>
        </div>
      </div>
    </div>

    <!-- 留言板 -->
    <div class="about-card guestbook-card">
      <div class="about-card-icon">💬</div>
      <h3>留言板</h3>
      <div id="twikoo" class="twikoo-wrap"></div>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { Message } from '@element-plus/icons-vue'

onMounted(() => {
  const script = document.createElement('script')
  script.src = 'https://cdn.staticfile.org/twikoo/1.6.39/twikoo.all.min.js'
  script.onload = () => { window.twikoo?.init({ envId: 'your-twikoo-env-id', el: '#twikoo' }) }
  document.body.appendChild(script)
})
</script>

<style scoped>
.about-page { max-width: 700px; margin: 0 auto; }
.about-hero { text-align: center; padding: 40px 20px 32px; }
.about-avatar { border: 4px solid var(--border-color); box-shadow: 0 4px 20px rgba(0,0,0,0.08); }
.about-hero h1 { font-size: 1.8rem; font-weight: 800; margin-top: 16px; color: var(--text-primary); }
.about-tagline { font-size: 1.05rem; color: var(--text-secondary); margin-top: 4px; }
.about-social { display: flex; gap: 10px; justify-content: center; margin-top: 16px; }
.social-link {
  width: 40px; height: 40px; border-radius: 50%;
  background: var(--bg-card); border: 1px solid var(--border-color);
  display: flex; align-items: center; justify-content: center;
  color: var(--text-muted); transition: all 0.2s;
}
.social-link:hover { color: var(--accent); border-color: var(--accent); transform: translateY(-2px); }

.about-cards { display: flex; flex-direction: column; gap: 16px; }
.about-card {
  background: var(--bg-card); border: 1px solid var(--border-color);
  border-radius: 16px; padding: 24px;
  transition: border-color 0.2s;
}
.about-card:hover { border-color: var(--accent-light); }
.about-card-icon { font-size: 1.6rem; margin-bottom: 8px; }
.about-card h3 { font-size: 1.1rem; font-weight: 700; margin-bottom: 8px; color: var(--text-primary); }
.about-card p { color: var(--text-secondary); line-height: 1.8; font-size: 0.95rem; }

.skill-tags { display: flex; flex-wrap: wrap; gap: 8px; margin-top: 4px; }
.skill-tag {
  font-size: 0.82rem; padding: 4px 14px; border-radius: 20px;
  background: var(--tag-bg); color: var(--tag-text);
  border: 1px solid transparent;
}
.skill-tag:hover { border-color: var(--accent); }

.twikoo-wrap { margin-top: 8px; }

@media (max-width: 768px) {
  .about-hero { padding: 24px 16px; }
  .about-hero h1 { font-size: 1.5rem; }
  .about-card { padding: 18px; }
}
</style>
