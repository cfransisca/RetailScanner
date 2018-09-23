package com.zero.next.retailscanner;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zero.next.retailscanner.adapter.AdapterOrder;
import com.zero.next.retailscanner.adapter.HandleUpdateClickListener;
import com.zero.next.retailscanner.adapter.ItemClickListener;
import com.zero.next.retailscanner.data.Order;
import com.zero.next.retailscanner.data.OrderData;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements ItemClickListener {

    PrefManager prefManager;
    SharedPreferences sharedPreferences;
    FirebaseDatabase firebase;
    DatabaseReference myRef;
    String id, cartpath, namatoko, isiqr, currentstok;
    AdapterOrder mAdapter;
    RecyclerView recyclerView;
    TextView settotal;
    int cartGrandTotal, stok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        prefManager = new PrefManager(this);
        sharedPreferences = getSharedPreferences(PrefManager.PREF_NAME, PrefManager.PRIVATE_MODE);
        firebase = FirebaseDatabase.getInstance();
        id = (sharedPreferences).getString(PrefManager.USER_ID,"");
        cartGrandTotal = Integer.parseInt ((sharedPreferences).getString(PrefManager.GRAND_TOTAL, ""));
        Intent getIntent = getIntent();
        namatoko = getIntent.getStringExtra("namatoko");
        isiqr = getIntent.getStringExtra("qr");
        myRef = firebase.getReference("cart/"+id+"/cart"+namatoko);
        cartpath = "cart/"+id+"/cart"+namatoko+"/";
        recyclerView = findViewById(R.id.cartRec);
        settotal = findViewById(R.id.cartTotal);
        settotal.setText(String.valueOf(cartGrandTotal));
        showData();
        firebaseStok();
    }

    private void firebaseStok() {
        DatabaseReference refStok = firebase.getReference("barang/"+isiqr+"/stok");
        refStok.keepSynced(true);
        refStok.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                currentstok = String.valueOf(dataSnapshot.getValue());
                Log.d("stok", "onDataChange: "+currentstok);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
                    order.qr = String.valueOf(orderdata.getQr());
                    listOrder.add(order);

                }
                mAdapter = new AdapterOrder(CartActivity.this, listOrder);
                mAdapter.onClickInterface(CartActivity.this);

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



    @Override
    public void onClick(View view, String tag, String jumlahAwal, String jumlahAkhir, String harga, String path, String qr) {
//        Log.d("handleClick",data+" "+cartpath+path);
//        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
        if(tag.equals("ubah")) {

            DatabaseReference refUpdate = firebase.getReference("barang/"+qr+"/stok");
            refUpdate.keepSynced(true);
            if (Integer.parseInt(jumlahAkhir) > Integer.parseInt(jumlahAwal)) {
                stok = Integer.parseInt(currentstok) - (Integer.parseInt(jumlahAkhir) - Integer.parseInt(jumlahAwal));
                cartGrandTotal = cartGrandTotal + ((Integer.parseInt(jumlahAkhir) - Integer.parseInt(jumlahAwal)) * Integer.parseInt(harga));
                settotal.setText(String.valueOf(cartGrandTotal));
                prefManager.setGrandTotal(String.valueOf(cartGrandTotal));

                refUpdate.setValue(String.valueOf(stok));
            } else if (Integer.parseInt(jumlahAkhir) < Integer.parseInt(jumlahAwal)) {
                stok = Integer.parseInt(currentstok) + (Integer.parseInt(jumlahAwal) - Integer.parseInt(jumlahAkhir));
                cartGrandTotal = cartGrandTotal - ((Integer.parseInt(jumlahAwal) - Integer.parseInt(jumlahAkhir)) * Integer.parseInt(harga));
                settotal.setText(String.valueOf(cartGrandTotal));
                prefManager.setGrandTotal(String.valueOf(cartGrandTotal));

                refUpdate.setValue(String.valueOf(stok));
            } else {

            }
//
            DatabaseReference refJumlah = firebase.getReference(cartpath+path);


            refJumlah.keepSynced(true);
            refJumlah.setValue(jumlahAkhir);

            showData();
        } else if(tag.equals("hapus")) {

            cartGrandTotal = cartGrandTotal - (Integer.parseInt(jumlahAwal)*Integer.parseInt(harga));
            settotal.setText(String.valueOf(cartGrandTotal));
            prefManager.setGrandTotal(String.valueOf(cartGrandTotal));
            DatabaseReference refHapus = firebase.getReference(cartpath+path);
            DatabaseReference refUpdate = firebase.getReference("barang/"+qr+"/stok");
            refUpdate.keepSynced(true);
            stok = Integer.parseInt(currentstok) + Integer.parseInt(jumlahAwal);
            refHapus.removeValue();
            Log.d("stok", String.valueOf(stok));
            refUpdate.setValue(String.valueOf(stok));
            showData();
        }

    }
}
