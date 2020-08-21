package de.msiemens.icas7monitor.notify

internal data class Contents(
    val text: String,
    val html: String?,
)

internal data class Envelope(
    val from: String,
    val to: String,
    val subject: String,
)
