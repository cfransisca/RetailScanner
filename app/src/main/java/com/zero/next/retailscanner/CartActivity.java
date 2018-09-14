package com.zero.next.retailscanner;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zero.next.retailscanner.adapter.AdapterOrder;
import com.zero.next.retailscanner.data.Order;
import com.zero.next.retailscanner.data.OrderData;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    PrefManager prefManager;
    SharedPreferences sharedPreferences;
    FirebaseDatabase firebase;
    DatabaseReference myRef;
    String id, cartGrandTotal;
    AdapterOrder mAdapter;
    RecyclerView recyclerView;
    TextView total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        prefManager = new PrefManager(this);
        sharedPreferences = getSharedPreferences(PrefManager.PREF_NAME, PrefManager.PRIVATE_MODE);
        firebase = FirebaseDatabase.getInstance();
        id = (sharedPreferences).getString(PrefManager.USER_ID,"");
        cartGrandTotal = (sharedPreferences).getString(PrefManager.GRAND_TOTAL, "");
        myRef = firebase.getReference("cart/"+id+"buydate");
        recyclerView = findViewById(R.id.cartRec);
        total = findViewById(R.id.cartTotal);
        total.setText(cartGrandTotal);
        showData();
    }

    private void showData() {
        final List<Order> listOrder = new ArrayList<>();
        myRef.keepSynced(true);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    OrderData orderdata = child.getValue(OrderData.class);
                    Order order = new Order();
                    order.id = String.valueOf(orderdata.getId());
                    order.idBarang = String.valueOf(orderdata.getIdBarang());
                    order.namaBarang = String.valueOf(orderdata.getNamaBarang());
                    order.jumlah = String.valueOf(orderdata.getJumlah());
                    order.harga = String.valueOf(orderdata.getHarga());
                    listOrder.add(order);
                }
                mAdapter = new AdapterOrder(CartActivity.this, listOrder);
                recyclerView.setAdapter(mAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(CartActivity.this));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Keranjang Belanja akan terhapus. Apakah Anda yakin akan keluar?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        myRef.removeValue();
                        Intent intent = new Intent(CartActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }
}
