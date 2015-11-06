package com.tile.janv.intents;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int SPEACH_ID = 2;

    private TextView speechResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        speechResult = (TextView) findViewById(R.id.text_speech_result);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSpeakButtonClicked(View view) {
        // good help: http://viralpatel.net/blogs/android-speech-to-text-api/
        Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
        // sadly, can't do in dutch
//        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "nl-BE");

        PackageManager packageManager = getPackageManager();
        ComponentName cn = speechIntent.resolveActivity(packageManager);
        if (cn == null) {
            Toast t = Toast.makeText(getApplicationContext(),
                    "Opps! Your device doesn't support Speech to Text",
                    Toast.LENGTH_SHORT);
            t.show();
        } else {
            startActivityForResult(speechIntent, SPEACH_ID);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEACH_ID) {
            switch (resultCode) {
                case RESULT_OK:
                    if (null != data) {
                        ArrayList<String> text = data
                                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                        // array is list of possible interpretations. Just show first (best).
                        speechResult.setText(text.get(0));
                    }
                    break;
                case RESULT_CANCELED:
                    Toast.makeText(getApplicationContext(),
                            "Speech to text canceled",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private void findPackageInGooglePlay(String packageName) {
        // If there is no Activity available to perform the action
        // Check to see if the Google Play Store is available.
        Uri marketUri = Uri.parse("market://search?q=" + packageName);
        Intent marketIntent = new Intent(Intent.ACTION_VIEW).setData(marketUri);
        // If the Google Play Store is available, use it to download an application
        // capable of performing the required action. Otherwise log an
        // error.
        if (marketIntent.resolveActivity(getPackageManager()) != null){
            startActivity(marketIntent);
        }
        else{
            Log.d("try downloading speach", "Market client not available.");
        }
    }
}
