package com.example.musicplayer.entity;

public class Song {
	/** * 歌手 */
	private String singer;
	/** * 歌曲名 */
	private String song;
	/** * 歌曲的地址 */
	private String path;
	/** * 歌曲长度 */
	private int duration;
	/** * 歌曲的大小 */
	private long size;
	/** * 歌曲所在的id名字，在获取封面的时候会用到，检索本地音乐时get的id */
	private int album_id;
	/** * 封面路径 */
	private String cover;
	/** * 歌曲的流派类别(其实还没用到) */
	private String sort;

	public String getSinger() {
		return singer;
	}

	public void setSinger(String singer) {
		this.singer = singer;
	}

	public String getSong() {
		return song;
	}

	public void setSong(String song) {
		this.song = song;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public int getAlbum_id() {
		return album_id;
	}

	public void setAlbum_id(int album_id) {
		this.album_id = album_id;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	@Override
	public String toString() {
		return "Song{" +
				"singer='" + singer + '\'' +
				", song='" + song + '\'' +
				", path='" + path + '\'' +
				", duration=" + duration +
				", size=" + size +
				", album_id=" + album_id +
				", sort='" + sort + '\'' +
				'}';
	}
}
