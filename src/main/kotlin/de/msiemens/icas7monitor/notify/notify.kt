package de.msiemens.icas7monitor.notify

import de.msiemens.icas7monitor.config.Config
import de.msiemens.icas7monitor.data.Course
import io.ktor.client.*

suspend fun notify(courses: List<Course>, client: HttpClient) {
    val html = renderHtml(courses, Config.greeting)
    val text = renderText(courses, Config.greeting)

    sendMail(
        Contents(text, html),
        Envelope(
            from = Config.sendmailSender,
            to = Config.sendmailReceiver,
            subject = Config.sendmailSubject,
        ),
        client
    )
}
