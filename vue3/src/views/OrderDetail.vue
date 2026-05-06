<template>
  <component :is="layoutComponent">
    <div class="order-detail">
      <h2 class="page-title">订单详情</h2>

      <div v-if="loading" class="loading-state"><div class="spinner"></div></div>

      <div v-else-if="order" class="detail-content">
        <!-- Status -->
        <div class="status-card">
          <div class="status-card__icon" :class="getStatusClass(order.orderStatus)">
            <svg v-if="order.orderStatus === 3" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
            <svg v-else-if="order.orderStatus === 1" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
            <svg v-else width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>
          </div>
          <div class="status-card__text">
            <h3>{{ getStatusText(order.orderStatus) }}</h3>
            <p class="text-muted">{{ order.createTime }}</p>
          </div>
        </div>

        <!-- Program Info -->
        <div class="info-section">
          <h3 class="section-title">演出信息</h3>
          <div class="info-grid">
            <div class="info-item"><span class="info-item__label">演出名称</span><span class="info-item__value">{{ order.programTitle || order.title }}</span></div>
            <div class="info-item"><span class="info-item__label">票档</span><span class="info-item__value">{{ order.ticketCategoryName }}</span></div>
            <div class="info-item"><span class="info-item__label">数量</span><span class="info-item__value">{{ order.count || 1 }} 张</span></div>
            <div class="info-item"><span class="info-item__label">场馆</span><span class="info-item__value">{{ order.place || order.venue || '—' }}</span></div>
            <div class="info-item"><span class="info-item__label">演出时间</span><span class="info-item__value">{{ order.showTime || '—' }}</span></div>
          </div>
        </div>

        <!-- Seat Info -->
        <div v-if="order.seatInfo || order.seats" class="info-section">
          <h3 class="section-title">座位信息</h3>
          <div class="seat-tags">
            <span class="seat-tag">{{ order.seatInfo || (order.seats || []).map(s => `${s.row}排${s.col}座`).join('、') }}</span>
          </div>
        </div>

        <!-- Payment Info -->
        <div class="info-section">
          <h3 class="section-title">支付信息</h3>
          <div class="info-grid">
            <div class="info-item"><span class="info-item__label">订单号</span><span class="info-item__value">{{ order.orderNumber }}</span></div>
            <div class="info-item"><span class="info-item__label">订单金额</span><span class="info-item__value info-item__value--gold text-gold font-bold">¥{{ order.orderPrice || order.totalAmount }}</span></div>
            <div class="info-item"><span class="info-item__label">支付方式</span><span class="info-item__value">{{ order.payType === 1 ? '支付宝' : '—' }}</span></div>
            <div class="info-item"><span class="info-item__label">创建时间</span><span class="info-item__value">{{ order.createTime }}</span></div>
            <div v-if="order.payTime" class="info-item"><span class="info-item__label">支付时间</span><span class="info-item__value">{{ order.payTime }}</span></div>
          </div>
        </div>

        <!-- Actions -->
        <div class="detail-actions">
          <button v-if="order.orderStatus === 1" class="btn btn-ghost detail-actions__cancel" @click="handleCancel">取消订单</button>
          <button v-if="order.orderStatus === 1" class="btn btn-primary detail-actions__pay" @click="handlePay">去支付</button>
          <router-link to="/orderManagement" class="btn btn-outline detail-actions__back">返回订单列表</router-link>
        </div>
      </div>

      <div v-else class="empty-state"><p class="text-muted">订单不存在</p></div>
    </div>
  </component>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getOrderDetail, cancelOrder } from '@/api/order'
import { useToast } from '@/components/Toast.vue'
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import AccountLayout from '@/layouts/AccountLayout.vue'

const route = useRoute()
const router = useRouter()
const toast = useToast()

const layoutComponent = computed(() => route.meta.layout === 'account' ? AccountLayout : DefaultLayout)

const loading = ref(true)
const order = ref(null)

const getStatusText = (status) => ({ 1: '待支付', 2: '已取消', 3: '已支付', 4: '已取消' }[status] || '未知')
const getStatusClass = (status) => ({ 1: 'status--warning', 2: 'status--muted', 3: 'status--success', 4: 'status--muted' }[status] || '')

const handleCancel = async () => {
  if (!confirm('确定要取消该订单吗？')) return
  try {
    const res = await cancelOrder({ orderNumber: order.value.orderNumber })
    if (res.code === 0) { toast.success('订单已取消'); order.value.orderStatus = 2 }
    else toast.error(res.msg || '取消失败')
  } catch (e) { toast.error('网络错误') }
}

const handlePay = () => { router.push({ path: '/order/payMethod', query: { orderNumber: order.value.orderNumber } }) }

onMounted(async () => {
  const orderNumber = route.params.orderNumber
  if (!orderNumber) { loading.value = false; return }
  try {
    const res = await getOrderDetail({ orderNumber })
    if (res.code === 0) order.value = res.data
  } catch (e) { console.error('Load order detail failed:', e) }
  finally { loading.value = false }
})
</script>

<style scoped lang="scss">
.order-detail {
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

.loading-state {
  display: flex;
  justify-content: center;
  padding: 60px;
}

.empty-state {
  text-align: center;
  padding: 80px 0;
}

.detail-content {
  display: flex;
  flex-direction: column;
  gap: 22px;
}

.status-card {
  display: flex;
  align-items: center;
  gap: 18px;
  padding: 28px;
  background: var(--color-card-bg);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid var(--color-border);
  border-radius: 16px;
  box-shadow: var(--shadow-card);

  &__icon {
    width: 52px;
    height: 52px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  &__text {
    h3 {
      font-size: 20px;
      font-weight: 700;
      margin-bottom: 4px;
      color: var(--color-text);
      font-family: var(--font-display), var(--font-serif-cn), serif;
    }
  }
}

.status--warning {
  background: rgba(212, 168, 83, 0.15);
  color: var(--color-primary);
  box-shadow: 0 0 16px rgba(212, 168, 83, 0.15);
}

.status--success {
  background: rgba(76, 175, 80, 0.15);
  color: var(--color-success);
  box-shadow: 0 0 16px rgba(76, 175, 80, 0.15);
}

.status--muted {
  background: rgba(138, 138, 142, 0.12);
  color: var(--color-muted);
}

.info-section {
  background: var(--color-card-bg);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid var(--color-border);
  border-radius: 16px;
  padding: 24px;
  box-shadow: var(--shadow-card);
}

.section-title {
  font-family: var(--font-display), var(--font-serif-cn), serif;
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text);
  margin-bottom: 18px;
  padding-left: 14px;
  border-left: 3px solid var(--color-primary);
  letter-spacing: 1px;
}

.info-grid {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  padding: 4px 0;

  &__label {
    color: var(--color-muted);
    font-size: 13px;
    flex-shrink: 0;
    margin-right: 16px;
  }

  &__value {
    color: var(--color-text);
    text-align: right;

    &--gold {
      font-size: 18px;
      text-shadow: 0 0 10px rgba(255, 215, 0, 0.2);
    }
  }
}

.seat-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.seat-tag {
  display: inline-block;
  padding: 6px 14px;
  background: rgba(212, 168, 83, 0.1);
  border: 1px solid rgba(212, 168, 83, 0.25);
  border-radius: 20px;
  color: var(--color-primary);
  font-size: 13px;
  font-weight: 500;
}

.detail-actions {
  display: flex;
  gap: 12px;
  margin-top: 10px;

  &__pay {
    background: var(--gradient-primary) !important;
    border: none !important;
    color: var(--color-bg) !important;
    font-weight: 600;
    letter-spacing: 1px;

    &:hover {
      box-shadow: var(--shadow-gold-glow);
      transform: translateY(-1px);
    }
  }

  &__back {
    border-color: var(--color-primary) !important;
    color: var(--color-primary) !important;

    &:hover {
      background: rgba(212, 168, 83, 0.08) !important;
      color: var(--color-primary-hover) !important;
    }
  }

  &__cancel {
    color: var(--color-muted) !important;

    &:hover {
      color: var(--color-error) !important;
    }
  }
}
</style>
