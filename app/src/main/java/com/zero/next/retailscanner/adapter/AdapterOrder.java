package com.zero.next.retailscanner.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zero.next.retailscanner.AddToCartActivity;
import com.zero.next.retailscanner.CartActivity;
import com.zero.next.retailscanner.R;
import com.zero.next.retailscanner.ScannerActivity;
import com.zero.next.retailscanner.data.Order;

import java.util.Collections;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by cicak on 13/05/2018.
 */

public class AdapterOrder extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //public static HandleUpdateClickListener handle;
    ItemClickListener itemClickListener;
    private Context context;
    List<Order> listOrder = Collections.emptyList();
    private LayoutInflater inflater;
    int currentPost = 0;
    Order currentOrder;


    public AdapterOrder(Context context, List<Order> listOrder/*, ItemClickListener itemClickListener*/) {
        this.context = context;
        this.listOrder = listOrder;
        //this.itemClickListener = itemClickListener

        inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_order_layout,parent,false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyHolder myHolder = (MyHolder)holder;
        Order dataOrder = listOrder.get(position);
        myHolder.nama.setText(dataOrder.namaBarang);
        myHolder.harga.setText(dataOrder.harga);
        myHolder.qty.setText(dataOrder.jumlah);
        myHolder.idcart.setText(dataOrder.id);
        int tempHarga = Integer.parseInt(dataOrder.harga);
        int tempJumlah = Integer.parseInt(dataOrder.jumlah);
        int total = tempHarga * tempJumlah;
        /*int total = Integer.parseInt(myHolder.harga.getText().toString()) * Integer.parseInt(myHolder.qty.getText().toString());
        */
        myHolder.grandTotal.setText(String.valueOf(total));
    }

    @Override
    public int getItemCount() {
        return listOrder.size();
    }

    public void onClickInterface(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    class MyHolder extends RecyclerView.ViewHolder {

        TextView nama, harga, qty, grandTotal,idcart;
        ImageView gambar;
        ImageButton editjumlah;
        Dialog dialog;
        FirebaseDatabase firebaseDatabase;
        DatabaseReference myRef;
        String sIdCart;

        public MyHolder(View itemView) {
            super(itemView);
            firebaseDatabase = FirebaseDatabase.getInstance();
            idcart = itemView.findViewById(R.id.cartpath);
            nama = itemView.findViewById(R.id.namaItem);
            gambar = itemView.findViewById(R.id.gbrItem);
            harga = itemView.findViewById(R.id.hrgItem);
            qty = itemView.findViewById(R.id.qtyItem);
            grandTotal = itemView.findViewById(R.id.grandTotal);
            editjumlah = itemView.findViewById(R.id.editjumlah);
            editjumlah.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sIdCart = idcart.getText().toString();
                    myRef = firebaseDatabase.getReference(sIdCart+"/jumlah");
                    dialog = new Dialog(context);
                    dialog.setContentView(R.layout.edit_jumlah_dialog);
                    final EditText editText = dialog.findViewById(R.id.texteditjumlah);
                    editText.setText(qty.getText().toString());
                    Button btnUbah = dialog.findViewById(R.id.btnUbahJumlah);
                    btnUbah.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                          if(itemClickListener!=null){
                              itemClickListener.onClick(view,qty.getText().toString(),
                                      editText.getText().toString(),
                                      harga.getText().toString(),
                                      sIdCart+"/jumlah");
                          }else {
                              Log.d("handleClick", "onClick: failed");
                          }
//                            myRef.setValue(editText.getText().toString());
                            dialog.dismiss();
//                            Log.d("path", path+"/jumlah"+editText.getText().toString());
                        }
                    });
                    dialog.show();
                }
            });
        }
    }

}
