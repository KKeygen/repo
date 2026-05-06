<template>
  <component :is="layoutComponent">
    <div class="mobile-verify">
      <h2 class="page-title">手机验证</h2>
      <p class="page-subtitle">绑定手机号以保护账户安全</p>

      <div class="glass-card">
        <div class="card-inner">
          <div class="form-group">
            <label class="form-label">手机号</label>
            <div class="phone-input">
              <span class="phone-prefix">+86</span>
              <input v-model="formData.mobile" type="text" class="form-input phone-field" placeholder="请输入手机号" maxlength="11" :class="{ 'input-error': errors.mobile }" />
            </div>
            <p v-if="errors.mobile" class="form-error">{{ errors.mobile }}</p>
          </div>

          <div class="form-group">
            <label class="form-label">验证码</label>
            <div class="code-input">
              <div class="input-wrapper code-input-field">
                <svg class="input-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
                <input v-model="formData.code" type="text" class="form-input" placeholder="请输入验证码" maxlength="6" />
              </div>
              <button class="btn-code" :disabled="codeSending || codeCountdown > 0" @click="sendCode">
                {{ codeCountdown > 0 ? `${codeCountdown}s 后重发` : '获取验证码' }}
              </button>
            </div>
          </div>

          <button class="btn btn-primary btn-lg submit-btn" :disabled="saving" @click="handleSubmit">
            <span v-if="saving" class="spinner"></span><span v-else>确认绑定</span>
          </button>
        </div>
      </div>
    </div>
  </component>
</template>

<script setup>
import { ref, reactive, computed, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { updateMobile } from '@/api/user'
import { useToast } from '@/components/Toast.vue'
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import AccountLayout from '@/layouts/AccountLayout.vue'

const route = useRoute()
const router = useRouter()
const toast = useToast()

const layoutComponent = computed(() => route.meta.layout === 'account' ? AccountLayout : DefaultLayout)

const saving = ref(false)
const codeSending = ref(false)
const codeCountdown = ref(0)
let countdownTimer = null

const formData = reactive({ mobile: '', code: '' })
const errors = reactive({ mobile: '' })

const phonePattern = /^1[3-9]\d{9}$/

const sendCode = () => {
  if (!formData.mobile || !phonePattern.test(formData.mobile)) { errors.mobile = '请输入正确的手机号'; return }
  errors.mobile = ''
  codeSending.value = true
  setTimeout(() => {
    codeSending.value = false
    codeCountdown.value = 60
    toast.success('验证码已发送')
    countdownTimer = setInterval(() => {
      codeCountdown.value--
      if (codeCountdown.value <= 0) clearInterval(countdownTimer)
    }, 1000)
  }, 1000)
}

const handleSubmit = async () => {
  if (!formData.mobile || !phonePattern.test(formData.mobile)) { errors.mobile = '请输入正确的手机号'; return }
  if (!formData.code) { toast.error('请输入验证码'); return }
  saving.value = true
  try {
    const res = await updateMobile({ mobile: formData.mobile, code: formData.code })
    if (res.code === 0) { toast.success('手机号绑定成功'); router.push('/accountSettings') }
    else toast.error(res.msg || '绑定失败')
  } catch (e) { toast.error('网络错误') }
  finally { saving.value = false }
}

onUnmounted(() => { if (countdownTimer) clearInterval(countdownTimer) })
</script>

<style scoped lang="scss">
.mobile-verify {
  min-height: 60vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 48px 20px;
}

.page-title {
  font-family: var(--font-serif-cn), var(--font-display), serif;
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text);
  margin-bottom: 8px;
  text-align: center;
  letter-spacing: 2px;
}

.page-subtitle {
  font-family: var(--font-body);
  font-size: 14px;
  color: var(--color-muted);
  margin-bottom: 36px;
  text-align: center;
}

.glass-card {
  width: 100%;
  max-width: 480px;
  background: var(--color-card-bg);
  backdrop-filter: blur(24px);
  border: 1px solid var(--color-border);
  border-radius: 16px;
  box-shadow: var(--shadow-card);
  overflow: hidden;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 2px;
    background: var(--gradient-primary);
    opacity: 0.6;
  }
}

.card-inner {
  padding: 36px 32px;
}

.form-group {
  margin-bottom: 24px;
}

.form-label {
  display: block;
  font-family: var(--font-body);
  font-size: 13px;
  font-weight: 500;
  color: var(--color-muted);
  margin-bottom: 8px;
  letter-spacing: 0.5px;
  text-transform: uppercase;
}

.phone-input {
  display: flex;
  align-items: stretch;
}

.phone-prefix {
  display: flex;
  align-items: center;
  padding: 0 14px;
  background: var(--color-elevated);
  border: 1px solid var(--color-border);
  border-right: none;
  border-radius: 10px 0 0 10px;
  color: var(--color-primary);
  font-family: var(--font-body);
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.phone-field {
  border-radius: 0 10px 10px 0 !important;
  padding-left: 14px !important;
}

.input-wrapper {
  position: relative;
}

.input-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--color-muted);
  pointer-events: none;
  transition: color 0.3s ease;
}

.input-wrapper:focus-within .input-icon {
  color: var(--color-primary);
}

.form-input {
  width: 100%;
  padding: 12px 14px 12px 44px;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 10px;
  color: var(--color-text);
  font-family: var(--font-body);
  font-size: 15px;
  transition: all 0.3s ease;
  outline: none;
  box-sizing: border-box;

  &::placeholder {
    color: var(--color-muted);
    opacity: 0.5;
  }

  &:focus {
    border-color: var(--color-primary);
    box-shadow: 0 0 0 3px rgba(212, 168, 83, 0.1);
  }
}

.input-error {
  border-color: var(--color-error) !important;
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1) !important;
}

.form-error {
  font-size: 12px;
  color: var(--color-error);
  margin-top: 6px;
  padding-left: 2px;
}

.code-input {
  display: flex;
  gap: 12px;
  align-items: stretch;
}

.code-input-field {
  flex: 1;
  min-width: 0;
}

.btn-code {
  flex-shrink: 0;
  min-width: 128px;
  padding: 12px 16px;
  background: transparent;
  border: 1px solid var(--color-primary);
  border-radius: 10px;
  color: var(--color-primary);
  font-family: var(--font-body);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.3s ease;

  &:hover:not(:disabled) {
    background: rgba(212, 168, 83, 0.1);
    box-shadow: var(--shadow-gold-glow);
  }

  &:disabled {
    opacity: 0.4;
    cursor: not-allowed;
    border-color: var(--color-muted);
    color: var(--color-muted);
  }
}

.submit-btn {
  width: 100%;
  margin-top: 8px;
  padding: 14px;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 1px;
  border-radius: 10px;
  background: var(--gradient-primary);
  border: none;
  color: var(--color-bg);
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover:not(:disabled) {
    box-shadow: var(--shadow-gold-glow);
    transform: translateY(-1px);
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}
</style>
