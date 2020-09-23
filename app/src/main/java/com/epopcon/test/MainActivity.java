package com.epopcon.test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.epopcon.core.utils.ExecutorPool;
import com.epopcon.extra.ExtraLibHelper;
import com.epopcon.extra.common.BasicWebView;
import com.epopcon.extra.common.BasicWebViewClient;
import com.epopcon.extra.common.ExtraContext;
import com.epopcon.extra.common.net.Client;
import com.epopcon.extra.common.net.Result;
import com.epopcon.extra.common.parser.ScriptHandler;
import com.epopcon.test.logger.Log;
import com.epopcon.test.logger.LogFragment;
import com.epopcon.test.logger.LogView;
import com.epopcon.test.logger.LogWrapper;
import com.epopcon.test.logger.MessageOnlyLogFilter;

import org.mozilla.javascript.tools.jsc.Main;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    private Context context;

    private LogFragment mLogFragment;

    private Client client = new Client();
    private WebView view2;
    private BasicWebView view;
    private BasicWebViewClient webViewClient;
    private ScriptHandler scriptHandler;

    private final String USER_AGENT = "Mozilla/5.0 (Linux; Android 6.0.1; SM-A800S Build/MMB29K; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/55.0.2883.91 Mobile Safari/537.36";
    private final String PAGE_ENCODING = "utf-8";

    private Button mBtnCard;
    private Button mBtnOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnCard = (Button) findViewById(R.id.card);
        mBtnOnline = (Button) findViewById(R.id.online);

        mBtnCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CardActivity.class);
                startActivity(intent);
            }
        });

        mBtnOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, OnlineActivity.class);
                startActivity(intent);
            }
        });

//        context = this;
//        client.setUserAgent(USER_AGENT);
//        client.setEncoding(PAGE_ENCODING);

        //initializeLogging();
//        initializeApp();
    }

//    private void initializeLogging() {
//        // Using Log, front-end to the logging chain, emulates
//        // android.util.log method signatures.
//
//        // Wraps Android's native log framework
//        LogWrapper logWrapper = new LogWrapper();
//        Log.setLogNode(logWrapper);
//
//        // A filter that strips out everything except the message text.
//        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
//        logWrapper.setNext(msgFilter);
//
//        // On screen logging via a fragment with a TextView.
//        mLogFragment = (LogFragment) getSupportFragmentManager().findFragmentById(R.id.logFragment);
//        msgFilter.setNext(mLogFragment.getLogView());
//    }
//
//    private void initializeApp() {
//
//        try {
///*
//            SharedPreferences prefs = getSharedPreferences("KEEPALIVE_INFO", Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = prefs.edit();
//
//            editor.putString("ePopconId", "eppcn156443d729d6b8d19b3062a72");
//            editor.commit();
//
//            Utils.putPrefInt(this, "USER_SALT_VERSION", 2);
//            Utils.putPrefString(this, "USER_SALT", "1491993057324");
//            //Utils.putPrefString(this, "BACKUP_EPOPCON_ID", "eppcn1576b84b599bd034b626728c4");
//
//            String cipherText = Encryption.getInstance(this).encryptString("1204dlrbgkr!");
//            String plainText = Encryption.getInstance(this).decryptString("fEGzyrX0GUJ6cOphN8+cCg==");
//
//            System.out.println(cipherText);
//            System.out.println(plainText);*/
//
//            /*ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
//            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//            activityManager.getMemoryInfo(mi);
//            double availableMegs = mi.availMem / 1048576L;
//            double percentAvail = mi.availMem / mi.totalMem;
//
//            Log.v("onCreate", "memoryClass:" + percentAvail);*/
//            CookieManager.getInstance().removeAllCookie();
//
//            ExtraLibHelper.setWebViewUserAgentString(this);
//            //view = new BasicWebView(this, null);
//
//            view2 = (WebView) findViewById(R.id.webview);
//            view2.setVisibility(View.VISIBLE);
//
//            view2.clearCache(true);
//            view2.getSettings().setUserAgentString(USER_AGENT);
//            view2.getSettings().setAllowContentAccess(true);
//            view2.getSettings().setJavaScriptEnabled(true);
//            //view.getSettings().setBlockNetworkImage(true);
//            //view.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE); // 웹뷰 로딩 향상
//            view2.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//            view2.setWebViewClient(new WebViewClient() {
//                @Override
//                public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                    Log.d(TAG, url);
//                    view.loadUrl(url);
//                    return false;
//                }
//            });
//
//            com.epopcon.extra.common.LogWrapper logger = new com.epopcon.extra.common.LogWrapper();
//
//            ExtraContext extraContext = new ExtraContext();
//            extraContext.context = context;
//            extraContext.client = client;
//            extraContext.logger = new com.epopcon.extra.common.LogWrapper();
///*
//            webViewClient = new BasicWebViewClient(new BasicWebViewClient.Handler() {
//                @Override
//                public void onComplete(boolean success, String requestUrl, String currentUrl, Throwable throwable) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            String script = "(function(){ " +
//                                    "  $.cookie('loginTracking', true, {\n" +
//                                    "                     path: '/'\n" +
//                                    "                 });" +
//                                    //" document.querySelector('input#j_username').value = 'lmu00@daum.net';" +
//                                    //" document.querySelector('input#j_password').value = 'Kaon639018';" +
//                                    //" $(\"#loginForm\").submit();" +
//                                    "  var json = document.querySelector('input[name=\"CSRFToken\"]').value;" +
//                                    "  return json; " +
//                                    "})()";
//
//                            scriptHandler.execute("tryLogin", script);
//                        }
//                    });
//                }
//            }, extraContext);*/
//            webViewClient.setEnableCss(false);
//
//            scriptHandler = new ScriptHandler(logger);
//            scriptHandler.putHandler("tryLogin", new ScriptHandler.MessageHandler(){
//                @Override
//                public void onComplete(boolean success, String message) {
//                    submitLoginData(message);
//                }
//            });
//
//            view.setWebViewClient(webViewClient);
//            view = BasicWebView.create(context, webViewClient, scriptHandler);
//        } catch (Exception e) {
//            Log.e(TAG, e.getMessage(), e);
//        }
//    }

//    public void showPreference(View v) {
//        SharedPreferences prefs = context.getSharedPreferences(
//                context.getPackageName(), Context.MODE_PRIVATE);
//
//        Log.d(TAG, prefs.getAll().toString());
//    }
//
//    public void tryLogin(View v) {
//        view.requestUrl("https://www.matchesfashion.com/login?noattraqt=Set");
//    }
//
//
//    public void queryOrderDetails(View v) {
//        //showProgress();
//        try {
//            view2.loadUrl("http://www.matchesfashion.com");
//        } catch (Exception e) {
//            Log.e(TAG, e.getMessage(), e);
//        }
//    }

//    private void submitLoginData(final String csrfToken) {
//        ExecutorPool.execute(new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... params) {
//                String username = "lmu00@daum.net";
//                String password = "Kaon639018";
//
//                try {
//                    URL url = new URL("https://www.matchesfashion.com/login/j_spring_security_check");
//
//                    client.newFormParameter();
//                    client.openConnection(url);
//                    client.addFormParameter("j_username", username);
//                    client.addFormParameter("j_password", password);
//                    client.addFormParameter("CSRFToken", csrfToken);
//
//                    //client.setCookie("loginTracking=true");
//
//
//                    client.addRequestHeader("Host", "www.matchesfashion.com");
//                    client.addRequestHeader("Origin", "https://www.matchesfashion.com");
//                    client.addRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
//                    client.addRequestHeader("Accept-Language", "ko,en-US;q=0.8,en;q=0.6");
//                    client.addRequestHeader("Upgrade-Insecure-Requests", "1");
//                    client.addRequestHeader("Cache-Control", "max-age=0");
//                    client.addRequestHeader("Referer", "https://www.matchesfashion.com/login?noattraqt=Set");
//
//                    Result result = client.executeWithFormData();
//                    result = client.executeIfRedirection(result);
//                    int responseCode = result.getResponseCode();
//
//                    Log.d(TAG, String.format("responseCode -> %s", responseCode));
//
//                    if (responseCode == HttpsURLConnection.HTTP_OK) {
//
//                    }
//                } catch (Exception e) {
//                    Log.e(TAG, e.getMessage(), e);
//                }
//                return null;
//            }
//        });
//    }

//    public void queryTrackingShipment(View v) {
//        try {
//
//
//        } catch (Exception e) {
//            Log.e(TAG, e.getMessage(), e);
//        }
//    }
//
//    public void clearView(View v) {
//        LogView view = mLogFragment.getLogView();
//        view.setText("");
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
