package hackathon.buria.automl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;

import org.w3c.dom.Text;

import java.util.List;

import hackathon.buria.camera.GraphicOverlay;

/** Graphic instance for rendering a label within an associated graphic overlay view. */
public class LabelGraphic extends GraphicOverlay.Graphic {

    private final Paint textPaint;
    private final GraphicOverlay overlay;

    private final List<FirebaseVisionImageLabel> labels;
    private final TextView fullTextView;

    LabelGraphic(GraphicOverlay overlay, List<FirebaseVisionImageLabel> labels) {
        super(overlay);
        this.overlay = overlay;
        this.labels = labels;
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(60.0f);
        fullTextView = overlay.getFullTextView();
    }

    @Override
    public synchronized void draw(Canvas canvas) {
        float x = overlay.getWidth() / 4.0f;
        float y = overlay.getHeight() / 2.0f;

        for (FirebaseVisionImageLabel label : labels) {
            canvas.drawText(label.getText(), x, y, textPaint);
            y = y - 62.0f;

            if (fullTextView != null) {

                String str = label.getText();
                if (str.equals("space")) {
                    fullTextView.append(" ");
                } else if (str.equals("del")) {
                    String prev = fullTextView.getText().toString();
                    String next = removeLastChar(prev);
                    fullTextView.setText(next);

                } else if(str.equals("nothing")) {
                    //do nothing
                } else {
                    fullTextView.append(str);
                }
            }
        }
    }

    private String removeLastChar(@NonNull String str)
    {
        char lastChar = str.charAt(str.length() - 1);
        int cut = Character.isSurrogate(lastChar) ? 2 : 1;
        return str.substring(0, str.length() - cut);
    }
}
