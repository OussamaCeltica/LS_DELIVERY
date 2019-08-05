package com.ls.celtica.lsdelivryls.Clients;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ls.celtica.lsdelivryls.Accueil;
import com.ls.celtica.lsdelivryls.DeviceConfig;
import com.ls.celtica.lsdelivryls.R;
import com.google.android.gms.maps.model.LatLng;

public class AddClient extends AppCompatActivity {
    public static Client c;
    private EditText nom,tel,adr;
      Spinner type;
    public static LatLng p;
      AlertDialog ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);
        if (savedInstanceState != null) {
            Intent intent = new Intent(getApplicationContext(), DeviceConfig.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {

            nom = (EditText) findViewById(R.id.addClt_nom);
            tel = (EditText) findViewById(R.id.addClt_tel);
            adr = (EditText) findViewById(R.id.addClt_adr);

            //region configuration de spinner des types clt ..
            type = (Spinner) findViewById(R.id.addClt_type);
            type.setPrompt("select type client");
            Cursor r = Accueil.bd.read("select * from type_client");
            String[] type_clt = new String[r.getCount() + 1];
            type_clt[0] = "Autre";
            int ind = 1;//index pour parcourir le tableau des type ..
            while (r.moveToNext()) {
                type_clt[ind] = r.getString(r.getColumnIndex("type_clt"));
                ind++;
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_spinner_item, type_clt);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            type.setAdapter(adapter);

            type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int y, long l) {
                    //lors de changement de selection ..
                    ((TextView) type.getSelectedView()).setTextColor(getResources().getColor(R.color.Black)); // set text color of selected item ..

                    type.getSelectedItem().toString();

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            //endregion

            //region lors de click valider un client ..
            ((TextView) findViewById(R.id.addClt_valider)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (nom.getText().toString().equals("") || tel.getText().toString().equals("") || adr.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.remplissage_err), Toast.LENGTH_LONG).show();
                    } else {
                        final AlertDialog.Builder mb = new AlertDialog.Builder(AddClient.this); //c est l activity non le context ..

                        //Mode de Récupération de Location (Position Actuel ou Choix d apres la map)
                        View v = getLayoutInflater().inflate(R.layout.div_position, null);

                        //Cordonnée de position Actuel ..
                        ((TextView) v.findViewById(R.id.add_pos_actuel)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Location loc = getLocation();

                                if (loc != null) {
                                    p = new LatLng(loc.getLatitude(), loc.getLongitude());
                                    ad.dismiss();
                                    // Log.e("Position","Lat="+p.getLatitude()+" / Longi="+p.getLongitude());

                                    Client c = new Client("", nom.getText().toString(), tel.getText().toString(), adr.getText().toString(), p.latitude, p.longitude, 0, 0, type.getSelectedItem().toString());
                                    addClient(c);

                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.add_clt_succes), Toast.LENGTH_LONG).show();
                                    finish();

                                }


                            }
                        });

                        // roooh l google Map ..
                        ((TextView) v.findViewById(R.id.add_pos_map)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                startActivityForResult(new Intent(AddClient.this, ClientLocation.class), 3);

                            }
                        });

                        mb.setView(v);
                        ad = mb.create();
                        ad.show();
                    }


                }
            });
            //endregion
        }
    }



    public static void addClient(Client c){
        int code=0;

        Cursor r=Accueil.bd.read("select code_clt from client where sync='0' order by code_clt desc limit 1");
        if(r.moveToNext()){
            code=Integer.parseInt(r.getString(r.getColumnIndex("code_clt")))+1;

        }
        String lat,longi;

        lat=String.valueOf(c.latitude);
        longi=String.valueOf(c.longitude);

        if(lat.length()>10){
            lat=lat.substring(0,10);
        }
        if(longi.length()>10){
            longi=longi.substring(0,10);
        }

        Accueil.bd.write("insert into client (code_clt,nom_clt,adr_clt,tél,latitude,longitude,solde,verser,sync,type) values('"+code+"','"+c.nom.replaceAll("'","")+"','"+c.adr.replaceAll("'","")+"','"+c.tel+"','"+lat+"','"+longi+"','0','0','0','"+c.type+"')");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3) {
            if (resultCode == RESULT_OK) { // Activity.RESULT_OK
                ad.dismiss();

                Bundle b=data.getExtras();

                p=new LatLng(Double.parseDouble(b.getString("Latitude")),Double.parseDouble(b.getString("Longitude")));

                Client c=new Client("",nom.getText().toString(),tel.getText().toString(),adr.getText().toString(),p.latitude,p.longitude,0,0,type.getSelectedItem().toString());
                addClient(c);

                Toast.makeText(getApplicationContext(),"Le Client est ajouté.",Toast.LENGTH_LONG).show();

            }
        }

    }

    //Récupération les cordoné GPS Actuel ..
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

}
