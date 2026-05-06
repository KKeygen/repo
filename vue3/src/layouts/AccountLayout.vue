<template>
  <div class="account-layout">
    <AppHeader />
    <div class="account-layout__body container">
      <AccountSidebar />
      <main class="account-layout__content">
        <slot />
      </main>
    </div>
    <AppFooter />
  </div>
</template>

<script setup>
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
import AccountSidebar from '@/components/AccountSidebar.vue'
</script>

<style scoped lang="scss">
.account-layout {
  position: relative;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: var(--color-bg);
}

.account-layout__body {
  flex: 1;
  display: flex;
  gap: 24px;
  padding-top: 24px;
  padding-bottom: 40px;
  position: relative;
  z-index: 1;
}

.account-layout__content {
  flex: 1;
  min-width: 0;
}
</style>
