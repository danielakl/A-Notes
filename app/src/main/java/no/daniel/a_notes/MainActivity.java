package no.daniel.a_notes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;

import no.daniel.a_notes.dataTransfer.Note;
import no.daniel.a_notes.util.NoteManager;
import no.daniel.a_notes.util.ObjectSerializer;

public class MainActivity extends AppCompatActivity {
    public static final int NOTE_REQUEST_CODE = 0;

    private static NoteManager noteManager;
    private static ArrayAdapter<Note> arrayAdapter;
    private static SharedPreferences storage;

    /**
     * Called when activity is created.
     * @param savedInstanceState - Instance object to restore instance.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup permanent storage.
        storage = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        // Get saved notes.
        try {
            noteManager = NoteManager.initialize((ArrayList<Note>) ObjectSerializer.deserialize(storage.getString("notes", ObjectSerializer.serialize(new ArrayList<Note>()))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Setup note list.
        final ListView notesView = findViewById(R.id.notes_list);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, noteManager.getNotes());
        notesView.setAdapter(arrayAdapter);
        notesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startNoteActivity((Note) adapterView.getItemAtPosition(i), false);
            }
        });
        notesView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int position, long l) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                alertBuilder.setTitle(R.string.delete_confirm_title)
                        .setMessage(R.string.delete_confirm_mesg)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                noteManager.deleteNote((Note) adapterView.getItemAtPosition(position));
                                updateNotes();
                            }
                        }).setNegativeButton(R.string.no, null)
                        .create().show();
                return true;
            }
        });
    }

    /**
     * Start the note activity to view, edit or create new notes.
     * If a note is given and edit is set to true, the activity will be started
     * in edit mode.
     * If a note is given and edit is set to false, the activity will be started
     * in view mode.
     * If no note is given, the activity will be started in 'new note' mode.
     * @param note - The note to view or edit, null means writing a new one.
     * @param edit - True to edit a note, false to view.
     */
    private void startNoteActivity(Note note, boolean edit) {
        Intent intent = new Intent(getPackageName() + ".ManageNote");
        if (note != null) {
            // Adding note to intent.
            intent.putExtra("note", note);
            if (!edit) {
                // Starting activity to view existing note.
                intent.putExtra("editMode", false);
                this.startActivity(intent);
                return;
            }
        }
        // Starting activity to edit or write a new note.
        intent.putExtra("editMode", true);
        this.startActivityForResult(intent, NOTE_REQUEST_CODE);
    }

    /**
     * When finished editing or writing a new note.
     * @param requestCode - Only processes MainActivity.NOTE_REQUEST_CODE.
     * @param resultCode - RESULT_OK or RESULT_CANCELED, when activity is
     *                   finished or cancelled.
     * @param data - Intent with modified note.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NOTE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Note note = (Note) data.getSerializableExtra("note");
            if (note != null) {
                Note updatedNote = noteManager.updateNote(note.getId(), note.getNote());
                Note createdNote = null;
                if (updatedNote == null) {
                    createdNote = noteManager.createNote(note);
                }
                if (updatedNote != null || createdNote != null) {
                    updateNotes();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Called when creating options menu.
     * @param menu - The menu to create.
     * @return Returns true if an options menu was created, false if not.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Called when menu items in the top right menu is selected.
     * @param item - The menu item selected.
     * @return True if any menu item was selected, false if not.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_create_new_note:
                startNoteActivity(null, false);
                break;
            default:
                return false;
        }
        return true;
    }

    private static void updateNotes() {
        arrayAdapter.notifyDataSetChanged();
        try {
            storage.edit().putString("notes", ObjectSerializer.serialize(noteManager.getNotes())).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
