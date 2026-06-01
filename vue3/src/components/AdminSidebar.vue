<template>
  <aside class="admin-sidebar">
    <div class="sidebar-user">
      <div class="sidebar-user__avatar">⚙️</div>
      <span class="sidebar-user__label">管理后台</span>
    </div>
    <nav class="sidebar-nav">
      <router-link
        v-for="item in menuItems"
        :key="item.path"
        :to="item.path"
        class="sidebar-nav__item"
        :class="{ 'sidebar-nav__item--active': isActive(item.path) }"
      >
        <span class="sidebar-nav__icon">{{ item.icon }}</span>
        <span class="sidebar-nav__label">{{ item.label }}</span>
      </router-link>
    </nav>
  </aside>
</template>

<script setup>
import { useRoute } from 'vue-router'

const route = useRoute()

const menuItems = [
  { path: '/admin', label: '管理首页', icon: '📊' },
  { path: '/admin/programs', label: '节目管理', icon: '🎭' },
  { path: '/admin/seats', label: '座位管理', icon: '💺' },
  { path: '/admin/ticket-categories', label: '票档管理', icon: '🎫' },
  { path: '/admin/categories', label: '分类管理', icon: '📂' }
]

function isActive(path) {
  if (path === '/admin') return route.path === '/admin'
  return route.path.startsWith(path)
}
</script>

<style scoped lang="scss">
.admin-sidebar {
  width: 220px;
  flex-shrink: 0;
}

.sidebar-user {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 18px 16px 14px;
  margin-bottom: 4px;

  &__avatar {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background: var(--gradient-primary);
    color: #0D0D0F;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 16px;
  }

  &__label {
    font-family: var(--font-display);
    font-size: 15px;
    font-weight: 700;
    color: var(--color-text);
    letter-spacing: 0.03em;
  }
}

.sidebar-nav {
  background: var(--color-card-bg);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
  padding: 6px;
  position: sticky;
  top: 88px;
}

.sidebar-nav__item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 11px 16px;
  border-radius: var(--radius);
  font-size: 14px;
  font-weight: 500;
  font-family: var(--font-serif-cn);
  color: var(--color-muted);
  text-decoration: none;
  transition: all var(--transition);
  border-left: 2px solid transparent;

  &:hover {
    background: var(--color-surface);
    color: var(--color-text);
  }

  &--active {
    background: rgba(212, 168, 83, 0.08);
    color: var(--color-primary);
    border-left-color: var(--color-primary);
  }
}

.sidebar-nav__icon { font-size: 15px; width: 20px; text-align: center; }
.sidebar-nav__label { flex: 1; }
</style>
