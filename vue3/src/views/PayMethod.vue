<template>
  <component :is="layoutComponent">
    <div class="pay-method-page">
      <div class="container">
        <div class="page-header">
          <h2 class="page-title">订单支付</h2>
          <div class="page-title__line"></div>
        </div>

        <div class="pay-card">
          <div class="pay-card__header">
            <h3>订单信息</h3>
            <div class="countdown" :class="{ 'countdown--warning': countdown <= 60 }">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
              <span>{{ formatCountdown }}</span>
            </div>
          </div>
          <div class="pay-card__info">
            <div class="info-row">
              <span class="info-row__label">订单号</span>
              <span class="info-row__value">{{ orderNumber }}</span>
            </div>
            <div class="info-row info-row--total">
              <span class="info-row__label">应付金额</span>
              <span class="total-amount">¥{{ orderAmount }}</span>
            </div>
          </div>
        </div>

        <div class="pay-card">
          <h3 class="pay-card__title">选择支付方式</h3>
          <label class="payment-option payment-option--selected">
            <input type="radio" checked />
            <div class="alipay-icon"><span>支</span></div>
            <div class="payment-option__info">
              <span class="payment-option__name">支付宝</span>
              <span class="payment-option__desc">推荐使用支付宝支付</span>
            </div>
            <svg class="payment-option__check" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
          </label>
        </div>

        <button class="btn btn-primary btn-lg btn-block pay-btn" :disabled="paying || countdown <= 0" @click="handlePay">
          <span v-if="paying" class="spinner"></span>
          <span v-else-if="countdown <= 0">订单已超时</span>
          <span v-else>立即支付 ¥{{ orderAmount }}</span>
        </button>
      </div>
    </div>
  </component>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { payOrder, getOrderDetail } from '@/api/order'
import { useToast } from '@/components/Toast.vue'
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import AccountLayout from '@/layouts/AccountLayout.vue'

const route = useRoute()
const toast = useToast()

const layoutComponent = computed(() => route.meta.layout === 'account' ? AccountLayout : DefaultLayout)

const orderNumber = ref('')
const orderAmount = ref('0.00')
const orderTitle = ref('')
const paying = ref(false)
const countdown = ref(15 * 60)
let countdownTimer = null

const formatCountdown = computed(() => {
  const mins = Math.floor(countdown.value / 60)
  const secs = countdown.value % 60
  return `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`
})

const startCountdown = () => {
  countdownTimer = setInterval(() => {
    if (countdown.value > 0) countdown.value--
    else { clearInterval(countdownTimer); toast.error('订单已超时，请重新下单') }
  }, 1000)
}

onMounted(async () => {
  orderNumber.value = route.query.orderNumber || ''
  if (orderNumber.value) {
    try {
      const res = await getOrderDetail({ orderNumber: orderNumber.value })
      if (res.code == 0) {
        const data = res.data
        orderAmount.value = data.orderPrice || data.totalAmount || '0.00'
        orderTitle.value = data.programTitle || data.subject || ''
        if (data.createTime) {
          const elapsed = Math.floor((Date.now() - new Date(data.createTime).getTime()) / 1000)
          countdown.value = Math.max(0, 15 * 60 - elapsed)
        }
      }
    } catch (e) { console.error('Load order detail failed:', e) }
  }
  startCountdown()
})

onUnmounted(() => { if (countdownTimer) clearInterval(countdownTimer) })

const handlePay = async () => {
  if (paying.value || countdown.value <= 0) return
  paying.value = true
  try {
    const res = await payOrder({
      platform: 3,
      orderNumber: orderNumber.value,
      subject: orderTitle.value,
      price: parseFloat(orderAmount.value),
      channel: 'alipay',
      payBillType: 1
    })
    if (res.code == 0) {
      // PC web 支付：把支付宝返回的 form HTML 写进浏览器，浏览器解析后
      // form 内的 script 会自动 submit 跳到收银台。
      // 支付完成后支付宝通过 return_url 跳回 /order/paySuccess。
      document.write(res.data)
    } else toast.error(res.msg || '支付发起失败')
  } catch (e) { toast.error('网络错误') }
}
</script>

<style scoped lang="scss">
.pay-method-page {
  min-height: 100vh;
  padding: 32px 0 60px;
  background: var(--color-bg);
}

// ── Page Header ──
.page-header {
  margin-bottom: 32px;
  text-align: center;
}

.page-title {
  font-size: 26px;
  font-family: var(--font-serif-cn);
  color: var(--color-text);
  letter-spacing: 4px;
  margin-bottom: 12px;

  &__line {
    width: 60px;
    height: 2px;
    background: var(--gradient-primary);
    margin: 0 auto;
    border-radius: 1px;
  }
}

// ── Pay Card ──
.pay-card {
  background: var(--color-card-bg);
  border: 1px solid var(--color-border);
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
  backdrop-filter: blur(12px);
  transition: border-color 0.25s ease;

  &:hover {
    border-color: rgba(212, 168, 83, 0.3);
  }

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;

    h3 {
      font-size: 16px;
      font-family: var(--font-serif-cn);
      color: var(--color-text);
      letter-spacing: 1px;
    }
  }

  &__title {
    font-size: 16px;
    font-family: var(--font-serif-cn);
    color: var(--color-text);
    margin-bottom: 16px;
    letter-spacing: 1px;
  }

  &__info {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
}

// ── Countdown ──
.countdown {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 600;
  color: var(--color-primary);
  padding: 4px 14px;
  border-radius: 20px;
  background: rgba(212, 168, 83, 0.08);

  &--warning {
    color: var(--color-error);
    background: rgba(239, 68, 68, 0.08);
    animation: pulse 1s infinite;
  }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

// ── Info Row ──
.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;

  &__label {
    color: var(--color-muted);
  }

  &__value {
    color: var(--color-text);
    font-weight: 500;
    letter-spacing: 0.5px;
  }
}

.total-amount {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-gold);
  text-shadow: 0 0 12px rgba(255, 215, 0, 0.25);
}

// ── Payment Option ──
.payment-option {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px;
  border: 2px solid var(--color-border);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;

  &--selected {
    border-color: var(--color-primary);
    background: rgba(212, 168, 83, 0.04);
  }

  input {
    display: none;
  }

  &__info {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 2px;
  }

  &__name {
    font-weight: 600;
    font-size: 15px;
    color: var(--color-text);
  }

  &__desc {
    font-size: 12px;
    color: var(--color-muted);
  }

  &__check {
    color: var(--color-primary);
  }
}

.alipay-icon {
  width: 42px;
  height: 42px;
  border-radius: 10px;
  background: #1677FF;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 700;
}

// ── Pay Button ──
.pay-btn {
  margin-top: 32px;
  height: 54px;
  font-size: 17px;
  font-weight: 600;
  background: var(--gradient-primary);
  border: none;
  letter-spacing: 3px;
  box-shadow: 0 4px 20px rgba(212, 168, 83, 0.3);

  &:hover:not(:disabled) {
    transform: translateY(-1px);
    box-shadow: 0 6px 28px rgba(212, 168, 83, 0.45);
  }

  &:disabled {
    opacity: 0.4;
    box-shadow: none;
  }
}

// ── Polling ──
.polling-status {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 32px;
  padding: 24px;

  .spinner {
    border-color: var(--color-border);
    border-top-color: var(--color-primary);
  }
}
</style>
