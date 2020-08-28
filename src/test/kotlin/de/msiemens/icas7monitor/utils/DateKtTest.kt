package de.msiemens.icas7monitor.utils

import com.soywiz.klock.Date
import com.soywiz.klock.DateTime
import com.soywiz.klock.until
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import kotlin.test.*

internal class DateKtTest {

    @Test
    @DisplayName("correctly calculates date range")
    fun days() {
        val from = DateTime(2020, 1, 1, 17)
        val to = DateTime(2020, 1, 5, 17)

        assertEquals(listOf(
            Date(2020, 1, 1),
            Date(2020, 1, 2),
            Date(2020, 1, 3),
            Date(2020, 1, 4),
        ), (from until to).days().asSequence().toList())
    }
}