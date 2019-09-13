package com.ricknout.rugbyranker.core.util

import androidx.emoji.text.EmojiCompat

object EmojiUtils {

    fun processEmoji(charSequence: CharSequence): CharSequence = try {
        EmojiCompat.get().process(charSequence)
    } catch (_: Exception) {
        charSequence
    }
}
