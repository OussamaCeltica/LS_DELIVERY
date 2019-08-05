package com.ls.celtica.lsdelivryls;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ls.celtica.lsdelivryls.Clients.AfficherClients;
import com.ls.celtica.lsdelivryls.Produits.AfficherProduits;

import com.ls.celtica.lsdelivryls.Reglements.AfficherReglements;
import com.ls.celtica.lsdelivryls.Ventes.AfficherBonRetour;
import com.ls.celtica.lsdelivryls.Ventes.AfficherCommandes;
import com.ls.celtica.lsdelivryls.Ventes.AfficherVentes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Accueil extends AppCompatActivity {

     public static MyBD bd;

     public static SqlServerBD BDsql;
     public static Accueil me;
    DrawerLayout mDrawerLayout;

    ProgressDialog progress;
    int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //test si le process est tué par le system ou nn ..
        if(savedInstanceState != null){
            Intent intent = new Intent(getApplicationContext(), DeviceConfig.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else {
            setContentView(R.layout.activity_accueil);
            bd = new MyBD("delivrydata.db", this);
            me=this;

            getLocation();

            //region check camera permission ..
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(Accueil.this, new String[]{Manifest.permission.CAMERA}, 8);

            }
            //endregion

            Cursor r=Accueil.bd.read("select strftime('%Y-%m-%d %H:%M','now','localtime') as d");
            while (r.moveToNext()){
                Log.e("ddd",r.getString(r.getColumnIndex("d"))+"");
            }




            //Accueil.bd.write("update facture set sync='0'");
            //Accueil.bd.write("insert into product_erp(code_pr,codebar,nom_pr,prix_v) values('PIF0002','6130093010045','COCA COLA CANNETTE','80')");
            //Accueil.bd.write("insert into produit(code_pr,codebar,nom_pr,stock,prix_vente) values('PIF0002','6130093010045','IFRI 50 cl','50','30')");
            //Accueil.bd.write("insert into camion(code_camion,nom_camion) values('gg-1','FORD Extra')");
            //Accueil.bd.write("insert into vendeur (code_vendeur,nom_vendeur) values('V0001','Amine')");

            progress = new ProgressDialog(Accueil.this); // activité non context ..

            //region Config DRawerLayout
            mDrawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);

            ((ImageView)findViewById(R.id.openDrawer_butt)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawerLayout.openDrawer(Gravity.LEFT);

                }
            });


            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            // set item as selected to persist highlight
                            //menuItem.setChecked(true);
                            if(menuItem.getItemId()== R.id.menu_parametrage){
                                startActivity(new Intent(Accueil.this,Parametrage.class));

                            }else if(menuItem.getItemId()== R.id.menu_synchronisation) {
                                startActivity(new Intent(Accueil.this, Synchronisation.class));
                            }
                            // close drawer when item is tapped
                            mDrawerLayout.closeDrawers();

                            // Add code here to update the UI based on the item selected
                            // For example, swap UI fragments here

                            return true;
                        }
                    });

            //endregion

            //region Afficher les clients ..
            ((LinearLayout) findViewById(R.id.butt_clt)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Accueil.this, AfficherClients.class));

                }
            });
            //endregion

            //region Afficher les Produits ..
            ((LinearLayout) findViewById(R.id.butt_pr)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Accueil.this, AfficherProduits.class));

                }
            });
            //endregion

            //region afficher les ventes ..
            ((LinearLayout) findViewById(R.id.butt_vente)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Accueil.this, AfficherVentes.class));


                }
            });
            //endregion

            //region afficher les Règlements ..
            ((LinearLayout) findViewById(R.id.butt_rglmt)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Accueil.this, AfficherReglements.class));


                }
            });
            //endregion

            //region commande ..
            ((LinearLayout) findViewById(R.id.butt_command)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                 startActivity(new Intent(Accueil.this, AfficherCommandes.class));

                }
            });
            //endregion

            //region Bon de retour ..
            ((LinearLayout) findViewById(R.id.butt_bon_retour)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startActivity(new Intent(Accueil.this, AfficherBonRetour.class));

                }
            });
            //endregion
        }
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
    //endregion

    //cette mathode on va l appelé pour tt demande d une permission donc pour différenier le permissions il faut test le request avec une switch ..

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode){
            case 123 :{ //si par example le code 123 de GPS qui invoqué cette methode ..
                if (grantResults[1]== PackageManager.PERMISSION_GRANTED){



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
