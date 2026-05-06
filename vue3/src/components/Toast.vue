<template>
  <Teleport to="body">
    <div class="toast-container">
      <TransitionGroup name="toast-anim">
        <div
          v-for="toast in toasts"
          :key="toast.id"
          class="toast"
          :class="`toast--${toast.type}`"
        >
          <span class="toast__icon">{{ iconMap[toast.type] }}</span>
          <span class="toast__message">{{ toast.message }}</span>
        </div>
      </TransitionGroup>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, onBeforeUnmount } from 'vue'
import eventBus from '@/utils/eventBus'

const toasts = ref([])

const iconMap = {
  success: '✓',
  error: '✕',
  warning: '⚠',
  info: 'ℹ'
}

let toastId = 0

function addToast(message, type = 'info', duration = 3000) {
  const id = ++toastId
  toasts.value.push({ id, message, type })

  setTimeout(() => {
    toasts.value = toasts.value.filter(t => t.id !== id)
  }, duration)
}

function handleToast({ message, type, duration }) {
  addToast(message, type, duration)
}

eventBus.on('toast', handleToast)

onBeforeUnmount(() => {
  eventBus.off('toast', handleToast)
})

defineExpose({ addToast })
</script>

<script>
// Composable for using toast from anywhere
import eventBus from '@/utils/eventBus'

export function useToast() {
  return {
    success(message, duration) {
      eventBus.emit('toast', { message, type: 'success', duration })
    },
    error(message, duration) {
      eventBus.emit('toast', { message, type: 'error', duration })
    },
    warning(message, duration) {
      eventBus.emit('toast', { message, type: 'warning', duration })
    },
    info(message, duration) {
      eventBus.emit('toast', { message, type: 'info', duration })
    }
  }
}
</script>

<style scoped lang="scss">
.toast-container {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10000;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  pointer-events: none;
}

.toast {
  padding: 12px 20px 12px 16px;
  border-radius: var(--radius);
  font-size: 14px;
  font-weight: 500;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5);
  pointer-events: auto;
  display: flex;
  align-items: center;
  gap: 10px;
  white-space: nowrap;
  background: var(--color-elevated);
  color: var(--color-text);
  border: 1px solid var(--color-border);
  border-left: 3px solid var(--color-primary);

  &--success {
    border-left-color: var(--color-success);
    .toast__icon { color: var(--color-success); }
  }

  &--error {
    border-left-color: var(--color-error);
    .toast__icon { color: var(--color-error); }
  }

  &--warning {
    border-left-color: var(--color-warning);
    .toast__icon { color: var(--color-warning); }
  }

  &--info {
    border-left-color: var(--color-primary);
    .toast__icon { color: var(--color-primary); }
  }
}

.toast__icon {
  font-size: 16px;
  font-weight: 700;
}

.toast-anim-enter-active {
  animation: toastSlideIn 0.35s ease;
}

.toast-anim-leave-active {
  animation: toastSlideOut 0.3s ease forwards;
}
</style>
