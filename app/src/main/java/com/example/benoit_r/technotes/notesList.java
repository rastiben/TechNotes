package com.example.benoit_r.technotes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class notesList extends AppCompatActivity {

    ListView notes;
    SwipeRefreshLayout mSwipeRefreshLayout;

    private ArrayList<Notes> arrayList;
    private NotesAdapter adapter;
    private boolean onCreateRunned = false;

    View blackLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);

        notes = (ListView)findViewById(R.id.listNotes);
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe);
        blackLine = (View)findViewById(R.id.blackLine);

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

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                     @Override
                                                     public void onRefresh() {
                                                         new AsynCallSoap().execute();
                                                     }
                                                 });

        new AsynCallSoap().execute();
    }

    public boolean demandeDevis(MenuItem item){
        Intent intent = new Intent(this,demandeDevis.class);
        startActivity(intent);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menustart, menu);
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
            temp.setImportant(data.getBooleanExtra("important",false));
            temp.setPhoto(data.getBooleanExtra("photo",false));
            arrayList.add(0, temp);
            adapter.notifyDataSetChanged();
        }
    }


    public class AsynCallSoap extends AsyncTask<String, Void, String> {
        private final ProgressDialog dialog = new ProgressDialog(notesList.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Récupération des notes");
            this.dialog.show();
        }

        protected String doInBackground(String... params) {

            arrayList.clear();

            ArrayList<Notes> temp = MSSQL.getAllNote();

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
            blackLine.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
