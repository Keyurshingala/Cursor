package com.example.cursor;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class AppPreference {

    Gson gson = new Gson();

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sEditor;
    Context context;

    public AppPreference(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("FantasyStock", Context.MODE_PRIVATE);
    }

    public void open_editor() {
        sEditor = sharedPreferences.edit();
    }


    //--------------------- Contact LIST-----------------------------------
    public void setContactList(List<Contact> contactList) {
        open_editor();
        sEditor.putString("Contact", new Gson().toJson(contactList));
        sEditor.commit();
    }

    public List<Contact> getContactList() {
        List<Contact> list = new ArrayList<>();
        try {
            list = new Gson().fromJson(sharedPreferences.getString("Contact", ""), new TypeToken<List<Contact>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list == null ? new ArrayList<>() : list;
    }
    //--------------------- Contact LIST-----------------------------------

    public void removeAll() {
        open_editor();
        sEditor.clear();
        sEditor.commit();
    }
}


