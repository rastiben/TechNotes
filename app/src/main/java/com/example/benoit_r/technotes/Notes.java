package com.example.benoit_r.technotes;

import java.util.Date;

/**
 * Created by benoit_r on 21/04/2016.
 */
public class Notes implements java.io.Serializable{

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    private int _id;
    private String client;
    private String Note;
    private String noteDate;
    private String Tech;

    public String getTech() {
        return Tech;
    }

    public void setTech(String Tech) {
        this.Tech = Tech;
    }

    public String getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(String noteDate) {
        this.noteDate = noteDate;
    }

    public Notes() {

    }

    public Notes(String client,String Note,String noteDate){
        this.client = client;
        this.Note = Note;
        this.noteDate = noteDate;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    @Override
    public String toString()
    {
        return client + " " + Note;
    }

}
