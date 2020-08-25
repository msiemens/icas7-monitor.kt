package de.msiemens.icas7monitor.data

data class State(
    val auth: Auth,

    val courses: List<Course>?,
)