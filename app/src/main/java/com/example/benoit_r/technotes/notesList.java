package com.example.benoit_r.technotes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class notesList extends AppCompatActivity {

    ListView notes;

    private ArrayList<Notes> arrayList;
    private NotesAdapter adapter;
    private boolean onCreateRunned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);

        notes = (ListView)findViewById(R.id.listNotes);

        arrayList = new ArrayList<Notes>();
        adapter = new NotesAdapter(this,R.layout.notesviewlist,arrayList);
        notes.setAdapter(adapter);

        final Intent intentMain = new Intent(this, modifyNote.class);

        //ONITEMCLICK
        notes.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Notes note = (Notes)parent.getItemAtPosition(position);
                //System.out.println(note.getClient());
                intentMain.putExtra("note", note);
                startActivity(intentMain);
            }
        });

        onCreateRunned = true;

        new AsynCallSoap().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                callSearch(query);
                searchView.clearFocus();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
//              if (searchView.isExpanded() && TextUtils.isEmpty(newText)) {
                callSearch(newText);
//              }
                return true;
            }

            public void callSearch(String query) {
                //Do searching
                adapter.getFilter().filter("411"+query);
                //adapter.notifyDataSetChanged();
            }
        });


        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
    }


    @Override
    public void onResume() {
        super.onResume();

        if(onCreateRunned)
            onCreateRunned = false;
        else{
            Intent refresh = new Intent(this, notesList.class);
            startActivity(refresh);
            this.finish();
        }

    }

    public void addNote(View view){
        Intent addNote = new Intent(this, addNote.class);
        //startActivity(addNote);
        startActivityForResult(addNote,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Notes temp = new Notes(data.getStringExtra("Client"),
                    data.getStringExtra("Note"),
                    data.getStringExtra("noteDate"));
            arrayList.add(0, temp);
            adapter.notifyDataSetChanged();
        }
    }


    public class AsynCallSoap extends AsyncTask<String, Void, String> {
        private final ProgressDialog dialog = new ProgressDialog(notesList.this);

        @Override

        protected String doInBackground(String... params) {
            MSSQL mssql = new MSSQL();

            ArrayList<Notes> temp = mssql.getAllNote();

            for (int i = 0; i < temp.size(); i++) {
                arrayList.add(temp.get(i));
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dialog.dismiss();
            adapter.notifyDataSetChanged();
        }
    }
}
