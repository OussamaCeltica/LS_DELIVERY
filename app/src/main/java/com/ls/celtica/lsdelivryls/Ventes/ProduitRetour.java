package com.ls.celtica.lsdelivryls.Ventes;


import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.ls.celtica.lsdelivryls.Accueil;
import com.ls.celtica.lsdelivryls.MyBD;
import com.ls.celtica.lsdelivryls.Produits.Produit;

/**
 * Created by celtica on 13/02/19.
 */

public class ProduitRetour extends ProduitVendu {

    public ProduitRetour(String code_pr, String codebar, String nom, Double qt, double prix) {
        super(code_pr, codebar, nom, qt, prix);
    }

    public void addProduitTRetourToBd(int numBon,int id){
        Accueil.bd.write2("insert into produit_retour(id,code_pr,num_bon,quantity,prix_v,nom_pr) values('"+id+"','"+code_pr+"','"+numBon+"','"+qt+"','"+prix+"',?)", new MyBD.SqlPrepState() {
            @Override
            public void putValue(SQLiteStatement stmt) {
                stmt.bindString(1,nom);
                stmt.execute();
            }
        });

        Cursor r=Accueil.bd.read("select code_pr from produit where code_pr='"+code_pr+"'");
        if (r.moveToNext()){
            Accueil.bd.write("update produit set stock=stock+"+qt+" where code_pr='"+code_pr+"'");
        }else {
            Produit p=new Produit(code_pr,qt);
            p.addPrToBD();
        }



    }
}
