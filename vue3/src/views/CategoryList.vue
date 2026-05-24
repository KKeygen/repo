<template>
  <component :is="layoutComponent">
    <div class="category-page">
      <div class="container">
        <!-- Page Header -->
        <div class="page-header child-stagger-1">
          <h1 class="page-header__title font-display">全部分类</h1>
          <div class="page-header__line"></div>
        </div>

        <!-- Main Category Tabs -->
        <div class="category-tabs child-stagger-2">
          <button
            v-for="cat in categories"
            :key="cat.id"
            class="category-tabs__item"
            :class="{ 'category-tabs__item--active': activeCategoryId === cat.id }"
            @click="selectCategory(cat)"
          >
            {{ cat.name }}
          </button>
        </div>

        <!-- Sub Categories -->
        <div v-if="subCategories.length > 0" class="sub-categories child-stagger-3">
          <button
            class="sub-categories__item"
            :class="{ 'sub-categories__item--active': !activeSubCategoryId }"
            @click="selectSubCategory(null)"
          >
            全部
          </button>
          <button
            v-for="sub in subCategories"
            :key="sub.id"
            class="sub-categories__item"
            :class="{ 'sub-categories__item--active': activeSubCategoryId === sub.id }"
            @click="selectSubCategory(sub)"
          >
            {{ sub.name }}
          </button>
        </div>

        <!-- Filters -->
        <div class="filters child-stagger-4">
          <div class="filters__city">
            <span class="text-muted">当前城市：</span>
            <span class="filters__city-name">{{ appStore.currentCity?.name || '全国' }}</span>
          </div>

          <!-- Time Filter -->
          <div class="filters__time">
            <button
              v-for="t in timeTabs"
              :key="t.value"
              class="filters__time-btn"
              :class="{ 'filters__time-btn--active': timeType === t.value }"
              @click="selectTime(t.value)"
            >
              {{ t.label }}
            </button>
          </div>

          <!-- Date Range Picker -->
          <div v-if="timeType === 5" class="filters__daterange">
            <input type="date" v-model="dateStart" class="form-input form-input--sm" />
            <span class="text-muted">至</span>
            <input type="date" v-model="dateEnd" class="form-input form-input--sm" />
            <button class="btn btn--sm btn--outline" @click="applyDateRange">确定</button>
          </div>
        </div>

        <!-- Sort Tabs -->
        <div class="sort-tabs child-stagger-5">
          <button
            v-for="s in sortTabs"
            :key="s.value"
            class="sort-tabs__item"
            :class="{ 'sort-tabs__item--active': sortType === s.value }"
            @click="selectSort(s.value)"
          >
            {{ s.label }}
          </button>
        </div>

        <!-- Loading -->
        <div v-if="loading" class="loading-state">
          <div class="spinner"></div>
          <p class="text-muted mt-2">加载中...</p>
        </div>

        <!-- Program Grid -->
        <div v-else class="child-stagger-5">
          <div v-if="programs.length > 0" class="program-grid">
            <ProgramCard
              v-for="program in programs"
              :key="program.id"
              :program="program"
            />
          </div>
          <div v-else class="empty-state">
            <div class="empty-state__icon">🎭</div>
            <p class="text-muted">暂无相关演出</p>
          </div>

          <!-- Pagination -->
          <div v-if="totalPages > 1" class="pagination child-stagger-6">
            <button
              class="pagination__btn"
              :disabled="currentPage <= 1"
              @click="goToPage(currentPage - 1)"
            >
              上一页
            </button>
            <button
              v-for="page in visiblePages"
              :key="page"
              class="pagination__num"
              :class="{ 'pagination__num--active': page === currentPage }"
              @click="goToPage(page)"
            >
              {{ page }}
            </button>
            <button
              class="pagination__btn"
              :disabled="currentPage >= totalPages"
              @click="goToPage(currentPage + 1)"
            >
              下一页
            </button>
          </div>
        </div>
      </div>
    </div>
  </component>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { getCategoryTypes, getCategoryByParent, getProgramPage } from '@/api/program'
import ProgramCard from '@/components/ProgramCard.vue'
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import AccountLayout from '@/layouts/AccountLayout.vue'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()

const layoutComponent = computed(() => {
  return route.meta.layout === 'account' ? AccountLayout : DefaultLayout
})

const categories = ref([])
const subCategories = ref([])
const programs = ref([])
const loading = ref(false)
const activeCategoryId = ref(null)
const activeSubCategoryId = ref(null)
const currentPage = ref(1)
const totalPages = ref(0)
const pageSize = 12

const timeType = ref(0)
const sortType = ref(1)
const dateStart = ref('')
const dateEnd = ref('')

const timeTabs = [
  { label: '全部', value: 0 },
  { label: '今天', value: 1 },
  { label: '明天', value: 2 },
  { label: '一周内', value: 3 },
  { label: '一个月内', value: 4 },
  { label: '按日历', value: 5 }
]

const sortTabs = [
  { label: '相关度排序', value: 1 },
  { label: '推荐排序', value: 2 },
  { label: '最近开场', value: 3 },
  { label: '最新上架', value: 4 }
]

const selectTime = (value) => {
  timeType.value = value
  if (value !== 5) {
    dateStart.value = ''
    dateEnd.value = ''
  }
  currentPage.value = 1
  loadPrograms()
}

const selectSort = (value) => {
  sortType.value = value
  currentPage.value = 1
  loadPrograms()
}

const applyDateRange = () => {
  if (dateStart.value && dateEnd.value) {
    currentPage.value = 1
    loadPrograms()
  }
}

const visiblePages = computed(() => {
  const pages = []
  const total = totalPages.value
  const current = currentPage.value
  let start = Math.max(1, current - 2)
  let end = Math.min(total, current + 2)

  if (end - start < 4) {
    if (start === 1) end = Math.min(total, start + 4)
    else start = Math.max(1, end - 4)
  }

  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  return pages
})

const selectCategory = async (cat) => {
  activeCategoryId.value = cat.id
  activeSubCategoryId.value = null
  currentPage.value = 1

  router.replace({ query: { ...route.query, id: cat.id, name: cat.name, type: cat.type || '' } })
  await loadSubCategories(cat.id)
  await loadPrograms()
}

const selectSubCategory = async (sub) => {
  activeSubCategoryId.value = sub?.id || null
  currentPage.value = 1
  await loadPrograms()
}

const goToPage = (page) => {
  if (page < 1 || page > totalPages.value) return
  currentPage.value = page
  loadPrograms()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const loadCategories = async () => {
  try {
    const res = await getCategoryTypes({ type: 1 })
    if (res.code == 0) {
      categories.value = res.data || []
    }
  } catch (e) {
    console.error('Load categories failed:', e)
  }
}

const loadSubCategories = async (parentId) => {
  if (!parentId) {
    subCategories.value = []
    return
  }
  try {
    const res = await getCategoryByParent({ parentProgramCategoryId: parentId })
    if (res.code == 0) {
      subCategories.value = res.data || []
    }
  } catch (e) {
    subCategories.value = []
  }
}

const loadPrograms = async () => {
  loading.value = true
  try {
    const cityId = appStore.currentCity?.id
    const params = {
      pageNumber: currentPage.value,
      pageSize,
      timeType: timeType.value,
      type: sortType.value
    }
    if (cityId) params.areaId = Number(cityId)
    if (timeType.value === 5) {
      params.startDateTime = dateStart.value
      params.endDateTime = dateEnd.value
    }

    if (activeSubCategoryId.value) {
      params.programCategoryId = activeSubCategoryId.value
    } else if (activeCategoryId.value) {
      params.parentProgramCategoryId = activeCategoryId.value
    }

    const res = await getProgramPage(params)
    if (res.code == 0) {
      const pageData = res.data || {}
      programs.value = pageData.records || pageData.list || []
      const total = pageData.total || 0
      totalPages.value = Math.ceil(total / pageSize)
    }
  } catch (e) {
    programs.value = []
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadCategories()

  const queryId = route.query.id
  if (queryId) {
    activeCategoryId.value = Number(queryId)
    await loadSubCategories(queryId)
  } else if (categories.value.length > 0) {
    activeCategoryId.value = categories.value[0].id
    await loadSubCategories(categories.value[0].id)
  }

  await loadPrograms()
})

watch(() => route.query, (newQuery) => {
  if (newQuery.id && newQuery.id !== activeCategoryId.value) {
    activeCategoryId.value = Number(newQuery.id)
    activeSubCategoryId.value = null
    currentPage.value = 1
    loadSubCategories(newQuery.id)
    loadPrograms()
  }
})
</script>

<style scoped lang="scss">
.category-page {
  padding: 32px 0 80px;
  min-height: 100vh;
  background: var(--color-bg);
}

.page-header {
  padding: 24px 0 20px;
  animation: fadeInUp 0.5s ease both;

  &__title {
    font-family: var(--font-serif-cn);
    font-size: 32px;
    font-weight: 700;
    color: var(--color-text);
    letter-spacing: 2px;
    margin: 0 0 12px;
  }

  &__line {
    width: 60px;
    height: 3px;
    background: var(--gradient-primary);
    border-radius: 2px;
  }
}

.category-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding: 20px 0;
  border-bottom: 1px solid var(--color-border);
  animation: fadeInUp 0.5s ease both;

  &__item {
    padding: 9px 24px;
    border-radius: 50px;
    background: var(--color-surface);
    border: 1px solid var(--color-border);
    font-family: var(--font-body);
    font-size: 14px;
    font-weight: 500;
    color: var(--color-muted);
    cursor: pointer;
    transition: all var(--transition);
    letter-spacing: 0.5px;

    &:hover {
      border-color: var(--color-primary);
      color: var(--color-primary);
      background: var(--color-elevated);
    }

    &--active {
      background: transparent;
      color: var(--color-primary);
      border-color: var(--color-primary);
      font-weight: 600;
      box-shadow: 0 0 12px rgba(212, 168, 83, 0.2);
    }
  }
}

.sub-categories {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 18px 0;
  animation: fadeInUp 0.5s ease both;

  &__item {
    padding: 6px 18px;
    border-radius: var(--radius);
    background: var(--color-elevated);
    border: 1px solid transparent;
    font-size: 13px;
    color: var(--color-muted);
    cursor: pointer;
    transition: all var(--transition);

    &:hover {
      color: var(--color-primary);
      border-color: var(--color-border);
    }

    &--active {
      background: rgba(212, 168, 83, 0.1);
      color: var(--color-primary);
      font-weight: 600;
      border-color: var(--color-primary);
    }
  }
}

.filters {
  display: flex;
  align-items: center;
  padding: 14px 0;
  margin-bottom: 24px;
  animation: fadeInUp 0.5s ease both;

  &__city {
    font-size: 14px;
    font-family: var(--font-body);
  }

  &__city-name {
    font-weight: 600;
    color: var(--color-primary);
  }
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80px 0;
}

.program-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 24px;
}

.empty-state {
  text-align: center;
  padding: 100px 0;

  &__icon {
    font-size: 48px;
    margin-bottom: 16px;
    opacity: 0.5;
  }

  p {
    font-size: 16px;
    color: var(--color-muted);
  }
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 48px;
  padding: 24px 0;

  &__btn {
    padding: 9px 20px;
    border-radius: var(--radius);
    background: var(--color-surface);
    border: 1px solid var(--color-border);
    font-size: 14px;
    color: var(--color-muted);
    cursor: pointer;
    transition: all var(--transition);

    &:hover:not(:disabled) {
      border-color: var(--color-primary);
      color: var(--color-primary);
      background: var(--color-elevated);
    }

    &:disabled {
      opacity: 0.35;
      cursor: not-allowed;
    }
  }

  &__num {
    width: 38px;
    height: 38px;
    border-radius: var(--radius);
    background: var(--color-surface);
    border: 1px solid var(--color-border);
    font-size: 14px;
    color: var(--color-muted);
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all var(--transition);

    &:hover {
      border-color: var(--color-primary);
      color: var(--color-primary);
    }

    &--active {
      background: var(--gradient-primary);
      color: var(--color-bg);
      border-color: var(--color-primary);
      font-weight: 700;
      box-shadow: var(--shadow-gold-glow);
    }
  }
}

@media (max-width: 768px) {
  .page-header__title {
    font-size: 24px;
  }

  .program-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 16px;
  }
}
</style>
