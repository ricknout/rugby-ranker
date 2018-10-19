package com.ricknout.rugbyranker.util

import org.junit.Test
import org.junit.Assert.assertEquals

class FlagUtilsTest {

    @Test
    fun getFlagEmojiForTeamAbbreviation_Valid() {
        assertEquals(FlagUtils.getFlagEmojiForTeamAbbreviation("NZL"), "\uD83C\uDDF3\uD83C\uDDFF")
        assertEquals(FlagUtils.getFlagEmojiForTeamAbbreviation("IRE"), "\uD83C\uDDEE\uD83C\uDDEA")
        assertEquals(FlagUtils.getFlagEmojiForTeamAbbreviation("RSA"), "\uD83C\uDDFF\uD83C\uDDE6")
    }

    @Test
    fun getFlagEmojiForTeamAbbreviation_Invalid() {
        assertEquals(FlagUtils.getFlagEmojiForTeamAbbreviation("ABC"), "\uD83C\uDFF3")
        assertEquals(FlagUtils.getFlagEmojiForTeamAbbreviation("123"), "\uD83C\uDFF3")
        assertEquals(FlagUtils.getFlagEmojiForTeamAbbreviation("XYZ"), "\uD83C\uDFF3")
    }
}
