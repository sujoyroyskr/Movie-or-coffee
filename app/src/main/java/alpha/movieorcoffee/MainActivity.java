package alpha.movieorcoffee;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;

import java.util.List;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = " 5rsCOTLqWsV6TbJH5rDNQd61s";
    private static final String TWITTER_SECRET = "FDJwBOmQZQ8AXTIGrfTKY4GFfHqk1LIzRzH0wrVxJ9vRao3ZuK";
    private TwitterLoginButton loginButton;
    private TextView tv;
    private TwitterSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.tv);
        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()
                session = result.data;
                // TODO: Remove toast and use the TwitterSession's userID
                // with your app's user model
                String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";

                loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
                loginButton.setCallback(new Callback<TwitterSession>() {
                    @Override
                    public void success(Result<TwitterSession> result) {
                        // The TwitterSession is also available through:
                        // Twitter.getInstance().core.getSessionManager().getActiveSession()
                        session = result.data;
                        // TODO: Remove toast and use the TwitterSession's userID
                        // with your app's user model

                        String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";


                        tv.setText(msg);
                        loginButton.setVisibility(View.GONE);
                        final SearchTimeline searchTimeline = new SearchTimeline.Builder().query("logaprakash03").build();
                        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter(getApplicationContext(), searchTimeline);
                        Toast.makeText(getApplicationContext(), adapter.getCount(), Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void failure(TwitterException exception) {
                        Log.d("TwitterKit", "Login with Twitter failure", exception);
                    }
                });

            }
            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
// Can also use Twitter directly: Twitter.getApiClient()
                Long tweetID = session.getUserId();
                StatusesService statusesService = twitterApiClient.getStatusesService();
                statusesService.show(tweetID, null, null, null, new Callback<com.twitter.sdk.android.core.models.Tweet>() {
                    @Override
                    public void success(Result<Tweet> result) {
                        //Do something with result, which provides a Tweet inside of result.data
                        Log.d("Tweet Data", result.data.text);
                    }
                    public void failure(TwitterException exception) {
                        //Do something on failure
                    }
                });
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Make sure that the loginButton hears the result from any
        // Activity that it triggered.
        loginButton.onActivityResult(requestCode, resultCode, data);

    }

    private void fetch(){}
}
