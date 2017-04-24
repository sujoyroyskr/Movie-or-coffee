package alpha.movieorcoffee;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.concurrency.AsyncTask;

public class MainActivity extends AppCompatActivity {


    private static final String TWITTER_KEY = " 5rsCOTLqWsV6TbJH5rDNQd61s";
    private static final String TWITTER_SECRET = "FDJwBOmQZQ8AXTIGrfTKY4GFfHqk1LIzRzH0wrVxJ9vRao3ZuK";
    private String lat="",lon="";
    private Button navigate,more,refresh;
    private ImageView im;
    private String recentTweet ="";
    ProgressDialog dialog;
    private static int count = 0;
    private String response;
    public static String AuthToken;
    private TwitterLoginButton loginButton;
    private TextView tv,label,rating;
    private LinearLayout lv;
    private TwitterSession session;
    public static final String PREFS_NAME = "MyApp_Settings";
    private static final int PERMISSION_REQUEST_CODE = 1;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);
        rating = (TextView) findViewById(R.id.rating);
        im = (ImageView) findViewById(R.id.resultIcon);
        label = (TextView) findViewById(R.id.label);
        more = (Button) findViewById(R.id.more);
        refresh = (Button) findViewById(R.id.refresh);
        navigate = (Button) findViewById(R.id.navigate);
        lv = (LinearLayout) findViewById(R.id.top);
        lv.setVisibility(View.GONE);
        more.setVisibility(View.GONE);
        refresh.setVisibility(View.GONE);
        navigate.setVisibility(View.GONE);

        // Here, thisActivity is the current activity
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        /*SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        // Reading from SharedPreferences
        String value = settings.getString("key", "");
        if(!value.equals("")){
            dialog = ProgressDialog.show(MainActivity.this, "",
                    "Getting feeds", true);
            new getTweets().execute(AuthToken);
        }*/

        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double v1 = Double.parseDouble(lat);
                double v2 = Double.parseDouble(lon);
                double latitude=0.0,longitude=0.0;
                GPSTracker tracker = new GPSTracker(MainActivity.this);
                if (!tracker.canGetLocation()) {
                    tracker.showSettingsAlert();
                } else {
                    latitude = tracker.getLatitude();
                    longitude = tracker.getLongitude();
                }
                if (latitude==0.0 && longitude==0.0) {
                    latitude = tracker.getLatitude();
                    longitude = tracker.getLongitude();
                }
                String uri = "http://maps.google.com/maps?saddr=" + v1 + "," + v2+ "&daddr=" + latitude + "," + longitude;;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                // Writing data to SharedPreferences
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("key", "");
                editor.commit();
                lv.setVisibility(View.GONE);
                navigate.setVisibility(View.GONE);
                refresh.setVisibility(View.GONE);
                more.setVisibility(View.GONE);
                loginButton.setVisibility(View.VISIBLE);
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = 0;
                dialog = ProgressDialog.show(MainActivity.this, "",
                        "Getting feeds", true);
                new getTweets().execute(AuthToken);
            }
        });

        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()
                session = result.data;
                // TODO: Remove toast and use the TwitterSession's userID
                // with your app's user model


                AuthToken = session.getUserName().toString();
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                // Writing data to SharedPreferences
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("key", AuthToken);
                editor.commit();
                Log.d("token",AuthToken);
                dialog = ProgressDialog.show(MainActivity.this, "",
                        "Getting feeds", true);
                new getTweets().execute(AuthToken);

            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Make sure that the loginButton hears the result from any
        // Activity that it triggered.
        loginButton.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {


        super.onStart();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {


        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
    public class getTweets extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            try {
                HttpClient httpclient = new DefaultHttpClient();

                HttpGet getRequest = new HttpGet("http://cyberknights.in/api/twitter/fetchTweet.php?username=" + strings[0]); // create an HTTP GET object
                getRequest.setHeader("Content-Type", "application/json");
                HttpResponse response = httpclient.execute(getRequest);
                String responseString = EntityUtils.toString(response.getEntity());
                return responseString;
            } catch (Exception e) {
                // Output the stack trace.
                e.printStackTrace();
            }
            return "false";
        }

        protected void onPostExecute(String dec) {
            Log.d("res",dec);
            dialog.dismiss();
            recentTweet = dec;
            dialog = ProgressDialog.show(MainActivity.this, "",
                    "Analysing tweet ...", true);
            // ArrayList<TwitterTweet> twitterTweetArrayList = convertJsonToTwitterTweet(dec);
           new getSentiment().execute(dec);

        }
    }
    public class getSentiment extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            try {
                HttpClient httpclient = new DefaultHttpClient();

                HttpPost httppost = new HttpPost("http://text-processing.com/api/sentiment/");
                httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("text", strings[0]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                String responseString = EntityUtils.toString(response.getEntity());
                return responseString;
            } catch (Exception e) {
                // Output the stack trace.
                e.printStackTrace();
            }


            return "false";
        }

        protected void onPostExecute(String dec) {
            Double latitude=0.0,longitude=0.0;
            Log.d("sentiment",dec);
            String[] words=dec.split(",");
            String[] temp = words[3].split(":");
            String lab = temp[1].substring(2, temp[1].length()-2);
            recentTweet = recentTweet.toLowerCase();
            boolean check = recentTweet.contains("excited") || recentTweet.contains("interesting") ||recentTweet.contains("crazy") || recentTweet.contains("excitement") ;

            if(lab.equals("pos") || (recentTweet.contains("long")&&recentTweet.contains("drive")) || check){
                label.setText("To make your day even better");
                lab = "movie_theater";
                new DownloadImageTask((ImageView) findViewById(R.id.resultIcon))
                        .execute("https://maps.gstatic.com/mapfiles/place_api/icons/movies-71.png");

            }

            else{
                label.setText("A single coffee can change your day");
                lab = "cafe";
                new DownloadImageTask((ImageView) findViewById(R.id.resultIcon))
                        .execute("https://maps.gstatic.com/mapfiles/place_api/icons/cafe-71.png");
            }




            GPSTracker tracker = new GPSTracker(MainActivity.this);
            if (!tracker.canGetLocation()) {
                tracker.showSettingsAlert();
            } else {
                latitude = tracker.getLatitude();
                longitude = tracker.getLongitude();
            }
            while (latitude == 0.0 && longitude==0.0) {
                latitude = tracker.getLatitude();
                longitude = tracker.getLongitude();
            }


            Log.d("lat",latitude.toString());
            Log.d("lon",longitude.toString());
            dialog.dismiss();
            dialog = ProgressDialog.show(MainActivity.this, "",
                    "Fetching places ...", true);
            new getPlaces().execute(latitude.toString(),longitude.toString(),lab);
            //ArrayList<TwitterTweet> twitterTweetArrayList = convertJsonToTwitterTweet(dec);
        }
    }
    public class getPlaces extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            try {
                HttpClient httpclient = new DefaultHttpClient();

                HttpGet getRequest = new HttpGet("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+strings[0]+","+strings[1]+"&radius=50000&type="+strings[2]+"&key=AIzaSyCPsXoEcT2Sd3Bk_NB3ZQgsDVXWyhT3Rz8");
                getRequest.setHeader("Content-Type", "application/json");
                HttpParams httpParameters = new BasicHttpParams();
                int timeoutConnection = 100000;
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                HttpResponse response = httpclient.execute(getRequest);
                String responseString = EntityUtils.toString(response.getEntity());
                return responseString;
            } catch (Exception e) {
                // Output the stack trace.
                e.printStackTrace();
            }
            return "false";
        }

        protected void onPostExecute(String dec) {
            Log.d("places",dec);
            response = dec;
            extract(count);
            dialog.dismiss();
            loginButton.setVisibility(View.GONE);
            navigate.setVisibility(View.VISIBLE);
            more.setVisibility(View.VISIBLE);
            refresh.setVisibility(View.VISIBLE);
            lv.setVisibility(View.VISIBLE);

        }
    }
    public void extract(int n){
        String words[] = response.split(":");
        if( (n*27)+26 <words.length) {
            n = n * 27;
            int extra=1;
            String test = words[1].substring(9, words[1].length() - 2);
            if(test.equals("results"))
            {
                extra = 0;
            }
            Log.d("test",test);
            String temp[] = words[17 + n + extra].split(",");
            String word = temp[0].substring(2, temp[0].length() - 1);
            Log.d("word", word);
            tv.setText(word);

            String s1[] = words[5 + n + extra].split(",");
            String word1 = s1[0].substring(1, s1[0].length());
            lat = word1;
            Log.d("lat", word1);

            String s2[] = words[6 + n + extra].split(",");
            String word2 = s2[0].substring(1, s2[0].length() - 1);
            lon = word2;
            Log.d("lon", word2);


        }
        else{
            extract(count--);
            tv.setText("No places found");
        }
    }
    
}
