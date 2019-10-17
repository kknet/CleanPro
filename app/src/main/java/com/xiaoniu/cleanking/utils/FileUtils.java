package com.xiaoniu.cleanking.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.ui.main.bean.SecondJunkInfo;
import com.xiaoniu.common.utils.DateUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class FileUtils {

    public static boolean isAppInstalled(String str) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = AppApplication.getInstance().getPackageManager().getPackageInfo(str, 0);
        } catch (PackageManager.NameNotFoundException e) {
           // e.printStackTrace();
        }
        if (packageInfo != null) {
            return true;
        }else {
            return false;
        }
    }


    public static boolean isSystemApK(String str) {
        try {
            if ((AppApplication.getInstance().getPackageManager().getPackageInfo(str, 0).applicationInfo.flags & 1) > 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public static void showIconByFile(ImageView imageView, File file) {
        if (imageView != null && file != null) {
//          if (file.getAbsolutePath().endsWith(".apk") || file.getAbsolutePath().endsWith(".apk.1")) {
//                DisplayImageUtils.getInstance().displayImage(file.getAbsolutePath(), imageView);
//            } else
              if (file.getAbsolutePath().toLowerCase().endsWith(".mp3") || file.getAbsolutePath().toLowerCase().endsWith(".ape") || file.getAbsolutePath().toLowerCase().endsWith(".flac") || file.getAbsolutePath().toLowerCase().endsWith(".wav") || file.getAbsolutePath().toLowerCase().endsWith(".wma") || file.getAbsolutePath().toLowerCase().endsWith(".amr") || file.getAbsolutePath().toLowerCase().endsWith(".rm") || file.getAbsolutePath().toLowerCase().endsWith(".mwv") || file.getAbsolutePath().toLowerCase().endsWith(".amv")) {
                imageView.setImageResource(R.mipmap.icon_clean_music);
            } else if (file.getAbsolutePath().toLowerCase().endsWith(".doc") || file.getAbsolutePath().toLowerCase().endsWith(".docx")) {
                imageView.setImageResource(R.drawable.clean_icon_doc);
            } else if (file.getAbsolutePath().toLowerCase().endsWith(".pdf")) {
                imageView.setImageResource(R.drawable.clean_icon_pdf);
            } else if (file.getAbsolutePath().toLowerCase().endsWith(".ppt") || file.getAbsolutePath().toLowerCase().endsWith(".pptx")) {
                imageView.setImageResource(R.drawable.clean_icon_ppt);
            } else if (file.getAbsolutePath().toLowerCase().endsWith(".xls") || file.getAbsolutePath().toLowerCase().endsWith(".xlsx")) {
                imageView.setImageResource(R.drawable.clean_icon_xls);
            } else if (file.getAbsolutePath().toLowerCase().endsWith(".txt") || file.getAbsolutePath().toLowerCase().endsWith(".text")) {
                imageView.setImageResource(R.drawable.clean_icon_txt);
            } else if (file.getAbsolutePath().toLowerCase().endsWith(".zip") || file.getAbsolutePath().toLowerCase().endsWith(".rar") || file.getAbsolutePath().toLowerCase().endsWith(".7z") || file.getAbsolutePath().toLowerCase().endsWith(".iso")) {
                imageView.setImageResource(R.mipmap.icon_clean_zip);
            } else if (file.getAbsolutePath().toLowerCase().endsWith(".png") || file.getAbsolutePath().toLowerCase().endsWith(".jpg") || file.getAbsolutePath().toLowerCase().endsWith(".jpeg") || file.getAbsolutePath().toLowerCase().endsWith(".gif") || file.getAbsolutePath().toLowerCase().endsWith(".svg") || file.getAbsolutePath().toLowerCase().endsWith(".psd") || file.getAbsolutePath().toLowerCase().endsWith(".raw") || file.getAbsolutePath().toLowerCase().endsWith(".webp") || file.getAbsolutePath().toLowerCase().endsWith(".bmp") || file.getAbsolutePath().toLowerCase().endsWith(".tiff") || file.getAbsolutePath().toLowerCase().endsWith(".tga") || file.getAbsolutePath().toLowerCase().endsWith(".wmf")) {
                imageView.setTag(null);
                Glide.with(AppApplication.getInstance()).load("file://" + file.getAbsolutePath()).apply(new RequestOptions().dontAnimate().placeholder(R.drawable.clean_icon_pic).error(R.drawable.clean_icon_pic)).into(imageView);
            } else {
                imageView.setTag(null);
                Glide.with(AppApplication.getInstance()).load("file://" + file.getAbsolutePath()).apply(new RequestOptions().dontAnimate().placeholder(R.drawable.clean_icon_others).error(R.drawable.clean_icon_others)).into(imageView);
            }
        }
    }

    /*------------外置存储_私有路径cache下筛选------------*/

    /**
     * 遍历文件夹
     *
     * @param file
     * @return
     */
    public static SecondJunkInfo cacheListFiles(final File file) {
        final SecondJunkInfo secondlevelGarbageInfo = new SecondJunkInfo();
        cacheInnerListFiles(secondlevelGarbageInfo, file);
        return secondlevelGarbageInfo;
    }

    //递归遍历file下的所有文件
    public static void cacheInnerListFiles(final SecondJunkInfo secondJunkInfo, File file) {
        final File[] listFiles = file.listFiles();
        if (listFiles == null) {
            return;
        }
        try {
            if (listFiles.length == 0) {
                file.delete();
                return;
            }
        } catch (Exception ex) {
        }
        for (int length = listFiles.length, i = 0; i < length; ++i) {
            file = listFiles[i];
            if (file.isDirectory()) {
                cacheInnerListFiles(secondJunkInfo, file);
            } else {   //单个文件
                if (checkFile(file,3)) { //改文件操作超过三天
                    secondJunkInfo.setFilesCount(secondJunkInfo.getFilesCount() + 1);
                    secondJunkInfo.setGarbageSize(secondJunkInfo.getGarbageSize() + file.length());
                }

            }
        }
    }

    //判断是否超过n天
    public static boolean checkFile(File file,int count) {
        if (!file.exists()) {
            return false;
        }
        try {
            long time = file.lastModified();
            return DateUtils.isOverThreeDay(time,count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }


    //读取json文件
    public static String readJSONFromAsset(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }



    public static Map<String, String> checkOutAllGarbageFolder(final File file) {
        final HashMap<String, String> hashMap = new HashMap<String, String>();
        checAllkFiles(hashMap, file);
        return hashMap;
    }

    /**
     * 筛选file下需要清理的文件夹
     *
     * @param map
     * @param file
     *
     */
    private static Map<String, String> checAllkFiles(final Map<String, String> map, final File file) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        File[] listFiles = file.listFiles();
        if (listFiles != null && listFiles.length>0) {
            for (File file2 : listFiles) {
                String fileName = file2.getName();
                if (file2.isDirectory()) {//文件夹
                        checAllkFiles(map, file2);
                } else { //文件类型
                    map.put(file2.getAbsolutePath(), "残留文件");
                }
            }
        }else{
            try {
                file.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
