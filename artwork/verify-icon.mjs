// Пиксельная проверка app-icon-512.png против геометрии generate-icon.mjs.
// Запускать после node generate-icon.mjs.

import { readFileSync } from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { PNG } from 'pngjs'

const ROOT = path.dirname(fileURLToPath(import.meta.url))
const png = PNG.sync.read(readFileSync(path.join(ROOT, 'png', 'app-icon-512.png')))

const S = png.width / 1024 // 0.5 для 512

const at = (x, y) => {
  const i = (Math.round(y * S) * png.width + Math.round(x * S)) << 2
  return [png.data[i], png.data[i + 1], png.data[i + 2], png.data[i + 3]]
}
const hex = ([r, g, b]) => '#' + [r, g, b].map((v) => v.toString(16).padStart(2, '0')).join('')

let failed = 0
const expect = (name, x, y, want, alpha = 255, tol = 8) => {
  const got = at(x, y)
  const ok =
    got[3] === alpha &&
    Math.abs(got[0] - parseInt(want.slice(1, 3), 16)) <= tol &&
    Math.abs(got[1] - parseInt(want.slice(3, 5), 16)) <= tol &&
    Math.abs(got[2] - parseInt(want.slice(5, 7), 16)) <= tol
  if (!ok) failed++
  console.log(`${ok ? 'OK ' : 'FAIL'} ${name}: (${x},${y}) -> ${hex(got)} a=${got[3]}, ожидалось ~${want}`)
}

// подчёркивания строк (новая геометрия вместо чипов)
expect('underline-1 (зелёная)', 448, 434, '#22C55E')
expect('underline-2 (оранжевая)', 448, 662, '#F97316')
// цифры: сегмент F шестёрки (верхняя строка) и сегмент C тройки (нижняя)
expect('цифра row1', 247, 300, '#58EE8C')
expect('цифра row2', 655, 620, '#58EE8C')
// на месте старого чипа (x 176..206) — теперь фон карточки
expect('карточка слева от цифр', 191, 346, '#081711')
// мяч: радиальный градиент, поэтому допуск шире
expect('мяч (центр-право)', 900, 860, '#DCF065', 255, 20)
expect('шов мяча', 874, 818, '#1F3D28')
// фон и прозрачные углы скругления
expect('фон вне карточки', 96, 96, '#1B4D31')
const corner = at(4, 4)
console.log(`${corner[3] === 0 ? 'OK ' : 'FAIL'} угол прозрачный: alpha=${corner[3]}`)
if (corner[3] !== 0) failed++

process.exit(failed ? 1 : 0)
