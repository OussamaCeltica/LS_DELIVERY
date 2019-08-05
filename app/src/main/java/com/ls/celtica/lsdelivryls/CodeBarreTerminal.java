package com.ls.celtica.lsdelivryls;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ls.celtica.lsdelivryls.MySpinner.MySpinnerSearchable;
import com.ls.celtica.lsdelivryls.MySpinner.SpinnerItem;

import java.util.ArrayList;

public class CodeBarreTerminal extends AppCompatActivity {
    MySpinnerSearchable spinn;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_barre_terminal);

        if (savedInstanceState != null) {
            //region Revenir a au Deviceconfig ..
            Intent intent = new Intent(getApplicationContext(), DeviceConfig.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            //endregion
        } else {
            final EditText scanner=((EditText) findViewById(R.id.terminal_code));

            //region désactiver le clavier ou ouvrir scanner pour normal devices ..
            scanner.setShowSoftInputOnFocus(false);
            scanner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!getIntent().getExtras().getString("request").equals("Vente2")) {
                        scanner.setFocusable(true);
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.add_pr_panier_scan_Hint), Toast.LENGTH_SHORT).show();
                    }else {
                        startActivityForResult(new Intent(CodeBarreTerminal.this,CodeBarScanner.class),9);
                    }
                }
            });
            //endregion

            //region récupérer le code bar ..
            scanner.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence s, int ii, int i1, int i2) {
                    Intent i = new Intent();
                    i.putExtra("code", "" + s.toString().substring(0, s.toString().length() - 1));
                    setResult(RESULT_OK, i);
                    finish();
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            //endregion

            if(getIntent().getExtras().getString("request").equals("Vente") || getIntent().getExtras().getString("request").equals("Vente2") ){

                //region séléctioner le produit du camion ..
                TextView selectPrButt=((TextView)findViewById(R.id.terminal_select_Butt));
                selectPrButt.setVisibility(View.VISIBLE);

                ArrayList<SpinnerItem> SpinnerItems= new ArrayList<SpinnerItem>();
                Cursor r3=Accueil.bd.read("select * from produit p inner join product_erp pe on pe.code_pr=p.code_pr");
                while (r3.moveToNext()){
                    SpinnerItems.add(new SpinnerItem(r3.getString(r3.getColumnIndex("code_pr")),r3.getString(r3.getColumnIndex("nom_pr"))));
                }

                spinn=new MySpinnerSearchable(this,SpinnerItems ,getResources().getString(R.string.faireVente_selectPr), new MySpinnerSearchable.SpinnerConfig() {
                    @Override
                    public void onChooseItem(int pos, SpinnerItem item) {
                        //pos not fonctional yet ..

                        spinn.closeSpinner();
                        Intent i = new Intent();
                        i.putExtra("code", "" +item.key);
                        setResult(RESULT_OK, i);
                        finish();

                    }
                });


                selectPrButt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e("ccc","click");
                        spinn.openSpinner();
                    }
                });
                //endregion
            }

            //region select product si c est une commande
            if(getIntent().getExtras().getString("request").equals("Commande")){
                //region séléctioner le produit du camion ..
                TextView selectPrButt=((TextView)findViewById(R.id.terminal_select_Butt));
                selectPrButt.setVisibility(View.VISIBLE);

                ArrayList<SpinnerItem> SpinnerItems= new ArrayList<SpinnerItem>();
                //Cursor r3=Accueil.bd.read("select * from product_erp");
                Cursor r3=Accueil.bd.read("select * from  product_erp  ");
                while (r3.moveToNext()){
                    SpinnerItems.add(new SpinnerItem(r3.getString(r3.getColumnIndex("code_pr")),r3.getString(r3.getColumnIndex("nom_pr"))));
                }

                spinn=new MySpinnerSearchable(this,SpinnerItems ,getResources().getString(R.string.faireVente_selectPr), new MySpinnerSearchable.SpinnerConfig() {
                    @Override
                    public void onChooseItem(int pos, SpinnerItem item) {
                        //pos not fonctional yet ..

                        spinn.closeSpinner();
                        Intent i = new Intent();
                        i.putExtra("code", "" +item.key);
                        setResult(RESULT_OK, i);
                        finish();

                    }
                });


                selectPrButt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e("ccc","click");
                        spinn.openSpinner();
                    }
                });
                //endregion
            }


            //endregion


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            String code=data.getExtras().getString("code");
            Intent i = new Intent();
            i.putExtra("code", "" + code);
            setResult(RESULT_OK, i);
            finish();
        }
    }
}
