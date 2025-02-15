package com.example.createreviewback;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class notefragment extends Fragment {
    private GridView gridView;
    private NotesAdapter notesAdapter;
    private ArrayList<Integer> noteIds;
    private ArrayList<String> noteList;
    private SQLiteDatabase database;
    private EditText editTextNote;

    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notecode, container, false);

        gridView = view.findViewById(R.id.gridView);
        editTextNote = view.findViewById(R.id.editTextNote);
        Button btnSubmitNote = view.findViewById(R.id.btnSubmitNote);

        noteList = new ArrayList<>();
        noteIds = new ArrayList<>();
        notesAdapter = new NotesAdapter(requireContext(), noteList, noteIds);
        gridView.setAdapter(notesAdapter);

        // Initialize your database helper and get writable database
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        database = dbHelper.getWritableDatabase();

        // Load notes from the database
        loadNotesFromDatabase();

        btnSubmitNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteText = editTextNote.getText().toString().trim();
                if (!noteText.isEmpty()) {
                    // Save the note to the database
                    dbHelper.insertNote(noteText);
                    // Add the note to the list and notify the adapter
                    noteList.add(noteText);
                    noteIds.add(getNoteId(noteText));
                    notesAdapter.notifyDataSetChanged();
                    editTextNote.setText(""); // Clear the input field
                }
            }
        });
        return view;
    }

    private void loadNotesFromDatabase() {
        Cursor cursor = database.query(DatabaseHelper.TABLE_NOTES, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                @SuppressLint("Range") String note = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NOTE));
                noteList.add(note);
                noteIds.add(id);
            } while (cursor.moveToNext());
            cursor.close();
        }
        notesAdapter.notifyDataSetChanged();
    }

    private int getNoteId(String note) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_NOTES, new String[]{DatabaseHelper.COLUMN_ID},
                DatabaseHelper.COLUMN_NOTE + "=?", new String[]{note},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
            cursor.close();
            return id;
        }
        return -1; // Return -1 if not found
    }

    public class NotesAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<String> notes;
        private ArrayList<Integer> noteIds;

        public NotesAdapter(Context context, ArrayList<String> notes, ArrayList<Integer> noteIds) {
            this.context = context;
            this.notes = notes;
            this.noteIds = noteIds;
        }

        @Override
        public int getCount() {
            return notes.size();
        }

        @Override
        public Object getItem(int position) {
            return notes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return noteIds.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                // Inflate a new view if convertView is null
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.grid_item_layout_note, parent, false);
            } else {
                // Reuse the existing view
                view = convertView;
            }

            // Retrieve the TextView and ImageButton from the layout
            TextView textView = view.findViewById(R.id.textViewNote);
            ImageButton btnDelete = view.findViewById(R.id.btnDeleteNote);

            // Set the note text
            String note = notes.get(position);
            textView.setText(note);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Remove the note from the database
                    int noteId = noteIds.get(position);
                    deleteNoteFromDatabase(noteId);

                    // Remove the note from the list and notify the adapter
                    notes.remove(position);
                    noteIds.remove(position);
                    notifyDataSetChanged();
                }
            });

            return view;
        }


        private void deleteNoteFromDatabase(int noteId) {
            SQLiteDatabase db = context.openOrCreateDatabase("MediaDB", Context.MODE_PRIVATE, null);
            db.delete(DatabaseHelper.TABLE_NOTES, DatabaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(noteId)});
            db.close();
        }
    }
}