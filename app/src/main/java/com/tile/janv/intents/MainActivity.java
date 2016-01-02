package com.tile.janv.intents;

import android.app.ListFragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final int SPEACH_ID = 2;

    //        http://androidexample.com/Create_A_Simple_Listview_-_Android_Example/index.php?view=article_discription&aid=65&aaid=90
//        see example from http://developer.android.com/training/basics/fragments/fragment-ui.html
    protected ListFragment speechResult;

    private int speechResultLayout;

    //---------------------
    // lifecycle methods
    //---------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        speechResult = (ListFragment) getFragmentManager().findFragmentById(R.id.text_speech_result);
        speechResult.getListView().setChoiceMode(ListView.CHOICE_MODE_NONE);
        speechResult.setListAdapter(new ArrayAdapter<String>(this, speechResultLayout,
                new ArrayList<String>()));

        // We need to use a different list item layout for devices older than Honeycomb
        speechResultLayout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                android.R.layout.simple_list_item_activated_1 : android.R.layout.simple_list_item_1;
    }

    //---------------------
    // menu methods
    //---------------------

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

    //---------------------
    // speak intent methods
    //---------------------

    @OnClick(R.id.speak_button)
    public void onSpeakButtonClicked(View view) {
        // good help: http://viralpatel.net/blogs/android-speech-to-text-api/
        Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
        // sadly, can't do in dutch
//        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "nl-BE");

        PackageManager packageManager = getPackageManager();
        ComponentName cn = speechIntent.resolveActivity(packageManager);
        if (cn == null) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speak_app_not_available),
                    Toast.LENGTH_SHORT).show();
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
                        ArrayList<String> textList = data
                                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                        // array is list of possible interpretations. Just show first (best).
                        speechResult.setListAdapter(new ArrayAdapter<String>(this, speechResultLayout,
                                textList));
                    }
                    break;
                case RESULT_CANCELED:
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.speech_to_text_cancelled),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    //---------------------
    // private methods
    //---------------------

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
