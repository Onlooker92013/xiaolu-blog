import { defineStore } from 'pinia'
import { ref } from 'vue'
import { authApi } from '@/api/endpoints'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const username = ref(localStorage.getItem('username') || '')
  const role = ref(localStorage.getItem('role') || '')
  const userId = ref(Number(localStorage.getItem('userId')) || 0)

  const isLoggedIn = () => !!token.value
  const isAdmin = () => role.value === 'ADMIN'

  const login = async (usernameVal: string, password: string) => {
    const res = await authApi.login({ username: usernameVal, password })
    token.value = res.data.token
    username.value = res.data.username
    role.value = res.data.role
    userId.value = res.data.userId
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('username', res.data.username)
    localStorage.setItem('role', res.data.role)
    localStorage.setItem('userId', String(res.data.userId))
  }

  const logout = () => {
    token.value = ''
    username.value = ''
    role.value = ''
    userId.value = 0
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('role')
    localStorage.removeItem('userId')
  }

  return { token, username, role, userId, isLoggedIn, isAdmin, login, logout }
})
