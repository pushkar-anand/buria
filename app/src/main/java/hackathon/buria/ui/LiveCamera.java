package hackathon.buria.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;

import com.google.firebase.ml.common.FirebaseMLException;

import java.io.IOException;

import hackathon.buria.R;
import hackathon.buria.automl.AutoMLImageLabelerProcessor;
import hackathon.buria.camera.CameraSource;
import hackathon.buria.camera.CameraSourcePreview;
import hackathon.buria.camera.GraphicOverlay;
import hackathon.buria.helpers.PermissionHelper;

public class LiveCamera extends AppCompatActivity
        implements OnRequestPermissionsResultCallback {

    private static final String TAG = "LIVE";
    private PermissionHelper ph;
    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;
    private Button startBtn, stopBtn;
    private TextView fullView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_camera);

        ph = new PermissionHelper(this);

        fullView = findViewById(R.id.translationTv);
        preview = findViewById(R.id.firePreview);
        graphicOverlay = findViewById(R.id.fireFaceOverlay);
        startBtn = findViewById(R.id.start_btn);
        stopBtn = findViewById(R.id.stop_btn);

        if (!ph.areAllPermissionsGranted()) {
            ph.requestPermissions();
        } else {
            createCameraSource();
        }
        setupListeners();
        graphicOverlay.setFullTextView(fullView);
    }

    private void setupListeners() {
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullView.setText("");
                startCameraSource();
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraSource.stop();
            }
        });
    }

    private void createCameraSource() {
        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
        }

        try {
            AutoMLImageLabelerProcessor imageLabelerProcessor = new AutoMLImageLabelerProcessor(this);
            cameraSource.setMachineLearningFrameProcessor(imageLabelerProcessor);
        } catch (FirebaseMLException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating processor.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startCameraSource() {
        if (cameraSource != null) {

            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null");
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null");
                }
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }

        }
    }


    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        if (ph.areAllPermissionsGranted()) {
            createCameraSource();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
