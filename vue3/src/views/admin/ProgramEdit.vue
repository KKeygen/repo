<template>
  <component :is="layoutComponent">
    <div class="program-edit">
    <div class="page-header">
      <h2 class="page-title">{{ isEdit ? '编辑节目' : '新增节目' }}</h2>
      <div class="page-title__line"></div>
    </div>

    <form class="edit-form card" @submit.prevent="handleSubmit">
      <!-- 基本信息 -->
      <div class="form-section">
        <h3 class="form-section__title">基本信息</h3>
        <div class="form-grid">
          <div class="form-group form-group--full">
            <label class="form-label">节目标题 <span class="required">*</span></label>
            <input v-model="form.title" class="form-input" placeholder="请输入节目名称" />
          </div>
          <div class="form-group">
            <label class="form-label">艺人</label>
            <input v-model="form.actor" class="form-input" placeholder="主要演员/艺人" />
          </div>
          <div class="form-group">
            <label class="form-label">场馆 <span class="required">*</span></label>
            <input v-model="form.place" class="form-input" placeholder="演出场馆名称" />
          </div>
          <div class="form-group">
            <label class="form-label">所在城市 <span class="required">*</span></label>
            <select v-model.number="form.areaId" class="form-select">
              <option :value="null" disabled>请选择城市</option>
              <option v-for="c in cities" :key="c.id" :value="c.id">{{ c.name }}</option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-label">演出大类 <span class="required">*</span></label>
            <select v-model.number="form.parentProgramCategoryId" class="form-select" @change="onParentChange">
              <option :value="null" disabled>请选择分类</option>
              <option v-for="c in parentCategories" :key="c.id" :value="c.id">{{ c.name }}</option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-label">子分类 <span class="required">*</span></label>
            <select v-model.number="form.programCategoryId" class="form-select" :disabled="subCategoryDisabled">
              <option v-if="subCategoryDisabled" :value="form.parentProgramCategoryId">无子分类（使用大类）</option>
              <option v-else :value="null" disabled>请先选择大类</option>
              <option v-for="c in subCategories" :key="c.id" :value="c.id">{{ c.name }}</option>
            </select>
          </div>
          <div class="form-group form-group--full">
            <label class="form-label">节目图片URL</label>
            <input v-model="form.itemPicture" class="form-input" placeholder="图片链接" />
          </div>
        </div>
      </div>

      <!-- 项目详情 -->
      <div class="form-section">
        <h3 class="form-section__title">项目详情</h3>
        <div class="form-group">
          <label class="form-label">项目详情</label>
          <textarea v-model="form.detail" class="form-textarea" rows="4" placeholder="节目详细介绍（支持HTML）..."></textarea>
        </div>
      </div>

      <!-- 购票规则 -->
      <div class="form-section">
        <h3 class="form-section__title">购票规则</h3>
        <div class="form-grid">
          <div class="form-group">
            <label class="form-label">限购规则</label>
            <input v-model="form.purchaseLimitRule" class="form-input" placeholder="如：每笔订单限购6张" />
          </div>
          <div class="form-group">
            <label class="form-label">退票/换票规则</label>
            <input v-model="form.refundTicketRule" class="form-input" placeholder="如：条件退" />
          </div>
          <div class="form-group">
            <label class="form-label">入场规则</label>
            <input v-model="form.entryRule" class="form-input" placeholder="如：凭电子票入场" />
          </div>
          <div class="form-group">
            <label class="form-label">儿童购票</label>
            <input v-model="form.childPurchase" class="form-input" placeholder="如：1.2米以下免票" />
          </div>
          <div class="form-group">
            <label class="form-label">实名购票规则</label>
            <input v-model="form.realTicketPurchaseRule" class="form-input" placeholder="如：一证一票" />
          </div>
          <div class="form-group">
            <label class="form-label">发票说明</label>
            <input v-model="form.invoiceSpecification" class="form-input" placeholder="如：支持电子发票" />
          </div>
        </div>
      </div>

      <!-- 演出信息 -->
      <div class="form-section">
        <h3 class="form-section__title">演出信息</h3>
        <div class="form-grid">
          <div class="form-group">
            <label class="form-label">演出时间 <span class="required">*</span></label>
            <input v-model="form.showTime" type="datetime-local" class="form-input" />
          </div>
          <div class="form-group">
            <label class="form-label">演出日期</label>
            <input v-model="form.showDayTime" class="form-input" placeholder="自动生成" readonly />
          </div>
          <div class="form-group">
            <label class="form-label">演出星期</label>
            <input v-model="form.showWeekTime" class="form-input" placeholder="自动生成" readonly />
          </div>
          <div class="form-group">
            <label class="form-label">演出时长</label>
            <input v-model="form.performanceDuration" class="form-input" placeholder="如：约120分钟" />
          </div>
          <div class="form-group">
            <label class="form-label">入场时间</label>
            <input v-model="form.entryTime" class="form-input" placeholder="如：演出前30分钟" />
          </div>
          <div class="form-group">
            <label class="form-label">最低演出曲目</label>
            <input v-model.number="form.minPerformanceCount" type="number" class="form-input" />
          </div>
          <div class="form-group">
            <label class="form-label">主要演员</label>
            <input v-model="form.mainActor" class="form-input" />
          </div>
          <div class="form-group">
            <label class="form-label">最低演出时长</label>
            <input v-model="form.minPerformanceDuration" class="form-input" placeholder="如：约90分钟" />
          </div>
          <div class="form-group">
            <label class="form-label">禁止携带物品</label>
            <input v-model="form.prohibitedItem" class="form-input" placeholder="如：食品饮料" />
          </div>
          <div class="form-group">
            <label class="form-label">寄存说明</label>
            <input v-model="form.depositSpecification" class="form-input" />
          </div>
          <div class="form-group">
            <label class="form-label">配送信息说明</label>
            <input v-model="form.deliveryInstruction" class="form-input" />
          </div>
          <div class="form-group">
            <label class="form-label">异常排单说明</label>
            <input v-model="form.abnormalOrderDescription" class="form-input" />
          </div>
          <div class="form-group form-group--full">
            <label class="form-label">温馨提示</label>
            <input v-model="form.kindReminder" class="form-input" />
          </div>
        </div>
      </div>

      <!-- 开关设置 -->
      <div class="form-section">
        <h3 class="form-section__title">功能开关</h3>
        <div class="switch-row">
          <label class="switch-label">
            <span>允许退款</span>
            <select v-model.number="form.permitRefund" class="form-select">
              <option :value="1">是</option>
              <option :value="0">否</option>
            </select>
          </label>
          <label class="switch-label">
            <span>允许选座</span>
            <select v-model.number="form.permitChooseSeat" class="form-select">
              <option :value="1">是</option>
              <option :value="0">否</option>
            </select>
          </label>
          <label class="switch-label">
            <span>电子票</span>
            <select v-model.number="form.electronicDeliveryTicket" class="form-select">
              <option :value="1">是</option>
              <option :value="0">否</option>
            </select>
          </label>
          <label class="switch-label">
            <span>电子发票</span>
            <select v-model.number="form.electronicInvoice" class="form-select">
              <option :value="1">是</option>
              <option :value="0">否</option>
            </select>
          </label>
        </div>
        <div class="form-group" style="margin-top:16px;">
          <label class="form-label">总票数</label>
          <input v-model.number="form.totalCount" type="number" class="form-input" style="max-width:200px;" />
        </div>
      </div>

      <div class="form-actions">
        <button type="submit" class="btn btn-primary btn-lg" :disabled="saving">
          <span v-if="saving" class="spinner"></span>
          <span v-else>{{ isEdit ? '保存修改' : '创建节目' }}</span>
        </button>
        <router-link to="/admin/programs" class="btn btn-ghost btn-lg">取消</router-link>
      </div>
    </form>
    </div>
  </component>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AdminLayout from '@/layouts/AdminLayout.vue'
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import { addProgram, addShowTime } from '@/api/admin'
import { getProgramDetail, getCategoryTypes, getCategoryByParent } from '@/api/program'
import { getHotCities } from '@/api/area'
import { useToast } from '@/components/Toast.vue'

const route = useRoute()
const router = useRouter()
const toast = useToast()

const layoutComponent = computed(() => route.meta.layout === 'admin' ? AdminLayout : DefaultLayout)

const isEdit = ref(false)
const saving = ref(false)
const cities = ref([])
const parentCategories = ref([])
const subCategories = ref([])
const subCategoryDisabled = computed(() => subCategories.value.length === 0)

const form = reactive({
  title: '', actor: '', place: '', areaId: null, parentProgramCategoryId: null, programCategoryId: null,
  itemPicture: '', detail: '', totalCount: null,
  purchaseLimitRule: '', refundTicketRule: '', entryRule: '', childPurchase: '',
  realTicketPurchaseRule: '', invoiceSpecification: '', deliveryInstruction: '', abnormalOrderDescription: '',
  kindReminder: '', performanceDuration: '', entryTime: '', minPerformanceCount: null,
  mainActor: '', minPerformanceDuration: '', prohibitedItem: '', depositSpecification: '',
  permitRefund: 0, permitChooseSeat: 1, electronicDeliveryTicket: 1, electronicInvoice: 1,
  showTime: '', showDayTime: '', showWeekTime: ''
})

function toDateTimeLocal(value) {
  if (!value) return ''
  return value.replace(' ', 'T').slice(0, 16)
}

function toServerDateTime(value) {
  if (!value) return ''
  return value.replace('T', ' ') + ':00'
}

function pad2(n) { return n < 10 ? `0${n}` : `${n}` }

function updateShowFields(value) {
  if (!value) {
    form.showDayTime = ''
    form.showWeekTime = ''
    return
  }
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return
  const y = date.getFullYear()
  const m = pad2(date.getMonth() + 1)
  const d = pad2(date.getDate())
  form.showDayTime = `${y}-${m}-${d} 00:00:00`
  const weekMap = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  form.showWeekTime = weekMap[date.getDay()]
}

function countCjk(value) {
  if (!value) return 0
  const matches = value.match(/[\u4e00-\u9fff]/g)
  return matches ? matches.length : 0
}

function decodeBytes(value, encoding) {
  const bytes = Uint8Array.from(value, ch => ch.charCodeAt(0))
  return new TextDecoder(encoding).decode(bytes)
}

function fixMojibake(value) {
  if (!value || !/[\u00C0-\u00FF]/.test(value)) return value
  try {
    const utf8Decoded = decodeBytes(value, 'utf-8')
    const utf8Score = countCjk(utf8Decoded)
    const originalScore = countCjk(value)
    if (utf8Score >= originalScore && /[\u4e00-\u9fff]/.test(utf8Decoded)) {
      return utf8Decoded
    }
  } catch (e) {
    // ignore
  }
  try {
    const gbkDecoded = decodeBytes(value, 'gbk')
    const gbkScore = countCjk(gbkDecoded)
    const originalScore = countCjk(value)
    if (gbkScore >= originalScore && /[\u4e00-\u9fff]/.test(gbkDecoded)) {
      return gbkDecoded
    }
  } catch (e) {
    // ignore
  }
  return value
}

function normalizeNameList(list) {
  return (list || []).map(item => ({
    ...item,
    name: fixMojibake(item.name)
  }))
}

async function loadRefData() {
  try {
    const [cityRes, catRes] = await Promise.all([
      getHotCities(), getCategoryTypes({ type: 1 })
    ])
    if (cityRes.code == 0) cities.value = normalizeNameList(cityRes.data)
    if (catRes.code == 0) parentCategories.value = normalizeNameList(catRes.data)
  } catch (e) { /* fallback to empty */ }
}

async function onParentChange() {
  subCategories.value = []
  form.programCategoryId = null
  if (!form.parentProgramCategoryId) return
  try {
    const res = await getCategoryByParent({ parentProgramCategoryId: form.parentProgramCategoryId })
    if (res.code == 0) {
      subCategories.value = normalizeNameList(res.data)
      if (subCategories.value.length === 0) {
        form.programCategoryId = form.parentProgramCategoryId
      }
    }
  } catch (e) { /* ignore */ }
}

onMounted(async () => {
  await loadRefData()
  const id = route.params.id
  if (id) {
    isEdit.value = true
    try {
      const res = await getProgramDetail({ id })
      if (res.code == 0 && res.data) {
        Object.assign(form, res.data)
        form.showTime = toDateTimeLocal(res.data.showTime)
        form.showDayTime = res.data.showDayTime || form.showDayTime
        form.showWeekTime = res.data.showWeekTime || form.showWeekTime
        if (form.parentProgramCategoryId) await onParentChange()
      } else {
        toast.error(res.message || '加载节目信息失败')
      }
    } catch (e) { toast.error(e.message || '加载节目信息失败') }
  }
})

watch(() => form.showTime, (value) => updateShowFields(value))

async function handleSubmit() {
  if (!form.title || !form.place || !form.areaId || !form.parentProgramCategoryId || !form.programCategoryId) {
    toast.error('请填写必填字段（标题、场馆、城市、分类）')
    return
  }
  if (!isEdit.value && !form.showTime) {
    toast.error('请填写演出时间')
    return
  }
  saving.value = true
  try {
    const res = await addProgram({ ...form })
    if (res.code == 0) {
      if (!isEdit.value) {
        const showTimeRes = await addShowTime({
          programId: res.data,
          showTime: toServerDateTime(form.showTime),
          showDayTime: form.showDayTime,
          showWeekTime: form.showWeekTime
        })
        if (showTimeRes.code != 0) {
          toast.error(showTimeRes.message || '演出时间保存失败')
          return
        }
      }
      toast.success(isEdit.value ? '修改成功' : '节目创建成功')
      router.push('/admin/programs')
    } else {
      toast.error(res.message || '操作失败')
    }
  } catch (e) { toast.error(e.message || '网络错误') }
  finally { saving.value = false }
}
</script>

<style scoped lang="scss">
.program-edit { color: var(--color-text); max-width: 900px; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 24px; font-family: var(--font-serif-cn); letter-spacing: 2px; margin: 0 0 12px; }
.page-title__line { width: 60px; height: 2px; background: var(--gradient-primary); border-radius: 1px; }
.edit-form { padding: 32px; display: flex; flex-direction: column; gap: 32px; }

.form-section__title { font-size: 16px; font-weight: 600; margin-bottom: 16px; padding-bottom: 10px; border-bottom: 1px solid var(--color-border); font-family: var(--font-serif-cn); letter-spacing: 1px; }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.form-group--full { grid-column: 1 / -1; }
.form-group { display: flex; flex-direction: column; gap: 6px; }
.form-label { font-size: 13px; color: var(--color-muted); }
.required { color: var(--color-error); }

.form-input, .form-textarea, .form-select {
  padding: 10px 14px; background: var(--color-elevated); border: 1px solid var(--color-border);
  border-radius: var(--radius); color: var(--color-text); font-size: 14px; font-family: var(--font-body);
  &:focus { border-color: var(--color-primary); box-shadow: 0 0 0 3px rgba(212,168,83,0.1); outline: none; }
}
.form-textarea { resize: vertical; }
.form-select { cursor: pointer; }
.switch-row { display: flex; gap: 24px; flex-wrap: wrap; }
.switch-label { display: flex; align-items: center; gap: 8px; font-size: 14px; color: var(--color-text); }
.form-actions { display: flex; gap: 12px; padding-top: 8px; }
.card { background: var(--color-card-bg); backdrop-filter: blur(12px); border: 1px solid var(--color-border); border-radius: var(--radius-lg); box-shadow: var(--shadow-card); }
@media (max-width: 640px) { .form-grid { grid-template-columns: 1fr; } }
</style>
