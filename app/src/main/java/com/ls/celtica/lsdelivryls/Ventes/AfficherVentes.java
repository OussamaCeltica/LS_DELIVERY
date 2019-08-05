package com.ls.celtica.lsdelivryls.Ventes;

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
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.ls.celtica.lsdelivryls.Accueil;
import com.ls.celtica.lsdelivryls.Clients.Client;
import com.ls.celtica.lsdelivryls.Clients.ClientAdapter;
import com.ls.celtica.lsdelivryls.DeviceConfig;
import com.ls.celtica.lsdelivryls.ImprimerBon;
import com.ls.celtica.lsdelivryls.Login;
import com.ls.celtica.lsdelivryls.MySpinner.MySpinnerSearchable;
import com.ls.celtica.lsdelivryls.MySpinner.SpinnerItem;

import com.ls.celtica.lsdelivryls.R;
import com.ls.celtica.lsdelivryls.Reglements.AfficherReglements;
import com.ls.celtica.lsdelivryls.User;

import java.util.ArrayList;
import java.util.Calendar;

public class AfficherVentes extends AppCompatActivity {
    Cursor r;
    BonAdapter mAdapter;
    MySpinnerSearchable spinnerClient;
    String dernierCamion;
    double somme;
    EditText inputSearch;
    public static boolean modeArchive=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afficher_ventes);

        if (savedInstanceState != null) {
            //region Revenir a au Deviceconfig ..
            Intent intent = new Intent(getApplicationContext(), DeviceConfig.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            //endregion
        } else {

            ImprimerBon.c=this;

            ProduitVenduAdapter.type="vendue";

            //region open div de recherche
            inputSearch=((EditText) findViewById(R.id.facture_search));
            final FrameLayout div_search=(FrameLayout)findViewById(R.id.vente_div_search);

            ((LinearLayout)findViewById(R.id.bon_search_butt)).setOnClickListener(new View.OnClickListener() {
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
                  ((ImageView)findViewById(R.id.vente_div_search_calendar)).setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                        final Calendar myCalendar = Calendar.getInstance();

                          DatePickerDialog dp = new DatePickerDialog(AfficherVentes.this, new DatePickerDialog.OnDateSetListener() {

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
            ((ImageView)findViewById(R.id.vente_div_search_back)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view2) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) div_search.getLayoutParams();
                    params.topMargin = (int) -User.pxFromDp(getApplicationContext(),80);
                    div_search.setLayoutParams(params);
                    inputSearch.setText("");

                    View view = AfficherVentes.this.getCurrentFocus();
                    if (view != null) {
                        view.clearFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            });
            //endregion

            //region Configuration de Recyclerview ..
            RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.Div_AffichVente);

            // use this setting to improve performance if you know that changes
            // in content do not change thAfficherVentese layout size of the RecyclerView
            mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(AfficherVentes.this);
            mRecyclerView.setLayoutManager(mLayoutManager);

            // specify an adapter (see also next example)
            mAdapter = new BonAdapter(AfficherVentes.this);
            mAdapter.notifyDataSetChanged();

            mRecyclerView.setAdapter(mAdapter);
            //endregion

            //region récupération de toutes les factures ..
            if(getIntent().getExtras() == null){
                Cursor r = Accueil.bd.read("select * from facture  where  sync='0' order by num_fact desc");
                afficherFacture(r);
            }
            //endregion

            //region récupération de toutes les factures archiver ..
            else {
                modeArchive=true;
                ((TextView)findViewById(R.id.affVentes_total)).setVisibility(View.GONE);
                ((LinearLayout)findViewById(R.id.bon_faireVente_butt)).setVisibility(View.GONE);
                final LinearLayout menu_archive_butt=(LinearLayout)findViewById(R.id.bon_voirArchive_butt);
                menu_archive_butt.setVisibility(View.VISIBLE);

                menu_archive_butt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Creating the instance of PopupMenu
                        PopupMenu popup = new PopupMenu(AfficherVentes.this,menu_archive_butt);
                        //Inflating the Popup using xml file
                        popup.getMenuInflater().inflate(R.menu.menu_archive, popup.getMenu());

                        //registering popup with OnMenuItemClickListener
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()){
                                    case R.id.commande_archive:{
                                        Intent i=new Intent(AfficherVentes.this,AfficherCommandes.class);
                                        i.putExtra("Demande","archive");
                                        startActivity(i);
                                        finish();
                                        break;
                                    }
                                    case R.id.retour_archive: {
                                        Intent i=new Intent(AfficherVentes.this,AfficherBonRetour.class);
                                        i.putExtra("Demande","archive");
                                        startActivity(i);
                                        finish();
                                        break;
                                    }
                                    case R.id.vente_archive: {

                                        break;
                                    }
                                    case R.id.rglmt_archive: {
                                        Intent i=new Intent(AfficherVentes.this,AfficherReglements.class);
                                        i.putExtra("Demande","archive");
                                        startActivity(i);
                                        finish();
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

                r = Accueil.bd.read("select * from facture f where sync ='1' order by num_fact desc");
                afficherFactureArchiver(r);
            }

            //endregion

            //region la recherche des factures ..
            inputSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                   BonAdapter.Bons.clear();
                    mAdapter.notifyDataSetChanged();

                    if(getIntent().getExtras() == null) {

                        if (s.toString().equals("")) {
                            r = Accueil.bd.read("select * from facture   where  sync ='0' order by num_fact desc ");

                        } else {
                            r = Accueil.bd.read("select * from facture  where   (date_fact LIKE '%" + s.toString().replaceAll("'", "`") + "%' or nom_clt Like '%" + s.toString().replaceAll("'", "`") + "%') and sync='0' order by num_fact desc ");

                        }
                        afficherFacture(r);
                    }else {

                        if (s.toString().equals("")) {
                            r = Accueil.bd.read("select * from facture f where sync ='1' order by num_fact desc ");

                        } else {
                            r = Accueil.bd.read("select * from facture where sync ='1' and date_fact LIKE '%" + s.toString().replaceAll("'", "`") + "%' or nom_clt LIKE '%" + s.toString().replaceAll("'", "`") + "%' order by num_fact desc");

                        }
                        afficherFactureArchiver(r);
                    }


                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            //endregion

            //region faire une vente ..
            ((LinearLayout)findViewById(R.id.bon_faireVente_butt)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Select le client puis va vers
                    if(Login.user.vendeurr == null || Login.user.camion == null){
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.import_produit_noUser),Toast.LENGTH_SHORT).show();
                    }else {
                        //region testé si ce camion a des produit ..
                        Cursor r6= Accueil.bd.read("select * from camion_actuel");
                        while (r6.moveToNext()){
                            dernierCamion=r6.getString(r6.getColumnIndex("code_camion"));
                        }
                        if(!dernierCamion.equals(Login.user.code_camion)){
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.faireVente_noProduit),Toast.LENGTH_SHORT).show();
                        }
                        //endregion

                        //region Select le client puis va vers FaireVente ..
                        else {

                            ArrayList<SpinnerItem> SpinnerItems= new ArrayList<SpinnerItem>();

                            Cursor r2=Accueil.bd.read("select * from client");
                            while (r2.moveToNext()){
                                SpinnerItems.add(new SpinnerItem(r2.getString(r2.getColumnIndex("code_clt")),r2.getString(r2.getColumnIndex("nom_clt"))));
                            }

                            spinnerClient=new MySpinnerSearchable(AfficherVentes.this, SpinnerItems, getResources().getString(R.string.faireVente_selectClt), new MySpinnerSearchable.SpinnerConfig() {
                                @Override
                                public void onChooseItem(int pos, SpinnerItem item) {
                                    //pos not fonctional yet ..
                                    selectClt(item);

                                }
                            }, new MySpinnerSearchable.ButtonSpinnerOnClick() {
                                @Override
                                public void onClick() {
                                    spinnerClient.closeSpinner();
                                    Login.user.openScannerCodeBarre(AfficherVentes.this, new User.OnScanListener() {
                                        @Override
                                        public void OnScan(String code) {
                                            SpinnerItem item=cltCodeBarExiste(code);
                                            if(item != null){
                                                selectClt(item);
                                            }else {
                                                Toast.makeText(AfficherVentes.this,getResources().getString(R.string.faireVente_noCodebarre),Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                                }
                            });
                            spinnerClient.openSpinner();
                        }
                        //endregion
                    }

                }
            });
            //endregion
        }
    }

    public void afficherFacture(Cursor r){
        //region récupération de toutes les factures ..
        somme=0;

        while (r.moveToNext()) {
            BonAdapter.Bons.add(new Bon(r.getString(r.getColumnIndex("num_fact")), r.getString(r.getColumnIndex("date_fact")), Double.parseDouble(r.getString(r.getColumnIndex("verser"))), r.getString(r.getColumnIndex("nom_clt")), r.getString(r.getColumnIndex("code_clt"))));
        }
        mAdapter.notifyDataSetChanged();

        somme=mAdapter.getTotalFact();
        //Log.e("tott", User.formatPrix(somme)+" DA");
        ((TextView)findViewById(R.id.affVentes_total)).setText(getResources().getString(R.string.faireVente_panierTotal)+" "+User.formatPrix(somme)+" DA");


    }

    public void afficherFactureArchiver(Cursor r){
        //region récupération de toutes les factures ..
        somme=0;

        while (r.moveToNext()) {
            if(r.getString(r.getColumnIndex("etat")).equals("supprimé")) {
                BonAdapter.Bons.add(new BonSupprime(r.getString(r.getColumnIndex("num_fact")), r.getString(r.getColumnIndex("date_fact")), Double.parseDouble(r.getString(r.getColumnIndex("verser"))), r.getString(r.getColumnIndex("nom_clt")), r.getString(r.getColumnIndex("code_clt"))));
            }else if(r.getString(r.getColumnIndex("etat")).equals("exporté")){
                BonAdapter.Bons.add(new BonExport(r.getString(r.getColumnIndex("num_fact")), r.getString(r.getColumnIndex("date_fact")), Double.parseDouble(r.getString(r.getColumnIndex("verser"))), r.getString(r.getColumnIndex("nom_clt")), r.getString(r.getColumnIndex("code_clt"))));

            }
        }
        mAdapter.notifyDataSetChanged();
        somme=mAdapter.getTotalFact();
        Log.e("tott", User.formatPrix(somme)+" DA");

    }

    private SpinnerItem cltCodeBarExiste(String codebar){
        Cursor r = Accueil.bd.read("select * from client where codebar='" + codebar + "'");
        if(r.moveToNext()){
            return new SpinnerItem(r.getString(r.getColumnIndex("code_clt")),r.getString(r.getColumnIndex("nom_clt")));
        }else {
            return null;
        }

    }

    private void selectClt(SpinnerItem item){
        ClientAdapter.clients.clear();
        Cursor r = Accueil.bd.read("select type from client where code_clt='" + item.key + "'");
        if (r.moveToNext()) {
            String type="Autre";
            if(!r.getString(r.getColumnIndex("type")).equals("")){
                type=r.getString(r.getColumnIndex("type"));
            }
            ClientAdapter.clients.add(new Client(item.key,item.value, "", "", 0, 0, 0, 0, type));
            ClientAdapter.itemSelected = 0;
            spinnerClient.closeSpinner();
            Intent i = new Intent(AfficherVentes.this, FaireVente.class);
            i.putExtra("request", "Vente");
            startActivity(i);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(data != null) {
                String code = data.getExtras().getString("code");
                SpinnerItem item=cltCodeBarExiste(code);
                if(item != null){
                    selectClt(item);
                }else {
                    Toast.makeText(AfficherVentes.this,getResources().getString(R.string.faireVente_noCodebarre),Toast.LENGTH_SHORT).show();
                }
            }
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
        BonAdapter.Bons.clear();
    }
}
