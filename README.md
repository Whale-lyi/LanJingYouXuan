# 蓝鲸优选

## 项目介绍
本项目是以用户点评和商户信息为主要内容的本地生活服务平台，实现了短信登陆、探店点评、商品秒杀、每日签到、好友关注等多个模块

## 技术栈
SpringBoot、MySQL、MyBatis-Plus、Redis

## 细节描述

- **登陆注册**：使用Redis存储验证码、Token，自定义拦截器完成用户认证，通过双重拦截器解决Token刷新问题
- **缓存**：系统中采取主动更新配合超时剔除的缓存更新方案，互斥锁/逻辑过期解决缓存击穿，以及缓存穿透、缓存雪崩等问题
- **商品秒杀**：基于Redis自增实现全局唯一ID，使用乐观锁解决超卖问题，通过分布式锁 Redisson 实现一人一单
