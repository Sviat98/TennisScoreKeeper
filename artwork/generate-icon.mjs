// Генератор иконки приложения TennisScoreKeeper.
// Мотив: фрагмент табло со счётом (без фамилий) + теннисный мяч.
// Из одной геометрической модели собирает:
//   artwork/app-icon.svg        — основная иконка (скруглённый квадрат)
//   artwork/app-icon-round.svg  — круглый вариант (ic_launcher_round)
//   artwork/png/*.png           — растеризация через resvg
//   artwork/app.ico             — мультиразмерный ICO (PNG-entries)
// Слои background/foreground разделены: foreground без изменений портируется
// в adaptive-icon VectorDrawable (группа scale 66/108), фон — градиент.

import { Resvg } from '@resvg/resvg-js'
import { mkdirSync, writeFileSync } from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const ROOT = path.dirname(fileURLToPath(import.meta.url))

const C = {
  bgTop: '#1B4D31',
  bgMid: '#0E2417',
  bgBot: '#060D08',
  card: '#081711',
  cardStroke: '#2C5C3E',
  chip1: '#22C55E',
  chip2: '#F97316',
  serve: '#EAFBEF',
  digit: '#58EE8C',
  ballLight: '#EDFF9B',
  ballMid: '#DCF065',
  ballDark: '#C9E243',
  ballFlat: '#D6EC5C', // плоская замена градиенту в VectorDrawable
  seam: '#1F3D28',
}

// ---------- геометрия ----------

// 7-сегментные цифры (стиль табло), ячейка 80x150, толщина сегмента 20, скос 10
const DW = 80, DH = 150, T = 20, B = 10, H = T / 2, BV = B

const segH = (x0, x1, cy) =>
  [[x0, cy - H], [x1 - BV, cy - H], [x1, cy], [x1 - BV, cy + H], [x0, cy + H], [x0 + BV, cy]]
// вертикальный сегмент: выемка сверху (y0) и остриё снизу (y1)
const segVTopNotch = (cx, y0, y1) =>
  [[cx - H, y0], [cx, y0 + BV], [cx + H, y0], [cx + H, y1 - BV], [cx, y1], [cx - H, y1 - BV]]
// вертикальный сегмент: остриё сверху (y0) и остриё снизу (y1)
const segVPoints = (cx, y0, y1) =>
  [[cx - H, y0 + BV], [cx, y0], [cx + H, y0 + BV], [cx + H, y1 - BV], [cx, y1], [cx - H, y1 - BV]]

const SEGMENTS = {
  A: { pts: segH(T / 2, DW - T / 2, T / 2) },
  G: { pts: segH(T / 2, DW - T / 2, DH / 2) },
  D: { pts: segH(T / 2, DW - T / 2, DH - T / 2) },
  F: { pts: segVTopNotch(T / 2, T / 2, DH / 2) },
  B: { pts: segVTopNotch(DW - T / 2, T / 2, DH / 2) },
  E: { pts: segVPoints(T / 2, DH / 2, DH - T / 2) },
  C: { pts: segVPoints(DW - T / 2, DH / 2, DH - T / 2) },
}

const DIGIT_MAP = {
  0: 'ABCDEF', 1: 'BC', 2: 'ABGED', 3: 'ABGCD', 4: 'FGBC',
  5: 'AFGCD', 6: 'AFGECD', 7: 'ABC', 8: 'ABCDEFG', 9: 'ABCDFG',
}

const CARD = { x: 128, y: 232, w: 640, h: 456, r: 40 }
const ROW1_Y = 263, ROW2_Y = 491
const ROW1 = [['6', 232], ['4', 372], ['3', 480], ['0', 584]]
const ROW2 = [['3', 232], ['5', 372], ['4', 480], ['0', 584]]
// цветные подчёркивания строк — цвета игроков (заменяют вертикальные чипы,
// которые читались как лишняя цифра «1» перед счётом)
const UL = { x: 232, w: 432, h: 14, r: 7 }
const UL1_Y = 427, UL2_Y = 655
const BALL = { cx: 830, cy: 818, r: 152 }

// швы мяча: две кубические кривые от полюса к полюсу, выпуклые к центру
const seamPath = (side) => {
  const { cx, cy, r } = BALL
  const ex = side === 'L' ? cx - r * 0.5 : cx + r * 0.5
  const ctl = side === 'L' ? cx + r * 0.55 : cx - r * 0.55
  return `M${round(ex)},${round(cy - r * 0.866)} C${round(ctl)},${round(cy - r * 0.58)} ${round(ctl)},${round(cy + r * 0.58)} ${round(ex)},${round(cy + r * 0.866)}`
}

// ---------- построители path-d ----------

const round = (n) => Math.round(n)

const polyPath = (pts) => 'M' + pts.map(([x, y]) => `${round(x)},${round(y)}`).join('L') + 'Z'

const roundedRectPath = (x, y, w, h, r) =>
  `M${round(x + r)},${round(y)}H${round(x + w - r)}A${r},${r} 0 0 1 ${round(x + w)},${round(y + r)}` +
  `V${round(y + h - r)}A${r},${r} 0 0 1 ${round(x + w - r)},${round(y + h)}H${round(x + r)}` +
  `A${r},${r} 0 0 1 ${round(x)},${round(y + h - r)}V${round(y + r)}A${r},${r} 0 0 1 ${round(x + r)},${round(y)}Z`

const circlePath = (cx, cy, r) =>
  `M${round(cx - r)},${round(cy)}A${r},${r} 0 0 1 ${round(cx + r)},${round(cy)}A${r},${r} 0 0 1 ${round(cx - r)},${round(cy)}Z`

const digitRowPath = (row, baseY) =>
  row.map(([ch, x]) => {
    const segs = DIGIT_MAP[ch]
    return [...segs].map((s) =>
      polyPath(SEGMENTS[s].pts.map(([px, py]) => [px + x, py + baseY]))
    ).join('')
  }).join('')

// ---------- SVG ----------

const bgGradientDef = (scale) => `
    <linearGradient id="bg" gradientUnits="userSpaceOnUse" x1="0" y1="0" x2="${1024 * scale}" y2="${1024 * scale}">
      <stop offset="0" stop-color="${C.bgTop}"/>
      <stop offset="0.55" stop-color="${C.bgMid}"/>
      <stop offset="1" stop-color="${C.bgBot}"/>
    </linearGradient>
    <radialGradient id="ball" gradientUnits="userSpaceOnUse" cx="${BALL.cx}" cy="${BALL.cy}" r="${BALL.r}" fx="${BALL.cx - 58}" fy="${BALL.cy - 58}">
      <stop offset="0" stop-color="${C.ballLight}"/>
      <stop offset="0.55" stop-color="${C.ballMid}"/>
      <stop offset="1" stop-color="${C.ballDark}"/>
    </radialGradient>`

const foregroundBody = () => `
  <g id="foreground">
    <path d="${roundedRectPath(CARD.x, CARD.y, CARD.w, CARD.h, CARD.r)}" fill="${C.card}" stroke="${C.cardStroke}" stroke-width="3"/>
    <path d="${roundedRectPath(UL.x, UL1_Y, UL.w, UL.h, UL.r)}" fill="${C.chip1}"/>
    <path d="${roundedRectPath(UL.x, UL2_Y, UL.w, UL.h, UL.r)}" fill="${C.chip2}"/>
    <path d="${digitRowPath(ROW1, ROW1_Y)}" fill="${C.digit}"/>
    <path d="${digitRowPath(ROW2, ROW2_Y)}" fill="${C.digit}"/>
    <path d="${circlePath(BALL.cx, BALL.cy, BALL.r)}" fill="url(#ball)"/>
    <path d="${seamPath('L')}" fill="none" stroke="${C.seam}" stroke-width="14" stroke-linecap="round"/>
    <path d="${seamPath('R')}" fill="none" stroke="${C.seam}" stroke-width="14" stroke-linecap="round"/>
  </g>`

const svgSquircle = `<?xml version="1.0" encoding="UTF-8"?>
<svg xmlns="http://www.w3.org/2000/svg" width="1024" height="1024" viewBox="0 0 1024 1024">
  <defs>${bgGradientDef(1)}</defs>
  <g id="background">
    <path d="${roundedRectPath(0, 0, 1024, 1024, 224)}" fill="url(#bg)"/>
  </g>${foregroundBody()}
</svg>
`

const svgRound = `<?xml version="1.0" encoding="UTF-8"?>
<svg xmlns="http://www.w3.org/2000/svg" width="1024" height="1024" viewBox="0 0 1024 1024">
  <defs>${bgGradientDef(1)}</defs>
  <g id="background">
    <path d="${circlePath(512, 512, 512)}" fill="url(#bg)"/>
  </g>${foregroundBody()}
</svg>
`

// ---------- VectorDrawable (adaptive-icon foreground, 108x108, safe zone 66) ----------

const vdForeground = `<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <group
        android:scaleX="0.0644531"
        android:scaleY="0.0644531"
        android:translateX="21"
        android:translateY="21">
        <path
            android:fillColor="${C.card}"
            android:strokeColor="${C.cardStroke}"
            android:strokeWidth="3"
            android:pathData="${roundedRectPath(CARD.x, CARD.y, CARD.w, CARD.h, CARD.r)}" />
        <path
            android:fillColor="${C.chip1}"
            android:pathData="${roundedRectPath(UL.x, UL1_Y, UL.w, UL.h, UL.r)}" />
        <path
            android:fillColor="${C.chip2}"
            android:pathData="${roundedRectPath(UL.x, UL2_Y, UL.w, UL.h, UL.r)}" />
        <path
            android:fillColor="${C.digit}"
            android:pathData="${digitRowPath(ROW1, ROW1_Y)}" />
        <path
            android:fillColor="${C.digit}"
            android:pathData="${digitRowPath(ROW2, ROW2_Y)}" />
        <path
            android:fillColor="${C.ballFlat}"
            android:pathData="${circlePath(BALL.cx, BALL.cy, BALL.r)}" />
        <path
            android:strokeColor="${C.seam}"
            android:strokeWidth="14"
            android:strokeLineCap="round"
            android:pathData="${seamPath('L')}" />
        <path
            android:strokeColor="${C.seam}"
            android:strokeWidth="14"
            android:strokeLineCap="round"
            android:pathData="${seamPath('R')}" />
    </group>
</vector>
`

// ---------- ICO (PNG-entries: 16..256) ----------

const makeIco = (entries) => {
  const header = Buffer.alloc(6)
  header.writeUInt16LE(0, 0)
  header.writeUInt16LE(1, 2)
  header.writeUInt16LE(entries.length, 4)
  let offset = 6 + 16 * entries.length
  const dir = entries.map(({ size, buf }) => {
    const e = Buffer.alloc(16)
    e.writeUInt8(size >= 256 ? 0 : size, 0)
    e.writeUInt8(size >= 256 ? 0 : size, 1)
    e.writeUInt8(0, 2)
    e.writeUInt8(0, 3)
    e.writeUInt16LE(1, 4)
    e.writeUInt16LE(32, 6)
    e.writeUInt32LE(buf.length, 8)
    e.writeUInt32LE(offset, 12)
    offset += buf.length
    return e
  })
  return Buffer.concat([header, ...dir, ...entries.map((e) => e.buf)])
}

// ---------- сборка ----------

const renderPng = (svg, size) =>
  new Resvg(svg, { fitTo: { mode: 'width', value: size } }).render().asPng()

mkdirSync(path.join(ROOT, 'png'), { recursive: true })
writeFileSync(path.join(ROOT, 'app-icon.svg'), svgSquircle)
writeFileSync(path.join(ROOT, 'app-icon-round.svg'), svgRound)
writeFileSync(path.join(ROOT, 'app-icon-adaptive-foreground.xml'), vdForeground)

const pngSizes = [512, 256, 192, 180, 144, 128, 96, 72, 64, 48, 32, 16]
for (const size of pngSizes) {
  writeFileSync(path.join(ROOT, 'png', `app-icon-${size}.png`), renderPng(svgSquircle, size))
}
for (const size of [192, 144, 96, 72, 48]) {
  writeFileSync(path.join(ROOT, 'png', `round-${size}.png`), renderPng(svgRound, size))
}

const icoEntries = [256, 128, 64, 48, 32, 16].map((size) => ({
  size,
  buf: renderPng(svgSquircle, size),
}))
writeFileSync(path.join(ROOT, 'app.ico'), makeIco(icoEntries))

console.log('OK: app-icon.svg, app-icon-round.svg, adaptive-foreground.xml, png/*, app.ico')
