<template>
  <component :is="layoutComponent">
    <div class="order-management">
      <h2 class="page-title">我的订单</h2>

      <!-- Tabs -->
      <div class="order-tabs">
        <button v-for="tab in tabs" :key="tab.value" class="order-tabs__item" :class="{ 'order-tabs__item--active': activeTab === tab.value }" @click="switchTab(tab.value)">
          {{ tab.label }}
        </button>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="loading-state"><div class="spinner"></div></div>

      <!-- Order List -->
      <div v-else>
        <div v-if="filteredOrders.length === 0" class="empty-state">
          <p class="text-muted">暂无相关订单</p>
        </div>
        <div v-else class="order-list">
          <div v-for="order in filteredOrders" :key="order.orderNumber" class="order-card">
            <div class="order-card__header">
              <span class="order-card__number">订单号：{{ order.orderNumber }}</span>
              <span class="order-card__status badge" :class="getStatusClass(order.orderStatus)">{{ getStatusText(order.orderStatus) }}</span>
            </div>
            <div class="order-card__body">
              <img v-if="order.imgUrl || order.programImg" :src="order.imgUrl || order.programImg" class="order-card__img" />
              <div class="order-card__info">
                <h4 class="order-card__title">{{ order.programTitle || order.title }}</h4>
                <p class="text-muted">{{ order.ticketCategoryName }} × {{ order.count || 1 }}</p>
                <p class="order-card__price">¥{{ order.orderPrice || order.totalAmount }}</p>
              </div>
            </div>
            <div class="order-card__actions">
              <button v-if="order.orderStatus === 1" class="btn btn-ghost btn-sm" @click="handleCancel(order)">取消订单</button>
              <button v-if="order.orderStatus === 1" class="btn btn-primary btn-sm order-card__pay-btn" @click="handlePay(order)">去支付</button>
              <router-link :to="`/orderManagement/detail/${order.orderNumber}`" class="btn btn-outline btn-sm order-card__detail-btn">查看详情</router-link>
            </div>
          </div>
        </div>
      </div>

      <!-- Cancel Confirm Dialog -->
      <div v-if="showCancelDialog" class="dialog-overlay" @click.self="showCancelDialog = false">
        <div class="dialog">
          <div class="dialog__icon">
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
          </div>
          <h3>取消订单</h3>
          <p>确定要取消该订单吗？取消后不可恢复。</p>
          <div class="dialog__actions">
            <button class="btn btn-ghost" @click="showCancelDialog = false">再想想</button>
            <button class="btn btn-primary dialog__confirm-btn" @click="confirmCancel">确认取消</button>
          </div>
        </div>
      </div>
    </div>
  </component>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getOrderList, cancelOrder } from '@/api/order'
import { useToast } from '@/components/Toast.vue'
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import AccountLayout from '@/layouts/AccountLayout.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const toast = useToast()

const layoutComponent = computed(() => route.meta.layout === 'account' ? AccountLayout : DefaultLayout)

const loading = ref(true)
const orders = ref([])
const activeTab = ref(null)
const showCancelDialog = ref(false)
const cancelTarget = ref(null)

const tabs = [
  { label: '全部', value: null },
  { label: '待支付', value: 1 },
  { label: '已支付', value: 3 },
  { label: '已取消', value: 2 }
]

const filteredOrders = computed(() => {
  if (activeTab.value === null) return orders.value
  return orders.value.filter(o => {
    if (activeTab.value === 2) return o.orderStatus === 2 || o.orderStatus === 4
    return o.orderStatus === activeTab.value
  })
})

const getStatusText = (status) => {
  const map = { 1: '待支付', 2: '已取消', 3: '已支付', 4: '已取消' }
  return map[status] || '未知'
}

const getStatusClass = (status) => {
  const map = { 1: 'status--warning', 2: 'status--muted', 3: 'status--success', 4: 'status--muted' }
  return map[status] || ''
}

const switchTab = (value) => { activeTab.value = value }

const handleCancel = (order) => { cancelTarget.value = order; showCancelDialog.value = true }

const confirmCancel = async () => {
  if (!cancelTarget.value) return
  try {
    const res = await cancelOrder({ orderNumber: cancelTarget.value.orderNumber })
    if (res.code == 0) { toast.success('订单已取消'); cancelTarget.value.orderStatus = 2 }
    else toast.error(res.msg || '取消失败')
  } catch (e) { toast.error('网络错误') }
  finally { showCancelDialog.value = false; cancelTarget.value = null }
}

const handlePay = (order) => { router.push({ path: '/order/payMethod', query: { orderNumber: order.orderNumber } }) }

onMounted(async () => {
  try {
    const res = await getOrderList({ userId: userStore.userId })
    if (res.code == 0) orders.value = res.data || []
  } catch (e) { console.error('Load orders failed:', e) }
  finally { loading.value = false }
})
</script>

<style scoped lang="scss">
.order-management {
  min-height: 60vh;
  color: var(--color-text);
}

.page-title {
  font-family: var(--font-display), var(--font-serif-cn), serif;
  font-size: 26px;
  font-weight: 700;
  color: var(--color-text);
  margin-bottom: 28px;
  letter-spacing: 2px;
}

.order-tabs {
  display: flex;
  gap: 0;
  border-bottom: 1px solid var(--color-border);
  margin-bottom: 28px;

  &__item {
    padding: 12px 24px;
    background: none;
    border: none;
    font-size: 14px;
    font-weight: 500;
    color: var(--color-muted);
    border-bottom: 2px solid transparent;
    cursor: pointer;
    transition: color 0.3s ease, border-color 0.3s ease;
    font-family: var(--font-body);
    letter-spacing: 0.5px;

    &:hover {
      color: var(--color-primary-hover);
    }

    &--active {
      color: var(--color-primary);
      border-bottom-color: var(--color-primary);
      text-shadow: 0 0 12px rgba(212, 168, 83, 0.3);
    }
  }
}

.loading-state {
  display: flex;
  justify-content: center;
  padding: 60px;
}

.empty-state {
  text-align: center;
  padding: 80px 0;
  font-size: 15px;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.order-card {
  background: var(--color-card-bg);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid var(--color-border);
  border-radius: 14px;
  padding: 22px;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;

  &:hover {
    border-color: rgba(212, 168, 83, 0.35);
    box-shadow: var(--shadow-card-hover);
  }

  &__header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    font-size: 13px;
  }

  &__number {
    color: var(--color-muted);
    font-family: var(--font-body);
    font-size: 12px;
    letter-spacing: 0.3px;
  }

  &__status {
    font-weight: 600;
    font-size: 12px;
    padding: 3px 10px;
    border-radius: 20px;
  }

  &__body {
    display: flex;
    gap: 16px;
    margin-bottom: 16px;
  }

  &__img {
    width: 88px;
    height: 88px;
    object-fit: cover;
    border-radius: 10px;
    flex-shrink: 0;
    border: 1px solid var(--color-border);
  }

  &__info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;

    p {
      font-size: 13px;
      margin-bottom: 4px;
    }
  }

  &__title {
    font-size: 15px;
    font-weight: 600;
    color: var(--color-text);
    margin-bottom: 6px;
    line-height: 1.4;
  }

  &__price {
    font-size: 18px;
    font-weight: 700;
    color: var(--color-gold);
    margin-top: 6px;
    text-shadow: 0 0 8px rgba(255, 215, 0, 0.15);
  }

  &__actions {
    display: flex;
    gap: 10px;
    justify-content: flex-end;
    padding-top: 14px;
    border-top: 1px solid var(--color-border);
  }

  &__pay-btn {
    background: var(--gradient-primary) !important;
    border: none !important;
    color: var(--color-bg) !important;
    font-weight: 600;

    &:hover {
      box-shadow: 0 2px 12px rgba(212, 168, 83, 0.35);
    }
  }

  &__detail-btn {
    border-color: var(--color-primary) !important;
    color: var(--color-primary) !important;

    &:hover {
      background: rgba(212, 168, 83, 0.08) !important;
      color: var(--color-primary-hover) !important;
    }
  }
}

.status--warning {
  color: var(--color-primary);
  background: rgba(212, 168, 83, 0.12);
}

.status--success {
  color: var(--color-success);
  background: rgba(76, 175, 80, 0.12);
}

.status--muted {
  color: var(--color-muted);
  background: rgba(138, 138, 142, 0.12);
}

.dialog-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 18px;
  padding: 36px;
  max-width: 420px;
  width: 90%;
  text-align: center;
  box-shadow: 0 24px 64px rgba(0, 0, 0, 0.5);

  &__icon {
    display: flex;
    justify-content: center;
    margin-bottom: 16px;
    color: var(--color-primary);
  }

  h3 {
    margin-bottom: 12px;
    font-size: 19px;
    font-weight: 700;
    color: var(--color-text);
    font-family: var(--font-display), var(--font-serif-cn), serif;
    letter-spacing: 1px;
  }

  p {
    color: var(--color-muted);
    font-size: 14px;
    margin-bottom: 28px;
    line-height: 1.6;
  }

  &__actions {
    display: flex;
    gap: 12px;
    justify-content: center;
  }

  &__confirm-btn {
    background: var(--gradient-primary) !important;
    border: none !important;
    color: var(--color-bg) !important;
    font-weight: 600;
  }
}
</style>
