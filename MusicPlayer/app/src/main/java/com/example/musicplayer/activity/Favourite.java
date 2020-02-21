package com.example.musicplayer.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicplayer.MainActivity;
import com.example.musicplayer.MainApplication;
import com.example.musicplayer.R;
import com.example.musicplayer.adapter.LocalSongAdapter;
import com.example.musicplayer.adapter.SongListAdapter;
import com.example.musicplayer.entity.Song;
import com.example.musicplayer.helper.SongListHelper;
import com.example.musicplayer.utils.MusicUtils;
import com.example.musicplayer.utils.MusicUtilsForSongList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

public class Favourite extends AppCompatActivity {

    private List<Song> list;
    private SongListAdapter adapter;
    public MainApplication myApp = (MainApplication) getApplication();
    public MediaPlayer mplayer = myApp.getMplayer();
//    public MediaPlayer mplayer =new MediaPlayer();
    private Thread playCompleteThread;

    private ListView favourite_lv_songs;
    private View layout_playbar;
    private ImageView imageView;
    private TextView favourite_tv_text;
    private ImageView imageView_play,imageView_next,imageView_front,imageView_repeat;
    TextView textView1;
    TextView textView2;
    SeekBar seekBar;

    // 当前歌曲的序号（从0开始）
    private int currentposition = 0 ;// TODO sharedPreference
    // 该字符串用于判断主题
    private String string_theme;
    // seekbar是否在拖动
    private boolean ischanging = false;
    // 用于判断当前的播放顺序，0->单曲循环,1->顺序播放,2->随机播放
    private int play_style = 0;
    //关闭seekbar监控进程,通过个数来确定进程是否继续
    private int threadNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取权限
        getAuthority();
        //设置主题
        string_theme = "bule"; //TODO sharedPreferences，恢复上次关闭时的主题
        switch (string_theme){
            case "bule":{setTheme(R.style.blueTheme);break;}
            case "pink":{setTheme(R.style.pinkTheme);break;}
            case "green":{setTheme(R.style.greenTheme);break;}
        }
        //绑定指定视图
        setContentView(R.layout.myfavourite);
        //歌曲列表获取
        setListView();
        //设置各种点击事件
        initAndSetListeners();
    }

    //获取本地音乐并用listView显示
    private void setListView(){
        favourite_lv_songs = (ListView) this.findViewById(R.id.favourite_lv_songs);
        list = new ArrayList<Song>();
        list = MusicUtilsForSongList.getMusicData(Favourite.this);
        adapter = new SongListAdapter(Favourite.this, list);
        favourite_lv_songs.setAdapter(adapter);
        favourite_lv_songs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                currentposition = position;
                Log.i("click", "main.setListView().onItemClick--position="+position);
                musicPlay(currentposition);
            }
        });
    }

    //播放指定歌曲
    private void musicPlay(int position){
        try
        {
            File f=new File(list.get(position).getPath());
            //如果歌曲不存在
            if(!f.exists())
            {
                Log.i("play", "file not exist! ");
                Toast.makeText(Favourite.this,"file not exist! ",Toast.LENGTH_SHORT).show();
                //从数据库中删除
                SongListHelper helper = new SongListHelper(Favourite.this);
                SQLiteDatabase db = helper.getWritableDatabase();
                db.execSQL("delete from songlist where path='"+list.get(position).getPath()+"'");
                //从list中删除
                list.remove(position);
                for (Song newsong : list) {
                    Log.i("play", "id:" + newsong.getSong());
                }
                favourite_lv_songs.setAdapter(adapter);
            }
            //歌曲存在
            else{
                seekBar.setMax(list.get(position).getDuration());
                Log.i("musicPlay()", "musicPlay: getDuration="+list.get(position).getDuration());
                try {
                    if(mplayer.isPlaying())
                        mplayer.stop();
                    if(mplayer.isPlaying())
                        mplayer.stop();
                    Log.i("main.musicPlay()","path="+list.get(position).getPath());
                    mplayer.reset();
                    mplayer.setDataSource(list.get(position).getPath());
                    mplayer.prepare();
                    mplayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //开启歌曲播放完毕的监听线程
                playCompleteThread = new Thread(new Favourite.PlayCompleteThread());
                playCompleteThread.start();

                String string_song = list.get(position).getSong();
                if (string_song.length() >= 5
                        && string_song.substring(string_song.length() - 4,
                        string_song.length()).equals(".mp3")) { //歌名带有.mp3的
                    textView1.setText(string_song.substring(0,string_song.length() - 4).trim());
                } else {  //歌名没有.mp3的
                    textView1.setText(string_song.trim());
                }
                textView2.setText(list.get(position).getSinger());
            }
        }
        catch (Exception e)
        {
            Log.i("play", "musicPlay: file not exist!");
        }
    }

    //各种点击事件的初始化和监听
    private void initAndSetListeners(){
        favourite_tv_text = (TextView)findViewById(R.id.favourite_tv_text);
        //注意：如果是从别的layout布局（include的）找部件，那就要先得到那个layout的view，如下这样
        layout_playbar = (View) findViewById(R.id.favourite_playbar);//这是<include>那里的id名,不是对应layout文件的id
        imageView = (ImageView) layout_playbar.findViewById(R.id.imageview);
        imageView_play = (ImageView) layout_playbar
                .findViewById(R.id.imageview_play);
        imageView_next = (ImageView) layout_playbar
                .findViewById(R.id.imageview_next);
        imageView_front = (ImageView) layout_playbar
                .findViewById(R.id.imageview_front);
        imageView_repeat = (ImageView) layout_playbar
                .findViewById(R.id.imageview_repeat);
        textView1 = (TextView) layout_playbar.findViewById(R.id.name);
        textView2 = (TextView) layout_playbar.findViewById(R.id.singer);
        seekBar = (SeekBar) layout_playbar.findViewById(R.id.seekbar);

        //构建的初始化模样
        switch (play_style){
            case 0:{imageView_repeat.setImageResource(R.mipmap.repeat_single);break;}
            case 1:{imageView_repeat.setImageResource(R.mipmap.repeat_mul);break;}
            case 2:{imageView_repeat.setImageResource(R.mipmap.repeat_random);break;}
        }

        //长按标题栏清空喜爱列表
        favourite_tv_text.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view) {
                SongListHelper helper = new SongListHelper(Favourite.this);
                SQLiteDatabase db = helper.getWritableDatabase();
                //清空表
                db.execSQL("delete from songlist");
                db.execSQL("update sqlite_sequence SET seq = 0 where name ='songlist'"); //自增长ID为0
                setListView();
                return false;
            }
        });
        favourite_tv_text.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(Favourite.this,"长按清空喜爱列表",Toast.LENGTH_SHORT).show();
            }
        });

        //播放键
        imageView_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause();
            }
        });

        //上一首
        imageView_front.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                currentposition--;
                if(currentposition < 0)
                    currentposition = list.size()-1;
                Log.i("click", "imageView_front: currentposition="+currentposition);
                musicPlay(currentposition);
            }
        });

        //下一首
        imageView_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentposition++;
                if(currentposition > list.size()-1)
                    currentposition = 0;
                Log.i("click", "imageView_next: currentposition="+currentposition);
                musicPlay(currentposition);
            }
        });

        //进度条
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ischanging = false;
                //注意，拖动进度条滑块之后，不是调用musicplay()函数中的mplay.start(),而是用seekto()
                mplayer.seekTo(seekBar.getProgress());
                //同样的，只要在播放，就要监听结束的时候
                playCompleteThread = new Thread(new Favourite.PlayCompleteThread());
                playCompleteThread.start();  // TODO how to stop thread
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                ischanging = true;
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO 写拖动的时候实时显示时间
            }
        });

        //监听mplayer的结束时间，在结束播放时做反应
        mplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //判断当前的播放顺序，0->单曲循环,1->顺序播放,2->随机播放
                switch (play_style){
                    case 0:{musicPlay(currentposition);break;}
                    case 1:{
                        currentposition++;
                        if(currentposition > list.size()-1)
                            currentposition = 0;
                        musicPlay(currentposition);
                        break;
                    }
                    case 2:{
                        Random random = new Random();
                        currentposition = currentposition + random.nextInt(list.size() - 1);
                        currentposition %= list.size();
                        musicPlay(currentposition);
                        break;
                    }
                }
            }
        });

        //循环,判断循环按钮应更换什么图片,0->单曲循环,1->顺序播放,2->随机播放
        imageView_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play_style++;
                if (play_style > 2) {
                    play_style = 0;
                }
                switch (play_style){
                    case 0:{
                        imageView_repeat.setImageResource(R.mipmap.repeat_single);
                        break;
                    }
                    case 1:{
                        imageView_repeat.setImageResource(R.mipmap.repeat_mul);
                        break;
                    }
                    case 2:{
                        imageView_repeat.setImageResource(R.mipmap.repeat_random);
                        break;
                    }
                }

            }
        });


    }


    //播放键、暂停
    private void pause(){
        Log.i("click", "imageView_play ");
        //判断暂停键应该更换什么图片
        int picPause,picPlay;
        switch (string_theme){
            case "bule":{picPause = R.mipmap.pause; picPlay = R.mipmap.play;break;}
            case "pink":{picPause = R.mipmap.pause_pink; picPlay = R.mipmap.play_pink;break;}
            case "green":{picPause = R.mipmap.pause_green; picPlay = R.mipmap.play_green;break;}
            default:{picPause = R.mipmap.pause; picPlay = R.mipmap.play;break;}
        }
        if (mplayer.isPlaying()) {
            mplayer.pause();
            imageView_play.setImageResource(picPause);
            //TODO 设置播放的时候的动态旋转效果，如唱片播放
            //imageview.clearAnimation();
        } else {
            mplayer.start();
            imageView_play.setImageResource(picPlay);
            playCompleteThread = new Thread(new Favourite.PlayCompleteThread());
            playCompleteThread.start();
            //imageview.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.imageview_rotate));
        }
    }


    //监听播放完毕
    class PlayCompleteThread implements Runnable {
        @Override
        public void run() {
            Log.i("PlayCompleteThread", "run: thread start");
            threadNum++;
            while (!ischanging && mplayer.isPlaying()) { //要等到播放完毕这个线程才会结束
                if(threadNum > 1)
                    break;
                // 将SeekBar位置设置到当前播放位置
                seekBar.setProgress(mplayer.getCurrentPosition());
                try {
                    // 每500毫秒更新一次位置
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            threadNum--;
            Log.i("PlayCompleteThread", "run: thread end");
        }
    }

    //适配6.0以上机型请求权限
    private void getAuthority(){
        PermissionGen.with(Favourite.this)
                .addRequestCode(100)
                .permissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .request();
    }

    //以下三个方法用于6.0以上权限申请适配
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = 100)
    public void doSomething(){
        //Toast.makeText(this, "相关权限已允许", Toast.LENGTH_SHORT).show();
    }

    @PermissionFail(requestCode = 100)
    public void doFailSomething(){
        //Toast.makeText(this, "相关权限已拒绝", Toast.LENGTH_SHORT).show();
    }




}
