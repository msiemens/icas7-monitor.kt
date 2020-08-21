package de.msiemens.icas7monitor.http

import com.google.gson.annotations.SerializedName
import com.soywiz.klock.*
import de.msiemens.icas7monitor.config.Config
import de.msiemens.icas7monitor.data.Course
import de.msiemens.icas7monitor.state.State
import de.msiemens.icas7monitor.state.initializeState
import de.msiemens.icas7monitor.utils.days
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*

data class FetchCoursesResponse(
    @SerializedName("courselist")
    val courses: List<Course>,
)

suspend fun fetchCourses(state: State, client: HttpClient): Pair<List<Course>, State> {
    val today = DateTime.now()
    val range = today until (today + 2.weeks)

    return range.days().fold(Pair(emptyList(), state), { acc: Pair<List<Course>, State>, date: Date ->
        fetchCoursesFor(date, acc, client)
    })
}

private suspend fun fetchCoursesFor(date: Date, acc: Pair<List<Course>, State>, client: HttpClient): Pair<List<Course>, State> {
    val response = try {
        client.get<FetchCoursesResponse> {
            url.takeFrom("https://sh.icas7.de/courselist")
            header(HttpHeaders.Authorization, "Bearer ${acc.second.auth.accessToken}")
            parameter("present", date.format(ISO8601.DATE_CALENDAR_COMPLETE.extended))
            parameter("teacher_id", acc.second.auth.teacherId.toString())
        }
    } catch (e: ClientRequestException) {
        if (e.response?.status == HttpStatusCode.Forbidden) {
            return fetchCoursesFor(
                date,
                Pair(acc.first, initializeState(Config.username, Config.password, client)),
                client
            )
        }

        throw e
    }

    val courses = response.courses
        .map { it.copy(startsOn = date) }

    return Pair(acc.first + courses, acc.second)
}