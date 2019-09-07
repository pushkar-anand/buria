package hackathon.buria.ui;

import android.os.Bundle;
import android.util.Log;
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

    private PermissionHelper ph;
    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;
    private static final String TAG = "LIVE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_camera);

        ph = new PermissionHelper(this);

        preview = findViewById(R.id.firePreview);
        graphicOverlay = findViewById(R.id.fireFaceOverlay);

        if (ph.areAllPermissionsGranted()) {
            createCameraSource();
            startCameraSource();
        } else {
            ph.requestPermissions();
        }

    }

    private void createCameraSource()
    {
        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
            try {
                cameraSource.setMachineLearningFrameProcessor(new AutoMLImageLabelerProcessor(this));
            } catch (FirebaseMLException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error creating processor.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCameraSource()
    {
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
            startCameraSource();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
