<template>
  <component :is="layoutComponent">
    <div class="seat-manage">
    <div class="page-header">
      <h2 class="page-title">座位管理</h2>
      <div class="page-title__line"></div>
    </div>

    <!-- Select Program & Ticket Category -->
    <div class="filter-bar card">
      <div class="filter-bar__row">
        <div class="form-group">
          <label class="form-label">选择节目</label>
          <select v-model.number="programId" class="form-select" @change="onProgramChange">
            <option :value="null" disabled>搜索或选择节目</option>
            <option v-for="p in programs" :key="p.id" :value="p.id">{{ p.title }}</option>
          </select>
          <input v-model="programSearch" class="form-input" placeholder="输入节目名搜索..." @input="searchPrograms" style="margin-top:6px;" />
        </div>
        <div class="form-group">
          <label class="form-label">选择票档</label>
          <select v-model.number="ticketCategoryId" class="form-select">
            <option :value="null" disabled>请先选择节目</option>
            <option v-for="t in ticketCategories" :key="t.id" :value="t.id">{{ t.name }} (¥{{ t.price }})</option>
          </select>
        </div>
        <button class="btn btn-outline btn-sm" @click="loadSeats" style="align-self:flex-end;">查询已有座位</button>
      </div>
    </div>

    <!-- Existing Seats -->
    <div v-if="loading" class="loading-state"><div class="spinner"></div></div>
    <div v-else-if="seats.length > 0" class="seat-table-wrap card">
      <table class="seat-table">
        <thead><tr><th>座位ID</th><th>排</th><th>列</th><th>价格</th><th>状态</th></tr></thead>
        <tbody>
          <tr v-for="s in seats" :key="s.id">
            <td>{{ s.id }}</td><td>{{ s.rowCode }}排</td><td>{{ s.colCode }}座</td>
            <td>¥{{ s.price }}</td>
            <td :class="s.sellStatus === 1 ? 'text-warning' : 'text-success'">{{ s.sellStatus === 1 ? '已售' : '可售' }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Add Seats Form -->
    <div class="action-bar card">
      <h3 class="action-bar__title">批量生成座位</h3>
      <div class="form-grid">
        <div class="form-group">
          <label class="form-label">排数</label>
          <input v-model.number="genRows" type="number" class="form-input" min="1" max="50" placeholder="如 10" />
        </div>
        <div class="form-group">
          <label class="form-label">每排列数</label>
          <input v-model.number="genCols" type="number" class="form-input" min="1" max="50" placeholder="如 20" />
        </div>
        <div class="form-group">
          <label class="form-label">起始排号</label>
          <input v-model.number="genRowStart" type="number" class="form-input" min="1" placeholder="如 1" />
        </div>
        <div class="form-group">
          <label class="form-label">起始列号</label>
          <input v-model.number="genColStart" type="number" class="form-input" min="1" placeholder="如 1" />
        </div>
      </div>
      <button class="btn btn-outline btn-sm" @click="generatePreview" style="margin-top:12px;">生成预览</button>

      <!-- Preview Grid -->
      <div v-if="preview.length > 0" class="preview-section">
        <p class="text-muted" style="font-size:12px;margin-bottom:8px;">点击格子可取消不想要的座位，再次点击恢复</p>
        <div class="preview-grid">
          <div v-for="row in preview" :key="row.row" class="preview-row">
            <span class="preview-row-label">{{ row.row }}排</span>
            <span
              v-for="seat in row.seats"
              :key="`${row.row}-${seat.col}`"
              class="preview-seat"
              :class="{ 'preview-seat--off': seat.off }"
              @click="seat.off = !seat.off"
            >{{ seat.col }}</span>
          </div>
        </div>
        <button class="btn btn-primary btn-sm" @click="batchAdd" :disabled="adding || !ticketCategoryId" style="margin-top:16px;">
          确认添加 {{ preview.reduce((s,r) => s + r.seats.filter(x => !x.off).length, 0) }} 个座位
        </button>
      </div>
    </div>
    </div>
  </component>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import AdminLayout from '@/layouts/AdminLayout.vue'
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import { getSeatInfo, searchPrograms as searchApi } from '@/api/program'
import { batchAddSeats } from '@/api/admin'
import { useToast } from '@/components/Toast.vue'

const route = useRoute()
const toast = useToast()
const layoutComponent = computed(() => route.meta.layout === 'admin' ? AdminLayout : DefaultLayout)

const programs = ref([])
const programId = ref(null)
const programSearch = ref('')
const ticketCategories = ref([])
const ticketCategoryId = ref(null)
const seats = ref([])
const loading = ref(false)
const adding = ref(false)

const genRows = ref(5)
const genCols = ref(10)
const genRowStart = ref(1)
const genColStart = ref(1)
const preview = ref([])

let searchTimer = null

async function searchPrograms() {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(async () => {
    const kw = programSearch.value.trim()
    if (!kw) { programs.value = []; return }
    try {
      const res = await searchApi({ content: kw, pageNumber: 1, pageSize: 20, timeType: 0 })
      if (res.code == 0) programs.value = (res.data?.records || res.data?.list || [])
    } catch (e) { /* ignore */ }
  }, 300)
}

async function onProgramChange() {
  ticketCategories.value = []; ticketCategoryId.value = null
  if (!programId.value) return
  try {
    const res = await getSeatInfo({ programId: programId.value })
    if (res.code == 0) {
      const data = res.data || {}
      ticketCategories.value = data.ticketCategoryList || data.ticketCategories || []
    }
  } catch (e) { /* ignore */ }
}

async function loadSeats() {
  if (!programId.value || !ticketCategoryId.value) { toast.error('请选择节目和票档'); return }
  loading.value = true
  try {
    const res = await getSeatInfo({ programId: programId.value, ticketCategoryId: ticketCategoryId.value })
    if (res.code == 0) {
      const data = res.data || {}
      seats.value = data.seatList || data.seats || []
    } else toast.error(res.message || '查询失败')
  } catch (e) { toast.error('网络错误') }
  finally { loading.value = false }
}

function generatePreview() {
  const list = []
  for (let r = 0; r < genRows.value; r++) {
    const rowNum = genRowStart.value + r
    const row = { row: rowNum, seats: [] }
    for (let c = 0; c < genCols.value; c++) {
      row.seats.push({ col: genColStart.value + c, off: false })
    }
    list.push(row)
  }
  preview.value = list
}

async function batchAdd() {
  if (!ticketCategoryId.value) { toast.error('请先选择票档'); return }
  const seatList = []
  for (const row of preview.value) {
    for (const seat of row.seats) {
      if (!seat.off) {
        seatList.push({ ticketCategoryId: ticketCategoryId.value, rowCode: row.row, colCode: seat.col })
      }
    }
  }
  if (!seatList.length) { toast.error('没有可添加的座位'); return }
  adding.value = true
  try {
    const res = await batchAddSeats(seatList)
    if (res.code == 0) { toast.success(`成功添加 ${seatList.length} 个座位`); preview.value = []; loadSeats() }
    else toast.error(res.message || '添加失败')
  } catch (e) { toast.error('网络错误') }
  finally { adding.value = false }
}
</script>

<style scoped lang="scss">
.seat-manage { color: var(--color-text); max-width: 800px; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 24px; font-family: var(--font-serif-cn); letter-spacing: 2px; margin: 0 0 12px; }
.page-title__line { width: 60px; height: 2px; background: var(--gradient-primary); border-radius: 1px; }
.filter-bar { padding: 20px; margin-bottom: 16px; }
.filter-bar__row { display: flex; gap: 16px; align-items: flex-end; flex-wrap: wrap; }
.form-group { display: flex; flex-direction: column; gap: 6px; min-width: 160px; }
.form-label { font-size: 13px; color: var(--color-muted); }
.form-input, .form-select { padding: 8px 12px; background: var(--color-elevated); border: 1px solid var(--color-border); border-radius: var(--radius); color: var(--color-text); font-size: 14px; }
.form-input:focus, .form-select:focus { border-color: var(--color-primary); box-shadow: 0 0 0 3px rgba(212,168,83,0.1); outline: none; }
.form-select { cursor: pointer; }
.form-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; }
.action-bar { padding: 20px; margin-top: 16px; }
.action-bar__title { font-size: 15px; font-weight: 600; margin-bottom: 8px; font-family: var(--font-serif-cn); letter-spacing: 1px; }
.preview-section { margin-top: 16px; }
.preview-grid { display: flex; flex-direction: column; gap: 4px; max-height: 400px; overflow-y: auto; }
.preview-row { display: flex; align-items: center; gap: 4px; flex-wrap: wrap; }
.preview-row-label { font-size: 11px; color: var(--color-muted); width: 36px; text-align: right; flex-shrink: 0; }
.preview-seat { width: 28px; height: 24px; display: flex; align-items: center; justify-content: center; font-size: 10px; background: rgba(212,168,83,0.2); border: 1px solid rgba(212,168,83,0.3); border-radius: 3px; cursor: pointer; color: var(--color-primary); transition: all 0.15s; }
.preview-seat:hover { background: rgba(212,168,83,0.4); }
.preview-seat--off { background: rgba(239,68,68,0.1); border-color: rgba(239,68,68,0.2); color: var(--color-error); text-decoration: line-through; }
.seat-table-wrap { padding: 0; overflow-x: auto; margin-bottom: 16px; }
.seat-table { width: 100%; border-collapse: collapse; font-size: 13px; }
.seat-table th { text-align: left; padding: 12px 14px; color: var(--color-muted); font-weight: 600; border-bottom: 1px solid var(--color-border); }
.seat-table td { padding: 10px 14px; border-bottom: 1px solid rgba(212,168,83,0.06); }
.seat-table tr:hover td { background: rgba(212,168,83,0.03); }
.text-warning { color: var(--color-warning); }
.text-success { color: var(--color-success); }
.card { background: var(--color-card-bg); backdrop-filter: blur(12px); border: 1px solid var(--color-border); border-radius: var(--radius-lg); box-shadow: var(--shadow-card); }
.loading-state { display: flex; justify-content: center; padding: 60px; }
</style>
