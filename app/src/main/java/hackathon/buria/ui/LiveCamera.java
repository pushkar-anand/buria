package hackathon.buria.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;

import hackathon.buria.R;
import hackathon.buria.helpers.PermissionHelper;

public class LiveCamera extends AppCompatActivity
        implements OnRequestPermissionsResultCallback {

    private PermissionHelper ph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_camera);
        ph = new PermissionHelper(this);
        ph.requestPermissions();
    }


    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        if (ph.areAllPermissionsGranted()) {
            //createCameraSource(selectedModel);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
