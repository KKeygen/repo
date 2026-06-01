<template>
  <component :is="layoutComponent">
    <div class="pay-success-page">
      <div class="container">
        <!-- Decorative background particles -->
        <div class="success-particles">
          <span v-for="n in 6" :key="n" class="particle" :style="{ animationDelay: `${n * 0.3}s`, left: `${10 + n * 14}%` }"></span>
        </div>

        <div class="success-card">
          <!-- Glow ring behind icon -->
          <div class="success-icon-wrapper">
            <div class="success-icon-glow"></div>
            <div class="success-icon">
              <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="#4CAF50" stroke-width="2">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/>
              </svg>
            </div>
          </div>

          <h1 class="success-title">支付成功！</h1>
          <div class="success-divider">
            <span class="success-divider__line"></span>
            <span class="success-divider__diamond">◆</span>
            <span class="success-divider__line"></span>
          </div>
          <p class="success-subtitle">您的订单已支付完成</p>

          <div v-if="checking" class="order-info__checking">
            <div class="spinner spinner--inline"></div>
            <span class="text-muted">正在校验支付状态...</span>
          </div>

          <div class="order-info">
            <div class="order-info__row">
              <span class="text-muted">订单号</span>
              <span class="order-info__value">{{ orderNumber }}</span>
            </div>
            <div v-if="payStatus" class="order-info__row">
              <span class="text-muted">支付状态</span>
              <span class="order-info__value" :class="payStatusClass">{{ payStatusText }}</span>
            </div>
          </div>

          <div class="success-actions">
            <router-link to="/orderManagement" class="btn btn-primary btn-lg success-actions__primary">查看订单</router-link>
            <router-link to="/index" class="btn btn-outline btn-lg success-actions__outline">返回首页</router-link>
          </div>
        </div>
      </div>
    </div>
  </component>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { checkPayStatus } from '@/api/order'
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import AccountLayout from '@/layouts/AccountLayout.vue'

const route = useRoute()
const layoutComponent = computed(() => route.meta.layout === 'account' ? AccountLayout : DefaultLayout)
const orderNumber = ref('')
const checking = ref(false)
const payStatus = ref(null)

const payStatusText = computed(() => {
  if (payStatus.value === 1) return '待支付'
  if (payStatus.value === 2) return '已取消'
  if (payStatus.value === 3) return '已支付'
  if (payStatus.value === 4) return '已退单'
  return '未知'
})

const payStatusClass = computed(() => {
  if (payStatus.value === 3) return 'text-success'
  if (payStatus.value === 1) return 'text-warning'
  if (payStatus.value === 2 || payStatus.value === 4) return 'text-muted'
  return ''
})

onMounted(async () => {
  orderNumber.value = route.query.orderNumber || ''
  if (!orderNumber.value) return
  checking.value = true
  try {
    const res = await checkPayStatus({ orderNumber: orderNumber.value, payChannelType: 1 })
    if (res.code == 0 && res.data) {
      payStatus.value = res.data.orderStatus ?? res.data.status ?? null
    }
  } catch (e) { console.error('Check pay status failed:', e) }
  finally { checking.value = false }
})
</script>

<style scoped lang="scss">
.pay-success-page {
  min-height: 80vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  background: var(--color-bg);
  position: relative;
  overflow: hidden;
}

.success-particles {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}

.particle {
  position: absolute;
  bottom: -10px;
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: var(--color-primary);
  opacity: 0;
  animation: floatUp 4s ease-in-out infinite;

  &:nth-child(even) {
    background: var(--color-gold);
    width: 3px;
    height: 3px;
  }
}

@keyframes floatUp {
  0% { transform: translateY(0); opacity: 0; }
  20% { opacity: 0.6; }
  80% { opacity: 0.3; }
  100% { transform: translateY(-80vh); opacity: 0; }
}

.success-card {
  max-width: 480px;
  width: 100%;
  background: var(--color-card-bg);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border: 1px solid var(--color-border);
  border-radius: 20px;
  box-shadow: var(--shadow-card);
  padding: 60px 40px;
  text-align: center;
  position: relative;
  z-index: 1;
}

.success-icon-wrapper {
  position: relative;
  display: flex;
  justify-content: center;
  margin-bottom: 28px;
}

.success-icon-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(76, 175, 80, 0.25) 0%, transparent 70%);
  animation: pulseGlow 2.5s ease-in-out infinite;
}

@keyframes pulseGlow {
  0%, 100% { transform: translate(-50%, -50%) scale(1); opacity: 0.6; }
  50% { transform: translate(-50%, -50%) scale(1.3); opacity: 1; }
}

.success-icon {
  display: flex;
  justify-content: center;
  animation: scaleIn 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
  filter: drop-shadow(0 0 12px rgba(76, 175, 80, 0.5));
  position: relative;
  z-index: 1;
}

@keyframes scaleIn {
  from { transform: scale(0); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}

.success-title {
  font-family: var(--font-display), var(--font-serif-cn), serif;
  font-size: 30px;
  font-weight: 700;
  color: var(--color-success);
  margin-bottom: 12px;
  letter-spacing: 2px;
}

.success-divider {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 12px;

  &__line {
    width: 48px;
    height: 1px;
    background: linear-gradient(90deg, transparent, var(--color-primary), transparent);
  }

  &__diamond {
    color: var(--color-primary);
    font-size: 8px;
    opacity: 0.8;
  }
}

.success-subtitle {
  font-family: var(--font-body);
  font-size: 15px;
  color: var(--color-muted);
  margin-bottom: 36px;
}

.order-info {
  background: var(--color-elevated);
  border: 1px solid var(--color-border);
  border-radius: 12px;
  padding: 18px 24px;
  margin-bottom: 36px;

  &__row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 14px;
    padding: 6px 0;
  }

  &__value {
    color: var(--color-text);
    font-weight: 500;
    font-family: var(--font-body);
  }

  &__checking {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    margin-bottom: 18px;
    font-size: 13px;
  }
}

.spinner--inline {
  width: 16px;
  height: 16px;
  border-width: 2px;
}

.success-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;

  .btn {
    width: 100%;
  }

  &__primary {
    background: var(--gradient-primary) !important;
    border: none !important;
    color: var(--color-bg) !important;
    font-weight: 600;
    letter-spacing: 1px;
    box-shadow: 0 4px 20px rgba(212, 168, 83, 0.3);
    transition: box-shadow 0.3s ease, transform 0.3s ease;

    &:hover {
      box-shadow: var(--shadow-gold-glow);
      transform: translateY(-1px);
    }
  }

  &__outline {
    border-color: var(--color-primary) !important;
    color: var(--color-primary) !important;
    background: transparent !important;

    &:hover {
      background: rgba(212, 168, 83, 0.08) !important;
      border-color: var(--color-primary-hover) !important;
      color: var(--color-primary-hover) !important;
    }
  }
}
</style>
