package de.msiemens.icas7monitor.scheduler

import com.soywiz.klock.Date
import com.soywiz.klock.DateTime
import com.soywiz.klock.Time
import com.soywiz.klock.hours
import de.msiemens.icas7monitor.data.Auth
import de.msiemens.icas7monitor.data.Course
import de.msiemens.icas7monitor.data.State
import de.msiemens.icas7monitor.data.Student
import org.junit.jupiter.api.DisplayName
import kotlin.test.*

internal class SchedulerKtTest {
    val auth = Auth("", 1)
    val course = Course(
        courseId = "1",
        startsOn = Date(2020, 1, 1),
        endsOn = null,
        startsAt = Time(16),
        endsAt = Time(17),
        students = listOf(
            Student("DEU", "", "8GYM")
        )
    )

    @Test
    @DisplayName("skips when no changes")
    fun `skips when no changes`() {
        val state = State(
            auth = auth,
            courses = listOf(course.copy()),
            queuedNotification = null
        )

        assertEquals(Action.SKIP, processUpdates(listOf(course), state))
    }

    @Test
    @DisplayName("skips when not scheduled notification")
    fun `skips when not scheduled notification`() {
        val state = State(
            auth = auth,
            courses = listOf(course.copy()),
            queuedNotification = DateTime.now()
        )

        assertEquals(Action.SKIP, processUpdates(listOf(course), state))
    }

    @Test
    @DisplayName("queues notification when changes")
    fun `queues notification when changes`() {
        val state = State(
            auth = auth,
            courses = listOf(course.copy(endsAt = Time(18))),
            queuedNotification = null
        )

        assertEquals(Action.QUEUE_NOTIFICATION, processUpdates(listOf(course), state))
    }

    @Test
    @DisplayName("queues notification when changes and scheduled")
    fun `queues notification when changes and scheduled`() {
        val state = State(
            auth = auth,
            courses = listOf(course.copy(endsAt = Time(18))),
            queuedNotification = DateTime.now() - 2.hours
        )

        assertEquals(Action.QUEUE_NOTIFICATION, processUpdates(listOf(course), state))
    }

    @Test
    @DisplayName("sends notification when scheduled")
    fun `sends notification when scheduled`() {
        val state = State(
            auth = auth,
            courses = listOf(course.copy()),
            queuedNotification = DateTime.now() - 2.hours
        )

        assertEquals(Action.SEND_NOTIFICATION, processUpdates(listOf(course), state))
    }
}
