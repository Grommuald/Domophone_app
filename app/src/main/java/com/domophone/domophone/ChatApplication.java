package com.domophone.domophone;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.interceptors.ParseLogInterceptor;

public class ChatApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Message.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
        .applicationId("domophone_chat_app")
        .addNetworkInterceptor(new ParseLogInterceptor())
        .server("https://chat-app-domophone.herokuapp.com/parse/").build());
    }
}
