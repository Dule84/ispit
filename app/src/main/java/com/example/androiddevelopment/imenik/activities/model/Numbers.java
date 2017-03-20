package com.example.androiddevelopment.imenik.activities.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by androiddevelopment on 20.3.17..
 */
@DatabaseTable(tableName = Numbers.TABLE_NAME_USERS)
public class Numbers {

    public static final String TABLE_NAME_USERS = "numbers";

    public static final String FIELD_NAME_ID = "id";
    public static final String FIELD_NAME_HOME = "home";
    public static final String FIELD_NAME_MOB = "mob";
    public static final String FIELD_NAME_CONTACT = "contact";

    @DatabaseField(columnName = FIELD_NAME_ID, generatedId = true)
    private int id;

    @DatabaseField(columnName = FIELD_NAME_HOME)
    private String home;

    @DatabaseField(columnName = FIELD_NAME_MOB)
    private String mob;

    @DatabaseField(columnName = FIELD_NAME_CONTACT, foreign = true, foreignAutoRefresh = true)
    private Contact contact;

    public Numbers(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getMob() {
        return mob;
    }

    public void setMob(String mob) {
        this.mob = mob;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
        return home;
    }
}
