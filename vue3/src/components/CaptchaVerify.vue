<template>
  <Teleport to="body">
    <div class="cap-box" @click.self="handleClose">
      <div class="cap-card">
        <div class="cap-top">
          <h3>安全验证</h3>
          <button class="cap-x" @click="handleClose">✕</button>
        </div>

        <div v-if="loading" style="height:200px;display:flex;flex-direction:column;align-items:center;justify-content:center;gap:10px;">
          <div class="spin"></div>
          <span style="color:#999;font-size:13px;">加载验证码...</span>
        </div>

        <div v-else-if="loadError" style="height:200px;display:flex;flex-direction:column;align-items:center;justify-content:center;gap:12px;">
          <span style="color:#999;">验证码加载失败</span>
          <button class="reload-btn" @click="loadCaptcha">重新加载</button>
        </div>

        <div v-else style="position:relative;">
          <!-- Image panel (like damai verify-img-out + verify-img-panel) -->
          <div class="cap-img-wrap" ref="imgWrapRef" :style="{width: imgW + 'px', height: imgH + 'px'}">
            <img :src="bgSrc" style="width:100%;height:100%;display:block;">
          </div>

          <!-- Slider bar area (like damai verify-bar-area) -->
          <div class="cap-bar-area" ref="barAreaRef" :style="{width: imgW + 'px'}">
            <span class="cap-bar-msg">{{ barMsg }}</span>
            <!-- Left bar that fills as you drag (like damai verify-left-bar) -->
            <div class="cap-left-bar" :style="{width: leftBarW + 'px'}">
              <span class="cap-bar-msg" style="color:#fff;">{{ finishMsg }}</span>
              <!-- Movable thumb block (like damai verify-move-block) -->
              <div class="cap-move-block"
                   :style="{left: moveBlockLeft + 'px'}"
                   @mousedown="start"
                   @touchstart.prevent="start">
                <span class="cap-move-icon">{{ verified ? '✓' : '→' }}</span>
                <!-- Puzzle piece window (like damai verify-sub-block) -->
                <div v-if="showPiece" class="cap-sub-block"
                     :style="{
                       width: subW + 'px',
                       height: imgH + 'px',
                       top: '-' + (imgH + 5) + 'px',
                       backgroundSize: imgW + 'px ' + imgH + 'px'
                     }">
                  <img :src="jigSrc" style="width:100%;height:100%;display:block;-webkit-user-drag:none;">
                </div>
              </div>
            </div>
          </div>

          <div style="text-align:center;margin-top:8px;">
            <button class="reload-btn" @click="loadCaptcha">换一张</button>
          </div>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import CryptoJS from 'crypto-js'
import { getCaptcha, verifyCaptcha } from '@/api/captcha'

const emit = defineEmits(['success', 'close'])

const IMG_W = 310; const IMG_H = 155; const BLOCK_W = 50

const loading = ref(true)
const loadError = ref(false)
const verified = ref(false)
const showPiece = ref(false)
const imgW = ref(IMG_W); const imgH = ref(IMG_H)
const subW = ref(47)
const moveBlockLeft = ref(0)
const leftBarW = ref(0)
const barMsg = ref('向右滑动完成验证')
const finishMsg = ref('')

const imgWrapRef = ref(null)
const barAreaRef = ref(null)

let bgSrc = ''; let jigSrc = ''
let secretKey = ''; let backToken = ''
let pieceX = 0
let startLeft = 0
let isMoving = false
let isEnd = false
let startMoveTime = 0

function b64url(b) {
  if (!b) return ''
  return b.startsWith('data:') ? b : 'data:image/png;base64,' + b
}
function aesEnc(w, k) {
  const key = CryptoJS.enc.Utf8.parse(k)
  const src = CryptoJS.enc.Utf8.parse(w)
  return CryptoJS.AES.encrypt(src, key, { mode: CryptoJS.mode.ECB, padding: CryptoJS.pad.Pkcs7 }).toString()
}

async function loadCaptcha() {
  loading.value = true; loadError.value = false; verified.value = false; showPiece.value = false
  moveBlockLeft.value = 0; leftBarW.value = 0; barMsg.value = '向右滑动完成验证'; finishMsg.value = ''
  isEnd = false

  let capData = null
  try {
    const res = await getCaptcha({ captchaType: 'blockPuzzle' })
    capData = (res && res.repData) ? res.repData : (res && res.data ? res.data : res)
    if (capData && capData.repData) capData = capData.repData
  } catch (e) { loadError.value = true; loading.value = false; return }
  if (!capData) { loadError.value = true; loading.value = false; return }

  secretKey = capData.secretKey || ''
  backToken = capData.token || ''
  pieceX = capData.x || 0
  bgSrc = b64url(capData.originalImageBase64 || capData.backgroundImage || '')
  jigSrc = b64url(capData.jigsawImageBase64 || capData.cutoutImage || '')

  loading.value = false
  await nextTick()
  // Measure actual rendered size (like damai resetSize)
  if (imgWrapRef.value) {
    const rect = imgWrapRef.value.getBoundingClientRect()
    imgW.value = rect.width; imgH.value = rect.height
  }
  subW.value = Math.floor(imgW.value * BLOCK_W / IMG_W)
  showPiece.value = true
}

// ===== Drag handlers (like damai start/move/end) =====
function start(e) {
  if (isEnd) return
  e = e || window.event
  const barArea = barAreaRef.value
  if (!barArea) return
  const barRect = barArea.getBoundingClientRect()
  const x = e.clientX || (e.touches && e.touches[0].pageX) || 0
  startLeft = Math.floor(x - barRect.left)
  startMoveTime = +new Date()
  isMoving = true
  barMsg.value = ''
}

function move(e) {
  if (!isMoving || isEnd) return
  e = e || window.event
  const barArea = barAreaRef.value
  if (!barArea) return
  const barRect = barArea.getBoundingClientRect()
  const x = e.clientX || (e.touches && e.touches[0].pageX) || 0
  let ml = x - barRect.left
  const maxLeft = barArea.offsetWidth - parseInt(BLOCK_W / 2) - 2
  if (ml > maxLeft) ml = maxLeft
  if (ml <= 0) ml = parseInt(BLOCK_W / 2)

  moveBlockLeft.value = ml - startLeft
  leftBarW.value = ml - startLeft
}

function end() {
  if (!isMoving || isEnd) return
  isMoving = false
  const endTime = +new Date()

  let dist = (moveBlockLeft.value || 0)
  // Scale to 310px reference (like damai: * 310 / imgWidth)
  dist = dist * IMG_W / imgW.value

  const rawPj = JSON.stringify({ x: dist, y: 5.0 })
  const encPj = secretKey ? aesEnc(rawPj, secretKey) : rawPj

  verifyCaptcha({ captchaType: 'blockPuzzle', token: backToken, pointJson: encPj }).then(res => {
    if (res && res.repCode === '0000') {
      isEnd = true; verified.value = true
      finishMsg.value = ((endTime - startMoveTime) / 1000).toFixed(2) + 's验证成功'
      const cv = secretKey ? aesEnc(backToken + '---' + rawPj, secretKey) : backToken + '---' + rawPj
      setTimeout(() => {
        emit('success', { captchaVerification: cv, token: backToken })
        handleClose()
      }, 1000)
    } else {
      moveBlockLeft.value = 0; leftBarW.value = 0; barMsg.value = '向右滑动完成验证'
      loadCaptcha()
    }
  }).catch(() => {
    moveBlockLeft.value = 0; leftBarW.value = 0; barMsg.value = '向右滑动完成验证'
    loadCaptcha()
  })
}

// Global listeners (like damai)
function globalMove(e) { move(e) }
function globalEnd() { end() }

onMounted(() => {
  window.addEventListener('mousemove', globalMove)
  window.addEventListener('mouseup', globalEnd)
  window.addEventListener('touchmove', globalMove)
  window.addEventListener('touchend', globalEnd)
  loadCaptcha()
})
onBeforeUnmount(() => {
  window.removeEventListener('mousemove', globalMove)
  window.removeEventListener('mouseup', globalEnd)
  window.removeEventListener('touchmove', globalMove)
  window.removeEventListener('touchend', globalEnd)
})
</script>

<style scoped>
.cap-box { position:fixed;inset:0;background:rgba(0,0,0,0.7);display:flex;align-items:center;justify-content:center;z-index:9999; }
.cap-card { background:#1e1e22;border:1px solid #333;border-radius:12px;padding:24px;width:360px; }
.cap-top { display:flex;align-items:center;justify-content:space-between;margin-bottom:16px; }
.cap-top h3 { color:#e0e0e0;font-size:16px;font-weight:700;margin:0; }
.cap-x { width:28px;height:28px;border-radius:50%;border:1px solid transparent;background:#2a2a2e;color:#999;font-size:14px;cursor:pointer; }
.cap-x:hover { border-color:#444;color:#fff; }

/* Image panel */
.cap-img-wrap { position:relative;background:#111;border-radius:8px;overflow:hidden;border:1px solid #333;margin:0 auto; }

/* Slider bar area (like damai verify-bar-area) */
.cap-bar-area { position:relative;height:40px;line-height:40px;margin-top:5px; }
.cap-bar-msg { position:absolute;width:100%;text-align:center;color:#888;font-size:13px;z-index:1;pointer-events:none; }

/* Left bar fill (like damai verify-left-bar) */
.cap-left-bar { position:absolute;height:100%;background:rgba(212,168,83,0.2);border:1px solid rgba(212,168,83,0.5);border-radius:4px;overflow:visible; }

/* Movable thumb block (like damai verify-move-block) */
.cap-move-block { position:absolute;top:0;width:40px;height:40px;background:linear-gradient(135deg,#d4a853,#f0d060);border-radius:4px;display:flex;align-items:center;justify-content:center;cursor:grab;z-index:10; }
.cap-move-block:active { cursor:grabbing; }
.cap-move-icon { color:#0d0d0f;font-size:16px;font-weight:700;user-select:none; }

/* Puzzle piece window (like damai verify-sub-block) */
.cap-sub-block { position:absolute;text-align:center;z-index:3;overflow:hidden;pointer-events:none; }
.cap-sub-block img { width:100%;height:100%;display:block;-webkit-user-drag:none; }

.spin { width:32px;height:32px;border:3px solid #333;border-top-color:#d4a853;border-radius:50%;animation:spin 0.8s linear infinite; }
@keyframes spin { to { transform:rotate(360deg); } }
.reload-btn { color:#999;background:transparent;border:1px solid #333;padding:6px 16px;border-radius:6px;cursor:pointer;font-size:13px; }
.reload-btn:hover { border-color:#555;color:#ddd; }
</style>
