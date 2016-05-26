package com.example.benoit_r.technotes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
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
    ImageView gallery;

    String noteS;
    Boolean checked;
    String image;
    Uri fileURI;

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
        gallery = (ImageView) findViewById(R.id.gallery);
        //dbHandler = new MyDBHandler(this,null,null,1);

        //TOOLBAR
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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


    /*public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menustart, menu);
        return true;
    }*/

    public void addNote(View view) {
        noteS = note.getText().toString();
        checked = important.isChecked();

        //ENVOIE DE LA PHOTO ET DE LA NOTE
        if(image != null)
            new uploadPhoto().execute();
        else
            new AsynCallSoap().execute("");

    }

    public void getCustomer(View view) {
        Intent customer = new Intent(this, Customers.class);
        startActivityForResult(customer, 0);
        //startActivity(customer);
    }

    public void takePicture(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileURI = getOutputMediaFileUri();
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,fileURI);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CODE);
        }
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
        if (resultCode == Activity.RESULT_OK && requestCode == 0) {
            idClient = data.getIntExtra("idClient", -1);
            //String Client = data.getStringExtra("Client");
            client.setText(data.getStringExtra("Client"));
            // do something with B's return values
        } else if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {

            try {
                Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fileURI);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 40, stream);
                byte[] byteArray = stream.toByteArray();

                image = Base64.encodeToString(byteArray,Base64.DEFAULT);

                gallery.setImageBitmap(Bitmap.createScaledBitmap(bmp,140,200,true));

            } catch(IOException e){

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

            MSSQL.addNote(noteS,
                    sdf.format(d),
                    idClient,
                    idTech,
                    checked,
                    params[0]);

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
                            .putExtra("important", checked)
                            .putExtra("photo",(image == null) ? false : true));
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

            mTech = MSSQL.getTech();

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dialog.dismiss();
        }
    }

    public class uploadPhoto extends AsyncTask<String, Void, String> {
        private final ProgressDialog dialog = new ProgressDialog(addNote.this);

        @Override

        protected void onPreExecute() {
            this.dialog.setMessage("Envoi de la photo");
            this.dialog.show();
        }

        protected String doInBackground(String... params) {

            String result = MSSQL.uploadPhoto(image);

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dialog.dismiss();

            new AsynCallSoap().execute(result);

            Toast.makeText(getApplicationContext(), "Photo envoyé", Toast.LENGTH_LONG).show();
        }
    }


    //IMAGE
    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(){
        return Uri.fromFile(getOutputMediaFile());
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");

        return mediaFile;
    }


}
