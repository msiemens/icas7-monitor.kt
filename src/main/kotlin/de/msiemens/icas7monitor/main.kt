package de.msiemens.icas7monitor

import com.soywiz.klock.DateTime
import com.soywiz.klock.ISO8601
import de.msiemens.icas7monitor.data.Course
import de.msiemens.icas7monitor.data.State
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

data class Options(
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

    if (options.dryRun) {
        println("state.courses = ${state.courses}")
        println("courses = $courses")

        return
    }

    if (courses != state.courses && !options.init) {
        println("Courses have changed")

        logger.info { "sending notification" }

        notify(courses, client)
    }

    persistState(state.copy(courses = courses))
}

private suspend fun getState(init: Boolean, client: HttpClient) =
    if (init) initializeState(client) else loadState(client)