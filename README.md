# 校园失物招领系统
基于 Spring Boot 3 + Vue 3 的微服务实战项目

## 📖 项目简介
一个帮助高校师生发布和认领失物的平台，包含用户认证、失物/招领发布、智能匹配、实时聊天等核心功能。

## 🛠 技术栈
**后端**: Spring Boot 3, Spring Cloud, MyBatis, JWT, WebSocket
**前端**: Vue 3, Vite, Pinia, Vue Router
**基础设施**: Nacos, MySQL, Docker

## 如何运行
**后端**: 依次启动 Nacos、MySQL 和各微服务模块
![Uploading image.png…]()

**前端**: `cd vue` 然后 `npm install && npm run dev`

## ✨ 核心功能
用户注册/登录 (JWT 认证)
发布/管理失物与招领
基于 AI 的智能匹配
用户间实时聊天 (WebSocket)
