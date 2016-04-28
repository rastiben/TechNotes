package com.example.benoit_r.technotes;

/**
 * Created by benoit_r on 21/04/2016.
 */
public class Client implements java.io.Serializable{

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    private int _id;
    private String client;

    public Client() {

    }

    public Client(int id, String Client){
        this.client = Client;
        this._id = id;

    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    @Override
    public String toString()
    {
        return client;
    }

}
