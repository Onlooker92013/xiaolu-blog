# 🦌 小鹿博客 - 全栈个人博客系统

从零手写的全栈博客系统，前后端分离架构，支持中英文切换和深色模式。

## 技术栈

| 层级 | 技术 |
|------|------|
| **前端** | Vue 3 + Vite + Pinia + Vue Router + Element Plus + vue-i18n + marked + highlight.js |
| **后端** | SpringBoot 3.2 + MyBatis-Plus + Spring Security + JWT + Knife4j |
| **数据库** | MySQL 8.0 + Redis |
| **评论** | Twikoo（第三方自部署） |
| **部署** | Docker Compose + Nginx |

## 项目结构

```
xiaolu-blog/
├── blog-frontend/          # Vue 3 前端
│   ├── src/
│   │   ├── api/            # API 请求封装
│   │   ├── assets/         # 样式/图片
│   │   ├── components/     # 公共组件
│   │   ├── locales/        # 中英文语言包
│   │   ├── router/         # 路由
│   │   ├── stores/         # Pinia 状态管理
│   │   └── views/          # 页面（前台+后台）
│   ├── Dockerfile
│   └── nginx.conf
├── blog-backend/           # SpringBoot 后端
│   ├── src/main/java/com/xiaolu/blog/
│   │   ├── common/         # 统一返回/异常处理
│   │   ├── config/         # Spring Security/JWT/CORS/MyBatis
│   │   ├── controller/     # REST 接口
│   │   ├── dto/            # 数据传输对象
│   │   ├── entity/         # 数据库实体
│   │   ├── mapper/         # MyBatis-Plus Mapper
│   │   └── service/        # 业务逻辑
│   └── src/main/resources/
│       ├── application.yml
│       └── init.sql        # 数据库初始化脚本
└── docker-compose.yml      # 一键部署
```

## 功能清单

### 前台（中英文双语）
- ✅ 首页：粒子背景 + 分类导航 + 推荐/最新文章
- ✅ 文章列表：分页、分类筛选
- ✅ 文章详情：Markdown 渲染 + 代码高亮 + Twikoo 评论
- ✅ 归档页：时间线展示
- ✅ 分类/标签页
- ✅ 友链页 + 友链申请
- ✅ 关于页 + 留言板
- ✅ 全文搜索
- ✅ 深色/浅色模式切换
- ✅ 中英文语言切换
- ✅ RSS 订阅
- ✅ 移动端适配

### 后台（中文）
- ✅ 文章管理：新建/编辑/删除 + Markdown 编辑器
- ✅ 分类/标签管理
- ✅ 友链审核
- ✅ 图片上传
- ✅ JWT 登录认证

## 快速开始

### 1. 启动数据库
```bash
docker-compose up -d mysql redis
```

### 2. 启动后端
```bash
cd blog-backend
mvn clean package -DskipTests
java -jar target/blog-1.0.0.jar
```

### 3. 启动前端
```bash
cd blog-frontend
npm install
npm run dev
```

### 4. 访问
- 前台: http://localhost:5173
- 后台: http://localhost:5173/admin/login
- API 文档: http://localhost:9090/doc.html

### 一键部署
```bash
docker-compose up -d
```

## 默认账号
- 用户名: `admin`
- 密码: `admin123`
