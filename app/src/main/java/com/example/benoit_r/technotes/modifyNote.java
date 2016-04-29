package com.example.benoit_r.technotes;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class modifyNote extends AppCompatActivity {

    EditText client;
    EditText note;
    EditText tech;
    Notes clickedNote;

    String originalNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_note);

        client = (EditText)findViewById(R.id.clientModifier);
        note = (EditText)findViewById(R.id.noteModifier);
        tech = (EditText)findViewById(R.id.techModifier);

        clickedNote = (Notes)getIntent().getSerializableExtra("note");

        originalNote = clickedNote.getNote();

        client.setText(clickedNote.getClient());
        note.setText(clickedNote.getNote());
        tech.setText(clickedNote.getTech());
    }

    public void cancelModification(View view){

        if(!note.getText().toString().equals(originalNote)) {
            AlertDialog.Builder YesNo = new AlertDialog.Builder(modifyNote.this);
            YesNo.setMessage("Voulez-vous annuler les modifications ?").setCancelable(false)
                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Toast.makeText(getApplicationContext(), "Modifications annulées", Toast.LENGTH_LONG).show();

                            dialog.dismiss();
                            finish();
                        }
                    })
                    .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = YesNo.create();
            alert.setTitle("");
            alert.show();
        } else {
            finish();
        }
        //new AsynCallSoap().execute(String.valueOf(clickedNote.get_id()));

    }

    public void modifyNote(View view)
    {
        //DO NOTHING FOR NOW
        new AsynCallSoap().execute(String.valueOf(clickedNote.get_id()));
    }

    public class AsynCallSoap extends AsyncTask<String, Void, String> {
        private final ProgressDialog dialog = new ProgressDialog(modifyNote.this);

        @Override
        protected void onPreExecute() {
            /*this.dialog.setMessage("Suppression de la note");
            this.dialog.show();*/
        }

        @Override
        protected String doInBackground(final String... params) {
            return params[0];
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);

            final MSSQL mssql = new MSSQL();

            AlertDialog.Builder YesNo = new AlertDialog.Builder(modifyNote.this);
            YesNo.setMessage("Voulez-vous valider les modifications?").setCancelable(false)
                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Date d = Calendar.getInstance().getTime();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh-mm-ss");

                            mssql.updateNote(note.getText().toString(),sdf.format(d),Integer.parseInt(result));

                            Toast.makeText(getApplicationContext(), "Modifications validées", Toast.LENGTH_LONG).show();

                            dialog.dismiss();
                            finish();
                        }
                    })
                    .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = YesNo.create();
            alert.setTitle("");
            alert.show();
        }
    }

}
