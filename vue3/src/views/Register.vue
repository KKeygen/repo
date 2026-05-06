<template>
  <div class="register-page">
    <!-- Background ambient effects -->
    <div class="register-page__ambient"></div>
    <div class="register-page__particles"></div>

    <div class="register-card">
      <!-- Logo -->
      <div class="register-card__logo child-stagger-1">
        <div class="register-card__logo-mark">✦</div>
        <h1 class="font-display">创建账号</h1>
        <p>华彩流光 · 探索精彩世界</p>
      </div>

      <!-- Form -->
      <form class="register-form" @submit.prevent="handleSubmit">
        <div class="form-group child-stagger-2">
          <label class="form-label">手机号</label>
          <div class="phone-input">
            <span class="phone-prefix">+86</span>
            <input
              v-model="formData.mobile"
              type="text"
              class="form-input phone-field"
              placeholder="请输入手机号"
              maxlength="11"
              :class="{ 'input-error': errors.mobile }"
            />
          </div>
          <p v-if="errors.mobile" class="form-error">{{ errors.mobile }}</p>
        </div>

        <div class="form-group child-stagger-3">
          <label class="form-label">密码</label>
          <input
            v-model="formData.password"
            type="password"
            class="form-input"
            placeholder="6-20位，需包含字母和数字"
            :class="{ 'input-error': errors.password }"
          />
          <p v-if="errors.password" class="form-error">{{ errors.password }}</p>
        </div>

        <div class="form-group child-stagger-4">
          <label class="form-label">确认密码</label>
          <input
            v-model="formData.confirmPassword"
            type="password"
            class="form-input"
            placeholder="请再次输入密码"
            :class="{ 'input-error': errors.confirmPassword }"
          />
          <p v-if="errors.confirmPassword" class="form-error">{{ errors.confirmPassword }}</p>
        </div>

        <div class="form-group child-stagger-5">
          <label class="agreement">
            <input v-model="formData.agreed" type="checkbox" class="agreement__checkbox" />
            <span class="agreement__text">
              我已阅读并同意
              <a href="javascript:void(0)">《用户协议》</a>
              和
              <a href="javascript:void(0)">《隐私政策》</a>
            </span>
          </label>
          <p v-if="errors.agreed" class="form-error">{{ errors.agreed }}</p>
        </div>

        <button type="submit" class="btn btn-primary btn-block btn-lg register-btn child-stagger-6" :disabled="submitting">
          <span v-if="submitting" class="spinner"></span>
          <span v-else>注 册</span>
        </button>
      </form>

      <!-- Captcha Modal -->
      <div v-if="showCaptcha" class="captcha-overlay" @click.self="showCaptcha = false">
        <CaptchaVerify @success="onCaptchaSuccess" @close="showCaptcha = false" />
      </div>

      <!-- Footer -->
      <div class="register-card__footer child-stagger-7">
        <span class="text-muted">已有账号？</span>
        <router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register, checkCaptchaNeed } from '@/api/user'
import CaptchaVerify from '@/components/CaptchaVerify.vue'
import { useToast } from '@/components/Toast.vue'

const router = useRouter()
const toast = useToast()

const phonePattern = /^1[3-9]\d{9}$/
const passwordPattern = /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d]{6,20}$/

const formData = reactive({
  mobile: '',
  password: '',
  confirmPassword: '',
  agreed: false
})
const errors = reactive({
  mobile: '',
  password: '',
  confirmPassword: '',
  agreed: ''
})
const submitting = ref(false)
const showCaptcha = ref(false)

const validate = () => {
  errors.mobile = ''
  errors.password = ''
  errors.confirmPassword = ''
  errors.agreed = ''
  let valid = true

  if (!formData.mobile.trim()) {
    errors.mobile = '请输入手机号'
    valid = false
  } else if (!phonePattern.test(formData.mobile)) {
    errors.mobile = '请输入正确的手机号格式'
    valid = false
  }

  if (!formData.password) {
    errors.password = '请输入密码'
    valid = false
  } else if (!passwordPattern.test(formData.password)) {
    errors.password = '密码需为6-20位字母和数字组合'
    valid = false
  }

  if (!formData.confirmPassword) {
    errors.confirmPassword = '请再次输入密码'
    valid = false
  } else if (formData.password !== formData.confirmPassword) {
    errors.confirmPassword = '两次输入的密码不一致'
    valid = false
  }

  if (!formData.agreed) {
    errors.agreed = '请先同意用户协议和隐私政策'
    valid = false
  }

  return valid
}

const handleSubmit = async () => {
  if (!validate()) return

  submitting.value = true
  try {
    const captchaRes = await checkCaptchaNeed({ mobile: formData.mobile })
    if (captchaRes.code === 0 && captchaRes.data?.verifyCaptcha) {
      showCaptcha.value = true
      submitting.value = false
      return
    }
    await doRegister()
  } catch (e) {
    await doRegister()
  }
}

const onCaptchaSuccess = () => {
  showCaptcha.value = false
  doRegister()
}

const doRegister = async () => {
  submitting.value = true
  try {
    const res = await register({
      mobile: formData.mobile,
      password: formData.password,
      code: '0001'
    })

    if (res.code === 0) {
      toast.success('注册成功，请登录')
      router.push('/login')
    } else {
      toast.error(res.msg || '注册失败，请稍后重试')
    }
  } catch (e) {
    toast.error('网络错误，请稍后重试')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped lang="scss">
.register-page {
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
      radial-gradient(ellipse 50% 50% at 70% 25%, rgba(212, 168, 83, 0.06) 0%, transparent 70%),
      radial-gradient(ellipse 45% 55% at 25% 75%, rgba(198, 40, 40, 0.04) 0%, transparent 60%);
    pointer-events: none;
  }

  &__particles {
    position: absolute;
    inset: 0;
    background-image:
      radial-gradient(1px 1px at 25% 20%, rgba(212, 168, 83, 0.25) 0%, transparent 100%),
      radial-gradient(1px 1px at 65% 35%, rgba(255, 215, 0, 0.15) 0%, transparent 100%),
      radial-gradient(1px 1px at 40% 80%, rgba(212, 168, 83, 0.2) 0%, transparent 100%),
      radial-gradient(1px 1px at 80% 70%, rgba(240, 237, 230, 0.1) 0%, transparent 100%);
    pointer-events: none;
    animation: glowPulse 8s ease-in-out infinite;
  }
}

.register-card {
  width: 100%;
  max-width: 440px;
  background: var(--color-card-bg);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: var(--radius-lg);
  padding: 44px 40px;
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-card);
  position: relative;
  z-index: 1;

  &__logo {
    text-align: center;
    margin-bottom: 32px;

    &-mark {
      font-size: 20px;
      color: var(--color-primary);
      margin-bottom: 8px;
      animation: glowPulse 3s ease-in-out infinite;
    }

    h1 {
      font-size: 28px;
      background: var(--gradient-primary);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      margin-bottom: 8px;
      letter-spacing: 3px;
    }

    p {
      color: var(--color-muted);
      font-size: 13px;
      font-family: var(--font-serif-cn);
      letter-spacing: 1px;
    }
  }

  &__footer {
    text-align: center;
    margin-top: 24px;
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

.register-form {
  .form-group {
    margin-bottom: 18px;
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

.phone-input {
  display: flex;
  align-items: center;
  gap: 0;
}

.phone-prefix {
  padding: 10px 14px;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-right: none;
  border-radius: var(--radius) 0 0 var(--radius);
  color: var(--color-muted);
  font-size: 14px;
  white-space: nowrap;
}

.phone-field {
  border-radius: 0 var(--radius) var(--radius) 0 !important;
}

.input-error {
  border-color: var(--color-error) !important;
}

.agreement {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  cursor: pointer;

  &__checkbox {
    margin-top: 3px;
    width: 16px;
    height: 16px;
    accent-color: var(--color-primary);
  }

  &__text {
    font-size: 13px;
    color: var(--color-muted);
    line-height: 1.5;

    a {
      color: var(--color-primary);
      font-weight: 500;
      transition: color var(--transition);

      &:hover {
        color: var(--color-gold);
      }
    }
  }
}

.register-btn {
  margin-top: 8px;
  height: 48px;
  font-size: 16px;
  letter-spacing: 4px;
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

.captcha-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
</style>
