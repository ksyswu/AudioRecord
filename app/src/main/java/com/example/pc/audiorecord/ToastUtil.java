package com.example.pc.audiorecord;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by pc on 2017-07-13.
 */

public class ToastUtil {

    private static Toast toast = null;


    public static void showToast(Context context, String msg) {

        if(toast != null ) {
            toast.cancel();
        }
        toast = toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();

    }
}
