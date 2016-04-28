package com.example.benoit_r.technotes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class addNote extends AppCompatActivity {

    EditText client;
    EditText note;

    String noteS;

    int idClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        client = (EditText)findViewById(R.id.client);
        note = (EditText)findViewById(R.id.note);
        //dbHandler = new MyDBHandler(this,null,null,1);
    }

    public void addNote(View view)
    {
        noteS = note.getText().toString();
        new AsynCallSoap().execute();
    }

    public void getCustomer(View view)
    {
        Intent customer = new Intent(this, Customers.class);
        startActivityForResult(customer,0);
        //startActivity(customer);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            idClient = data.getIntExtra("idClient", -1);
            //String Client = data.getStringExtra("Client");
            client.setText(data.getStringExtra("Client"));
            // do something with B's return values
        }
    }

    public class AsynCallSoap extends AsyncTask<String, Void, String>
    {
        private final ProgressDialog dialog = new ProgressDialog(addNote.this);
        @Override

        protected void onPreExecute() {
            this.dialog.setMessage("Envoi de la note");
            this.dialog.show();
        }

        protected String doInBackground(String... params)
        {
            Date d = Calendar.getInstance().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh-mm-ss");
            //ALLEZ CHERCHER LE CLIENT

            MSSQL mssql = new MSSQL();
            //int idClient = mssql.addClient(clientS);
            mssql.addNote(noteS,sdf.format(d),idClient);

            return sdf.format(d);
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), "Note ajouté", Toast.LENGTH_LONG).show();

            setResult(Activity.RESULT_OK,
                    new Intent().putExtra("Client", client.getText().toString())
                            .putExtra("Note",note.getText().toString())
                            .putExtra("noteDate",result));
            finish();
        }
    }
}
