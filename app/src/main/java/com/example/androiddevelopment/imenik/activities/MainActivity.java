package com.example.androiddevelopment.imenik.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.androiddevelopment.imenik.R;
import com.example.androiddevelopment.imenik.activities.db.DatabaseHelper;
import com.example.androiddevelopment.imenik.activities.dialog.AboutDialog;
import com.example.androiddevelopment.imenik.activities.model.Contact;
import com.example.androiddevelopment.imenik.activities.preference.Settings;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private SharedPreferences prefs;

    public static String KEY = "KEY";
    public static String NOTIF_TOAST = "notif_toast";
    public static String NOTIF_STATUS = "notif_statis";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        final ListView listView = (ListView) findViewById(R.id.contact_list);

        try{
            List<Contact> list = getDatabaseHelper().getContactDao().queryForAll();

            ListAdapter adapter = new ArrayAdapter<>(MainActivity.this, R.layout.contacts, list);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id){

                    Contact contact = (Contact) listView.getItemAtPosition(position);
                    Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                    intent.putExtra(KEY, contact.getId());
                    startActivity(intent);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();

        refresh();
    }

    private void refresh() {
        ListView listView = (ListView) findViewById(R.id.contact_list);
        if (listView != null) {
            ArrayAdapter<Contact> adapter = (ArrayAdapter<Contact>) listView.getAdapter();

            if (adapter != null) {
                try {
                    adapter.clear();

                    List<Contact> list = getDatabaseHelper().getContactDao().queryForAll();

                    adapter.addAll(list);
                    adapter.notifyDataSetChanged();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showStatusMesage(String message){
        NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.ic_menu_add);
        mBuilder.setContentTitle("Pripremni test");
        mBuilder.setContentText(message);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_menu_add);

        mBuilder.setLargeIcon(bm);
        // notificationID allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());
    }

    public DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.add_contact :
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.add_contact_dialog);

                Button add = (Button) dialog.findViewById(R.id.add_button);
                add.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        EditText name = (EditText) dialog.findViewById(R.id.name);
                        EditText surname = (EditText) dialog.findViewById(R.id.surname);
                        EditText address = (EditText) dialog.findViewById(R.id.address);


                        Contact contact = new Contact();
                        contact.setName(name.getText().toString());
                        contact.setSurname(surname.getText().toString());
                        contact.setAddress(address.getText().toString());


                        try {
                            getDatabaseHelper().getContactDao().create(contact);

                            boolean toast = prefs.getBoolean(NOTIF_TOAST, false);
                            boolean status = prefs.getBoolean(NOTIF_STATUS, false);

                            if (toast) {
                                Toast.makeText(MainActivity.this, "Added new contact", Toast.LENGTH_SHORT).show();
                            }

                            if (status) {
                                showStatusMesage("Added new contact");
                            }

                            refresh();

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
            case R.id.about:

                AlertDialog alertDialog = new AboutDialog(this).prepareDialog();
                alertDialog.show();
                break;
            case R.id.settings:
                startActivity(new Intent(MainActivity.this, Settings.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
