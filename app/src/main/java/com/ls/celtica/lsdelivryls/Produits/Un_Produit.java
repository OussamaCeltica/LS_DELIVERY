package com.ls.celtica.lsdelivryls.Produits;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ls.celtica.lsdelivryls.Accueil;
import com.ls.celtica.lsdelivryls.Produits.ProduitAdapter;
import com.ls.celtica.lsdelivryls.R;

public class Un_Produit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_un_produit);

        TextView nom,stock,codebarre,prix;
        nom=(TextView)findViewById(R.id.nom_pr);
        stock=(TextView)findViewById(R.id.stock_pr);
        codebarre=(TextView)findViewById(R.id.code_pr);
        prix=(TextView)findViewById(R.id.prix_pr);

        //region Information de produit
        nom.setText(ProduitAdapter.produits.get(ProduitAdapter.itemSelected).nom);
        stock.setText(ProduitAdapter.produits.get(ProduitAdapter.itemSelected).stock+"");
        codebarre.setText(ProduitAdapter.produits.get(ProduitAdapter.itemSelected).codebar);
        prix.setText(ProduitAdapter.produits.get(ProduitAdapter.itemSelected).prix_u+" DA");
        //endregion

        //region prix de produit
        LinearLayout div_affichPrix=(LinearLayout)findViewById(R.id.pr_divAffichPrix);
        Cursor r= Accueil.bd.read("select * from produit_erp_prix_type where code_pr='"+ProduitAdapter.produits.get(ProduitAdapter.itemSelected).code_pr+"'");
        while (r.moveToNext()){
            View v=getLayoutInflater().inflate(R.layout.div_prix_type,null);
            TextView type,priix;
            type=(TextView)v.findViewById(R.id.div_prix_type);
            priix=(TextView)v.findViewById(R.id.div_prix_prix);

            type.setText(r.getString(r.getColumnIndex("type_clt")));
            priix.setText(r.getString(r.getColumnIndex("prix"))+" DA");

            div_affichPrix.addView(v);

        }
        //endregion
    }
}
