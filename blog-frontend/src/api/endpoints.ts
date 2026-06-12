import api from './index'

export const authApi = {
  login: (data: { username: string; password: string }) => api.post('/auth/login', data),
  register: (data: { username: string; password: string; email: string }) => api.post('/auth/register', data),
  check: () => api.get('/auth/check')
}

export const articleApi = {
  list: (params: any) => api.get('/articles', { params }),
  top: () => api.get('/articles/top'),
  detail: (id: number) => api.get(`/articles/${id}`),
  // Admin
  adminList: (params: any) => api.get('/admin/articles', { params }),
  create: (data: any) => api.post('/admin/articles', data),
  update: (id: number, data: any) => api.put(`/admin/articles/${id}`, data),
  delete: (id: number) => api.delete(`/admin/articles/${id}`)
}

export const categoryApi = {
  list: () => api.get('/categories'),
  adminList: () => api.get('/admin/categories'),
  create: (data: any) => api.post('/admin/categories', data),
  update: (id: number, data: any) => api.put(`/admin/categories/${id}`, data),
  delete: (id: number) => api.delete(`/admin/categories/${id}`)
}

export const tagApi = {
  list: () => api.get('/tags'),
  adminList: () => api.get('/admin/tags'),
  create: (data: any) => api.post('/admin/tags', data),
  update: (id: number, data: any) => api.put(`/admin/tags/${id}`, data),
  delete: (id: number) => api.delete(`/admin/tags/${id}`)
}

export const commentApi = {
  listByArticle: (articleId: number, page: number) => api.get(`/comments/article/${articleId}`, { params: { page } }),
  replies: (parentId: number) => api.get(`/comments/replies/${parentId}`),
  create: (data: any) => api.post('/comments', data)
}

export const friendApi = {
  list: () => api.get('/friends'),
  apply: (data: any) => api.post('/friends/apply', data),
  adminList: () => api.get('/admin/friends'),
  approve: (id: number) => api.put(`/admin/friends/${id}/approve`),
  delete: (id: number) => api.delete(`/admin/friends/${id}`)
}

export const searchApi = {
  search: (keyword: string, page: number) => api.get('/search', { params: { keyword, page } })
}

export const uploadApi = {
  upload: (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('/admin/upload', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
  }
}
