package com.zero.next.retailscanner.data;

/**
 * Created by cicak on 12/05/2018.
 */

/*tempat menyimpan data sementara dari firebase*/
public class OrderData {
    public  String id;
    public String idBarang;
    public String namaBarang;
    public String jumlah;
    public String harga;

    public OrderData() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdBarang() {
        return idBarang;
    }

    public void setIdBarang(String idBarang) {
        this.idBarang = idBarang;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }
}
