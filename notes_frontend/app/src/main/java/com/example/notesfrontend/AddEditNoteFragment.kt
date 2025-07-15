package com.example.notesfrontend

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.notesfrontend.viewmodel.AddEditNoteViewModel
import com.example.notesfrontend.model.Note

class AddEditNoteFragment : Fragment() {

    private val viewModel: AddEditNoteViewModel by viewModels()
    private var noteId: String? = null
    private var isNewNote: Boolean = true

    private lateinit var editTitle: EditText
    private lateinit var editBody: EditText
    private lateinit var buttonSave: Button
    private lateinit var buttonDelete: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_edit_note, container, false)
        editTitle = view.findViewById(R.id.editNoteTitle)
        editBody = view.findViewById(R.id.editNoteBody)
        buttonSave = view.findViewById(R.id.buttonSave)
        buttonDelete = view.findViewById(R.id.buttonDelete)
        val toolbar = view.findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)

        noteId = arguments?.getString("noteId")
        isNewNote = noteId.isNullOrEmpty()

        toolbar.title = if (isNewNote) getString(R.string.add_note) else getString(R.string.edit_note)
        toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        buttonDelete.visibility = if (isNewNote) View.GONE else View.VISIBLE

        buttonSave.setOnClickListener {
            viewModel.saveNote(
                id = noteId,
                title = editTitle.text.toString(),
                body = editBody.text.toString(),
                isNew = isNewNote
            ) { success ->
                if (success) {
                    findNavController().popBackStack()
                }
            }
        }

        buttonDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.confirm_delete)
                .setPositiveButton(R.string.yes) { _, _ ->
                    viewModel.deleteNote(noteId ?: "") { success ->
                        if (success) findNavController().popBackStack()
                    }
                }
                .setNegativeButton(R.string.no, null)
                .show()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (!isNewNote) {
            viewModel.loadNote(noteId)
        }
        viewModel.note.observe(viewLifecycleOwner) { note ->
            note?.let {
                editTitle.setText(it.title)
                editBody.setText(it.body)
            }
        }
    }
}
