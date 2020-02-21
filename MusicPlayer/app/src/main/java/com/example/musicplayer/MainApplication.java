package com.example.musicplayer;

import android.app.Application;
import android.media.MediaPlayer;

public class MainApplication extends Application {

    public static MediaPlayer mplayer = new MediaPlayer();

    public static MediaPlayer getMplayer() {
        return mplayer;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
