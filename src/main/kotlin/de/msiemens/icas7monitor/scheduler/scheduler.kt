package de.msiemens.icas7monitor.scheduler

import com.soywiz.klock.DateTime
import com.soywiz.klock.hours
import de.msiemens.icas7monitor.data.Course
import de.msiemens.icas7monitor.data.State

enum class Action {
    SKIP, QUEUE_NOTIFICATION, SEND_NOTIFICATION
}

fun processUpdates(
    courses: List<Course>,
    state: State,
): Action {
    // Phase 1: detect new changes
    if (courses != state.courses) {
        println("Courses have changed -> queueing")

        return Action.QUEUE_NOTIFICATION
    }

    // Phase 2: send queued notification
    if (!hasScheduledNotification(state)) {
        return Action.SKIP
    }

    return Action.SEND_NOTIFICATION
}

private fun hasScheduledNotification(state: State): Boolean =
    state.queuedNotification != null && DateTime.now() - state.queuedNotification >= 2.hours