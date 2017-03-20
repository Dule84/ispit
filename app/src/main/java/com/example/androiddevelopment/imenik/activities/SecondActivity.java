package com.example.androiddevelopment.imenik.activities;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.androiddevelopment.imenik.R;
import com.example.androiddevelopment.imenik.activities.db.DatabaseHelper;
import com.example.androiddevelopment.imenik.activities.model.Contact;
import com.example.androiddevelopment.imenik.activities.model.Numbers;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by androiddevelopment on 20.3.17..
 */

public class SecondActivity extends AppCompatActivity {

    private Contact contact;
    private EditText name;
    private EditText surname;
    private EditText address;

    private DatabaseHelper databaseHelper;
    private SharedPreferences prefs;

    public static String KEY = "KEY";
    public static String NOTIF_TOAST = "notif_toast";
    public static String NOTIF_STATUS = "notif_statis";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int key = getIntent().getExtras().getInt(MainActivity.KEY);

        try {
            contact = getDatabaseHelper().getContactDao().queryForId(key);

            name = (EditText) findViewById(R.id.name);
            surname = (EditText) findViewById(R.id.surname);
            address = (EditText) findViewById(R.id.address);

            name.setText(contact.getName());
            surname.setText(contact.getSurname());
            address.setText(contact.getAddress());
        } catch (SQLException e) {
            e.printStackTrace();
        }


        final ListView listView = (ListView) findViewById(R.id.numbers_list);

        try {
            List<Numbers> list = getDatabaseHelper().getNumbersDao().queryBuilder()
                    .where()
                    .eq(Numbers.FIELD_NAME_CONTACT, contact.getId())
                    .query();

            ListAdapter adapter = new ArrayAdapter<>(this, R.layout.numbers, list);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Numbers number = (Numbers) listView.getItemAtPosition(position);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        refresh();
    }

    private void refresh() {
        ListView listView = (ListView) findViewById(R.id.numbers_list);
        if (listView != null) {
            ArrayAdapter<Numbers> adapter = (ArrayAdapter<Numbers>) listView.getAdapter();

            if (adapter != null) {
                try {
                    adapter.clear();

                    List<Numbers> list = getDatabaseHelper().getNumbersDao().queryBuilder().
                            where().
                            eq(Numbers.FIELD_NAME_CONTACT, contact.getId()).
                            query();

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.add_number :
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.add_number_dialog);

                final Spinner spinner = (Spinner) dialog.findViewById(R.id.home);

                List<String> list = new ArrayList<String>();
                list.add("Home");
                list.add("Mob");
                ArrayAdapter<String> list_home = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
                spinner.setAdapter(list_home);
                spinner.setSelection(0);

                Button add = (Button) dialog.findViewById(R.id.add_number_button);
                add.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        EditText home = (EditText) dialog.findViewById(R.id.number);
                        String mob = (String) spinner.getSelectedItem();

                        Numbers number = new Numbers();
                        number.setHome(home.getText().toString());
                        number.setMob(mob);
                        number.setContact(contact);

                        try {
                            getDatabaseHelper().getNumbersDao().create(number);

                            boolean toast = prefs.getBoolean(NOTIF_TOAST, false);
                            boolean status = prefs.getBoolean(NOTIF_STATUS, false);

                            if (toast){
                                Toast.makeText(SecondActivity.this, "Added new number", Toast.LENGTH_SHORT).show();
                            }

                            if (status){
                                showStatusMesage("Added new number");
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
            case R.id.edit_contact:
                contact.setName(name.getText().toString());
                contact.setSurname(surname.getText().toString());
                contact.setAddress(address.getText().toString());

                try {
                    getDatabaseHelper().getContactDao().update(contact);

                    boolean toast = prefs.getBoolean(NOTIF_TOAST, false);
                    boolean status = prefs.getBoolean(NOTIF_STATUS, false);

                    if (toast){
                        Toast.makeText(SecondActivity.this, "Contact updated", Toast.LENGTH_SHORT).show();
                    }

                    if (status){
                        showStatusMesage("Contact updated");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.delete_contact :
                try{
                    getDatabaseHelper().getContactDao().delete(contact);

                    boolean toast = prefs.getBoolean(NOTIF_TOAST, false);
                    boolean status = prefs.getBoolean(NOTIF_STATUS, false);

                    if (toast){
                        Toast.makeText(SecondActivity.this, "Contact deleted", Toast.LENGTH_SHORT).show();
                    }

                    if (status){
                        showStatusMesage("Contact deleted");
                    }

                    finish();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
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
}
