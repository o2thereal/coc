package com.example.cochelper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CoordinateCodecTest {
    @Test
    fun encodeDecodeRoundTrip() {
        val src = listOf(
            GameButtonCoordinate("攻击按钮", 100, 200),
            GameButtonCoordinate("训练按钮", 300, 450)
        )

        val raw = CoordinateCodec.encode(src)
        val decoded = CoordinateCodec.decode(raw)

        assertEquals(src, decoded)
    }

    @Test
    fun decodeIgnoresBadLines() {
        val raw = "攻击按钮|100|200\n坏数据\n升级|A|20\n实验室|30|40"

        val decoded = CoordinateCodec.decode(raw)

        assertEquals(2, decoded.size)
        assertTrue(decoded.any { it.name == "攻击按钮" && it.x == 100 && it.y == 200 })
        assertTrue(decoded.any { it.name == "实验室" && it.x == 30 && it.y == 40 })
    }
}
