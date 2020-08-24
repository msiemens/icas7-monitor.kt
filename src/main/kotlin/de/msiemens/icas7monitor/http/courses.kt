package de.msiemens.icas7monitor.http

import com.google.gson.annotations.SerializedName
import com.soywiz.klock.*
import de.msiemens.icas7monitor.data.Course
import de.msiemens.icas7monitor.data.State
import de.msiemens.icas7monitor.state.refreshState
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

    return range.days()
        .fold(Pair(emptyList(), state),
            { acc: Pair<List<Course>, State>, date: Date ->
                fetchCoursesFor(date, acc, client)
            })
}

private suspend fun fetchCoursesFor(
    date: Date,
    acc: Pair<List<Course>, State>,
    client: HttpClient
): Pair<List<Course>, State> {
    val (courses, state) = acc

    val response = try {
        client.get<FetchCoursesResponse> {
            url.takeFrom("https://sh.icas7.de/courselist")
            header(HttpHeaders.Authorization, "Bearer ${state.auth.accessToken}")
            parameter("present", date.format(ISO8601.DATE_CALENDAR_COMPLETE.extended))
            parameter("teacher_id", state.auth.teacherId.toString())
        }
    } catch (e: ClientRequestException) {
        if (e.response?.status == HttpStatusCode.Forbidden) {
            return fetchCoursesFor(
                date,
                courses to refreshState(state, client),
                client
            )
        }

        throw e
    }

    val newCourses = response.courses.map { it.copy(startsOn = date) }

    return courses + newCourses to state
}