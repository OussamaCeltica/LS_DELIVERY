package com.ls.celtica.lsdelivryls.Ventes;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ls.celtica.lsdelivryls.R;
import com.ls.celtica.lsdelivryls.User;

import java.util.ArrayList;

/**
 * Created by celtica on 15/08/18.
 */

public class ProduitVenduAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public boolean parCode=false;
    AppCompatActivity c;
    public static ArrayList<ProduitVendu> produits=new ArrayList<ProduitVendu>();
    public static int itemSelected;
    public static String type;//vendue / retourné /commandé ..

    public ProduitVenduAdapter(  AppCompatActivity c) {
        this.c = c;

    }

    public static class PrView extends RecyclerView.ViewHolder  {
        public TextView code_pr;
        public TextView nom_pr;
        public TextView qt;
        public TextView prix;
        public PrView(View v) {
            super(v);
            code_pr=(TextView)v.findViewById(R.id.div_pr_codebar);
            nom_pr=(TextView)v.findViewById(R.id.div_pr_nom);
            qt=(TextView)v.findViewById(R.id.div_pr_qt);
            prix=(TextView)v.findViewById(R.id.div_pr_prix);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.div_pr_panier,parent,false);

        PrView vh = new PrView(v);
        return vh;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        // Afficher les information d un produits lors de click au dessus ..
        ((LinearLayout)(((PrView)holder).nom_pr).getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*
                //l index de div qu on a clicker dans le arraylist ..
                itemSelected=position;
                c.startActivity(new Intent(c,Un_Produit.class));
                Log.e("index",""+position);
                */

            }
        });

        if(!parCode) {
            ((PrView) holder).nom_pr.setVisibility(View.VISIBLE);
            ((PrView) holder).code_pr.setVisibility(View.GONE);
        }else {
            ((PrView) holder).nom_pr.setVisibility(View.GONE);
            ((PrView) holder).code_pr.setVisibility(View.VISIBLE);
        }

        ((PrView) holder).nom_pr.setText(produits.get(position).nom.toLowerCase());
        ((PrView) holder).code_pr.setText(produits.get(position).code_pr);
        ((PrView)holder).qt.setText(User.formatQt(produits.get(position).qt)+"");
        //((PrView)holder).prix.setVisibility(View.VISIBLE);
        if (type.equals("vendue")){
            ((PrView)holder).prix.setText(PanierAdapter.formatPrix(produits.get(position).getPrixApresRemise(BonAdapter.Bons.get(BonAdapter.itemSelected).code))+" DA");

        }else {
            ((PrView)holder).prix.setText(PanierAdapter.formatPrix(produits.get(position).prix)+" DA");

        }



    }

    @Override
    public int getItemCount() {
        return produits.size();
    }

    public double prixTotal(){
        double tot=0;
        int i=0;
        while(i != produits.size()){
            if (type.equals("vendue")) {
                tot = tot + (produits.get(i).qt * produits.get(i).getPrixApresRemise(BonAdapter.Bons.get(BonAdapter.itemSelected).code));
            }else {
                tot = tot + (produits.get(i).qt * produits.get(i).prix);
            }
            i++;
        }

        return tot;
    }
}
