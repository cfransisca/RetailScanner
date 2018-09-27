package com.zero.next.retailscanner;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
/*import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;*/

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Scanner2Activity extends AppCompatActivity {
    SharedPreferences sharedPreferences;

    String grandTotal,namatoko;
    private int requestCode=100;
    private DecoratedBarcodeView barcodeView;
    private String TAG = Scanner2Activity.class.getSimpleName();
    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result.getText() == null /*|| result.getText().equals(lastText)*/) {
                // Prevent duplicate scans
                //return;
            }

            Log.d(TAG, "barcodeResult: "+result.getText());
            //lastText = result.getText();
            //barcodeView.setStatusText(result.getText());
            barcodeView.pause();
            Intent intent = new Intent(Scanner2Activity.this, AddToCartActivity.class);
            intent.putExtra("qrResult", result.getText());
            startActivity(intent);
            finish();

            //beepManager.playBeepSoundAndVibrate();

            //Added preview of scanned barcode
            /*ImageView imageView = (ImageView) findViewById(R.id.barcodePreview);
            imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));*/
        }

        /*@Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }*/

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(PrefManager.PREF_NAME, PrefManager.PRIVATE_MODE);
        grandTotal = (sharedPreferences).getString(PrefManager.GRAND_TOTAL, "");
        namatoko = (sharedPreferences).getString(PrefManager.TOKO, "");
        setContentView(R.layout.activity_scanner);

        if (ContextCompat.checkSelfPermission(Scanner2Activity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(Scanner2Activity.this, new String[] {Manifest.permission.CAMERA}, requestCode);
        }
        barcodeView = findViewById(R.id.scanner);
        barcodeView.setStatusText("Arahkan Pada QR dengan Stabil");
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.decodeContinuous(callback);

    }

    @Override
    protected void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    public void pause(View view) {
        barcodeView.pause();
    }

    public void resume(View view) {
        barcodeView.resume();
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        namatoko = (sharedPreferences).getString(PrefManager.TOKO, "");
        if(Integer.parseInt(grandTotal)==0){
            Intent goBack = new Intent(Scanner2Activity.this,Main2Activity.class);
            startActivity(goBack);
            finish();
        } else {
            Intent backToCart = new Intent(Scanner2Activity.this, CartActivity.class);
            backToCart.putExtra("namatoko",namatoko);
            startActivity(backToCart);
            finish();
        }
    }
}
