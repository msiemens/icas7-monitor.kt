package de.msiemens.icas7monitor.state

import de.msiemens.icas7monitor.config.Config
import de.msiemens.icas7monitor.data.State
import de.msiemens.icas7monitor.http.login
import de.msiemens.icas7monitor.utils.serializationBuilder
import io.ktor.client.*
import java.io.File
import java.io.FileNotFoundException

suspend fun loadState(client: HttpClient): State =
    restoreState() ?: State(login(Config.username, Config.password, client), null)

suspend fun refreshState(state: State, client: HttpClient): State =
    state.copy(auth = login(Config.username, Config.password, client))

private fun restoreState(): State? {
    val contents = try {
        File("state.json").readText()
    } catch (e: FileNotFoundException) {
        return null
    }

    return serializationBuilder().create().fromJson(contents, State::class.java)
}

fun persistState(state: State) {
    val contents = serializationBuilder().create().toJson(state)

    File("state.json").writeText(contents)
}
