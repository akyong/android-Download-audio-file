package com.binus.ind.cloud_computing_binus;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.SynthesizeOptions;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;
import com.ibm.watson.developer_cloud.service.security.IamOptions;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.binus.ind.cloud_computing_binus.domain.Language;
import com.binus.ind.cloud_computing_binus.domain.Paragraf;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voices;
import com.ibm.watson.developer_cloud.text_to_speech.v1.util.WaveUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    EditText editText;
    Button button;
    public static String uservoice ;
    public static String apikey = "OdakPdQFAiwJDVY0FyodJK8eQDS_W--59pVJL9KqWf6k";


    private Spinner mySpinner= null;
    // Custom Spinner adapter (ArrayAdapter<User>)
    // You can define as a private to use it in the all class
    // This is the object that is going to do the "magic"
    private SpinnerAdapter adapter =null;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText= (EditText) findViewById(R.id.texttospeech);
        button= (Button)findViewById(R.id.buttonSpeak);
        // Example of a call to a native method
        Language[] languages = new Language[12];

        languages[0] = new Language();
        languages[0].setName("Brazilian Portuguese");
        languages[0].setVoice("pt-BR_IsabelaVoice");

        languages[1] = new Language();
        languages[1].setName("Castilian Spanish");
        languages[1].setVoice("es-ES_LauraVoice");

        languages[2] = new Language();
        languages[2].setName("French");
        languages[2].setVoice("fr-FR_ReneeVoice");

        languages[3] = new Language();
        languages[3].setName("German");
        languages[3].setVoice("de-DE_BirgitVoice");

        languages[4] = new Language();
        languages[4].setName("Japanese");
        languages[4].setVoice("ja-JP_EmiVoice");

        languages[5] = new Language();
        languages[5].setName("Latin American Spanish");
        languages[5].setVoice("es-LA_SofiaVoice");

        languages[6] = new Language();
        languages[6].setName("North American Spanish");
        languages[6].setVoice("es-US_SofiaVoice");

        languages[7] = new Language();
        languages[7].setName("UK English");
        languages[7].setVoice("en-GB_KateVoice");

        languages[8] = new Language();
        languages[8].setName("US English - Female - Lisa");
        languages[8].setVoice("en-US_LisaVoice");

        languages[9] = new Language();
        languages[9].setName("US English - Female - Allison");
        languages[9].setVoice("en-US_AllisonVoice");

        languages[10] = new Language();
        languages[10].setName("US English - Male - Michael");
        languages[10].setVoice("en-US_MichaelVoice");

        languages[11] = new Language();
        languages[11].setName("Italian");
        languages[11].setVoice("it-IT_FrancescaVoice");

        // Initialize the adapter sending the current context
        // Send the simple_spinner_item layout
        // And finally send the Users array (Your data)
        adapter = new SpinnerAdapter(MainActivity.this,
                android.R.layout.simple_spinner_item,
                languages);

        mySpinner = (Spinner) findViewById(R.id.miSpinner);
        mySpinner.setAdapter(adapter); // Set the custom adapter to the spinner
        // You can create an anonymous listener to handle the event when is selected an spinner item
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                // Here you get the current item (a User object) that is selected by its position
                Language language = (Language) adapter.getItem(position);
                uservoice = language.getVoice();
                // Here you can do the action you want to...
                Toast.makeText(MainActivity.this, "Voice : " + language.getName() + "\nVoice code: " + language.getVoice(),
                        Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapter) {  }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().matches("")){
                    Toast.makeText(MainActivity.this, "Please insert some text.\n\nCreated By: \n1. Adri Wiratam\n2. Badia Felix\n3. Bobby",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    RunCoba runner = new RunCoba(editText.getText().toString(),uservoice);
                    String sleepTime = "2";
                    runner.execute(sleepTime);
                }

            }
        });
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();


    public String createFolder(String fname){
        String myfolder=Environment.getExternalStorageDirectory()+"/"+fname;
        File f=new File(myfolder);
        if(!f.exists())
            if(!f.mkdir()){
                Toast.makeText(this, myfolder+" can't be created.", Toast.LENGTH_SHORT).show();

            }
            else
                Toast.makeText(this, myfolder+" can be created.", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, myfolder+" already exits.", Toast.LENGTH_SHORT).show();

        return myfolder;
    }


    private void initTextToSpeech(String text, String voice) {
        TextToSpeech service = new TextToSpeech();
        IamOptions options = new IamOptions.Builder()
                .apiKey(apikey)
                .build();
        service.setIamCredentials(options);
        SynthesizeOptions synthesizeOptions = new SynthesizeOptions.Builder()
                .text(text).voice(voice)
                .accept(SynthesizeOptions.Accept.AUDIO_WAV) // specifying that we want a WAV file
                .build();
        InputStream streamResult = service.synthesize(synthesizeOptions).execute();

        StreamPlayer player = new StreamPlayer();
        player.playStream(streamResult); // should work like a charm
    }

    private class RunCoba extends AsyncTask<String, Void, String> {
        String inputtext;
        String suara;
        public RunCoba(String text, String suara){
            this.inputtext = text;
            this.suara = suara;
        }

        @Override
        protected String doInBackground(String... params) {
            initTextToSpeech(inputtext,suara);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
//            TextView txt = (TextView) findViewById(R.id.output);
//            txt.setText("Executed"); // txt.setText(result);
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

}
