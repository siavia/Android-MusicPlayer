package com.example.musicplayer.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SongListHelper extends SQLiteOpenHelper {

    public SongListHelper(Context context) {
        super(context, "musicplay.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //建立记录歌单中歌曲的表（目前只做了喜爱列表，可扩展自定义歌单）
        db.execSQL(" create table songlist(_id integer primary key autoincrement," +
                                            "song varchar(200)," +
                                            "singer varchar(200)," +
                                            "path varchar(500)," +
                                            "duration integer," +
                                            "size integer," +
                                            "cover varchar(500)," +
                                            "sort varchar(20))" );
//        //建立记录本地音乐列表的表
//        db.execSQL(" create table locallist(_id integer primary key autoincrement," +
//                                            "song varchar(200)," +
//                                            "singer varchar(200)," +
//                                            "path varchar(500)," +
//                                            "duration integer," +
//                                            "size integer," +
//                                            "cover varchar(500)," +
//                                            "sort varchar(20))" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
