package com.example.benoit_r.technotes;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.HttpsTransportSE;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Created by benoit_r on 22/04/2016.
 */
public class MSSQL {

    private static final String HOST = "192.168.0.66";
    private static final String WS_OPS = "/NotesService.asmx";

    private static final String SOAP_ACTION_AddNote = "http://tempuri.org/addNote";
    private static final String METHOD_NAME_addNote = "addNote";

    private static final String SOAP_ACTION_getAllNotes = "http://tempuri.org/getAllNote";
    private static final String METHOD_NAME_getAllNotes = "getAllNote";

    private static final String SOAP_ACTION_Customers = "http://tempuri.org/Customers";
    private static final String METHOD_NAME_Customers = "Customers";

    private static final String SOAP_ACTION_updateNote = "http://tempuri.org/updateNote";
    private static final String METHOD_NAME_updateNote = "updateNote";

    private static final String SOAP_ACTION_getTech = "http://tempuri.org/getTech";
    private static final String METHOD_NAME_getTech = "getTech";

    private static final String SOAP_ACTION_uploadPhoto = "http://tempuri.org/uploadPhoto";
    private static final String METHOD_NAME_uploadPhoto = "uploadPhoto";

    private static final String SOAP_ACTION_getPicture = "http://tempuri.org/getPicture";
    private static final String METHOD_NAME_getPicture = "getPicture";

    private static final String NAMESPACE = "http://tempuri.org/";
    private static final String URL = "https://192.168.0.66/NotesService.asmx";

    public static String getPicture(String id)
    {
        String base64 = "";
        try {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_getPicture);

            request.addProperty("id", id);

            SoapSerializationEnvelope envelope = MSSQL.createEnvelope(request,SOAP_ACTION_getPicture);

            Object result = (Object)envelope.getResponse();

            base64 = result.toString();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            return base64;
        }
    }

    public static String uploadPhoto(String image)
    {

        String id = "";
        try {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_uploadPhoto);

            request.addProperty("image", image);

            SoapSerializationEnvelope envelope = MSSQL.createEnvelope(request,SOAP_ACTION_uploadPhoto);

            Object result = (Object)envelope.getResponse();

            id = result.toString();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            return id;
        }
    }

    public static void addNote(String note,String date,int idClient,int idTech,Boolean important,String photo)
    {
        try {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_addNote);

            request.addProperty("note", note);
            request.addProperty("date", date);
            request.addProperty("idClient", idClient);
            request.addProperty("idTech", idTech);
            request.addProperty("important", important);
            if(photo != "")
                request.addProperty("photo",photo);

            SoapSerializationEnvelope envelope = MSSQL.createEnvelope(request,SOAP_ACTION_AddNote);

            Object result = (Object)envelope.getResponse();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void updateNote(String note,String date,int idNote)
    {
        try {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_updateNote);

            request.addProperty("note", note);
            request.addProperty("date", date);
            request.addProperty("idNote", idNote);

            SoapSerializationEnvelope envelope = MSSQL.createEnvelope(request,SOAP_ACTION_updateNote);

            Object result = (Object)envelope.getResponse();

        } catch (Exception e) {
            //System.out.println(e.getMessage());
        }
    }

    public static ArrayList getAllCustomers()
    {
        ArrayList notes = new ArrayList();

        try {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_Customers);

            SoapSerializationEnvelope envelope = MSSQL.createEnvelope(request,SOAP_ACTION_Customers);

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

    public static ArrayList getAllNote()
    {
        ArrayList notes = new ArrayList();

        try {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_getAllNotes);

            SoapSerializationEnvelope envelope = MSSQL.createEnvelope(request,SOAP_ACTION_getAllNotes);

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
                temp.setImportant(Boolean.valueOf(actualNotes[5]));
                temp.setPhoto(actualNotes[6].toString().equals("0") ? false : true);
                temp.setIdPhoto(actualNotes[6]);

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

    public static List<String> getTech()
    {
        List<String> mTech = new ArrayList<String>();

        try {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_getTech);

            SoapSerializationEnvelope envelope = MSSQL.createEnvelope(request,SOAP_ACTION_getTech);

            SoapObject result = (SoapObject)envelope.getResponse();

            int count = result.getPropertyCount();

            for(int i=0;i<count;i++){
                mTech.add(result.getProperty(i).toString());
            }
            //return notes;
        } catch (Exception e) {
            String toto = e.getMessage();
            //return null;
        } finally {
            return mTech;
        }
    }


    private static SoapSerializationEnvelope createEnvelope(SoapObject request,String action){

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet=true;
        envelope.setOutputSoapObject(request);

        HttpsTransportSE androidHttpTransport = new HttpsTransportSE(HOST,443,WS_OPS,2000);
        androidHttpTransport.debug = true;
        SslRequest.allowAllSSL();

        try {
            androidHttpTransport.call(action, envelope);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return envelope;
    }

}
