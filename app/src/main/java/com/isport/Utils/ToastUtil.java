package com.isport.Utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Euphoria on 2017/7/27.
 */

public class ToastUtil {
    public static void setShortToast(Context context, String content){
        Toast.makeText(context,content,Toast.LENGTH_SHORT).show();
    }

    public static void setLongToast(Context context,String content){
        Toast.makeText(context,content,Toast.LENGTH_LONG).show();
    }
}
