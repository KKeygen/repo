<template>
  <component :is="layoutComponent">
    <div class="authentication">
      <h2 class="page-title">实名认证</h2>
      <p class="page-subtitle">完成身份验证以解锁全部功能</p>

      <div class="glass-card">
        <div class="card-inner">
          <div class="form-group">
            <label class="form-label">真实姓名</label>
            <div class="input-wrapper">
              <svg class="input-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
              <input v-model="formData.relName" type="text" class="form-input" placeholder="请输入真实姓名" :class="{ 'input-error': errors.relName }" />
            </div>
            <p v-if="errors.relName" class="form-error">{{ errors.relName }}</p>
          </div>

          <div class="form-group">
            <label class="form-label">证件类型</label>
            <div class="select-wrapper">
              <svg class="input-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="2" y="5" width="20" height="14" rx="2"/><line x1="2" y1="10" x2="22" y2="10"/></svg>
              <select v-model="formData.idType" class="form-input form-select">
                <option v-for="t in idTypes" :key="t.value" :value="t.value">{{ t.label }}</option>
              </select>
              <svg class="select-arrow" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="6 9 12 15 18 9"/></svg>
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">证件号码</label>
            <div class="input-wrapper">
              <svg class="input-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
              <input v-model="formData.idNumber" type="text" class="form-input" placeholder="请输入证件号码" :class="{ 'input-error': errors.idNumber }" />
            </div>
            <p v-if="errors.idNumber" class="form-error">{{ errors.idNumber }}</p>
          </div>

          <div class="notice">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>
            <span>实名认证信息提交后不可修改，请确保信息准确</span>
          </div>

          <button class="btn btn-primary btn-lg submit-btn" :disabled="saving" @click="handleSubmit">
            <span v-if="saving" class="spinner"></span><span v-else>提交认证</span>
          </button>
        </div>
      </div>
    </div>
  </component>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { authenticate } from '@/api/user'
import { useToast } from '@/components/Toast.vue'
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import AccountLayout from '@/layouts/AccountLayout.vue'

const route = useRoute()
const router = useRouter()
const toast = useToast()

const layoutComponent = computed(() => route.meta.layout === 'account' ? AccountLayout : DefaultLayout)

const saving = ref(false)
const formData = reactive({ relName: '', idType: 1, idNumber: '' })
const errors = reactive({ relName: '', idNumber: '' })

const idTypes = [
  { value: 1, label: '身份证' }, { value: 2, label: '港澳台居民证' },
  { value: 3, label: '港澳通行证' }, { value: 4, label: '台湾通行证' },
  { value: 5, label: '护照' }, { value: 6, label: '外国人永居证' }
]

const validate = () => {
  errors.relName = ''; errors.idNumber = ''
  let valid = true
  if (!formData.relName.trim()) { errors.relName = '请输入真实姓名'; valid = false }
  if (!formData.idNumber.trim()) { errors.idNumber = '请输入证件号码'; valid = false }
  return valid
}

const handleSubmit = async () => {
  if (!validate()) return
  saving.value = true
  try {
    const res = await authenticate({ relName: formData.relName, idType: formData.idType, idNumber: formData.idNumber, code: '0001' })
    if (res.code === 0) { toast.success('认证成功'); router.push('/accountSettings') }
    else toast.error(res.msg || '认证失败')
  } catch (e) { toast.error('网络错误') }
  finally { saving.value = false }
}
</script>

<style scoped lang="scss">
.authentication {
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

.input-wrapper,
.select-wrapper {
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
  z-index: 1;
}

.input-wrapper:focus-within .input-icon,
.select-wrapper:focus-within .input-icon {
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

.form-select {
  appearance: none;
  cursor: pointer;
  padding-right: 40px;

  option {
    background: var(--color-surface);
    color: var(--color-text);
    padding: 8px;
  }
}

.select-arrow {
  position: absolute;
  right: 14px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--color-muted);
  pointer-events: none;
  transition: color 0.3s ease;
}

.select-wrapper:focus-within .select-arrow {
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

.notice {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 14px 16px;
  background: rgba(212, 168, 83, 0.06);
  border: 1px solid rgba(212, 168, 83, 0.12);
  border-radius: 10px;
  margin-bottom: 24px;
  font-family: var(--font-body);
  font-size: 13px;
  line-height: 1.5;
  color: var(--color-primary);

  svg {
    flex-shrink: 0;
    margin-top: 2px;
    color: var(--color-primary);
    opacity: 0.8;
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
