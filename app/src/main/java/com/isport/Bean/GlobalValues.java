package com.isport.Bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Euphoria on 2017/7/27.
 */

public class GlobalValues {
    //通信网址
    public static String baseUrl = "http://10.4.20.182:8080/";
    public static List<RunRecord> AllRunRecord = new ArrayList<>();
    public static List<ShareRecord> MyShareRecord = new ArrayList<>();
    public static List<ShareRecord> FriendShareRecord = new ArrayList<>();
    public static List<Friend> FriendList = new ArrayList<>();
    public static List<Friend> DiscoverList = new ArrayList<>();
    public static boolean signal1 = false;
}
