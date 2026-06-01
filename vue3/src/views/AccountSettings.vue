<template>
  <component :is="layoutComponent">
    <div class="account-settings">
      <div class="page-header">
        <h2 class="page-title">账号设置</h2>
        <p class="page-subtitle text-muted">管理账号安全与验证信息</p>
      </div>

      <div v-if="loading" class="loading-state"><div class="spinner"></div></div>

      <div v-else class="settings-list">
        <router-link to="/accountSettings/editPassword" class="settings-item card card--static">
          <div class="settings-item__icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"><rect x="3" y="11" width="18" height="11" rx="2" /><path d="M7 11V7a5 5 0 0 1 10 0v4" /></svg>
          </div>
          <div class="settings-item__info">
            <h4>修改密码</h4>
            <p class="text-muted">定期修改密码，保障账号安全</p>
          </div>
          <svg class="settings-item__chevron" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"/></svg>
        </router-link>

        <router-link to="/accountSettings/email" class="settings-item card card--static">
          <div class="settings-item__icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" /><polyline points="22,6 12,13 2,6" /></svg>
          </div>
          <div class="settings-item__info">
            <h4>邮箱验证</h4>
            <p class="text-muted">
              <span v-if="userInfo.emailStatus === 1" class="text-success">已验证：{{ userInfo.email }}</span>
              <span v-else-if="userInfo.email" class="text-warning">已绑定：{{ userInfo.email }}（待验证）</span>
              <span v-else class="text-error">未绑定</span>
            </p>
          </div>
          <svg class="settings-item__chevron" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"/></svg>
        </router-link>

        <router-link to="/accountSettings/mobile" class="settings-item card card--static">
          <div class="settings-item__icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"><rect x="5" y="2" width="14" height="20" rx="2" /><line x1="12" y1="18" x2="12.01" y2="18" /></svg>
          </div>
          <div class="settings-item__info">
            <h4>手机验证</h4>
            <p class="text-muted">
              <span v-if="userInfo.mobile">已绑定：{{ maskPhone(userInfo.mobile) }}</span>
              <span v-else class="text-error">未绑定</span>
            </p>
          </div>
          <svg class="settings-item__chevron" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"/></svg>
        </router-link>

        <router-link to="/accountSettings/authentication" class="settings-item card card--static">
          <div class="settings-item__icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" /></svg>
          </div>
          <div class="settings-item__info">
            <h4>实名认证</h4>
            <p class="text-muted">
              <span v-if="userInfo.relAuthenticationStatus === 1" class="text-success">已认证</span>
              <span v-else class="text-error">未认证</span>
            </p>
          </div>
          <svg class="settings-item__chevron" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"/></svg>
        </router-link>
      </div>
    </div>
  </component>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getUserInfo } from '@/api/user'
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import AccountLayout from '@/layouts/AccountLayout.vue'

const route = useRoute()
const userStore = useUserStore()

const layoutComponent = computed(() => route.meta.layout === 'account' ? AccountLayout : DefaultLayout)

const loading = ref(true)
const userInfo = reactive({ email: '', emailStatus: 0, mobile: '', relAuthenticationStatus: 0 })

const maskPhone = (phone) => { const p = String(phone || ''); if (p.length < 7) return p; return p.slice(0, 3) + '****' + p.slice(-4) }

onMounted(async () => {
  try {
    const res = await getUserInfo({ id: userStore.userId })
    if (res.code == 0) {
      const data = res.data
      userInfo.email = data.email || ''
      userInfo.emailStatus = Number(data.emailStatus || 0)
      userInfo.mobile = data.mobile || ''
      userInfo.relAuthenticationStatus = Number(data.relAuthenticationStatus || 0)
    }
  } catch (e) { console.error('Load user info failed:', e) }
  finally { loading.value = false }
})
</script>

<style scoped lang="scss">
.account-settings {
  min-height: 60vh;
  max-width: 640px;
  margin: 0 auto;
  padding: 24px 0;
}

.page-header {
  margin-bottom: 32px;
}

.page-title {
  font-family: var(--font-serif-cn), serif;
  font-size: 24px;
  color: var(--color-text);
  margin: 0 0 6px;
  letter-spacing: 0.04em;
}

.page-subtitle {
  font-size: 13px;
  margin: 0;
}

.loading-state {
  display: flex;
  justify-content: center;
  padding: 60px;
}

.settings-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.settings-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 24px;
  text-decoration: none;
  color: inherit;
  background: var(--color-card-bg);
  border: 1px solid var(--color-border);
  border-radius: 12px;
  backdrop-filter: blur(12px);
  box-shadow: var(--shadow-card);
  transition: border-color 0.3s ease, box-shadow 0.3s ease, transform 0.25s ease;

  &:hover {
    border-color: var(--color-primary);
    box-shadow: var(--shadow-card-hover), 0 0 20px rgba(212, 168, 83, 0.08);
    transform: translateY(-2px);
  }

  &__icon {
    width: 40px;
    height: 40px;
    border-radius: 10px;
    background: var(--color-elevated);
    border: 1px solid var(--color-border);
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--color-primary);
    flex-shrink: 0;
    transition: background 0.3s ease;
  }

  &:hover &__icon {
    background: rgba(212, 168, 83, 0.1);
  }

  &__info {
    flex: 1;
    min-width: 0;

    h4 {
      font-size: 15px;
      font-weight: 500;
      margin: 0 0 4px;
      color: var(--color-text);
    }

    p {
      font-size: 13px;
      margin: 0;
    }
  }

  &__chevron {
    color: var(--color-primary);
    opacity: 0.5;
    flex-shrink: 0;
    transition: opacity 0.3s ease, transform 0.3s ease;
  }

  &:hover &__chevron {
    opacity: 1;
    transform: translateX(3px);
  }
}
</style>
