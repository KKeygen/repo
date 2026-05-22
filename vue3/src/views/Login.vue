<template>
  <div class="login-page">
    <!-- Background ambient effects -->
    <div class="login-page__ambient"></div>
    <div class="login-page__particles"></div>

    <div class="login-card">
      <!-- Logo -->
      <div class="login-card__logo child-stagger-1">
        <div class="login-card__logo-mark">✦</div>
        <h1 class="font-display">{{ isAdminMode ? '管理员登录' : '登录' }}</h1>
        <p>华彩流光 · 发现精彩，触手可及</p>
      </div>

      <!-- Tabs -->
      <div class="login-card__tabs child-stagger-2">
        <button class="tab tab--active">密码登录</button>
        <button class="tab tab--disabled">
          短信登录
          <span class="tab__badge">即将开放</span>
        </button>
      </div>

      <!-- Form -->
      <form class="login-form" @submit.prevent="handleLogin">
        <div class="form-group child-stagger-3">
          <label class="form-label">手机号 / 邮箱</label>
          <input
            v-model="formData.account"
            type="text"
            class="form-input"
            placeholder="请输入手机号或邮箱"
            :class="{ 'input-error': errors.account }"
          />
          <p v-if="errors.account" class="form-error">{{ errors.account }}</p>
        </div>

        <div class="form-group child-stagger-4">
          <label class="form-label">密码</label>
          <div class="password-wrapper">
            <input
              v-model="formData.password"
              :type="showPassword ? 'text' : 'password'"
              class="form-input"
              placeholder="请输入密码"
              :class="{ 'input-error': errors.password }"
            />
            <button type="button" class="password-toggle" @click="showPassword = !showPassword">
              <svg v-if="showPassword" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/>
              </svg>
              <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/>
                <line x1="1" y1="1" x2="23" y2="23"/>
              </svg>
            </button>
          </div>
          <p v-if="errors.password" class="form-error">{{ errors.password }}</p>
        </div>

        <!-- Trial Account -->
        <div v-if="showTrialAccount" class="trial-hint child-stagger-5" @click="fillTrialAccount">
          <span>🎫 体验账号一键登录</span>
        </div>

        <button type="submit" class="btn btn-primary btn-block btn-lg login-btn child-stagger-6" :disabled="submitting">
          <span v-if="submitting" class="spinner"></span>
          <span v-else>登 录</span>
        </button>
      </form>

      <!-- Footer -->
      <div class="login-card__footer child-stagger-7">
        <span class="text-muted">还没有账号？</span>
        <router-link to="/register">立即注册</router-link>
        <span class="admin-divider">·</span>
        <a v-if="!isAdminMode" href="javascript:void(0)" class="admin-link" @click="isAdminMode = true">🔑 管理员登录</a>
        <a v-else href="javascript:void(0)" class="admin-link" @click="isAdminMode = false">返回普通登录</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useToast } from '@/components/Toast.vue'
import { ADMIN_USER_IDS } from '@/constants/site'

const router = useRouter()
const userStore = useUserStore()
const toast = useToast()

const phonePattern = /^1[3-9]\d{9}$/
const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

const showTrialAccount = computed(() => import.meta.env.VITE_EXPERIENCE_ACCOUNT_FLAG === '1')

const formData = reactive({
  account: '',
  password: ''
})
const errors = reactive({
  account: '',
  password: ''
})
const showPassword = ref(false)
const submitting = ref(false)
const isAdminMode = ref(false)

const fillTrialAccount = () => {
  formData.account = '13800138000'
  formData.password = 'abc123'
}

const validate = () => {
  errors.account = ''
  errors.password = ''
  let valid = true

  if (!formData.account.trim()) {
    errors.account = '请输入手机号或邮箱'
    valid = false
  } else if (!phonePattern.test(formData.account) && !emailPattern.test(formData.account)) {
    errors.account = '请输入正确的手机号或邮箱格式'
    valid = false
  }

  if (!formData.password) {
    errors.password = '请输入密码'
    valid = false
  }

  return valid
}

const handleLogin = async () => {
  if (!validate()) return

  submitting.value = true
  try {
    const credentials = { password: formData.password, code: '0001' }

    if (phonePattern.test(formData.account)) {
      credentials.mobile = formData.account
    } else {
      credentials.email = formData.account
    }

    const res = await userStore.login(credentials)
    if (res.code == 0) {
      toast.success('登录成功')
      if (isAdminMode.value) {
        if (ADMIN_USER_IDS.includes(formData.account)) {
          router.push('/admin')
        } else {
          toast.error('该账号无管理员权限')
          router.push('/')
        }
      } else {
        router.push('/')
      }
    } else {
      toast.error(res.message || '登录失败，请检查账号和密码')
    }
  } catch (e) {
    toast.error('网络错误，请稍后重试')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped lang="scss">
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg);
  padding: 20px;
  position: relative;
  overflow: hidden;

  &__ambient {
    position: absolute;
    inset: 0;
    background:
      radial-gradient(ellipse 60% 50% at 30% 20%, rgba(212, 168, 83, 0.06) 0%, transparent 70%),
      radial-gradient(ellipse 40% 60% at 80% 80%, rgba(198, 40, 40, 0.04) 0%, transparent 60%);
    pointer-events: none;
  }

  &__particles {
    position: absolute;
    inset: 0;
    background-image:
      radial-gradient(1px 1px at 20% 30%, rgba(212, 168, 83, 0.25) 0%, transparent 100%),
      radial-gradient(1px 1px at 70% 20%, rgba(255, 215, 0, 0.15) 0%, transparent 100%),
      radial-gradient(1px 1px at 50% 75%, rgba(212, 168, 83, 0.2) 0%, transparent 100%),
      radial-gradient(1px 1px at 85% 55%, rgba(240, 237, 230, 0.1) 0%, transparent 100%);
    pointer-events: none;
    animation: glowPulse 8s ease-in-out infinite;
  }
}

.login-card {
  width: 100%;
  max-width: 420px;
  background: var(--color-card-bg);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: var(--radius-lg);
  padding: 48px 40px;
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-card);
  position: relative;
  z-index: 1;

  &__logo {
    text-align: center;
    margin-bottom: 36px;

    &-mark {
      font-size: 20px;
      color: var(--color-primary);
      margin-bottom: 8px;
      animation: glowPulse 3s ease-in-out infinite;
    }

    h1 {
      font-size: 30px;
      background: var(--gradient-primary);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      margin-bottom: 8px;
      letter-spacing: 4px;
    }

    p {
      color: var(--color-muted);
      font-size: 13px;
      font-family: var(--font-serif-cn);
      letter-spacing: 1px;
    }
  }

  &__tabs {
    display: flex;
    gap: 0;
    margin-bottom: 28px;
    border-bottom: 1px solid var(--color-border);
  }

  &__footer {
    text-align: center;
    margin-top: 28px;
    font-size: 14px;

    a {
      margin-left: 4px;
      font-weight: 500;
      color: var(--color-primary);
      transition: color var(--transition);

      &:hover {
        color: var(--color-gold);
      }
    }
  }
}

.admin-divider { margin: 0 8px; color: var(--color-border); }
.admin-link { color: var(--color-muted) !important; font-size: 13px; font-weight: 400 !important; transition: color var(--transition); cursor: pointer; }
.admin-link:hover { color: var(--color-primary) !important; }

.tab {
  flex: 1;
  padding: 12px 0;
  background: none;
  font-size: 15px;
  font-weight: 500;
  color: var(--color-muted);
  border: none;
  border-bottom: 2px solid transparent;
  transition: all var(--transition);
  position: relative;
  cursor: pointer;
  font-family: var(--font-body);

  &--active {
    color: var(--color-primary);
    border-bottom-color: var(--color-primary);
  }

  &--disabled {
    cursor: not-allowed;
    opacity: 0.5;
  }

  &__badge {
    position: absolute;
    top: 4px;
    right: 8px;
    font-size: 10px;
    padding: 1px 6px;
    border-radius: 8px;
    background: var(--color-accent);
    color: var(--color-text);
  }
}

.login-form {
  .form-group {
    margin-bottom: 20px;
  }

  .form-label {
    color: var(--color-muted);
  }

  .form-input {
    background: var(--color-elevated);
    border: 1px solid var(--color-border);
    color: var(--color-text);
    transition: border-color var(--transition), box-shadow var(--transition);

    &::placeholder {
      color: rgba(138, 138, 142, 0.6);
    }

    &:focus {
      border-color: var(--color-primary);
      box-shadow: 0 0 0 3px rgba(212, 168, 83, 0.1);
    }
  }
}

.password-wrapper {
  position: relative;

  .form-input {
    padding-right: 44px;
  }
}

.password-toggle {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  padding: 4px;
  color: var(--color-muted);
  cursor: pointer;

  &:hover {
    color: var(--color-primary);
  }
}

.input-error {
  border-color: var(--color-error) !important;
}

.trial-hint {
  text-align: center;
  margin-bottom: 20px;
  padding: 12px;
  background: rgba(212, 168, 83, 0.06);
  border: 1px solid var(--color-border);
  border-radius: var(--radius);
  cursor: pointer;
  font-size: 14px;
  color: var(--color-primary);
  transition: all var(--transition);

  &:hover {
    background: rgba(212, 168, 83, 0.12);
    border-color: rgba(212, 168, 83, 0.3);
  }
}

.login-btn {
  margin-top: 8px;
  height: 48px;
  font-size: 16px;
  letter-spacing: 3px;
  background: var(--gradient-primary);
  color: var(--color-bg);
  border: none;
  font-weight: 600;

  &:hover:not(:disabled) {
    transform: translateY(-1px);
    box-shadow: var(--shadow-gold-glow);
  }

  &:disabled {
    opacity: 0.5;
  }
}
</style>
