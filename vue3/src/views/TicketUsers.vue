<template>
  <component :is="layoutComponent">
    <div class="ticket-users">
      <div class="ticket-users__header">
        <h2 class="page-title">购票人管理</h2>
        <button class="btn btn-primary btn-sm add-btn" @click="showModal = true">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><line x1="12" y1="5" x2="12" y2="19" /><line x1="5" y1="12" x2="19" y2="12" /></svg>
          <span>添加购票人</span>
        </button>
      </div>

      <div v-if="loading" class="loading-state"><div class="spinner"></div></div>

      <div v-else>
        <div v-if="users.length === 0" class="empty-state">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" /><circle cx="9" cy="7" r="4" /><path d="M23 21v-2a4 4 0 0 0-3-3.87" /><path d="M16 3.13a4 4 0 0 1 0 7.75" /></svg>
          <p class="text-muted">暂无购票人信息</p>
        </div>
        <div v-else class="user-table">
          <div class="user-row user-row--header">
            <span>姓名</span><span>证件类型</span><span>证件号码</span><span>操作</span>
          </div>
          <div v-for="user in users" :key="user.id" class="user-row">
            <span class="user-row__name">{{ user.relName }}</span>
            <span>{{ getIdTypeName(user.relIdType) }}</span>
            <span class="text-muted">{{ maskId(user.relIdNumber) }}</span>
            <span>
              <button class="btn btn-ghost btn-sm delete-btn" @click="handleDelete(user)">删除</button>
            </span>
          </div>
        </div>
      </div>

      <!-- Add Modal -->
      <div v-if="showModal" class="dialog-overlay" @click.self="showModal = false">
        <div class="dialog">
          <div class="dialog__header">
            <h3>添加购票人</h3>
            <div class="dialog__header-line"></div>
          </div>
          <div class="form-group">
            <label class="form-label">姓名</label>
            <input v-model="newUser.relName" type="text" class="form-input" placeholder="请输入姓名" />
          </div>
          <div class="form-group">
            <label class="form-label">证件类型</label>
            <select v-model="newUser.relIdType" class="form-input">
              <option v-for="t in idTypes" :key="t.value" :value="t.value">{{ t.label }}</option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-label">证件号码</label>
            <input v-model="newUser.relIdNumber" type="text" class="form-input" placeholder="请输入证件号码" />
          </div>
          <div class="dialog__actions">
            <button class="btn btn-ghost" @click="showModal = false">取消</button>
            <button class="btn btn-primary confirm-btn" :disabled="addLoading" @click="handleAdd">
              <span v-if="addLoading" class="spinner"></span><span v-else>确认添加</span>
            </button>
          </div>
        </div>
      </div>

      <!-- Delete Confirm -->
      <div v-if="showDeleteDialog" class="dialog-overlay" @click.self="showDeleteDialog = false">
        <div class="dialog dialog--danger">
          <div class="dialog__header">
            <h3>确认删除</h3>
            <div class="dialog__header-line dialog__header-line--danger"></div>
          </div>
          <p class="dialog__body-text">确定要删除购票人 <strong>{{ deleteTarget?.relName }}</strong> 吗？</p>
          <div class="dialog__actions">
            <button class="btn btn-ghost" @click="showDeleteDialog = false">取消</button>
            <button class="btn btn-primary danger-confirm-btn" @click="confirmDelete">确认删除</button>
          </div>
        </div>
      </div>
    </div>
  </component>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getTicketUserList, addTicketUser, deleteTicketUser } from '@/api/ticketUser'
import { useToast } from '@/components/Toast.vue'
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import AccountLayout from '@/layouts/AccountLayout.vue'

const route = useRoute()
const userStore = useUserStore()
const toast = useToast()

const layoutComponent = computed(() => route.meta.layout === 'account' ? AccountLayout : DefaultLayout)

const loading = ref(true)
const addLoading = ref(false)
const users = ref([])
const showModal = ref(false)
const showDeleteDialog = ref(false)
const deleteTarget = ref(null)

const idTypes = [
  { value: 1, label: '身份证' }, { value: 2, label: '港澳台居民证' },
  { value: 3, label: '港澳通行证' }, { value: 4, label: '台湾通行证' },
  { value: 5, label: '护照' }, { value: 6, label: '外国人永居证' }
]

const newUser = reactive({ relName: '', relIdType: 1, relIdNumber: '' })

const getIdTypeName = (type) => idTypes.find(t => t.value === type)?.label || '证件'
const maskId = (id) => { if (!id || id.length < 6) return id || ''; return id.slice(0, 3) + '****' + id.slice(-4) }

const loadUsers = async () => {
  try {
    const res = await getTicketUserList({ userId: userStore.userId })
    if (res.code == 0) users.value = res.data || []
  } catch (e) { console.error('Load ticket users failed:', e) }
  finally { loading.value = false }
}

const handleAdd = async () => {
  if (!newUser.relName.trim() || !newUser.relIdNumber.trim()) { toast.error('请填写完整信息'); return }
  addLoading.value = true
  try {
    const res = await addTicketUser({ userId: userStore.userId, relName: newUser.relName, idType: newUser.relIdType, idNumber: newUser.relIdNumber })
    if (res.code == 0) {
      toast.success('添加成功'); showModal.value = false
      newUser.relName = ''; newUser.relIdNumber = ''; newUser.relIdType = 1
      await loadUsers()
    } else toast.error(res.msg || '添加失败')
  } catch (e) { toast.error('网络错误') }
  finally { addLoading.value = false }
}

const handleDelete = (user) => { deleteTarget.value = user; showDeleteDialog.value = true }

const confirmDelete = async () => {
  if (!deleteTarget.value) return
  try {
    const res = await deleteTicketUser({ id: deleteTarget.value.id })
    if (res.code == 0) { toast.success('删除成功'); users.value = users.value.filter(u => u.id !== deleteTarget.value.id) }
    else toast.error(res.msg || '删除失败')
  } catch (e) { toast.error('网络错误') }
  finally { showDeleteDialog.value = false; deleteTarget.value = null }
}

onMounted(() => { loadUsers() })
</script>

<style scoped lang="scss">
.ticket-users {
  min-height: 60vh;
  padding: 24px 0;

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 28px;
  }
}

.page-title {
  font-family: var(--font-serif-cn), serif;
  font-size: 24px;
  color: var(--color-text);
  margin: 0;
  letter-spacing: 0.04em;
}

.add-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: var(--gradient-primary);
  border: none;
  color: var(--color-bg);
  font-weight: 600;
  letter-spacing: 0.03em;
  transition: box-shadow 0.3s ease, transform 0.2s ease;

  &:hover {
    box-shadow: var(--shadow-gold-glow);
    transform: translateY(-1px);
  }
}

.loading-state {
  display: flex;
  justify-content: center;
  padding: 60px;
}

.empty-state {
  text-align: center;
  padding: 72px 0;
  color: var(--color-muted);

  svg {
    margin-bottom: 16px;
    opacity: 0.35;
  }

  p {
    font-size: 14px;
  }
}

.user-table {
  background: var(--color-card-bg);
  border: 1px solid var(--color-border);
  border-radius: 12px;
  overflow: hidden;
  backdrop-filter: blur(12px);
  box-shadow: var(--shadow-card);
}

.user-row {
  display: grid;
  grid-template-columns: 1fr 1fr 1.5fr auto;
  align-items: center;
  padding: 16px 24px;
  font-size: 14px;
  color: var(--color-text);
  border-bottom: 1px solid var(--color-border);
  transition: background 0.25s ease, box-shadow 0.25s ease;

  &--header {
    background: var(--color-elevated);
    font-weight: 600;
    font-size: 13px;
    color: var(--color-primary);
    letter-spacing: 0.03em;
    border-bottom: 1px solid var(--color-primary);
  }

  &:not(.user-row--header):hover {
    background: rgba(212, 168, 83, 0.04);
    box-shadow: inset 3px 0 0 var(--color-primary);
  }

  &:last-child {
    border-bottom: none;
  }

  &__name {
    font-weight: 500;
    color: var(--color-text);
  }
}

.delete-btn {
  color: var(--color-error) !important;
  font-size: 13px;
  transition: opacity 0.2s ease;

  &:hover {
    opacity: 0.8;
  }
}

.dialog-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.65);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 16px;
  padding: 32px;
  max-width: 440px;
  width: 90%;
  box-shadow: 0 24px 48px rgba(0, 0, 0, 0.5), var(--shadow-gold-glow);

  &__header {
    margin-bottom: 24px;

    h3 {
      font-family: var(--font-serif-cn), serif;
      font-size: 20px;
      color: var(--color-text);
      margin: 0 0 10px;
    }
  }

  &__header-line {
    height: 2px;
    width: 40px;
    background: var(--gradient-primary);
    border-radius: 1px;

    &--danger {
      background: linear-gradient(90deg, var(--color-error), transparent);
    }
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

    option {
      background: var(--color-surface);
      color: var(--color-text);
    }
  }

  .form-label {
    color: var(--color-muted);
    font-size: 13px;
  }

  &__body-text {
    color: var(--color-muted);
    font-size: 14px;
    margin: 0 0 24px;
    line-height: 1.6;

    strong {
      color: var(--color-text);
    }
  }

  &__actions {
    display: flex;
    gap: 12px;
    justify-content: flex-end;
    margin-top: 24px;
  }

  &--danger {
    border-color: rgba(239, 68, 68, 0.2);
  }
}

.confirm-btn {
  background: var(--gradient-primary);
  border: none;
  color: var(--color-bg);
  font-weight: 600;

  &:hover:not(:disabled) {
    box-shadow: var(--shadow-gold-glow);
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}

.danger-confirm-btn {
  background: var(--color-error);
  border: none;
  color: #fff;
  font-weight: 600;
  transition: opacity 0.2s ease, box-shadow 0.2s ease;

  &:hover {
    opacity: 0.9;
    box-shadow: 0 0 16px rgba(239, 68, 68, 0.3);
  }
}
</style>
