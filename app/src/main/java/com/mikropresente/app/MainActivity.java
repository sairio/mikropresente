package com.mikropresente.app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.mikropresente.app.db.AppDatabase;
import com.mikropresente.app.helpers.PermissionsHelper;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

   public static final int ACTIVITY_RESULT_PERMISSIONS = 201;
   private @Nullable String code;
   GestureDetector gestureDetector;
   private static final String LOGTAG = "SAIRIO";
   boolean isToScan = false;
   TextView tvEmail, tvName, tvCode, tvPosition;
   SurfaceView surfaceView;
   CameraSource cameraSource;
   BarcodeDetector barcodeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        gestureDetector = new GestureDetector(MainActivity.this, MainActivity.this);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissions();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.gestureDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent event) {

        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent event) {

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent event) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        resetText();
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        return false;
    }

    private void init() {
        tvEmail = findViewById(R.id.tvEmail);
        tvName = findViewById(R.id.tvPerson);
        tvCode = findViewById(R.id.tvValidationCode);
        tvPosition = findViewById(R.id.tvPosition);
        surfaceView = findViewById(R.id.cameraPreview);

        barcodeDetector = new BarcodeDetector.Builder(this)
        .setBarcodeFormats(Barcode.QR_CODE).build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(720,500)
                .setAutoFocusEnabled(true)
                .build();

        initQREvents();

        resetText();
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class,
                "mikro-presente").build();
    }

    private void initQREvents() {
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    cameraSource.start(holder);
                } catch(IOException ex){
                    ex.printStackTrace();
                }
                isToScan = true;
                resetText();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCodes = detections.getDetectedItems();
                if (qrCodes.size() > 0) {
                    Vibrator vibrator = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(500);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(LOGTAG, qrCodes.valueAt(0).displayValue);
                            String [] qrData =  qrCodes.valueAt(0).rawValue.split("\\n");
                            manageData(qrData);
                            // Stuff that updates the UI
                        }
                    });
                }
            }
        });
    }

    public void manageData(String[] qrData) {
        resetText();
        if(qrData.length > 0) {
            try {
                String name = qrData[2].split(":")[1];
                String position = qrData[3].split(":")[1];
                String email = qrData[4].split(":")[1];
                String code = qrData[5].split(":")[1].replaceAll(";","");

                tvCode.setText("Código de Validación: " + code);
                tvName.setText(name);
                tvEmail.setText(email);
                tvPosition.setText(position);
                ManagePositionColor(position);
            } catch(Exception ex) {
                Log.e(LOGTAG, ex.getMessage());
                ex.printStackTrace();
                resetText();
                tvName.setText("Error en datos de codigo QR " + ex.getMessage());
            }
        } else {
            tvName.setText("Contenido de codigo QR Incorrecto");
        }
    }

    private void resetText() {
        if (isToScan) {
            tvCode.setVisibility(View.GONE);
            tvName.setVisibility(View.GONE);
            tvEmail.setVisibility(View.GONE);
            tvPosition.setVisibility(View.GONE);
            surfaceView.setVisibility(View.VISIBLE);

        } else {
            tvCode.setVisibility(View.VISIBLE);
            tvName.setVisibility(View.VISIBLE);
            tvEmail.setVisibility(View.VISIBLE);
            tvPosition.setVisibility(View.VISIBLE);
            surfaceView.setVisibility(View.GONE);
        }
        isToScan = !isToScan;
        tvCode.setText("");
        tvName.setText("");
        tvEmail.setText("");
        tvPosition.setText("");
        tvPosition.setBackgroundColor(getResources().getColor(R.color.white));
    }

    private void ManagePositionColor(String position) {
        position = position.toLowerCase();
        int color = 0;
        if ( position.contains("exhibi"))
            color = getResources().getColor(R.color.exhibition);
        else if (position.contains("general"))
            color = getResources().getColor(R.color.general);
        else if(position.contains("confer"))
            color = getResources().getColor(R.color.speaker);
        else if (position.contains("staff"))
            color = getResources().getColor(R.color.staff);
        else if (position.contains("vip"))
            color = getResources().getColor(R.color.vip);
        else if (position.contains("invita"))
            color  = getResources().getColor(R.color.specialGest);

        tvPosition.setBackgroundColor(color);
    }

    private void checkPermissions() {
        //Comprobacion de permisos para poder continuar. Necesario para versiones >= a Android 6.0
        try {
            if (new PermissionsHelper().permissionCheck(this, ACTIVITY_RESULT_PERMISSIONS)) {
                Log.i(LOGTAG, "permissions ok");
            }
        } catch (Exception e) {
            tvEmail.setText(e.getMessage());
        }
    }
}
