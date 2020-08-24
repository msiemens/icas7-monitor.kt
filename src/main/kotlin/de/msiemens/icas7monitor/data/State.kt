package de.msiemens.icas7monitor.data

import com.soywiz.klock.DateTime

data class State(
    val auth: Auth,

    val lastModified: DateTime?,
)