package com.ls.celtica.lsdelivryls.Ventes;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ls.celtica.lsdelivryls.Accueil;
import com.ls.celtica.lsdelivryls.DeviceConfig;
import com.ls.celtica.lsdelivryls.R;
import com.ls.celtica.lsdelivryls.User;

public class AfficherProduitVendu extends AppCompatActivity {
    ProduitVenduAdapter mAdapter;
    TextView titre_bon;
    boolean parCode=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afficher_produit_vendu);

        if (savedInstanceState != null) {
            Intent intent = new Intent(getApplicationContext(), DeviceConfig.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {

            //Le Titre de bon ..
            titre_bon=((TextView) findViewById(R.id.prv_titre));

            //region Récupération des produit vendu ..
            if(getIntent().getExtras() == null){
                titre_bon.setText(getResources().getString(R.string.aff_vente_bon_nom) + BonAdapter.Bons.get(BonAdapter.itemSelected).code);
                //Afficher le prix total de vente ..
                ((TextView) findViewById(R.id.aff_vente_pTot)).setText(getResources().getString(R.string.aff_vente_pTot) + " " + User.formatPrix(Double.parseDouble(BonAdapter.Bons.get(BonAdapter.itemSelected).getTotal()))+" DA");
                if (BonAdapter.Bons.get(BonAdapter.itemSelected).getRemise()>0){
                    ((TextView)findViewById(R.id.aff_vente_pTotSansRemise)).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.aff_vente_pTotSansRemise)).setText(getResources().getString(R.string.aff_vente_pTotSansRemise) + " " +User.formatPrix(Double.parseDouble(BonAdapter.Bons.get(BonAdapter.itemSelected).getTotalSansRemise()))+" DA");
                }

                Cursor r = Accueil.bd.read("select pf.*  from produit_facture pf where num_fact='" + BonAdapter.Bons.get(BonAdapter.itemSelected).code + "'");
                while (r.moveToNext()) {
                    ProduitVenduAdapter.produits.add(new ProduitVendu(r.getString(r.getColumnIndex("code_pr")),"", r.getString(r.getColumnIndex("nom_pr")), Double.parseDouble(r.getString(r.getColumnIndex("quantity"))), Double.parseDouble(r.getString(r.getColumnIndex("prix_v")))));
                }
            }
            //endregion

            //region recupération des produit du commande ..
            else if(getIntent().getExtras().get("demande").equals("produit_commande")) {
                titre_bon.setText(getResources().getString(R.string.aff_vente_bon_nom) + BonCommandeAdapter.Bons.get(BonCommandeAdapter.itemSelected).code);
                ((TextView) findViewById(R.id.aff_vente_pTot)).setVisibility(View.GONE);
                Cursor r = Accueil.bd.read("select * from produit_commande  where  num_comm='" + BonCommandeAdapter.Bons.get(BonCommandeAdapter.itemSelected).code + "'");
                while (r.moveToNext()) {
                    ProduitVenduAdapter.produits.add(new ProduitVendu(r.getString(r.getColumnIndex("code_pr")),"", r.getString(r.getColumnIndex("nom_pr")), Double.parseDouble(r.getString(r.getColumnIndex("quantity"))), Double.parseDouble(r.getString(r.getColumnIndex("prix_v")))));
                }

            }
            //endregion

            //region recupération des retour ..
            else if(getIntent().getExtras().get("demande").equals("produit_retour")){
                titre_bon.setText(getResources().getString(R.string.aff_vente_bon_nom) + BonRetourAdapter.Bons.get(BonRetourAdapter.itemSelected).code);

                ((TextView) findViewById(R.id.aff_vente_pTot)).setText(getResources().getString(R.string.aff_vente_pTot) + " " + User.formatPrix(Double.parseDouble(BonRetourAdapter.Bons.get(BonRetourAdapter.itemSelected).getTotal())) + " DA");

                Cursor r = Accueil.bd.read("select * from produit_retour  where  num_bon='" + BonRetourAdapter.Bons.get(BonRetourAdapter.itemSelected).code + "'");
                while (r.moveToNext()) {
                    ProduitVenduAdapter.produits.add(new ProduitVendu(r.getString(r.getColumnIndex("code_pr")),"", r.getString(r.getColumnIndex("nom_pr")), Double.parseDouble(r.getString(r.getColumnIndex("quantity"))), Double.parseDouble(r.getString(r.getColumnIndex("prix_v")))));
                }
            }
            //endregion

            //region recycler config ..
            RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.Div_AffichPrv);

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(AfficherProduitVendu.this);
            mRecyclerView.setLayoutManager(mLayoutManager);

            // specify an adapter (see also next example)
            mAdapter = new ProduitVenduAdapter(AfficherProduitVendu.this);



            mRecyclerView.setAdapter(mAdapter);
            //endregion

            //region toggle Produit code/nom ..
            final Button toggle=((Button)findViewById(R.id.panier_change_prToggle));
            toggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!parCode){
                        parCode=true;
                        toggle.setText("-");
                        ((TextView)findViewById(R.id.panier_pr_titre)).setText(getResources().getString(R.string.panier_codePr));
                        mAdapter.parCode=true;
                        mAdapter.notifyDataSetChanged();

                    }else {
                        parCode=false;
                        mAdapter.parCode=false;
                        mAdapter.notifyDataSetChanged();
                        toggle.setText("+");
                        ((TextView)findViewById(R.id.panier_pr_titre)).setText(getResources().getString(R.string.faireVente_pr));

                    }
                }
            });

            //endregion


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ProduitVenduAdapter.produits.clear();
    }
}
