package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {
    public static final int MAX_TWEET_LENGTH = 140;
    private TextInputLayout textInputCompose;
    private Button btnTweet;
    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApp.getRestClient(this);
        textInputCompose = findViewById(R.id.text_input_compose);
        btnTweet = findViewById(R.id.btnTweet);

        // set hint programmatically
        textInputCompose.setHint("What's happening?");
        textInputCompose.setHintEnabled(false);

        // set click listener on button
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String tweetContent = textInputCompose.getEditText().getText().toString();

                // error handling
                if (tweetContent.isEmpty()){
                    Toast.makeText(ComposeActivity.this, "Tweet is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (tweetContent.length() > MAX_TWEET_LENGTH){
                    Toast.makeText(ComposeActivity.this, "Tweet is too long", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Toast.makeText(ComposeActivity.this, tweetContent, Toast.LENGTH_SHORT).show();

                // make API call to Twitter to publish the content in edit text
                client.composeTweet(tweetContent, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("eli", "successfully posted tweet" + response.toString());
                        try {
                            Tweet tweet = Tweet.fromJson(response);
                            Intent data = new Intent();
                            // Pass relevant data back as a result
                            data.putExtra("tweet", Parcels.wrap(tweet));
                            // Activity finished ok, return the data
                            setResult(RESULT_OK, data); // set result code and bundle data for response
                            finish(); // closes the activity, pass data to parent
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("eli", "failed tweet" + responseString.toString());
                    }
                });


            }
        });


    }
}
