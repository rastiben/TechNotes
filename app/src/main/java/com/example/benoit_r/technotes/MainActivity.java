package com.example.benoit_r.technotes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void addNoteActivity(View view) {
        Intent intentMain = new Intent(this, addNote.class);
        this.startActivity(intentMain);
    }

    public void modifyNoteActivity(View view) {
        Intent intentMain = new Intent(this, notesList.class);
        this.startActivity(intentMain);
    }
}
