<template>
  <header class="app-header">
    <div class="app-header__inner container">
      <!-- Logo -->
      <router-link to="/index" class="app-header__logo">
        <span class="logo-text">万花筒</span>
      </router-link>

      <!-- City Selector -->
      <div class="city-selector" @click.stop="showCityPopover = !showCityPopover; showUserMenu = false">
        <span class="city-selector__name">{{ currentCity.name }}</span>
        <span class="city-selector__arrow">▾</span>

        <!-- City Popover -->
        <div v-if="showCityPopover" class="city-popover" @click.stop>
          <div class="city-popover__section">
            <h4>当前城市</h4>
            <span class="city-tag city-tag--active">{{ currentCity.name }}</span>
          </div>
          <div class="city-popover__section">
            <h4>热门城市</h4>
            <div class="city-tags">
              <span
                v-for="city in hotCities"
                :key="city.id"
                class="city-tag"
                @click="selectCity(city)"
              >{{ city.name }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Search Bar -->
      <div class="search-bar">
        <input
          v-model="searchText"
          class="search-bar__input"
          type="text"
          placeholder="搜索演出、场馆..."
          @keyup.enter="handleSearch"
        />
        <button class="search-bar__btn" @click="handleSearch">
          <span class="search-icon">⌕</span>
        </button>
      </div>

      <!-- Navigation -->
      <nav class="app-header__nav">
        <router-link to="/index" class="nav-link">首页</router-link>
        <router-link to="/allType" class="nav-link">分类</router-link>
      </nav>

      <!-- User Area -->
      <div class="app-header__user">
        <template v-if="userStore.isLoggedIn">
          <div class="user-dropdown" @click.stop="showUserMenu = !showUserMenu; showCityPopover = false">
            <div class="user-avatar">{{ avatarText }}</div>
            <span class="user-name">{{ userStore.userName || '用户' }}</span>

            <div v-if="showUserMenu" class="user-menu" @click.stop>
              <router-link to="/personInfo" class="user-menu__item" @click="showUserMenu = false">
                个人信息
              </router-link>
              <router-link to="/orderManagement" class="user-menu__item" @click="showUserMenu = false">
                我的订单
              </router-link>
              <router-link to="/accountSettings" class="user-menu__item" @click="showUserMenu = false">
                账号设置
              </router-link>
              <div class="user-menu__divider"></div>
              <div class="user-menu__item user-menu__item--danger" @click="handleLogout">
                退出登录
              </div>
            </div>
          </div>
        </template>
        <template v-else>
          <router-link to="/login" class="auth-link">登录</router-link>
          <router-link to="/register" class="auth-link auth-link--accent">注册</router-link>
        </template>
      </div>
    </div>
    <div class="app-header__accent-line"></div>
  </header>
  <!-- Spacer to prevent content from going under fixed header -->
  <div class="header-spacer"></div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'
import { getHotCities, getCurrentCity } from '@/api/area'
import eventBus from '@/utils/eventBus'

const router = useRouter()
const userStore = useUserStore()
const appStore = useAppStore()

const showCityPopover = ref(false)
const showUserMenu = ref(false)
const searchText = ref('')
const hotCities = ref([])

const currentCity = computed(() => appStore.currentCity)
const avatarText = computed(() => {
  const name = userStore.userName || '用'
  return name.charAt(0).toUpperCase()
})

function selectCity(city) {
  appStore.setCity(city)
  showCityPopover.value = false
  eventBus.emit('cityChange', city)
}

function handleSearch() {
  if (searchText.value.trim()) {
    router.push({ path: '/allType', query: { keyword: searchText.value.trim() } })
    searchText.value = ''
  }
}

async function handleLogout() {
  showUserMenu.value = false
  await userStore.logout()
  router.push('/login')
}

function handleClickOutside(e) {
  showCityPopover.value = false
  showUserMenu.value = false
}

onMounted(async () => {
  document.addEventListener('click', handleClickOutside)
  try {
    const res = await getCurrentCity()
    if (res.code == 0 && res.data) {
      appStore.setCity(res.data)
    }
  } catch (e) { /* keep default city */ }
  try {
    const res = await getHotCities({})
    hotCities.value = res.data || []
  } catch (e) {
    hotCities.value = [
      { id: '1', name: '北京' },
      { id: '2', name: '上海' },
      { id: '3', name: '广州' },
      { id: '4', name: '深圳' },
      { id: '5', name: '杭州' },
      { id: '6', name: '成都' }
    ]
  }
})

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped lang="scss">
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: rgba(13, 13, 15, 0.92);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
}

.app-header__accent-line {
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--color-primary) 20%, var(--color-gold) 50%, var(--color-primary) 80%, transparent);
  opacity: 0.5;
}

.header-spacer {
  height: 65px;
}

.app-header__inner {
  display: flex;
  align-items: center;
  height: 64px;
  gap: 24px;
}

.app-header__logo {
  text-decoration: none;
  flex-shrink: 0;
}

.logo-text {
  font-family: var(--font-display);
  font-size: 24px;
  font-weight: 700;
  background: var(--gradient-gold-text);
  background-size: 200% auto;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 0.08em;
}

.city-selector {
  position: relative;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: var(--radius);
  cursor: pointer;
  font-size: 13px;
  color: var(--color-muted);
  flex-shrink: 0;
  transition: all var(--transition);
  border: 1px solid transparent;

  &:hover {
    color: var(--color-text);
    border-color: var(--color-border);
    background: var(--color-surface);
  }

  &__name { font-weight: 500; color: var(--color-text); }
  &__arrow { font-size: 10px; color: var(--color-muted); transition: transform var(--transition); }
}

.city-popover {
  position: absolute;
  top: calc(100% + 8px);
  left: 0;
  background: var(--color-elevated);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-dropdown);
  padding: 16px;
  min-width: 300px;
  z-index: 100;
  animation: fadeInUp 0.2s ease;

  &__section {
    margin-bottom: 14px;
    &:last-child { margin-bottom: 0; }

    h4 {
      font-family: var(--font-serif-cn);
      font-size: 11px;
      text-transform: uppercase;
      letter-spacing: 0.08em;
      color: var(--color-muted);
      margin-bottom: 8px;
      font-weight: 500;
    }
  }
}

.city-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.city-tag {
  padding: 5px 14px;
  border-radius: var(--radius);
  font-size: 13px;
  cursor: pointer;
  background: var(--color-surface);
  color: var(--color-text);
  border: 1px solid transparent;
  transition: all var(--transition);

  &:hover {
    border-color: var(--color-primary);
    color: var(--color-primary);
    background: rgba(212, 168, 83, 0.08);
  }

  &--active {
    background: rgba(212, 168, 83, 0.15);
    color: var(--color-primary);
    border-color: var(--color-primary);
  }
}

.search-bar {
  flex: 1;
  max-width: 340px;
  display: flex;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: var(--radius);
  overflow: hidden;
  transition: all var(--transition);
  background: var(--color-surface);

  &:focus-within {
    border-color: var(--color-primary);
    box-shadow: 0 0 0 2px rgba(212, 168, 83, 0.08);
  }

  &__input {
    flex: 1;
    border: none;
    padding: 8px 14px;
    font-size: 13px;
    background: transparent;
    color: var(--color-text);

    &::placeholder { color: var(--color-muted); }
  }

  &__btn {
    padding: 8px 12px;
    background: transparent;
    border: none;
    border-left: 1px solid rgba(255, 255, 255, 0.06);
    cursor: pointer;
    color: var(--color-muted);
    transition: all var(--transition);

    &:hover {
      color: var(--color-primary);
      background: rgba(212, 168, 83, 0.06);
    }
  }
}

.search-icon {
  font-size: 16px;
  font-weight: 700;
}

.app-header__nav {
  display: flex;
  gap: 2px;
  flex-shrink: 0;
}

.nav-link {
  position: relative;
  padding: 8px 16px;
  font-size: 14px;
  font-weight: 500;
  color: var(--color-muted);
  text-decoration: none;
  transition: color var(--transition);
  font-family: var(--font-serif-cn);
  letter-spacing: 0.02em;

  &::after {
    content: '';
    position: absolute;
    bottom: 2px;
    left: 50%;
    transform: translateX(-50%) scaleX(0);
    width: 60%;
    height: 1.5px;
    background: var(--color-primary);
    transition: transform var(--transition);
  }

  &:hover {
    color: var(--color-text);
    &::after { transform: translateX(-50%) scaleX(1); }
  }

  &.router-link-active {
    color: var(--color-primary);
    &::after { transform: translateX(-50%) scaleX(1); }
  }
}

.app-header__user {
  display: flex;
  align-items: center;
  flex-shrink: 0;
  gap: 12px;
}

.auth-link {
  padding: 6px 16px;
  font-size: 13px;
  font-weight: 500;
  color: var(--color-muted);
  text-decoration: none;
  border: 1px solid transparent;
  border-radius: var(--radius);
  transition: all var(--transition);
  font-family: var(--font-serif-cn);

  &:hover {
    color: var(--color-primary);
    border-color: var(--color-primary);
    background: rgba(212, 168, 83, 0.06);
  }

  &--accent {
    color: #0D0D0F;
    background: var(--gradient-primary);
    border-color: transparent;
    font-weight: 700;

    &:hover {
      color: #0D0D0F;
      box-shadow: 0 2px 12px rgba(212, 168, 83, 0.3);
    }
  }
}

.user-dropdown {
  position: relative;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 10px;
  border-radius: var(--radius);
  transition: background var(--transition);

  &:hover { background: var(--color-surface); }
}

.user-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: var(--gradient-primary);
  color: #0D0D0F;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 700;
  font-family: var(--font-display);
}

.user-name {
  font-size: 13px;
  font-weight: 500;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: var(--color-text);
}

.user-menu {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  background: var(--color-elevated);
  border: 1px solid var(--color-border);
  border-radius: var(--radius);
  box-shadow: var(--shadow-dropdown);
  min-width: 170px;
  padding: 6px;
  z-index: 100;
  animation: fadeInUp 0.2s ease;

  &__item {
    display: block;
    padding: 9px 14px;
    font-size: 13px;
    color: var(--color-text);
    text-decoration: none;
    border-radius: 4px;
    cursor: pointer;
    transition: all var(--transition);
    font-family: var(--font-serif-cn);

    &:hover {
      background: var(--color-surface);
      color: var(--color-primary);
    }

    &--danger {
      color: var(--color-error);
      &:hover { background: rgba(239, 68, 68, 0.08); color: var(--color-error); }
    }
  }

  &__divider {
    height: 1px;
    background: var(--color-border);
    margin: 4px 0;
  }
}
</style>
