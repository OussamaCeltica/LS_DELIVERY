package com.ls.celtica.lsdelivryls.Produits;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ls.celtica.lsdelivryls.Accueil;
import com.ls.celtica.lsdelivryls.Clients.ClientPrix;
import com.ls.celtica.lsdelivryls.CodeBarScanner;
import com.ls.celtica.lsdelivryls.CodeBarreTerminal;
import com.ls.celtica.lsdelivryls.Login;
import com.ls.celtica.lsdelivryls.R;

import java.util.ArrayList;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class AddProduit extends AppCompatActivity {
    SpinnerDialog types_clt;
    ArrayList<String> types=new ArrayList<String>();
    ArrayList<ClientPrix> clt_prix=new ArrayList<ClientPrix>();
    LinearLayout div_prix;
    int i=0;
    boolean existe=false;
     TextView codebar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_produit);

        div_prix=(LinearLayout) findViewById(R.id.param_DivPrix);
        codebar =(TextView) findViewById(R.id.param_AddPr_codebar);
        final EditText qt=(EditText)findViewById(R.id.param_AddPr_stock);
        final EditText nom=(EditText)findViewById(R.id.param_AddPr_nom);
        final EditText DefaultPrix=(EditText)findViewById(R.id.param_AddPr_prix);

        View view = this.getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            //on peut remplace le view par l id de notre EditText ..
        }


        //region scanner le code barre de produit ..
        codebar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codebar.setFocusable(false);
                if(Login.user.type_device.equals(getResources().getString(R.string.login_device_type1))) {
                    Intent i=new Intent(AddProduit.this, CodeBarreTerminal.class);
                    i.putExtra("request","AddPr");
                    startActivityForResult(i, 2);
                }else {
                    startActivityForResult(new Intent(AddProduit.this, CodeBarScanner.class), 2);

                }
            }

        });
        //endregion

        //region ajouter un prix ..
        ((TextView)findViewById(R.id.param_AddPrix_butt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mb = new AlertDialog.Builder(AddProduit.this); //c est l activity non le context ..

                View v= getLayoutInflater().inflate(R.layout.add_prix,null);
                TextView valider=(TextView) v.findViewById(R.id.add_prix_valider);
                final EditText prix=(EditText)v.findViewById(R.id.add_prix_prix);
                final TextView typee=(TextView) v.findViewById(R.id.add_prix_type);

                //region spinner pour les type client ..
                Cursor r= Accueil.bd.read("select distinct * from type_client");
                while (r.moveToNext()){
                    types.add(r.getString(r.getColumnIndex("type_clt")));
                }
                types_clt=new SpinnerDialog(AddProduit.this,types,"",R.style.DialogAnimations_SmileWindow,"Close");

                types_clt.bindOnSpinerListener(new OnSpinerItemClick() {
                    @Override
                    public void onClick(String item, int position) {
                         typee.setText(item);
                    }
                });
                typee.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        types_clt.showSpinerDialog();
                    }
                });
                //endregion

                //region afficher le div d ajout des prix ..
                mb.setView(v);
                final AlertDialog ad=mb.create();
                ad.show();
                //endregion

                //region valider l ajout d un prix/client ..
                valider.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        View v=getLayoutInflater().inflate(R.layout.div_prix_type,null);
                        TextView type,priix;
                        type=(TextView)v.findViewById(R.id.div_prix_type);
                        priix=(TextView)v.findViewById(R.id.div_prix_prix);

                        type.setText(typee.getText());
                        priix.setText(prix.getText());

                        i=0;
                        existe=false;
                        while (i != clt_prix.size() && !existe){
                            if(clt_prix.get(i).type.equals(typee.getText().toString())){
                                existe=true;
                            }
                            i++;
                        }

                        if(!existe) {
                            clt_prix.add(new ClientPrix(type.getText().toString(), Double.parseDouble(prix.getText().toString())));
                            div_prix.addView(v);
                        }else {
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.param_AddPrix_existe),Toast.LENGTH_SHORT).show();
                        }

                        ad.dismiss();
                    }
                });
                //endregion


            }
        });

        //endregion

        //region valider l ajout des produit ..
        ((TextView)findViewById(R.id.param_AddPr_valider)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(codebar.getText().toString().equals("") || qt.getText().toString().equals("") || nom.getText().toString().equals("") || DefaultPrix.getText().toString().equals("") ){
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.remplissage_err),Toast.LENGTH_SHORT).show();

                }else {


                        //region test si il existe déja , augment stock ou insérer ..
                        Cursor r2=Accueil.bd.read("select codebar from produit where codebar='"+codebar.getText().toString()+"'");
                        if (r2.moveToNext()){
                            Accueil.bd.write("update produit set stock=stock+"+Integer.parseInt(qt.getText().toString())+" where codebar='"+codebar.getText().toString()+"'");
                        }else {
                            Accueil.bd.write("insert into produit (codebar,nom_pr,stock,prix_vente) values ('"+codebar.getText().toString()+"','"+nom.getText().toString().replaceAll("'","")+"','"+qt.getText().toString()+"','"+DefaultPrix.getText().toString()+"')");

                        }
                        //endregion

                        //region insérer les type s il nexiste pas deja ..
                        i=0;
                        while (i != clt_prix.size()){
                            r2=Accueil.bd.read("select * from produit_prix_type where codebar='"+codebar.getText().toString()+"' and type_clt='"+clt_prix.get(i).type+"' ");
                            if(!r2.moveToNext()){
                                Accueil.bd.write("insert into produit_prix_type (codebar,type_clt,prix) values ('"+codebar.getText().toString()+"','"+clt_prix.get(i).type+"','"+clt_prix.get(i).prix+"') ");
                            }

                            i++;
                        }
                        //endregion

                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.param_AddPrix_success),Toast.LENGTH_SHORT).show();
                        clt_prix.clear();
                        div_prix.removeAllViews();
                        codebar.setText("");
                        nom.setText("");
                        qt.setText("");
                        DefaultPrix.setText("");



                }

            }
        });
        //endregion

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){

            String code=data.getExtras().getString("code");
            codebar.setText(code);
            Log.e("codd"," "+code);

        }
    }
}
