import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'Home', component: () => import('@/views/Home.vue') },
    { path: '/articles', name: 'Articles', component: () => import('@/views/ArticleList.vue') },
    { path: '/article/:id', name: 'ArticleDetail', component: () => import('@/views/ArticleDetail.vue') },
    { path: '/archives', name: 'Archives', component: () => import('@/views/Archives.vue') },
    { path: '/categories', name: 'Categories', component: () => import('@/views/Categories.vue') },
    { path: '/tags', name: 'Tags', component: () => import('@/views/Tags.vue') },
    { path: '/friends', name: 'Friends', component: () => import('@/views/Friends.vue') },
    { path: '/about', name: 'About', component: () => import('@/views/About.vue') },
    { path: '/search', name: 'Search', component: () => import('@/views/Search.vue') },
    { path: '/admin/login', name: 'AdminLogin', component: () => import('@/views/admin/Login.vue') },
    {
      path: '/admin',
      component: () => import('@/views/admin/Layout.vue'),
      meta: { auth: true },
      children: [
        { path: '', redirect: '/admin/dashboard' },
        { path: 'dashboard', name: 'AdminDashboard', component: () => import('@/views/admin/Dashboard.vue') },
        { path: 'articles', name: 'AdminArticles', component: () => import('@/views/admin/Articles.vue') },
        { path: 'categories', name: 'AdminCategories', component: () => import('@/views/admin/Categories.vue') },
        { path: 'tags', name: 'AdminTags', component: () => import('@/views/admin/Tags.vue') },
        { path: 'friends', name: 'AdminFriends', component: () => import('@/views/admin/Friends.vue') },
        { path: 'comments', name: 'AdminComments', component: () => import('@/views/admin/Comments.vue') },
        { path: 'settings', name: 'AdminSettings', component: () => import('@/views/admin/Settings.vue') }
      ]
    },
    { path: '/:pathMatch(.*)*', name: 'NotFound', component: () => import('@/views/NotFound.vue') }
  ],
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.meta.auth && !userStore.isAdmin()) {
    next('/admin/login')
  } else {
    next()
  }
})

export default router
