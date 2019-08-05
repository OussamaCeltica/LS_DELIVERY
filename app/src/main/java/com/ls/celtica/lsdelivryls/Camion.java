package com.ls.celtica.lsdelivryls;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by celtica on 17/01/19.
 */

public class Camion {
    public String code,name;
    private String produit="";

    public Camion(String code) {
        this.code = code;
        getName();
    }

    private void getName(){
        Cursor r=Accueil.bd.read("select nom_camion from camion where code_camion='"+code+"'");
        if (r.moveToNext()){
            name=r.getString(r.getColumnIndex("nom_camion"));

        }

    }

    public void getProducts(final AppCompatActivity c, final ProgressDialog progess) {


        Accueil.BDsql.read("select distinct p.id,p.EAN13Code code_barre, p.name produit,p.salePrice_tax_included As default_price, wpl.stock, ct.designation type_client, \n" +
                    " pct.priceBeforeTax prix_ht, pct.priceAfterTax prix_ttc " +
                    "from WarehouseProductLine wpl inner join Warehouse w on wpl.warehouse = w.oid " +
                    "inner join Truck t on w.oid = t.oid inner join Product p on wpl.product = p.oid " +
                    "left join ProductCustomerType pct on p.oid = pct.product " +
                    "left join CustomerType ct on pct.customerType = ct.oid " +
                    "where w.id = '" + Login.user.code_camion + "' and wpl.stock > 0", new SqlServerBD.doAfterBeforeGettingData() {
                @Override
                public void echec(SQLException e) {
                    Synchronisation.existeErrImportExport=true;
                    Synchronisation.ImportationErr=Synchronisation.ImportationErr+"erreur de récupération des produit: "+e.getMessage()+" \n";

                }

                @Override
                public void before() {


                }

                @Override
                public void After() {
                    // progess.dismiss();
                    final ResultSet r = Accueil.BDsql.r;
                    Accueil.bd.write("update camion_actuel set code_camion='" + Login.user.code_camion + "' where '1'='1'");
                    try {
                        while (r.next()) {
                            // Log.e("prr",r.getString("id")+" / "+r.getString("type_client")+" / "+r.getString("prix_ttc")+" / "+ r.getString("stock")+" / "+r.getString("produit"));
                            if (!produit.equals(r.getString("id"))) {
                                produit = r.getString("id");
                                try{
                                    Accueil.bd.write2("insert into produit (code_pr,stock) values(?,?)", new MyBD.SqlPrepState() {
                                        @Override
                                        public void putValue(SQLiteStatement stmt) {
                                            try {
                                                stmt.bindString(1,convertNull(r.getString("id")));
                                                stmt.bindString(2, convertNullDouble(r.getString("stock")));
                                                stmt.execute();
                                                Accueil.bd.write("update product_erp set prix_v='"+convertNullDouble(r.getString("default_price"))+"' where code_pr='"+convertNull(r.getString("id"))+"'");
                                            }catch (android.database.SQLException e){
                                                Synchronisation.ImportationErr=Synchronisation.ImportationErr+"erreur d insertion des produits du camion: "+e.getMessage()+" \n";

                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });
                                }catch (android.database.SQLException e){

                                }

                                if (r.getString("type_client") != null) {
                                    Accueil.bd.write2("replace into  produit_erp_prix_type (code_pr,type_clt,prix) values(?,?,'" + r.getString("prix_ttc") + "')", new MyBD.SqlPrepState() {
                                        @Override
                                        public void putValue(SQLiteStatement stmt) {

                                            try {
                                                stmt.bindString(1, convertNull(r.getString("id")));
                                                stmt.bindString(2, convertNullDouble(r.getString("type_client")));
                                                stmt.execute();
                                            } catch (SQLException e) {

                                                e.printStackTrace();
                                            }
                                            catch (final android.database.SQLException e1){
                                                Synchronisation.existeErrImportExport=true;
                                                Synchronisation.ImportationErr=Synchronisation.ImportationErr+"erreur d insertion des prix par type client: "+e1.getMessage()+" \n";

                                            }


                                        }
                                    });

                                }
                            } else {
                                Accueil.bd.write2("replace into  produit_erp_prix_type (code_pr,type_clt,prix) values(?,?,'" + r.getString("prix_ttc") + "')", new MyBD.SqlPrepState() {
                                    @Override
                                    public void putValue(SQLiteStatement stmt) {

                                        try {
                                            stmt.bindString(1, convertNull(r.getString("id")));
                                            stmt.bindString(2, convertNullDouble(r.getString("type_client")));
                                            stmt.execute();
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                        catch (final android.database.SQLException e1){
                                            Synchronisation.existeErrImportExport=true;
                                            Synchronisation.ImportationErr=Synchronisation.ImportationErr+"erreur d insertion des prix par type client: "+e1.getMessage()+" \n";

                                        }


                                    }
                                });
                                //  Accueil.bd.write("insert into produit_prix_type (code_pr,type_clt,prix) values('" + r.getString("id") + "','" + r.getString("type_client").replaceAll("`","") + "','" + r.getString("prix_ttc") + "')");
                            }

                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }


                }
            });



        /*
        try {
            Accueil.BDsql.read("select distinct p.id,p.EAN13Code code_barre, p.name produit,p.salePrice_tax_included As default_price, wpl.stock, ct.designation type_client, \n" +
                    " pct.priceBeforeTax prix_ht, pct.priceAfterTax prix_ttc " +
                    "from WarehouseProductLine wpl inner join Warehouse w on wpl.warehouse = w.oid " +
                    "inner join Truck t on w.oid = t.oid inner join Product p on wpl.product = p.oid " +
                    "left join ProductCustomerType pct on p.oid = pct.product " +
                    "left join CustomerType ct on pct.customerType = ct.oid " +
                    "where w.id = '" + Login.user.code_camion + "'", new SqlServerBD.doAfterBeforeGettingData() {
                @Override
                public void echec(SQLException e) {
                    Synchronisation.existeErrImportExport=true;
                    Synchronisation.ImportationErr=Synchronisation.ImportationErr+"erreur de récupération des produit: "+e.getMessage()+" \n";

                }

                @Override
                public void before() {


                }

                @Override
                public void After() throws SQLException {
                   // progess.dismiss();
                    final ResultSet r = Accueil.BDsql.r;
                    Accueil.bd.write("update camion_actuel set code_camion='" + Login.user.code_camion + "' where '1'='1'");
                    while (r.next()) {
                        // Log.e("prr",r.getString("id")+" / "+r.getString("type_client")+" / "+r.getString("prix_ttc")+" / "+ r.getString("stock")+" / "+r.getString("produit"));
                        if (!produit.equals(r.getString("id"))) {
                            produit = r.getString("id");
                            Accueil.bd.write2("insert into produit(code_pr,codebar,stock,nom_pr,prix_vente) values(?,'" + r.getString("code_barre") + "',?,?,?)", new MyBD.SqlPrepState() {
                                @Override
                                public void putValue(SQLiteStatement stmt) {
                                    try {
                                        stmt.bindString(1, convertNull(r.getString("id")));
                                        stmt.bindString(2, convertNullDouble(r.getString("stock")));
                                        stmt.bindString(3, convertNull(r.getString("produit")));
                                        stmt.bindString(4, convertNullDouble(r.getString("default_price")));
                                        stmt.execute();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    catch (android.database.SQLException e1){
                                        Synchronisation.existeErrImportExport=true;
                                        Synchronisation.ImportationErr=Synchronisation.ImportationErr+"erreur d insertion des produit: "+e1.getMessage()+" \n";

                                    }

                                }
                            });

                            if (r.getString("type_client") != null) {
                                Accueil.bd.write2("insert into produit_prix_type (code_pr,type_clt,prix) values(?,?,'" + r.getString("prix_ttc") + "')", new MyBD.SqlPrepState() {
                                    @Override
                                    public void putValue(SQLiteStatement stmt) {

                                        try {
                                            stmt.bindString(1, convertNull(r.getString("id")));
                                            stmt.bindString(2, convertNullDouble(r.getString("type_client")));
                                            stmt.execute();
                                        } catch (SQLException e) {

                                            e.printStackTrace();
                                        }
                                        catch (final android.database.SQLException e1){
                                            Synchronisation.existeErrImportExport=true;
                                            Synchronisation.ImportationErr=Synchronisation.ImportationErr+"erreur d insertion des prix par type client: "+e1.getMessage()+" \n";

                                        }


                                    }
                                });

                            }
                        } else {
                            Accueil.bd.write2("insert into produit_prix_type (code_pr,type_clt,prix) values(?,?,'" + r.getString("prix_ttc") + "')", new MyBD.SqlPrepState() {
                                @Override
                                public void putValue(SQLiteStatement stmt) {

                                    try {
                                        stmt.bindString(1, convertNull(r.getString("id")));
                                        stmt.bindString(2, convertNullDouble(r.getString("type_client")));
                                        stmt.execute();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    catch (final android.database.SQLException e1){
                                        Synchronisation.existeErrImportExport=true;
                                        Synchronisation.ImportationErr=Synchronisation.ImportationErr+"erreur d insertion des prix par type client: "+e1.getMessage()+" \n";

                                    }


                                }
                            });
                            //  Accueil.bd.write("insert into produit_prix_type (code_pr,type_clt,prix) values('" + r.getString("id") + "','" + r.getString("type_client").replaceAll("`","") + "','" + r.getString("prix_ttc") + "')");
                        }

                    }



                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
         */


    }

    public Cursor getStock(){
        Cursor r=null;
        String dernierCamion="";

        //region tester si les produit existant sont de meme camion
        Cursor r6= Accueil.bd.read("select * from camion_actuel");
        while (r6.moveToNext()){
            dernierCamion=r6.getString(r6.getColumnIndex("code_camion"));

        }
        //endregion
        if (dernierCamion.equals(code)) {
            r = Accueil.bd.read("select * from produit p inner join product_erp pe on p.code_pr=pe.code_pr  ");
        }else {
            return r;
        }


        return r;
    }

    public void ImprimerStock(){
        ArrayList<String> pr=new ArrayList<String>();
        ArrayList<String> qt=new ArrayList<String>();


        Cursor r=getStock();
        String msg="------------------------------------- \n \n";
        msg=msg+"Entreprise: "+Login.user.nom_company+" \n \n";
        msg=msg+"Camion: "+code+" \n";
        msg=msg+"\n \n---------------------------------------------------- \n \n";
        msg=msg+"Produit                                   Quantite \n";
        msg=msg+"_________________________________________________________ \n";


        if (r != null){
            while (r.moveToNext()) {
                pr.clear();
                qt.clear();

                formatString(r.getString(r.getColumnIndex("nom_pr")), 38, pr);
                formatString(r.getString(r.getColumnIndex("stock")), 8, qt);


                int i = 0;
                int max=getStringMaxVal(pr, qt);
                while (i < max) {
                    if (pr.size() > i){
                        msg=msg+""+pr.get(i)+"";
                    }

                    if (qt.size() > i ){
                        msg=msg+"       "+qt.get(i)+"";

                    }
                    i++;
                    msg=msg+"\n";
                }

                msg=msg+"_________________________________________________________\n";

            }
        }

        ImprimerBon.msg=msg;
        ImprimerBon.FindBluetoothDevice(Accueil.me);
    }

    private String convertNull(String s){

        if(s == null){
            return "";

        }else {
            return s;
        }

    }

    private String convertNullDouble(String s){

        if(s == null){
            return "0";

        }else {
            return s;
        }

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

    public int getStringMaxVal(ArrayList<String> pr,ArrayList<String> qt){

        if(pr.size() > qt.size()){

            return pr.size();

        }else {
            return qt.size();
        }


    }

}
