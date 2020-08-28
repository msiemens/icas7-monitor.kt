package de.msiemens.icas7monitor.utils

import com.soywiz.klock.Date
import com.soywiz.klock.DateTimeRange
import com.soywiz.klock.days

fun DateTimeRange.days(): Iterable<Date> {
    return Iterable {
        object : Iterator<Date> {
            var now = from

            override fun hasNext(): Boolean = contains(now)

            override fun next(): Date {
                val date = now

                now += 1.days

                return date.date
            }
        }
    }
}
