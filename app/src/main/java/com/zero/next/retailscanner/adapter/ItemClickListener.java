package com.zero.next.retailscanner.adapter;

import android.view.View;

public interface ItemClickListener {
    void onClick(View view, String tag, String jumlahAwal, String jumlahAkhir, String harga, String path, String qr);
}
