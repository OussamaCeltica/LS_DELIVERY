package com.ls.celtica.lsdelivryls.Reglements;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ls.celtica.lsdelivryls.Accueil;
import com.ls.celtica.lsdelivryls.Clients.Client;
import com.ls.celtica.lsdelivryls.DeviceConfig;
import com.ls.celtica.lsdelivryls.Login;
import com.ls.celtica.lsdelivryls.MySpinner.MySpinnerSearchable;
import com.ls.celtica.lsdelivryls.MySpinner.SpinnerItem;
import com.ls.celtica.lsdelivryls.R;
import com.ls.celtica.lsdelivryls.User;
import com.ls.celtica.lsdelivryls.Ventes.AfficherBonRetour;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class FaireReglement extends AppCompatActivity {
    TextView clt;
    EditText montant;
    MySpinnerSearchable spinnerClient;
    String id_clt,nom_clt;

    int id_rglmt=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faire_reglement);


        if (savedInstanceState != null) {
            //region Revenir a au Deviceconfig ..
            Intent intent = new Intent(getApplicationContext(), DeviceConfig.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            //endregion
        } else {
            clt = (TextView) findViewById(R.id.rglmt_clt);
            montant = (EditText) findViewById(R.id.rglmt_montant);

            // region remplissage de spinnerDialog avec les clients ..
            ArrayList<SpinnerItem> SpinnerItems= new ArrayList<SpinnerItem>();

            Cursor r2=Accueil.bd.read("select * from client");
            while (r2.moveToNext()){
                SpinnerItems.add(new SpinnerItem(r2.getString(r2.getColumnIndex("code_clt")),r2.getString(r2.getColumnIndex("nom_clt"))));
            }

            spinnerClient=new MySpinnerSearchable(FaireReglement.this, SpinnerItems, getResources().getString(R.string.faireVente_selectClt), new MySpinnerSearchable.SpinnerConfig() {
                @Override
                public void onChooseItem(int pos, SpinnerItem item) {
                    //pos not fonctional yet .
                    selectClt(item);
                }
            }, new MySpinnerSearchable.ButtonSpinnerOnClick() {
                @Override
                public void onClick() {
                    Login.user.openScannerCodeBarre(FaireReglement.this, new User.OnScanListener() {
                        @Override
                        public void OnScan(String code) {

                            SpinnerItem item=cltCodeBarExiste(code);
                            if(item != null){
                                selectClt(item);
                            }else {
                                Toast.makeText(FaireReglement.this,getResources().getString(R.string.faireVente_noCodebarre),Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            });
           clt.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   spinnerClient.openSpinner();
               }
           });


            if(getIntent().getExtras() != null){
                nom_clt=getIntent().getExtras().getString("clt_nom");
                clt.setText(getIntent().getExtras().getString("clt_nom"));
                id_clt=getIntent().getExtras().getString("clt_code");
                Client c=new Client(id_clt);
                ((LinearLayout)findViewById(R.id.div_solde)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.solde_selectesdClt)).setText(User.formatPrix(c.getSolde())+" DA");

            }
            //endregion

            //region remplissage de spinner avec les mode de paiment..
            final Spinner modeReglement = (Spinner) findViewById(R.id.mode_rglmt);
            modeReglement.setPrompt("Choisir le mode de reglement");
            String[] wil = {getResources().getString(R.string.rglmt_ModEspece), getResources().getString(R.string.rglmt_ModCheq)};
            // modeReglement.setSelection(0);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_spinner_item, wil);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            modeReglement.setAdapter(adapter);

            modeReglement.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int y, long l) {
                    ((TextView) modeReglement.getSelectedView()).setTextColor(getResources().getColor(R.color.Black));


                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            //endregion

            //region Validation de reglement ..
            ((TextView) findViewById(R.id.valider_rglmt)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clt.getText().equals(getString(R.string.rglmt_selectClt)) || montant.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.remplissage_err), Toast.LENGTH_LONG).show();

                    } else {
                        Reglement reg=new Reglement(id_clt+"",nom_clt+"");
                        reg.AddReglementToBD(Double.parseDouble(montant.getText().toString()), modeReglement.getSelectedItem().toString());

                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.add_ok), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), AfficherReglements.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }
                }
            });
            //endregion

        }
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
        clt.setText(item.value);
        nom_clt = item.value;
        id_clt = item.key;
        Client c = new Client(item.key);
        spinnerClient.closeSpinner();
        ((LinearLayout) findViewById(R.id.div_solde)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.solde_selectesdClt)).setText(User.formatPrix(c.getSolde()) + " DA");

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if (data != null) {
                String code = data.getExtras().getString("code");
                SpinnerItem item = cltCodeBarExiste(code);
                if (item != null) {
                    selectClt(item);
                } else {
                    Toast.makeText(FaireReglement.this, getResources().getString(R.string.faireVente_noCodebarre), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(getIntent().getExtras() != null){
            Intent intent = new Intent(getApplicationContext(), Accueil.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
