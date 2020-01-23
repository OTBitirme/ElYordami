package org.tensorflow.lite.examples.detection.voice;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.lite.examples.detection.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class VoiceRecognitionActivity extends AppCompatActivity {
    TextView tv;
    public static String[] metin;
    public static String[] labelmap;
    public static String wantedObject;

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recognition);

        tv=(TextView)findViewById(R.id.tv);

        String text = "";
        try{
            InputStream inputStream = getAssets().open("labelmap.txt");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            text = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        labelmap=text.split("\n");
        Log.d("LabelMap size:" ,""+labelmap.length);

        recognizer();
    }

    private void recognizer(){
        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Please say what you want to find");
        try{
            startActivityForResult(intent,REQUEST_CODE_SPEECH_INPUT);
        }catch (Exception e){
            Log.e("VOICE_RECOGNATION_ERROR","ERROR:");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_CODE_SPEECH_INPUT:
            {
                if(resultCode==RESULT_OK && null!=data){
                    ArrayList<String> result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    tv.setText(result.get(0));
                    Log.d("CATCHED: ",""+result.get(0));
                    for(int i=0;i<result.size();i++){
                        Log.d("Catched:   ",result.get(i));
                    }
                    metin=result.get(0).split(" ");
                    Log.d("Metin Size: ",""+metin.length);
                    for(int i=0;i<metin.length;i++){
                        Log.d("Metin:   ",metin[i]);
                    }
                    int flag=0;//indicates whether the object is present or not
                    for(int i=0;i<labelmap.length;i++){
                        for (int j=0;j<metin.length;j++){
                            if(metin[j].equalsIgnoreCase(labelmap[i])){
                                wantedObject=labelmap[i];
                                flag=1;
                                Log.d("FOUND",labelmap[i]);
                                break;
                            }
                        }
                        if(flag==1) break;
                    }
                }
            }
            break;
        }
    }
}
