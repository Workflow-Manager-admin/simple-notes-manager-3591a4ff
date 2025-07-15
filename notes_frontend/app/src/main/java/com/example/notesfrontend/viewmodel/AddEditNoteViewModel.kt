package com.example.notesfrontend.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notesfrontend.data.SupabaseRepository
import com.example.notesfrontend.model.Note

// PUBLIC_INTERFACE
class AddEditNoteViewModel : ViewModel() {
    private val repository = SupabaseRepository()

    private val _note = MutableLiveData<Note?>()
    val note: LiveData<Note?> = _note

    private val _saving = MutableLiveData<Boolean>()
    val saving: LiveData<Boolean> = _saving

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadNote(noteId: String?) {
        if (noteId == null || noteId.isEmpty()) {
            _note.postValue(null)
            return
        }
        repository.getNoteById(noteId, onSuccess = {
            _note.postValue(it)
        }, onFailure = {
            _error.postValue(it)
        })
    }

    fun saveNote(id: String?, title: String, body: String, isNew: Boolean, onComplete: (Boolean) -> Unit) {
        _saving.postValue(true)
        if (isNew) {
            repository.addNote(Note(title = title, body = body), onSuccess = {
                _note.postValue(it)
                _saving.postValue(false)
                _error.postValue(null)
                onComplete(true)
            }, onFailure = {
                _saving.postValue(false)
                _error.postValue(it)
                onComplete(false)
            })
        } else {
            repository.updateNote(Note(id = id ?: "", title = title, body = body), onSuccess = {
                _note.postValue(it)
                _saving.postValue(false)
                _error.postValue(null)
                onComplete(true)
            }, onFailure = {
                _saving.postValue(false)
                _error.postValue(it)
                onComplete(false)
            })
        }
    }

    fun deleteNote(noteId: String, onComplete: (Boolean) -> Unit) {
        repository.deleteNote(noteId, onSuccess = {
            _note.postValue(null)
            onComplete(true)
        }, onFailure = {
            _error.postValue(it)
            onComplete(false)
        })
    }
}
