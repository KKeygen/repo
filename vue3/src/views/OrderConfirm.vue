<template>
  <component :is="layoutComponent">
    <div class="order-confirm-page">
      <div class="container">
        <div class="page-header">
          <h2 class="page-title">确认订单</h2>
          <div class="page-title__line"></div>
        </div>

        <div v-if="loading" class="loading-state">
          <div class="spinner"></div>
          <p class="text-muted mt-2">加载中...</p>
        </div>

        <div v-else class="order-sections">
          <!-- Program Summary -->
          <div class="order-section">
            <div class="section-icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/></svg>
            </div>
            <h3 class="section-title">演出信息</h3>
            <div class="program-summary">
              <h4 class="program-summary__title">{{ programInfo.title }}</h4>
              <p class="text-muted">{{ programInfo.showTime }}</p>
              <p class="text-muted">{{ programInfo.venue }}</p>
              <div class="price-row">
                <span class="text-muted">{{ programInfo.ticketName }}</span>
                <span class="text-gold font-bold ml-2">¥{{ programInfo.price }}</span>
                <span class="text-muted ml-2">× {{ count }}</span>
              </div>
              <div v-if="selectedSeats.length > 0" class="seat-info">
                <span class="text-muted">座位：</span>
                <span v-for="seat in selectedSeats" :key="`${seat.row}-${seat.col}`" class="seat-tag">{{ seat.row }}排{{ seat.col }}座</span>
              </div>
            </div>
          </div>

          <!-- Ticket Users -->
          <div class="order-section">
            <div class="section-header">
              <div class="section-header__left">
                <div class="section-icon">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                </div>
                <h3 class="section-title">购票人信息</h3>
              </div>
              <button class="btn btn-sm btn-outline" @click="showAddForm = true">+ 添加购票人</button>
            </div>
            <div v-if="ticketUsers.length === 0 && !showAddForm" class="empty-users">
              <p class="text-muted">暂无购票人，请先添加</p>
            </div>
            <div v-else class="user-list">
              <label v-for="user in ticketUsers" :key="user.id" class="user-item" :class="{ 'user-item--selected': selectedUserId === user.id }">
                <input v-model="selectedUserId" type="radio" :value="user.id" class="user-item__radio" />
                <div class="user-item__info">
                  <span class="user-item__name">{{ user.relName }}</span>
                  <span class="user-item__id">{{ getIdTypeName(user.relIdType) }} {{ maskId(user.relIdNumber) }}</span>
                </div>
              </label>
            </div>
            <div v-if="showAddForm" class="add-user-form">
              <div class="form-row">
                <input v-model="newUser.relName" type="text" class="form-input" placeholder="姓名" />
                <select v-model="newUser.relIdType" class="form-input select-input">
                  <option v-for="t in idTypes" :key="t.value" :value="t.value">{{ t.label }}</option>
                </select>
                <input v-model="newUser.relIdNumber" type="text" class="form-input" placeholder="证件号码" />
              </div>
              <div class="form-actions mt-2">
                <button class="btn btn-primary btn-sm" @click="handleAddUser">保存</button>
                <button class="btn btn-ghost btn-sm" @click="showAddForm = false">取消</button>
              </div>
            </div>
          </div>

          <!-- Contact -->
          <div class="order-section">
            <div class="section-icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72c.13.88.36 1.74.7 2.55a2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45c.81.34 1.67.57 2.55.7A2 2 0 0 1 22 16.92z"/></svg>
            </div>
            <h3 class="section-title">联系方式</h3>
            <p class="contact-phone">{{ userPhone || '未绑定手机号' }}</p>
          </div>

          <!-- Payment Method -->
          <div class="order-section">
            <div class="section-icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="1" y="4" width="22" height="16" rx="2" ry="2"/><line x1="1" y1="10" x2="23" y2="10"/></svg>
            </div>
            <h3 class="section-title">支付方式</h3>
            <label class="payment-option payment-option--selected">
              <input type="radio" checked disabled />
              <div class="alipay-icon"><span>支</span></div>
              <span>支付宝</span>
            </label>
          </div>

          <!-- Price Summary -->
          <div class="order-section price-summary">
            <div class="price-summary__row">
              <span>票价</span>
              <span>¥{{ programInfo.price }} × {{ count }}</span>
            </div>
            <div class="price-summary__divider"></div>
            <div class="price-summary__total">
              <span>应付总额</span>
              <span class="total-amount">¥{{ totalAmount }}</span>
            </div>
            <button class="btn btn-primary btn-lg btn-block submit-btn" :disabled="!selectedUserId || submitting" @click="handleSubmit">
              <span v-if="submitting" class="spinner"></span>
              <span v-else>提交订单</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </component>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getTicketUserList, addTicketUser } from '@/api/ticketUser'
import { createOrderV1, createOrderV2, createOrderV3, createOrderV4, getOrderCache } from '@/api/order'
import { getProgramDetail } from '@/api/program'
import { useToast } from '@/components/Toast.vue'
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import AccountLayout from '@/layouts/AccountLayout.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const toast = useToast()

const layoutComponent = computed(() => route.meta.layout === 'account' ? AccountLayout : DefaultLayout)

const loading = ref(true)
const submitting = ref(false)
const ticketUsers = ref([])
const selectedUserId = ref(null)
const showAddForm = ref(false)
const userPhone = ref('')
const selectedSeats = ref([])
const count = ref(1)

const programInfo = reactive({ id: '', title: '', showTime: '', venue: '', ticketName: '', ticketCategoryId: '', price: 0 })

const idTypes = [
  { value: 1, label: '身份证' }, { value: 2, label: '港澳台居民证' },
  { value: 3, label: '港澳通行证' }, { value: 4, label: '台湾通行证' },
  { value: 5, label: '护照' }, { value: 6, label: '外国人永居证' }
]

const newUser = reactive({ relName: '', relIdType: 1, relIdNumber: '' })

const totalAmount = computed(() => (programInfo.price * count.value).toFixed(2))

const getIdTypeName = (type) => idTypes.find(t => t.value === type)?.label || '证件'
const maskId = (id) => { if (!id || id.length < 6) return id || ''; return id.slice(0, 3) + '****' + id.slice(-4) }

const loadTicketUsers = async () => {
  try {
    const res = await getTicketUserList({ userId: userStore.userId })
    if (res.code == 0) {
      ticketUsers.value = res.data || []
      if (ticketUsers.value.length > 0 && !selectedUserId.value) selectedUserId.value = ticketUsers.value[0].id
    }
  } catch (e) { console.error('Load ticket users failed:', e) }
}

const handleAddUser = async () => {
  if (!newUser.relName.trim() || !newUser.relIdNumber.trim()) { toast.error('请填写完整信息'); return }
  try {
    const res = await addTicketUser({ userId: userStore.userId, relName: newUser.relName, idType: newUser.relIdType, idNumber: newUser.relIdNumber })
    if (res.code == 0) {
      toast.success('添加成功'); showAddForm.value = false; newUser.relName = ''; newUser.relIdNumber = ''; newUser.relIdType = 1
      await loadTicketUsers()
    } else { toast.error(res.msg || '添加失败') }
  } catch (e) { toast.error('网络错误') }
}

const handleSubmit = async () => {
  if (!selectedUserId.value) { toast.error('请选择购票人'); return }
  submitting.value = true
  const selectedUser = ticketUsers.value.find(u => u.id === selectedUserId.value)
  const params = {
    programId: programInfo.id,
    userId: userStore.userId,
    ticketUserIdList: [selectedUserId.value]
  }
  if (selectedSeats.value.length > 0) {
    params.seatDtoList = selectedSeats.value.map(s => ({
      id: s.id,
      ticketCategoryId: s.ticketCategoryId || programInfo.ticketCategoryId,
      rowCode: s.rowCode || s.row,
      colCode: s.colCode || s.col,
      price: s.price
    }))
  } else {
    params.ticketCategoryId = programInfo.ticketCategoryId
    params.ticketCount = count.value
  }
  try {
    const version = import.meta.env.VITE_CREATE_ORDER_VERSION || '4'
    const createOrder = { '1': createOrderV1, '2': createOrderV2, '3': createOrderV3 }[version] || createOrderV4
    const res = await createOrder(params)
    if (res.code == 0) {
      toast.success('订单创建成功')
      router.push({ path: '/order/payMethod', query: { orderNumber: res.data.orderNumber || res.data.id } })
    } else { toast.error(res.msg || '创建订单失败') }
  } catch (e) { toast.error('网络错误，请稍后重试') }
  finally { submitting.value = false }
}

onMounted(async () => {
  const { programId, ticketCategoryId, count: qCount, seatSelect } = route.query
  count.value = parseInt(qCount) || 1
  programInfo.id = programId
  programInfo.ticketCategoryId = ticketCategoryId
  if (seatSelect === '1') {
    const stored = sessionStorage.getItem('selectedSeats')
    if (stored) try { selectedSeats.value = JSON.parse(stored) } catch (e) { /* ignore */ }
  }
  try {
    const [programRes] = await Promise.all([getProgramDetail({ id: programId }), loadTicketUsers()])
    if (programRes?.code == 0) {
      const data = programRes.data
      programInfo.title = data.title; programInfo.showTime = data.showTime || data.time || ''
      programInfo.venue = data.place || data.venue || ''; userPhone.value = data.mobile || ''
      const ticket = (data.ticketCategoryList || []).find(t => String(t.id) === String(ticketCategoryId))
      if (ticket) { programInfo.ticketName = ticket.name; programInfo.price = ticket.price }
    }
    try {
      const cacheRes = await getOrderCache({ programId, ticketCategoryId })
      if (cacheRes.code == 0 && cacheRes.data?.ticketUserId) selectedUserId.value = cacheRes.data.ticketUserId
    } catch (e) { /* ignore */ }
  } catch (e) { console.error('Load order data failed:', e) }
  finally { loading.value = false }
})
</script>

<style scoped lang="scss">
.order-confirm-page {
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

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px 0;

  .spinner {
    border-color: var(--color-border);
    border-top-color: var(--color-primary);
  }
}

.order-sections {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

// ── Glass-morphism Section Cards ──
.order-section {
  background: var(--color-card-bg);
  border: 1px solid var(--color-border);
  border-radius: 12px;
  padding: 24px;
  backdrop-filter: blur(12px);
  transition: border-color 0.25s ease;

  &:hover {
    border-color: rgba(212, 168, 83, 0.3);
  }
}

.section-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  border-radius: 8px;
  background: rgba(212, 168, 83, 0.1);
  color: var(--color-primary);
  margin-bottom: 12px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  color: var(--color-text);
  font-family: var(--font-serif-cn);
  letter-spacing: 1px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;

  &__left {
    display: flex;
    align-items: center;
    gap: 12px;

    .section-icon {
      margin-bottom: 0;
    }

    .section-title {
      margin-bottom: 0;
    }
  }
}

// ── Program Summary ──
.program-summary {
  &__title {
    font-size: 18px;
    font-weight: 600;
    margin-bottom: 10px;
    color: var(--color-text);
  }

  p {
    font-size: 14px;
    margin-bottom: 4px;
  }
}

.price-row {
  margin-top: 14px;
  padding-top: 12px;
  border-top: 1px solid var(--color-border);
  font-size: 14px;
}

.seat-info {
  margin-top: 12px;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.seat-tag {
  padding: 3px 12px;
  background: rgba(212, 168, 83, 0.1);
  border: 1px solid rgba(212, 168, 83, 0.25);
  border-radius: 6px;
  font-size: 13px;
  color: var(--color-primary);
}

// ── Ticket Users ──
.empty-users {
  padding: 24px;
  text-align: center;
  border: 1px dashed var(--color-border);
  border-radius: 8px;
}

.user-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.user-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border: 1px solid var(--color-border);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  background: rgba(26, 26, 30, 0.5);

  &:hover {
    border-color: var(--color-primary);
    background: rgba(212, 168, 83, 0.04);
  }

  &--selected {
    border-color: var(--color-primary);
    background: rgba(212, 168, 83, 0.06);
    box-shadow: 0 0 0 1px rgba(212, 168, 83, 0.15);
  }

  &__radio {
    accent-color: var(--color-primary);
    width: 18px;
    height: 18px;
  }

  &__info {
    display: flex;
    gap: 16px;
    align-items: center;
  }

  &__name {
    font-weight: 500;
    font-size: 15px;
    color: var(--color-text);
  }

  &__id {
    font-size: 13px;
    color: var(--color-muted);
  }
}

.add-user-form {
  margin-top: 16px;
  padding: 20px;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 10px;
}

.form-row {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;

  .form-input {
    flex: 1;
    min-width: 120px;
  }
}

.select-input {
  max-width: 160px;
}

.form-actions {
  display: flex;
  gap: 8px;
}

// ── Contact ──
.contact-phone {
  font-size: 15px;
  font-weight: 500;
  color: var(--color-text);
  letter-spacing: 1px;
}

// ── Payment Method ──
.payment-option {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border: 1px solid var(--color-border);
  border-radius: 10px;
  transition: all 0.2s ease;

  &--selected {
    border-color: var(--color-primary);
    background: rgba(212, 168, 83, 0.04);
  }

  input {
    accent-color: var(--color-primary);
  }
}

.alipay-icon {
  width: 34px;
  height: 34px;
  border-radius: 8px;
  background: #1677FF;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  font-weight: 700;
}

// ── Price Summary ──
.price-summary {
  &__row {
    display: flex;
    justify-content: space-between;
    font-size: 14px;
    padding: 8px 0;
    color: var(--color-muted);
  }

  &__divider {
    height: 1px;
    background: var(--color-border);
    margin: 12px 0;
  }

  &__total {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 0 0;
    font-size: 16px;
    font-weight: 500;
    color: var(--color-text);
  }
}

.total-amount {
  font-size: 26px;
  font-weight: 700;
  color: var(--color-gold);
  text-shadow: 0 0 12px rgba(255, 215, 0, 0.25);
}

.submit-btn {
  margin-top: 24px;
  height: 54px;
  font-size: 17px;
  font-weight: 600;
  background: var(--gradient-primary);
  border: none;
  letter-spacing: 3px;
  box-shadow: 0 4px 20px rgba(212, 168, 83, 0.3);

  &:hover:not(:disabled) {
    box-shadow: 0 6px 28px rgba(212, 168, 83, 0.45);
    transform: translateY(-1px);
  }

  &:disabled {
    opacity: 0.4;
    box-shadow: none;
  }

  .spinner {
    border-color: rgba(255, 255, 255, 0.3);
    border-top-color: #fff;
  }
}
</style>
