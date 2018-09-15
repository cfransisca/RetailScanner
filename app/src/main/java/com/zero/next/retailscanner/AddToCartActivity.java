package com.zero.next.retailscanner;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zero.next.retailscanner.data.Cart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class AddToCartActivity extends AppCompatActivity {
    private static final String TAG = "scan";
    FirebaseDatabase database;
    DatabaseReference myRef, updateStok;
    TextView namaText, hargaText, totalText;
    EditText qtyText;
    Button btnAddToCart;
    String id, nama, harga, userId, grandTotal, stok, namatoko;
    int total, currentStok;
    PrefManager prefManager;
    SharedPreferences sharedPreferences;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_cart);
        database = FirebaseDatabase.getInstance();
        prefManager = new PrefManager(this);
        sharedPreferences = getSharedPreferences(PrefManager.PREF_NAME, PrefManager.PRIVATE_MODE);
        userId = (sharedPreferences).getString(PrefManager.USER_ID, "");
        Toast.makeText(this,userId,Toast.LENGTH_LONG).show();
        initComponent();

        Intent intent = getIntent();
        String data = intent.getStringExtra("qrResult");
        prosesFbase(data);
        String[] qrList = data.split(",");
        namatoko = qrList[0];
        //nama toko disimpan ke sharedpreferences trus dibandingkan sama atau ga dengan qrlist[0]
    }

    private void initComponent() {
        builder = new AlertDialog.Builder(this);
        namaText = findViewById(R.id.namaTxt);
        hargaText = findViewById(R.id.hargaTxt);
        totalText = findViewById(R.id.totalTxt);
        qtyText = findViewById(R.id.qty);
        qtyText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (i2 > 0) {
                    int hrg = Integer.parseInt(harga);
                    int qty = Integer.parseInt(String.valueOf(qtyText.getText()));
                    total = hrg * qty;
                    totalText.setText(String.valueOf(total));
                    Log.d(TAG, "if");
                } else {
                    totalText.setText("0");
                    Log.d(TAG, "else");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                grandTotal = (sharedPreferences).getString(PrefManager.GRAND_TOTAL,"");
                int grand = Integer.parseInt(grandTotal);
                total += grand;
                prefManager.setGrandTotal(String.valueOf(total));
                Log.d(TAG, "grandTotal: " + total);
                addToCart(id,nama,qtyText.getText().toString(),harga);
            }
        });
    }

    /*private void writeNewUser(String id, String name, String email) {
        User user = new User(name, email);
        userDatabaseRef = userDatabase.getReference("users/"+id);
        userDatabaseRef.setValue(user);
    }*/

    private void addToCart(String idBarang, String namaBarang, String jumlah, String harga) {

        Date now = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
        String buyDate = format.format(now);
        Cart cart = new Cart(buyDate,idBarang,namaBarang,jumlah,harga);
        myRef = database.getReference("cart/"+userId+"/cart"+namatoko+"/"+buyDate+"");
        myRef.setValue(cart);
        builder.setTitle("Omni")
                .setMessage("Barang berhasil ditambahkan ke Keranjang Belanja")
                .setPositiveButton("Scan Lagi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(AddToCartActivity.this, ScannerActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Bayar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(AddToCartActivity.this, CartActivity.class);
                        intent.putExtra("namatoko",namatoko);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
        currentStok = Integer.parseInt(stok) - Integer.parseInt(jumlah);
        updateStok = database.getReference("barang/"+id+"/stok");
        updateStok.setValue(String.valueOf(currentStok));
        //Toast.makeText(this, buyDate, Toast.LENGTH_LONG).show();
    }

    private void prosesFbase(String contents) {
        myRef = database.getReference("barang/"+contents); //omni-ubaya/barang/sasa
        myRef.keepSynced(true);
        /*Read from the database*/
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                /*cart*/
                /*for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Product dummy = child.getValue(Product.class);
                    String nama = String.valueOf(dummy.getNama());
                    String harga = String.valueOf(dummy.getHarga());
                }*/
                Product dummy = dataSnapshot.getValue(Product.class);
                id = String.valueOf(dummy.getId());
                nama = String.valueOf(dummy.getNama());
                harga = String.valueOf(dummy.getHarga());
                namaText.setText(nama);
                hargaText.setText(harga);
                stok = String.valueOf(dummy.getStok());

                /*String value = dataSnapshot.getValue(String.class);*/
                /*Log.d(TAG, "Value is: " + value);*/
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah Anda yakin akan keluar?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        myRef.removeValue();
//                        Intent intent = new Intent(AddToCartActivity.this, MainActivity.class);
//                        startActivity(intent);
//                        finish();
                        AddToCartActivity.super.onBackPressed();
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
