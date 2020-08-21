package de.msiemens.icas7monitor

import com.soywiz.klock.DateTime
import de.msiemens.icas7monitor.data.Course
import de.msiemens.icas7monitor.http.fetchCourses
import de.msiemens.icas7monitor.http.getClient
import de.msiemens.icas7monitor.notify.notify
import de.msiemens.icas7monitor.state.loadState
import de.msiemens.icas7monitor.state.persistState

suspend fun run() {
    val client = getClient()

    val (courses, state) = fetchCourses(loadState(client), client)

    val lastModified = findLastModified(courses)

    if (lastModified != state.lastModified) {
        println("Courses have changed")

        notify(courses, client)
    } else {
        println("No course updates")
    }

    persistState(state.copy(lastModified = lastModified))
}

fun findLastModified(courses: List<Course>): DateTime? =
    courses.flatMap { course ->
        course.timestamps() + course.students.flatMap { it.timestamps() }
    }.maxOrNull()