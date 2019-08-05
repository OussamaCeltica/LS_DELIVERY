package com.ls.celtica.lsdelivryls.Produits;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ls.celtica.lsdelivryls.R;

import java.util.ArrayList;

/**
 * Created by celtica on 15/08/18.
 */

public class ProduitAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    AppCompatActivity c;
    public static ArrayList<Produit> produits=new ArrayList<Produit>();
    public static int itemSelected;

    public ProduitAdapter(  AppCompatActivity c) {
        this.c = c;

    }

    public static class PrView extends RecyclerView.ViewHolder  {
        public TextView code_pr;
        public TextView nom;
       public TextView qt;
        //public TextView prix;
        public PrView(View v) {
            super(v);
            code_pr=(TextView)v.findViewById(R.id.codebar_pr);
            nom=(TextView)v.findViewById(R.id.nom_pr);
            qt=(TextView)v.findViewById(R.id.qt_pr);



        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.div_produit,parent,false);

        PrView vh = new PrView(v);
        return vh;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        // Afficher les information d un client lors de click au dessus ..
        ((LinearLayout)(((PrView)holder).code_pr).getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //l index de div qu on a clicker dans le arraylist ..
                itemSelected=position;
                c.startActivity(new Intent(c,Un_Produit.class));
                 Log.e("index",""+position);

            }
        });

        //Html.fromHtml("<div><font color='red'>simple</font>"+items.get(position).code+"</div>")
        ((PrView)holder).code_pr.setText(produits.get(position).code_pr);

        ((PrView)holder).nom.setText(produits.get(position).nom);
        ((PrView)holder).qt.setText(produits.get(position).stock+"");
        //((PrView)holder).prix.setText(produits.get(position).prix_u+" DA");



    }

    @Override
    public int getItemCount() {
        return produits.size();
    }
}
