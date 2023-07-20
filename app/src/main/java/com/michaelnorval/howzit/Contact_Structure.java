package com.michaelnorval.howzit;

public class Contact_Structure {
    private String ContactName;
    private String ContactNumber;
    private String ContactExtra;

    public Contact_Structure() {
    }

    public Contact_Structure(String ContactName, String ContactNumber, String Extra) {
        this.ContactName = ContactName;
        this.ContactNumber = ContactNumber;
        this.ContactExtra = ContactExtra;
    }

    public String getContactName() {
        return ContactName;
    }

    public void setContactName(String name) {
        this.ContactName = name;
    }

    public String getContactExtra() {
        return ContactExtra;
    }

    public void setContactExtra(String ContactExtra) {
        this.ContactExtra = ContactExtra;
    }

    public String getContactNumber() {
        return ContactNumber;
    }

    public void setContactNumber(String ContactNumber) {
        this.ContactNumber = ContactNumber;
    }
}

