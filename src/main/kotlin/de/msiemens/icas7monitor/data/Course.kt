package de.msiemens.icas7monitor.data

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.soywiz.klock.Date
import com.soywiz.klock.DateTime
import com.soywiz.klock.Time
import de.msiemens.icas7monitor.utils.InlineDateTimeDeserializer
import de.msiemens.icas7monitor.utils.InlineTimeDeserializer

data class Course(
    @SerializedName("course_id")
    val courseId: String,

    @SerializedName("starts_on")
    val startsOn: Date?,

    @SerializedName("ends_on")
    val endsOn: Date?,

    @SerializedName("starts_at")
    @JsonAdapter(InlineTimeDeserializer::class)
    val startsAt: Time,

    @SerializedName("ends_at")
    @JsonAdapter(InlineTimeDeserializer::class)
    val endsAt: Time,

    @SerializedName("created_at")
    @JsonAdapter(InlineDateTimeDeserializer::class)
    val createdAt: DateTime,

    @SerializedName("modified_at")
    @JsonAdapter(InlineDateTimeDeserializer::class)
    val modifiedAt: DateTime,

    @SerializedName("course_member")
    val students: List<Student>,
) {
    fun timestamps(): List<DateTime> = listOf(createdAt, modifiedAt)

    fun subjects(): List<String> = students
        .groupingBy { it.subject }
        .eachCount()
        .map { "${it.value}x ${it.key}" }

    fun grades(): List<String> = students.map { it.grade }
}

