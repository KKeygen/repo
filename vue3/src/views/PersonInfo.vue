<template>
  <component :is="layoutComponent">
    <div class="person-info">
      <div class="page-header">
        <div class="avatar-placeholder">
          <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" /><circle cx="12" cy="7" r="4" />
          </svg>
        </div>
        <h2 class="page-title">个人信息</h2>
        <p class="page-subtitle text-muted">管理您的个人资料</p>
      </div>

      <div v-if="loading" class="loading-state"><div class="spinner"></div></div>

      <div v-else class="card card--static info-form">
        <div class="form-group">
          <label class="form-label">昵称</label>
          <input v-model="formData.name" type="text" class="form-input" placeholder="请输入昵称" />
        </div>
        <div class="form-group">
          <label class="form-label">真实姓名</label>
          <input v-model="formData.relName" type="text" class="form-input" placeholder="请输入真实姓名" />
        </div>
        <div class="form-group">
          <label class="form-label">性别</label>
          <div class="radio-group">
            <label class="radio-item"><input v-model="formData.gender" type="radio" :value="1" /><span>男</span></label>
            <label class="radio-item"><input v-model="formData.gender" type="radio" :value="2" /><span>女</span></label>
            <label class="radio-item"><input v-model="formData.gender" type="radio" :value="0" /><span>保密</span></label>
          </div>
        </div>
        <div class="form-group">
          <label class="form-label">证件号</label>
          <input v-model="formData.idNumber" type="text" class="form-input" placeholder="请输入身份证号" />
        </div>
        <button class="btn btn-primary btn-lg btn-block save-btn" :disabled="saving" @click="handleSave">
          <span v-if="saving" class="spinner"></span>
          <span v-else>保存修改</span>
        </button>
      </div>
    </div>
  </component>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getUserInfo, updateUser } from '@/api/user'
import { useToast } from '@/components/Toast.vue'
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import AccountLayout from '@/layouts/AccountLayout.vue'

const route = useRoute()
const userStore = useUserStore()
const toast = useToast()

const layoutComponent = computed(() => route.meta.layout === 'account' ? AccountLayout : DefaultLayout)

const loading = ref(true)
const saving = ref(false)
const formData = reactive({ name: '', relName: '', gender: 0, idNumber: '' })

const handleSave = async () => {
  saving.value = true
  try {
    const res = await updateUser({ id: userStore.userId, name: formData.name, relName: formData.relName, gender: formData.gender, idNumber: formData.idNumber })
    if (res.code == 0) toast.success('保存成功')
    else toast.error(res.msg || '保存失败')
  } catch (e) { toast.error('网络错误') }
  finally { saving.value = false }
}

onMounted(async () => {
  try {
    const res = await getUserInfo({ id: userStore.userId })
    if (res.code == 0) {
      const data = res.data
      formData.name = data.name || ''; formData.relName = data.relName || ''
      formData.gender = data.gender ?? 0; formData.idNumber = data.idNumber || ''
    }
  } catch (e) { console.error('Load user info failed:', e) }
  finally { loading.value = false }
})
</script>

<style scoped lang="scss">
.person-info {
  min-height: 60vh;
  max-width: 560px;
  margin: 0 auto;
  padding: 24px 0;
}

.page-header {
  text-align: center;
  margin-bottom: 32px;
}

.avatar-placeholder {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: var(--color-surface);
  border: 2px solid var(--color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  color: var(--color-primary);
  box-shadow: 0 0 20px rgba(212, 168, 83, 0.15);
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

.info-form {
  background: var(--color-card-bg);
  border: 1px solid var(--color-border);
  border-radius: 12px;
  padding: 32px;
  backdrop-filter: blur(12px);
  box-shadow: var(--shadow-card);

  &:hover {
    transform: none;
    box-shadow: var(--shadow-card);
  }

  .form-input {
    background: var(--color-bg);
    border: 1px solid var(--color-border);
    color: var(--color-text);
    transition: border-color 0.3s ease, box-shadow 0.3s ease;

    &::placeholder {
      color: var(--color-muted);
    }

    &:focus {
      border-color: var(--color-primary);
      box-shadow: 0 0 0 3px rgba(212, 168, 83, 0.12);
      outline: none;
    }
  }

  .form-label {
    color: var(--color-muted);
    font-size: 13px;
    letter-spacing: 0.02em;
  }
}

.radio-group {
  display: flex;
  gap: 16px;
}

.radio-item {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 14px;
  color: var(--color-text);
  padding: 8px 16px;
  border-radius: 8px;
  border: 1px solid var(--color-border);
  background: var(--color-bg);
  transition: border-color 0.3s ease, background 0.3s ease;

  &:hover {
    border-color: var(--color-primary);
  }

  input[type="radio"] {
    accent-color: var(--color-primary);
    width: 16px;
    height: 16px;
  }

  span {
    user-select: none;
  }
}

.save-btn {
  margin-top: 8px;
  background: var(--gradient-primary);
  border: none;
  font-weight: 600;
  letter-spacing: 0.06em;
  transition: box-shadow 0.3s ease, transform 0.2s ease;

  &:hover:not(:disabled) {
    box-shadow: var(--shadow-gold-glow);
    transform: translateY(-1px);
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}
</style>
