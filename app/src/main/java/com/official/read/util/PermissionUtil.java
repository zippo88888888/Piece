package com.official.read.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


/**
 * com.test.zzs.text.util
 * Created by ZP on 2017/5/2.
 * description:
 * version: 1.0
 */

public class PermissionUtil {

    public static final int LOCATION_CODE = 0x1001;
    public static final int STORAGE_CODE = 0x1002;
    public static final int PHONE_CODE = 0x1003;
    public static final int CAMERA_CODE = 0x1004;

    /** 定位权限 */
    public static final String ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    /** 读写SD卡权限 */
    public static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    /** 电话权限 */
    public static final String CALL_PHONE = Manifest.permission.CALL_PHONE;

    /** 调用相机权限 */
    public static final String CAMERA = Manifest.permission.CAMERA;

    /**
     * 判断是否申请过权限
     * @param context   Context
     * @param permissions   权限
     * @return true表示没有申请过
     */
    public static boolean hasPermission(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                return true;
        }
        return false;
    }

    /**
     * 请求权限
     * @param a Activity
     * @param code  请求码
     * @param requestPermission 权限
     */
    public static void requestPermission(Activity a, int code, String... requestPermission) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(a, requestPermission, code);
        }
    }

}
