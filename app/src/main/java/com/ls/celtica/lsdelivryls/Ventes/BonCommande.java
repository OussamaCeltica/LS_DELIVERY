package com.ls.celtica.lsdelivryls.Ventes;


import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.location.Location;
import android.util.Log;

import com.ls.celtica.lsdelivryls.Accueil;
import com.ls.celtica.lsdelivryls.Clients.ClientAdapter;
import com.ls.celtica.lsdelivryls.ImprimerBon;
import com.ls.celtica.lsdelivryls.Login;
import com.ls.celtica.lsdelivryls.MyBD;
import com.ls.celtica.lsdelivryls.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by celtica on 08/02/19.
 */

public class BonCommande extends Bon {
    public BonCommande(String code, String date, double versement, String nom_clt, String id_clt) {
        super(code, date, versement, nom_clt, id_clt);
    }

    public BonCommande(){
        //region recupéré le dernier num de bon ..
        Cursor r = Accueil.bd.read("select num_bon_commande as num_fact from derniere_exportation order by num_fact desc limit 1");
        if (r.moveToNext()) {
            numBon = Integer.parseInt(r.getString(r.getColumnIndex("num_fact"))) + 1;
            code=String.valueOf(numBon);
            Log.e("bbb",""+numBon);
        }
        //endregion

    }

    public BonCommande(String code){
        this.code=code;
        //recupération de ce bon ..
        Cursor r=Accueil.bd.read("select * from bon_commande where num_comm='"+code+"'");
        if (r.moveToNext()){
            //inintialistion des attr ..
        }
    }

    public void updateNumBon(){
        Accueil.bd.write("update derniere_exportation set num_bon_commande='" + numBon + "'");
    }

    @Override
    public void chnageEtatArchive(String etat) {
        Accueil.bd.write("update bon_commande set etat='"+etat+"' , sync='1' where num_comm='"+code+"'");

    }

    public Cursor getProduitsCommande(){
        Cursor r=Accueil.bd.read("select * from produit_commande where num_comm='"+code+"'");
        return r;
    }

    @Override
    public void imprimerBon(){
        ArrayList<String> pr=new ArrayList<String>();
        ArrayList<String> qt=new ArrayList<String>();
        ArrayList<String> prix=new ArrayList<String>();

        Cursor r=getProduitsCommande();
        String msg="----------------------------------------------------- \n \n";
        msg=msg+"Entreprise: "+Login.user.nom_company+" \n \n";
        msg=msg+"Client: "+nom_clt+" \n";
        msg=msg+"Bon de commande: "+code;
        msg=msg+"\n \n---------------------------------------------------- \n \n";
        msg=msg+"Produit            \t\t\t\t\t\t\t\t        Quantite       P.U \n";
        msg=msg+"_________________________________________________________ \n";

        while (r.moveToNext()) {
            pr.clear();
            qt.clear();
            prix.clear();
            formatString(r.getString(r.getColumnIndex("nom_pr")), 23, pr);
            formatString(r.getString(r.getColumnIndex("quantity")), 13, qt);
            formatString(r.getString(r.getColumnIndex("prix_v")), 12,prix);

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

    public void addCommandeToBD(Location p){
        nom_clt=ClientAdapter.clients.get(ClientAdapter.itemSelected).nom;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = df.format(Calendar.getInstance().getTime());

        //region creation d un bon du commande ..
        Accueil.bd.write2("insert into bon_commande (num_comm,code_clt,verser,date_comm,sync,latitude,longitude,code_vendeur,code_camion,nom_clt,etat) values ('" + numBon + "','" + ClientAdapter.clients.get(ClientAdapter.itemSelected).codebar + "','0','" + date + "',0,'" + p.getLatitude() + "','" + p.getLongitude() + "','" + Login.user.vendeur + "','" + Login.user.code_camion + "',?,'commande')", new MyBD.SqlPrepState() {
            @Override
            public void putValue(SQLiteStatement stmt) {
                stmt.bindString(1,ClientAdapter.clients.get(ClientAdapter.itemSelected).nom);
                stmt.execute();
            }
        });
        updateNumBon();
        //endregion

    }

    public String getTotal(){

            Cursor r= Accueil.bd.read("select sum(prix_v*quantity) as tot from produit_commande where num_comm='"+code+"'  ");
            if (r.moveToNext()){
                return r.getString(r.getColumnIndex("tot"));
            }

        return "0";
    }



}
