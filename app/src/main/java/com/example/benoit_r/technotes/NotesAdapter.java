package com.example.benoit_r.technotes;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by benoit_r on 28/04/2016.
 */
public class NotesAdapter extends ArrayAdapter<Notes> implements Filterable {

    Context context;
    int layoutResourceId;
    ArrayList<Notes> notes = null;
    ArrayList<Notes> newNotes = null;


    ArrayList<Notes> mOriginalValues;
    NotesFilter notesFilter;

    public NotesAdapter(Context context, int layoutResourceId, ArrayList<Notes> notes) {
        super(context, layoutResourceId, notes);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.notes = notes;
        this.newNotes = notes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        NotesHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new NotesHolder();
            holder.listClient= (TextView) row.findViewById(R.id.listClient);
            holder.listDateDayInt = (TextView)row.findViewById(R.id.listDateDayInt);
            holder.listDateDayString = (TextView)row.findViewById(R.id.listDateDayString);
            holder.listDateMonth = (TextView)row.findViewById(R.id.listDateMonth);
            holder.listDateYear = (TextView)row.findViewById(R.id.listDateYear);
            holder.listAuteur= (TextView) row.findViewById(R.id.listAuteur);
            holder.listNote = (TextView)row.findViewById(R.id.listNote);
            holder.important = (View)row.findViewById(R.id.importantIcon);
            holder.photo = (View)row.findViewById(R.id.photoIcon);

            row.setTag(holder);
        }
        else
        {
            holder = (NotesHolder)row.getTag();
        }

        Notes note = newNotes.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh-mm-ss");
        DateTime date = null;

        try {
            date = new DateTime(sdf.parse(note.getNoteDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Date now = new Date();

        DecimalFormat df = new DecimalFormat("00");
        DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE");
        DateTimeFormatter fmtM = DateTimeFormat.forPattern("MMM");

        holder.listClient.setText(note.getClient());
        holder.listAuteur.setText(note.getTech());
        holder.listNote.setText(note.getNote());
        holder.listDateDayInt.setText(df.format(date.getDayOfMonth()));
        holder.listDateDayString.setText(fmt.print(date).replace(".","").toUpperCase());
        holder.listDateMonth.setText(fmtM.print(date).replace(".","").toUpperCase());
        holder.listDateYear.setText(String.valueOf(date.getYear()));
        holder.important.setVisibility(note.getImportant() ? View.VISIBLE : View.INVISIBLE);
        holder.photo.setVisibility(note.getPhoto() ? View.VISIBLE : View.INVISIBLE);

        return row;
    }

    static class NotesHolder
    {
        TextView listClient;
        TextView listDateDayString;
        TextView listDateDayInt;
        TextView listDateMonth;
        TextView listDateYear;
        TextView listNote;
        TextView listAuteur;
        View important;
        View photo;
    }

    @Override
    public int getCount() {
        return newNotes.size();
    }

    @Override
    public Filter getFilter() {
        if (notesFilter == null)
            notesFilter = new NotesFilter();

        return notesFilter;
    }

    private class NotesFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
// We implement here the filter logic
            if (constraint == null || constraint.length() == 0) {
// No filter implemented we return all the list
                results.values = newNotes;
                results.count = newNotes.size();
            }
            else {
// We perform filtering operation
                ArrayList<Notes> nNotesList = new ArrayList<Notes>();

                for (Notes n : notes) {
                    if (n.getClient().toUpperCase().startsWith(constraint.toString().toUpperCase()))
                        nNotesList.add(n);
                }

                results.values = nNotesList;
                results.count = nNotesList.size();

            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,FilterResults results) {
            // Now we have to inform the adapter about the new list filtered
            /*if (results.count == 0)
                notifyDataSetInvalidated();
            else {*/
                newNotes = (ArrayList<Notes>) results.values;
                notifyDataSetChanged();
            //}
        }

    }

}
