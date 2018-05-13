package com.zero.next.retailscanner.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zero.next.retailscanner.R;
import com.zero.next.retailscanner.data.Order;

import java.util.Collections;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by cicak on 13/05/2018.
 */

public class AdapterOrder extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    List<Order> listOrder = Collections.emptyList();
    private LayoutInflater inflater;
    int currentPost = 0;
    Order currentOrder;

    public AdapterOrder(Context context, List<Order> listOrder) {
        this.context = context;
        this.listOrder = listOrder;
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
        /*int total = Integer.parseInt(myHolder.harga.getText().toString()) * Integer.parseInt(myHolder.qty.getText().toString());
        myHolder.grandTotal.setText(String.valueOf(total));*/
    }

    @Override
    public int getItemCount() {
        return listOrder.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView nama, harga, qty, grandTotal;
        ImageView gambar;

        public MyHolder(View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.namaItem);
            gambar = itemView.findViewById(R.id.gbrItem);
            harga = itemView.findViewById(R.id.hrgItem);
            qty = itemView.findViewById(R.id.qtyItem);
            grandTotal = itemView.findViewById(R.id.grandTotal);
        }
    }

}
