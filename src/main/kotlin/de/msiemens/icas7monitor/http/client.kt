package de.msiemens.icas7monitor.http

import de.msiemens.icas7monitor.utils.serializationBuilder
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.statement.*

fun getClient() = client

private val client = createClient()

private fun createClient(): HttpClient {
    return HttpClient {
        install(JsonFeature) {
            serializer = GsonSerializer { serializationBuilder(this) }
        }
    }
}