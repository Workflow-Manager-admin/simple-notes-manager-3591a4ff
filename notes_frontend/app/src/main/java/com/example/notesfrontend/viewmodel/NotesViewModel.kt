package com.example.notesfrontend.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notesfrontend.data.SupabaseRepository
import com.example.notesfrontend.model.Note

// PUBLIC_INTERFACE
class NotesViewModel : ViewModel() {
    private val repository = SupabaseRepository()

    private val _notes = MutableLiveData<List<Note>>(emptyList())
    val notes: LiveData<List<Note>> = _notes

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun fetchNotes() {
        _loading.postValue(true)
        repository.getNotes(
            onSuccess = {
                _notes.postValue(it)
                _loading.postValue(false)
                _error.postValue(null)
            },
            onFailure = {
                _notes.postValue(emptyList())
                _loading.postValue(false)
                _error.postValue(it)
            }
        )
    }

    fun searchNotes(query: String) {
        if (query.isEmpty()) {
            fetchNotes()
            return
        }
        _loading.postValue(true)
        repository.searchNotes(query,
            onSuccess = {
                _notes.postValue(it)
                _loading.postValue(false)
                _error.postValue(null)
            },
            onFailure = {
                _notes.postValue(emptyList())
                _loading.postValue(false)
                _error.postValue(it)
            }
        )
    }

    fun deleteNote(noteId: String, onComplete: () -> Unit) {
        repository.deleteNote(noteId, onSuccess = {
            fetchNotes()
            onComplete()
        }, onFailure = {
            _error.postValue(it)
            onComplete()
        })
    }
}
