# 基于SpringBoot的乡镇防汛资源调度管理系统

## 项目简介

本系统是一个面向乡镇级防汛部门的资源调度管理平台，采用 SpringBoot + Vue 前后端分离架构，实现防汛物资数字化管理、应急队伍快速调度、预警信息实时发布等功能，为基层防汛指挥人员提供科学的资源调配决策支持。

## 技术栈

### 后端
- **Java 17** + **Spring Boot 3.2.5**
- **Spring Security** - 认证授权（JWT + RBAC）
- **MyBatis-Plus 3.5.5** - 持久层框架
- **MySQL 8.0** - 关系型数据库
- **Redis** - 缓存
- **JWT (jjwt 0.11.5)** - 无状态Token认证
- **Hutool 5.8.22** - Java工具包
- **Maven** - 项目管理

### 前端
- **Vue 3.4** + **Vite 5.1**
- **Element Plus 2.6** - UI组件库
- **Vue Router 4.3** - 路由管理
- **Pinia 2.1** - 状态管理
- **Axios 1.6** - HTTP请求
- **ECharts 5.5** - 数据可视化

## 项目结构

```
flood-prevention-system/
├── database/
│   └── flood_prevention.sql          # 数据库建表脚本+初始数据
├── backend/                          # 后端项目
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/flood/
│       │   ├── FloodApplication.java       # 启动类
│       │   ├── config/                     # 配置类
│       │   │   ├── SecurityConfig.java     # Spring Security配置
│       │   │   ├── RedisConfig.java        # Redis序列化配置
│       │   │   ├── MyBatisPlusConfig.java  # 分页插件配置
│       │   │   └── CorsConfig.java         # 跨域配置
│       │   ├── common/                     # 通用类
│       │   │   ├── Result.java             # 统一响应
│       │   │   ├── PageResult.java         # 分页响应
│       │   │   └── Constants.java          # 常量
│       │   ├── security/                   # 安全模块
│       │   │   ├── JwtTokenProvider.java   # JWT生成验证
│       │   │   ├── JwtAuthenticationFilter.java # JWT过滤器
│       │   │   ├── LoginUser.java          # UserDetails实现
│       │   │   └── UserDetailsServiceImpl.java
│       │   ├── entity/                     # 实体类(8个)
│       │   ├── dto/                        # 数据传输对象
│       │   ├── mapper/                     # MyBatis接口(8个)
│       │   ├── service/                    # 业务逻辑层(7个Service)
│       │   │   └── impl/                   # Service实现类
│       │   └── controller/                 # 控制器(8个)
│       └── resources/
│           ├── application.yml             # 应用配置
│           └── mapper/                     # MyBatis XML映射
└── frontend/                         # 前端项目
    ├── package.json
    ├── vite.config.js
    └── src/
        ├── main.js
        ├── App.vue
        ├── api/                            # API请求封装(9个)
        ├── router/index.js                 # 路由配置+守卫
        ├── store/user.js                   # Pinia状态管理
        ├── components/Layout.vue           # 主布局
        └── views/                          # 页面(9个)
            ├── Login.vue                   # 登录
            ├── Dashboard.vue               # 首页统计
            ├── Material.vue                # 物资管理
            ├── Team.vue                    # 队伍管理
            ├── Dispatch.vue                # 调度管理
            ├── Warning.vue                 # 预警管理
            ├── User.vue                    # 用户管理
            ├── Log.vue                     # 操作日志
            └── Profile.vue                 # 个人中心
```

## 功能模块

| 模块 | 功能说明 |
|------|---------|
| 防汛物资管理 | 物资入库/出库、库存盘点、低库存预警、物资信息CRUD |
| 应急队伍管理 | 队伍信息维护、人员管理、装备登记、状态管理 |
| 资源调度决策 | 智能调度算法（就近分配+权重评分）、调度计划CRUD、状态跟踪 |
| 预警信息管理 | 预警发布、级别管理（红/橙/黄/蓝）、影响区域记录 |
| 系统权限管理 | RBAC角色权限模型、用户管理、操作日志 |
| 队伍技能管理 | 按队伍维护专业技能（堵口、搜救、抢修、急救等） |
| 站内通知 | 低库存/水位超限自动推送、未读数、标记已读 |
| 首页数据看板 | 物资/队伍/预警/调度统计、ECharts可视化图表、调度状态分布 |

## 调度算法说明

系统核心创新点是资源调度决策模块的智能调度算法：

1. **距离评分（权重40%）**：优先派遣距离受灾点最近的队伍（Haversine 公式计算直线距离，20km 以上得 0 分）
2. **队伍人数评分（权重25%）**：优先派遣人数充足的队伍
3. **装备匹配度评分（权重25%）**：根据队伍装备描述与灾害级别的匹配度评分
4. **道路状况评分（权重10%）**：道路良好 100 分、一般 60 分、较差 20 分
5. **综合评分**：四维度加权计算，选择评分最高的队伍生成调度计划，并将该队伍状态自动更新为"出动中"

## 快速开始

### 1. 数据库准备

```sql
CREATE DATABASE flood_prevention DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

然后导入建表脚本：
```bash
mysql -u root -p flood_prevention < database/flood_prevention.sql
```

如果数据库已经存在旧版结构（已有业务数据，不想重建），只执行增量升级脚本即可：
```bash
mysql -u root -p flood_prevention < database/upgrade_v2.sql
```

### 2. 后端启动

```bash
cd backend
# 修改 application.yml 中的数据库密码（如需要）
mvn spring-boot:run
```
后端启动在 http://localhost:8080

### 3. 前端启动

```bash
cd frontend
npm install
npm run dev
```
前端启动在 http://localhost:5173

### 4. 默认账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 管理员 |
| zhangsan | 123456 | 防汛指挥人员 |
| lisi | 123456 | 普通操作员 |

## 系统架构

采用B/S架构，前后端分离开发：

- **前端**（Vue + Element UI）负责页面展示和用户交互
- **后端**（Spring Boot）负责业务逻辑处理和数据接口
- **数据层**（MySQL + Redis）负责数据持久化和缓存加速
- **安全层**（Spring Security + JWT）负责认证授权
- 通信方式：RESTful API + JSON

后端采用三层架构：Controller（请求控制）→ Service（业务逻辑）→ Mapper（数据访问）
