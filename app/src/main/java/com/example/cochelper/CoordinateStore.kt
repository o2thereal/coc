package com.example.cochelper

data class GameButtonCoordinate(
    val name: String,
    val x: Int,
    val y: Int
)

object CoordinateCodec {
    fun encode(items: List<GameButtonCoordinate>): String {
        return items.joinToString(separator = "\n") { "${it.name}|${it.x}|${it.y}" }
    }

    fun decode(raw: String): MutableList<GameButtonCoordinate> {
        if (raw.isBlank()) return mutableListOf()
        return raw
            .lines()
            .mapNotNull { line ->
                val parts = line.split("|")
                if (parts.size != 3) return@mapNotNull null
                val x = parts[1].toIntOrNull() ?: return@mapNotNull null
                val y = parts[2].toIntOrNull() ?: return@mapNotNull null
                GameButtonCoordinate(parts[0], x, y)
            }
            .toMutableList()
    }
}
