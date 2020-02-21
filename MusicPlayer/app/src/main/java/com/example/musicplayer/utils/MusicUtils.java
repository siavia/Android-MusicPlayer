package com.example.musicplayer.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.example.musicplayer.R;
import com.example.musicplayer.entity.Song;
import com.example.musicplayer.helper.SongListHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicUtils {

	/**
	 * 扫描系统里面的音频文件，返回一个list集合
	 */
	public static List<Song> getMusicData(Context context) {
		List<Song> list = new ArrayList<>();
//		//1.初始化数据库中的表locallist
//		SongListHelper helper = new SongListHelper(context);
//		SQLiteDatabase db = helper.getWritableDatabase();
//		//清空表
//		db.execSQL("delete from locallist");
//		db.execSQL("update sqlite_sequence SET seq = 0 where name ='locallist'"); //自增长ID为0
		//1.扫描本地音乐
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Audio.AudioColumns.IS_MUSIC);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				Song song = new Song();
				song.setSong( cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
				song.setSinger( cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
				song.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
				song.setDuration( cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
				song.setAlbum_id( cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));
				//歌曲长度Duration是int类型的
				song.setSize( cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));
				if (song.getSize() > 1000 * 800) {//过滤掉短音频
					// 分离出歌曲名和歌手
					if (song.getSong().contains("-")) {
						String[] str = song.getSong().split("-");
						song.setSinger( str[0]);
						song.setSong( str[1]);
					}
					list.add(song);
				}
				//for images
				int album_id = song.getAlbum_id();
				Log.i("main", "test: "+album_id);
				String albumArt = getAlbumArt(album_id,context);
				if(albumArt!=null){ //如果找到了封面的路径
					try{
						File f=new File(albumArt);
						if(f.exists()){  //如果搜到路径，且路径文件存在
							song.setCover(albumArt);
						}
					}catch (Exception e){
						Log.e("file not exist", "MusicUtils.getMusicData: " );
					}
				}
			}
			// 释放资源
			cursor.close();
		}
		return list;
	}

	private static String getAlbumArt(int album_id,Context context) {
		String mUriAlbums = "content://media/external/audio/albums";
		String[] projection = new String[] { "album_art" };
		Cursor cur = context.getContentResolver().query(Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)),projection, null, null, null);
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


	//格式化时间
	public static String formatTime(int time) {  //歌曲长度Duration是int类型的
		if (time / 1000 % 60 < 10) {
			return time / 1000 / 60 + ":0" + time / 1000 % 60;
		} else {
			return time / 1000 / 60 + ":" + time / 1000 % 60;
		}
	}
}
