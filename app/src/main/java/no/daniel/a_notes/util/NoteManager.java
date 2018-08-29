package no.daniel.a_notes.util;

import android.support.annotation.NonNull;

import java.util.ArrayList;

import no.daniel.a_notes.dataTransfer.Note;

public final class NoteManager {
    private static int currentNoteId = 0;
    private final ArrayList<Note> notes;

    private static NoteManager noteManager;

    /**
     * Initialize the note manager with an array of existing notes, can be an
     * empty array.
     * @param notes - Pre-existing notes.
     * @return The only note manager object.
     */
    public static NoteManager initialize(ArrayList<Note> notes) {
        if (noteManager == null) {
            ArrayList<Note> tempNotes = new ArrayList<>();
            if (notes != null) {
                for (Note n : notes) {
                    if (n != null && !tempNotes.contains(n)) {
                        tempNotes.add(n);
                    }
                }
            }
            noteManager = new NoteManager(tempNotes);
        }
        return noteManager;
    }

    private NoteManager(ArrayList<Note> notes) {
        this.notes = notes;
    }

    /**
     * Create a note from a string. Storing the note in a list, returns the new note.
     * @param note - The string the note consists of.
     * @return The newly created note, or null.
     */
    public Note createNote(String note) {
        if (note != null && !note.trim().equals("")) {
            Note n = new Note(currentNoteId++, capitalizeAndTrim(note));
            notes.add(n);
            return n;
        }
        return null;
    }

    /**
     * Create a note from a pre-existing note object, useful if the given note
     * is returned from another somewhere else as a java bean.
     * @param note - The note to store and recreate in a list.
     * @return The newly created note, or null.
     */
    public Note createNote(Note note) {
        if (note != null) {
            String sNote = note.getNote();
            if (sNote != null && !sNote.trim().equals("")) {
                note = new Note(currentNoteId++, capitalizeAndTrim(sNote));
                notes.add(note);
                return note;
            }
        }
        return null;
    }

    /**
     * Updates a note, using the id to find the note and updating its text content.
     * @param id - The id of the note to update.
     * @param note - The new text of the note.
     * @return The newly updated note, or null.
     */
    public Note updateNote(int id, String note) {
        int index = notes.indexOf(new Note(id, null));
        if (index > -1) {
            Note n = notes.get(index);
            if (n != null && note != null && !note.trim().equals("")) {
                n.setNote(capitalizeAndTrim(note));
                return n;
            }
        }
        return null;
    }

    /**
     * Updates a note, using the id of the oldNote as reference.
     * Updating the note with the text content of the new note.
     * @param oldNote - Using equality to find note.
     * @param newNote - Contains new data.
     * @return The newly updated note.
     */
    public Note updateNote(Note oldNote, Note newNote) {
        int index = notes.indexOf(oldNote);
        if (index > -1) {
            if (newNote != null) {
                Note note = notes.get(index);
                String sNote = newNote.getNote();
                if (sNote != null && !sNote.trim().equals("")) {
                    note.setNote(capitalizeAndTrim(sNote));
                    return note;
                }
            }
        }
        return null;
    }

    /**
     * Deletes a note from the list with the given id.
     * @param id - The id of the note to delete.
     * @return False if no note was deleted, true if the note was deleted.
     */
    public boolean deleteNote(int id) {
        return notes.remove(new Note(id, null));
    }

    /**
     * Deletes a note from the list by checking the equality of the given note
     * to the notes in the list.
     * @param note - The note to delete.
     * @return False if no note was deleted, true if the note was deleted.
     */
    public boolean deleteNote(Note note) {
        return note != null && notes.remove(note);
    }

    /**
     * Gets the entire list of notes.
     * @return The list of notes.
     */
    public ArrayList<Note> getNotes() {
        return notes;
    }

    private static String capitalizeAndTrim(@NonNull String string) {
        string = string.trim();
        if (!string.equals("")) {
            string = string.substring(0, 1).toUpperCase() + string.substring(1);
        }
        return string;
    }
}
