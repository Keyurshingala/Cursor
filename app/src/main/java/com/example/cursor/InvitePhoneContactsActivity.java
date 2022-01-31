package com.example.cursor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cursor.databinding.ActivityInvitePhoneContactsBinding;

import org.jetbrains.annotations.NotNull;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class InvitePhoneContactsActivity extends BaseActivity {

    ActivityInvitePhoneContactsBinding binding;

    ContactAdapter contactAdapter;

    List<Contact> contactList = new ArrayList<>();

    HashMap<String, String> contactMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        binding = ActivityInvitePhoneContactsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initui();
    }

    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_CONTACTS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (pref.getContactList().size() == 0) {
                    new YourAsyncTask(this).execute();
                } else {
                    contactList = pref.getContactList();
                    showContactList();
                }
            } else {
                tos("Permission denied");
            }
        }
    }

    private void initui() {
        setToolBar();

        if (readContactPermission()) {
            if (pref.getContactList().size() == 0) {
                new YourAsyncTask(this).execute();
            } else {
                contactList = pref.getContactList();
                showContactList();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_PERMISSION);
        }


        binding.til.setEndIconOnClickListener(view -> {
            binding.etSearch.setText("");

            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        });


        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int start, int before, int count) {
                List<Contact> tempList;
                if (cs != null && !cs.toString().trim().equals("")) {
                    tempList = new ArrayList<>();
                    for (int i = 0; i < contactList.size(); i++) {
                        if (contactList.get(i).getName().toLowerCase().contains(String.valueOf(cs).toLowerCase())) {
                            tempList.add(contactList.get(i));
                        }
                    }
                    showContactList(tempList);
                } else {
                    showContactList();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private class YourAsyncTask extends AsyncTask<Void, Void, Void> {

        public YourAsyncTask(InvitePhoneContactsActivity activity) {

        }

        @Override
        protected void onPreExecute() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showProgress();
                }
            });

        }

        @Override
        protected Void doInBackground(Void... args) {
            getContactList();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (progress != null && progress.isShowing()) dismissProgress();
                    showContactList();
                }
            });

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progress != null && progress.isShowing()) dismissProgress();
    }

    private void showContactList() {
        if (contactList.size() == 0) {
            binding.tvEmpty.setVisibility(View.VISIBLE);
        } else {
            binding.tvEmpty.setVisibility(View.GONE);
        }


        contactAdapter = new ContactAdapter(this, contactList);
        binding.rvContact.setLayoutManager(new LinearLayoutManager(this));
        binding.rvContact.setAdapter(contactAdapter);

        contactAdapter.setOnContactClickListener((contact, pos) -> {
            inviteClick(contact);
        });
    }


    private void inviteClick(Contact contact) {

        String msg = "Msg";

        PackageManager packageManager = getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);
        try {
            String url = "https://api.whatsapp.com/send?phone=" + contact.getContact() + "&text=" + URLEncoder.encode(msg, "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));

            if (isWAppInstalled())
                startActivity(i);
            else
                tos("WhatsApp is Not Installed");

        } catch (Exception e) {
            e.printStackTrace();
        }

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

    //For Filter
    private void showContactList(List<Contact> tempList) {

        if (tempList.size() == 0) {
            binding.tvEmpty.setVisibility(View.VISIBLE);
        } else {
            binding.tvEmpty.setVisibility(View.GONE);
        }

        contactAdapter = new ContactAdapter(this, tempList);
        binding.rvContact.setLayoutManager(new LinearLayoutManager(this));
        binding.rvContact.setAdapter(contactAdapter);

        contactAdapter.setOnContactClickListener((contact, pos) -> {
            inviteClick(contact);
        });
    }

    public static class SortByName implements Comparator<Contact> {
        @Override
        public int compare(Contact a, Contact b) {
            return a.getName().compareToIgnoreCase(b.getName());
        }
    }

    @SuppressLint("Range")
    private void getContactList() {
        contactList = new ArrayList<>();
        contactMap = new HashMap<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        try {
            if ((cur != null ? cur.getCount() : 0) > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    Log.i(TAG, "getContactList: " + name);
                    String phoneNo = "";


                    if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);

                        while (pCur.moveToNext()) {
                            int type = pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

                            switch (type) {

                                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                    phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    Log.i(TAG, "getContactList: TYPE_HOME " + name + " " + phoneNo);
                                    setContact(name, phoneNo);
                                    break;
                                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                    phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    Log.i(TAG, "getContactList: TYPE_MOBILE " + name + " " + phoneNo);
                                    setContact(name, phoneNo);
                                    break;
                                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                    phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    Log.i(TAG, "getContactList: TYPE_WORK " + name + " " + phoneNo);
                                    setContact(name, phoneNo);
                                    break;

                                case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:
                                    phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    Log.i(TAG, "getContactList: TYPE_MAIN " + name + " " + phoneNo);
                                    setContact(name, phoneNo);
                                    break;
                                case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                                    phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    Log.i(TAG, "getContactList: TYPE_OTHER " + name + " " + phoneNo);
                                    setContact(name, phoneNo);
                                    break;
                                case ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM:
                                    phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    Log.i(TAG, "getContactList: TYPE_CUSTOM " + name + " " + phoneNo);
                                    setContact(name, phoneNo);
                                    break;

                            }
                        }
                        pCur.close();
                    }
                }
            }
            if (cur != null) {
                cur.close();
            }
            Collections.sort(contactList, new SortByName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        pref.setContactList(contactList);
    }

    void setContact(String name, String phoneNo) {

        if (name == null) name = "";

        if (phoneNo == null) phoneNo = "";

        String s = phoneNo;
        phoneNo = s.replaceAll("[^0-9+]", "").trim();

        phoneNo = phoneNo.replaceAll(" ", "");

        if (!contactMap.containsKey(phoneNo)) {
            contactMap.put(phoneNo, name);
            contactList.add(new Contact(name, phoneNo));
        }
    }

    private void setToolBar() {
        binding.tb.getRoot().setBackgroundColor(getResources().getColor(R.color.black, null));

        binding.tb.ivBack.setOnClickListener(v -> finish());
        binding.tb.tvTitle.setText("My Contacts");

        binding.tb.ivAction.setImageResource(R.drawable.ic_refresh);
        binding.tb.ivAction.setOnClickListener(view -> {
            new YourAsyncTask(this).execute();
        });
    }


}