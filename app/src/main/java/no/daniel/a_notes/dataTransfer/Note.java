package no.daniel.a_notes.dataTransfer;

import android.support.annotation.NonNull;

import java.io.Serializable;

public final class Note implements Serializable, Comparable<Note> {
    private final int id;
    private String note;

    public Note(int id, String note) {
        this.id = (id < 0) ? -1 : id;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public int compareTo(@NonNull Note other) {
        return id - other.getId();
    }

    @Override
    public boolean equals(Object other) {
        return other != null && other instanceof Note && id == ((Note) other).getId();
    }

    @Override
    public String toString() {
        return note;
    }
}
