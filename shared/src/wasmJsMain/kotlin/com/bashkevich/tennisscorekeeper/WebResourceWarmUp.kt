@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package com.bashkevich.tennisscorekeeper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.jetbrains.compose.resources.getString
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.tournaments
import kotlin.js.JsAny

// Обход веб-бага Compose Multiplatform 1.12.x (components-resources):
// на вебе строковые ресурсы грузятся асинхронно, а загрузчик молча глотает любые
// ошибки/отмены загрузки (stringResource остаётся ""). При старте приложения локаль
// меняется с дефолтной (en) на сохранённую в настройках — key(localeTag) пересоздаёт
// всё дерево до завершения первого fetch .cvr, и отменённая загрузка подвешивает
// последующие запросы тех же ресурсов (воспроизведено на CMP 1.12.1; upstream:
// CMP-9369 / CMP-10137).
//
// Гейт прогревает кэш браузера (Cache API) обоими файлами строк ДО композиции App:
// после этого подписки stringResource резолвятся из кэша за микрозадачу и не
// попадают в окно гонки. Прогрев best-effort: при недоступности файлов App стартует
// как раньше (поведение не хуже статус-кво).
@Composable
fun WarmedAppGate(content: @Composable () -> Unit) {
    var warmed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        warmUpStringResources()
        warmed = true
    }
    if (warmed) {
        content()
    }
}

// Прогрев через публичный suspend getString: его окружение (getSystemEnvironment)
// читает navigator.language при каждом вызове, поэтому временная подмена языка
// позволяет прогреть оба файла локалей одним и тем же проверенным кодом загрузки.
private suspend fun warmUpStringResources() {
    installWarmupLanguagePatch()
    try {
        setWarmupLangEn()
        try {
            getString(Res.string.tournaments)
        } catch (_: Exception) {
            // файл недоступен (оффлайн-старт и т.п.) — не блокируем запуск UI
        }
        setWarmupLangRu()
        try {
            getString(Res.string.tournaments)
        } catch (_: Exception) {
            // аналогично
        }
    } finally {
        clearWarmupLang()
    }
}

// js() в Kotlin/Wasm не может захватывать параметры — значения живут в window.__warmupLang.
private fun setWarmupLangEn(): JsAny =
    js("window.__warmupLang = 'en'")

private fun setWarmupLangRu(): JsAny =
    js("window.__warmupLang = 'ru'")

private fun clearWarmupLang(): JsAny =
    js("delete window.__warmupLang")

private fun installWarmupLanguagePatch(): JsAny =
    js(
        """
        (!window.__warmupLangInstalled) && (function () {
            window.__warmupLangInstalled = true;
            var orig = Object.getOwnPropertyDescriptor(Navigator.prototype, 'language');
            Object.defineProperty(Navigator.prototype, 'language', {
                get: function () {
                    return window.__warmupLang || (orig ? orig.get.apply(this) : 'en');
                },
                configurable: true
            });
            return true;
        })()
        """
    )
