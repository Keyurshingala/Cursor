package com.example.cursor;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import com.example.cursor.databinding.PgDialogBinding;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


//API key
//AIzaSyCj6I72noFLa_9t4aqXDd6ZgeQononVHJs
public class BaseActivity extends AppCompatActivity {

    public static final int READ_CONTACTS_PERMISSION = 1;
    public static final int CASH_ADDED = 202;
    public static final int JOIN_CONTEST = 101;

    public static final int WRITE_EXTERNAL_STORAGE = 111;


    public Gson g = new Gson();
    public static final String TAG = "TAGZ";

    AppPreference pref;

    Dialog progress;
    PgDialogBinding pgDialogBinding;

    boolean isNet;


    private boolean tknStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);


        g = new Gson();
        pref = new AppPreference(this);

        initDialog();
    }

    public void initDialog() {
        progress = new Dialog(this);
        pgDialogBinding = PgDialogBinding.inflate(getLayoutInflater());
        progress.setContentView(pgDialogBinding.getRoot());
        int width = WindowManager.LayoutParams.MATCH_PARENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        progress.getWindow().setLayout(width, height);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
    }

    public void showProgress() {
        if (progress != null)
            progress.show();
        else {
            initDialog();
            progress.show();
        }
    }

    public void dismissProgress() {
        if (progress != null && progress.isShowing())
            progress.dismiss();
    }

    public boolean checkWritePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }


    public boolean isWAppInstalled() {
        boolean isInstalled;
        try {
            getPackageManager().getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            isInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            isInstalled = false;
        }
        return isInstalled;
    }



    //------------------- for count down -------------------
    public long getToday9am() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(getDateToday() + " 09:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public long getToday12am() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(getDateToday() + " 00:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public String getDateToday() {
        return DateFormat.format("yyyy/MM/dd", new Date(System.currentTimeMillis())).toString();
    }

    public int getHour24(long second) {
        return Integer.parseInt(DateFormat.format("HH", new Date(second)).toString());
    }
    //------------------- for count down -------------------



    public boolean hasInternetConnect() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }

        if (haveConnectedWifi || haveConnectedMobile) {

        } else {
            tos("Network Not Available");
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public boolean readContactPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    public String getTxnDate(long l) {
        return DateFormat.format("dd MMMM, yyyy", new Date(l)).toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (progress != null) {
            if (progress.isShowing()) {
                progress.dismiss();
                Log.i(TAG, "onDestroy: ");
            }
        }

    }

    public void tos(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public void tosL(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

}