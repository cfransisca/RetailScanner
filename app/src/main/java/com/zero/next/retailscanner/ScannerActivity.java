package com.zero.next.retailscanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import retailscannerhelper.AnyOrientationCaptureActivity;

public class ScannerActivity extends AppCompatActivity {
    private static final String TAG = "scan";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        /*scanner*/
        IntentIntegrator integrator = new IntentIntegrator(this); //manggil lib scanner
        integrator.setPrompt("");
        integrator.setCaptureActivity(AnyOrientationCaptureActivity.class); //potrait  / landscape
        integrator.setOrientationLocked(false); //lock orientation (potrait/landscape)
        integrator.setCameraId(0); //set front camera (1) or main camera (0)
        integrator.setBeepEnabled(false); //set beep
        integrator.setBarcodeImageEnabled(true); //
        integrator.initiateScan(); /*untuk membuka kamera pada saat activity dibuka*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null){
            if(result.getContents()==null) {
                Log.d(TAG, "onActivityResult: cancelled");
                Toast.makeText(this, "Tidak ada data!", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(ScannerActivity.this, AddToCartActivity.class);
                intent.putExtra("qrResult", result.getContents());
                startActivity(intent);
                Log.d(TAG, "onActivityResult: "+result.getContents());
                finish();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}
