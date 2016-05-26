package com.example.benoit_r.technotes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Customers extends AppCompatActivity {

    private ArrayList<Notes> arrayList;
    private ArrayAdapter adapter;
    private ListView customers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);

        customers = (ListView)findViewById(R.id.customers);

        arrayList = new ArrayList<Notes>();
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,arrayList);
        customers.setAdapter(adapter);

        new AsynCallSoap().execute();

        final Intent intentMain = new Intent(this, modifyNote.class);

        customers.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Client client = (Client)parent.getItemAtPosition(position);
                setResult(Activity.RESULT_OK,
                        new Intent().putExtra("idClient", client.get_id()).putExtra("Client", client.getClient()));
                finish();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
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

    public class AsynCallSoap extends AsyncTask<String, Void, String> {
        private final ProgressDialog dialog = new ProgressDialog(Customers.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Récupération des tiers");
            this.dialog.show();
        }

        protected String doInBackground(String... params) {

            ArrayList<Notes> temp = MSSQL.getAllCustomers();

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
