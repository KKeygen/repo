# 万花筒网 Vue3 前端

面向票务浏览、选座下单、订单管理和账号中心的前端工程。当前由 `ivorythule` 负责页面与交互、接口联调、打包发布。

## 技术栈

- Vue 3 + Vite 5
- Vue Router 4
- Pinia
- Axios
- Sass
- NProgress

## 目录说明

```text
src/
  api/          后端接口封装，按业务域拆分
  assets/       图片、图标等静态资源
  components/   复用组件，如头部、底部、节目卡片、Toast
  constants/    站点文案、默认城市、兜底分类等共享常量
  layouts/      默认布局与账号中心布局
  router/       路由表、页面标题、登录守卫
  stores/       Pinia 状态
  styles/       全局设计变量和基础样式
  utils/        请求、鉴权、事件总线等工具
  views/        页面级组件
```

## 本地开发

```bash
npm install
npm run dev
```

默认开发端口为 `5173`。接口代理读取 `.env.development`：

- `VITE_APP_BASE_API`：前端请求前缀，默认 `/barley-dev`
- `VITE_APP_URL`：后端服务地址，默认 `http://127.0.0.1:6085`
- `VITE_SIGN_FLAG`：是否启用签名调用，`0` 为普通调用，`1` 为签名调用

## 构建发布

```bash
npm run build
npm run preview
```

构建产物输出到 `dist/`。发布前至少执行一次 `npm run build`，确认路由懒加载、资源路径和接口环境变量都符合目标环境。

## 协作约定

- 页面文案、默认城市和兜底分类优先放到 `src/constants/`，避免散落在页面里。
- 接口统一通过 `src/utils/request.js`，不要在页面中直接创建新的 Axios 实例。
- 每次接口请求会自动携带 `X-Request-Id`、`X-Client-Timestamp` 和 `X-Client-Route`，用于网关与各微服务日志串联，排查分布式调用链路问题。
- 列表图片使用懒加载和兜底图，避免接口图片为空时破坏页面排版。
- 页面样式遵循 `src/styles/global.scss` 中的设计变量，新增样式优先复用现有色彩、圆角、阴影和间距。
- 需要登录的页面在路由 `meta.requiresAuth` 标记，由路由守卫统一处理跳转。

## 当前注意事项

- 构建会出现 Dart Sass legacy JS API 的上游警告，不影响产物生成；后续升级 Sass/Vite 生态时再统一处理。
- 账号相关业务会引入签名库，相关 chunk 偏大，后续可按页面和接口调用路径继续拆包。
