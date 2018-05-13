package com.zero.next.retailscanner.data;

/**
 * Created by cicak on 12/05/2018.
 */

public class Cart {
    public  String id;
    public  String idBarang;
    public String namaBarang;
    public String jumlah;
    public  String harga;

    public Cart() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Cart(String id, String idBarang, String namaBarang, String jumlah, String harga) {
        this.id = id;
        this.idBarang = idBarang;
        this.namaBarang = namaBarang;
        this.jumlah = jumlah;
        this.harga = harga;
    }
}
