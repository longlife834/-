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
**前端**: `cd vue` 然后 `npm install && npm run dev`

## ✨ 核心功能
用户注册/登录 (JWT 认证)
<img width="2539" height="1272" alt="image" src="https://github.com/user-attachments/assets/19b4e0c2-3877-47f9-a38b-c258f2995d67" />

发布/管理失物与招领
<img width="2164" height="732" alt="image" src="https://github.com/user-attachments/assets/ecacf257-149e-49ee-a6d3-11c20c029426" />
基于 AI 的智能匹配
<img width="634" height="428" alt="image" src="https://github.com/user-attachments/assets/bae38ef3-de92-44b4-8d2a-8a70f640e429" />
用户间实时聊天 (WebSocket)
<img width="1946" height="1026" alt="image" src="https://github.com/user-attachments/assets/bdb75ab1-70a7-4cf0-a33c-13457bbce799" />

