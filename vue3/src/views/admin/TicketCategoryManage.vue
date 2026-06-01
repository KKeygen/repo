<template>
  <component :is="layoutComponent">
    <div class="ticket-category-manage">
    <div class="page-header">
      <h2 class="page-title">票档管理</h2>
      <div class="page-title__line"></div>
    </div>

    <div class="filter-bar card">
      <div class="form-group">
        <label class="form-label">选择节目</label>
          <select v-model="programId" class="form-select">
          <option :value="null" disabled>搜索或选择节目</option>
          <option v-for="p in programs" :key="p.id" :value="p.id">{{ p.title }}</option>
        </select>
        <input v-model="programSearch" class="form-input" placeholder="输入节目名搜索..." @input="searchPrograms" style="margin-top:6px;" />
      </div>
    </div>

    <div v-if="programId" class="add-form card">
      <h3 class="add-form__title">添加票档</h3>
      <div class="form-row">
        <div class="form-group">
          <label class="form-label">票档名称 <span class="required">*</span></label>
          <input v-model="form.introduce" class="form-input" placeholder="如：VIP票/普通票" />
        </div>
        <div class="form-group">
          <label class="form-label">价格 <span class="required">*</span></label>
          <input v-model.number="form.price" type="number" class="form-input" placeholder="如：580" step="0.01" />
        </div>
        <div class="form-group">
          <label class="form-label">总数量 <span class="required">*</span></label>
          <input v-model.number="form.totalNumber" type="number" class="form-input" placeholder="如：500" />
        </div>
        <div class="form-group">
          <label class="form-label">剩余数量 <span class="required">*</span></label>
          <input v-model.number="form.remainNumber" type="number" class="form-input" placeholder="如：500" />
        </div>
        <div class="form-group" style="align-self:flex-end;">
          <button class="btn btn-primary btn-sm" @click="handleAdd" :disabled="adding">添加</button>
        </div>
      </div>
    </div>

    <div v-if="loading" class="loading-state"><div class="spinner"></div></div>

    <div v-else-if="ticketCategories.length > 0" class="table-wrap card">
      <table class="table">
        <thead>
          <tr>
            <th>票档名称</th>
            <th>价格</th>
            <th>总数量</th>
            <th>剩余数量</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(t, i) in ticketCategories" :key="i">
            <td>{{ t.introduce }}</td>
            <td>¥{{ t.price }}</td>
            <td>{{ t.totalNumber }}</td>
            <td>{{ t.remainNumber }}</td>
          </tr>
        </tbody>
      </table>
    </div>
    <div v-else-if="programId && !loading" class="empty card">该节目暂无票档</div>
    </div>
  </component>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import AdminLayout from '@/layouts/AdminLayout.vue'
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import { searchPrograms as searchApi, getProgramPage } from '@/api/program'
import { getTicketCategoriesByProgram, addTicketCategory } from '@/api/admin'
import { useToast } from '@/components/Toast.vue'

const route = useRoute()
const toast = useToast()
const layoutComponent = computed(() => route.meta.layout === 'admin' ? AdminLayout : DefaultLayout)

const programs = ref([])
const programId = ref(null)
const programSearch = ref('')
const ticketCategories = ref([])
const loading = ref(false)
const adding = ref(false)

const form = ref({ introduce: '', price: null, totalNumber: null, remainNumber: null })

let searchTimer = null

watch(programId, (val) => {
  if (val) onProgramChange()
})

function stripHtml(str) {
  if (!str) return ''
  return str.replace(/<[^>]+>/g, '')
}

async function searchPrograms() {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(async () => {
    const kw = programSearch.value.trim()
    try {
      if (kw) {
        const res = await searchApi({ content: kw, pageNumber: 1, pageSize: 50, timeType: 0 })
        if (res.code == 0) {
          const list = res.data?.records || res.data?.list || []
          programs.value = list.map(p => ({ ...p, title: stripHtml(p.title) }))
        }
      } else {
        await loadAllPrograms()
      }
    } catch (e) { /* ignore */ }
  }, 300)
}

async function loadAllPrograms() {
  try {
    const res = await getProgramPage({ pageNumber: 1, pageSize: 50, timeType: 0, type: 1 })
    if (res.code == 0) {
      const list = res.data?.records || res.data?.list || []
      programs.value = list.map(p => ({ ...p, title: stripHtml(p.title) }))
    }
  } catch (e) { /* ignore */ }
}

onMounted(() => loadAllPrograms())

async function onProgramChange() {
  ticketCategories.value = []
  if (!programId.value) return
  loading.value = true
  try {
    const res = await getTicketCategoriesByProgram({ programId: programId.value })
    if (res.code == 0) ticketCategories.value = res.data || []
  } catch (e) { toast.error('加载票档失败') }
  finally { loading.value = false }
}

async function handleAdd() {
  if (!form.value.introduce || !form.value.price || !form.value.totalNumber || !form.value.remainNumber) {
    toast.error('请填写完整信息')
    return
  }
  adding.value = true
  try {
    const res = await addTicketCategory({
      programId: programId.value,
      introduce: form.value.introduce,
      price: form.value.price,
      totalNumber: form.value.totalNumber,
      remainNumber: form.value.remainNumber
    })
    if (res.code == 0) {
      toast.success('添加成功')
      form.value = { introduce: '', price: null, totalNumber: null, remainNumber: null }
      onProgramChange()
    } else toast.error(res.message || '添加失败')
  } catch (e) { toast.error('网络错误') }
  finally { adding.value = false }
}
</script>

<style scoped lang="scss">
.ticket-category-manage { color: var(--color-text); max-width: 800px; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 24px; font-family: var(--font-serif-cn); letter-spacing: 2px; margin: 0 0 12px; }
.page-title__line { width: 60px; height: 2px; background: var(--gradient-primary); border-radius: 1px; }

.filter-bar { padding: 20px; margin-bottom: 16px; }
.form-group { display: flex; flex-direction: column; gap: 6px; min-width: 160px; }
.form-label { font-size: 13px; color: var(--color-muted); }
.required { color: var(--color-error); }
.form-input, .form-select { padding: 8px 12px; background: var(--color-elevated); border: 1px solid var(--color-border); border-radius: var(--radius); color: var(--color-text); font-size: 14px; }
.form-input:focus, .form-select:focus { border-color: var(--color-primary); box-shadow: 0 0 0 3px rgba(212,168,83,0.1); outline: none; }
.form-select { cursor: pointer; }

.add-form { padding: 20px; margin-bottom: 20px; }
.add-form__title { font-size: 15px; font-weight: 600; margin-bottom: 14px; font-family: var(--font-serif-cn); letter-spacing: 1px; }
.form-row { display: flex; gap: 14px; align-items: flex-end; flex-wrap: wrap; }

.table-wrap { padding: 0; overflow-x: auto; }
.table { width: 100%; border-collapse: collapse; font-size: 14px; }
.table th { text-align: left; padding: 14px 16px; color: var(--color-muted); font-weight: 600; border-bottom: 1px solid var(--color-border); }
.table td { padding: 12px 16px; border-bottom: 1px solid rgba(212,168,83,0.08); }
.table tr:hover td { background: rgba(212,168,83,0.03); }

.empty { padding: 40px; text-align: center; color: var(--color-muted); }
.card { background: var(--color-card-bg); backdrop-filter: blur(12px); border: 1px solid var(--color-border); border-radius: var(--radius-lg); box-shadow: var(--shadow-card); }
.loading-state { display: flex; justify-content: center; padding: 60px; }
</style>
