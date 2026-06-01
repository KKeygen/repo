<template>
  <component :is="layoutComponent">
    <div class="program-detail">
      <!-- Loading -->
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p class="text-muted mt-3">加载中...</p>
      </div>

      <div v-else-if="program" class="detail-layout container">
        <!-- Left / Main Section -->
        <div class="detail-main">
          <!-- Hero Image -->
          <div class="hero-image child-stagger-1">
            <img :src="program.imgUrl || program.image" :alt="program.title" />
            <div class="hero-image__overlay"></div>
            <span v-if="program.preSell === 1" class="badge badge--gold presale-badge">预售</span>
          </div>

          <!-- Program Info -->
          <div class="detail-info child-stagger-2">
            <h1 class="detail-info__title font-display">{{ program.title }}</h1>
            <div class="detail-info__meta">
              <div class="meta-item">
                <svg class="meta-item__icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
                <span>{{ program.showTime || program.time }}</span>
                <span v-if="program.showDayOfWeek" class="text-muted ml-2">{{ program.showDayOfWeek }}</span>
              </div>
              <div class="meta-item">
                <svg class="meta-item__icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>
                <span>{{ program.place || program.venue }}</span>
                <span v-if="program.areaName" class="text-muted ml-2">· {{ program.areaName }}</span>
              </div>
            </div>
          </div>

          <!-- Ticket Categories -->
          <div class="ticket-section child-stagger-3">
            <h3 class="section-heading">
              <span class="section-heading__bar"></span>
              选择票档
            </h3>
            <div class="ticket-cards">
              <div
                v-for="ticket in program.ticketCategoryVoList || []"
                :key="ticket.id"
                class="ticket-card"
                :class="{ 'ticket-card--selected': selectedTicket?.id === ticket.id }"
                @click="selectTicket(ticket)"
              >
                <div class="ticket-card__name">{{ ticket.introduce }}</div>
                <div class="ticket-card__price">¥{{ ticket.price }}</div>
              </div>
            </div>
            <p v-if="!program.ticketCategoryVoList?.length" class="text-muted text-center" style="padding:32px 0;">
              暂无可售票档
            </p>
          </div>

          <!-- Quantity Selector -->
          <div v-if="selectedTicket" class="quantity-section child-stagger-4">
            <h3 class="section-heading">
              <span class="section-heading__bar"></span>
              购买数量
            </h3>
            <div class="quantity-row">
              <div class="quantity-selector">
                <button class="qty-btn" :disabled="quantity <= 1" @click="quantity--">−</button>
                <span class="qty-value">{{ quantity }}</span>
                <button class="qty-btn" :disabled="quantity >= 6" @click="quantity++">+</button>
              </div>
              <div class="total-price">
                <span class="text-muted">合计：</span>
                <span class="total-price__value">¥{{ totalPrice }}</span>
              </div>
            </div>
          </div>

          <!-- CTA Button -->
          <div class="cta-section child-stagger-5">
            <button class="cta-btn" :disabled="!selectedTicket" @click="handleBuy">
              {{ program.permitChooseSeat === 1 ? '选座购买' : '立即购买' }}
            </button>
          </div>

          <!-- Detail Tabs -->
          <div class="detail-tabs child-stagger-6">
            <div class="detail-tabs__header">
              <button
                v-for="tab in tabs"
                :key="tab.key"
                class="detail-tabs__tab"
                :class="{ 'detail-tabs__tab--active': activeTab === tab.key }"
                @click="activeTab = tab.key"
              >{{ tab.label }}</button>
            </div>
            <div class="detail-tabs__content">
              <div v-if="activeTab === 'detail'" v-html="program.detail || '<p>暂无详情</p>'"></div>
              <div v-if="activeTab === 'buyNotice'" v-html="program.buyNotice || '<p>暂无购票须知</p>'"></div>
              <div v-if="activeTab === 'watchNotice'" v-html="program.watchNotice || '<p>暂无观演须知</p>'"></div>
            </div>
          </div>
        </div>

        <!-- Right Sidebar -->
        <div class="detail-sidebar child-stagger-7">
          <div class="sidebar-card">
            <h4 class="sidebar-card__title">服务信息</h4>
            <div class="service-list">
              <div class="service-item">
                <span class="service-item__label">退换政策</span>
                <span class="service-item__value" :class="program.canRefund ? 'text-success' : 'text-error'">{{ program.canRefund ? '支持退款' : '不支持退款' }}</span>
              </div>
              <div class="service-item">
                <span class="service-item__label">实名要求</span>
                <span class="service-item__value" :class="program.needRealName ? 'text-primary' : ''">{{ program.needRealName ? '需实名认证' : '无需实名' }}</span>
              </div>
              <div class="service-item">
                <span class="service-item__label">配送方式</span>
                <span class="service-item__value">{{ deliveryText }}</span>
              </div>
              <div class="service-item">
                <span class="service-item__label">发票类型</span>
                <span class="service-item__value">{{ invoiceText }}</span>
              </div>
              <div class="service-item">
                <span class="service-item__label">选座方式</span>
                <span class="service-item__value" :class="program.permitChooseSeat === 1 ? 'text-primary' : ''">{{ program.permitChooseSeat === 1 ? '可选座' : '随机分配' }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Error State -->
      <div v-else class="error-state container">
        <div class="error-state__icon">🎭</div>
        <p>未找到演出信息</p>
        <router-link to="/" class="btn btn-outline mt-3">返回首页</router-link>
      </div>
    </div>
  </component>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProgramDetail } from '@/api/program'
import { useToast } from '@/components/Toast.vue'
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import AccountLayout from '@/layouts/AccountLayout.vue'

const route = useRoute()
const router = useRouter()
const toast = useToast()

const layoutComponent = computed(() => {
  return route.meta.layout === 'account' ? AccountLayout : DefaultLayout
})

const loading = ref(true)
const program = ref(null)
const selectedTicket = ref(null)
const quantity = ref(1)
const activeTab = ref('detail')

const tabs = [
  { key: 'detail', label: '项目详情' },
  { key: 'buyNotice', label: '购票须知' },
  { key: 'watchNotice', label: '观演须知' }
]

const totalPrice = computed(() => {
  if (!selectedTicket.value) return 0
  return (selectedTicket.value.price * quantity.value).toFixed(2)
})

const deliveryText = computed(() => {
  if (!program.value) return '—'
  const type = program.value.deliveryType
  if (type === 1) return '电子票'
  if (type === 2) return '快递配送'
  if (type === 3) return '现场取票'
  return '电子票'
})

const invoiceText = computed(() => {
  if (!program.value) return '—'
  const type = program.value.invoiceType
  if (type === 1) return '电子发票'
  if (type === 2) return '纸质发票'
  return '电子发票'
})

const selectTicket = (ticket) => {
  if (selectedTicket.value?.id === ticket.id) {
    selectedTicket.value = null
  } else {
    selectedTicket.value = ticket
  }
  quantity.value = 1
}

const handleBuy = () => {
  if (!selectedTicket.value) {
    toast.error('请先选择票档')
    return
  }
  if (program.value.permitChooseSeat === 1) {
    router.push({
      path: '/order/seatSelect',
      query: { programId: program.value.id, ticketCategoryId: selectedTicket.value.id }
    })
  } else {
    router.push({
      path: '/order/index',
      query: { programId: program.value.id, ticketCategoryId: selectedTicket.value.id, count: quantity.value }
    })
  }
}

onMounted(async () => {
  const id = route.params.id
  if (!id) { loading.value = false; return }
  try {
    const res = await getProgramDetail({ id })
    if (res.code == 0) {
      program.value = res.data
    }
  } catch (e) {
    console.error('Load program detail failed:', e)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped lang="scss">
.program-detail {
  min-height: 100vh;
  padding: 0 0 80px;
  background: var(--color-bg);
}

/* Loading & Error States */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
}

.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  color: var(--color-muted);
  font-size: 16px;

  &__icon {
    font-size: 56px;
    margin-bottom: 16px;
    opacity: 0.5;
  }
}

/* Layout */
.detail-layout {
  display: grid;
  grid-template-columns: 1fr 340px;
  gap: 40px;
  align-items: flex-start;
  padding-top: 32px;
}

/* Hero Image */
.hero-image {
  position: relative;
  border-radius: var(--radius-lg);
  overflow: hidden;
  margin-bottom: 32px;
  animation: fadeInUp 0.5s ease both;

  img {
    width: 100%;
    max-height: 440px;
    object-fit: cover;
    display: block;
  }

  &__overlay {
    position: absolute;
    inset: 0;
    background: linear-gradient(
      to bottom,
      transparent 40%,
      rgba(13, 13, 15, 0.6) 75%,
      rgba(13, 13, 15, 0.9) 100%
    );
    pointer-events: none;
  }
}

.presale-badge {
  position: absolute;
  top: 16px;
  left: 16px;
  padding: 5px 14px;
  font-size: 13px;
  z-index: 2;
  animation: glowPulse 2s ease-in-out infinite;
}

/* Program Info */
.detail-info {
  margin-bottom: 36px;
  animation: fadeInUp 0.5s ease 0.1s both;

  &__title {
    font-family: var(--font-display);
    font-size: 32px;
    font-weight: 700;
    color: var(--color-text);
    margin: 0 0 20px;
    line-height: 1.3;
    letter-spacing: 0.5px;
  }

  &__meta {
    display: flex;
    flex-direction: column;
    gap: 14px;
  }
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 15px;
  color: var(--color-text);

  &__icon {
    color: var(--color-primary);
    flex-shrink: 0;
  }
}

/* Section Heading */
.section-heading {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 20px;

  &__bar {
    width: 4px;
    height: 20px;
    background: var(--gradient-primary);
    border-radius: 2px;
  }
}

/* Ticket Section */
.ticket-section {
  margin-bottom: 36px;
  animation: fadeInUp 0.5s ease 0.2s both;
}

.ticket-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(170px, 1fr));
  gap: 14px;
}

.ticket-card {
  padding: 20px 16px;
  border: 2px solid var(--color-border);
  border-radius: var(--radius-lg);
  background: var(--color-surface);
  cursor: pointer;
  transition: all var(--transition);
  text-align: center;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background: radial-gradient(circle at center, rgba(212, 168, 83, 0.06) 0%, transparent 70%);
    opacity: 0;
    transition: opacity var(--transition);
  }

  &:hover {
    border-color: var(--color-primary);
    background: var(--color-elevated);
    transform: translateY(-2px);

    &::before {
      opacity: 1;
    }
  }

  &--selected {
    border-color: var(--color-primary);
    background: var(--color-elevated);
    box-shadow: 0 0 20px rgba(212, 168, 83, 0.25), inset 0 0 20px rgba(212, 168, 83, 0.05);

    &::before {
      opacity: 1;
    }
  }

  &__name {
    font-weight: 600;
    margin-bottom: 10px;
    font-size: 15px;
    color: var(--color-text);
    position: relative;
  }

  &__price {
    font-size: 22px;
    font-weight: 700;
    color: var(--color-gold);
    margin-bottom: 6px;
    font-family: var(--font-display);
    position: relative;
  }

  &__remain {
    font-size: 12px;
    position: relative;
  }
}

/* Quantity Section */
.quantity-section {
  margin-bottom: 36px;
  animation: fadeInUp 0.4s ease both;
}

.quantity-row {
  display: flex;
  align-items: center;
  gap: 28px;
}

.quantity-selector {
  display: flex;
  align-items: center;
  border: 1px solid var(--color-border);
  border-radius: var(--radius);
  overflow: hidden;
  background: var(--color-surface);
}

.qty-btn {
  width: 40px;
  height: 40px;
  background: var(--color-elevated);
  border: none;
  font-size: 18px;
  color: var(--color-primary);
  cursor: pointer;
  transition: all var(--transition);
  font-weight: 600;

  &:hover:not(:disabled) {
    background: rgba(212, 168, 83, 0.15);
    color: var(--color-primary-hover);
  }

  &:disabled {
    opacity: 0.3;
    cursor: not-allowed;
    color: var(--color-muted);
  }
}

.qty-value {
  width: 52px;
  text-align: center;
  font-size: 17px;
  font-weight: 700;
  color: var(--color-text);
  border-left: 1px solid var(--color-border);
  border-right: 1px solid var(--color-border);
  line-height: 40px;
  background: var(--color-surface);
}

.total-price {
  font-size: 16px;

  &__value {
    font-size: 28px;
    font-weight: 700;
    color: var(--color-gold);
    font-family: var(--font-display);
    text-shadow: 0 0 20px rgba(255, 215, 0, 0.3);
  }
}

/* CTA Button */
.cta-section {
  margin-bottom: 48px;
  animation: fadeInUp 0.5s ease 0.3s both;
}

.cta-btn {
  width: 280px;
  height: 56px;
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 2px;
  border: none;
  border-radius: var(--radius-lg);
  background: var(--gradient-primary);
  color: var(--color-bg);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background: linear-gradient(135deg, transparent 0%, rgba(255, 255, 255, 0.15) 50%, transparent 100%);
    transform: translateX(-100%);
    transition: transform 0.6s ease;
  }

  &:hover:not(:disabled) {
    transform: translateY(-3px);
    box-shadow: 0 8px 32px rgba(212, 168, 83, 0.4), 0 0 60px rgba(212, 168, 83, 0.15);

    &::before {
      transform: translateX(100%);
    }
  }

  &:active:not(:disabled) {
    transform: translateY(-1px);
  }

  &:disabled {
    opacity: 0.4;
    cursor: not-allowed;
    filter: grayscale(0.5);
  }
}

/* Detail Tabs */
.detail-tabs {
  animation: fadeInUp 0.5s ease 0.35s both;

  &__header {
    display: flex;
    border-bottom: 1px solid var(--color-border);
    margin-bottom: 28px;
    gap: 4px;
  }

  &__tab {
    padding: 14px 28px;
    background: none;
    border: none;
    font-size: 15px;
    font-weight: 500;
    color: var(--color-muted);
    cursor: pointer;
    border-bottom: 2px solid transparent;
    transition: all var(--transition);
    position: relative;

    &:hover {
      color: var(--color-primary-hover);
    }

    &--active {
      color: var(--color-primary);
      border-bottom-color: var(--color-primary);
      font-weight: 600;
    }
  }

  &__content {
    padding: 20px 0;
    font-size: 15px;
    line-height: 1.9;
    color: var(--color-text);

    :deep(img) {
      max-width: 100%;
      border-radius: var(--radius-lg);
      margin: 12px 0;
    }

    :deep(p) {
      margin-bottom: 12px;
      color: var(--color-text);
    }
  }
}

/* Sidebar */
.detail-sidebar {
  position: sticky;
  top: 32px;
}

.sidebar-card {
  background: var(--color-card-bg);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-card);
  padding: 28px;

  &__title {
    font-size: 17px;
    font-weight: 600;
    color: var(--color-text);
    margin: 0 0 22px;
    padding-bottom: 14px;
    border-bottom: 1px solid var(--color-border);
    font-family: var(--font-serif-cn);
    letter-spacing: 1px;
  }
}

.service-list {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.service-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;

  &__label {
    color: var(--color-muted);
    font-weight: 400;
  }

  &__value {
    font-weight: 500;
    color: var(--color-text);
  }
}

/* Responsive */
@media (max-width: 960px) {
  .detail-layout {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .detail-sidebar {
    position: static;
  }

  .detail-info__title {
    font-size: 24px;
  }

  .cta-btn {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .ticket-cards {
    grid-template-columns: 1fr 1fr;
  }

  .quantity-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
}
</style>
