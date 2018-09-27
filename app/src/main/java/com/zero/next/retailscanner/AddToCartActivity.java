package com.zero.next.retailscanner;

import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
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
    Button btnAddToCart, btnCancel;
    String id, nama, harga, userId, grandTotal, stok, namatoko, data;
    int total, currentStok;
    PrefManager prefManager;
    SharedPreferences sharedPreferences;
    AlertDialog.Builder builder;
    InputMethodManager imm;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_cart);
        database = FirebaseDatabase.getInstance();
        prefManager = new PrefManager(this);

        sharedPreferences = getSharedPreferences(PrefManager.PREF_NAME, PrefManager.PRIVATE_MODE);
        userId = (sharedPreferences).getString(PrefManager.USER_ID, "");

        grandTotal = (sharedPreferences).getString(PrefManager.GRAND_TOTAL,"");
        Toast.makeText(this,userId,Toast.LENGTH_LONG).show();
        /*imm = (InputMethodManager)AddToCartActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);*/
        initComponent();

        Intent intent = getIntent();
        data = intent.getStringExtra("qrResult");
        prosesFbase(data);
        String[] qrList = data.split(",");
        namatoko = qrList[0];
        prefManager.setToko(namatoko);
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
        btnCancel = findViewById(R.id.btnCancelAddtoCart);
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int grand = Integer.parseInt(grandTotal);
                total += grand;
                prefManager.setGrandTotal(String.valueOf(total));
                Log.d(TAG, "grandTotal: " + total);
                if(!qtyText.getText().toString().equals("")){
                addToCart(id,nama,qtyText.getText().toString(),harga,data);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddToCartActivity.this);
                    builder.setMessage("Jumlah barang belum diisi. Silahkan isi terlebih dahulu.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    qtyText.requestFocus();
                                }
                            })
                            .show();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Integer.parseInt(grandTotal)==0){
                    Intent goBack = new Intent(AddToCartActivity.this,Main2Activity.class);
                    startActivity(goBack);
                    finish();
                } else {
                    Intent backToCart = new Intent(AddToCartActivity.this, CartActivity.class);
                    backToCart.putExtra("namatoko",namatoko);
                    backToCart.putExtra("qr",data);
                    startActivity(backToCart);
                    finish();
                }
            }
        });

    }

    /*private void writeNewUser(String id, String name, String email) {
        User user = new User(name, email);
        userDatabaseRef = userDatabase.getReference("users/"+id);
        userDatabaseRef.setValue(user);
    }*/

    private void addToCart(String idBarang, String namaBarang, String jumlah, String harga, String qr) {
        if(Integer.parseInt(stok)<Integer.parseInt(jumlah)){
            //muncul alert stok kurang dari inputan
        } else {
            //jalankan semua fungsi yg dibawah ini
        }
        Date now = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
        String buyDate = format.format(now);
        //penyimpanan ke dalam firebase
        Cart cart = new Cart(buyDate,idBarang,namaBarang,jumlah,harga,qr);
        myRef = database.getReference("cart/"+userId+"/cart"+namatoko+"/"+buyDate+"");
        myRef.setValue(cart);
        //update stok firebase
        currentStok = Integer.parseInt(stok) - Integer.parseInt(jumlah);
        updateStok = database.getReference("barang/"+id+"/stok");
        updateStok.setValue(String.valueOf(currentStok));
        builder.setTitle(namatoko)
                .setMessage("Barang berhasil ditambahkan ke Keranjang Belanja")
                .setPositiveButton("Scan Lagi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(AddToCartActivity.this, Scanner2Activity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Bayar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //pindah halaman ke keranjang belanja
                        Intent intent = new Intent(AddToCartActivity.this, CartActivity.class);
                        intent.putExtra("namatoko",namatoko);
                        intent.putExtra("qr",data);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();

        //Toast.makeText(this, buyDate, Toast.LENGTH_LONG).show();
    }

    private void prosesFbase(String contents) {
        myRef = database.getReference("barang/"+contents); //omniubaya/barang/sasa
        myRef.keepSynced(true);
        /*Read from the database*/
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                Product dummy = dataSnapshot.getValue(Product.class);
                id = String.valueOf(dummy.getId());
                nama = String.valueOf(dummy.getNama());
                harga = String.valueOf(dummy.getHarga());
                namaText.setText(nama);
                hargaText.setText(harga);
                stok = String.valueOf(dummy.getStok());


//                Log.d(TAG, "Value is: " + dataSnapshot);
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
