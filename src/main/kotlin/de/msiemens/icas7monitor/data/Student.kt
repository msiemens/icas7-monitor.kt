package de.msiemens.icas7monitor.data

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.soywiz.klock.DateTime
import de.msiemens.icas7monitor.utils.InlineDateTimeDeserializer

data class Student(
    @SerializedName("created_at")
    @JsonAdapter(InlineDateTimeDeserializer::class)
    val createdAt: DateTime,

    @SerializedName("modified_at")
    @JsonAdapter(InlineDateTimeDeserializer::class)
    val modifiedAt: DateTime,

    @SerializedName("subject_short")
    val subject: String,

    val postponed: String,

    @SerializedName("pupil_display_subhead")
    val grade: String,
) {
    fun timestamps(): List<DateTime> = listOf(createdAt, modifiedAt)
}