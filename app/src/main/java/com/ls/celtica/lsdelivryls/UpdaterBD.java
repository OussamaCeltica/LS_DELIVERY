package com.ls.celtica.lsdelivryls;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by celtica on 02/05/19.
 */

public class UpdaterBD {
    public static void update(AppCompatActivity c) throws PackageManager.NameNotFoundException {
        PackageManager manager = c.getPackageManager();
        PackageInfo info = manager.getPackageInfo(c.getPackageName(), PackageManager.GET_ACTIVITIES);

        int  oldVersion=-1;
        try {
            Cursor r=Accueil.bd.read("select code_version from admin");
            if(r.moveToNext()){
                oldVersion=r.getInt(r.getColumnIndex("code_version"));

                Accueil.bd.write("update admin set code_version='"+info.versionCode+"' ");
            }
        }
        catch(SQLiteException e) {
            Accueil.bd.write("alter table admin add code_version varchar(30) ");
            Accueil.bd.write("update admin set code_version='"+info.versionCode+"' ");
        }

    }
}
