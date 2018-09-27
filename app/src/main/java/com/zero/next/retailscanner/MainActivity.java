package com.zero.next.retailscanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;

public class MainActivity extends AppCompatActivity {

    PrefManager mainPref;
    SharedPreferences mainSharedPref;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference balanceReference;
    String balanceValue;
    TextView balance, user;
    private String TAG=MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainPref = new PrefManager(this);
        mainSharedPref = getSharedPreferences(PrefManager.PREF_NAME, PrefManager.PRIVATE_MODE);
        String prefUserId = (mainSharedPref).getString(PrefManager.USER_ID,"");
        String prefUsername = (mainSharedPref).getString(PrefManager.USER_NAME, "");

        setContentView(R.layout.activity_main);
        firebaseDatabase = FirebaseDatabase.getInstance();
        balanceReference = firebaseDatabase.getReference("users/"+prefUserId+"/balance");
        balanceReference.keepSynced(true);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainPref.setGrandTotal("0");
        user = findViewById(R.id.tuser);
        Log.d(TAG, "onCreate: "+balanceValue);
        balance = findViewById(R.id.balance);
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
        /*user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cart = new Intent(MainActivity.this, CartActivity.class);
                cart.putExtra("namatoko","omniubayatenggilis");
                startActivity(cart);
            }
        });*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                /*untuk pindah ke activity scanner*/
                Intent scanner = new Intent(MainActivity.this, Scanner2Activity.class);
                startActivity(scanner);
                //finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
