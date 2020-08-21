import de.msiemens.icas7monitor.run
import io.ktor.client.features.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        try {
            run()
        } catch (e: ClientRequestException) {
            println("Response: ${e.response?.readText()}")

            throw e
        }
    }
}