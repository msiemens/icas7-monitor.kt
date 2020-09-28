package de.msiemens.icas7monitor.config

object Config {
    val username: String = System.getenv("I7M_USERNAME")
    val password: String = System.getenv("I7M_PASSWORD")
    val greeting: String = System.getenv("I7M_GREETING")
    val sendmailToken: String = System.getenv("I7M_SENDMAIL_TOKEN")
    val sendmailSender: String = System.getenv("I7M_SENDMAIL_SENDER")
    val sendmailReceiver: String = System.getenv("I7M_SENDMAIL_RECEIVER")
    val sendmailSubject: String = System.getenv("I7M_SENDMAIL_SUBJECT")
}
