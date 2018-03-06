package unidesign.ussdsmscodes.HelpActivity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import unidesign.ussdsmscodes.R;

public class HelpActivity extends AppCompatActivity {

    WebView helpWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        this.setTitle(R.string.action_help);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.help_toolbar);
        setSupportActionBar(myToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        helpWebView = new WebView(this);
        // initiate a web view
        helpWebView=(WebView) findViewById(R.id.HelpWebView);
        helpWebView.setWebViewClient(new WebViewClient());

        // specify the url of the web page in loadUrl function
        String help_index_path = "file:///android_asset/" + getResources().getString(R.string.help_locale)+"/index.html";
        helpWebView.loadUrl(help_index_path);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
