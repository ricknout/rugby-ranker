package com.ricknout.rugbyranker.core.util

import org.junit.Assert.assertEquals
import org.junit.Test

class FlagUtilsTest {

    @Test
    fun getFlagEmojiForTeamAbbreviation_Valid() {
        assertEquals(FlagUtils.getFlagEmojiForTeamAbbreviation("NZL"), "\uD83C\uDDF3\uD83C\uDDFF")
        assertEquals(FlagUtils.getFlagEmojiForTeamAbbreviation("IRE"), "\u2618\uFE0F")
        assertEquals(FlagUtils.getFlagEmojiForTeamAbbreviation("RSA"), "\uD83C\uDDFF\uD83C\uDDE6")
    }

    @Test
    fun getFlagEmojiForTeamAbbreviation_Invalid() {
        assertEquals(FlagUtils.getFlagEmojiForTeamAbbreviation("ABC"), "\uD83C\uDFC9")
        assertEquals(FlagUtils.getFlagEmojiForTeamAbbreviation("123"), "\uD83C\uDFC9")
        assertEquals(FlagUtils.getFlagEmojiForTeamAbbreviation("XYZ"), "\uD83C\uDFC9")
    }
}
