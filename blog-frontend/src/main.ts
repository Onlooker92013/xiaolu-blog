import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { createI18n } from 'vue-i18n'
import zhCN from './locales/zh-CN.json'
import enUS from './locales/en-US.json'
import App from './App.vue'
import router from './router'
import './assets/styles/main.css'

const i18n = createI18n({
  legacy: false,
  locale: localStorage.getItem('lang') || 'zh-CN',
  fallbackLocale: 'zh-CN',
  messages: {
    'zh-CN': zhCN,
    'en-US': enUS
  }
})

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(i18n)
app.use(ElementPlus, { size: 'default' })

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')

// Dynamic favicon & title from site settings
fetch('/api/settings')
  .then(r => r.json())
  .then(res => {
    const s = res.data || {}
    if (s.site_name) document.title = s.site_name
    if (s.avatar_url) {
      const link = document.querySelector('link[rel="icon"]') as HTMLLinkElement
      if (link) link.href = s.avatar_url
    }
  })
  .catch(() => {})
