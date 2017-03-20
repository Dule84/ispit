package com.example.androiddevelopment.imenik.activities.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by androiddevelopment on 20.3.17..
 */
@DatabaseTable(tableName = Contact.TABLE_NAME_USERS)
public class Contact {

    public static final String TABLE_NAME_USERS = "contact";

    public static final String FIELD_NAME_ID = "id";
    public static final String FIELD_NAME_NAME = "name";
    public static final String FIELD_NAME_SURNAME = "surname";
    public static final String FIELD_NAME_ADDRESS = "address";
    public static final String FIELD_NAME_NUMBER= "number";

    @DatabaseField(columnName = FIELD_NAME_ID, generatedId = true)
    private int id;

    @DatabaseField(columnName = FIELD_NAME_NAME)
    private String name;

    @DatabaseField(columnName = FIELD_NAME_SURNAME)
    private String surname;

    @DatabaseField(columnName = FIELD_NAME_ADDRESS)
    private String address;

    @ForeignCollectionField(columnName = FIELD_NAME_NUMBER, eager = true)
    private ForeignCollection<Numbers> number;

    public Contact(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ForeignCollection<Numbers> getNumber() {
        return number;
    }

    public void setNumber(ForeignCollection<Numbers> number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return name;
    }
}
