package com.example.androiddevelopment.imenik.activities.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.androiddevelopment.imenik.activities.model.Contact;
import com.example.androiddevelopment.imenik.activities.model.Numbers;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by androiddevelopment on 20.3.17..
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "imenik.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<Contact, Integer> contactDao = null;
    private Dao<Numbers, Integer> numbersDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Contact.class);
            TableUtils.createTable(connectionSource, Numbers.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Contact.class, true);
            TableUtils.dropTable(connectionSource, Numbers.class, true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Dao<Contact, Integer> getContactDao() throws SQLException {
        if (contactDao == null) {
            contactDao = getDao(Contact.class);
        }
        return contactDao;
    }

    public Dao<Numbers, Integer> getNumbersDao() throws SQLException {
        if (numbersDao == null) {
            numbersDao = getDao(Contact.class);
        }
        return numbersDao;
    }

    @Override
    public void close() {
        contactDao = null;
        numbersDao = null;

        super.close();
    }
}
