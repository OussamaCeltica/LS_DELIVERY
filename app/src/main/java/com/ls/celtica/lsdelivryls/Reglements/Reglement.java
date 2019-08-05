package com.ls.celtica.lsdelivryls.Reglements;

import android.database.Cursor;

import com.ls.celtica.lsdelivryls.Accueil;
import com.ls.celtica.lsdelivryls.Login;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by celtica on 25/09/18.
 */

public class Reglement {
    public String nom_client,mode,date,code_client;
    public double montant;
    public int id_rglmt;

    public Reglement(String nom_client, String mode, String date, double montant) {
        this.nom_client = nom_client;
        this.mode = mode;
        this.date = date;
        this.montant = montant;
    }

    public Reglement(String code_client,String nom_client){
        this.code_client=code_client;
        this.nom_client=nom_client;
        id_rglmt=0;
        Cursor r= Accueil.bd.read("select id_rglmt from reglement order by id_rglmt desc limit 1 ");
        if(r.moveToNext()){
            id_rglmt=Integer.parseInt(r.getString(r.getColumnIndex("id_rglmt")))+1;
        }
    }

    public   void AddReglementToBD(double montant,String mode){

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = df.format(Calendar.getInstance().getTime());

        Accueil.bd.write("insert into reglement (id_rglmt,code_clt,montant,mode,date_rglmt,sync,code_vendeur,nom_clt) values('"+id_rglmt+"','"+code_client+"','"+montant+"','"+mode+"','"+date+"','0','"+ Login.user.vendeur+"','"+nom_client.replaceAll("'","`")+"')");

        Accueil.bd.write("update client set solde=solde+("+montant+") where code_clt='"+code_client+"'");

    }
}
