package com.example.benoit_r.technotes;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by benoit_r on 22/04/2016.
 */
public class MSSQL {

    private static final String SOAP_ACTION_AddNote = "http://tempuri.org/addNote";
    private static final String METHOD_NAME_addNote = "addNote";

    private static final String SOAP_ACTION_getAllNotes = "http://tempuri.org/getAllNote";
    private static final String METHOD_NAME_getAllNotes = "getAllNote";

    private static final String SOAP_ACTION_Customers = "http://tempuri.org/Customers";
    private static final String METHOD_NAME_Customers = "Customers";

    private static final String SOAP_ACTION_updateNote = "http://tempuri.org/updateNote";
    private static final String METHOD_NAME_updateNote = "updateNote";

    private static final String NAMESPACE = "http://tempuri.org/";
    private static final String URL = "http://192.168.0.66/Site/NotesService.asmx";

    public MSSQL()
    {

    }

    public void addNote(String note,String date,int idClient,int idTech)
    {
        try {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_addNote);

            request.addProperty("note", note);
            request.addProperty("date", date);
            request.addProperty("idClient", idClient);
            request.addProperty("idTech", idTech);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet=true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            androidHttpTransport.call(SOAP_ACTION_AddNote, envelope);

            Object result = (Object)envelope.getResponse();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateNote(String note,String date,int idNote)
    {
        try {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_updateNote);

            request.addProperty("note", note);
            request.addProperty("date", date);
            request.addProperty("idNote", idNote);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet=true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            androidHttpTransport.call(SOAP_ACTION_updateNote, envelope);

            Object result = (Object)envelope.getResponse();

        } catch (Exception e) {
            //System.out.println(e.getMessage());
        }
    }

    public ArrayList getAllCustomers()
    {
        ArrayList notes = new ArrayList();

        try {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_Customers);

            //request.addProperty("client", client);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet=true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            androidHttpTransport.call(SOAP_ACTION_Customers, envelope);

            SoapObject result = (SoapObject)envelope.getResponse();

            int count = result.getPropertyCount();

            for(int i=0;i<count;i++){
                String[] actualNotes = result.getProperty(i).toString().split(";");

                Client temp = new Client(Integer.parseInt(actualNotes[0]),
                        actualNotes[1]);

                notes.add(temp);
            }
            //return notes;
        } catch (Exception e) {
            String toto = e.getMessage();
            //return null;
        } finally {
            return notes;
        }
    }

    public ArrayList getAllNote()
    {
        ArrayList notes = new ArrayList();

        try {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_getAllNotes);

            //request.addProperty("client", client);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet=true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            androidHttpTransport.call(SOAP_ACTION_getAllNotes, envelope);

            SoapObject result = (SoapObject)envelope.getResponse();

            int count = result.getPropertyCount();

            for(int i=count-1;i>=0;i--){
                String[] actualNotes = result.getProperty(i).toString().split(";");

                Notes temp = new Notes();
                temp.set_id(Integer.parseInt(actualNotes[0]));
                temp.setNote(actualNotes[1]);
                temp.setClient(actualNotes[2]);
                temp.setNoteDate(actualNotes[3]);
                temp.setTech(actualNotes[4]);

                notes.add(temp);
            }
            //return notes;
        } catch (Exception e) {
            String toto = e.getMessage();
            //return null;
        } finally {
            return notes;
        }
    }

}
