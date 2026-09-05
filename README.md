# 书库 · 图书管理系统

## 启动前端
```bash
npm install
npm run dev
```

## 启动后端
```bash
cd backend
mvn spring-boot:run
```

前端默认访问 `http://localhost:5173`，后端 API 为 `http://localhost:8080/api/books`。后端使用 H2 文件数据库，数据保存在 `backend/data`。
