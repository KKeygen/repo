<template>
  <component :is="layoutComponent">
    <div class="category-manage">
    <div class="page-header">
      <h2 class="page-title">分类管理</h2>
      <div class="page-title__line"></div>
    </div>

    <!-- Add Category Form -->
    <div class="add-form card">
      <h3 class="add-form__title">添加分类</h3>
      <div class="form-row">
        <div class="form-group">
          <label class="form-label">分类名称 <span class="required">*</span></label>
          <input v-model="newName" class="form-input" placeholder="如：演唱会" />
        </div>
        <div class="form-group">
          <label class="form-label">类型 <span class="required">*</span></label>
          <select v-model.number="newType" class="form-select" @change="newParentId = null">
            <option :value="1">大类（一级分类）</option>
            <option :value="2">子类（二级分类）</option>
          </select>
        </div>
        <div v-if="newType === 2" class="form-group">
          <label class="form-label">所属大类 <span class="required">*</span></label>
          <select v-model.number="newParentId" class="form-select">
            <option :value="null" disabled>选择父分类</option>
            <option v-for="c in parentCategories" :key="c.id" :value="c.id">{{ c.name }}</option>
          </select>
        </div>
        <div class="form-group" style="align-self:flex-end;">
          <button class="btn btn-primary btn-sm" @click="handleAdd" :disabled="adding">添加</button>
        </div>
      </div>
    </div>

    <!-- Category List -->
    <div v-if="loading" class="loading-state"><div class="spinner"></div></div>

    <div v-else class="category-list card">
      <div class="category-list__header">
        <h3>现有分类</h3>
        <span class="text-muted">{{ categories.length }} 个</span>
      </div>
      <div v-if="categories.length === 0" class="empty">暂无分类</div>
      <div v-for="cat in categories" :key="cat.id" class="category-item">
        <span class="category-item__name">{{ cat.name }}</span>
        <span class="category-item__meta">ID: {{ cat.id }} · {{ cat.type === 1 ? '大类' : '子类' }}</span>
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
import { getAllCategories, batchSaveCategories } from '@/api/admin'
import { useToast } from '@/components/Toast.vue'

const route = useRoute()
const toast = useToast()
const layoutComponent = computed(() => route.meta.layout === 'admin' ? AdminLayout : DefaultLayout)

const categories = ref([])
const loading = ref(true)
const adding = ref(false)

const newName = ref('')
const newType = ref(1)
const newParentId = ref(null)

const parentCategories = computed(() => categories.value.filter(c => c.type === 1))

async function loadCategories() {
  try {
    const res = await getAllCategories()
    if (res.code == 0) categories.value = res.data || []
  } catch (e) { toast.error('加载分类失败') }
  finally { loading.value = false }
}

async function handleAdd() {
  if (!newName.value.trim()) { toast.error('请输入分类名称'); return }
  if (newType.value === 2 && !newParentId.value) { toast.error('请选择所属大类'); return }
  adding.value = true
  const item = { name: newName.value.trim(), type: newType.value }
  if (newType.value === 2) item.parentId = newParentId.value
  try {
    const res = await batchSaveCategories([item])
    if (res.code == 0) { toast.success('添加成功'); newName.value = ''; newParentId.value = null; loadCategories() }
    else toast.error(res.message || '添加失败')
  } catch (e) { toast.error('网络错误') }
  finally { adding.value = false }
}

onMounted(() => loadCategories())
</script>

<style scoped lang="scss">
.category-manage { color: var(--color-text); max-width: 700px; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 24px; font-family: var(--font-serif-cn); letter-spacing: 2px; margin: 0 0 12px; }
.page-title__line { width: 60px; height: 2px; background: var(--gradient-primary); border-radius: 1px; }

.add-form { padding: 20px; margin-bottom: 20px; }
.add-form__title { font-size: 15px; font-weight: 600; margin-bottom: 14px; font-family: var(--font-serif-cn); letter-spacing: 1px; }
.form-row { display: flex; gap: 14px; align-items: flex-end; flex-wrap: wrap; }
.form-group { display: flex; flex-direction: column; gap: 6px; min-width: 150px; }
.form-label { font-size: 13px; color: var(--color-muted); }
.required { color: var(--color-error); }
.form-input, .form-select { padding: 8px 12px; background: var(--color-elevated); border: 1px solid var(--color-border); border-radius: var(--radius); color: var(--color-text); font-size: 14px; }
.form-input:focus, .form-select:focus { border-color: var(--color-primary); box-shadow: 0 0 0 3px rgba(212,168,83,0.1); outline: none; }
.form-select { cursor: pointer; }

.category-list { padding: 20px; }
.category-list__header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.category-list__header h3 { font-size: 15px; font-weight: 600; font-family: var(--font-serif-cn); letter-spacing: 1px; }
.empty { padding: 40px; text-align: center; color: var(--color-muted); }
.category-item { display: flex; align-items: center; justify-content: space-between; padding: 10px 14px; border: 1px solid var(--color-border); border-radius: var(--radius); margin-bottom: 8px; }
.category-item__name { font-weight: 500; }
.category-item__meta { font-size: 12px; color: var(--color-muted); }

.card { background: var(--color-card-bg); backdrop-filter: blur(12px); border: 1px solid var(--color-border); border-radius: var(--radius-lg); box-shadow: var(--shadow-card); }
.loading-state { display: flex; justify-content: center; padding: 60px; }
</style>
