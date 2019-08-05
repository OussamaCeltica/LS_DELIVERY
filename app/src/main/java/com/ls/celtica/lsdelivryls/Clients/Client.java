package com.ls.celtica.lsdelivryls.Clients;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.ls.celtica.lsdelivryls.Accueil;
import com.ls.celtica.lsdelivryls.Login;
import com.ls.celtica.lsdelivryls.MyBD;

import java.sql.SQLException;

/**
 * Created by celtica on 23/09/18.
 */

public class Client {
    public String codebar,nom,tel,adr,type;
    public double latitude,longitude,solde,verser;


    public Client(String codebar, String nom, String tel, String adr, double latitude, double longitude, double solde, double verser,String type) {
        this.codebar = codebar;
        this.nom = nom;
        this.tel = tel;
        this.adr = adr;
        this.latitude = latitude;
        this.longitude = longitude;
        this.solde = solde;
        this.verser = verser;
        this.type=type;
    }

    public Client(String codebar){
        this.codebar=codebar;
    }

    public void addToBD(){
        Accueil.bd.write2("insert into client (code_clt,nom_clt,tél,adr_clt,latitude,longitude,solde,verser,sync,type) values(?,?,?,?,?,?,?,?,1,?)", new MyBD.SqlPrepState() {
            @Override
            public void putValue(SQLiteStatement stmt)  {
                try {
                    stmt.bindString(1, Login.user.convertNull(codebar));
                    stmt.bindString(2,Login.user.convertNull(nom));
                    stmt.bindString(3,Login.user.convertNull(tel));
                    stmt.bindString(4,Login.user.convertNull(adr));
                    stmt.bindString(5,Login.user.convertNullDouble(String.valueOf(latitude)));
                    stmt.bindString(6,Login.user.convertNullDouble(String.valueOf("LONGITUDE")));
                    stmt.bindString(7,Login.user.convertNullDouble(String.valueOf("SOLDE")));
                    stmt.bindString(8,Login.user.convertNullDouble(String.valueOf("VERSER")));
                    // stmt.bindDouble(9,1);
                    stmt.bindString(9,Login.user.convertNull(type));
                    stmt.execute();


                } catch (android.database.SQLException e) {
                    e.printStackTrace();
                    Log.e("sqll","f sync .."+e.getMessage());

                }

            }
        });

    }

    public double getSolde(){
        Cursor r=Accueil.bd.read("select solde from client where code_clt='"+codebar+"'");
        if (r.moveToNext()){
            return r.getDouble(r.getColumnIndex("solde"));
        }else {
            return 0;
        }
    }


}
