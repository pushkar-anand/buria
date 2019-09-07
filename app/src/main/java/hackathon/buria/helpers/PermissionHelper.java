package hackathon.buria.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionHelper {

    private static final int PERMISSION_REQUESTS = 1;
    private Activity activity;
    //private View mLayout;
    private List<String> allPermissions = new ArrayList<>();


    public PermissionHelper(Activity activity) {
        this.activity = activity;
        //this.mLayout = mLayout;
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i("PMH", "Permission granted: " + permission);
            return true;
        }
        Log.i("PMH", "Permission NOT granted: " + permission);
        return false;
    }

    public boolean areAllPermissionsGranted() {
        for (String permission : listRequiredPermissions() ) {
            if (!isPermissionGranted(activity, permission)) {
                return false;
            }
        }
        return true;
    }

    private String[] listRequiredPermissions() {
        try {
            PackageInfo info =
                    activity.getPackageManager()
                            .getPackageInfo(activity.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }
    private void getAllPermissions()
    {
        for (String permission : listRequiredPermissions() ) {
            if (!isPermissionGranted(activity, permission)) {
                allPermissions.add(permission);
            }
        }
    }

    public void requestPermissions() {
        getAllPermissions();
        ActivityCompat.requestPermissions(activity,
                allPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
    }

}
