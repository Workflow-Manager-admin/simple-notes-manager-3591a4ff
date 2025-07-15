package com.example.notesfrontend.data

import android.util.Log
import com.example.notesfrontend.model.Note
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.github.cdimascio.dotenv.dotenv
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

/**
 * Handles all communication with Supabase backend for notes CRUD.
 */
class SupabaseRepository {

    private val dotenv = dotenv {
        directory = "/data/data/com.example.notesfrontend/files"
        ignoreIfMalformed = true
        ignoreIfMissing = true
    }

    private val supabaseUrl: String = System.getenv("SUPABASE_URL") ?: dotenv["SUPABASE_URL"] ?: ""
    private val supabaseKey: String = System.getenv("SUPABASE_KEY") ?: dotenv["SUPABASE_KEY"] ?: ""
    private val tableName = "notes"

    private val client = OkHttpClient()
    private val moshi = Moshi.Builder().build()
    private val noteType = Types.newParameterizedType(List::class.java, Note::class.java)
    private val notesAdapter = moshi.adapter<List<Note>>(noteType)
    private val noteAdapter = moshi.adapter(Note::class.java)

    private fun buildRequestBuilder(path: String): Request.Builder {
        return Request.Builder()
            .url("$supabaseUrl/rest/v1/$path")
            .addHeader("apikey", supabaseKey)
            .addHeader("Authorization", "Bearer $supabaseKey")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
    }

    // PUBLIC_INTERFACE
    fun getNotes(
        onSuccess: (List<Note>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val path = "$tableName?select=*"
        val request = buildRequestBuilder(path)
            .get()
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(e.message ?: "Network error")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        onFailure("Failed: ${it.code}")
                        return
                    }
                    val result = notesAdapter.fromJson(it.body?.string().orEmpty())
                    onSuccess(result ?: emptyList())
                }
            }
        })
    }

    // PUBLIC_INTERFACE
    fun searchNotes(
        query: String,
        onSuccess: (List<Note>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val path = "$tableName?select=*&or=(title.ilike.%25$query%25,body.ilike.%25$query%25)"
        val request = buildRequestBuilder(path)
            .get()
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(e.message ?: "Network error")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        onFailure("Failed: ${it.code}")
                        return
                    }
                    val result = notesAdapter.fromJson(it.body?.string().orEmpty())
                    onSuccess(result ?: emptyList())
                }
            }
        })
    }

    // PUBLIC_INTERFACE
    fun addNote(
        note: Note,
        onSuccess: (Note) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val data = listOf(
            mapOf(
                "title" to note.title,
                "body" to note.body
            )
        )
        val body = moshi.adapter<List<Map<String, String>>>(List::class.java).toJson(data)
        val request = buildRequestBuilder(tableName)
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(e.message ?: "Network error")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        onFailure("Failed: ${it.code}")
                        return
                    }
                    val arr = notesAdapter.fromJson(it.body?.string().orEmpty()) ?: emptyList()
                    onSuccess(arr.firstOrNull() ?: note)
                }
            }
        })
    }

    // PUBLIC_INTERFACE
    fun updateNote(
        note: Note,
        onSuccess: (Note) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val data = mapOf(
            "title" to note.title,
            "body" to note.body
        )
        val body = moshi.adapter<Map<String, String>>(Map::class.java).toJson(data)
        val path = "$tableName?id=eq.${note.id}"
        val request = buildRequestBuilder(path)
            .patch(body.toRequestBody("application/json".toMediaType()))
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(e.message ?: "Network error")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        onFailure("Failed: ${it.code}")
                        return
                    }
                    val arr = notesAdapter.fromJson(it.body?.string().orEmpty()) ?: emptyList()
                    onSuccess(arr.firstOrNull() ?: note)
                }
            }
        })
    }

    // PUBLIC_INTERFACE
    fun deleteNote(
        noteId: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val path = "$tableName?id=eq.$noteId"
        val request = buildRequestBuilder(path)
            .delete()
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(e.message ?: "Network error")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        onFailure("Failed: ${it.code}")
                        return
                    }
                    onSuccess()
                }
            }
        })
    }

    // PUBLIC_INTERFACE
    fun getNoteById(
        noteId: String,
        onSuccess: (Note?) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val path = "$tableName?id=eq.$noteId"
        val request = buildRequestBuilder(path)
            .get()
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(e.message ?: "Network error")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        onFailure("Failed: ${it.code}")
                        return
                    }
                    val list = notesAdapter.fromJson(it.body?.string().orEmpty())
                    onSuccess(list?.firstOrNull())
                }
            }
        })
    }
}
