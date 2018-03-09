package unidesign.ussdsmscodes.HelpActivity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import unidesign.ussdsmscodes.R;
import unidesign.ussdsmscodes.RN_USSD;

public class HelpActivity extends AppCompatActivity {

    WebView helpWebView;
    public final static int HELP_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        this.setTitle(R.string.action_help);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.help_toolbar);
        setSupportActionBar(myToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_close);
        //ab.setIcon(R.drawable.ic_close);
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        helpWebView = new WebView(this);
        // initiate a web view
        helpWebView=(WebView) findViewById(R.id.HelpWebView);
        helpWebView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = helpWebView.getSettings();
        //webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(true);
        //webSettings.setSupportZoom(true);

        // specify the url of the web page in loadUrl function
        String help_index_path = "file:///android_asset/" + getResources().getString(R.string.help_locale)+"/index.html";
        helpWebView.loadUrl(help_index_path);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_help_activity, menu);
        //getMenuInflater().inflate(R.menu.selected_menu_bottom, amvMenu.getMenu());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            helpWebView.clearHistory();
            onBackPressed();
            return true;
        }
        if (menuItem.getItemId() == R.id.action_intro) {
            //setSelectionMode();
            Log.d("R.id.action_intro", "intent.action.introslider");
            RN_USSD.prefManager.setFirstTimeLaunch(true);
            RN_USSD.prefManager.setShowMainDemo(true);
            Intent i = new Intent();
            i.putExtra("message", "finishRN_USSD");

            startActivity(new Intent("intent.action.introslider"));
            setResult(RESULT_CANCELED, i);
            finish();
            //getActionBar().hide();
            //actionMode = startActionMode(actionModeCallback);
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onBackPressed() {
        if (helpWebView.canGoBack() ){
            helpWebView.goBack();
        }
        else {
            setResult(RESULT_OK, new Intent());
            super.onBackPressed();
        }
    }
}
