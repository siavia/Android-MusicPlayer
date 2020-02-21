package com.example.musicplayer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.LocalSongAdapter;
import com.example.musicplayer.entity.Song;
import com.example.musicplayer.ui.MyDialog;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class Detail extends AppCompatActivity {

    private List<Song> list;
    public static MediaPlayer mplayer = new MediaPlayer();
    private Thread playCompleteThread;

    private ListView my_lv_songs;
    private View layout_playbar;
    private ImageView imageView;
    private ImageView imageView_play,imageView_next,imageView_front,imageView_repeat;
    TextView textView1;
    TextView textView2;
    SeekBar seekBar;
    private MyDialog dialog_loveSong;
    private NavigationView my_navigationView;
    private DrawerLayout my_drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        Intent intent = getIntent();


    }



}
