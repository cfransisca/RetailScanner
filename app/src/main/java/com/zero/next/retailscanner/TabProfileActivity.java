package com.zero.next.retailscanner;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TabProfileActivity extends Fragment {
    private PrefManager mainPref;
    private SharedPreferences mainSharedPref;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference balanceReference;
    String balanceValue;
    TextView balance, user;
    private String TAG=TabProfileActivity.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_main, container, false);
        user = rootView.findViewById(R.id.tuser);
        balance = rootView.findViewById(R.id.balance);
        mainPref = new PrefManager(getActivity());
        mainSharedPref = getActivity().getSharedPreferences(PrefManager.PREF_NAME, PrefManager.PRIVATE_MODE);
        String prefUserId = (mainSharedPref).getString(PrefManager.USER_ID,"");
        String prefUsername = (mainSharedPref).getString(PrefManager.USER_NAME, "");
        firebaseDatabase = FirebaseDatabase.getInstance();
        balanceReference = firebaseDatabase.getReference("users/"+prefUserId+"/balance");
        balanceReference.keepSynced(true);
        balanceReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                balanceValue = String.valueOf(dataSnapshot.getValue());
                balance.setText(balanceValue);
                mainPref.setBalance(balanceValue);
                Log.d(TAG, "onDataChange: "+balanceValue);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        user.setText(prefUsername);
        mainPref.setGrandTotal("0");
        return rootView;
    }
}
