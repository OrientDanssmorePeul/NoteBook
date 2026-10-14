package com.example.notebook.data.model

import android.graphics.Color
import android.util.Log

data class Note(
    val id: String? = null,
    val title: String = "",
    val content: String = "",
    val colour: PastelPalette = PastelPalette.WHITE,
    val createdAt: Long = System.currentTimeMillis(),
    val editedAt: Long? = null,
    val deletedAt: Long? = null,
    val inTrash: Boolean = false
) {
    fun toHashMap(): Map<String, Any?> {
        return mapOf(
            "title" to title,
            "content" to content,
            "colour" to colour.name,
            "createdAt" to createdAt,
            "editedAt" to editedAt,
            "deletedAt" to deletedAt,
            "inTrash" to inTrash
        )
    }

    companion object {
        fun fromHashMap(map: Map<String, Any>, id: String): Note {
            Log.d("IDIIDID", "${map}")
            return Note(
                id,
                map["title"].toString(),
                map["content"].toString(),
                PastelPalette.valueOf(map["colour"].toString()),
                map["createdAt"] as Long,
                map["editedAt"] as? Long,
                map["deletedAt"] as? Long,
                map["inTrash"] as? Boolean ?: false
            )
        }
    }
}
enum class PastelPalette(val hex: String) {
    LIGHT_BLUE("#CDE8F6"),
    MINT_GREEN("#CFEEDD"),
    PASTEL_YELLOW("#FBEFB5"),
    PEACH_ORANGE("#FAD9C4"),
    BABY_PINK("#F8D0D8"),
    LAVENDER_PURPLE("#DDD8F7"),
    WHITE("#FFFFFF");
    val colorInt: Int
        get() = Color.parseColor(hex)
}
