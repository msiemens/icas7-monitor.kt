package de.msiemens.icas7monitor

import com.soywiz.klock.DateTime
import com.soywiz.klock.ISO8601
import de.msiemens.icas7monitor.data.Course
import de.msiemens.icas7monitor.data.State
import de.msiemens.icas7monitor.http.fetchCourses
import de.msiemens.icas7monitor.http.getClient
import de.msiemens.icas7monitor.notify.notify
import de.msiemens.icas7monitor.state.loadState
import de.msiemens.icas7monitor.state.persistState
import io.ktor.client.features.*
import io.ktor.client.statement.*
import kotlinx.cli.*
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import java.util.logging.LogManager

private val loggingConfig = {}::class.java.getResourceAsStream("/logging.properties")
private val _log = LogManager.getLogManager().readConfiguration(loggingConfig)
private val logger = KotlinLogging.logger {}

fun run(args: Array<String>) {
    val parser = ArgParser("icas7-monitor")
    val dryRun by parser.option(ArgType.Boolean, fullName = "dry-run", description = "Perform dry run").default(false)

    parser.parse(args)

    runBlocking {
        try {
            execute(dryRun)
        } catch (e: ClientRequestException) {
            val response = e.response?.readText()

            logger.info { "HTTP error: $response" }

            throw e
        }
    }
}

private suspend fun execute(dryRun: Boolean) {
    val client = getClient()

    val (courses, state) = fetchCourses(loadState(client), client)

    val lastModified = findLastModified(courses)

    logger.info { "lastModified = $lastModified" }
    logger.info { "state.lastModified = ${state.lastModified}" }

    if (dryRun) {
        println("state.lastModified = ${state.lastModified}")
        println("lastModified = $lastModified")

        return
    }

    if (lastModified != state.lastModified) {
        println("Courses have changed")

        logger.info { "sending notification" }

        notify(courses, client)
    }

    persistState(state.copy(lastModified = lastModified))
}

fun findLastModified(courses: List<Course>): DateTime? =
    courses.flatMap { course ->
        course.timestamps() + course.students.flatMap { it.timestamps() }
    }.maxOrNull()