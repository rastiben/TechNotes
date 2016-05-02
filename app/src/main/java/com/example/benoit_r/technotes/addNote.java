package com.example.benoit_r.technotes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class addNote extends AppCompatActivity {

    public static final String PREFS_NAME = "MyPrefsFile";
    private static final int REQUEST_CODE = 2;
    private Bitmap bitmap;

    EditText client;
    EditText note;
    EditText tech;
    CheckBox important;

    String noteS;
    Boolean checked;

    int idClient;
    int idTech;

    List<String> mTech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        client = (EditText) findViewById(R.id.client);
        note = (EditText) findViewById(R.id.note);
        tech = (EditText) findViewById(R.id.tech);
        important = (CheckBox) findViewById(R.id.Important);
        //dbHandler = new MyDBHandler(this,null,null,1);

        note.setHorizontallyScrolling(false);
        note.setMaxLines(Integer.MAX_VALUE);

        //Affectation du tech
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if (settings.contains("tech") && settings.contains("idTech")) {
            tech.setText(settings.getString("tech", ""));
            idTech = settings.getInt("idTech", 1);
        }

        new getTechSOAP().execute();

    }

    public void addNote(View view) {
        noteS = note.getText().toString();
        checked = important.isChecked();
        new AsynCallSoap().execute();
    }

    public void getCustomer(View view) {
        Intent customer = new Intent(this, Customers.class);
        startActivityForResult(customer, 0);
        //startActivity(customer);
    }

    public void takePicture(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void getTech(View view) {
        final CharSequence[] Techs = mTech.toArray(new String[mTech.size()]);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Technicien");
        dialogBuilder.setItems(Techs, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                tech.setText(Techs[item].toString());
                idTech = item + 1;

                //EDIT TECH
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("tech", Techs[item].toString());
                editor.putInt("idTech", item + 1);

                // Commit the edits!
                editor.commit();
            }
        });
        //Create alert dialog object via builder  
        AlertDialog alertDialogObject = dialogBuilder.create();
        //Show the dialog  
        alertDialogObject.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            idClient = data.getIntExtra("idClient", -1);
            //String Client = data.getStringExtra("Client");
            client.setText(data.getStringExtra("Client"));
            // do something with B's return values
        } else if (resultCode == REQUEST_CODE) {
            InputStream stream = null;
            try {
                // recyle unused bitmaps
                if (bitmap != null) {
                    bitmap.recycle();
                }
                stream = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(stream);

                //addToGallery
                //imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                {
                    if (stream != null)
                        try {
                            stream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
            }
        }
    }

    public class AsynCallSoap extends AsyncTask<String, Void, String> {
        private final ProgressDialog dialog = new ProgressDialog(addNote.this);

        @Override

        protected void onPreExecute() {
            this.dialog.setMessage("Envoi de la note");
            this.dialog.show();
        }

        protected String doInBackground(String... params) {
            Date d = Calendar.getInstance().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh-mm-ss");
            //ALLEZ CHERCHER LE CLIENT

            MSSQL mssql = new MSSQL();
            //int idClient = mssql.addClient(clientS);
            mssql.addNote(noteS, sdf.format(d), idClient, idTech, checked);

            return sdf.format(d);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), "Note ajouté", Toast.LENGTH_LONG).show();

            setResult(Activity.RESULT_OK,
                    new Intent().putExtra("Client", client.getText().toString())
                            .putExtra("Note", note.getText().toString())
                            .putExtra("noteDate", result)
                            .putExtra("important", checked));
            finish();
        }
    }

    public class getTechSOAP extends AsyncTask<String, Void, String> {
        private final ProgressDialog dialog = new ProgressDialog(addNote.this);

        @Override

        protected void onPreExecute() {
            /*this.dialog.setMessage("Envoi de la note");
            this.dialog.show();*/
        }

        protected String doInBackground(String... params) {
            MSSQL mssql = new MSSQL();
            //int idClient = mssql.addClient(clientS);
            mTech = mssql.getTech();

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dialog.dismiss();
        }
    }
}
