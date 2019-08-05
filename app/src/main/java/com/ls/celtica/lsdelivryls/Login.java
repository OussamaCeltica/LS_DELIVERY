package com.ls.celtica.lsdelivryls;

import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.ls.celtica.lsdelivryls.MySpinner.MySpinnerSearchable;
import com.ls.celtica.lsdelivryls.MySpinner.SpinnerItem;

import java.util.ArrayList;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class Login extends AppCompatActivity {
    TextView vendeur,camion,mode;
    EditText mdp;

    MySpinnerSearchable spinnerVendor,spinnerCamion;
    public static Login me;


    ArrayList<String> modes=new ArrayList<String>();
    SpinnerDialog spinnerDialog;

    SpinnerDialog code_vendor,code_camion;
    public  static User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Accueil.bd=new MyBD("delivrydata.db",getApplicationContext());
    //    startActivity(new Intent(Login.this,Accueil.class));

        me=this;

        vendeur=(TextView)findViewById(R.id.login_vendeur);
        camion=(TextView)findViewById(R.id.login_camion);
        mode=(TextView)findViewById(R.id.login_mode);
       // mdp=(EditText)findViewById(R.id.login_mdp);

        try {
            UpdaterBD.update(this);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        //region Tester si on a deja une session connecté pour un vendeur  ..
        Cursor r=Accueil.bd.read("select * from utilisateur ");
        if(r.moveToNext()) {
            user=new User(r.getString(r.getColumnIndex("code_camion")),r.getString(r.getColumnIndex("code_vendeur")),r.getString(r.getColumnIndex("type_device")),r.getString(r.getColumnIndex("mode")));
            startActivity(new Intent(Login.this,Accueil.class));
            finish();
        }
        //endregion

        //region configuration du mode d acces ..
        modes.add(getResources().getString(R.string.login_modeAdmin));
        modes.add(getResources().getString(R.string.login_modeVendeur));

        spinnerDialog=new SpinnerDialog(Login.this,modes,"",R.style.DialogAnimations_SmileWindow,"Close");// With 	Animation

        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                //Toast.makeText(MainActivity.this, item + "  " + position+"", Toast.LENGTH_SHORT).show();

                if(item.equals(getResources().getString(R.string.login_modeVendeur))){
                    ((LinearLayout)findViewById(R.id.div_modeVendeur)).setVisibility(View.VISIBLE);
                }else {
                    ((LinearLayout)findViewById(R.id.div_modeVendeur)).setVisibility(View.GONE);
                }
                ((TextView)findViewById(R.id.login_valider)).setVisibility(View.VISIBLE);
                mode.setText(item);//selectedItems c le textView ou je met ma selection ..

            }
        });
        mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerDialog.showSpinerDialog();
            }
        });
        //endregion

        //region remplissage de spinner code vendeur ..
        ArrayList<SpinnerItem> vendor_items= new ArrayList<SpinnerItem>();
        Cursor r1 = Accueil.bd.read("select * from vendeur");
        while (r1.moveToNext()) {
            vendor_items.add(new SpinnerItem(r1.getString(r1.getColumnIndex("code_vendeur")),r1.getString(r1.getColumnIndex("nom_vendeur"))));
        }

        spinnerVendor=new MySpinnerSearchable(this,vendor_items ,getResources().getString(R.string.login_pseudoHint), new MySpinnerSearchable.SpinnerConfig() {
            @Override
            public void onChooseItem(int pos, SpinnerItem item) {
                //pos not fonctional yet ..
                vendeur.setText(item.key);
                spinnerVendor.closeSpinner();

            }
        });

        vendeur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerVendor.openSpinner();
            }
        });


        /*

        Cursor r5=Accueil.bd.read("select * from vendeur");
        while (r5.moveToNext()){
            vendeurs.add(r5.getString(r5.getColumnIndex("code_vendeur")));
        }
          //items c est un arrayList des string ..
         code_vendor=new SpinnerDialog(Login.this,vendeurs,"",R.style.DialogAnimations_SmileWindow,"Close");// With 	Animation


        code_vendor.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                //Toast.makeText(MainActivity.this, item + "  " + position+"", Toast.LENGTH_SHORT).show();
                vendeur.setText(item);//selectedItems c le textView ou je met ma selection ..

            }
        });
        vendeur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                code_vendor.showSpinerDialog();
            }
        });
        */
        //endregion

        //region remplissage de spinner avec les code camion
        ArrayList<SpinnerItem> camion_items= new ArrayList<SpinnerItem>();
        Cursor r9 = Accueil.bd.read("select * from camion");
        while (r9.moveToNext()) {
            camion_items.add(new SpinnerItem(r9.getString(r9.getColumnIndex("code_camion")),r9.getString(r9.getColumnIndex("nom_camion"))));
        }

        spinnerCamion=new MySpinnerSearchable(this,camion_items ,getResources().getString(R.string.login_user_descCamion), new MySpinnerSearchable.SpinnerConfig() {
            @Override
            public void onChooseItem(int pos, SpinnerItem item) {
                //pos not fonctional yet ..
                camion.setText(item.key);
                spinnerCamion.closeSpinner();

            }
        });

        camion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerCamion.openSpinner();
            }
        });





        /*
        //récupération des codes camion from bd local ..
         Cursor r2=Accueil.bd.read("select * from camion");
         while (r2.moveToNext()){
             camions.add(r2.getString(r2.getColumnIndex("code_camion")));
         }

        //items c est un arrayList des string ..
        code_camion=new SpinnerDialog(Login.this,camions,"",R.style.DialogAnimations_SmileWindow,"Close");// With 	Animation


        code_camion.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                //Toast.makeText(MainActivity.this, item + "  " + position+"", Toast.LENGTH_SHORT).show();
                camion.setText(item);//selectedItems c le textView ou je met ma selection ..

            }
        });
        camion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                code_camion.showSpinerDialog();
            }
        });

         */

        //endregion

        //region remplissage de spinner avec les type device
        Cursor r2=Accueil.bd.read("select * from admin");
        if (r2.moveToNext()){
            if (r2.getString(r2.getColumnIndex("type_device")) == null){
                Accueil.bd.write("update admin set type_device='"+getResources().getString(R.string.login_device_type1)+"' ");
            }
        }

        final Spinner  type_mobile=(Spinner)findViewById(R.id.login_type_mobile);
        type_mobile.setPrompt("");
        String[] wil ={getResources().getString(R.string.login_device_type1),getResources().getString(R.string.login_device_type2)};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.myspinner_item, wil);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_mobile.setAdapter(adapter);

        type_mobile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int y, long l) {
                //lors de changement de selection ..
                ((TextView)type_mobile.getSelectedView()).setTextColor(getResources().getColor(R.color.White)); // set text color of selected item ..

                type_mobile.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //endregion

        //region valider les choix ...
        ((TextView)findViewById(R.id.login_valider)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Accueil.bd.write("insert into utilisateur(code_camion,code_vendeur,type_device,mode) values('"+camion.getText().toString()+"','"+vendeur.getText().toString()+"','"+type_mobile.getSelectedItem().toString()+"','"+mode.getText().toString()+"')");
                user=new User(camion.getText().toString(),vendeur.getText().toString(),type_mobile.getSelectedItem().toString(),mode.getText().toString());

                startActivity(new Intent(Login.this,Accueil.class));
                finish();
                /*
                if(pseudo.getText().toString().equals("") || mdp.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.remplissage_err),Toast.LENGTH_LONG).show();

                }else{
                    Cursor r=Accueil.bd.read("select * from utilisateur where pseudo='"+pseudo.getText().toString().replaceAll("'","")+"'");
                    if(r.moveToNext()){
                        if(r.getString(r.getColumnIndex("mdp")).equals(mdp.getText().toString())){
                            user=new User(camions.getSelectedItem().toString(),pseudo.getText().toString(),mdp.getText().toString(),type_mobile.getSelectedItem().toString());

                        }else {
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.login_mdp_err),Toast.LENGTH_LONG).show();

                        }
                    }else {
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.login_user_err),Toast.LENGTH_LONG).show();

                    }
                }*/

            }
        });
        //endregion

    }
}
