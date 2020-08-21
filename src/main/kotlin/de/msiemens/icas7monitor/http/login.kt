package de.msiemens.icas7monitor.http

import com.google.gson.annotations.SerializedName
import de.msiemens.icas7monitor.data.Auth
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

data class AuthRequest(
    val email: String,
    val password: String,
)

data class AuthResponse(
    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("user_id")
    val userId: String,
)

suspend fun login(email: String, password: String, client: HttpClient): Auth {
    val response = client.post<AuthResponse> {
        url.takeFrom("https://sh.icas7.de/auth/login")
        contentType(ContentType.Application.Json)
        body = AuthRequest(email, password)
    }

    return Auth(response.accessToken, response.userId.toInt())
}
