package com.example.musicplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.musicplayer.MainActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.entity.Song;
import com.example.musicplayer.utils.MusicUtils;

import java.util.List;

public class LocalSongAdapter extends BaseAdapter {

    // TODO 用于判断颜色的position_flag
    private Context context;
    private List<Song> list;

    public LocalSongAdapter(MainActivity mainActivity, List<Song> list) {
        this.list = list;
        this.context = mainActivity;
    }

    //得到item总数
    @Override
    public int getCount() {
        return list.size();
    }
    //得到item代表的对象
    @Override
    public Object getItem(int i) {
        return list.get(i);
    }
    //得到item的id
    @Override
    public long getItemId(int i) {
        return i;
    }
    //得到item的view视图
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder;
        if(view == null){
            holder = new ViewHolder();
            //引入布局
            view = View.inflate(context, R.layout.song_item,null);
            //实例化对象
            holder.song = (TextView) view.findViewById(R.id.item_tv_songname);
            holder.singer = (TextView) view.findViewById(R.id.item_tv_singer);
            holder.duration = (TextView) view.findViewById(R.id.item_tv_duration);
            holder.position = (TextView) view.findViewById(R.id.item_tv_index);
            view.setTag(holder);
        }
        else{
            holder = (ViewHolder)view.getTag();
        }

        //给控件赋值
        String string_song = list.get(i).getSong();
        if (string_song.length() >= 5
                && string_song.substring(string_song.length() - 4,
                string_song.length()).equals(".mp3")) { //歌名带有.mp3的
            holder.song.setText(string_song.substring(0,
                    string_song.length() - 4).trim());
        } else {  //歌名没有.mp3的
            holder.song.setText(string_song.trim());
        }
        holder.singer.setText(list.get(i).getSinger().toString().trim());
        // 时间需要转换一下
        int duration = list.get(i).getDuration();
        String time = MusicUtils.formatTime(duration);
        holder.duration.setText(time);
        holder.position.setText(i+1+"");

        return view;
    }

    class ViewHolder {
        TextView song;// 歌曲名
        TextView singer;// 歌手
        TextView duration;// 时长
        TextView position;// 序号
    }
}
