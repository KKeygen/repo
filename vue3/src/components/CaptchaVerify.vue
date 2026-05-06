<template>
  <div class="captcha-overlay" @click.self="$emit('close')">
    <div class="captcha-modal">
      <div class="captcha-header">
        <h3>安全验证</h3>
        <button class="captcha-close" @click="$emit('close')">✕</button>
      </div>

      <div class="captcha-body">
        <div class="captcha-image">
          <div class="captcha-bg"></div>
          <div
            class="captcha-piece"
            :style="{ left: pieceLeft + 'px' }"
          ></div>
        </div>

        <div class="captcha-slider">
          <div class="captcha-slider__track">
            <div class="captcha-slider__fill" :style="{ width: sliderProgress + '%' }"></div>
          </div>
          <div
            class="captcha-slider__thumb"
            :style="{ left: sliderProgress + '%' }"
            @mousedown="startDrag"
            @touchstart="startDrag"
          >
            →
          </div>
          <span v-if="sliderProgress === 0" class="captcha-slider__hint">
            向右拖动滑块完成验证
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const emit = defineEmits(['success', 'close'])

const sliderProgress = ref(0)
const pieceLeft = ref(10)
const isDragging = ref(false)
const startX = ref(0)
const trackWidth = 280

function startDrag(e) {
  isDragging.value = true
  startX.value = e.clientX || e.touches?.[0]?.clientX || 0

  const onMove = (ev) => {
    if (!isDragging.value) return
    const clientX = ev.clientX || ev.touches?.[0]?.clientX || 0
    const diff = clientX - startX.value
    const progress = Math.max(0, Math.min(100, (diff / trackWidth) * 100))
    sliderProgress.value = progress
    pieceLeft.value = 10 + (progress / 100) * (trackWidth - 60)
  }

  const onEnd = () => {
    isDragging.value = false
    document.removeEventListener('mousemove', onMove)
    document.removeEventListener('mouseup', onEnd)
    document.removeEventListener('touchmove', onMove)
    document.removeEventListener('touchend', onEnd)

    // If user dragged past 80%, consider it successful
    if (sliderProgress.value > 80) {
      emit('success', { token: 'captcha_verified_' + Date.now() })
    } else {
      // Reset
      sliderProgress.value = 0
      pieceLeft.value = 10
    }
  }

  document.addEventListener('mousemove', onMove)
  document.addEventListener('mouseup', onEnd)
  document.addEventListener('touchmove', onMove)
  document.addEventListener('touchend', onEnd)
}
</script>

<style scoped lang="scss">
.captcha-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  animation: fadeInUp 0.25s ease;
}

.captcha-modal {
  background: var(--color-elevated);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: 24px;
  width: 340px;
  box-shadow: 0 24px 80px rgba(0, 0, 0, 0.5);
}

.captcha-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;

  h3 {
    font-family: var(--font-display);
    font-size: 16px;
    font-weight: 700;
    color: var(--color-text);
  }
}

.captcha-close {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-surface);
  color: var(--color-muted);
  font-size: 14px;
  cursor: pointer;
  transition: all var(--transition);
  border: 1px solid transparent;

  &:hover {
    border-color: var(--color-border);
    color: var(--color-text);
  }
}

.captcha-body {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.captcha-image {
  position: relative;
  width: 100%;
  height: 160px;
  background: linear-gradient(135deg, #1A1A1E, #252528);
  border-radius: var(--radius);
  overflow: hidden;
  border: 1px solid var(--color-border);
}

.captcha-bg {
  position: absolute;
  top: 50%;
  right: 40px;
  transform: translateY(-50%);
  width: 44px;
  height: 44px;
  border-radius: 6px;
  background: rgba(212, 168, 83, 0.15);
  border: 2px dashed rgba(212, 168, 83, 0.35);
}

.captcha-piece {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 44px;
  height: 44px;
  border-radius: 6px;
  background: var(--gradient-primary);
  box-shadow: 0 2px 12px rgba(212, 168, 83, 0.4);
  transition: left 0.05s linear;
}

.captcha-slider {
  position: relative;
  height: 40px;
  display: flex;
  align-items: center;

  &__track {
    position: absolute;
    width: 100%;
    height: 36px;
    background: var(--color-surface);
    border-radius: 18px;
    overflow: hidden;
    border: 1px solid var(--color-border);
  }

  &__fill {
    height: 100%;
    background: rgba(212, 168, 83, 0.1);
    border-radius: 18px;
    transition: width 0.05s linear;
  }

  &__thumb {
    position: absolute;
    width: 44px;
    height: 36px;
    background: var(--gradient-primary);
    border-radius: 18px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #0D0D0F;
    font-size: 16px;
    font-weight: 700;
    cursor: grab;
    user-select: none;
    transform: translateX(-50%);
    box-shadow: 0 2px 12px rgba(212, 168, 83, 0.3);
    z-index: 2;

    &:active { cursor: grabbing; }
  }

  &__hint {
    position: absolute;
    width: 100%;
    text-align: center;
    font-size: 13px;
    color: var(--color-muted);
    pointer-events: none;
    z-index: 1;
  }
}
</style>
