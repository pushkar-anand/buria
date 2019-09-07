package hackathon.buria.automl;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.common.modeldownload.FirebaseRemoteModel;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceAutoMLImageLabelerOptions;

import java.io.IOException;
import java.util.List;

import hackathon.buria.VisionProcessorBase;
import hackathon.buria.camera.CameraImageGraphic;
import hackathon.buria.camera.FrameMetadata;
import hackathon.buria.camera.GraphicOverlay;

/**
 * AutoML image labeler Demo.
 */

public class AutoMLImageLabelerProcessor
        extends VisionProcessorBase<List<FirebaseVisionImageLabel>> {

    private static final String TAG = "AutoMLabel";

    private static final String REMOTE_MODEL_NAME = "SignLangModelNew";

    private final FirebaseVisionImageLabeler detector;

    public AutoMLImageLabelerProcessor(final Context context) throws FirebaseMLException {
        FirebaseRemoteModel remoteModel = new FirebaseRemoteModel.Builder(REMOTE_MODEL_NAME).build();
        FirebaseModelManager.getInstance()
                .registerRemoteModel(remoteModel);

//        FirebaseModelManager.getInstance()
//                .registerLocalModel(
//                        new FirebaseLocalModel.Builder(LOCAL_MODEL_NAME)
//                                .setAssetFilePath("automl/manifest.json")
//                                .build());

        FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder optionsBuilder =
                new FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder().setConfidenceThreshold(0.5f);

        optionsBuilder.setRemoteModelName(REMOTE_MODEL_NAME);

        detector =
                FirebaseVision.getInstance().getOnDeviceAutoMLImageLabeler(optionsBuilder.build());

        Toast.makeText(context, "Begin downloading the remote AutoML model.", Toast.LENGTH_SHORT)
                .show();
        // To track the download and get notified when the download completes, call
        // downloadRemoteModelIfNeeded. Note that if you don't call downloadRemoteModelIfNeeded, the model
        // downloading is still triggered implicitly.
        FirebaseModelManager.getInstance().downloadRemoteModelIfNeeded(remoteModel).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Download remote AutoML model success.", Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            String downloadingError = "Error downloading remote model.";
                            Log.e(TAG, downloadingError, task.getException());
                            Toast.makeText(context, downloadingError, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    public void stop() {
        try {
            detector.close();
        } catch (IOException e) {
            Log.e(TAG, "Exception thrown while trying to close the image labeler: " + e);
        }
    }

    @Override
    protected Task<List<FirebaseVisionImageLabel>> detectInImage(FirebaseVisionImage image) {
        return detector.processImage(image);
    }

    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull List<FirebaseVisionImageLabel> labels,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        if (originalCameraImage != null) {
            CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay,
                    originalCameraImage);
            graphicOverlay.add(imageGraphic);
        }
        LabelGraphic labelGraphic = new LabelGraphic(graphicOverlay, labels);
        graphicOverlay.add(labelGraphic);
        graphicOverlay.postInvalidate();
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.w(TAG, "Label detection failed." + e);
    }
}