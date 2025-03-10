package com.test.others;
import java.util.LinkedList;

public class LinkedListDemo {
    public static void main(String[] args) {
        LinkedList<String> playlist = new LinkedList<>();
        playlist.add("歌曲1");
        playlist.add("歌曲2");
        playlist.add("歌曲3");
        
        System.out.println("播放清單：" + playlist);
        playlist.removeFirst();
        System.out.println("播放下一首後的清單：" + playlist);
    }
}