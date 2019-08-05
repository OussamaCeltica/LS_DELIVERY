package com.ls.celtica.lsdelivryls;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class CodeBarScanner extends AppCompatActivity {


        private ZXingScannerView mScannerView;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


            if (savedInstanceState != null) {
                //region Revenir a au Deviceconfig ..
                Intent intent = new Intent(getApplicationContext(), DeviceConfig.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                //endregion
            } else {
                //region check camera permission ..
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(CodeBarScanner.this, new String[]{Manifest.permission.CAMERA}, 8);

                }
                //endregion
                else {
                    mScannerView = new ZXingScannerView(this); // Programmatically initialize the scanner view


                    setContentView(mScannerView);
                    mScannerView.startCamera();

                    mScannerView.setResultHandler(new ZXingScannerView.ResultHandler() {
                        @Override
                        public void handleResult(Result result) {
                            // Log.e("code"," "+result.getText());
                            mScannerView.stopCamera();
                            mScannerView.resumeCameraPreview(this);
                            Intent i = new Intent();
                            i.putExtra("code", "" + result.getText());
                            setResult(RESULT_OK, i);
                            finish();

                        }
                    }); // Register ourselves as a handler for scan results.


                }
            }
        }

        @Override
        public void onResume() {
            super.onResume();


        }

        @Override
        public void onPause() {
            super.onPause();
            mScannerView.stopCamera();           // Stop camera on pause
        }


    }
