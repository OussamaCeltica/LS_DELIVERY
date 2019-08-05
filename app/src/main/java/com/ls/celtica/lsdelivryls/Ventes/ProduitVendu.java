package com.ls.celtica.lsdelivryls.Ventes;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.ls.celtica.lsdelivryls.Accueil;
import com.ls.celtica.lsdelivryls.Clients.ClientPrix;
import com.ls.celtica.lsdelivryls.DeviceConfig;
import com.ls.celtica.lsdelivryls.MyBD;
import com.ls.celtica.lsdelivryls.SqlServerBD;
import com.ls.celtica.lsdelivryls.Synchronisation;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by celtica on 23/09/18.
 */

public class ProduitVendu {

    /*cette class est utilisé dans l affichage des produitVendue "AfficherVentes"
      et aussi dans "FaireVente" pour effectué une vente ..
     */
    String codebar,nom,code_pr;
    double qt;
    double prix;
    double remise;

    private ArrayList<ClientPrix> clt_prix=new ArrayList<ClientPrix>();

    public ProduitVendu(String code_pr,String codebar, String nom,double qt, double prix) {
        this.codebar = codebar;
        this.qt = qt;
        this.prix = prix;
        this.nom=nom;
        this.code_pr=code_pr;
        remise=0;
        getAllPrix();

    }

    public ProduitVendu(String code_pr,String codebar, String nom,double qt, double prix,double remise) {
        this.codebar = codebar;
        this.qt = qt;
        this.prix = prix;
        this.nom=nom;
        this.code_pr=code_pr;
        this.remise=remise;
    }

    public void setRemise(double remise) {
        this.remise = remise;
    }

    public double getRemise(String code_fact){
        Cursor r=Accueil.bd.read("select remise from produit_facture where num_fact='"+code_fact+"' and code_pr='"+code_pr+"'");
        if(r.moveToNext()){
            return r.getDouble(r.getColumnIndex("remise"));
        }
        return 0;
    }

    //cette methode est dans ProduitVenduAdapter ..
    public double getPrixApresRemise(String code_fact){
        return prix-(prix*getRemise(code_fact)/100);
    }

    //l ajout de ce produit au Table "ProduitVendu" juste dans le cas de vente
    public void addToBD(int numBon,int id){

        //double prix_final=getPrixAfterRemise();
       // Accueil.bd.write("insert into produit_facture(id,code_pr,num_fact,quantity,prix_v) values('"+id+"','"+code_pr+"','"+numBon+"','"+qt+"','"+prix+"')");
        Accueil.bd.write2("insert into produit_facture(id,code_pr,num_fact,quantity,prix_v,nom_pr,remise) values('"+id+"','"+code_pr+"','"+numBon+"','"+qt+"','"+prix+"',?,'"+remise+"')", new MyBD.SqlPrepState() {
            @Override
            public void putValue(SQLiteStatement stmt) {
                stmt.bindString(1,nom);
                stmt.execute();
            }
        });
        //Accueil.bd.write("insert into produit_facture_archive(id,code_pr,num_fact,quantity,prix_v,nom_pr) values('"+id+"','"+code_pr+"','"+numBon+"','"+qt+"','"+prix+"','"+nom+"')");

        Accueil.bd.write("update produit set stock=stock-"+qt+" where code_pr='"+code_pr+"' ");

    }

    //cette methode j ai utilisé dans FaireVente.java
    public double getPrixAfterRemise(){
        return prix-(prix*remise/100);
    }

    private void getAllPrix(){
        Cursor r=Accueil.bd.read("select * from produit_prix_type where code_pr='"+code_pr+"' ");
        while (r.moveToNext()){
            if(r.getString(r.getColumnIndex("prix")) != null && !r.getString(r.getColumnIndex("prix")).equals("null")){
                clt_prix.add(new ClientPrix(r.getString(r.getColumnIndex("type_clt")),Double.parseDouble(r.getString(r.getColumnIndex("prix")))));
                //Log.e("ClientPrix","Type:"+r.getString(r.getColumnIndex("type_clt"))+" / prix"+r.getString(r.getColumnIndex("prix")));

            }
        }
    }

    public double getClientPrix(String type){
        int i=0;

        while(i != clt_prix.size()){
            if(clt_prix.get(i).type.equals(type)){
                Log.e("ClientPrix","OK:"+prix+" TYPE: "+type);
                return clt_prix.get(i).prix;
            }
            i++;
        }
        Log.e("ClientPrix","NOT OK:"+prix+" TYPE: "+type);

        return -1;
    }

    public void exportéProduitVendu(String record_b2,String numFact,String code_camion){
        Accueil.BDsql.write("insert into bon2 (RECORDID,NUM_BON,CODE_BARRE,QTE,PV_HT,PRODUIT,CODE_DEPOT,BLOCAGE,REMISE) values('" + record_b2 + "','" + (DeviceConfig.id_device + "_v_" + numFact) + "','" + codebar + "','" +qt + "','" + prix + "','" + code_pr + "','"+ code_camion + "','F','"+remise+"')", new SqlServerBD.doAfterBeforeGettingData() {
            @Override
            public void echec(SQLException e) {
                Log.e("errBon2","I m here");
                Synchronisation.existeErrImportExport=true;
                Accueil.BDsql.transactErr=true;
                Synchronisation.bon2_err="Erreur d insertion dans  bon2: "+e.getMessage()+" \n";

            }

            @Override
            public void before() {

            }

            @Override
            public void After() {
                Log.e("bon_succ_pr","Produit inséré");
            }
        });
    }


}
