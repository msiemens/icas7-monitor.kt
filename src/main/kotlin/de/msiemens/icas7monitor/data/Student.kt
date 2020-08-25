package de.msiemens.icas7monitor.data

import com.google.gson.annotations.SerializedName

data class Student(
    @SerializedName("subject_short")
    val subject: String,

    val postponed: String,

    @SerializedName("pupil_display_subhead")
    val grade: String,
)