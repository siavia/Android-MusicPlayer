package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicplayer.activity.Favourite;
import com.example.musicplayer.activity.ThemeSet;
import com.example.musicplayer.adapter.LocalSongAdapter;
import com.example.musicplayer.adapter.SongListAdapter;
import com.example.musicplayer.entity.Song;
import com.example.musicplayer.helper.SongListHelper;
import com.example.musicplayer.ui.MyDialog;
import com.example.musicplayer.utils.MusicUtils;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;


//TODO bitmap 歌曲图片
public class MainActivity extends AppCompatActivity {

    private List<Song> list;
    private LocalSongAdapter adapter;
    public MainApplication myApp = (MainApplication) getApplication();
    public MediaPlayer mplayer = myApp.getMplayer();
    //public static MediaPlayer mplayer = new MediaPlayer();
    private Thread playCompleteThread;

    private ListView my_lv_songs;
    private View layout_playbar;
    private ImageView imageView;
    private ImageView imageView_play,imageView_next,imageView_front,imageView_repeat;
    TextView textView1;
    TextView textView2;
    private TextView my_tv_scan;
    SeekBar seekBar;
    private MyDialog dialog_loveSong;
    private NavigationView my_navigationView;
    private DrawerLayout my_drawerLayout;

    // 当前歌曲的序号（从0开始）
    private int currentposition = 0 ;// TODO sharedPreference
    // 该字符串用于判断主题
    private static String string_theme;
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        switch (string_theme){
            case "bule":{setTheme(R.style.blueTheme);break;}
            case "pink":{setTheme(R.style.pinkTheme);break;}
            case "green":{setTheme(R.style.greenTheme);break;}
        }
        //绑定指定视图
        setContentView(R.layout.my);
        //设置各种点击事件
        initAndSetListeners();
        //歌曲列表获取
        setListView();

    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == 1){
            if(resultCode == 1){
                Log.i("main", "onActivityResult: get it="+data.getStringExtra("theme"));
                string_theme = data.getStringExtra("theme");
                switch (string_theme){
                    case "blue":{
                        setTheme(R.style.blueTheme);
                        //绑定指定视图
                        setContentView(R.layout.my);
                        //歌曲列表获取
                        setListView();
                        //设置各种点击事件
                        initAndSetListeners();
                        break;
                    }
                    case "pink":{
                        setTheme(R.style.pinkTheme);
                        //绑定指定视图
                        setContentView(R.layout.my);
                        //歌曲列表获取
                        setListView();
                        //设置各种点击事件
                        initAndSetListeners();
                        break;
                    }
                    case "green":{
                        setTheme(R.style.greenTheme);
                        //绑定指定视图
                        setContentView(R.layout.my);
                        //歌曲列表获取
                        setListView();
                        //设置各种点击事件
                        initAndSetListeners();
                        break;
                    }
                }
            }
        }
    }


    //设置drawer的点击事件
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drawer_drawer,menu);
        return true;
    }


    //获取本地音乐并用listView显示
    private void setListView(){
        my_lv_songs = (ListView) this.findViewById(R.id.my_lv_songs);
        list = new ArrayList<Song>();
        list = MusicUtils.getMusicData(MainActivity.this);
        //Log.i("main.setListView()","list(0)="+list.get(0).toString());
        adapter = new LocalSongAdapter(MainActivity.this, list);
        //设置adapter
        my_lv_songs.setAdapter(adapter);
        //点击listview的item
        my_lv_songs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                currentposition = position;
                Log.i("click", "main.setListView().onItemClick--position="+position);
                if (mplayer.isPlaying()) {
                    mplayer.pause();
                }
                musicPlay(currentposition);
            }
        });
        //监听长按item，设置为喜爱歌曲
        dialog_loveSong = new MyDialog(MainActivity.this,R.layout.dialog_favourite);
        final Window window_loveSong = dialog_loveSong.getWindow();
        window_loveSong.setGravity(Gravity.CENTER);
        my_lv_songs.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                dialog_loveSong.show();
                TextView tv_yes = (TextView)dialog_loveSong.findViewById(R.id.favourite_yes); //TODO 学习一下怎么隔空取部件
                TextView tv_no = (TextView)dialog_loveSong.findViewById(R.id.favourite_no); //TODO 学习一下怎么隔空取部件
                //确认设置为喜爱歌曲
                tv_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog_loveSong.dismiss();
                        SongListHelper helper = new SongListHelper(MainActivity.this);
                        SQLiteDatabase db = helper.getWritableDatabase();
                        //清空表
//                        db.execSQL("delete from songlist");
//                        db.execSQL("update sqlite_sequence SET seq = 0 where name ='songlist'"); //自增长ID为0
                        //查询数据库中是否已经有这首歌了
                        Song song = list.get(position);
                        Cursor cursor = db.query("songlist",new String[]{"song"},"song=?",
                                new String[]{song.getSong()},null,null,null);
                        if(cursor.getCount() == 0){//若没有则添加进去数据库
                            ContentValues values = new ContentValues();
                            values.put("song",song.getSong());
                            values.put("singer",song.getSinger());
                            values.put("path",song.getPath());
                            values.put("duration",song.getDuration());
                            values.put("size",song.getSize());
                            db.insert("songlist",null,values);
                            Toast.makeText(MainActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                        }
                        else{ //提示用户,数据库中已经有这首歌了
                            cursor.moveToNext();
                            Log.i("tv_yes", "onClick: song="+cursor.getString(0));
                            Toast.makeText(MainActivity.this, "数据库中已存在这首歌", Toast.LENGTH_SHORT).show();
                        }
                        cursor.close();
                        db.close();
                    }
                });
                //取消
                tv_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog_loveSong.dismiss();
                    }
                });
                return false;
            }
        });


    }

    //TODO 初始化上次关闭时的数据
    private void setOnStartInit(){

    }

    //播放指定歌曲
    private void musicPlay(int position){
        try{
            File f=new File(list.get(position).getPath());
            //如果歌曲不存在
            if(!f.exists())
            {
                Log.i("play", "file not exist! "+list.get(position).getSong()+",position="+position);
                Toast.makeText(MainActivity.this,"file not exist! ",Toast.LENGTH_SHORT).show();
                //TODO 从数据库中删除
                //从list中删除
                list.remove(position);
                for (Song newsong : list) {
                    Log.i("play", "id:" + newsong.getSong());
                }
                // 显示setListView()
                //adapter = new LocalSongAdapter(MainActivity.this, list);
                my_lv_songs.setAdapter(adapter);
            }
            else{
                seekBar.setMax(list.get(position).getDuration());
                Log.i("musicPlay()", "musicPlay: getDuration="+list.get(position).getDuration());
                try {
                    Log.i("main.musicPlay()","path="+list.get(position).getPath());
                    mplayer.reset();
                    mplayer.setDataSource(list.get(position).getPath());
                    mplayer.prepare();
                    mplayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //开启歌曲播放完毕的监听线程
                playCompleteThread = new Thread(new PlayCompleteThread());
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
                //images
                String albumArt = list.get(position).getCover();
                Bitmap bm = null;
                if (albumArt == null) {
                    Log.i("main", "test: here");
                    imageView.setImageResource(R.mipmap.default_cover);
                }
                else {
                    bm = BitmapFactory.decodeFile(albumArt);
                    BitmapDrawable bmpDraw = new BitmapDrawable(getResources(),bm);
//            BitmapDrawable bmpDraw = new BitmapDrawable(bm);这个函数用不了了
                    Log.i("musicplay", "musicPlay: bmpDraw="+bmpDraw);
                    imageView.setImageDrawable(bmpDraw);
                }
            }
        }catch (Exception e){
            Log.e("my", "musicPlay:file not exist! " );
        }
    }

    //各种点击事件的初始化和监听
    private void initAndSetListeners(){
        //注意：如果是从别的layout布局（include的）找部件，那就要先得到那个layout的view，如下这样
        layout_playbar = (View) findViewById(R.id.my_playbar);//这是<include>那里的id名,不是对应layout文件的id
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
        my_drawerLayout = (DrawerLayout) findViewById(R.id.my_drawerLayout);
        my_navigationView = (NavigationView) findViewById(R.id.my_navigationView);
        my_tv_scan = (TextView) findViewById(R.id.my_tv_scan);


        //构建的初始化模样
        switch (play_style){
            case 0:{imageView_repeat.setImageResource(R.mipmap.repeat_single);break;}
            case 1:{imageView_repeat.setImageResource(R.mipmap.repeat_mul);break;}
            case 2:{imageView_repeat.setImageResource(R.mipmap.repeat_random);break;}
        }

        //侧滑栏里面的选项
        my_navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_theme: {
                        Log.i("onOptionsItemSelected", "onOptionsItemSelected: nav_theme");
                        int picPause;
                        switch (string_theme){
                            case "bule":{picPause = R.mipmap.pause;break;}
                            case "pink":{picPause = R.mipmap.pause_pink;break;}
                            case "green":{picPause = R.mipmap.pause_green; break;}
                            default:{picPause = R.mipmap.pause; break;}
                        }
                        if (mplayer.isPlaying()) {
                            mplayer.pause();
                            imageView_play.setImageResource(picPause);
                        }
                        Intent intent = new Intent(MainActivity.this, ThemeSet.class);
                        startActivityForResult(intent, 1);
                        break;
                    }
                    case R.id.nav_love:{
                        mplayer.stop();//线程在监听这个是否在播放，所以只要stop了，那么就能终止线程了
                        //saveSongList();//TODO other place 如果要保存播放位置的话就需要处理一下
                        Intent intent = new Intent(MainActivity.this, Favourite.class);
                        startActivity(intent);
                        break;
                    }
                    case R.id.nav_about:{
                        final MyDialog dialog = new MyDialog(MainActivity.this,R.layout.dialog_single_choice);
                        Window myWindow = dialog.getWindow();
                        myWindow.setGravity(Gravity.CENTER);

                        dialog.show();//注意一定要先show（）然后再findViewById，不然会找不到
                        TextView single_tv_context = (TextView)dialog.findViewById(R.id.single_tv_context);
                        TextView single_tv_yes = (TextView)dialog.findViewById(R.id.single_tv_yes);
                        single_tv_context.setText("作者：熠仔\n邮箱：liyiyiofficial@163.com");
                        single_tv_yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        break;
                    }
                    default:{break;}
                }

                return false;
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
                playCompleteThread = new Thread(new PlayCompleteThread());
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

        //扫描歌曲
        my_tv_scan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.i("test" ,"onClick: in2");
                setListView();
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
        } else {
            mplayer.start();
            imageView_play.setImageResource(picPlay);
            playCompleteThread = new Thread(new PlayCompleteThread());
            playCompleteThread.start();
        }
    }

    //下一首（包括判断当前播放循环状态,3种）
    //上一首（包括判断当前播放循环状态）
    // TODO 把歌单做出来之后，按照历史记录来找上一首，因为随机下一首再找上一首会不是那首歌了

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_theme:
                Log.i("onOptionsItemSelected", "onOptionsItemSelected: nav_theme");
                break;
        }
        return super.onOptionsItemSelected(item);
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
        PermissionGen.with(MainActivity.this)
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


    //下面两个函数是用于封面获取的测试函数
    private void test(){
        Song song = list.get(9);

        int album_id = song.getAlbum_id();
        Log.i("main", "test: "+album_id);
        String albumArt = getAlbumArt(album_id);
        Bitmap bm = null;
        if (albumArt == null) {
            Log.i("main", "test: here");
            imageView.setImageResource(R.mipmap.default_cover);
        }
        else {
            bm = BitmapFactory.decodeFile(albumArt);
            BitmapDrawable bmpDraw = new BitmapDrawable(bm);
            ((ImageView) imageView).setImageDrawable(bmpDraw);
        }
    }


    private String getAlbumArt(int album_id) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[] { "album_art" };
        Cursor cur = this.getContentResolver().query(Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)),projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        cur = null;
        Log.i("main", "getAlbumArt: "+album_art);
        return album_art;
    }

}













