package com.ls.celtica.lsdelivryls.Ventes;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.location.Location;
import android.util.Log;

import com.ls.celtica.lsdelivryls.Accueil;
import com.ls.celtica.lsdelivryls.Clients.ClientAdapter;
import com.ls.celtica.lsdelivryls.DeviceConfig;
import com.ls.celtica.lsdelivryls.ImprimerBon;
import com.ls.celtica.lsdelivryls.Login;
import com.ls.celtica.lsdelivryls.MyBD;
import com.ls.celtica.lsdelivryls.SqlServerBD;
import com.ls.celtica.lsdelivryls.Synchronisation;
import com.ls.celtica.lsdelivryls.User;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by celtica on 24/09/18.
 */

public class Bon {
    ETAT etat;

    public String code,date,nom_clt,id_clt,code_camion,code_vendeur,latitude,longitude,heure;
    double versement;
    private double remise;
    public int numBon=0;

    public Bon(String code, String date, double versement,String nom_clt,String id_clt) {
        this.code = code;
        this.date = date;
        this.id_clt=id_clt;
        this.nom_clt=nom_clt;
        this.versement=versement;
        this.remise=getRemise();

    }

    public Bon(String code, String date, double versement,String nom_clt,String id_clt,ETAT e) {
        this.code = code;
        this.date = date;
        this.id_clt=id_clt;
        this.nom_clt=nom_clt;
        this.versement=versement;
        this.remise=getRemise();
        etat=e;

    }

    //pour la synchronisation ..
    public Bon(String code, String date,String heure ,double remise,String id_clt,String code_camion,String code_vendeur,String latitude,String longitude) {
        this.code = code;
        this.date = date;
        this.id_clt=id_clt;
        this.nom_clt=nom_clt;
        this.versement=versement;
        this.remise=remise;
        this.heure=heure;
        this.code_vendeur=code_vendeur;
        this.code_camion=code_camion;
        this.longitude=longitude;
        this.latitude=latitude;

    }

    public Bon(String code){
        this.code=code;
        //recupération de ce bon ..
        Cursor r=Accueil.bd.read("select * from facture where num_fact='"+code+"'");
        if (r.moveToNext()){
            //inintialistion des attr ..
        }
    }

    public Bon(){
        remise=0;
        //region recupéré le dernier num de bon ..
        Cursor r = Accueil.bd.read("select num_bon as num_fact from derniere_importation order by num_fact desc limit 1");
        if (r.moveToNext()) {
            numBon = Integer.parseInt(r.getString(r.getColumnIndex("num_fact"))) + 1;
            Log.e("bbb",""+numBon);
            code= String.valueOf(numBon);
        }
        //endregion
    }

    public void setRemise(double remise) {
        this.remise = remise;
    }

    public double getRemise(){
        Cursor r=Accueil.bd.read("select remise from facture where num_fact='"+code+"'");
        if (r.moveToNext()){
           return r.getDouble(r.getColumnIndex("remise"));
        }
        return 0;
    }

    public double prixApresRemise(String prix){
        double prixx=Double.parseDouble(prix);
        Log.e("ppp","remise="+remise+" / prixNormal="+prix+" / prixRemise="+(prixx-(prixx*remise)));
        return prixx-(prixx*remise/100);
    }

    public String getTotal(){
        if(!AfficherVentes.modeArchive){
            Cursor r= Accueil.bd.read("select sum((prix_v-(prix_v*remise/100))*quantity) as tot from produit_facture where num_fact='"+code+"'  ");
            if (r.moveToNext()){
                return prixApresRemise(r.getString(r.getColumnIndex("tot")))+"";
            }
        }else {

            Cursor r= Accueil.bd.read("select sum((prix_v-(prix_v*remise/100))*quantity) as tot from produit_facture where num_fact='"+code+"'  ");
            if (r.moveToNext()){

                return prixApresRemise(r.getString(r.getColumnIndex("tot")))+"";
            }
        }
        return "0";
    }

    public String getTotalSansRemise(){
        if(!AfficherVentes.modeArchive){
            Cursor r= Accueil.bd.read("select sum((prix_v-(prix_v*remise/100))*quantity) as tot from produit_facture where num_fact='"+code+"'  ");
            if (r.moveToNext()){
                return r.getString(r.getColumnIndex("tot"))+"";
            }
        }else {

            Cursor r= Accueil.bd.read("select sum((prix_v-(prix_v*remise/100))*quantity) as tot from produit_facture where num_fact='"+code+"'  ");
            if (r.moveToNext()){

                return r.getString(r.getColumnIndex("tot"))+"";
            }
        }
        return "0";
    }

    public void addToBD(Location p){

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        nom_clt=ClientAdapter.clients.get(ClientAdapter.itemSelected).nom;

        //region creation d une facture du vente ..
       // Accueil.bd.write("insert into facture (num_fact,code_clt,verser,date_fact,sync,latitude,longitude,code_vendeur,code_camion) values ('" + numBon + "','" + ClientAdapter.clients.get(ClientAdapter.itemSelected).codebar + "','2345','" + date + "',0,'" + p.getLatitude() + "','" + p.getLongitude() + "','" + Login.user.vendeur + "','" + Login.user.code_camion + "') ");
        Accueil.bd.write2("insert into facture (num_fact,code_clt,verser,date_fact,sync,latitude,longitude,code_vendeur,code_camion,nom_clt,etat,remise) values ('" + numBon + "','" + ClientAdapter.clients.get(ClientAdapter.itemSelected).codebar + "','2345','" + date + "',0,'" + p.getLatitude() + "','" + p.getLongitude() + "','" + Login.user.vendeur + "','" + Login.user.code_camion + "',?,'vente','"+remise+"')", new MyBD.SqlPrepState() {
            @Override
            public void putValue(SQLiteStatement stmt) {
                stmt.bindString(1,ClientAdapter.clients.get(ClientAdapter.itemSelected).nom);
                stmt.execute();
            }
        });
        //endregion
    }


    public void updateNumBon(){
        Accueil.bd.write("update derniere_importation set num_bon='" + numBon + "'");
    }

    public void chnageEtatArchive(String etat){
        Accueil.bd.write("update facture set etat='"+etat+"' , sync='1' where num_fact='"+code+"'");
    }

    public void supprimerBon(){
        Accueil.bd.write("delete from facture where num_fact='"+code+"'");

    }

    public void exportéBon(String record_b1){
        Accueil.BDsql.write("insert into bon1 (RECORDID,NUM_BON,BLOCAGE ,CODE_CLIENT , CODE_DEPOT,CODE_VENDEUR,LATITUDE,LONGITUDE,DATE_BON,HEURE,TYPE_BON,REMISE) values('" + record_b1 + "','" + DeviceConfig.id_device + "_v_" + code + "','F','" + id_clt + "','" + code_camion + "','" + code_vendeur + "','" + latitude+ "','" + longitude + "',CAST("+ date + " as datetime),'" + heure + "','VENTE','"+remise+"')", new SqlServerBD.doAfterBeforeGettingData() {
            @Override
            public void echec(SQLException e) {
                Log.e("errBon","I m here");
                Accueil.BDsql.transactErr=true;
                Synchronisation.existeErrImportExport=true;
                Synchronisation.bon1_err="Erreur d insertion dans bon1: "+e.getMessage()+" \n";

            }

            @Override
            public void before() {

            }

            @Override
            public void After() {

                Log.e("bon_succ","Bon inséré");
            }
        });
    }

    public void imprimerBon(){
        ArrayList<String> pr=new ArrayList<String>();
        ArrayList<String> qt=new ArrayList<String>();
        ArrayList<String> prix=new ArrayList<String>();

        Cursor r=getProduitVendue();
        String msg="------------------------------------- \n \n";
        msg=msg+"Entreprise: "+Login.user.nom_company+" \n \n";
        msg=msg+"Client: "+nom_clt+" \n";
        msg=msg+"Numero de Bon: "+code;
        msg=msg+"\n \n------------------------------------- \n \n";
        msg=msg+"Produit            \t\t\t\t\t\t\t\t        Quantite       P.U \n";
        msg=msg+"_________________________________________________________ \n";

        while (r.moveToNext()) {
            pr.clear();
            qt.clear();
            prix.clear();
            formatString(r.getString(r.getColumnIndex("nom_pr")), 25, pr);
            formatString(r.getString(r.getColumnIndex("quantity")), 10, qt);
            formatString(r.getString(r.getColumnIndex("prix_v")), 13,prix);

            int i = 0;
            int max=getStringMaxVal(pr, qt, prix);
            while (i < max) {
                if (pr.size() > i){
                    msg=msg+""+pr.get(i)+"";
                }

                if (qt.size() > i ){
                    msg=msg+"       "+qt.get(i)+"";

                }

                if (prix.size() > i){
                    msg=msg+""+prix.get(i);
                    if( (i+1)>=prix.size()){
                        msg=msg+" DA";
                    }

                }
                i++;
                msg=msg+"\n";
            }

            msg=msg+"_________________________________________________________ \n";

        }

        msg=msg+"_________________________________________________________ \n\n";
        msg=msg+"\t \t \t \t  Total: "+ User.formatPrix(Double.parseDouble(getTotal()))+" DA\n";
        ImprimerBon.msg=msg;
        ImprimerBon.FindBluetoothDevice(Accueil.me);

    }

    public Cursor getProduitVendue(){
        Cursor r=Accueil.bd.read("select * from produit_facture pf  where num_fact='"+code+"'");
        return r;
    }

    public void formatString(String msgForForrmat,int colMax,ArrayList<String> arr){

        int i=0;
        int nbrOccurence=msgForForrmat.length()/colMax;
        if(nbrOccurence ==0){
            int i2=msgForForrmat.length();
            while (i2 != colMax-1){
                msgForForrmat=msgForForrmat+" ";
                i2++;
            }
            arr.add(msgForForrmat);
        }else {
            while ((i+1)<=nbrOccurence){
                arr.add(msgForForrmat.substring(i*colMax,i*colMax+(colMax-1)));
                i++;
            }
            i--;
            if(i != -1) {
                arr.add(msgForForrmat.substring(i * colMax + (colMax - 1), msgForForrmat.length()));
            }
        }

    }

    public int getStringMaxVal(ArrayList<String> pr,ArrayList<String> qt,ArrayList<String> prix){

        if(pr.size() > qt.size()){
            if (pr.size() > prix.size()){
                return pr.size();
            }else {
                return prix.size();
            }
        }else {
            if (qt.size() > prix.size()){
                return qt.size();
            }else {
                return prix.size();
            }
        }


    }


}
