package com.example.admin;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.admin.R;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private boolean doubleBackToExitPressedOnce = false;
    private SwipeRefreshLayout swipeRefreshLayout; // Reference to the SwipeRefreshLayout
    private ImageView offlineImage; // Reference to the offline image view
    private boolean isInternetConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView); // Use the class-level variable
        offlineImage = findViewById(R.id.offlineImage); // Initialize the offline image view

        if (!isConnectedToInternet()) {
            // Show the offline image if there is no internet connection
            webView.setVisibility(View.GONE);
            offlineImage.setVisibility(View.VISIBLE);
            isInternetConnected = false;
        } else {
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient());

            String url = "https://www.reflexion.ai/";
            webView.loadUrl(url);
            isInternetConnected = true;
        }

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout); // Initialize the SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnectedToInternet()) {
                    // Show the WebView and hide the offline image on refresh
                    webView.setVisibility(View.VISIBLE);
                    offlineImage.setVisibility(View.GONE);
                    if (isInternetConnected) {
                        webView.reload(); // Reload the current URL in the WebView
                    } else {
                        String url = "https://www.reflexion.ai/";
                        webView.loadUrl(url);
                        isInternetConnected = true;
                    }
                } else {
                    // Show the offline image and hide the WebView if there is no internet connection
                    webView.setVisibility(View.GONE);
                    offlineImage.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, "Please turn on Internet connection", Toast.LENGTH_LONG).show();
                }
                swipeRefreshLayout.setRefreshing(false); // Hide the refresh indicator
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            doubleBackToExitPressedOnce = true;

            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
