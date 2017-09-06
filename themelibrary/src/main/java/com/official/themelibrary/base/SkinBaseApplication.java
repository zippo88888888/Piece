package com.official.themelibrary.base;

import android.app.Application;

import com.official.themelibrary.SkinConfig;
import com.official.themelibrary.loader.SkinManager;
import com.official.themelibrary.utils.SkinFileUtils;

import java.io.File;
import java.io.IOException;



/**
 * Created by _SOLID
 * Date:2016/4/14
 * Time:10:54
 * Desc:
 */
public class SkinBaseApplication extends Application {

    public void onCreate() {

        super.onCreate();
        initSkinLoader();
    }

    /**
     * Must call init first
     */
    private void initSkinLoader() {
        setUpSkinFile();
        SkinManager.getInstance().init(this);
        if (!SkinConfig.isInNightMode(this)) {
            SkinManager.getInstance().loadSkin(null);
        } else {
            SkinManager.getInstance().NightMode();
        }

    }

    private void setUpSkinFile() {
        try {
            String[] skinFiles = getAssets().list(SkinConfig.SKIN_DIR_NAME);
            for (String fileName : skinFiles) {
                File file = new File(SkinFileUtils.getSkinDir(this), fileName);
                if (!file.exists())
                    SkinFileUtils.copySkinAssetsToDir(this, fileName, SkinFileUtils.getSkinDir(this));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
