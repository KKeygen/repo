import { createRouter, createWebHistory } from 'vue-router'
import NProgress from 'nprogress'
import { getToken } from '@/utils/auth'
import { SITE_NAME } from '@/constants/site'
import { ADMIN_USER_IDS } from '@/constants/site'
import { useUserStore } from '@/stores/user'

const routes = [
  { path: '/', redirect: '/index' },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/index',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页', layout: 'default' }
  },
  {
    path: '/allType',
    name: 'CategoryList',
    component: () => import('@/views/CategoryList.vue'),
    meta: { title: '全部分类', layout: 'default' }
  },
  {
    path: '/contentDetail/:id',
    name: 'ProgramDetail',
    component: () => import('@/views/ProgramDetail.vue'),
    meta: { title: '节目详情', layout: 'default' }
  },
  {
    path: '/order/index',
    name: 'OrderConfirm',
    component: () => import('@/views/OrderConfirm.vue'),
    meta: { title: '确认订单', layout: 'default', requiresAuth: true }
  },
  {
    path: '/order/seatSelect',
    name: 'SeatSelect',
    component: () => import('@/views/SeatSelect.vue'),
    meta: { title: '选座', layout: 'default', requiresAuth: true }
  },
  {
    path: '/order/payMethod',
    name: 'PayMethod',
    component: () => import('@/views/PayMethod.vue'),
    meta: { title: '支付方式', layout: 'default', requiresAuth: true }
  },
  {
    path: '/order/paySuccess',
    name: 'PaySuccess',
    component: () => import('@/views/PaySuccess.vue'),
    meta: { title: '支付成功', layout: 'default', requiresAuth: true }
  },
  {
    path: '/personInfo',
    name: 'PersonInfo',
    component: () => import('@/views/PersonInfo.vue'),
    meta: { title: '个人信息', layout: 'account', requiresAuth: true }
  },
  {
    path: '/personInfo/ticketUser',
    name: 'TicketUsers',
    component: () => import('@/views/TicketUsers.vue'),
    meta: { title: '购票人管理', layout: 'account', requiresAuth: true }
  },
  {
    path: '/orderManagement',
    name: 'OrderManagement',
    component: () => import('@/views/OrderManagement.vue'),
    meta: { title: '订单管理', layout: 'account', requiresAuth: true }
  },
  {
    path: '/orderManagement/detail/:orderNumber',
    name: 'OrderDetail',
    component: () => import('@/views/OrderDetail.vue'),
    meta: { title: '订单详情', layout: 'account', requiresAuth: true }
  },
  {
    path: '/accountSettings',
    name: 'AccountSettings',
    component: () => import('@/views/AccountSettings.vue'),
    meta: { title: '账号设置', layout: 'account', requiresAuth: true }
  },
  {
    path: '/accountSettings/editPassword',
    name: 'EditPassword',
    component: () => import('@/views/EditPassword.vue'),
    meta: { title: '修改密码', layout: 'account', requiresAuth: true }
  },
  {
    path: '/accountSettings/email',
    name: 'EmailVerify',
    component: () => import('@/views/EmailVerify.vue'),
    meta: { title: '邮箱验证', layout: 'account', requiresAuth: true }
  },
  {
    path: '/accountSettings/mobile',
    name: 'MobileVerify',
    component: () => import('@/views/MobileVerify.vue'),
    meta: { title: '手机验证', layout: 'account', requiresAuth: true }
  },
  {
    path: '/accountSettings/authentication',
    name: 'Authentication',
    component: () => import('@/views/Authentication.vue'),
    meta: { title: '实名认证', layout: 'account', requiresAuth: true }
  },
  // === Admin Routes ===
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: () => import('@/views/admin/Dashboard.vue'),
    meta: { title: '管理后台', layout: 'admin', requiresAuth: true }
  },
  {
    path: '/admin/programs',
    name: 'AdminProgramList',
    component: () => import('@/views/admin/ProgramList.vue'),
    meta: { title: '节目管理', layout: 'admin', requiresAuth: true }
  },
  {
    path: '/admin/programs/add',
    name: 'AdminProgramAdd',
    component: () => import('@/views/admin/ProgramEdit.vue'),
    meta: { title: '新增节目', layout: 'admin', requiresAuth: true }
  },
  {
    path: '/admin/seats',
    name: 'AdminSeatManage',
    component: () => import('@/views/admin/SeatManage.vue'),
    meta: { title: '座位管理', layout: 'admin', requiresAuth: true }
  },
  {
    path: '/admin/categories',
    name: 'AdminCategoryManage',
    component: () => import('@/views/admin/CategoryManage.vue'),
    meta: { title: '分类管理', layout: 'admin', requiresAuth: true }
  },
  {
    path: '/admin/ticket-categories',
    name: 'AdminTicketCategoryManage',
    component: () => import('@/views/admin/TicketCategoryManage.vue'),
    meta: { title: '票档管理', layout: 'admin', requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach((to, from, next) => {
  NProgress.start()
  document.title = to.meta.title ? `${to.meta.title} - ${SITE_NAME}` : SITE_NAME

  if (!to.meta.requiresAuth || getToken()) {
    if (to.path.startsWith('/admin')) {
      const store = useUserStore()
      if (!ADMIN_USER_IDS.includes(String(store.mobile))) {
        next('/')
        return
      }
    }
    next()
    return
  }

  next({ path: '/login', query: { redirect: to.fullPath } })
})

router.afterEach(() => {
  NProgress.done()
})

export default router
