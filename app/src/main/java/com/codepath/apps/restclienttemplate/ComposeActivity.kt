 package com.codepath.apps.restclienttemplate

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

 class ComposeActivity : AppCompatActivity() {

    lateinit var etCompose: EditText
    lateinit var buttonTweet: Button
    lateinit var charCount: TextView

    lateinit var client: TwitterClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById<EditText>(R.id.createTweetText)
        buttonTweet = findViewById(R.id.buttonTweet)
        charCount = findViewById(R.id.charCount)

        // Grab content of edit text
        var tweetContent = etCompose.text.toString()

        client = TwitterApplication.getRestClient(this)

        etCompose.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val currLength = 280 - etCompose.text.length
                charCount.text = currLength.toString()
                tweetContent = etCompose.text.toString()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        buttonTweet.setOnClickListener {

            // Make sure tweet isn't empty
            if (tweetContent.isEmpty()) {
                Toast.makeText(this, "Empty tweets aren't allowed", Toast.LENGTH_SHORT).show()
            } else if (tweetContent.length > 280) {
                Toast.makeText(this, "Tweet exceeds the character limit", Toast.LENGTH_SHORT).show()
            } else {
                client.publishTweet(tweetContent, object: JsonHttpResponseHandler() {
                    override fun onFailure(
                        statusCode: Int,
                        headers: Headers?,
                        response: String?,
                        throwable: Throwable?
                    ) {
                        Log.e(TAG, "Failed to public tweet", throwable)
                    }

                    override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                        Log.i(TAG, "yes")

                        val tweet = Tweet.fromJson(json.jsonObject)

                        val intent = Intent()
                        intent.putExtra("tweet", tweet)
                        setResult(RESULT_OK, intent)
                        finish()
                    }

                })

            }

        }
    }

     companion object {
         val TAG = "ComposeActivity"
     }

}