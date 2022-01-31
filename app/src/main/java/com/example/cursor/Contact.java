package com.example.cursor;

public class Contact {
    String name;
    String contact;

    public Contact() {
    }

    public Contact(String name, String contact) {
        this.name = name;
        this.contact = contact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        if (!contact.startsWith("+")) {
            contact = "+91" + contact;
        }
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
