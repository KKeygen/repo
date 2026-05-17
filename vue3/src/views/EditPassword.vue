<template>
  <component :is="layoutComponent">
    <div class="edit-password">
      <h2 class="page-title">修改密码</h2>
      <p class="page-subtitle">更新您的账户安全密码</p>

      <div class="glass-card">
        <div class="card-inner">
          <div class="form-group">
            <label class="form-label">当前密码</label>
            <div class="input-wrapper">
              <svg class="input-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
              <input v-model="formData.oldPassword" type="password" class="form-input" placeholder="请输入当前密码" :class="{ 'input-error': errors.oldPassword }" />
            </div>
            <p v-if="errors.oldPassword" class="form-error">{{ errors.oldPassword }}</p>
          </div>

          <div class="form-group">
            <label class="form-label">新密码</label>
            <div class="input-wrapper">
              <svg class="input-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M21 2l-2 2m-7.61 7.61a5.5 5.5 0 1 1-7.778 7.778 5.5 5.5 0 0 1 7.777-7.777zm0 0L15.5 7.5m0 0l3 3L22 7l-3-3m-3.5 3.5L19 4"/></svg>
              <input v-model="formData.newPassword" type="password" class="form-input" placeholder="6-20位，需包含字母和数字" :class="{ 'input-error': errors.newPassword }" />
            </div>
            <p v-if="errors.newPassword" class="form-error">{{ errors.newPassword }}</p>
          </div>

          <div class="form-group">
            <label class="form-label">确认新密码</label>
            <div class="input-wrapper">
              <svg class="input-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
              <input v-model="formData.confirmPassword" type="password" class="form-input" placeholder="请再次输入新密码" :class="{ 'input-error': errors.confirmPassword }" />
            </div>
            <p v-if="errors.confirmPassword" class="form-error">{{ errors.confirmPassword }}</p>
          </div>

          <button class="btn btn-primary btn-lg submit-btn" :disabled="saving" @click="handleSave">
            <span v-if="saving" class="spinner"></span><span v-else>保存修改</span>
          </button>
        </div>
      </div>
    </div>
  </component>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { updatePassword } from '@/api/user'
import { useToast } from '@/components/Toast.vue'
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import AccountLayout from '@/layouts/AccountLayout.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const toast = useToast()

const layoutComponent = computed(() => route.meta.layout === 'account' ? AccountLayout : DefaultLayout)

const saving = ref(false)
const formData = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const errors = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const passwordPattern = /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d]{6,20}$/

const validate = () => {
  errors.oldPassword = ''; errors.newPassword = ''; errors.confirmPassword = ''
  let valid = true
  if (!formData.oldPassword) { errors.oldPassword = '请输入当前密码'; valid = false }
  if (!formData.newPassword) { errors.newPassword = '请输入新密码'; valid = false }
  else if (!passwordPattern.test(formData.newPassword)) { errors.newPassword = '密码需为6-20位字母和数字组合'; valid = false }
  if (!formData.confirmPassword) { errors.confirmPassword = '请再次输入新密码'; valid = false }
  else if (formData.newPassword !== formData.confirmPassword) { errors.confirmPassword = '两次输入的密码不一致'; valid = false }
  return valid
}

const handleSave = async () => {
  if (!validate()) return
  saving.value = true
  try {
    const res = await updatePassword({ id: userStore.userId, password: formData.newPassword })
    if (res.code == 0) { toast.success('密码修改成功'); router.push('/accountSettings') }
    else toast.error(res.msg || '修改失败')
  } catch (e) { toast.error('网络错误') }
  finally { saving.value = false }
}
</script>

<style scoped lang="scss">
.edit-password {
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

  &:focus ~ .input-icon,
  &:focus + .input-icon {
    color: var(--color-primary);
  }
}

.input-wrapper:focus-within .input-icon {
  color: var(--color-primary);
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
