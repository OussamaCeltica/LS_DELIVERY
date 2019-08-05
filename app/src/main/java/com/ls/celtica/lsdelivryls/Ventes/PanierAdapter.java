package com.ls.celtica.lsdelivryls.Ventes;

import android.database.Cursor;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ls.celtica.lsdelivryls.Accueil;
import com.ls.celtica.lsdelivryls.Login;
import com.ls.celtica.lsdelivryls.R;
import com.ls.celtica.lsdelivryls.User;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by celtica on 15/08/18.
 */

public class PanierAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public boolean parCode=false;
    AppCompatActivity c;
    public static ArrayList<ProduitVendu> produits=new ArrayList<ProduitVendu>();

    public PanierAdapter(  AppCompatActivity c) {
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

    public static class CommandeView extends RecyclerView.ViewHolder  {
        public TextView code_pr;
        public TextView nom_pr;
        public TextView qt;
        public TextView prix;
        public CommandeView(View v) {
            super(v);
            code_pr=(TextView)v.findViewById(R.id.div_pr_codebar);
            nom_pr=(TextView)v.findViewById(R.id.div_pr_nom);
            qt=(TextView)v.findViewById(R.id.div_pr_qt);
            prix=(TextView)v.findViewById(R.id.div_pr_prix);
        }
    }

    public static class RetourView extends RecyclerView.ViewHolder  {
        public TextView code_pr;
        public TextView nom_pr;
        public TextView qt;
        public TextView prix;
        public RetourView(View v) {
            super(v);
            code_pr=(TextView)v.findViewById(R.id.div_pr_codebar);
            nom_pr=(TextView)v.findViewById(R.id.div_pr_nom);
            qt=(TextView)v.findViewById(R.id.div_pr_qt);
            prix=(TextView)v.findViewById(R.id.div_pr_prix);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case 1:{
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.div_pr_panier,parent,false);

                PrView vh = new PrView(v);
                return vh;
            }
            case 2:{
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.div_pr_panier,parent,false);

                CommandeView vh = new CommandeView(v);
                return vh;
            }

            default:{
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.div_pr_panier,parent,false);

                RetourView vh = new RetourView(v);
                return vh;
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {


        if(produits.get(position) instanceof Commande){

            //region  Config du produit de commande du panier ..
            ((LinearLayout)((((CommandeView)holder).code_pr).getParent())).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!FaireVente.venteValider) {
                        AlertDialog.Builder mb = new AlertDialog.Builder(c); //c est l activity non le context ..

                        View v = c.getLayoutInflater().inflate(R.layout.supp_pr_panier, null);
                        TextView oui = (TextView) v.findViewById(R.id.panier_supp_pr_oui);
                        TextView non = (TextView) v.findViewById(R.id.panier_supp_pr_non);
                        TextView valider = (TextView) v.findViewById(R.id.panier_modif_valider);
                        final EditText qt = (EditText) v.findViewById(R.id.panier_pr_modif_qt);
                        final EditText prix = (EditText) v.findViewById(R.id.panier_pr_modif_prix);

                        LinearLayout div_modif_remise = (LinearLayout) v.findViewById(R.id.div_change_remise);
                        final EditText remise = (EditText) v.findViewById(R.id.panier_pr_modif_remise);

                        mb.setView(v);
                        final AlertDialog ad = mb.create();
                        ad.show();

                        qt.setText("" + produits.get(position).qt);
                        prix.setText("" + String.format("%.2f", produits.get(position).prix));
                        remise.setText("0");

                        //region supprimer le produit du panier
                        oui.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                FaireVente.TotalCredit = FaireVente.TotalCredit - (produits.get(position).qt * produits.get(position).prix);
                                FaireVente.TotalCreditAff.setText(c.getResources().getString(R.string.faireVente_panierTotal) + formatPrix(FaireVente.TotalCredit) + "  DA");

                                produits.remove(position);
                                notifyItemChanged(position);
                                ad.dismiss();
                            }
                        });

                        non.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ad.dismiss();
                            }
                        });
                        //endregion

                        //region valider les changements ..
                        valider.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                //region verifier si la qt existe
                                 if(!qt.getText().toString().equals("")){
                                     //faire le calcul du prix ..
                                     onPrixChanged(position,remise,prix,qt);
                                     notifyDataSetChanged();
                                     ad.dismiss();

                                }

                                //endregion
                            }
                        });
                        //endregion


                    }

                }
            });


            if(!parCode) {
                ((CommandeView) holder).nom_pr.setVisibility(View.VISIBLE);
                ((CommandeView) holder).code_pr.setVisibility(View.GONE);
            }else {
                ((CommandeView) holder).nom_pr.setVisibility(View.GONE);
                ((CommandeView) holder).code_pr.setVisibility(View.VISIBLE);
            }

            ((CommandeView) holder).nom_pr.setText(produits.get(position).nom.toLowerCase());
            ((CommandeView) holder).code_pr.setText(produits.get(position).code_pr);
            ((CommandeView)holder).qt.setText(" "+User.formatQt(produits.get(position).qt)+"");
            ((CommandeView)holder).prix.setText(" "+String.format("%.2f", produits.get(position).prix) +" DA");
            //endregion

        }else if(produits.get(position) instanceof ProduitRetour){

            //region  Config du produit de retour du panier ..
            ((LinearLayout)((((RetourView)holder).code_pr).getParent())).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                   afficheDivModif(position, new ConfigDivModif() {
                       @Override
                       public void setUp(LinearLayout div_modif_prix, LinearLayout div_modif_remise, EditText remise) {

                           remise.setText("0");
                           div_modif_prix.setVisibility(View.GONE);
                       }

                       @Override
                       public void valider(AlertDialog ad, int position, EditText remise, EditText prix, EditText qt) {
                           if (!qt.getText().toString().equals("")){
                               onPrixChanged(position,remise,prix,qt);
                               notifyDataSetChanged();
                               ad.dismiss();
                           }
                       }
                   });

                }
            });


            if(!parCode) {
                ((RetourView) holder).nom_pr.setVisibility(View.VISIBLE);
                ((RetourView) holder).code_pr.setVisibility(View.GONE);
            }else {
                ((RetourView) holder).nom_pr.setVisibility(View.GONE);
                ((RetourView) holder).code_pr.setVisibility(View.VISIBLE);
            }

            ((RetourView) holder).nom_pr.setText(produits.get(position).nom.toLowerCase());
            ((RetourView) holder).code_pr.setText(produits.get(position).code_pr);
            ((RetourView)holder).qt.setText(" "+User.formatQt(produits.get(position).qt)+"");
            ((RetourView)holder).prix.setText(" "+String.format("%.2f", produits.get(position).prix) +" DA");
            //endregion
        }

        else {

            //region  Config du produit du panier vente ..
            ((LinearLayout)((((PrView)holder).code_pr).getParent())).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //region la modification ..
                   afficheDivModif(position,new ConfigDivModif() {

                       @Override
                       public void setUp(LinearLayout div_modif_prix, LinearLayout div_modif_remise, EditText remise) {

                           //region  le prix isEdit !
                           if (!Login.user.isEdit) {
                               div_modif_prix.setVisibility(View.GONE);
                           }
                           //endregion

                           //region la remise is Edit !
                           remise.setText(produits.get(position).remise+"");
                           if (Login.user.isRemise){
                               div_modif_remise.setVisibility(View.VISIBLE);
                           }
                           //endregion
                       }

                       @Override
                       public void valider(AlertDialog ad,int position, EditText remise, EditText prix, EditText qt) {
                           //region verifier si la qt existe
                           Cursor r = Accueil.bd.read("select stock from produit where code_pr='" + produits.get(position).code_pr + "' ");
                           if(!qt.getText().toString().equals("")){
                               if (r.moveToNext()) {
                                   if (Double.parseDouble(qt.getText().toString()) > Double.parseDouble(r.getString(r.getColumnIndex("stock")))) {
                                       Toast.makeText(c, "Max quantité: " + Double.parseDouble(r.getString(r.getColumnIndex("stock"))), Toast.LENGTH_SHORT).show();
                                   } else {
                                       if (!remise.getText().toString().equals("")){
                                           if (!prix.getText().toString().equals("")){
                                               //faire le calcul du prix ..
                                               onPrixChanged(position,remise,prix,qt);
                                               notifyDataSetChanged();
                                               ad.dismiss();
                                           }

                                       }

                                   }
                               }
                           }

                           //endregion
                       }


                   });
                   //endregion

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
            ((PrView)holder).qt.setText(" "+User.formatQt(produits.get(position).qt)+"");
            ((PrView)holder).prix.setText(" "+String.format("%.2f", produits.get(position).getPrixAfterRemise()) +" DA");
            //endregion
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(produits.get(position) instanceof Commande){
            return 2;
        }else if(produits.get(position) instanceof ProduitRetour) {
            return 3;
        }else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return produits.size();
    }

    public static String formatPrix(double prix){
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);
        formatter.format("%.2f", prix);

        return sb.toString();
    }

    private void onPrixChanged(int position,EditText remise,EditText prix,EditText qt){
        FaireVente.TotalCredit = FaireVente.TotalCredit - (produits.get(position).qt * produits.get(position).getPrixAfterRemise());
        produits.get(position).setRemise(Double.parseDouble(remise.getText().toString()));
        produits.get(position).prix = Double.parseDouble(prix.getText().toString().replaceAll(",", ".").replaceAll(" ",""));
        produits.get(position).qt = Double.parseDouble(qt.getText().toString());

        notifyDataSetChanged();

        FaireVente.TotalCredit=getTotal();
        FaireVente.TotalCredit=FaireVente.TotalCredit-(FaireVente.TotalCredit*Double.parseDouble(FaireVente.remise)/100);
        FaireVente.TotalCreditAff.setText(c.getResources().getString(R.string.faireVente_panierTotal) + " " + Login.user.formatPrix(FaireVente.TotalCredit) + "  DA");

    }

    public double getTotal(){
        double somme=0;
        for (ProduitVendu p :produits){
            somme=somme+p.qt * p.getPrixAfterRemise();
        }

        return somme;
    }

    private interface ConfigDivModif{
        void setUp(LinearLayout div_modif_prix,LinearLayout div_modif_remise,EditText remise);
        void valider(AlertDialog ad,int position,EditText remise,EditText prix,EditText qt);
    }

    private void afficheDivModif(final int position,final ConfigDivModif conf){

        if(!FaireVente.venteValider) {
            AlertDialog.Builder mb = new AlertDialog.Builder(c); //c est l activity non le context ..

            View v = c.getLayoutInflater().inflate(R.layout.supp_pr_panier, null);
            TextView oui = (TextView) v.findViewById(R.id.panier_supp_pr_oui);
            TextView non = (TextView) v.findViewById(R.id.panier_supp_pr_non);
            TextView valider = (TextView) v.findViewById(R.id.panier_modif_valider);

            LinearLayout div_modif_prix = (LinearLayout) v.findViewById(R.id.div_change_prix);
            final EditText qt = (EditText) v.findViewById(R.id.panier_pr_modif_qt);
            final EditText prix = (EditText) v.findViewById(R.id.panier_pr_modif_prix);

            LinearLayout div_modif_remise = (LinearLayout) v.findViewById(R.id.div_change_remise);
            final EditText remise = (EditText) v.findViewById(R.id.panier_pr_modif_remise);
            qt.setText("" + produits.get(position).qt);
            prix.setText("" + User.formatPrix(produits.get(position).prix));

            conf.setUp(div_modif_prix,div_modif_remise,remise);

            mb.setView(v);
            final AlertDialog ad = mb.create();
            ad.show();

            //region supprimer le produit du panier
            oui.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FaireVente.TotalCredit = FaireVente.TotalCredit - (produits.get(position).qt * produits.get(position).getPrixAfterRemise());
                    FaireVente.TotalCreditAff.setText(c.getResources().getString(R.string.faireVente_panierTotal) + formatPrix(FaireVente.TotalCredit) + "  DA");

                    produits.remove(position);
                    notifyItemChanged(position);
                    ad.dismiss();
                }
            });

            non.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ad.dismiss();
                }
            });
            //endregion

            valider.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    conf.valider(ad,position,remise,prix,qt);
                }
            });
        }

    }
}
