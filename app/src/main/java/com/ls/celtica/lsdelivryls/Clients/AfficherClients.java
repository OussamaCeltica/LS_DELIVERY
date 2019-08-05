package com.ls.celtica.lsdelivryls.Clients;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ls.celtica.lsdelivryls.Accueil;
import com.ls.celtica.lsdelivryls.DeviceConfig;
import com.ls.celtica.lsdelivryls.R;
import com.ls.celtica.lsdelivryls.User;

public class AfficherClients extends AppCompatActivity {
    Cursor r;
    ClientAdapter mAdapter;
    EditText inputSearch;
    int test1=0;
    int min;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            Intent intent = new Intent(getApplicationContext(), DeviceConfig.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {

            setContentView(R.layout.activity_afficher_clients);

            //region open div de recherche
            inputSearch=((EditText) findViewById(R.id.clt_search));
            final FrameLayout div_search=(FrameLayout)findViewById(R.id.affClt_div_search);

            ((LinearLayout)findViewById(R.id.affClt_search_butt)).setOnClickListener(new View.OnClickListener() {
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
            ((ImageView)findViewById(R.id.affClt_div_search_back)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view2) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) div_search.getLayoutParams();
                    params.topMargin = (int) -User.pxFromDp(getApplicationContext(),80);
                    div_search.setLayoutParams(params);
                    inputSearch.setText("");

                    View view = AfficherClients.this.getCurrentFocus();
                    if (view != null) {
                        view.clearFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            });
            //endregion

            //region Ajouter un Nouveau client ..
            ((LinearLayout) findViewById(R.id.addClt_butt)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(AfficherClients.this, AddClient.class));
                    finish();
                }
            });
            //endregion

            //region configuration de recyclerview ..
            RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.Div_AffichClt);

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(AfficherClients.this);
            mRecyclerView.setLayoutManager(mLayoutManager);

            // specify an adapter (see also next example)
            mAdapter = new ClientAdapter(AfficherClients.this);
            mAdapter.notifyDataSetChanged();


            mRecyclerView.setAdapter(mAdapter);
            r = Accueil.bd.read("select * from client order by nom_clt");
            afficherClient(r);
            //endregion

            //region la recherche des clients ..
            ((EditText) findViewById(R.id.clt_search)).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                    ClientAdapter.clients.clear();
                    mAdapter.notifyDataSetChanged();

                    if (s.toString().equals("")) {
                        r = Accueil.bd.read("select * from client order by nom_clt ");

                    } else {
                        r = Accueil.bd.read("select * from client where nom_clt LIKE '%" + s.toString().replaceAll("'","`") + "%' or code_clt LIKE '%" + s.toString().replaceAll("'","`") + "%'");

                    }
                    afficherClient(r);

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            //endregion


        }
    }

    private void afficherClient(Cursor r){
        ClientAdapter.clients.clear();
        mAdapter.notifyDataSetChanged();
        while (r.moveToNext()){
            ClientAdapter.clients.add(new Client(r.getString(r.getColumnIndex("code_clt")),r.getString(r.getColumnIndex("nom_clt")),r.getString(r.getColumnIndex("tél")),r.getString(r.getColumnIndex("adr_clt")),Double.parseDouble(r.getString(r.getColumnIndex("latitude"))),Double.parseDouble(r.getString(r.getColumnIndex("longitude"))),Double.parseDouble(r.getString(r.getColumnIndex("solde"))),Double.parseDouble(r.getString(r.getColumnIndex("verser"))),r.getString(r.getColumnIndex("type"))));
            mAdapter.notifyDataSetChanged();
        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ClientAdapter.clients.clear();
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
}
