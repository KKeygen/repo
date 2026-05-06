<template>
  <div class="program-card" @click="goDetail">
    <div class="program-card__image">
      <img :src="program.itemPicture" :alt="program.title" loading="lazy" />
      <div class="program-card__overlay"></div>
      <div v-if="program.minPrice" class="program-card__price">
        <span class="price-symbol">¥</span>{{ program.minPrice }}<span class="price-suffix">起</span>
      </div>
      <div v-if="program.areaName" class="program-card__tag">
        {{ program.areaName }}
      </div>
    </div>
    <div class="program-card__body">
      <h3 class="program-card__title">{{ program.title }}</h3>
      <p v-if="program.place" class="program-card__venue">
        {{ program.place }}
      </p>
      <p v-if="program.showTime" class="program-card__time">
        {{ program.showTime }}
      </p>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  program: {
    type: Object,
    required: true
  }
})

const router = useRouter()

function goDetail() {
  router.push(`/contentDetail/${props.program.id}`)
}
</script>

<style scoped lang="scss">
.program-card {
  background: var(--color-card-bg);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
  overflow: hidden;
  cursor: pointer;
  transition: transform var(--transition), box-shadow var(--transition), border-color var(--transition);

  &:hover {
    transform: translateY(-6px);
    box-shadow: var(--shadow-card-hover);
    border-color: rgba(212, 168, 83, 0.3);
  }
}

.program-card__image {
  position: relative;
  width: 100%;
  padding-top: 133%;
  overflow: hidden;
  background: var(--color-surface);

  img {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.4s ease;
  }

  .program-card:hover & img {
    transform: scale(1.05);
  }
}

.program-card__overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 50%;
  background: linear-gradient(to top, rgba(13, 13, 15, 0.8) 0%, transparent 100%);
  pointer-events: none;
}

.program-card__price {
  position: absolute;
  bottom: 10px;
  right: 10px;
  color: var(--color-gold);
  font-family: var(--font-body);
  font-size: 16px;
  font-weight: 700;
  z-index: 1;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.6);
}

.price-symbol {
  font-size: 12px;
  font-weight: 500;
  margin-right: 1px;
}

.price-suffix {
  font-size: 11px;
  font-weight: 400;
  margin-left: 2px;
  opacity: 0.8;
}

.program-card__tag {
  position: absolute;
  top: 10px;
  left: 10px;
  background: rgba(198, 40, 40, 0.85);
  color: var(--color-text);
  font-size: 10px;
  font-weight: 500;
  padding: 3px 8px;
  border-radius: 3px;
  letter-spacing: 0.03em;
  z-index: 1;
}

.program-card__body {
  padding: 14px 14px 16px;
}

.program-card__title {
  font-family: var(--font-serif-cn);
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text);
  line-height: 1.5;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.program-card__venue,
.program-card__time {
  font-size: 12px;
  color: var(--color-muted);
  margin-bottom: 4px;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
