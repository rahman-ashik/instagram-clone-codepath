package com.codepath.instagram;
import android.app.Application;

import com.codepath.instagram.model.Post;
import com.parse.Parse;
import com.parse.ParseObject;
public class ParseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Post.class);
        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("1DaOnayfEpGU1J8Hz9fmg7kDtvEru4nLWrxz6098")
                .clientKey("8PJYHYXwe5XPRrz41JJ4Nx3gY77bPK67mHkj97Up")
                .server("https://parseapi.back4app.com")
                .build();
        Parse.initialize(configuration);
    }
}
