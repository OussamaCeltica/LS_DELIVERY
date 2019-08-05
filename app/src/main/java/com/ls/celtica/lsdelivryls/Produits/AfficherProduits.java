package com.ls.celtica.lsdelivryls.Produits;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ls.celtica.lsdelivryls.Accueil;
import com.ls.celtica.lsdelivryls.DeviceConfig;
import com.ls.celtica.lsdelivryls.ImprimerBon;
import com.ls.celtica.lsdelivryls.Login;
import com.ls.celtica.lsdelivryls.R;
import com.ls.celtica.lsdelivryls.User;
import com.ls.celtica.lsdelivryls.Ventes.AfficherCommandes;
import com.ls.celtica.lsdelivryls.Ventes.AfficherVentes;

import java.util.Calendar;

public class AfficherProduits extends AppCompatActivity {
    Cursor r;
    ProduitAdapter mAdapter;
    EditText inputSearch;
    boolean produit_erp=false;
    String dernierCamion; // Pour conaitre si on affiche les produit du camion si le mm ou nn si c est un nv ..

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afficher_produits);

        if (savedInstanceState != null) {
            Intent intent = new Intent(getApplicationContext(), DeviceConfig.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {

            ImprimerBon.c=this;

            //region open div de recherche
            inputSearch=((EditText) findViewById(R.id.aff_pr_search));
            final FrameLayout div_search=(FrameLayout)findViewById(R.id.affPr_div_search);

            ((LinearLayout)findViewById(R.id.affPr_search_butt)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) div_search.getLayoutParams();
                    params.topMargin = 0;
                    div_search.setLayoutParams(params);

                    InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    inputSearch.requestFocus();

                }
            });
            //endregion

            //region fermer div de recherche
            ((ImageView)findViewById(R.id.affPr_div_search_back)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view2) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) div_search.getLayoutParams();
                    params.topMargin = (int) -User.pxFromDp(getApplicationContext(),80);
                    div_search.setLayoutParams(params);
                    inputSearch.setText("");

                    View view = AfficherProduits.this.getCurrentFocus();
                    if (view != null) {
                        view.clearFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            });
            //endregion

            //region configuration de recycler view ..

            //region tester si les produit existant sont de meme camion
            Cursor r6= Accueil.bd.read("select * from camion_actuel");
            while (r6.moveToNext()){
             dernierCamion=r6.getString(r6.getColumnIndex("code_camion"));
            }
            //endregion
            r = Accueil.bd.read("select * from produit p inner join product_erp pe on p.code_pr=pe.code_pr  ");

            RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.Div_AffichPr);

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(AfficherProduits.this);
            mRecyclerView.setLayoutManager(mLayoutManager);

            // specify an adapter (see also next example)
            mAdapter = new ProduitAdapter(AfficherProduits.this);
            mAdapter.notifyDataSetChanged();
            afficherProduit(r);

            mRecyclerView.setAdapter(mAdapter);

            //endregion

            //region chercher un produit ..
            inputSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence s, int i, int i1, int i2) {

                    if (s.toString().equals("")) {
                        r = Accueil.bd.read("select * from produit p inner join product_erp pe on p.code_pr=pe.code_pr ");

                    } else {
                        r = Accueil.bd.read("select * from produit p inner join product_erp pe on p.code_pr=pe.code_pr where nom_pr LIKE '%" + s.toString().replaceAll("'","`") + "%' or p.code_pr LIKE '%" + s.toString().replaceAll("'","`") + "%' or codebar='" + s.toString().replaceAll("'","`") + "'");

                    }
                    afficherProduit(r);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            //endregion

            //region imprimer le stock ..
            ((LinearLayout)findViewById(R.id.imprimPr_butt)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Login.user.camion.ImprimerStock();
                }
            });
            //endregion

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ProduitAdapter.produits.clear();
    }

    public void afficherProduit (Cursor r){
        if(dernierCamion.equals(Login.user.code_camion)) {
            ProduitAdapter.produits.clear();
            mAdapter.notifyDataSetChanged();
            while (r.moveToNext()) {
                ProduitAdapter.produits.add(new Produit(r.getString(r.getColumnIndex("code_pr")), Double.parseDouble(r.getString(r.getColumnIndex("stock"))), r.getString(r.getColumnIndex("codebar")), r.getString(r.getColumnIndex("nom_pr")), Double.parseDouble(r.getString(r.getColumnIndex("prix_v")))));
            }
            mAdapter.notifyDataSetChanged();
         }
    }
}
