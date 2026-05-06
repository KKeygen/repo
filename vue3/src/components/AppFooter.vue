<template>
  <footer class="app-footer">
    <div class="app-footer__accent-line"></div>
    <div class="app-footer__inner container">
      <div class="app-footer__links">
        <a href="javascript:void(0)">关于我们</a>
        <span class="divider">·</span>
        <a href="javascript:void(0)">联系方式</a>
        <span class="divider">·</span>
        <a href="javascript:void(0)">服务条款</a>
      </div>
      <p class="app-footer__copyright">© 2025 万花筒票务</p>
    </div>
  </footer>
</template>

<script setup>
</script>

<style scoped lang="scss">
.app-footer {
  background: var(--color-surface);
  margin-top: auto;
  padding: 0 0 32px;
}

.app-footer__accent-line {
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--color-primary) 30%, var(--color-gold) 50%, var(--color-primary) 70%, transparent);
  opacity: 0.3;
  margin-bottom: 28px;
}

.app-footer__inner {
  text-align: center;
}

.app-footer__links {
  margin-bottom: 10px;

  a {
    color: var(--color-muted);
    font-size: 13px;
    font-family: var(--font-serif-cn);
    text-decoration: none;
    transition: color var(--transition);
    letter-spacing: 0.02em;

    &:hover {
      color: var(--color-primary);
    }
  }

  .divider {
    margin: 0 14px;
    color: rgba(212, 168, 83, 0.25);
    font-size: 10px;
  }
}

.app-footer__copyright {
  font-size: 12px;
  color: var(--color-muted);
  font-family: var(--font-serif-cn);
  letter-spacing: 0.04em;
  opacity: 0.7;
}
</style>
