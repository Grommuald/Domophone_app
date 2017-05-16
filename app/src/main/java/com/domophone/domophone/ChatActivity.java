package com.domophone.domophone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    static final String TAG = ChatActivity.class.getSimpleName();
    static final String USER_ID_KEY = "userId";
    static final String BODY_KEY = "body";

    static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;

    static final int POOL_INTERVAL = 1000;
    Handler myHandler = new Handler();
    Runnable mRefreshMessagesRunnable = new Runnable() {
        @Override
        public void run() {
            refreshMessages();
            myHandler.postDelayed(this, POOL_INTERVAL);
        }
    };
    EditText etMessage;
    Button btSend;

    ListView lvChat;
    ArrayList<Message> mMessages;
    ChatListAdapter mAdapter;
    boolean mFirstLoad;

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyStarted = prefs.getBoolean(getString(R.string.pref_previously_started), false);
        if(!previouslyStarted) {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(getString(R.string.pref_previously_started), Boolean.TRUE);
            edit.commit();
            showFirstTimeLoginActivity();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_intercom:
                        break;
                    case R.id.action_advertisement:
                        break;
                    case R.id.action_discussions:
                        break;
                    case R.id.action_shoutbox:
                        break;
                }
                return true;
            }
        });

        if (ParseUser.getCurrentUser() != null) {
            startWithCurrentUser();
        } else {
            login();
        }
        myHandler.postDelayed(mRefreshMessagesRunnable, POOL_INTERVAL);
        findViewById(R.id.chatLayout).requestFocus();
    }
    void startWithCurrentUser() {
        setupMessagePosting();
    }
    void setupMessagePosting() {
        etMessage = (EditText) findViewById(R.id.etMessage);
        btSend = (Button) findViewById(R.id.btSend);
        lvChat = (ListView) findViewById(R.id.lvChat);
        mMessages = new ArrayList<>();

        lvChat.setTranscriptMode(1);
        mFirstLoad = true;
        final String userId = ParseUser.getCurrentUser().getObjectId();
        mAdapter = new ChatListAdapter(ChatActivity.this, userId, mMessages);
        lvChat.setAdapter(mAdapter);

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etMessage.getText().toString().trim().equals("")) {
                    Toast.makeText(ChatActivity.this, "Wprowadź tekst.", Toast.LENGTH_SHORT).show();
                } else {
                    String data = etMessage.getText().toString();
                    //ParseObject message = ParseObject.create("Wiadomosc");
                    //message.put(USER_ID_KEY, ParseUser.getCurrentUser().getObjectId());
                    //message.put(BODY_KEY, data);
                    Message message = new Message();
                    message.setBody(data);
                    message.setUserId(ParseUser.getCurrentUser().getObjectId());
                    message.setUsername(ParseUser.getCurrentUser().getUsername());
                    message.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                //Toast.makeText(ChatActivity.this, "Pomyślnie utworzono wiadomość na Parse",
                                //       Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e(TAG, "Nie udało się zapisać wiadomości", e);
                            }
                        }
                    });
                    etMessage.setText(null);
                }
            }
        });
    }
    void refreshMessages() {
        // Construct query to execute
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        // Configure limit and sort order
        query.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);

        // get the latest 50 messages, order will show up newest to oldest of this group
        query.orderByDescending("createdAt");
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        query.findInBackground(new FindCallback<Message>() {
            public void done(List<Message> messages, ParseException e) {
                if (e == null) {
                    mMessages.clear();
                    mMessages.addAll(messages);
                    mAdapter.notifyDataSetChanged(); // update adapter
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    // Scroll to the bottom of the list on initial load
                    if (mFirstLoad) {
                        lvChat.setSelection(mAdapter.getCount() - 1);
                        mFirstLoad = false;
                    }
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });
    }
    void login() {
        Intent firstLogInIntent = getIntent();
        ParseUser.logInInBackground(firstLogInIntent.getStringExtra("username"),
                firstLogInIntent.getStringExtra("password"), new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            Toast.makeText(ChatActivity.this, "Zalogowano się pomyślnie.",
                                          Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChatActivity.this, "Nie udało się zalogować.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        /*ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Wystąpił problem przy anonimowym logowaniu: ", e);
                } else {
                    startWithCurrentUser();
                }
            }
        });
        */
    }
    void showFirstTimeLoginActivity() {
        Intent intent = new Intent(ChatActivity.this, FirstLoginActivity.class);
        startActivity(intent);
    }
}
