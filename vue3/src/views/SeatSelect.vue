<template>
  <component :is="layoutComponent">
    <div class="seat-select-page">
      <!-- Top Bar -->
      <div class="top-bar">
        <div class="container top-bar__inner">
          <div class="top-bar__info">
            <svg class="top-bar__icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
            <span class="top-bar__time">{{ showTime }}</span>
          </div>
          <div class="top-bar__legend">
            <div v-for="tier in priceTiers" :key="tier.price" class="legend-item">
              <span class="legend-dot" :style="{ background: tier.color }"></span>
              <span class="legend-item__price">¥{{ tier.price }}</span>
            </div>
            <div class="legend-item">
              <span class="legend-dot legend-dot--sold"></span>
              <span>已售</span>
            </div>
            <div class="legend-item">
              <span class="legend-dot legend-dot--selected"></span>
              <span>已选</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p class="text-muted mt-2">加载座位信息...</p>
      </div>

      <!-- Seat Map -->
      <div v-else class="seat-map-container">
        <!-- Stage with spotlight -->
        <div class="stage">
          <div class="stage__glow"></div>
          <div class="stage__platform">
            <div class="stage__label">舞 台</div>
          </div>
          <div class="stage__edge"></div>
        </div>

        <!-- Seat Grid -->
        <div class="seat-grid">
          <div class="seat-grid__inner">
            <div v-for="(row, rowIndex) in seatRows" :key="rowIndex" class="seat-row">
              <span class="seat-row__label">{{ row.label }}</span>
              <div class="seat-row__seats">
                <div
                  v-for="seat in row.seats"
                  :key="`${seat.row}-${seat.col}`"
                  class="seat"
                  :class="getSeatClass(seat)"
                  :style="getSeatStyle(seat)"
                  :title="getSeatTooltip(seat)"
                  @click="toggleSeat(seat)"
                >
                  <span class="seat__number">{{ seat.col }}</span>
                </div>
              </div>
              <span class="seat-row__label">{{ row.label }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Bottom Bar -->
      <div class="bottom-bar">
        <div class="bottom-bar__border-glow"></div>
        <div class="container bottom-bar__inner">
          <div class="bottom-bar__selected">
            <div v-if="selectedSeats.length === 0" class="bottom-bar__empty">请点击座位图选座</div>
            <div v-else class="selected-tags">
              <span v-for="seat in selectedSeats" :key="`${seat.row}-${seat.col}`" class="selected-tag">
                {{ seat.row }}排{{ seat.col }}座 ¥{{ seat.price }}
                <button class="selected-tag__remove" @click="removeSeat(seat)">×</button>
              </span>
            </div>
          </div>
          <div class="bottom-bar__actions">
            <div class="bottom-bar__total">
              <span class="text-muted">合计：</span>
              <span class="total-value">¥{{ totalPrice }}</span>
            </div>
            <button class="btn btn-primary btn-lg confirm-btn" :disabled="selectedSeats.length === 0" @click="confirmSelection">
              确认选座 ({{ selectedSeats.length }})
            </button>
          </div>
        </div>
      </div>
    </div>
  </component>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getSeatInfo } from '@/api/program'
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
const seatRows = ref([])
const selectedSeats = ref([])
const priceTiers = ref([])
const showTime = ref('')
const tierColors = ['#6366F1', '#EC4899', '#10B981', '#F59E0B', '#3B82F6', '#8B5CF6', '#EF4444', '#14B8A6']

const totalPrice = computed(() => {
  return selectedSeats.value.reduce((sum, s) => sum + (s.price || 0), 0).toFixed(2)
})

const getSeatClass = (seat) => {
  if (seat.status === 'sold') return 'seat--sold'
  if (selectedSeats.value.find(s => s.row === seat.row && s.col === seat.col)) return 'seat--selected'
  return 'seat--available'
}

const getSeatStyle = (seat) => {
  if (seat.status === 'sold') return {}
  const tier = priceTiers.value.find(t => t.price === seat.price)
  return { '--seat-color': tier ? tier.color : '#6366F1' }
}

const getSeatTooltip = (seat) => {
  if (seat.status === 'sold') return '已售出'
  return `${seat.row}排${seat.col}座 ¥${seat.price}`
}

const toggleSeat = (seat) => {
  if (seat.status === 'sold') return
  const idx = selectedSeats.value.findIndex(s => s.row === seat.row && s.col === seat.col)
  if (idx > -1) {
    selectedSeats.value.splice(idx, 1)
  } else {
    if (selectedSeats.value.length >= 6) { toast.error('最多选择6个座位'); return }
    selectedSeats.value.push({ ...seat })
  }
}

const removeSeat = (seat) => {
  const idx = selectedSeats.value.findIndex(s => s.row === seat.row && s.col === seat.col)
  if (idx > -1) selectedSeats.value.splice(idx, 1)
}

const confirmSelection = () => {
  if (selectedSeats.value.length === 0) return
  sessionStorage.setItem('selectedSeats', JSON.stringify(selectedSeats.value))
  router.push({
    path: '/order/index',
    query: { programId: route.query.programId, ticketCategoryId: route.query.ticketCategoryId, count: selectedSeats.value.length, seatSelect: '1' }
  })
}

onMounted(async () => {
  const { programId, ticketCategoryId } = route.query
  if (!programId || !ticketCategoryId) { toast.error('参数错误'); loading.value = false; return }
  try {
    const res = await getSeatInfo({ programId, ticketCategoryId })
    if (res.code == 0) {
      const data = res.data || {}
      showTime.value = data.showTime || ''
      const seatList = data.seatList || data.seats || []
      const priceSet = new Map()
      const rowMap = new Map()
      seatList.forEach(seat => {
        const row = seat.rowCode || seat.rowNo || seat.row
        const col = seat.colCode || seat.colNo || seat.col
        const price = seat.price || 0
        const status = seat.status === 1 || seat.status === 'sold' || seat.sellStatus !== '1' ? 'sold' : 'available'
        if (!priceSet.has(price)) priceSet.set(price, tierColors[priceSet.size % tierColors.length])
        if (!rowMap.has(row)) rowMap.set(row, [])
        rowMap.get(row).push({ ...seat, row, col, price, status })
      })
      seatRows.value = Array.from(rowMap.entries())
        .sort((a, b) => typeof a[0] === 'number' && typeof b[0] === 'number' ? a[0] - b[0] : String(a[0]).localeCompare(String(b[0])))
        .map(([label, seats]) => ({ label, seats: seats.sort((a, b) => typeof a.col === 'number' && typeof b.col === 'number' ? a.col - b.col : String(a.col).localeCompare(String(b.col))) }))
      priceTiers.value = Array.from(priceSet.entries()).map(([price, color]) => ({ price, color }))
    } else { toast.error('加载座位信息失败') }
  } catch (e) { console.error('Load seat info failed:', e); toast.error('网络错误') }
  finally { loading.value = false }
})
</script>

<style scoped lang="scss">
.seat-select-page {
  min-height: 100vh;
  background: var(--color-bg);
  padding-bottom: 130px;
  position: relative;
}

// ── Top Bar ──
.top-bar {
  background: var(--color-surface);
  border-bottom: 1px solid var(--color-border);
  padding: 14px 0;
  position: sticky;
  top: 65px;
  z-index: 100;
  backdrop-filter: blur(12px);

  &__inner {
    display: flex;
    align-items: center;
    justify-content: space-between;
    flex-wrap: wrap;
    gap: 12px;
  }

  &__info {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  &__icon {
    color: var(--color-primary);
    opacity: 0.8;
  }

  &__time {
    color: var(--color-text);
    font-size: 14px;
    font-weight: 500;
    font-family: var(--font-serif-cn);
    letter-spacing: 1px;
  }

  &__legend {
    display: flex;
    align-items: center;
    gap: 18px;
    flex-wrap: wrap;
  }
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: var(--color-muted);

  &__price {
    color: var(--color-text);
    font-weight: 500;
  }
}

.legend-dot {
  width: 14px;
  height: 14px;
  border-radius: 3px;

  &--sold {
    background: #333338;
    opacity: 0.6;
  }

  &--selected {
    background: #fff;
    border: 2px solid var(--color-primary);
    box-shadow: 0 0 8px rgba(212, 168, 83, 0.5);
  }
}

// ── Loading ──
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80px 20px;

  .spinner {
    border-color: var(--color-border);
    border-top-color: var(--color-primary);
  }
}

// ── Seat Map ──
.seat-map-container {
  padding: 40px 20px;
  max-width: 900px;
  margin: 0 auto;
}

// ── Stage ──
.stage {
  text-align: center;
  margin-bottom: 48px;
  position: relative;

  &__glow {
    position: absolute;
    top: -50px;
    left: 50%;
    transform: translateX(-50%);
    width: 340px;
    height: 140px;
    background: radial-gradient(ellipse at center bottom, rgba(212, 168, 83, 0.12) 0%, transparent 70%);
    pointer-events: none;
  }

  &__platform {
    position: relative;
    display: inline-block;
  }

  &__label {
    display: inline-block;
    padding: 12px 72px;
    background: linear-gradient(180deg, var(--color-elevated), var(--color-surface));
    border: 1px solid var(--color-border);
    border-radius: 4px 4px 50% 50%;
    color: var(--color-primary);
    font-size: 15px;
    font-weight: 600;
    font-family: var(--font-serif-cn);
    letter-spacing: 10px;
    text-shadow: 0 0 24px rgba(212, 168, 83, 0.6);
  }

  &__edge {
    width: 55%;
    height: 1px;
    margin: 0 auto;
    background: linear-gradient(90deg, transparent, var(--color-primary), transparent);
    opacity: 0.3;
    margin-top: 8px;
  }
}

// ── Seat Grid ──
.seat-grid {
  overflow-x: auto;
  padding: 20px 0;

  &__inner {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 6px;
    transform: perspective(800px) rotateX(5deg);
    transform-origin: center top;
  }
}

.seat-row {
  display: flex;
  align-items: center;
  gap: 8px;

  &__label {
    width: 24px;
    text-align: center;
    font-size: 11px;
    color: var(--color-muted);
    font-weight: 500;
    flex-shrink: 0;
    opacity: 0.7;
  }

  &__seats {
    display: flex;
    gap: 4px;
    justify-content: center;
  }
}

.seat {
  width: 28px;
  height: 28px;
  border-radius: 5px 5px 8px 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.15s ease;
  position: relative;

  &__number {
    font-size: 9px;
    font-weight: 500;
    color: rgba(255, 255, 255, 0.85);
  }

  &--available {
    background: var(--seat-color, var(--color-primary));

    &:hover {
      transform: scale(1.25);
      box-shadow: 0 0 14px var(--seat-color, var(--color-primary)),
                  0 0 4px rgba(212, 168, 83, 0.3);
      z-index: 5;
    }
  }

  &--selected {
    background: #fff;
    border: 2px solid var(--color-primary);
    box-shadow: 0 0 12px rgba(212, 168, 83, 0.6),
                0 0 28px rgba(212, 168, 83, 0.2);
    transform: scale(1.15);

    .seat__number {
      color: var(--color-bg);
      font-weight: 700;
    }
  }

  &--sold {
    background: #2a2a2e;
    cursor: not-allowed;
    opacity: 0.35;

    .seat__number {
      color: #555;
    }

    &:hover {
      transform: none;
    }
  }
}

// ── Bottom Bar ──
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: var(--color-surface);
  border-top: 1px solid var(--color-border);
  padding: 16px 0;
  z-index: 100;
  backdrop-filter: blur(16px);

  &__border-glow {
    position: absolute;
    top: -1px;
    left: 0;
    right: 0;
    height: 1px;
    background: linear-gradient(90deg, transparent 10%, rgba(212, 168, 83, 0.25) 50%, transparent 90%);
  }

  &__inner {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 20px;
  }

  &__selected {
    flex: 1;
    min-width: 0;
  }

  &__empty {
    color: var(--color-muted);
    font-size: 14px;
  }

  &__actions {
    display: flex;
    align-items: center;
    gap: 20px;
    flex-shrink: 0;
  }

  &__total {
    font-size: 14px;
    color: var(--color-text);
  }
}

.total-value {
  font-size: 22px;
  font-weight: 700;
  color: var(--color-gold);
  text-shadow: 0 0 10px rgba(255, 215, 0, 0.3);
}

.selected-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  max-height: 60px;
  overflow-y: auto;
}

.selected-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 12px;
  background: rgba(212, 168, 83, 0.1);
  border: 1px solid rgba(212, 168, 83, 0.35);
  border-radius: 6px;
  font-size: 12px;
  color: var(--color-primary);

  &__remove {
    background: none;
    border: none;
    color: var(--color-muted);
    font-size: 16px;
    padding: 0 2px;
    cursor: pointer;
    line-height: 1;

    &:hover {
      color: var(--color-error);
    }
  }
}

.confirm-btn {
  background: var(--gradient-primary);
  border: none;
  white-space: nowrap;
  letter-spacing: 2px;
  box-shadow: 0 4px 16px rgba(212, 168, 83, 0.3);

  &:hover:not(:disabled) {
    box-shadow: 0 6px 24px rgba(212, 168, 83, 0.45);
  }

  &:disabled {
    opacity: 0.4;
    box-shadow: none;
  }
}
</style>
