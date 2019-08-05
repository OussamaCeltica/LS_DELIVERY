package com.ls.celtica.lsdelivryls;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class DeviceConfig extends AppCompatActivity {
    public  static String id_device;
    public  static boolean ProcessKilled=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ProcessKilled=false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_config);
        Accueil.bd=new MyBD("delivrydata.db",this);

        //region configuration de la langue
        Cursor r2=Accueil.bd.read("select * from langue");
        if(r2.moveToNext()){
                changeLang(this,r2.getString(r2.getColumnIndex("lang")));
        }
        //endregion

        Cursor r=Accueil.bd.read("select * from device");
        if(r.moveToNext()){
            id_device=r.getString(r.getColumnIndex("id_device"));
            startActivity(new Intent(DeviceConfig.this,Login.class));
            finish();
        }else {

            //region inséré le code dans la BD
            final EditText id=(EditText)findViewById(R.id.deviceconfig_id);

            ((TextView)findViewById(R.id.deviceconfig_valider)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(id.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.remplissage_err),Toast.LENGTH_LONG).show();
                    }else {
                        Accueil.bd.write("insert into device (id_device) values('"+id.getText().toString()+"')");
                        startActivity(new Intent(DeviceConfig.this,Login.class));
                        finish();

                    }
                }
            });

            //endregion
        }
    }

    public static void changeLang(AppCompatActivity context, String lang) {
        Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }
}
