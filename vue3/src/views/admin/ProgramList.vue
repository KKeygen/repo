<template>
  <component :is="layoutComponent">
    <div class="program-list">
    <div class="page-header">
      <h2 class="page-title">节目管理</h2>
      <div class="page-title__line"></div>
      <div class="page-header__actions">
        <router-link to="/admin/programs/add" class="btn btn-primary">+ 新增节目</router-link>
      </div>
    </div>

    <div class="filter-bar card">
      <input v-model="keyword" class="form-input" placeholder="搜索节目名称..." @keyup.enter="loadData" />
      <button class="btn btn-outline btn-sm" @click="loadData">搜索</button>
    </div>

    <div v-if="loading" class="loading-state"><div class="spinner"></div></div>

    <div v-else class="program-table-wrap card">
      <table class="program-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>标题</th>
            <th>场馆</th>
            <th>城市</th>
            <th>分类</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in programs" :key="p.id">
            <td class="td-id">{{ p.id }}</td>
            <td class="td-title">{{ p.title }}</td>
            <td class="td-place">{{ p.place || '—' }}</td>
            <td class="td-meta">{{ p.areaName || '—' }}</td>
            <td class="td-meta">{{ p.parentProgramCategoryName || '—' }} / {{ p.programCategoryName || '—' }}</td>
            <td class="td-actions">
              <button class="btn btn-sm btn-outline" @click="goEdit(p.id)">编辑</button>
              <button class="btn btn-sm btn-ghost btn-danger-text" @click="handleInvalid(p)">下架</button>
            </td>
          </tr>
          <tr v-if="programs.length === 0">
            <td colspan="6" class="td-empty">暂无节目</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="totalPages > 1" class="pagination">
      <button :disabled="page <= 1" @click="goPage(page - 1)" class="btn btn-sm btn-outline">上一页</button>
      <span class="pagination__info">{{ page }} / {{ totalPages }}</span>
      <button :disabled="page >= totalPages" @click="goPage(page + 1)" class="btn btn-sm btn-outline">下一页</button>
    </div>
    </div>
  </component>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AdminLayout from '@/layouts/AdminLayout.vue'
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import { getProgramPage } from '@/api/program'
import { invalidProgram } from '@/api/admin'
import { useToast } from '@/components/Toast.vue'

const route = useRoute()
const router = useRouter()
const toast = useToast()

const layoutComponent = computed(() => route.meta.layout === 'admin' ? AdminLayout : DefaultLayout)

const loading = ref(true)
const keyword = ref('')
const programs = ref([])
const page = ref(1)
const pageSize = 12
const totalPages = ref(0)

async function loadData() {
  loading.value = true
  try {
    const params = { pageNumber: page.value, pageSize, timeType: 0, type: 1 }
    if (keyword.value.trim()) params.content = keyword.value.trim()
    const res = await getProgramPage(params)
    if (res.code == 0) {
      const data = res.data || {}
      programs.value = data.records || data.list || []
      totalPages.value = Math.ceil((data.totalSize || data.total || 0) / pageSize)
    }
  } catch (e) { toast.error('加载失败') }
  finally { loading.value = false }
}

function goPage(p) { page.value = p; loadData() }
function goEdit(id) { router.push(`/admin/programs/${id}/edit`) }

async function handleInvalid(p) {
  if (!confirm(`确定下架节目 "${p.title}" 吗？`)) return
  try {
    const res = await invalidProgram({ id: p.id })
    if (res.code == 0) { toast.success('已下架'); loadData() }
    else toast.error(res.message || '下架失败')
  } catch (e) { toast.error('网络错误') }
}

onMounted(() => loadData())
</script>

<style scoped lang="scss">
.program-list { color: var(--color-text); }

.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px; flex-wrap: wrap; gap: 12px; }
.page-title { font-size: 24px; font-family: var(--font-serif-cn); letter-spacing: 2px; margin: 0; }
.page-title__line { width: 60px; height: 2px; background: var(--gradient-primary); border-radius: 1px; }

.filter-bar { display: flex; gap: 10px; padding: 16px; margin-bottom: 16px; align-items: center; }
.filter-bar .form-input { flex: 1; background: var(--color-elevated); border: 1px solid var(--color-border); color: var(--color-text); }
.filter-bar .form-input:focus { border-color: var(--color-primary); box-shadow: 0 0 0 3px rgba(212,168,83,0.1); outline: none; }

.program-table-wrap { padding: 0; overflow-x: auto; }
.program-table { width: 100%; border-collapse: collapse; font-size: 14px; }
.program-table th { text-align: left; padding: 14px 16px; color: var(--color-muted); font-weight: 600; border-bottom: 1px solid var(--color-border); }
.program-table td { padding: 12px 16px; border-bottom: 1px solid rgba(212,168,83,0.08); }
.program-table tr:hover td { background: rgba(212,168,83,0.03); }
.td-id { color: var(--color-muted); width: 80px; }
.td-title { font-weight: 500; }
.td-place { color: var(--color-muted); }
.td-meta { color: var(--color-muted); font-size: 12px; }
.td-actions { display: flex; gap: 8px; }
.td-empty { text-align: center; color: var(--color-muted); padding: 40px; }

.btn-danger-text { color: var(--color-error); &:hover { background: rgba(239,68,68,0.08); } }

.pagination { display: flex; align-items: center; justify-content: center; gap: 12px; margin-top: 20px; }
.pagination__info { color: var(--color-muted); font-size: 13px; }

.card { background: var(--color-card-bg); backdrop-filter: blur(12px); border: 1px solid var(--color-border); border-radius: var(--radius-lg); box-shadow: var(--shadow-card); }
.loading-state { display: flex; justify-content: center; padding: 60px; }
</style>
