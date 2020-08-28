package de.msiemens.icas7monitor

import com.soywiz.klock.DateTime
import com.soywiz.klock.ISO8601
import com.soywiz.klock.TimeSpan
import com.soywiz.klock.hours
import de.msiemens.icas7monitor.data.Course
import de.msiemens.icas7monitor.data.State
import de.msiemens.icas7monitor.data.Student
import de.msiemens.icas7monitor.http.fetchCourses
import de.msiemens.icas7monitor.http.getClient
import de.msiemens.icas7monitor.notify.notify
import de.msiemens.icas7monitor.state.initializeState
import de.msiemens.icas7monitor.state.loadState
import de.msiemens.icas7monitor.state.persistState
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.statement.*
import kotlinx.cli.*
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import java.util.logging.LogManager

private val loggingConfig = {}::class.java.getResourceAsStream("/logging.properties")
private val _log = LogManager.getLogManager().readConfiguration(loggingConfig)
private val logger = KotlinLogging.logger {}

private data class Options(
    val dryRun: Boolean,
    val init: Boolean,
) {
    companion object {
        fun parse(args: Array<String>): Options {
            val parser = ArgParser("icas7-monitor")

            val dryRun by parser.option(ArgType.Boolean, fullName = "dry-run", description = "Perform dry run")
                .default(false)
            val init by parser.option(ArgType.Boolean, fullName = "init", description = "Initialize state file")
                .default(false)

            parser.parse(args)

            return Options(dryRun, init)
        }
    }
}

internal enum class Action {
    SKIP, QUEUE_NOTIFICATION, SEND_NOTIFICATION
}

fun run(args: Array<String>) {
    val options = Options.parse(args)

    runBlocking {
        try {
            execute(options)
        } catch (e: ClientRequestException) {
            val response = e.response?.readText()

            logger.info { "HTTP error: $response" }

            throw e
        }
    }
}

private suspend fun execute(options: Options) {
    val client = getClient()

    val (courses, state) = fetchCourses(getState(options.init, client), client)

    if (options.init) {
        persistState(state.copy(courses = courses))

        return
    }

    when (processChanges(courses, state)) {
        Action.SKIP -> return
        Action.QUEUE_NOTIFICATION -> persistState(
            state.copy(
                courses = courses,
                queuedNotification = DateTime.now()
            )
        )
        Action.SEND_NOTIFICATION -> {
            notify(courses, client)

            persistState(
                state.copy(
                    courses = courses,
                    queuedNotification = null
                )
            )
        }
    }
}

internal fun processChanges(
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

private suspend fun getState(init: Boolean, client: HttpClient) =
    if (init) initializeState(client) else loadState(client)
