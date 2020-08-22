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
                Am <b>${course.startsOn?.format("dd.MM.yyyy")}, ${course.startsAt.format("HH:mm")} - ${course.endsAt.format("HH:mm")}</b>:<br>
                Fächer: ${
                    if (course.subjects().isNotEmpty()) course.subjects().joinToString(", ") else ""
                }<br>
                Schüler: ${course.students.size} (${course.grades().sorted().joinToString(", ")})
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
    Am **${course.startsOn?.format("dd.MM.yyyy")}, ${course.startsAt.format("HH:mm")} - ${course.endsAt.format("HH:mm")}**:
    Fächer: ${if (course.subjects().isNotEmpty()) course.subjects().joinToString(", ") else ""}
    Schüler: ${course.students.size} (${course.grades().sorted().joinToString(", ")})
"""
        }
        .joinToString("")
}

Liebe Grüße,
deine freundliche Nachhilfe-Erinnerung :)
"""