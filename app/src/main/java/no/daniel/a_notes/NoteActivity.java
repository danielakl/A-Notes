package no.daniel.a_notes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.widget.EditText;

import no.daniel.a_notes.dataTransfer.Note;

public class NoteActivity extends AppCompatActivity {
    private static Intent intent;
    private static Note note;
    private static boolean editMode;

    private EditText noteView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        // Setup views.
        noteView = findViewById(R.id.note_view);

        // Get intent and make changes to editView.
        intent = getIntent();
        if (intent != null) {
            note = (Note) intent.getSerializableExtra("note");
            if (note != null) {
                noteView.setText(note.getNote());
            } else {
                note = new Note(-1, "");
            }
            editMode = intent.getBooleanExtra("editMode", true);
            if (editMode) {
                noteView.setEnabled(true);
            } else {
                noteView.setEnabled(false);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (editMode) {
            if (note != null) {
                note.setNote(noteView.getText().toString());
                intent.putExtra("note", note);
                setResult(RESULT_OK, intent);
            } else {
                setResult(RESULT_CANCELED, intent);
            }
            finishActivity(MainActivity.NOTE_REQUEST_CODE);
        }
        super.onBackPressed();
    }
}
