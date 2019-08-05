package com.ls.celtica.lsdelivryls.Ventes;

import android.database.sqlite.SQLiteStatement;

import com.ls.celtica.lsdelivryls.Accueil;
import com.ls.celtica.lsdelivryls.MyBD;

/**
 * Created by celtica on 07/02/19.
 */

public class Commande extends ProduitVendu {
    public Commande(String code_pr, String codebar, String nom, double qt, double prix) {
        super(code_pr, codebar, nom, qt, prix);
    }

    public void addCommandProduitToBd(int numBon,int id){
        Accueil.bd.write2("insert into produit_commande(id,code_pr,num_comm,quantity,prix_v,nom_pr) values('"+id+"','"+code_pr+"','"+numBon+"','"+qt+"','"+prix+"',?)", new MyBD.SqlPrepState() {
            @Override
            public void putValue(SQLiteStatement stmt) {
                stmt.bindString(1,nom);
                stmt.execute();
            }
        });

    }
}
