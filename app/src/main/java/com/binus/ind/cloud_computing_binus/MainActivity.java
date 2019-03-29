package com.binus.ind.cloud_computing_binus;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
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

import com.binus.ind.cloud_computing_binus.domain.Language;
import com.binus.ind.cloud_computing_binus.domain.Paragraf;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class MainActivity extends AppCompatActivity {
    // You spinner view
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
                // Here you can do the action you want to...
                Toast.makeText(MainActivity.this, "ID: " + language.getName() + "\nName: " + language.getVoice(),
                        Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapter) {  }
        });

        Button button= (Button)findViewById(R.id.buttonSpeak);

        button.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                    }
                    else
                    {
                        //your code
                    }

                    EditText richTxt= (EditText) findViewById(R.id.texttospeech);
                    test(richTxt.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void test(String text) throws Exception {
        String folder = createFolder("binusian");
        int count;
        String URL_TOKDIS ="https://stream.watsonplatform.net/text-to-speech/api/v1/synthesize";
        URL obj = new URL(URL_TOKDIS);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setDoOutput(true);
        String encoded = Base64.getEncoder().encodeToString(("apikey"+":"+"OdakPdQFAiwJDVY0FyodJK8eQDS_W--59pVJL9KqWf6k").getBytes(StandardCharsets.UTF_8));  //Java 8
        connection.setRequestProperty("Authorization", "Basic "+encoded);

        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        //add reuqest header
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        Paragraf paragraf = new Paragraf();
        paragraf.setText(text);


        File file = new File(folder,"abc.mp3");
        // Send post request
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(paragraf.toString().getBytes());
        InputStream inputStream = connection.getInputStream();
        // download the file
        byte buffer[] = new byte[16 * 1024];

        int len1 = 0;
        while ((len1 = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, len1);
        }

//        wr.write(paragraf.toString().getBytes());
        outputStream.flush();
        outputStream.close();

//        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
//        wr.writeBytes(sms.toString());
//        wr.flush();
//        wr.close();
//
//        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//        String inputLine;
//        StringBuffer response = new StringBuffer();
//
//        while ((inputLine = in.readLine()) != null) {
//            response.append(inputLine);
//        }

//        in.close();

        //add request header
//        connection.setRequestProperty("User-Agent", "test");
//        int responseCode = connection.getResponseCode();

    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // permission was granted, yay! do the
//                    // calendar task you need to do.
//
//                } else {
//
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
//                return;
//            }
//
//            // other 'switch' lines to check for other
//            // permissions this app might request
//        }
//    }

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



}
