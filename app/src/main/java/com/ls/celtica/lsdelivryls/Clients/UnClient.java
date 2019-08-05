package com.ls.celtica.lsdelivryls.Clients;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ls.celtica.lsdelivryls.Accueil;
import com.ls.celtica.lsdelivryls.DeviceConfig;
import com.ls.celtica.lsdelivryls.Login;

import com.ls.celtica.lsdelivryls.R;
import com.ls.celtica.lsdelivryls.Ventes.AfficherVentes;
import com.ls.celtica.lsdelivryls.Ventes.FaireVente;
import com.ls.celtica.lsdelivryls.Ventes.PanierAdapter;
import com.ls.celtica.lsdelivryls.showClientLocation;

public class UnClient extends AppCompatActivity {
    int i; // index de client dans le tableau des clientAdapter ..
    String dernierCamion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            Intent intent = new Intent(getApplicationContext(), DeviceConfig.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            setContentView(R.layout.activity_un_client);
            ((TextView) findViewById(R.id.clt_titre)).setText(ClientAdapter.clients.get(ClientAdapter.itemSelected).nom);

            ((TextView) findViewById(R.id.num_clt)).setText(ClientAdapter.clients.get(ClientAdapter.itemSelected).tel);
            ((TextView) findViewById(R.id.gps_clt)).setText(ClientAdapter.clients.get(ClientAdapter.itemSelected).latitude + " / " + ClientAdapter.clients.get(ClientAdapter.itemSelected).longitude);
            ((TextView) findViewById(R.id.adr_clt)).setText(ClientAdapter.clients.get(ClientAdapter.itemSelected).adr);
            ((TextView) findViewById(R.id.solde_clt)).setText(PanierAdapter.formatPrix(ClientAdapter.clients.get(ClientAdapter.itemSelected).solde) + " DA");
            Log.e("sss",ClientAdapter.clients.get(ClientAdapter.itemSelected).solde+" da");
            ((TextView) findViewById(R.id.type_clt)).setText(ClientAdapter.clients.get(ClientAdapter.itemSelected).type);

            //region Changer les cordonnées GPS ..
            ((TextView)findViewById(R.id.aff_clt_modifGPS)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder mb = new AlertDialog.Builder(UnClient.this); //c est l activity non le context ..


                    View v= getLayoutInflater().inflate(R.layout.confirm_box,null);
                    mb.setView(v);
                    final AlertDialog ad=mb.create();
                    ad.show();
                    TextView msg=(TextView) v.findViewById(R.id.confirm_msg);
                    msg.setText(getResources().getString(R.string.aff_clt_modifGPS_msg));
                    TextView oui=(TextView) v.findViewById(R.id.confirm_oui);
                    TextView non=(TextView) v.findViewById(R.id.confirm_non);


                    oui.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Location l=getLocation();
                            if(l != null){
                                String lat,longi;

                                lat=String.valueOf(l.getLatitude());
                                longi=String.valueOf(l.getLongitude());

                                if(lat.length()>10){
                                    lat=lat.substring(0,10);
                                }
                                if(longi.length()>10){
                                    longi=longi.substring(0,10);
                                }

                                Cursor r= Accueil.bd.read("select code_clt from update_gps where code_clt='"+ClientAdapter.clients.get(ClientAdapter.itemSelected).codebar+"'");
                                if(!r.moveToNext()){
                                    Accueil.bd.write("insert into update_gps (code_clt) values('"+ClientAdapter.clients.get(ClientAdapter.itemSelected).codebar+"') ");
                                }
                                Accueil.bd.write("update client set latitude='"+lat+"',longitude='"+longi+"' where code_clt='"+ClientAdapter.clients.get(ClientAdapter.itemSelected).codebar+"' ");
                                ad.dismiss();
                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.aff_clt_modifGPS_succ),Toast.LENGTH_SHORT).show();

                                Log.e("ppp",""+lat+" / "+longi);
/*


                                try {
                                    Accueil.bd.beginTransact();
                                    //les requete sont ici ..
                                    Cursor r=Accueil.bd.read("select code_clt from update_gps where code_clt='"+ClientAdapter.clients.get(ClientAdapter.itemSelected).codebar+"'");
                                    if(!r.moveToNext()){
                                        Accueil.bd.write("insert into update_gps (code_clt) values('"+ClientAdapter.clients.get(ClientAdapter.itemSelected).codebar+"') ");
                                    }

                                    Accueil.bd.write("update client set latitude='"+lat+"',longitude='"+longi+"' where code_clt='"+ClientAdapter.clients.get(ClientAdapter.itemSelected).codebar+"' ");
                                    Accueil.bd.setSuccefulTransact(); // like Commit ..
                                    ad.dismiss();
                                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.aff_clt_modifGPS_succ),Toast.LENGTH_SHORT).show();
                                }catch (SQLException e) {
                                    e.printStackTrace();
                                    Log.e("TRansact","Mahabetch .."+e.getMessage());

                                }finally {
                                    Accueil.bd.endTransact();
                                }

 */

                            }else {
                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.update_clt_gps_err),Toast.LENGTH_SHORT).show();
                                ad.dismiss();
                            }
                        }
                    });

                    non.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ad.dismiss();
                        }
                    });



                }
            });
            //endregion

            //region Ouvrir le l Activité FaireVnte pour effectué une vente ..
            ((LinearLayout) findViewById(R.id.clt_faireVente_butt)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Login.user.vendeur.equals(getResources().getString(R.string.login_pseudoHint)) || Login.user.code_camion.equals(getResources().getString(R.string.login_user_descCamion))){
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

                        else {
                            Intent i=new Intent(UnClient.this, FaireVente.class);
                            i.putExtra("request","Vente");
                            startActivity(i);
                        }
                    }

                }
            });
            //endregion

            //region afficher la location des client ..
            ((ImageView)findViewById(R.id.clt_location)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Location p=getLocation();
                    if(p != null){
                        Login.user.pos=p;
                        startActivity(new Intent(UnClient.this,showClientLocation.class));
                    }else {
                        Toast.makeText(getApplicationContext(),"Activer le GPS !",Toast.LENGTH_SHORT).show();
                    }

                }
            });
            //endregion
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
