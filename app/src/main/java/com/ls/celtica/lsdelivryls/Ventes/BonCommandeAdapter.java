package com.ls.celtica.lsdelivryls.Ventes;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ls.celtica.lsdelivryls.R;
import com.ls.celtica.lsdelivryls.User;

import java.util.ArrayList;

/**
 * Created by celtica on 08/02/19.
 */

public class BonCommandeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    AppCompatActivity c;
    public static ArrayList<BonCommande> Bons=new ArrayList<BonCommande>();
    public static int itemSelected;

    public BonCommandeAdapter(AppCompatActivity c) {
        this.c = c;

    }

    public static class BonView extends RecyclerView.ViewHolder  {
        public TextView code;
        public TextView date;
        public TextView total;
        public TextView clt;
        public TextView imp_butt;
        public LinearLayout divBon;
        public BonView(View v) {
            super(v);
            code=(TextView)v.findViewById(R.id.code_bon);
            date=(TextView)v.findViewById(R.id.date_bon);
            clt=(TextView)v.findViewById(R.id.clt_bon);
            total=(TextView)v.findViewById(R.id.total_bon);
            divBon=(LinearLayout)v.findViewById(R.id.divBon);
            imp_butt=(TextView) v.findViewById(R.id.imp);

        }
    }

    public static class BonExportView extends RecyclerView.ViewHolder  {
        public TextView code;
        public TextView date;
        public TextView total;
        public TextView clt;
        public TextView imp_butt;
        public LinearLayout divBon;
        public BonExportView(View v) {
            super(v);
            code=(TextView)v.findViewById(R.id.code_bon_export);
            date=(TextView)v.findViewById(R.id.date_bon_export);
            clt=(TextView)v.findViewById(R.id.clt_bon_export);
            total=(TextView)v.findViewById(R.id.total_bon_export);
            divBon=(LinearLayout)v.findViewById(R.id.divBon_export);
            imp_butt=(TextView) v.findViewById(R.id.imp_export);

        }
    }

    public static class BonSuppView extends RecyclerView.ViewHolder  {
        public TextView code;
        public TextView date;
        public TextView total;
        public TextView clt;
        public TextView imp_butt;
        public LinearLayout divBon;
        public BonSuppView(View v) {
            super(v);
            code=(TextView)v.findViewById(R.id.code_bon_supp);
            date=(TextView)v.findViewById(R.id.date_bon_supp);
            clt=(TextView)v.findViewById(R.id.clt_bon_supp);
            total=(TextView)v.findViewById(R.id.total_bon_supp);
            divBon=(LinearLayout)v.findViewById(R.id.divBon_supp);
            imp_butt=(TextView) v.findViewById(R.id.imp_supp);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType){
            case 1:{
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.div_bon,parent,false);

                BonView vh = new BonView(v);
                return vh;
            }

            case 2:{

                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.div_bon_export,parent,false);

                 BonExportView vh = new  BonExportView(v);
                return vh;
            }
            default:{
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.div_bon_supp,parent,false);

                 BonSuppView vh = new  BonSuppView(v);
                return vh;

            }
        }



    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        //region Affichage des Bon De Commande Exporté ..
        if(Bons.get(position) instanceof BonCommandeExport){

            ((BonExportView)holder).divBon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemSelected=position;
                    if(!AfficherCommandes.modeArchive){
                        Intent i=new Intent(c,AfficherProduitVendu.class);
                        i.putExtra("demande","produit_commande");
                        c.startActivity(i);
                    }else {
                        Intent i=new Intent(c,AfficherProduitVendu.class);
                        i.putExtra("demande","produit_commande");
                        c.startActivity(i);
                    }

                }
            });


            ((BonExportView)holder).code.setText(Bons.get(position).code);
            //((BonExportView)holder).total.setVisibility(View.GONE);
            ((BonExportView)holder).total.setText(User.formatPrix(Double.parseDouble(Bons.get(position).getTotal()))+" DA");
            ((BonExportView)holder).date.setText(Bons.get(position).date);
            ((BonExportView)holder).clt.setText(Bons.get(position).nom_clt);
            ((BonExportView)holder).imp_butt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bons.get(position).imprimerBon();
                }
            });
        }
        //endregion

        else if(Bons.get(position) instanceof BonCommandeSupprime){
            ((BonSuppView)holder).divBon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemSelected=position;
                    if(!AfficherCommandes.modeArchive){
                        Intent i=new Intent(c,AfficherProduitVendu.class);
                        i.putExtra("demande","produit_commande");
                        c.startActivity(i);
                    }else {
                        Intent i=new Intent(c,AfficherProduitVendu.class);
                        i.putExtra("demande","produit_commande");
                        c.startActivity(i);
                    }

                }
            });


            ((BonSuppView)holder).code.setText(Bons.get(position).code);
            //((BonExportView)holder).total.setVisibility(View.GONE);
            ((BonSuppView)holder).total.setText(User.formatPrix(Double.parseDouble(Bons.get(position).getTotal()))+" DA");
            ((BonSuppView)holder).date.setText(Bons.get(position).date);
            ((BonSuppView)holder).clt.setText(Bons.get(position).nom_clt);
            ((BonSuppView)holder).imp_butt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bons.get(position).imprimerBon();
                }
            });
        }

        //region Affichage des Bons de Commande non archiver
        else {
            // Afficher les produit d un bon du client lors de click au dessus ..
            ((BonView)holder).divBon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemSelected=position;
                    if(!AfficherCommandes.modeArchive){
                        Intent i=new Intent(c,AfficherProduitVendu.class);
                        i.putExtra("demande","produit_commande");
                        c.startActivity(i);
                    }else {
                        Intent i=new Intent(c,AfficherProduitVendu.class);
                        i.putExtra("demande","archive");
                        c.startActivity(i);
                    }

                }
            });


            ((BonView)holder).code.setText(Bons.get(position).code);
            //((BonView)holder).total.setVisibility(View.GONE);
            ((BonView)holder).total.setText(User.formatPrix(Double.parseDouble(Bons.get(position).getTotal()))+" DA");
            ((BonView)holder).date.setText(Bons.get(position).date);
            ((BonView)holder).clt.setText(Bons.get(position).nom_clt);
            ((BonView)holder).imp_butt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bons.get(position).imprimerBon();
                }
            });
        }





    }

    @Override
    public int getItemViewType(int position) {
        if(Bons.get(position) instanceof BonCommandeExport){
            return 2;
        }
        else if(Bons.get(position) instanceof BonCommandeSupprime){
            return  3;
        }else {
            return 1;
        }

    }


    @Override
    public int getItemCount() {
        return Bons.size();
    }


}
