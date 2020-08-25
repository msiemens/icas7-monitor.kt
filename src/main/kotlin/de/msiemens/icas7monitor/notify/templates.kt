package de.msiemens.icas7monitor.notify

import de.msiemens.icas7monitor.data.Course

// language=HTML
internal fun renderHtml(courses: List<Course>) = """
<html>
<head></head>
<body>
<p>Hey Theresa!</p>
<p>
    Dein Nachhilfe-Plan für die nächsten zwei Wochen wurde aktualisiert.<br>
    Hier ist der neue Plan:
</p>
${
    courses
        .map { course ->
            """
            <p style="margin-left: 2em">
                Am <b>${schedule(course)}</b>:<br>
                Fächer: ${subjects(course)}<br>
                Schüler: ${students(course)}
            </p>        
            """.trimIndent()
        }
        .joinToString("")
}
<p>
    Liebe Grüße,<br>
    deine freundliche Nachhilfe-Erinnerung :)
</p>
</body>
</html>
"""

internal fun renderText(courses: List<Course>) = """Hey Theresa!

Dein Nachhilfe-Plan für die nächsten zwei Wochen wurde aktualisiert.
Hier ist der neue Plan:

${
    courses
        .map { course ->
"""
    Am **${schedule(course)}**:
    Fächer: ${subjects(course)}
    Schüler: ${students(course)}
"""
        }
        .joinToString("")
}

Liebe Grüße,
deine freundliche Nachhilfe-Erinnerung :)
"""

private fun students(course: Course) =
    "${course.students.size} (${course.grades().sorted().joinToString(", ")})"

private fun subjects(course: Course) =
    if (course.subjects().isNotEmpty()) course.subjects().joinToString(", ") else ""

private fun schedule(course: Course) =
    "${course.startsOn?.format("dd.MM.yyyy")}, ${course.startsAt.format("HH:mm")} - ${course.endsAt.format("HH:mm")}"