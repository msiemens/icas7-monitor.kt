package de.msiemens.icas7monitor.notify

import de.msiemens.icas7monitor.config.Config
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

private data class SendMailRequest(
    val personalizations: List<SendMailPersonalization>,
    val from: SendMailSender,
    val subject: String,
    val content: List<SendMailContent>,
)

private data class SendMailPersonalization(
    val to: List<SendMailReceiver>,
)

private data class SendMailReceiver(
    val email: String,
)

private data class SendMailSender(
    val email: String,
)

private data class SendMailContent(
    val type: String,
    val value: String,
)

internal suspend fun sendMail(contents: Contents, envelope: Envelope, client: HttpClient) {
    val token = Config.sendmailToken

    val content = mutableListOf(
        SendMailContent("text/plain", contents.text)
    )

    if (contents.html != null) {
        content += SendMailContent("text/html", contents.html)
    }

    val request = SendMailRequest(
        personalizations = listOf(SendMailPersonalization(listOf(SendMailReceiver(envelope.to)))),
        from = SendMailSender(envelope.from),
        subject = envelope.subject,
        content = content
    )

    client.post<HttpResponse> {
        url("https://api.sendgrid.com/v3/mail/send")
        header(HttpHeaders.Authorization, "Bearer $token")
        contentType(ContentType.Application.Json)
        body = request
    }
}
