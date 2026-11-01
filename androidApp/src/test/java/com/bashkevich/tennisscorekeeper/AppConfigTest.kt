package com.bashkevich.tennisscorekeeper

import org.junit.Assert.assertEquals
import org.junit.Test

// Импортировать типы ktor (URLProtocol) нельзя: implementation-зависимости shared
// не попадают в compile-classpath тестов androidApp, поэтому протоколы сверяем по имени
class AppConfigTest {

    @Test
    fun httpsUrl_derivesWssProtocol_andDefaultPort() {
        val config = AppConfig(
            baseUrlFrontend = "https://tennisscorekeeper.tech",
            baseUrlBackend = "https://api.tennisscorekeeper.tech",
        )

        assertEquals("https", config.backendProtocol.name)
        assertEquals("wss", config.backendWsProtocol.name)
        assertEquals("api.tennisscorekeeper.tech", config.backendHost)
        assertEquals(443, config.backendPort)
    }

    @Test
    fun httpUrl_derivesWsProtocol_andExplicitPort() {
        val config = AppConfig(
            baseUrlFrontend = "http://localhost:8080",
            baseUrlBackend = "http://10.0.2.2:8080",
        )

        assertEquals("http", config.backendProtocol.name)
        assertEquals("ws", config.backendWsProtocol.name)
        assertEquals("10.0.2.2", config.backendHost)
        assertEquals(8080, config.backendPort)
    }

    @Test
    fun urls_arePassedThroughAsRawStrings() {
        val config = AppConfig(
            baseUrlFrontend = "https://tennisscorekeeper.tech",
            baseUrlBackend = "http://10.0.2.2:8080",
        )

        assertEquals("https://tennisscorekeeper.tech", config.baseUrlFrontend)
        assertEquals("http://10.0.2.2:8080", config.baseUrlBackend)
    }
}
