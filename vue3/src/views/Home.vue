<template>
  <component :is="layoutComponent">
    <div class="home">
      <section class="hero">
        <div class="hero__bg"></div>
        <div class="hero__spotlight"></div>
        <div class="hero__particles"></div>
        <div class="hero__content">
          <p class="hero__eyebrow child-stagger-1">华彩流光</p>
          <h1 class="hero__title font-display child-stagger-2">发现精彩演出</h1>
          <p class="hero__subtitle child-stagger-3">在光影交织中，探索属于你的璀璨时刻</p>
          <div class="hero__search child-stagger-4">
            <div class="hero__search-icon" aria-hidden="true">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="8" /><path d="m21 21-4.35-4.35" />
              </svg>
            </div>
            <input
              v-model="searchKeyword"
              type="search"
              class="hero__search-input"
              placeholder="搜索演出、场馆、艺人"
              @keyup.enter="handleSearch"
            />
            <button class="hero__search-btn" @click="handleSearch">搜索</button>
          </div>
        </div>
      </section>

      <section class="categories container" aria-label="演出分类">
        <div class="categories__list">
          <button
            v-for="cat in categoryChips"
            :key="cat.name"
            class="chip chip--lg"
            :class="{ 'chip--active': activeCategoryId === cat.id }"
            @click="navigateToCategory(cat)"
          >
            {{ cat.name }}
          </button>
        </div>
      </section>

      <div v-if="loading" class="loading-container">
        <div class="spinner"></div>
        <p class="text-muted mt-3">正在加载精彩演出...</p>
      </div>

      <div v-else class="sections container">
        <section
          v-for="section in programSections"
          :key="section.categoryId"
          class="program-section"
        >
          <div class="program-section__header">
            <div class="program-section__title-group">
              <span class="program-section__accent"></span>
              <h2 class="program-section__title font-display">{{ section.categoryName }}</h2>
            </div>
            <router-link
              :to="{ path: '/allType', query: { id: section.categoryId, name: section.categoryName } }"
              class="program-section__more"
            >
              查看更多
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <path d="M5 12h14M12 5l7 7-7 7" />
              </svg>
            </router-link>
          </div>
          <div class="program-section__grid">
            <ProgramCard
              v-for="program in section.programs"
              :key="program.id"
              :program="program"
            />
          </div>
        </section>

        <div v-if="programSections.length === 0" class="empty-state">
          <p class="text-muted">暂无演出信息，请稍后再来看看</p>
        </div>
      </div>
    </div>
  </component>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { getCategoryTypes, getHomeList } from '@/api/program'
import ProgramCard from '@/components/ProgramCard.vue'
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import AccountLayout from '@/layouts/AccountLayout.vue'
import eventBus from '@/utils/eventBus'
import { FALLBACK_CATEGORIES } from '@/constants/site'

const router = useRouter()
const route = useRoute()
const appStore = useAppStore()

const layoutComponent = computed(() => {
  return route.meta.layout === 'account' ? AccountLayout : DefaultLayout
})

const searchKeyword = ref('')
const loading = ref(true)
const activeCategoryId = ref(null)
const programSections = ref([])
const categoryChips = ref([...FALLBACK_CATEGORIES])

const handleSearch = () => {
  const keyword = searchKeyword.value.trim()
  if (!keyword) return
  router.push({ path: '/allType', query: { keyword } })
}

const navigateToCategory = (cat) => {
  router.push({ path: '/allType', query: { id: cat.id, name: cat.name } })
}

const loadData = async () => {
  loading.value = true
  try {
    const catRes = await getCategoryTypes({ type: 1 })
    if (catRes.code == 0) {
      const categories = catRes.data || []
      if (categories.length > 0) {
        categoryChips.value = categories.slice(0, 10).map(c => ({
          name: c.name,
          id: c.id
        }))
      }
    }

    const cityId = appStore.currentCity?.id
    const categoryIds = categoryChips.value.map(c => c.id).filter(Boolean)
    const params = {}
    if (categoryIds.length > 0) params.parentProgramCategoryIds = categoryIds
    if (cityId) params.areaId = cityId
    const homeRes = await getHomeList(params)

    if (homeRes.code == 0) {
      const data = homeRes.data || []
      programSections.value = data.map(item => ({
        categoryId: item.categoryId || item.id,
        categoryName: item.categoryName || item.name,
        programs: item.programList || item.programs || []
      })).filter(s => s.programs.length > 0)
    }
  } catch (e) {
    console.error('Failed to load home data:', e)
    programSections.value = []
  } finally {
    loading.value = false
  }
}

const onCityChange = () => {
  loadData()
}

onMounted(() => {
  loadData()
  eventBus.on('cityChange', onCityChange)
})

onUnmounted(() => {
  eventBus.off('cityChange', onCityChange)
})
</script>

<style scoped lang="scss">
.home {
  min-height: 100vh;
  background: var(--color-bg);
}

.hero {
  position: relative;
  padding: 100px 20px 80px;
  text-align: center;
  overflow: hidden;
  min-height: 420px;
  display: flex;
  align-items: center;
  justify-content: center;

  &__bg {
    position: absolute;
    inset: 0;
    background:
      radial-gradient(ellipse 80% 60% at 50% 40%, rgba(212, 168, 83, 0.12) 0%, transparent 70%),
      radial-gradient(ellipse 50% 80% at 20% 80%, rgba(198, 40, 40, 0.06) 0%, transparent 60%),
      linear-gradient(180deg, #0d0d0f 0%, #141418 100%);
  }

  &__spotlight {
    position: absolute;
    top: -30%;
    left: 50%;
    transform: translateX(-50%);
    width: 600px;
    height: 600px;
    background: radial-gradient(circle, rgba(212, 168, 83, 0.08) 0%, transparent 65%);
    pointer-events: none;
    animation: glowPulse 6s ease-in-out infinite;
  }

  &__particles {
    position: absolute;
    inset: 0;
    background-image:
      radial-gradient(1px 1px at 15% 25%, rgba(212, 168, 83, 0.3) 0%, transparent 100%),
      radial-gradient(1px 1px at 75% 15%, rgba(255, 215, 0, 0.2) 0%, transparent 100%),
      radial-gradient(1px 1px at 45% 70%, rgba(212, 168, 83, 0.25) 0%, transparent 100%),
      radial-gradient(1px 1px at 85% 60%, rgba(240, 237, 230, 0.15) 0%, transparent 100%),
      radial-gradient(1px 1px at 30% 85%, rgba(212, 168, 83, 0.2) 0%, transparent 100%);
    pointer-events: none;
  }

  &__content {
    position: relative;
    z-index: 1;
    max-width: 640px;
    margin: 0 auto;
  }

  &__eyebrow {
    font-family: var(--font-serif-cn);
    font-size: 13px;
    letter-spacing: 6px;
    color: var(--color-primary);
    margin-bottom: 16px;
    animation: fadeInUp 0.6s ease both;
  }

  &__title {
    font-size: 52px;
    font-weight: 700;
    color: var(--color-text);
    margin-bottom: 16px;
    letter-spacing: 4px;
    line-height: 1.2;
    animation: fadeInUp 0.6s ease 0.1s both;
  }

  &__subtitle {
    font-family: var(--font-serif-cn);
    font-size: 17px;
    color: var(--color-muted);
    margin-bottom: 40px;
    letter-spacing: 1px;
    animation: fadeInUp 0.6s ease 0.2s both;
  }

  &__search {
    display: flex;
    align-items: center;
    max-width: 520px;
    margin: 0 auto;
    background: var(--color-surface);
    border: 1px solid var(--color-border);
    border-radius: 50px;
    overflow: hidden;
    transition: border-color var(--transition), box-shadow var(--transition);
    animation: fadeInUp 0.6s ease 0.3s both;

    &:focus-within {
      border-color: var(--color-primary);
      box-shadow: var(--shadow-gold-glow);
    }
  }

  &__search-icon {
    padding-left: 20px;
    color: var(--color-muted);
    display: flex;
    flex-shrink: 0;
  }

  &__search-input {
    flex: 1;
    min-width: 0;
    border: none;
    padding: 15px 16px;
    font-size: 15px;
    font-family: var(--font-body);
    color: var(--color-text);
    background: transparent;

    &::placeholder {
      color: var(--color-muted);
    }
  }

  &__search-btn {
    padding: 10px 28px;
    margin: 5px 5px 5px 0;
    background: var(--gradient-primary);
    color: var(--color-bg);
    border-radius: 50px;
    font-size: 14px;
    font-weight: 600;
    transition: transform var(--transition), box-shadow var(--transition);

    &:hover {
      transform: translateY(-1px);
      box-shadow: var(--shadow-gold-glow);
    }
  }
}

.categories {
  padding: 36px 20px 20px;

  &__list {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    justify-content: center;
  }
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80px 20px;
}

.sections {
  padding: 32px 20px 80px;
}

.program-section {
  margin-bottom: 56px;
  animation: fadeInUp 0.5s ease both;

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 16px;
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid var(--color-border);
  }

  &__title-group {
    display: flex;
    align-items: center;
    gap: 12px;
    min-width: 0;
  }

  &__accent {
    width: 3px;
    height: 22px;
    background: var(--gradient-primary);
    border-radius: 2px;
    flex-shrink: 0;
  }

  &__title {
    font-size: 22px;
    color: var(--color-text);
    letter-spacing: 1px;
  }

  &__more {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    flex-shrink: 0;
    font-size: 13px;
    color: var(--color-primary);
    font-weight: 500;
    transition: all var(--transition);

    svg {
      transition: transform var(--transition);
    }

    &:hover svg {
      transform: translateX(3px);
    }
  }

  &__grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 20px;
  }
}

.empty-state {
  text-align: center;
  padding: 100px 20px;
  font-size: 16px;
}

@media (max-width: 640px) {
  .hero {
    padding: 70px 16px 50px;

    &__title {
      font-size: 34px;
      letter-spacing: 2px;
    }

    &__subtitle {
      font-size: 15px;
    }

    &__search-btn {
      padding: 10px 18px;
      font-size: 13px;
    }
  }

  .program-section__grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 12px;
  }
}
</style>