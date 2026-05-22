<template>
  <component :is="layoutComponent">
    <div class="dashboard">
    <div class="page-header">
      <h2 class="page-title">管理后台</h2>
      <div class="page-title__line"></div>
      <p class="page-subtitle text-muted">演出管理 · 座位管理 · 分类管理</p>
    </div>

    <div class="stat-cards">
      <router-link to="/admin/programs" class="stat-card card">
        <span class="stat-card__icon">🎭</span>
        <div class="stat-card__info">
          <span class="stat-card__num">{{ stats.programCount }}</span>
          <span class="stat-card__label">节目总数</span>
        </div>
      </router-link>
      <router-link to="/admin/seats" class="stat-card card">
        <span class="stat-card__icon">💺</span>
        <div class="stat-card__info">
          <span class="stat-card__num">{{ stats.seatService }}</span>
          <span class="stat-card__label">座位服务</span>
        </div>
      </router-link>
      <router-link to="/admin/categories" class="stat-card card">
        <span class="stat-card__icon">📂</span>
        <div class="stat-card__info">
          <span class="stat-card__num">{{ stats.categoryService }}</span>
          <span class="stat-card__label">分类服务</span>
        </div>
      </router-link>
    </div>

    <div class="quick-actions card">
      <h3 class="quick-actions__title">快捷操作</h3>
      <div class="quick-actions__list">
        <router-link to="/admin/programs/add" class="quick-action-item">
          <span>➕</span> 新增节目
        </router-link>
        <router-link to="/admin/programs" class="quick-action-item">
          <span>📋</span> 管理节目
        </router-link>
        <router-link to="/admin/seats" class="quick-action-item">
          <span>💺</span> 管理座位
        </router-link>
        <router-link to="/admin/categories" class="quick-action-item">
          <span>📂</span> 管理分类
        </router-link>
      </div>
    </div>
    </div>
  </component>
</template>

<script setup>
import { computed, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import AdminLayout from '@/layouts/AdminLayout.vue'
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import { getProgramPage } from '@/api/program'

const route = useRoute()
const layoutComponent = computed(() => route.meta.layout === 'admin' ? AdminLayout : DefaultLayout)

const stats = reactive({
  programCount: '—',
  seatService: '正常',
  categoryService: '正常'
})

onMounted(async () => {
  try {
    const res = await getProgramPage({ pageNumber: 1, pageSize: 1, timeType: 0 })
    if (res.code == 0 && res.data) {
      stats.programCount = res.data.totalSize || res.data.total || 0
    }
  } catch (e) { /* keep '—' */ }
})
</script>

<style scoped lang="scss">
.dashboard { color: var(--color-text); }

.page-header { margin-bottom: 28px; }
.page-title { font-size: 24px; font-family: var(--font-serif-cn); letter-spacing: 2px; margin: 0 0 12px; }
.page-title__line { width: 60px; height: 2px; background: var(--gradient-primary); border-radius: 1px; }
.page-subtitle { font-size: 13px; margin-top: 8px; }

.stat-cards { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; margin-bottom: 24px; }

.stat-card {
  padding: 24px; display: flex; align-items: center; gap: 16px; text-decoration: none;
  transition: all var(--transition);

  &:hover { border-color: var(--color-primary); box-shadow: var(--shadow-gold-glow); }
  &__icon { font-size: 32px; }
  &__info { display: flex; flex-direction: column; gap: 4px; }
  &__num { font-size: 24px; font-weight: 700; color: var(--color-primary); }
  &__label { font-size: 13px; color: var(--color-muted); }
}

.quick-actions {
  padding: 24px;
  &__title { font-size: 16px; font-weight: 600; margin-bottom: 16px; font-family: var(--font-serif-cn); letter-spacing: 1px; }
  &__list { display: flex; gap: 12px; flex-wrap: wrap; }
}

.quick-action-item {
  display: flex; align-items: center; gap: 8px;
  padding: 12px 24px; border: 1px solid var(--color-border); border-radius: var(--radius);
  font-size: 14px; color: var(--color-muted); text-decoration: none; transition: all var(--transition);
  &:hover { border-color: var(--color-primary); color: var(--color-primary); background: rgba(212,168,83,0.06); }
}

.card {
  background: var(--color-card-bg); backdrop-filter: blur(12px);
  border: 1px solid var(--color-border); border-radius: var(--radius-lg); box-shadow: var(--shadow-card);
}

@media (max-width: 768px) { .stat-cards { grid-template-columns: 1fr; } }
</style>
