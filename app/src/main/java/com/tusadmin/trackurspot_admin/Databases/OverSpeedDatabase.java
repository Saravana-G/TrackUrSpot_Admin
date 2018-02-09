package com.tusadmin.trackurspot_admin.Databases;

/**
 * Created by KishoreKumar on 29-Jul-16.
 */
public class OverSpeedDatabase {
    String name;
    String date;

    public OverSpeedDatabase(String name, String date){
        this.name = name;
        this.date = date;
    }
    public OverSpeedDatabase(){

    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getDate(){
        return this.date;
    }

}
