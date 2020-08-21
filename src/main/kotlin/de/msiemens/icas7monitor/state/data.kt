package de.msiemens.icas7monitor.state

import com.soywiz.klock.DateTime
import de.msiemens.icas7monitor.data.Auth

data class State(
    val auth: Auth,

    val lastModified: DateTime?,
)