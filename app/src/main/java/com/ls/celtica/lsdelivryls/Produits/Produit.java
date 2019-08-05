package com.ls.celtica.lsdelivryls.Produits;

import android.database.sqlite.SQLiteStatement;

import com.ls.celtica.lsdelivryls.Accueil;
import com.ls.celtica.lsdelivryls.Login;
import com.ls.celtica.lsdelivryls.MyBD;
import com.ls.celtica.lsdelivryls.Synchronisation;

import java.sql.SQLException;

/**
 * Created by celtica on 23/09/18.
 */

public class Produit {

    public  String code_pr,codebar,nom;
    public double prix_u,stock;

    public Produit(String code_pr, double stock, String codebar, String nom, double prix_u) {
        this.code_pr = code_pr;
        this.stock = stock;
        this.codebar = codebar;
        this.nom = nom;
        this.prix_u = prix_u;
    }

    public Produit(String code_pr ,double stock){
        this.code_pr=code_pr;
        this.stock=stock;
    }

    public void addPrToBD(){
        Accueil.bd.write2("insert into produit (code_pr,stock) values(?,?)", new MyBD.SqlPrepState() {
            @Override
            public void putValue(SQLiteStatement stmt) {
                try {
                    stmt.bindString(1, Login.user.convertNull(code_pr));
                    stmt.bindString(2, Login.user.convertNullDouble(String.valueOf(stock)));
                    stmt.execute();
                }catch (android.database.SQLException e) {
                   e.printStackTrace();
                }

            }
        });
    }
}
