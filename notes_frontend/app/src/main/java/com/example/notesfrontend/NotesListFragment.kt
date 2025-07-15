package com.example.notesfrontend

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notesfrontend.model.Note
import com.example.notesfrontend.viewmodel.NotesViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NotesListFragment : Fragment() {

    private val viewModel: NotesViewModel by viewModels()
    private lateinit var notesRecyclerView: RecyclerView
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var noNotesText: TextView
    private lateinit var searchView: androidx.appcompat.widget.SearchView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_notes_list, container, false)
        notesRecyclerView = view.findViewById(R.id.notesRecyclerView)
        val fabAddNote = view.findViewById<FloatingActionButton>(R.id.fabAddNote)
        noNotesText = view.findViewById(R.id.noNotesText)
        searchView = view.findViewById(R.id.searchView)
        notesAdapter = NotesAdapter(onClick = { note ->
            val bundle = Bundle()
            bundle.putString("noteId", note.id)
            findNavController().navigate(R.id.editNoteFragment, bundle)
        })
        notesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        notesRecyclerView.adapter = notesAdapter

        fabAddNote.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("noteId", "")
            findNavController().navigate(R.id.addNoteFragment, bundle)
        }

        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.searchNotes(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchNotes(newText.orEmpty())
                return true
            }
        })
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.notes.observe(viewLifecycleOwner, Observer {
            notesAdapter.submitList(it)
            noNotesText.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        })
        viewModel.fetchNotes()
    }

    private class NotesAdapter(val onClick: (Note) -> Unit) :
        RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

        private var notes: List<Note> = emptyList()

        fun submitList(list: List<Note>) {
            notes = list
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_2, parent, false)
            return NoteViewHolder(view, onClick)
        }

        override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
            holder.bind(notes[position])
        }

        override fun getItemCount(): Int = notes.size

        inner class NoteViewHolder(view: View, val onClick: (Note) -> Unit) : RecyclerView.ViewHolder(view) {
            private val text1: TextView = view.findViewById(android.R.id.text1)
            private val text2: TextView = view.findViewById(android.R.id.text2)
            private var current: Note? = null

            init {
                view.setOnClickListener {
                    current?.let(onClick)
                }
            }

            fun bind(note: Note) {
                current = note
                text1.text = note.title
                text2.text = note.body
            }
        }
    }
}
