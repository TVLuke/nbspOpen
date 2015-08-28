package nbspOpen.lukeslog.de.nbspopen.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import nbspOpen.lukeslog.de.nbspopen.R;
import nbspOpen.lukeslog.de.nbspopen.status.StatusNotificationService;


public class MyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl("http://nobreakspace.org/status/");

        Intent i = new Intent(this, StatusNotificationService.class);
        startService(i);
    }

}
