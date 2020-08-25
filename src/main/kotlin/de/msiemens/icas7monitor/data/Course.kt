package de.msiemens.icas7monitor.data

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.soywiz.klock.Date
import com.soywiz.klock.Time
import de.msiemens.icas7monitor.utils.InlineTimeSerializer

data class Course(
    @SerializedName("course_id")
    val courseId: String,

    @SerializedName("starts_on")
    val startsOn: Date?,

    @SerializedName("ends_on")
    val endsOn: Date?,

    @SerializedName("starts_at")
    @JsonAdapter(InlineTimeSerializer::class)
    val startsAt: Time,

    @SerializedName("ends_at")
    @JsonAdapter(InlineTimeSerializer::class)
    val endsAt: Time,

    @SerializedName("course_member")
    val students: List<Student>,
) {
    fun subjects(): List<String> = students
        .groupingBy { it.subject }
        .eachCount()
        .map { "${it.value}x ${it.key}" }

    fun grades(): List<String> = students.map { it.grade }
}

