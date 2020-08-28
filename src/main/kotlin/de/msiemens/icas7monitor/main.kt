package de.msiemens.icas7monitor

import com.soywiz.klock.DateTime
import de.msiemens.icas7monitor.http.fetchCourses
import de.msiemens.icas7monitor.http.getClient
import de.msiemens.icas7monitor.notify.notify
import de.msiemens.icas7monitor.scheduler.Action
import de.msiemens.icas7monitor.scheduler.processUpdates
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

    when (processUpdates(courses, state)) {
        Action.SKIP -> return
        Action.QUEUE_NOTIFICATION -> {
            println("Courses have changed -> queueing")

            persistState(
                state.copy(
                    courses = courses,
                    queuedNotification = DateTime.now()
                )
            )
        }
        Action.SEND_NOTIFICATION -> {
            println("Notification sent")

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

private suspend fun getState(init: Boolean, client: HttpClient) =
    if (init) initializeState(client) else loadState(client)
