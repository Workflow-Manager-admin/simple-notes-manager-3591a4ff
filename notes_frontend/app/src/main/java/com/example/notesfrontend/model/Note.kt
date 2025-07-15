package com.example.notesfrontend.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
// PUBLIC_INTERFACE
data class Note(
    @Json(name = "id")
    val id: String = "",
    @Json(name = "title")
    val title: String = "",
    @Json(name = "body")
    val body: String = "",
    @Json(name = "created_at")
    val createdAt: String? = null,
    @Json(name = "updated_at")
    val updatedAt: String? = null
)
