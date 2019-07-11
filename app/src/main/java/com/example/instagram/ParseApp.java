package com.example.instagram;

import android.app.Application;

import com.example.instagram.model.Post;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);
        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("alexei-instagram")
                .clientKey("thisistheinstamasterkey")
                .server("http://alexei-fbu-instagram.herokuapp.com/parse")
                .build();

        Parse.initialize(configuration);
    }
}


