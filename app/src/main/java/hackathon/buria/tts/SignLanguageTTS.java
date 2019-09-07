package hackathon.buria.tts;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class SignLanguageTTS implements TextToSpeech.OnInitListener {

    private TextToSpeech t1;
    private String inputText;

    public SignLanguageTTS(Context context, String string) {
        this.inputText = string;
        t1 = new TextToSpeech(context, this);
    }


    @Override
    public void onInit(int status) {
        if (status != TextToSpeech.ERROR) {
            t1.setLanguage(Locale.UK);
            if (inputText.length() > TextToSpeech.getMaxSpeechInputLength()) {
                String[] words = inputText.split("\\s+");
                for (String word : words) {
                    t1.speak(word, TextToSpeech.QUEUE_ADD, null, word);
                }
            } else {
                t1.speak(inputText, TextToSpeech.QUEUE_ADD, null, inputText);
            }
        }
    }
}
