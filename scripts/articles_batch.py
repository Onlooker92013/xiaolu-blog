#!/usr/bin/env python3
import requests, json

BASE = "http://localhost:9090"
r = requests.post(f"{BASE}/api/auth/login", json={"username": "admin", "password": "admin123"})
TOKEN = r.json()["data"]["token"]
HEADERS = {"Content-Type": "application/json", "Authorization": f"Bearer {TOKEN}"}

articles = [
    {
        "title": "SpringBoot 3 + Vue 3 全栈博客从零搭建实战",
        "titleEn": "Building a Full-Stack Blog with SpringBoot 3 and Vue 3 from Scratch",
        "summary": "从项目架构设计、技术选型到完整实现，详细讲解如何使用 SpringBoot 3 + Vue 3 + MySQL + Redis 搭建一个支持中英文切换、深色模式的全栈博客系统。涵盖 JWT 认证、RBAC 权限控制、MyBatis-Plus 分页、Docker Compose 部署等企业级实践。",
        "summaryEn": "A comprehensive guide to building a production-ready full-stack blog covering architecture, security, i18n, dark mode, and Docker deployment.",
        "content": "# SpringBoot 3 + Vue 3 全栈博客从零搭建实战\n\n## 一、项目概述\n\n本文完整记录一个企业级全栈博客系统的搭建过程，涵盖前后端分离架构设计、数据库设计、接口开发、前端实现及容器化部署。\n\n### 技术选型\n\n| 层级 | 技术 | 选型理由 |\n|------|------|------|\n| 前端 | Vue 3 + Vite + Pinia | Composition API、按需编译 |\n| UI 框架 | Element Plus | 组件丰富，文档完善 |\n| 国际化 | vue-i18n | 轻量，配置简单 |\n| 后端 | SpringBoot 3.2 | 生态成熟，约定大于配置 |\n| ORM | MyBatis-Plus | 分页插件、减少80%样板代码 |\n| 安全 | Spring Security + JWT | 无状态认证 |\n| 数据库 | MySQL 8.0 + Redis | 主存储 + 缓存 |\n| 部署 | Docker Compose + Nginx | 一键部署 |\n\n## 二、架构设计\n\n```\nNginx (:80) -> Vue SPA (静态资源)\nNginx (:80) -> /api/* -> SpringBoot (:9090) -> MySQL + Redis\n```\n\n### 分层架构\n\nController -> Service -> Mapper -> DB，DTO/VO 隔离接口与实体。\n\n## 三、核心功能实现\n\n### 3.1 JWT 认证与 RBAC\n\n登录成功后签发包含 userId、username、role 的 JWT Token，自定义过滤器解析后注入 SecurityContext，管理接口使用 hasAuthority(\"ROLE_ADMIN\") 保护。\n\n### 3.2 中英文双语\n\n数据库文章表包含 title/titleEn、content/contentEn 字段对，前端 vue-i18n 切换语言后 API 返回对应内容。\n\n### 3.3 深色模式\n\nCSS 变量定义两套配色，Pinia store 管理主题状态，一键切换全局主题。\n\n## 四、性能优化\n\n- MySQL 全文索引加速搜索\n- MyBatis-Plus 分页插件避免全表扫描\n- Redis 缓存热点文章\n- 前端路由懒加载 + 图片懒加载\n- Vite Tree Shaking 减小打包体积\n\n## 五、安全防护\n\n- SQL 注入：MyBatis-Plus 参数化查询天然防护\n- XSS：Tomcat RFC 7230 拒绝非法字符，marked 渲染转义\n- 权限绕过：每个接口校验 ROLE_ADMIN\n- 文件上传：白名单后缀 + 大小限制 10MB\n\n## 六、部署\n\nDocker Compose 编排四服务，一条命令 docker-compose up -d 完成部署。\n\n> 完整源码：[GitHub](https://github.com/xiaolu/blog)",
        "contentEn": "## Overview\n\nA complete guide to building a production-ready full-stack blog with SpringBoot 3, Vue 3, MySQL, and Redis. Covers architecture design, JWT authentication, i18n, dark mode, security hardening, and Docker deployment.",
        "categoryId": 1,
        "tagIds": [1, 2, 3, 5, 7],
        "status": "PUBLISHED",
        "isTop": 1
    },
    {
        "title": "JWT 无状态认证与 Spring Security 权限控制深度解析",
        "titleEn": "Deep Dive into JWT Stateless Authentication and Spring Security Authorization",
        "summary": "深入分析 JWT 令牌结构、签名验证机制、Token 刷新策略，以及如何结合 Spring Security 实现细粒度的 RBAC 权限控制。包含常见安全漏洞案例分析及防御方案。",
        "summaryEn": "In-depth analysis of JWT structure, HMAC verification, refresh strategies, and RBAC implementation with Spring Security.",
        "content": "# JWT 无状态认证与 Spring Security 权限控制深度解析\n\n## 一、为什么选择 JWT？\n\n在前后端分离架构中，传统 Session+Cookie 面临跨域和扩展性问题。JWT 因其无状态、自包含、跨域友好成为主流。\n\n### JWT vs Session\n\n| 维度 | JWT | Session |\n|------|-----|------|\n| 存储位置 | 客户端 | 服务端 |\n| 扩展性 | 天然分布式 | 需共享 Session |\n| 跨域 | 优秀 | 需额外配置 |\n| 注销 | 需配合黑名单 | 直接销毁 |\n\n## 二、JWT 令牌结构\n\n```\nHeader.Payload.Signature\nHeader:  {\"alg\":\"HS512\",\"typ\":\"JWT\"}\nPayload: {\"userId\":1,\"username\":\"admin\",\"role\":\"ADMIN\",\"exp\":...}\nSignature: HMAC-SHA512(base64(Header)+\".\"+base64(Payload), secret)\n```\n\n## 三、Spring Security 集成\n\n```java\n@Bean\npublic SecurityFilterChain filterChain(HttpSecurity http) {\n    http.csrf(csrf -> csrf.disable())\n        .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))\n        .authorizeHttpRequests(auth -> auth\n            .requestMatchers(\"/api/admin/**\").hasAuthority(\"ROLE_ADMIN\")\n            .requestMatchers(\"/api/public/**\").permitAll()\n            .anyRequest().authenticated()\n        )\n        .addFilterBefore(new JwtAuthFilter(), \n            UsernamePasswordAuthenticationFilter.class);\n    return http.build();\n}\n```\n\n关键设计：Token 携带 role -> 过滤器设置 GrantedAuthority -> hasAuthority 校验。\n\n## 四、安全漏洞与防御\n\n| 攻击类型 | 风险 | 防御措施 |\n|------|------|------|\n| Token 泄露 | 高 | HTTPS + 短过期时间 + 黑名单 |\n| 弱签名密钥 | 高 | >=256bit 随机密钥，生产环境从密钥服务获取 |\n| XSS 窃取 | 中 | 内存存储 Token，不存 localStorage |\n| CSRF | 低 | 无状态 API 天然免疫 CSRF |\n| 权限绕过 | 严重 | 严格 RBAC，每个接口校验角色 |\n\n## 五、最佳实践\n\n1. Access Token 15分钟 + Refresh Token 7天，前端拦截 401 自动刷新\n2. Redis 维护 Token 黑名单实现登出\n3. 密钥通过环境变量注入，不硬编码\n4. 每个设备独立 Token，支持多设备登录\n\n## 六、总结\n\nJWT + Spring Security 为前后端分离提供强大安全方案。正确理解 JWT 特性，结合 RBAC，做好攻击防御，方能构建安全可靠系统。",
        "contentEn": "## Overview\n\nA thorough analysis of JWT authentication and Spring Security authorization patterns for modern web applications.",
        "categoryId": 4,
        "tagIds": [2, 8],
        "status": "PUBLISHED",
        "isTop": 1
    },
    {
        "title": "MySQL 性能优化实战：索引、慢查询与读写分离",
        "titleEn": "MySQL Performance Optimization: Indexing, Slow Queries, and Read-Write Splitting",
        "summary": "从索引原理到慢查询分析，从 SQL 优化到读写分离架构，系统讲解 MySQL 性能优化的核心方法。包含 EXPLAIN 执行计划解读、索引失效场景汇总、分库分表策略。",
        "summaryEn": "Systematic MySQL optimization covering B+Tree indexing, EXPLAIN analysis, slow query tuning, and read-write splitting architecture.",
        "content": "# MySQL 性能优化实战：索引、慢查询与读写分离\n\n## 一、索引原理\n\nInnoDB 使用 B+Tree 索引：所有数据存于叶子节点，非叶子仅存键值，叶子双向链表支持范围查询，树高通常 2-4 层，查找 O(log n)。\n\n### 索引类型速查\n\n| 类型 | 特点 | 适用场景 |\n|------|------|------|\n| 主键索引 | 聚簇索引 | 主键查询 |\n| 普通索引 | 非聚簇，叶子存主键 | 等值/范围 |\n| 联合索引 | 多列组合，最左前缀 | 多条件查询 |\n| 全文索引 | 自然语言搜索 | 文章内容搜索 |\n| 唯一索引 | 保证唯一性 | 用户名/邮箱 |\n\n## 二、EXPLAIN 执行计划\n\n```sql\nEXPLAIN SELECT * FROM articles WHERE status='PUBLISHED' ORDER BY created_at DESC;\n```\n\n关键字段：type（const > eq_ref > ref > range > index > ALL）、key（使用的索引）、rows（扫描行数）、Extra（Using index 最优，Using filesort 需优化）。\n\n## 三、索引失效场景\n\n- WHERE 使用函数：YEAR(created_at)=2026 ❌\n- 联合索引不满足最左前缀 ❌\n- LIKE 以 % 开头不走索引 ❌\n- OR 条件非所有列有索引 ❌\n\n## 四、慢查询优化流程\n\n开启慢查询日志 -> EXPLAIN 分析 -> 优化索引或 SQL -> 验证效果。工具：mysqldumpslow、pt-query-digest。\n\n## 五、读写分离\n\n应用 -> 路由中间件(ShardingSphere-JDBC) -> Master(写) + Slave(读)。主从复制通过 binlog 同步。\n\n```yaml\nspring:\n  shardingsphere:\n    rules:\n      readwrite-splitting:\n        data-sources:\n          ds:\n            write-data-source-name: master\n            read-data-source-names: [slave0, slave1]\n```\n\n## 六、总结\n\n遵循\"索引优先 -> SQL优化 -> 架构升级\"路径。每日开发养成 EXPLAIN 分析习惯，80% 性能问题迎刃而解。",
        "contentEn": "## Overview\n\nA systematic approach to MySQL optimization covering B+Tree indexing, EXPLAIN execution plan analysis, and read-write splitting for production databases.",
        "categoryId": 4,
        "tagIds": [4, 9],
        "status": "PUBLISHED",
        "isTop": 0
    },
    {
        "title": "Docker Compose 一键部署全栈应用完全指南",
        "titleEn": "Complete Guide to One-Click Full-Stack Deployment with Docker Compose",
        "summary": "从 Dockerfile 编写到 docker-compose.yml 编排，从镜像优化到数据持久化，详细讲解如何用 Docker Compose 实现 SpringBoot + Vue + MySQL + Redis + Nginx 全套服务的容器化部署。",
        "summaryEn": "Complete guide to containerizing and deploying a full-stack app stack with Docker Compose, multi-stage builds, health checks, and optimization.",
        "content": "# Docker Compose 一键部署全栈应用完全指南\n\n## 一、核心概念\n\n| 概念 | 说明 |\n|------|------|\n| Image | 应用及环境的只读模板 |\n| Container | 镜像运行实例，隔离进程空间 |\n| Volume | 持久化存储，容器删除数据不丢 |\n| Network | 容器间通信的虚拟网络 |\n| Compose | YAML 定义多容器编排 |\n\n## 二、多阶段构建\n\n```dockerfile\n# 后端 Dockerfile\nFROM maven:3.9-eclipse-temurin-17 AS builder\nCOPY src ./src\nRUN mvn package -DskipTests\n\nFROM eclipse-temurin:17-jre-alpine\nCOPY --from=builder /app/target/*.jar app.jar\nENTRYPOINT [\"java\",\"-jar\",\"app.jar\"]\n```\n\n```dockerfile\n# 前端 Dockerfile\nFROM node:18-alpine AS builder\nCOPY package*.json ./\nRUN npm ci && npm run build\n\nFROM nginx:alpine\nCOPY --from=builder /app/dist /usr/share/nginx/html\nCOPY nginx.conf /etc/nginx/conf.d/default.conf\n```\n\n效果：构建镜像从 600MB 降到 200MB，仅保留运行时依赖。\n\n## 三、Docker Compose 编排\n\n```yaml\nservices:\n  mysql:\n    image: mysql:8.0\n    environment:\n      MYSQL_ROOT_PASSWORD: ${MYSQL_PASSWORD}\n    volumes: [mysql_data:/var/lib/mysql]\n    healthcheck:\n      test: [\"CMD\",\"mysqladmin\",\"ping\"]\n  redis:\n    image: redis:7-alpine\n  backend:\n    build: ./blog-backend\n    depends_on:\n      mysql: { condition: service_healthy }\n  frontend:\n    build: ./blog-frontend\n    ports: [\"80:80\"]\n```\n\n## 四、常用命令\n\n```bash\ndocker-compose up -d           # 后台启动\ndocker-compose up -d --build   # 重建并启动\ndocker-compose ps               # 查看状态\ndocker-compose logs -f backend  # 查看日志\ndocker-compose down -v          # 停止清理\n```\n\n## 五、优化技巧\n\n| 技巧 | 效果 |\n|------|------|\n| 多阶段构建 | 600MB -> 200MB |\n| Alpine 基础镜像 | 比 Ubuntu 小 100MB+ |\n| .dockerignore | 排除 node_modules, target |\n| COPY package.json 先于源码 | 利用 Docker 层缓存 |\n| Healthcheck | 自动检测服务可用性 |\n\n## 六、总结\n\nDocker Compose 让复杂的多服务部署变得简单可靠。开发环境一条命令启动，生产环境也能无缝衔接。",
        "contentEn": "## Overview\n\nComplete guide to deploying a full-stack application with Docker Compose. Covers multi-stage builds, health checks, data persistence, networking, and optimization techniques.",
        "categoryId": 4,
        "tagIds": [5, 7],
        "status": "PUBLISHED",
        "isTop": 0
    },
    {
        "title": "Vue 3 Composition API 高级模式与状态管理最佳实践",
        "titleEn": "Vue 3 Composition API Advanced Patterns and State Management Best Practices",
        "summary": "深入探讨 Vue 3 Composition API 的设计理念，包括可组合函数模式、Provide/Inject 依赖注入、Pinia Setup Store 风格，以及大型项目中组件拆分与逻辑复用的真实案例。",
        "summaryEn": "Explore Vue 3 Composition API composables, Pinia state management patterns, and scalable component architecture for large applications.",
        "content": "# Vue 3 Composition API 高级模式与状态管理最佳实践\n\n## 一、设计哲学\n\nComposition API 解决 Options API 两大痛点：\n1. 逻辑分散：同一功能代码分散在 data/methods/computed 中\n2. 复用困难：Mixin 存在命名冲突、来源不清晰等问题\n\n## 二、Composables 模式\n\n```typescript\n// composables/useCounter.ts\nexport function useCounter(initial = 0) {\n  const count = ref(initial)\n  const double = computed(() => count.value * 2)\n  const increment = () => count.value++\n  return { count, double, increment }\n}\n\n// 组件中使用\nconst { count, double, increment } = useCounter(10)\n```\n\n## 三、Pinia Setup Store（推荐）\n\n```typescript\nexport const useUserStore = defineStore('user', () => {\n  const token = ref(localStorage.getItem('token') || '')\n  const isLoggedIn = computed(() => !!token.value)\n  \n  const login = async (u: string, p: string) => {\n    const res = await authApi.login({ username: u, password: p })\n    token.value = res.token\n    localStorage.setItem('token', res.token)\n  }\n  \n  const logout = () => {\n    token.value = ''\n    localStorage.removeItem('token')\n  }\n  \n  return { token, isLoggedIn, login, logout }\n})\n```\n\n## 四、跨组件通信策略\n\n| 场景 | 方案 |\n|------|------|\n| 父子通信 | Props + Emits |\n| 跨层级 | Provide/Inject |\n| 全局状态 | Pinia Store |\n| 临时状态 | Composable 函数 |\n\n## 五、项目结构最佳实践\n\n```\nsrc/\n├── views/          # 页面级组件\n├── components/     # 通用组件\n├── composables/    # 可组合函数\n├── stores/         # Pinia 状态\n├── api/            # HTTP 请求封装\n└── locales/        # i18n 语言包\n```\n\n## 六、总结\n\nComposition API 不是 Options API 的替代品，而是更适合大型项目的范式。通过 Composable 实现逻辑复用、Pinia 管理全局状态、合理拆分组件，能显著提升可维护性和开发效率。",
        "contentEn": "## Overview\n\nDeep dive into Vue 3 Composition API composables, Pinia Setup Store patterns, provide/inject dependency injection, and scalable component architecture for large applications.",
        "categoryId": 1,
        "tagIds": [3, 7],
        "status": "PUBLISHED",
        "isTop": 0
    }
]

for i, a in enumerate(articles):
    r = requests.post(f"{BASE}/api/admin/articles", json=a, headers=HEADERS)
    if r.status_code == 200:
        print(f"OK {i+1}: {a['title']}")
    else:
        print(f"FAIL {i+1}: {r.status_code} {r.text[:200]}")

print(f"\nDone! {len(articles)} articles created successfully.")
