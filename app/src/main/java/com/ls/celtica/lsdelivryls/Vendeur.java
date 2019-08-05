package com.ls.celtica.lsdelivryls;

import android.database.Cursor;

/**
 * Created by celtica on 03/02/19.
 */

public class Vendeur {
    public String code,name;

    public Vendeur(String code){
        this.code=code;
    }

    private void getName(){
        Cursor r=Accueil.bd.read("select nom_vendeur from vendeur where code_vendeur='"+code+"'");
        if (r.moveToNext()){
            name=r.getString(r.getColumnIndex("nom_vendeur"));

        }
    }
}
