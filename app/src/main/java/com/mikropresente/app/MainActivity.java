package com.mikropresente.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

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
import com.mikropresente.app.db.entity.Participant;
import com.mikropresente.app.db.repository.ParticipantRepository;
import com.mikropresente.app.helpers.Constants;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

   GestureDetector gestureDetector;
   boolean isToScan = false;
   TextView tvEmail, tvName, tvCode, tvPosition;
   SurfaceView surfaceView;
   CameraSource cameraSource;
   BarcodeDetector barcodeDetector;

    //region Metodos y Eventos del Activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        gestureDetector = new GestureDetector(MainActivity.this, MainActivity.this);
        init();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent event) {        return true;    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {        return false;    }

    @Override
    public void onLongPress(MotionEvent event) {    }

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

    //endregion

    //region Inicializadores
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
                   try {
                       Vibrator vibrator = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                       vibrator.vibrate(500);
                       Log.i(Constants.LOGTAG, qrCodes.valueAt(0).displayValue);
                       Log.i(Constants.LOGTAG, qrCodes.valueAt(0).rawValue);
                       Log.i(Constants.LOGTAG, qrCodes.valueAt(0).contactInfo.toString());
                       String [] qrData =  qrCodes.valueAt(0).rawValue.split("\\n");
                       final String code = getCode(qrData);
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               verifyCode(code);
                           }
                       });
                   } catch(Exception ex) {
                       Log.w(Constants.LOGTAG, ex.getMessage());
                       ex.printStackTrace();
                   }
                }
            }
        });
    }

    //endregion

    //region Metodos Generales

    private String getCode(String[] qrData) {
        String code = null;
        try {
            if(qrData.length > 0) {
                code = qrData[5].split(":")[1].replaceAll(";","");
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            code = null;
        }
        return code;
    }

    private void verifyCode(String code) {
        final ParticipantRepository repository = new ParticipantRepository(getApplicationContext());
        repository.findByCode(code).observe(MainActivity.this, new Observer<Participant>() {
            @Override
            public void onChanged(final Participant participant) {
                if (participant != null) {
                    manageData(participant);
                }
            }
        });
    }

    public void manageData(Participant participant) {
        resetText();
        if(participant != null) {
            try {
                tvCode.setText("Código de Validación: " + participant.code);
                tvName.setText(participant.name);
                tvEmail.setText(participant.email);
                tvPosition.setText(participant.position);
                ManagePositionColor(participant.position);
            } catch(Exception ex) {
                Log.e(Constants.LOGTAG, ex.getMessage());
                ex.printStackTrace();
                tvName.setText(R.string.invalid_qr_code);
            }
        } else {
            tvName.setText(R.string.invalid_qr_code);
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

    //endregion
}
