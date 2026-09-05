# 图书馆管理系统

一个基于 Vue 3 + Vite 前端和 Spring Boot + MyBatis 后端的图书管理系统，支持登录注册、图书管理、分类筛选、借阅和归还。

GitHub 页面只能查看源码，项目需要在本地启动后使用。

## 环境要求

- Node.js 18 或更高版本
- Java 8 或更高版本
- Maven 3.8 或更高版本
- MySQL 8.0 或更高版本

## 获取项目

```bash
git clone https://github.com/xiaobai233-hub/Library-management.git
cd Library-management
```

## 初始化数据库

启动 MySQL 后，执行 `backend/sql/library_new.sql`，然后根据本机配置修改 `backend/src/main/resources/application.properties` 中的数据库用户名和密码。

## 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端地址：`http://localhost:8080`

## 启动前端

打开另一个终端，在项目根目录执行：

```bash
npm install
npm run dev
```

浏览器访问：`http://localhost:5173`

Windows 用户也可以双击 `启动项目.vbs`，隐藏启动前后端服务。

## 常用命令

```bash
# 前端生产构建
npm run build

# 后端打包
cd backend
mvn package
```

## 使用说明

- 首次打开页面需要登录或注册账号。
- 管理员可以新增、编辑和删除图书。
- 普通用户可以浏览、借阅和归还图书。
