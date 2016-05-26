package com.example.benoit_r.technotes;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class modifyNote extends AppCompatActivity {

    EditText client;
    EditText note;
    EditText tech;
    CheckBox important;
    Notes clickedNote;
    ImageView modifyPicture;

    String originalNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_note);

        client = (EditText)findViewById(R.id.clientModifier);
        note = (EditText)findViewById(R.id.noteModifier);
        tech = (EditText)findViewById(R.id.techModifier);
        important = (CheckBox)findViewById(R.id.importantModifier);

        modifyPicture = (ImageView)findViewById(R.id.modifyPicture);

        clickedNote = (Notes)getIntent().getSerializableExtra("note");

        originalNote = clickedNote.getNote();

        client.setText(clickedNote.getClient());
        note.setText(clickedNote.getNote());
        tech.setText(clickedNote.getTech());
        important.setChecked(clickedNote.getImportant());

        if(clickedNote.getPhoto())
            new AsynCallpicture().execute(clickedNote.getIdPhoto());

        //TOOLBAR
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMN);
        setSupportActionBar(toolbar);
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

    public class AsynCallpicture extends AsyncTask<String, Void, String> {
        private final ProgressDialog dialog = new ProgressDialog(modifyNote.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Téléchargement de l'image associé");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(final String... params)
        {
            String base64 = MSSQL.getPicture(params[0]);

            return base64;
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            dialog.dismiss();

            byte[] decodedString = Base64.decode(result, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            int toto = decodedByte.getHeight();

            modifyPicture.setImageBitmap(Bitmap.createScaledBitmap(decodedByte,decodedByte.getWidth()/2,decodedByte.getHeight()/2,true));
        }
    }

    public class AsynCallSoap extends AsyncTask<String, Void, String> {
        private final ProgressDialog dialog = new ProgressDialog(modifyNote.this);

        @Override
        protected void onPreExecute() {
            /*this.dialog.setMessage("Téléchargement de l'image associé");
            this.dialog.show();*/
        }

        @Override
        protected String doInBackground(final String... params) {
            return params[0];
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);

            AlertDialog.Builder YesNo = new AlertDialog.Builder(modifyNote.this);
            YesNo.setMessage("Voulez-vous valider les modifications?").setCancelable(false)
                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Date d = Calendar.getInstance().getTime();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh-mm-ss");

                            MSSQL.updateNote(note.getText().toString(),sdf.format(d),Integer.parseInt(result));

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
