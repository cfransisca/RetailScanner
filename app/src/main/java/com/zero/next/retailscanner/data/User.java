package com.zero.next.retailscanner.data;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by cicak on 12/05/2018.
 */

@IgnoreExtraProperties
public class User {
    public  String id;
    public String username;
    public String email;
    public String balance;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String id, String username, String email, String balance) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.balance = balance;
    }


}
