package com.ls.celtica.lsdelivryls.Ventes;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ls.celtica.lsdelivryls.Accueil;
import com.ls.celtica.lsdelivryls.Clients.ClientAdapter;
import com.ls.celtica.lsdelivryls.CodeBarreTerminal;
import com.ls.celtica.lsdelivryls.DeviceConfig;
import com.ls.celtica.lsdelivryls.Login;
import com.ls.celtica.lsdelivryls.MyBD;
import com.ls.celtica.lsdelivryls.MySpinner.MySpinnerSearchable;
import com.ls.celtica.lsdelivryls.MySpinner.SpinnerItem;
import com.ls.celtica.lsdelivryls.R;
import com.ls.celtica.lsdelivryls.Reglements.FaireReglement;
import com.ls.celtica.lsdelivryls.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class FaireVente extends AppCompatActivity {

    MySpinnerSearchable spinn;
    public int Request_BARRECODE=5;
    public int Request_BARRECODE2=6;
    public int Request_COMMANDE=4;
    LinearLayout  validerVente_butt,addPrToPanier_butt;
    public static double TotalCredit=0;
    public static TextView TotalCreditAff;
    public static boolean venteValider=false;
    public boolean panierVide=true;
    public static String remise="0";
    boolean parCode=false;
     PanierAdapter mAdapter;
     Cursor r;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faire_vente);


        if (savedInstanceState != null) {

            //region Revenir a au Deviceconfig ..
            Intent intent = new Intent(getApplicationContext(), DeviceConfig.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            //endregion

        } else {

            validerVente_butt=((LinearLayout) findViewById(R.id.validerVente_butt));
            addPrToPanier_butt=((LinearLayout) findViewById(R.id.addPrToPanier_butt));

            //region configuration recyclerveiw et affichage ..
            TotalCreditAff=(TextView)findViewById(R.id.faireVente_tot);
            if(getIntent().getExtras().get("request").equals("Commande")){
               // TotalCreditAff.setVisibility(View.GONE);
                ((TextView)findViewById(R.id.faireVente_valider_desc)).setText(getResources().getString(R.string.commande_valider_Butt));
            }else if(getIntent().getExtras().get("request").equals("BonRetour")){
                //TotalCreditAff.setVisibility(View.GONE);
                ((TextView)findViewById(R.id.faireVente_valider_desc)).setText(getResources().getString(R.string.BonRetour_valider_Butt));
            }else {

                //region faire remise ..
                if(Login.user.isRemise){
                    ((ImageView)findViewById(R.id.faire_remise_butt)).setVisibility(View.VISIBLE);
                    ((ImageView)findViewById(R.id.faire_remise_butt)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder mb = new AlertDialog.Builder(FaireVente.this); //c est l activity non le context ..

                            View v= getLayoutInflater().inflate(R.layout.div_input_msg,null);
                            TextView msg=(TextView) v.findViewById(R.id.div_inp_msg_msg);
                            final EditText remiseInp=(EditText) v.findViewById(R.id.div_inp_msg_inp);
                            TextView  valider=(TextView) v.findViewById(R.id.div_inp_msg_valider);
                            remiseInp.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            msg.setText(getApplicationContext().getResources().getString(R.string.faireVente_remisePr)+"");
                            remiseInp.setHint("%");
                            remiseInp.setText(remise);

                            mb.setView(v);
                            final AlertDialog ad=mb.create();
                            ad.show();
                            ad.setCanceledOnTouchOutside(false); //ne pas fermer on click en dehors ..

                            valider.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (remiseInp.getText().toString().equals("")){
                                        remise="0";
                                    }else {
                                        remise=remiseInp.getText().toString();
                                    }

                                    FaireVente.TotalCredit=mAdapter.getTotal();
                                    FaireVente.TotalCredit=FaireVente.TotalCredit-(FaireVente.TotalCredit*Double.parseDouble(FaireVente.remise)/100);
                                    FaireVente.TotalCreditAff.setText(getResources().getString(R.string.faireVente_panierTotal) + " " + User.formatPrix(FaireVente.TotalCredit) + "  DA");

                                    ad.dismiss();
                                }
                            });

                        }
                    });
                }
                //endregion

            }
            ((TextView)findViewById(R.id.faireVente_clt)).setText(getResources().getString(R.string.rglmt_labClt)+" "+ClientAdapter.clients.get(ClientAdapter.itemSelected).nom);

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

            RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.Div_AffichPrVent);

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(FaireVente.this);
            mRecyclerView.setLayoutManager(mLayoutManager);

            // specify an adapter (see also next example)
            mAdapter = new PanierAdapter(FaireVente.this);

            mRecyclerView.setAdapter(mAdapter);
            //endregion

            //region ajouter un produit au panier via scanner code bar
            ((LinearLayout) findViewById(R.id.addPrToPanier_butt)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //region Configuration de vente ..
                    if(getIntent().getExtras().get("request").equals("Vente")){

                        //region séléctioner le produit du camion ..
                        ArrayList<SpinnerItem> SpinnerItems= new ArrayList<SpinnerItem>();
                        Cursor r3=Accueil.bd.read("select * from produit p inner join product_erp pe on pe.code_pr=p.code_pr");
                        while (r3.moveToNext()){
                            SpinnerItems.add(new SpinnerItem(r3.getString(r3.getColumnIndex("code_pr")),r3.getString(r3.getColumnIndex("nom_pr"))));
                        }

                        spinn=new MySpinnerSearchable(FaireVente.this, SpinnerItems, getResources().getString(R.string.faireVente_selectPr), new MySpinnerSearchable.SpinnerConfig() {
                            @Override
                            public void onChooseItem(int pos, SpinnerItem item) {
                                Cursor r=Accueil.bd.read("select p.code_pr from produit p inner join product_erp pe on pe.code_pr=p.code_pr  where   p.code_pr='"+item.key+"' ");
                                if(r.moveToNext()){
                                    addToPanier(r.getString(r.getColumnIndex("code_pr")));
                                }
                                spinn.closeSpinner();


                            }
                        }, new MySpinnerSearchable.ButtonSpinnerOnClick() {
                            @Override
                            public void onClick() {

                                spinn.closeSpinner();
                                Login.user.openScannerCodeBarre(FaireVente.this,new User.OnScanListener() {
                                    @Override
                                    public void OnScan(String code) {
                                        Cursor r=Accueil.bd.read("select * from product_erp pe inner join produit p on p.code_pr=pe.code_pr  where codebar='"+code+"'");

                                        if(r.moveToNext()) {
                                            addToPanier(r.getString(r.getColumnIndex("code_pr")));
                                        }else {
                                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.faireVente_noCadebar),Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });

                        spinn.openSpinner();
                        //endregion
                    }
                    //endregion

                    //region configuration de commande et de Bon Retour ..
                    else if(getIntent().getExtras().get("request").equals("Commande") || getIntent().getExtras().get("request").equals("BonRetour") ) {
                        //region séléctioner les produit d erp ..
                        ArrayList<SpinnerItem> SpinnerItems= new ArrayList<SpinnerItem>();
                        Cursor r3=Accueil.bd.read("select * from  product_erp");
                        while (r3.moveToNext()){
                            SpinnerItems.add(new SpinnerItem(r3.getString(r3.getColumnIndex("code_pr")),r3.getString(r3.getColumnIndex("nom_pr"))));
                        }

                        spinn=new MySpinnerSearchable(FaireVente.this, SpinnerItems, getResources().getString(R.string.faireVente_selectPr), new MySpinnerSearchable.SpinnerConfig() {
                            @Override
                            public void onChooseItem(int pos, SpinnerItem item) {
                                Cursor r=Accueil.bd.read("select  code_pr from   product_erp  where  code_pr='"+item.key+"' ");
                                if(r.moveToNext()){
                                    addCommandToPanier(r.getString(r.getColumnIndex("code_pr")));
                                }
                                spinn.closeSpinner();


                            }
                        }, new MySpinnerSearchable.ButtonSpinnerOnClick() {
                            @Override
                            public void onClick() {

                                spinn.closeSpinner();
                                Login.user.openScannerCodeBarre(FaireVente.this,new User.OnScanListener() {
                                    @Override
                                    public void OnScan(String code) {

                                        Cursor r=Accueil.bd.read("select * from product_erp where codebar='"+code+"'");

                                        if(r.moveToNext()) {
                                            addCommandToPanier(r.getString(r.getColumnIndex("code_pr")));
                                        }else {
                                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.faireVente_noProduit),Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });

                        spinn.openSpinner();
                        //endregion
                    }
                    //endregion
                }
            });
            //endregion

            //region Validation des Bon , Vente ou Commande , ou Retour ..  ..
            ((LinearLayout) findViewById(R.id.validerVente_butt)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int i = 0;
                    double somme=0;
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                    String date = df.format(Calendar.getInstance().getTime());

                    //region valider la vente ..
                    if(getIntent().getExtras().get("request").equals("Vente")){

                        //test si le panier est vide ..
                        if (PanierAdapter.produits.size() == 0) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.faireVente_panierVide), Toast.LENGTH_LONG).show();
                        } else {

                            getLocation(FaireVente.this, new doOnGetGPS() {
                                @Override
                                public void onSuccess(Location p) {
                                    Log.e("rrr","GOOOD");
                                    if (p != null) {

                                        int i = 0;
                                        double somme=0;
                                        //region creation d une facture du vente ..
                                        final Bon bon=new Bon();
                                        bon.setRemise(Double.parseDouble(remise));
                                        bon.addToBD(p);

                                        Log.e("rrr",""+remise);


                                        Cursor r9=Accueil.bd.read("select id from produit_facture order by id desc limit 1");
                                        int id=0;
                                        if (r9.moveToNext()){
                                            id=Integer.parseInt(r9.getString(r9.getColumnIndex("id")));
                                        }
                                        while (i != PanierAdapter.produits.size()) {
                                            somme=somme+PanierAdapter.produits.get(i).prix * PanierAdapter.produits.get(i).qt;
                                            PanierAdapter.produits.get(i).addToBD(bon.numBon,id);
                                            id++;
                                            i++;
                                        }
                                        //endregion

                                        //region Augmenter le solde du client ..
                                        Accueil.bd.write("update client set solde=solde+"+somme+" where code_clt='"+ClientAdapter.clients.get(ClientAdapter.itemSelected).codebar+"'");
                                        //endregion

                                        //region le vente est validé afficher Imprimante ..
                                        bon.updateNumBon();

                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.faireVente_succes), Toast.LENGTH_LONG).show();
                                        venteValider=true;

                                /*
                                Intent intent = new Intent(getApplicationContext(), Accueil.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                */


                                        validerVente_butt.setVisibility(View.GONE);
                                        addPrToPanier_butt.setVisibility(View.GONE);

                                        ((LinearLayout) findViewById(R.id.div_reg_butt)).setVisibility(View.VISIBLE);
                                        ((LinearLayout) findViewById(R.id.faireReg_butt)).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(getApplicationContext(), FaireReglement.class);
                                                intent.putExtra("clt_nom",ClientAdapter.clients.get(ClientAdapter.itemSelected).nom+"");
                                                intent.putExtra("clt_code",ClientAdapter.clients.get(ClientAdapter.itemSelected).codebar+"");
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            }
                                        });

                                        //region imprimer le bon ..
                                        ((LinearLayout)findViewById(R.id.imprime_butt)).setVisibility(View.VISIBLE);
                                        ((LinearLayout)findViewById(R.id.imprime_butt)).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                bon.imprimerBon();

                                            }
                                        });
                                        //endregion

                                        //endregion

                                    } else {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.faireVente_GPS), Toast.LENGTH_LONG).show();

                                    }
                                }

                                @Override
                                public void onGPSDisable() {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.faireVente_GPS), Toast.LENGTH_LONG).show();

                                }
                            });



                        }
                    }
                    //endregion

                    //region valider la Commande ..
                    else if(getIntent().getExtras().get("request").equals("Commande")) {

                        //test si le panier est vide ..
                        if (PanierAdapter.produits.size() == 0) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.faireVente_panierVide), Toast.LENGTH_LONG).show();
                        } else {


                            getLocation(FaireVente.this, new doOnGetGPS() {
                                @Override
                                public void onSuccess(Location p) {

                                    if (p != null) {
                                        int i = 0;
                                        double somme=0;

                                        //region creation d un bon de commande..
                                        final BonCommande bon=new BonCommande();
                                        bon.addCommandeToBD(p);


                                        Cursor r9=Accueil.bd.read("select id from produit_commande order by id desc limit 1");
                                        int id=0;
                                        if (r9.moveToNext()){
                                            id=Integer.parseInt(r9.getString(r9.getColumnIndex("id")));
                                        }
                                        while (i != PanierAdapter.produits.size()) {
                                            somme=somme+PanierAdapter.produits.get(i).prix * PanierAdapter.produits.get(i).qt;
                                            //PanierAdapter.produits.get(i).addToBD(bon.numBon,id);
                                            ((Commande)PanierAdapter.produits.get(i)).addCommandProduitToBd(bon.numBon,id);
                                            id++;
                                            i++;
                                        }
                                        //endregion

                                        //region la commande est validé afficher Imprimante ..
                                        bon.updateNumBon();

                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.commande_succ), Toast.LENGTH_LONG).show();
                                        venteValider=true;

                                /*
                                Intent intent = new Intent(getApplicationContext(), Accueil.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                */


                                        validerVente_butt.setVisibility(View.GONE);
                                        addPrToPanier_butt.setVisibility(View.GONE);

                                        //region imprimer le bon ..
                                        ((LinearLayout)findViewById(R.id.imprime_butt)).setVisibility(View.VISIBLE);
                                        ((LinearLayout)findViewById(R.id.imprime_butt)).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                bon.imprimerBon();

                                            }
                                        });
                                        //endregion

                                        //endregion

                                    } else {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.faireVente_GPS), Toast.LENGTH_LONG).show();

                                    }
                                }

                                @Override
                                public void onGPSDisable() {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.faireVente_GPS), Toast.LENGTH_LONG).show();

                                }
                            });


                        }
                    }
                    //endregion

                    //region valider le bon de retour
                    else {
                         //test si le panier est vide ..
                        if (PanierAdapter.produits.size() == 0) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.faireVente_panierVide), Toast.LENGTH_LONG).show();
                        } else {

                            getLocation(FaireVente.this, new doOnGetGPS() {
                                @Override
                                public void onSuccess(Location p) {

                                    if (p != null) {
                                        int i = 0;
                                        double somme=0;

                                        //region creation d un bon de retour..
                                        final BonRetour bon=new BonRetour();
                                        bon.addRetourToBD(p);


                                        Cursor r9=Accueil.bd.read("select id from produit_retour order by id desc limit 1");
                                        int id=0;
                                        if (r9.moveToNext()){
                                            id=Integer.parseInt(r9.getString(r9.getColumnIndex("id")));
                                        }
                                        while (i != PanierAdapter.produits.size()) {
                                            somme=somme+PanierAdapter.produits.get(i).prix * PanierAdapter.produits.get(i).qt;
                                            //PanierAdapter.produits.get(i).addToBD(bon.numBon,id);
                                            ((ProduitRetour)PanierAdapter.produits.get(i)).addProduitTRetourToBd(bon.numBon,id);
                                            id++;
                                            i++;
                                        }
                                        //endregion

                                        //region le retour est validé afficher Imprimante ..
                                        bon.updateNumBon();

                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.BonRetour_succ), Toast.LENGTH_LONG).show();
                                        venteValider=true;

                                /*
                                Intent intent = new Intent(getApplicationContext(), Accueil.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                */


                                        validerVente_butt.setVisibility(View.GONE);
                                        addPrToPanier_butt.setVisibility(View.GONE);

                                        //region imprimer le bon ..
                                        ((LinearLayout)findViewById(R.id.imprime_butt)).setVisibility(View.VISIBLE);
                                        ((LinearLayout)findViewById(R.id.imprime_butt)).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                bon.imprimerBon();

                                            }
                                        });
                                        //endregion

                                        //endregion

                                    } else {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.faireVente_GPS), Toast.LENGTH_LONG).show();

                                    }
                                }

                                @Override
                                public void onGPSDisable() {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.faireVente_GPS), Toast.LENGTH_LONG).show();

                                }
                            });





                        }
                    }
                    //endregion

                }
            });
            //endregion
        }
    }

    /*----------------------------- Methods -------------------*/



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            String code=data.getExtras().getString("code");
            if(getIntent().getExtras().get("request").equals("Vente")){
                Cursor r=Accueil.bd.read("select * from product_erp pe inner join produit p on p.code_pr=pe.code_pr  where codebar='"+code+"'");

                if(r.moveToNext()) {
                    addToPanier(r.getString(r.getColumnIndex("code_pr")));
                }else {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.faireVente_noCadebar),Toast.LENGTH_SHORT).show();
                }

            }else {
                Cursor r=Accueil.bd.read("select * from product_erp where codebar='"+code+"'");

                if(r.moveToNext()) {
                    addCommandToPanier(r.getString(r.getColumnIndex("code_pr")));
                }else {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.faireVente_noCadebar),Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    private boolean prExitInPanier(final String code){
       int i=0;
        while(i != PanierAdapter.produits.size() ){
            if (PanierAdapter.produits.get(i).code_pr.equals(code)){
               return true;
            }
            i++;
        }

        return false;
    }


    private void addToPanier(final String code){

        //region test si le produit est déja au panier ..
        if(prExitInPanier(code)){
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.add_pr_panier_pr_exist),Toast.LENGTH_LONG).show();
        }
        //endregion

        //region ajouter le produit au panier ..
        else {
            final Cursor r=Accueil.bd.read("select * from produit p inner join product_erp pe on pe.code_pr=p.code_pr  where p.code_pr='"+code+"'");
            afficheDivQt(r, new OnClickAddQtToPanier() {
               @Override
               public void valider(EditText qt,AlertDialog ad) {
                   final InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext()
                           .getSystemService(Context.INPUT_METHOD_SERVICE);
                   inputMethodManager.showSoftInput(qt, InputMethodManager.SHOW_IMPLICIT);
                   inputMethodManager.hideSoftInputFromWindow(qt.getWindowToken(), 0);


                   if(!qt.getText().toString().equals("")){
                       //tester si la quantité existe ..
                       if(Double.parseDouble(r.getString(r.getColumnIndex("stock"))) >= Double.parseDouble(qt.getText().toString()) && Double.parseDouble(qt.getText().toString()) > 0  ){
                           //récupéré le prix selon le type des clients ..
                           // Cursor r2=Accueil.bd.read("select * from prix ");

                           PanierAdapter.produits.add(new ProduitVendu(r.getString(r.getColumnIndex("code_pr")),r.getString(r.getColumnIndex("codebar")),r.getString(r.getColumnIndex("nom_pr")),Double.parseDouble(qt.getText().toString()),Double.parseDouble(r.getString(r.getColumnIndex("prix_v")))));
                           ad.dismiss();
                           double prix=PanierAdapter.produits.get(PanierAdapter.produits.size()-1).getClientPrix(ClientAdapter.clients.get(ClientAdapter.itemSelected).type);
                           if(prix != -1){
                               PanierAdapter.produits.get(PanierAdapter.produits.size()-1).prix=prix;
                               TotalCredit=TotalCredit+(prix*Double.parseDouble(qt.getText().toString()));

                           }else {
                               TotalCredit=TotalCredit+(Double.parseDouble(r.getString(r.getColumnIndex("prix_v")))*Double.parseDouble(qt.getText().toString()));
                           }

                           TotalCreditAff.setText(getResources().getString(R.string.faireVente_panierTotal)+" "+User.formatPrix(FaireVente.TotalCredit)+"  DA");
                           mAdapter.notifyDataSetChanged();


                       }else {
                           Toast.makeText(getApplicationContext(),getResources().getString(R.string.add_pr_panier_qt_err),Toast.LENGTH_LONG).show();

                       }
                   }
               }
           });

        }
        //endregion

    }

    private void addCommandToPanier(final String code){
        if(prExitInPanier(code)){
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.add_pr_panier_pr_exist),Toast.LENGTH_LONG).show();
        }

        //region ajouter le produit au panier ..
        else {
            final Cursor r = Accueil.bd.read("select * from product_erp where code_pr='" + code + "'");
            afficheDivQt(r, new OnClickAddQtToPanier() {
                @Override
                public void valider(EditText qt, AlertDialog ad) {
                    final InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(qt, InputMethodManager.SHOW_IMPLICIT);
                    inputMethodManager.hideSoftInputFromWindow(qt.getWindowToken(), 0);

                    if(!qt.getText().toString().equals("")){
                        if(getIntent().getExtras().get("request").equals("Commande")){
                            PanierAdapter.produits.add(new Commande(r.getString(r.getColumnIndex("code_pr")),r.getString(r.getColumnIndex("codebar")),r.getString(r.getColumnIndex("nom_pr")),Double.parseDouble(qt.getText().toString()),Double.parseDouble(r.getString(r.getColumnIndex("prix_v")))));
                            //PanierAdapter.produits.add(new Commande(r.getString(r.getColumnIndex("code_pr")),r.getString(r.getColumnIndex("codebar")),r.getString(r.getColumnIndex("nom_pr")),Double.parseDouble(qt.getText().toString()),Double.parseDouble(r.getString(r.getColumnIndex("prix_vente")))));

                        }else {
                            PanierAdapter.produits.add(new ProduitRetour(r.getString(r.getColumnIndex("code_pr")),r.getString(r.getColumnIndex("codebar")),r.getString(r.getColumnIndex("nom_pr")),Double.parseDouble(qt.getText().toString()),Double.parseDouble(r.getString(r.getColumnIndex("prix_v")))));
                            //PanierAdapter.produits.add(new ProduitRetour(r.getString(r.getColumnIndex("code_pr")),r.getString(r.getColumnIndex("codebar")),r.getString(r.getColumnIndex("nom_pr")),Double.parseDouble(qt.getText().toString()),Double.parseDouble(r.getString(r.getColumnIndex("prix_vente")))));

                        }
                        double prix=PanierAdapter.produits.get(PanierAdapter.produits.size()-1).getClientPrix(ClientAdapter.clients.get(ClientAdapter.itemSelected).type);
                        if(prix != -1){
                            PanierAdapter.produits.get(PanierAdapter.produits.size()-1).prix=prix;
                            TotalCredit=TotalCredit+(prix*Double.parseDouble(qt.getText().toString()));

                        }else {
                            TotalCredit=TotalCredit+(Double.parseDouble(r.getString(r.getColumnIndex("prix_v")))*Double.parseDouble(qt.getText().toString()));
                        }

                        TotalCreditAff.setText(getResources().getString(R.string.faireVente_panierTotal)+" "+User.formatPrix(FaireVente.TotalCredit)+"  DA");

                        mAdapter.notifyDataSetChanged();
                        ad.dismiss();
                    }
                }
            });
        }
        //endregion

    }

    private void addPrRetourToPanier(final String code){
        if(prExitInPanier(code)){
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.add_pr_panier_pr_exist),Toast.LENGTH_LONG).show();
        }

        //region ajouter le produit au panier ..
        else {
            final Cursor r = Accueil.bd.read("select * from produit where code_pr='" + code + "'");
            afficheDivQt(r, new OnClickAddQtToPanier() {
                @Override
                public void valider(EditText qt, AlertDialog ad) {
                    final InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(qt, InputMethodManager.SHOW_IMPLICIT);
                    inputMethodManager.hideSoftInputFromWindow(qt.getWindowToken(), 0);

                    if(!qt.getText().toString().equals("")){
                        PanierAdapter.produits.add(new Commande(r.getString(r.getColumnIndex("code_pr")),r.getString(r.getColumnIndex("codebar")),r.getString(r.getColumnIndex("nom_pr")),Double.parseDouble(qt.getText().toString()),Double.parseDouble(r.getString(r.getColumnIndex("prix_v")))));
                        mAdapter.notifyDataSetChanged();
                        ad.dismiss();
                    }
                }
            });
        }
        //endregion

    }

    //region Récupération les cordoné GPS Actuel ..
    Location getLocation(){
        Location myLoc=null;

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        //test si on a pas deja la permission de consulter se service ou oui ..
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //test si la version SDK d'android superieur a 23 donc il faut ajouter la permission au temps reel (la nouveauté d android) et les version précédente se faire au moment d instalation .
            if (Build.VERSION.SDK_INT >= 23) {
                // afficher  le dialog de pr koi on demande de permission  ..
                if(!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                    // l'affichage de box de permission avec les permission qu on souhaite a l utiliser est cette methode va appelé public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) et indiquer dans grantResults si ill accepté ou nn

                    requestPermissions(new String []{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},
                            123);

                    return null;//l objet myLoc va etre null si il na pas dit wéé



                }
            }
            return null;// null si les permission ne sont pas vérifié ..
        }

           // si tout est bien je vait consulter le service avec le manager et je fait ce que je veut

        myLoc=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(myLoc==null){

            myLoc=lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        }

        return myLoc;
    }

//cette mathode on va l appelé pour tt demande d une permission donc pour différenier le permissions il faut test le request avec une switch ..

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode){
            case 123 :{ //si par example le code 123 de GPS qui invoqué cette methode ..
                if (grantResults[1]== PackageManager.PERMISSION_GRANTED){

                    Location p=getLocation();
                    if(p!=null){
                        Log.e("Position","Lat="+p.getLatitude()+" / Longi="+p.getLongitude());

                    }

                }else {
                    // si il na pas accecpté un message pour l informer qu on peut paq continuer ou je c pas quoi ..
                    Toast.makeText(this,"On peut pas récupéré votre position",Toast.LENGTH_LONG).show();
                }

                break;


            }


            default: super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }

    }
    //endregion

    //region returner a l accueil ..
    @Override
    public void onBackPressed() {

        //region si le vente valider retourner accueil ..
        if(venteValider){
            Intent intent = new Intent(getApplicationContext(), Accueil.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        //endregion

        //region si le panier n est pas vide ..
        else if(PanierAdapter.produits.size() != 0){

            AlertDialog.Builder mb = new AlertDialog.Builder(FaireVente.this); //c est l activity non le context ..

            View v= getLayoutInflater().inflate(R.layout.confirm_box,null);
            TextView oui=(TextView) v.findViewById(R.id.confirm_oui);
            final TextView non=(TextView) v.findViewById(R.id.confirm_non);
             TextView msg=(TextView) v.findViewById(R.id.confirm_msg);
             msg.setText("voulez vraiment quitter !");



            mb.setView(v);
            final AlertDialog ad=mb.create();
            ad.show();
            ad.setCanceledOnTouchOutside(false); //ne pas fermer on click en dehors ..
            ad.setCancelable(false); //désactiver le button de retour ..

            oui.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ad.dismiss();
                    finish();
                }
            });


            non.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ad.dismiss();
                }
            });

        }else {
            finish();
        }
        //endregion
    }
    //endregion

    public void getLocation(AppCompatActivity c, final doOnGetGPS d){
        final LocationManager locationManager;
        locationManager = (LocationManager) c.getSystemService(c.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //test si la version SDK d'android superieur a 23 donc il faut ajouter la permission au temps reel (la nouveauté d android) et les version précédente se faire au moment d instalation .
            if (Build.VERSION.SDK_INT >= 23) {
                // afficher  le dialog de pr koi on demande de permission  ..
                if(!c.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                    // l'affichage de box de permission avec les permission qu on souhaite a l utiliser est cette methode va appelé public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) et indiquer dans grantResults si ill accepté ou nn

                    c.requestPermissions(new String []{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},
                            123);

                }
            }

            return ;
        }

        LocationListener locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locationManager.removeUpdates(this);
            }

            @Override
            public void onProviderDisabled(String provider) {
                d.onGPSDisable();
            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        if (locationManager != null)
        {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                d.onSuccess(location);
                locationManager.removeUpdates(locationListener);
            }
        }
    }

    public interface doOnGetGPS {
        void onSuccess(Location l);
        void onGPSDisable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        remise="0";
        PanierAdapter.produits.clear();
        TotalCredit=0;
        TotalCreditAff.setText("0.0 DA");
        venteValider=false;
    }

    public interface OnClickAddQtToPanier{
        void valider(EditText qt,AlertDialog ad);
    }

    private void afficheDivQt(final Cursor r, final OnClickAddQtToPanier add){
        if(r.moveToNext()){
            // Log.e("Produit",r.getString(r.getColumnIndex("nom_pr")));
            AlertDialog.Builder mb = new AlertDialog.Builder(FaireVente.this); //c est l activity non le context ..

            View v= getLayoutInflater().inflate(R.layout.div_qt_pr,null);
            TextView valider=(TextView) v.findViewById(R.id.valider_pr_panier);
            final EditText qt=(EditText)v.findViewById(R.id.panier_qt);
            qt.setText("");
            ((TextView)v.findViewById(R.id.panier_qt_nom_pr)).setText(r.getString(r.getColumnIndex("nom_pr")));
            if(getIntent().getExtras().get("request").equals("Vente")) {
                ((TextView) v.findViewById(R.id.panier_qt_qt_pr)).setText(getResources().getString(R.string.faireVente_scanner_qt) + r.getString(r.getColumnIndex("stock")));
            }else {
                ((TextView) v.findViewById(R.id.panier_qt_qt_pr)).setVisibility(View.GONE);
            }


            mb.setView(v);
            final AlertDialog ad=mb.create();
            ad.show();
            qt.setFocusable(true);
            qt.requestFocus();
            final InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(qt, InputMethodManager.SHOW_IMPLICIT);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);


            valider.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    add.valider(qt,ad);
                }
            });

        }else {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.add_pr_panier_err),Toast.LENGTH_LONG).show();
        }
    }


}
