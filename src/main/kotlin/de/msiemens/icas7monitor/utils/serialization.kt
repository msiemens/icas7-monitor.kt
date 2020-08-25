package de.msiemens.icas7monitor.utils

import com.google.gson.*
import com.soywiz.klock.*
import java.lang.reflect.Type

fun serializationBuilder(gson: GsonBuilder = GsonBuilder()): GsonBuilder = gson.apply {
    registerTypeAdapter(DateTime::class.java, DateTimeSerializer())
    registerTypeAdapter(Date::class.java, DateSerializer())
    registerTypeAdapter(Time::class.java, TimeSerializer())
}

val dateTimeFormat = ISO8601.IsoDateTimeFormat("YYYY-MM-DD hh:mm:ss", null)

class DateTimeSerializer : JsonSerializer<DateTime>, JsonDeserializer<DateTime> {
    override fun serialize(src: DateTime?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive((src ?: return JsonNull.INSTANCE).format(dateTimeFormat))
    }

    override fun deserialize(json: JsonElement?, type: Type?, context: JsonDeserializationContext?): DateTime? {
        return dateTimeFormat.parse(json?.asString ?: return null).utc
    }
}

class DateSerializer : JsonSerializer<Date>, JsonDeserializer<Date> {
    override fun serialize(src: Date?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive((src ?: return JsonNull.INSTANCE).format(ISO8601.DATE))
    }

    override fun deserialize(json: JsonElement?, type: Type?, context: JsonDeserializationContext?): Date? {
        val value = json?.asString ?: return null

        if (value == "0000-00-00") {
            return null
        }

        return ISO8601.DATE.parseDate(value)
    }
}

class TimeSerializer : JsonSerializer<Time>, JsonDeserializer<Time> {
    override fun serialize(src: Time?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive((src ?: return JsonNull.INSTANCE).format(ISO8601.TIME))
    }

    override fun deserialize(json: JsonElement?, type: Type?, context: JsonDeserializationContext?): Time? {
        return ISO8601.TIME.parseTime(json?.asString ?: return null)
    }
}

class InlineTimeSerializer : JsonSerializer<Double>, JsonDeserializer<Double> {
    override fun serialize(src: Double?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive((Time(TimeSpan(src ?: return JsonNull.INSTANCE))).format(ISO8601.TIME))
    }

    override fun deserialize(json: JsonElement?, type: Type?, context: JsonDeserializationContext?): Double? {
        return ISO8601.TIME.parseTime(json?.asString ?: return null).encoded.milliseconds
    }
}