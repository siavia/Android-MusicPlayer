package com.example.musicplayer.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.MediaStore;
import android.util.Log;

import com.example.musicplayer.entity.Song;
import com.example.musicplayer.helper.SongListHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MusicUtilsForSongList {
	/**
	 * 扫描db里面的自定义音频数据库，返回一个list集合
	 */
	public static List<Song> getMusicData(Context context) {
		List<Song> personnalList = new ArrayList<>();

		SongListHelper helper = new SongListHelper(context);
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("songlist",null,null,null,null,null,null);
		if(cursor != null){
			while (cursor.moveToNext()){
				Song song = new Song();
				song.setSong(cursor.getString(1));
				song.setSinger(cursor.getString(2));
				song.setPath(cursor.getString(3));
				song.setDuration(cursor.getInt(4));
				song.setSize(cursor.getLong(5));// TODO error? long
				Log.i("MusicUtilsForSongList", "getMusicData: song="+song);
				personnalList.add(song);
			}
		}
		cursor.close();
		db.close();
		return personnalList;
	}

	//格式化时间
	public static String formatTime(int time) {  //歌曲长度Duration是int类型的
		if (time / 1000 % 60 < 10) {
			return time / 1000 / 60 + ":0" + time / 1000 % 60;
		} else {
			return time / 1000 / 60 + ":" + time / 1000 % 60;
		}
	}
}
