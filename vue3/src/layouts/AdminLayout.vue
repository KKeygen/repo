<template>
  <div class="admin-layout">
    <AppHeader />
    <div class="admin-layout__body container">
      <AdminSidebar />
      <main class="admin-layout__content">
        <slot />
      </main>
    </div>
    <AppFooter />
  </div>
</template>

<script setup>
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
import AdminSidebar from '@/components/AdminSidebar.vue'
</script>

<style scoped lang="scss">
.admin-layout {
  position: relative;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: var(--color-bg);
}

.admin-layout__body {
  flex: 1;
  display: flex;
  gap: 24px;
  padding-top: 24px;
  padding-bottom: 40px;
  position: relative;
  z-index: 1;
}

.admin-layout__content {
  flex: 1;
  min-width: 0;
}
</style>
