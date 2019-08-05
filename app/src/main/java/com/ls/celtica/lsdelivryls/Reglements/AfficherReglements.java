package com.ls.celtica.lsdelivryls.Reglements;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.ls.celtica.lsdelivryls.Accueil;
import com.ls.celtica.lsdelivryls.DeviceConfig;
import com.ls.celtica.lsdelivryls.Login;
import com.ls.celtica.lsdelivryls.R;
import com.ls.celtica.lsdelivryls.User;
import com.ls.celtica.lsdelivryls.Ventes.AfficherBonRetour;
import com.ls.celtica.lsdelivryls.Ventes.AfficherCommandes;
import com.ls.celtica.lsdelivryls.Ventes.AfficherVentes;

import java.util.Calendar;

public class AfficherReglements extends AppCompatActivity {

    boolean visible=true;
    int test1=0;
    int min;
    Cursor r;
    EditText inputSearch;
     ReglementAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afficher_reglements);

        if (savedInstanceState != null) {
            Intent intent = new Intent(getApplicationContext(), DeviceConfig.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {

            //region open div de recherche
            inputSearch=((EditText) findViewById(R.id.rglmt_search));
            final FrameLayout div_search=(FrameLayout)findViewById(R.id.rglmt_div_search);

            ((LinearLayout)findViewById(R.id.rglmt_search_butt)).setOnClickListener(new View.OnClickListener() {
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

            //region open calendar ..
            ((ImageView)findViewById(R.id.rglmt_div_search_calendar)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Calendar myCalendar = Calendar.getInstance();

                    DatePickerDialog dp = new DatePickerDialog(AfficherReglements.this, new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {
                            // TODO Auto-generated method stub
                            myCalendar.set(Calendar.YEAR, year);
                            myCalendar.set(Calendar.MONTH, monthOfYear);
                            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            String mois,jour;
                            mois=monthOfYear+1+"";
                            jour=dayOfMonth+"";
                            if(monthOfYear+1 < 10){
                                mois="0"+(monthOfYear+1);
                            }
                            if(dayOfMonth < 10){
                                jour="0"+dayOfMonth;
                            }

                            inputSearch.setText(year+"-"+mois+"-"+jour);

                        }

                    }, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH));

                    dp.show();

                }
            });
            //endregion
            //endregion

            //region fermer div de recherche
            ((ImageView)findViewById(R.id.rglmt_div_search_back)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view2) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) div_search.getLayoutParams();
                    params.topMargin = (int) -User.pxFromDp(getApplicationContext(),80);
                    div_search.setLayoutParams(params);
                    inputSearch.setText("");

                    View view = AfficherReglements.this.getCurrentFocus();
                    if (view != null) {
                        view.clearFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            });
            //endregion

            //region configurer le recyclerview ..
            // specify an adapter (see also next example)
            mAdapter = new ReglementAdapter(AfficherReglements.this);
            if(getIntent().getExtras() == null) {
                r = Accueil.bd.read("select * from reglement where sync='0'  and code_vendeur='" + Login.user.vendeur + "' order by date_rglmt desc");
            }else {
                ((LinearLayout)findViewById(R.id.faireRglmt_butt)).setVisibility(View.GONE);
                final LinearLayout menu_archive_butt=(LinearLayout)findViewById(R.id.reg_voirArchive_butt);
                menu_archive_butt.setVisibility(View.VISIBLE);

                menu_archive_butt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Creating the instance of PopupMenu
                        PopupMenu popup = new PopupMenu(AfficherReglements.this,menu_archive_butt);
                        //Inflating the Popup using xml file
                        popup.getMenuInflater().inflate(R.menu.menu_archive, popup.getMenu());

                        //registering popup with OnMenuItemClickListener
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()){
                                    case R.id.commande_archive:{
                                        Intent i=new Intent(AfficherReglements.this,AfficherCommandes.class);
                                        i.putExtra("Demande","archive");
                                        startActivity(i);
                                        finish();
                                        break;
                                    }
                                    case R.id.retour_archive: {
                                        Intent i=new Intent(AfficherReglements.this,AfficherBonRetour.class);
                                        i.putExtra("Demande","archive");
                                        startActivity(i);
                                        finish();
                                        break;
                                    }
                                    case R.id.vente_archive: {
                                        Intent i=new Intent(AfficherReglements.this,AfficherVentes.class);
                                        i.putExtra("Demande","archive");
                                        startActivity(i);
                                        finish();
                                        break;
                                    }
                                    case R.id.rglmt_archive: {

                                        break;
                                    }
                                    default:{

                                    }
                                }
                                //startactiv ..
                                //Toast.makeText(getApplicationContext(),"You Clicked : " + item.getItemId(), Toast.LENGTH_SHORT).show();
                                return true;
                            }
                        });

                        popup.show();//showing popup menu
                    }
                });
                r = Accueil.bd.read("select * from reglement where sync='1'  order by date_rglmt desc");
            }
            afficherReglements(r);
            RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.Div_AffichRglmt);

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(AfficherReglements.this);
            mRecyclerView.setLayoutManager(mLayoutManager);


            mRecyclerView.setAdapter(mAdapter);
            //endregion

            //region la rechereche des règlement ..
            ((EditText) findViewById(R.id.rglmt_search)).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence s, int i, int i1, int i2) {

                    if(getIntent().getExtras()==null){
                        if (s.toString().equals("")) {
                            r = Accueil.bd.read("select * from reglement where sync='0' and  code_vendeur='"+Login.user.vendeur+"' order by date_rglmt desc");

                        } else {
                            r = Accueil.bd.read("select * from reglement where sync='0' and  code_vendeur='"+Login.user.vendeur+"' and (nom_clt LIKE '%" + s.toString().replaceAll("'","`") + "%' or  date_rglmt LIKE '%" + s.toString().replaceAll("'","`") + "%') order by  date_rglmt desc");

                        }
                    }else {
                        if (s.toString().equals("")) {
                            r = Accueil.bd.read("select * from reglement where sync='1' and  code_vendeur='"+Login.user.vendeur+"' order by date_rglmt desc");

                        } else {
                            r = Accueil.bd.read("select * from reglement where sync='1' and  code_vendeur='"+Login.user.vendeur+"' and (nom_clt LIKE '%" + s.toString().replaceAll("'","`") + "%' or  date_rglmt LIKE '%" + s.toString().replaceAll("'","`") + "%') order by  date_rglmt desc");

                        }
                    }
                    afficherReglements(r);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            //endregion

            //region Ajouter un nouveau Regelement ..
            ((LinearLayout) findViewById(R.id.faireRglmt_butt)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Login.user.vendeur.equals(getResources().getString(R.string.login_pseudoHint))){
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.import_produit_noUser),Toast.LENGTH_SHORT).show();
                    }else {
                        startActivity(new Intent(AfficherReglements.this, FaireReglement.class));
                        finish();
                    }

                }
            });
            //endregion
        }
    }

    public  void afficherReglements(Cursor r){
        ReglementAdapter.reglements.clear();
        mAdapter.notifyDataSetChanged();

        if(getIntent().getExtras()==null){
            while(r.moveToNext()){
                ReglementAdapter.reglements.add(new Reglement(r.getString(r.getColumnIndex("nom_clt")),r.getString(r.getColumnIndex("mode")),r.getString(r.getColumnIndex("date_rglmt")),Double.parseDouble(r.getString(r.getColumnIndex("montant")))));
            }
        }else {
            while(r.moveToNext()){
                if(r.getString(r.getColumnIndex("etat")).equals("exporté"))
                ReglementAdapter.reglements.add(new ReglementExport(r.getString(r.getColumnIndex("nom_clt")),r.getString(r.getColumnIndex("mode")),r.getString(r.getColumnIndex("date_rglmt")),Double.parseDouble(r.getString(r.getColumnIndex("montant")))));
            }
        }
        mAdapter.notifyDataSetChanged();
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
        ReglementAdapter.reglements.clear();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(getIntent().getExtras() == null){
            Intent intent = new Intent(getApplicationContext(), Accueil.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }


    }


}
