package com.ls.celtica.lsdelivryls;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ls.celtica.lsdelivryls.Ventes.Bon;
import com.ls.celtica.lsdelivryls.Ventes.BonCommande;
import com.ls.celtica.lsdelivryls.Ventes.BonRetour;
import com.ls.celtica.lsdelivryls.Ventes.ProduitVendu;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Synchronisation extends AppCompatActivity {
    private LinearLayout div_admin;

    ProgressDialog progess;
    EditText ip,port,bd,user,mdp;
    String produit="",client="",num_bon;
    ArrayList<String> FactForDelet=new ArrayList<String>();
    ArrayList<String> CommForDelet=new ArrayList<String>();
    ArrayList<String> RetourForDelet=new ArrayList<String>();

    boolean code_err=false;
    int indexDelete=0;
    int indexDeleteComm=0;
    int indexDeleteRetour=0;
    int record_b1=1;
    int record_b2=1;
    int record=1;
    int record_reg=1;

    boolean sqlerr=false;

    public static boolean isOnsync=false;

    public static   boolean existeErrImportExport=false;

    boolean forcerImportation=false;

    boolean newCamion=false;

    public  static String ImportationErr="",code_clt_err="", camion_err="",vendeur_err="",bon1_err="",bon2_err="",prod_err="",clt_gps_err="",rglmt_err="",prod_erp_err="";

    ArrayList<String> clts=new ArrayList<String>();
    ArrayList<String> regs=new ArrayList<String>();
    int index=0;
    int index_reg=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synchronisation);
        if (savedInstanceState != null) {
            //region Revenir a au Deviceconfig ..
            Intent intent = new Intent(getApplicationContext(), DeviceConfig.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            //endregion
        } else {
            progess = new ProgressDialog(Synchronisation.this);

        /*
        Accueil.bdSQL=new MyBDJava("jdbc:jtds:sqlserver://" +ip.getText()+":"+port.getText()+";"
                                + "databaseName=" +bd.getText() + ";user=" +user.getText() + ";password="
                                + mdp.getText() + ";","net.sourceforge.jtds.jdbc.Driver", new MyBDJava.doAfterBeforeConnect() {


         */

            //region connecter au sql server ..
            ip=(EditText)findViewById(R.id.synch_ip);
            port=(EditText)findViewById(R.id.synch_port);
            user=(EditText)findViewById(R.id.synch__user);
            bd=(EditText)findViewById(R.id.synch_bd);
            mdp=(EditText)findViewById(R.id.synch_mdp);

            Cursor r5= Accueil.bd.read("select * from sqlconnect");
            while(r5.moveToNext()) {
                if(r5.getString(r5.getColumnIndex("ip")) != null) {
                    ip.setText(r5.getString(r5.getColumnIndex("ip")));
                    port.setText(r5.getString(r5.getColumnIndex("port")));
                    user.setText(r5.getString(r5.getColumnIndex("user")));
                    bd.setText(r5.getString(r5.getColumnIndex("bd_name")));
                    mdp.setText(r5.getString(r5.getColumnIndex("mdp")));
                }
            }

            try {
                Accueil.BDsql = new SqlServerBD(ip.getText().toString(),port.getText().toString(),bd.getText().toString(),user.getText().toString() , mdp.getText().toString(),"net.sourceforge.jtds.jdbc.Driver", new SqlServerBD.doAfterBeforeConnect() {

                    @Override
                    public void echec() {
                        Log.e("connnect", " Echoue");
                        progess.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.sync_conect_err),Toast.LENGTH_SHORT).show();
                                if (Login.user.mode.equals(getResources().getString(R.string.login_modeAdmin))) {

                                    ((LinearLayout)findViewById(R.id.DivSqlConnect)).setVisibility(View.VISIBLE);
                                    ((TextView)findViewById(R.id.synch_Butt)).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            //region afficher le div de connexion SQL
                                            try {
                                                Accueil.BDsql = new SqlServerBD(ip.getText().toString(),port.getText().toString(),
                                                        bd.getText().toString() ,user.getText().toString() , mdp.getText().toString() + ";","net.sourceforge.jtds.jdbc.Driver", new SqlServerBD.doAfterBeforeConnect() {
                                                    @Override
                                                    public void echec() {
                                                        Log.e("connnect", " Echoue");
                                                        progess.dismiss();
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.sync_conect_err),Toast.LENGTH_SHORT).show();

                                                            }
                                                        });

                                                    }

                                                    @Override
                                                    public void before() {
                                                        progess.setTitle(getResources().getString(R.string.sync_conect));
                                                        progess.setMessage(getResources().getString(R.string.sync_wait));
                                                        progess.show();


                                                    }

                                                    @Override
                                                    public void After() throws SQLException {
                                                        Log.e("connnect", " Reussite");
                                                        progess.dismiss();
                                                        ((LinearLayout)findViewById(R.id.sync_div_butts)).setVisibility(View.VISIBLE);
                                                        ((LinearLayout)findViewById(R.id.DivSqlConnect)).setVisibility(View.GONE);
                                                        Accueil.bd.write("update sqlconnect set ip='"+ip.getText()+"',port='"+port.getText()+"',bd_name='"+bd.getText()+"',mdp='"+mdp.getText()+"',user='"+user.getText()+"' where id='1'");

                                                    }
                                                });
                                            } catch (ClassNotFoundException e) {
                                                e.printStackTrace();
                                            } catch (IllegalAccessException e) {
                                                e.printStackTrace();
                                            } catch (InstantiationException e) {
                                                e.printStackTrace();
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }
                                            //endregion
                                        }
                                    });
                                }else {
                                    finish();
                                }
                            }
                        });


                    }

                    @Override
                    public void before() {
                        progess.setTitle(getResources().getString(R.string.sync_conect));
                        progess.setMessage(getResources().getString(R.string.sync_wait));
                        progess.show();


                    }

                    @Override
                    public void After() throws SQLException {
                        Log.e("connnect", " Reussite");
                        progess.dismiss();
                        ((LinearLayout)findViewById(R.id.sync_div_butts)).setVisibility(View.VISIBLE);
                        ((LinearLayout)findViewById(R.id.DivSqlConnect)).setVisibility(View.GONE);
                        Accueil.bd.write("update sqlconnect set ip='"+ip.getText()+"',port='"+port.getText()+"',bd_name='"+bd.getText()+"',mdp='"+mdp.getText()+"',user='"+user.getText()+"' where id='1'");

                    }
                });
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            //endregion

            //region imort/export mode Admin ..
            if (Login.user.mode.equals(getResources().getString(R.string.login_modeAdmin))){

                //region Importation ..
                ((TextView)findViewById(R.id.sync_import_butt)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        progess.setTitle(getResources().getString(R.string.sync_titre));
                        progess.setMessage(getResources().getString(R.string.sync_wait));
                        progess.show();

                        //region import les camions ..
                        Accueil.BDsql.read("SELECT * FROM  Truck INNER JOIN Warehouse ON Truck.Oid=Warehouse.Oid where GCRecord is null  ", new SqlServerBD.doAfterBeforeGettingData() {
                                @Override
                                public void echec(SQLException e) {
                                    existeErrImportExport=true;
                                    ImportationErr=ImportationErr+"Erreur de récupération des camions: "+e.getMessage()+" \n";

                                }

                                @Override
                                public void before() {

                                }

                                @Override
                                public void After(){
                                    boolean sqlerr=false;

                                    ResultSet r = Accueil.BDsql.r;
                                    Accueil.bd.write("delete from camion where '1'='1'");

                                    try {
                                        while (r.next()) {
                                            Log.e("CAMION", r.getString("id")+"");
                                            try {
                                                Accueil.bd.write("insert into camion(code_camion,nom_camion) values('" + r.getString("id") + "','"+ r.getString("name").replaceAll("'","`") +"')");
                                            }
                                            catch (android.database.SQLException e1){
                                                if(!sqlerr){
                                                    existeErrImportExport=true;
                                                    sqlerr=true;
                                                    ImportationErr=ImportationErr+"Erreur d insertion des camion, code camion en doublant: \n"+r.getString("id")+" /";
                                                }else {
                                                    ImportationErr=ImportationErr+" "+r.getString("id")+"/";
                                                }
                                            }

                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    sqlerr=false;
                                }
                            });

                        //endregion

                        //region import les vendeurs ..

                            Accueil.BDsql.read("select * from vendor where matricule_employé is not null ", new SqlServerBD.doAfterBeforeGettingData() {
                                @Override
                                public void echec(SQLException e) {
                                    Log.e("errSQ",e.getMessage());
                                    existeErrImportExport=true;
                                    ImportationErr=ImportationErr+"Erreur de récupération des vendeurs: "+e.getMessage()+"\n";
                                }

                                @Override
                                public void before() {

                                }

                                @Override
                                public void After(){
                                    boolean sqlerr=false;
                                    ResultSet r = Accueil.BDsql.r;
                                    Accueil.bd.write("delete from vendeur where '1'='1'");
                                    try {
                                        while (r.next()) {
                                            Log.e("vvv", r.getString("matricule_employé")+" ");
                                            try {
                                                Accueil.bd.write("insert into vendeur(code_vendeur,nom_vendeur) values('" + r.getString("matricule_employé") + "','"+ r.getString("fullName").replaceAll("'","`") +"')");
                                            }
                                            catch (android.database.SQLException e2){
                                                if(!sqlerr){
                                                    existeErrImportExport=true;
                                                    ImportationErr=ImportationErr+"erreur d insertion des vendeur, code vendeur en doublant: \n";
                                                }else {
                                                    ImportationErr=ImportationErr+r.getString("matricule_employé")+"/";
                                                }

                                            }
                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    sqlerr=false;

                                }
                            });

                        //endregion

                        //region importation des produit ERP

                            Accueil.BDsql.read("select distinct p.id,p.EAN13Code code_barre, p.name produit,p.salePrice_tax_included As default_price, ct.designation type_client, \n" +
                                    "  pct.priceBeforeTax prix_ht, pct.priceAfterTax prix_ttc  \n" +
                                    "                     from  Product p  \n" +
                                    "                     left join ProductCustomerType pct on p.oid = pct.product  \n" +
                                    "                    left join CustomerType ct on pct.customerType = ct.oid  \n" +
                                    "             ", new SqlServerBD.doAfterBeforeGettingData() {
                                @Override
                                public void echec(SQLException e) {
                                    e.printStackTrace();
                                    existeErrImportExport=true;
                                    ImportationErr=ImportationErr+"Erreur d importation des produits du serveur: \n"+e.getMessage()+" \n";
                                }

                                @Override
                                public void before() {

                                }

                                @Override
                                public void After()  {
                                   sqlerr=false;
                                    Accueil.bd.write("delete from product_erp");
                                    final ResultSet r=Accueil.BDsql.r;
                                    try {
                                        while (r.next()){
                                            //Log.e("erpp","prod : "+r.getString("code_barre")+" / code: "+r.getString("id")+" / "+r.getString("type_client"));
                                            if (!produit.equals(r.getString("id"))) {
                                                produit = r.getString("id");
                                                Accueil.bd.write2("insert into product_erp(code_pr,codebar,nom_pr,prix_v) values(?,'" + r.getString("code_barre") + "',?,?)", new MyBD.SqlPrepState() {
                                                    @Override
                                                    public void putValue(SQLiteStatement stmt) {
                                                        try {
                                                            stmt.bindString(1, convertNull(r.getString("id")));
                                                            stmt.bindString(2, convertNull(r.getString("produit")));
                                                            stmt.bindString(3, convertNullDouble(r.getString("default_price")));
                                                            stmt.execute();
                                                        } catch (SQLException e) {
                                                            e.printStackTrace();
                                                        }
                                                        catch (android.database.SQLException e1){
                                                            Synchronisation.existeErrImportExport=true;
                                                            if(!sqlerr){
                                                                sqlerr=true;
                                                                ImportationErr=ImportationErr+"Erreur d insertion des produit dans product_erp: \n"+ e1.getMessage()+" \n";

                                                            }else {
                                                                ImportationErr=ImportationErr+e1.getMessage()+" \n";
                                                            }

                                                        }

                                                    }
                                                });

                                                if (r.getString("type_client") != null) {
                                                    try {
                                                        Accueil.bd.write2("insert into produit_erp_prix_type (code_pr,type_clt,prix) values(?,?,'" + r.getString("prix_ttc") + "')", new MyBD.SqlPrepState() {
                                                            @Override
                                                            public void putValue(SQLiteStatement stmt) {

                                                                try {
                                                                    stmt.bindString(1, convertNull(r.getString("id")));
                                                                    stmt.bindString(2, convertNullDouble(r.getString("type_client")));
                                                                    stmt.execute();
                                                                }
                                                                catch (android.database.SQLException e1){

                                                                    Synchronisation.existeErrImportExport=true;
                                                                    try {
                                                                        ImportationErr=ImportationErr+" erreur d insertion de prix/type client: \n code_produit:"+r.getString("id")+" .."+ e1.getMessage()+" \n";
                                                                    } catch (SQLException e) {
                                                                        e.printStackTrace();
                                                                    } e1.getMessage();

                                                                } catch (SQLException e) {
                                                                    e.printStackTrace();
                                                                }


                                                            }
                                                        });

                                                    }catch (android.database.SQLException e2){
                                                        Log.e("sqll"," sqlite: "+e2.getMessage());
                                                    }


                                                }
                                            } else {
                                                try {
                                                    Accueil.bd.write2("insert into produit_erp_prix_type (code_pr,type_clt,prix) values(?,?,'" + r.getString("prix_ttc") + "')", new MyBD.SqlPrepState() {
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
                                                                try {
                                                                    ImportationErr=ImportationErr+"-erreur d insertion de prix/type client (produit erp): \n code_produit:"+r.getString("id")+" .."+ e1.getMessage()+" \n";
                                                                } catch (SQLException e) {
                                                                    e.printStackTrace();
                                                                } e1.getMessage();
                                                            }


                                                        }
                                                    });
                                                }catch (android.database.SQLException e1){
                                                    Log.e("sqll","sqlite: "+e1.getMessage());
                                                }

                                                //  Accueil.bd.write("insert into produit_prix_type (code_pr,type_clt,prix) values('" + r.getString("id") + "','" + r.getString("type_client").replaceAll("`","") + "','" + r.getString("prix_ttc") + "')");
                                            }
                                        }
                                    } catch (SQLException e1) {
                                        e1.printStackTrace();
                                    }


                                }
                            });


                        //endregion

                        afficherMsgErr();



                    }
                });
                //endregion

                //region exportation ..
                ((TextView)findViewById(R.id.sync_export_butt)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    if (!isOnsync) {
                        isOnsync=true;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progess.setTitle(getResources().getString(R.string.sync_titre));
                                progess.setMessage(getResources().getString(R.string.sync_wait));
                                progess.show();
                            }
                        });
                        exportationDonnée();
                    }

                    }
                });
                //endregion

            }
            //endregion

            //region import/export mode Vendeur ..
            else {

                //region importation ..
                ((TextView)findViewById(R.id.sync_import_butt)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(Login.user.vendeur.equals(getResources().getString(R.string.login_pseudoHint)) || Login.user.code_camion.equals(getResources().getString(R.string.login_user_descCamion))){
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.import_produit_noUser),Toast.LENGTH_SHORT).show();
                        }else {

                            //region Tester si on a pas des Bon de vente ou Règlement a synchroniser ..
                            Cursor r6 = Accueil.bd.read("select num_fact from facture where sync='0' ");
                            Cursor r7=Accueil.bd.read("select * from reglement where sync='0' order by id_rglmt desc limit 1");
                            if (r6.moveToNext() || r7.moveToNext()) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.sync_clt_BonExiste), Toast.LENGTH_LONG).show();
                            }
                            //endregion

                            //region faire l imortation ..
                            else {

                                //region importaion des client

                                //ProgressDialog ...
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progess.setTitle(getResources().getString(R.string.sync_titre));
                                        progess.setMessage(getResources().getString(R.string.sync_wait));
                                        progess.show();
                                    }
                                });

                                syncClientLocal();

                                //region Récupération les type de client   ..
                                    Accueil.BDsql.read("select distinct designation from customerType where designation != ''   ", new SqlServerBD.doAfterBeforeGettingData() {
                                        @Override
                                        public void echec(final SQLException e) {
                                            ImportationErr=ImportationErr+"Erreur d importation des types client: "+e.getMessage()+" \n";
                                        }

                                        @Override
                                        public void before() {

                                        }

                                        @Override
                                        public void After()   {
                                            final ResultSet r = Accueil.BDsql.r;
                                            Accueil.bd.write("delete from type_client where '1'='1' ");
                                            try {
                                                while (r.next()) {
                                                    Log.e("ttt",""+convertNull(r.getString("designation")));
                                                    Accueil.bd.write2("insert into type_client (type_clt) values(?)", new MyBD.SqlPrepState() {
                                                        @Override
                                                        public void putValue(SQLiteStatement stmt) {
                                                            try {
                                                                stmt.bindString(1,convertNull(r.getString("designation")));
                                                                stmt.execute();
                                                            } catch (SQLException e) {
                                                                e.printStackTrace();
                                                                Log.e("sss","Hnaa");
                                                            }


                                                        }
                                                    });

                                                }
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });

                                //endregion

                                //region récupération de tous les clients de logiciel desktop ..
                                Accueil.BDsql.read("SELECT  * from clients  where CODE_CLIENT != '' and (CODE_VENDEUR='"+Login.user.vendeurr.code+"' or CODE_VENDEUR is null)  ", new SqlServerBD.doAfterBeforeGettingData() {
                                            @Override
                                            public void echec(SQLException e) {
                                                existeErrImportExport=true;
                                               ImportationErr=ImportationErr+"Erreur de récupération des clients: "+e.getMessage()+" \n";
                                            }

                                            @Override
                                            public void before() {


                                            }

                                            @Override
                                            public void After()   {
                                                //Supprimer les client de la BD Local ..
                                                Accueil.bd.write("delete from client where '1'='1'");
                                                final ResultSet r = Accueil.BDsql.r;

                                                try {
                                                    while (r.next()) {
                                                        Log.e("cltt",r.getString("CLIENT")+"");

                                                        //remplissez La BD ..
                                                        Accueil.bd.write2("insert into client (code_clt,nom_clt,tél,adr_clt,latitude,longitude,solde,verser,sync,type,codebar) values(?,?,?,?,?,?,?,?,1,?,'"+r.getString("CODE_BARRE")+"')", new MyBD.SqlPrepState() {
                                                            @Override
                                                            public void putValue(SQLiteStatement stmt)  {
                                                                try {
                                                                    stmt.bindString(1,convertNull(r.getString("CODE_CLIENT")));
                                                                    stmt.bindString(2,convertNull(r.getString("CLIENT")));
                                                                    stmt.bindString(3,convertNull(r.getString("TEL")));
                                                                    stmt.bindString(4,convertNull(r.getString("ADRESSE")));
                                                                    stmt.bindString(5,convertNullDouble(r.getString("LATITUDE")));
                                                                    stmt.bindString(6,convertNullDouble(r.getString("LONGITUDE")));
                                                                    stmt.bindString(7,convertNullDouble(r.getString("SOLDE")));
                                                                    stmt.bindString(8,convertNullDouble(r.getString("VERSER")));
                                                                    // stmt.bindDouble(9,1);
                                                                    stmt.bindString(9,convertNull(r.getString("TYPE_CLIENT")));
                                                                    stmt.execute();


                                                                } catch (android.database.SQLException e) {
                                                                    existeErrImportExport=true;
                                                                    e.printStackTrace();
                                                                    Log.e("sqll","f sync .."+e.getMessage());
                                                                    code_err=true;
                                                                    try {
                                                                        ImportationErr=ImportationErr+r.getString("CODE_CLIENT")+" /";
                                                                    } catch (SQLException e1) {
                                                                        e1.printStackTrace();
                                                                    }

                                                                } catch (SQLException e) {
                                                                    e.printStackTrace();

                                                                }

                                                            }
                                                        });
                                                    }
                                                } catch (SQLException e) {
                                                    existeErrImportExport=true;
                                                     ImportationErr=ImportationErr+"-Erreur dans la récupération des clients: \n "+e.getMessage()+"\n";

                                                }

                                            }
                                        });
                                    //endregion

                                //endregion

                                //region importation des produit du camion
                                //tester si le stock est a jour dans le serveur ..

                                    Accueil.BDsql.read("select NUM_BON from bon1 where CODE_DEPOT='"+Login.user.code_camion+"' and BLOCAGE='F'    ", new SqlServerBD.doAfterBeforeGettingData() {
                                        @Override
                                        public void echec(SQLException e) {


                                        }

                                        @Override
                                        public void before() {

                                        }

                                        @Override
                                        public void After(){

                                            ResultSet r=Accueil.BDsql.r;

                                            final Camion c=new Camion(Login.user.code_camion);

                                            //region Stock non a jour ..
                                            try {
                                                if(r.next()){

                                                    Accueil.BDsql.es.execute(new Runnable() {
                                                        @Override
                                                        public void run() {

                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    progess.dismiss();
                                                                    AlertDialog.Builder mb = new AlertDialog.Builder(Synchronisation.this); //c est l activity non le context ..

                                                                    View v= getLayoutInflater().inflate(R.layout.confirm_box,null);
                                                                    TextView oui=(TextView) v.findViewById(R.id.confirm_oui);
                                                                    TextView non=(TextView) v.findViewById(R.id.confirm_non);
                                                                    TextView msg=(TextView) v.findViewById(R.id.confirm_msg);
                                                                    msg.setText(getResources().getString(R.string.sync_ERPstockNoSynch));

                                                                    mb.setView(v);
                                                                    final AlertDialog ad=mb.create();
                                                                    ad.show();
                                                                    ad.setCanceledOnTouchOutside(false); //ne pas fermer on click en dehors ..

                                                                    oui.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View view) {
                                                                            Accueil.bd.write("delete from produit where '1'='1' ");

                                                                            ad.dismiss();
                                                                            progess.setTitle(getResources().getString(R.string.sync_titre));
                                                                            progess.setMessage(getResources().getString(R.string.sync_wait));
                                                                            progess.show();
                                                                            c.getProducts(Synchronisation.this,progess);

                                                                            afficherMsgErr();
                                                                        }
                                                                    });

                                                                    non.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View view) {

                                                                            ad.dismiss();

                                                                            afficherMsgErr();

                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    });

                                                }
                                                //endregion

                                                //region Stock a jour Faire la récupération ..
                                                else {

                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            progess.setTitle(getResources().getString(R.string.sync_titre));
                                                            progess.setMessage(getResources().getString(R.string.sync_wait));
                                                            progess.show();
                                                            Accueil.bd.write("delete from produit where '1'='1' ");
                                                            c.getProducts(Synchronisation.this,progess);
                                                            afficherMsgErr();

                                                        }
                                                    });



                                                }
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }
                                            //endregion

                                        }
                                    });


                                //endregion


                            }
                            //endregion

                        }

                    }

                });
                //endregion

                //region exportation ..
                ((TextView)findViewById(R.id.sync_export_butt)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(!isOnsync) {
                            isOnsync=true;
                            //ProgressDialog ...
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progess.setTitle(getResources().getString(R.string.sync_titre));
                                    progess.setMessage(getResources().getString(R.string.sync_wait));
                                    progess.show();
                                }
                            });

                            exportationDonnée();
                        }

                    }
                });
                //endregion

            }
            //endregion


        }
    }

    private void syncClientLocal(){

        //region mettre a jours le GPS ..
        Cursor r8=Accueil.bd.read("select c.code_clt, c.latitude , c.longitude from client c inner join update_gps up on c.code_clt=up.code_clt");
        while (r8.moveToNext()){

                Accueil.BDsql.write("update clients set LATITUDE='"+r8.getString(r8.getColumnIndex("latitude"))+"',LONGITUDE='"+r8.getString(r8.getColumnIndex("longitude"))+"' where CODE_CLIENT='"+r8.getString(r8.getColumnIndex("code_clt"))+"' ", new SqlServerBD.doAfterBeforeGettingData() {
                    @Override
                    public void echec(SQLException e) {
                        if(!sqlerr){
                            sqlerr=true;
                            clt_gps_err="erreur lors de MAJ des cordonnées GPS du client: \n"+e.getMessage()+" \n";

                        }else {
                            clt_gps_err=clt_gps_err+e.getMessage()+" \n";
                        }

                    }

                    @Override
                    public void before() {

                    }

                    @Override
                    public void After() {

                    }
                });

        }
        //endregion


            Accueil.BDsql.read("select top 1 RECORDID from clients order by RECORDID desc", new SqlServerBD.doAfterBeforeGettingData() {
                @Override
                public void echec(SQLException e) {

                }

                @Override
                public void before() {

                }

                @Override
                public void After()  {
                    ResultSet r2=Accueil.BDsql.r;

                    try {
                        if(r2.next()){
                            record=Integer.parseInt(r2.getString("RECORDID"))+1;
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    Cursor r=Accueil.bd.read("select * from client where sync='0'");

                    while(r.moveToNext()){
                        Log.e("CLTT",r.getString(r.getColumnIndex("nom_clt")));
                        clts.add(r.getString(r.getColumnIndex("code_clt")));

                            Accueil.BDsql.write("insert into clients(RECORDID,CODE_CLIENT,CLIENT,LATITUDE,LONGITUDE,ADRESSE,TEL,TYPE_CLIENT,SOLDE,VERSER) values ('"+record+"','"+DeviceConfig.id_device+"_"+r.getString(r.getColumnIndex("code_clt"))+"','"+r.getString(r.getColumnIndex("nom_clt"))+"','"+r.getString(r.getColumnIndex("latitude"))+"','"+r.getString(r.getColumnIndex("longitude"))+"','"+r.getString(r.getColumnIndex("adr_clt"))+"','"+r.getString(r.getColumnIndex("tél"))+"','"+r.getString(r.getColumnIndex("type"))+"','0','0')", new SqlServerBD.doAfterBeforeGettingData() {
                                @Override
                                public void echec(final SQLException e) {

                                    if(code_clt_err.equals("")){
                                        code_clt_err="Erreur lors d insertion des nouveau client dans le serveur: \n  "+e.getMessage()+" \n";
                                    }else {
                                        code_clt_err=code_clt_err+""+e.getMessage()+" \n";
                                    }
                                }

                                @Override
                                public void before() {

                                }

                                @Override
                                public void After()  {
                                    Accueil.bd.write("update client set sync='1' where code_clt='"+clts.get(index)+"'");
                                    index++;

                                }
                            });
                            record++;

                    }
                    sqlerr=false;
                }
            });

    }



    private void exportationDonnée(){
        FactForDelet.clear();
        CommForDelet.clear();
        RetourForDelet.clear();
        indexDelete=0;
        indexDeleteComm=0;
        indexDeleteRetour=0;

        syncClientLocal();

        //region exportation des règlement ..

            Accueil.BDsql.read("select top 1 RECORDID from carnet_c order by RECORDID desc", new SqlServerBD.doAfterBeforeGettingData() {
                @Override
                public void echec(final SQLException e) {
                    existeErrImportExport=true;

                }

                @Override
                public void before() {

                }

                @Override
                public void After()   {

                    ResultSet r2 = Accueil.BDsql.r;

                    try {
                        if (r2.next()) {
                            record_reg = Integer.parseInt(r2.getString("RECORDID")) + 1;
                        }
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                    Cursor r = Accueil.bd.read("select r.*,cast(-strftime('%s','1900-01-01') + strftime('%s',strftime('%Y-%m-%d 00:00:00', `date_rglmt`)) as double) / (3600 * 24)  as date_reg ,strftime('%H:%M', `date_rglmt`) as heure from reglement r where sync='0'");

                    while (r.moveToNext()) {
                        Log.e("REGG", r.getString(r.getColumnIndex("id_rglmt")));
                        regs.add(r.getString(r.getColumnIndex("id_rglmt")));
                        String mode;
                        if (r.getString(r.getColumnIndex("mode")).equals(getResources().getString(R.string.rglmt_ModEspece))){
                            mode="ESPECE";
                        }else {
                            mode="CHEQUE";
                        }

                            Accueil.BDsql.write("insert into carnet_c(RECORDID,VERSEMENTS,SOURCE,CODE_CLIENT,DATE_CARNET,HEURE,MODE_RG,BLOCAGE,CODE_VENDEUR) values ('" + record_reg + "','" + r.getString(r.getColumnIndex("montant")) + "','SITUATION-CLIENT','" + r.getString(r.getColumnIndex("code_clt")) + "',CAST(" + r.getDouble(r.getColumnIndex("date_reg")) + " as datetime ),'" + r.getString(r.getColumnIndex("heure")) + "','"+mode+"','F','"+Login.user.vendeur+"')", new SqlServerBD.doAfterBeforeGettingData() {
                                @Override
                                public void echec(SQLException e) {
                                    existeErrImportExport=true;
                                    if(!sqlerr){
                                        sqlerr=true;
                                        rglmt_err="erreur d exportation des règlements: \n"+e.getMessage();
                                    }else {
                                        rglmt_err=rglmt_err+e.getMessage()+" \n";
                                    }
                                }

                                @Override
                                public void before() {
                                    //ProgressDialog ...


                                }

                                @Override
                                public void After()  {
                                    Accueil.bd.write("update  reglement set sync='1',etat='exporté' where id_rglmt='" + regs.get(index_reg) + "'");
                                    index_reg++;
                                }
                            });
                            record_reg++;

                    }

                }
            });

        //endregion

        //region exportation des Bon Commande / Ventes / Retour ..

        //region récupéré le dernier id de bon1 ..

            Accueil.BDsql.read("select top 1 RECORDID from bon1 order by RECORDID desc", new SqlServerBD.doAfterBeforeGettingData() {
                @Override
                public void echec(SQLException e) {

                    Accueil.BDsql.RequestErr=true;

                }

                @Override
                public void before() {

                }

                @Override
                public void After() {
                    ResultSet r = Accueil.BDsql.r;
                    try {
                        if (r.next()) {
                            record_b1 = record_b1 + Integer.parseInt(r.getString("RECORDID"));
                            Log.e("Recordd", "" + record_b1);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
            });
        //endregion

        //region récupéré le dernier id de bon2 et faire l exportation
            Accueil.BDsql.read("select top 1 RECORDID from bon2 order by RECORDID desc", new SqlServerBD.doAfterBeforeGettingData() {
                @Override
                public void echec(SQLException e) {

                    Accueil.BDsql.RequestErr=true;
                    progess.dismiss();
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.sync_err),Toast.LENGTH_SHORT).show();
                }

                @Override
                public void before() {

                }

                @Override
                public void After() {

                    ResultSet r2 = Accueil.BDsql.r;
                    try {
                        if (r2.next()) {
                            record_b2 = record_b2 + Integer.parseInt(r2.getString("RECORDID"));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    //region exportation des ventes ..
                    final Cursor r=Accueil.bd.read("select f.*,cast(-strftime('%s','1900-01-01') + strftime('%s',strftime('%Y-%m-%d 00:00:00', `date_fact`)) as double) / (3600 * 24)  as date_facture ,strftime('%H:%M', `date_fact`) as heure from facture f where sync='0'");
                    while (r.moveToNext()){
                        Accueil.BDsql.beginTRansact();
                        FactForDelet.add(r.getString(r.getColumnIndex("num_fact")));
                        //Log.e("bonn", date+" // "+heure);

                        final Bon b=new Bon(r.getString(r.getColumnIndex("num_fact"))+"",r.getDouble(r.getColumnIndex("date_facture"))+"",r.getString(r.getColumnIndex("heure"))+"",Double.parseDouble(r.getString(r.getColumnIndex("remise"))),r.getString(r.getColumnIndex("code_clt"))+"",r.getString(r.getColumnIndex("code_camion"))+"",r.getString(r.getColumnIndex("code_vendeur"))+"",r.getString(r.getColumnIndex("latitude"))+"",r.getString(r.getColumnIndex("longitude"))+"");
                        b.exportéBon(record_b1+"");

                        /*
                        //region inséré la facture Bon1
                        Accueil.BDsql.write("insert into bon1 (RECORDID,NUM_BON,BLOCAGE ,CODE_CLIENT , CODE_DEPOT,CODE_VENDEUR,LATITUDE,LONGITUDE,DATE_BON,HEURE,TYPE_BON,REMISE) values('" + record_b1 + "','" + (DeviceConfig.id_device + "_v_" + r.getString(r.getColumnIndex("num_fact"))) + "','F','" + r.getString(r.getColumnIndex("code_clt")) + "','" + r.getString(r.getColumnIndex("code_camion")) + "','" + r.getString(r.getColumnIndex("code_vendeur")) + "','" + r.getString(r.getColumnIndex("latitude")) + "','" + r.getString(r.getColumnIndex("longitude")) + "',CAST("+ r.getDouble(r.getColumnIndex("date_facture")) + " as datetime),'" + r.getString(r.getColumnIndex("heure")) + "','VENTE','"+r.getString(r.getColumnIndex("remise"))+"')", new SqlServerBD.doAfterBeforeGettingData() {
                            @Override
                            public void echec(SQLException e) {
                                Log.e("errBon","I m here");
                                Accueil.BDsql.transactErr=true;
                                existeErrImportExport=true;
                               bon1_err="Erreur d insertion dans bon1: "+e.getMessage()+" \n";

                            }

                            @Override
                            public void before() {

                            }

                            @Override
                            public void After() {

                                Log.e("bon_succ","Bon inséré");
                            }
                        });
                        //endregion
                        */
                        //region inséré les produit du facture dans Bon2 ..
                        Cursor r3=Accueil.bd.read("select pf.*,f.code_camion , p.codebar  from produit_facture pf inner join facture f on f.num_fact=pf.num_fact inner join product_erp p on pf.code_pr=p.code_pr where pf.num_fact='"+r.getString(r.getColumnIndex("num_fact"))+"'");
                        while (r3.moveToNext()){

                            ProduitVendu pr=new ProduitVendu(r3.getString(r3.getColumnIndex("code_pr"))+"",r3.getString(r3.getColumnIndex("codebar"))+"","",r3.getDouble(r3.getColumnIndex("quantity")),r3.getDouble(r3.getColumnIndex("prix_v")),r3.getDouble(r3.getColumnIndex("remise")));
                            pr.exportéProduitVendu(record_b2+"",b.code,b.code_camion);

                            /*
                            Accueil.BDsql.write("insert into bon2 (RECORDID,NUM_BON,CODE_BARRE,QTE,PV_HT,PRODUIT,CODE_DEPOT,BLOCAGE,REMISE) values('" + record_b2 + "','" + (DeviceConfig.id_device + "_v_" + r3.getString(r3.getColumnIndex("num_fact"))) + "','" + r3.getString(r3.getColumnIndex("codebar")) + "','" + r3.getString(r3.getColumnIndex("quantity")) + "','" + r3.getString(r3.getColumnIndex("prix_v")) + "','" + r3.getString(r3.getColumnIndex("code_pr")) + "','"+ r3.getString(r3.getColumnIndex("code_camion")) + "','F','"+r3.getString(r3.getColumnIndex("remise"))+"')", new SqlServerBD.doAfterBeforeGettingData() {
                                @Override
                                public void echec(SQLException e) {
                                    Log.e("errBon2","I m here");
                                    existeErrImportExport=true;
                                    Accueil.BDsql.transactErr=true;
                                    bon2_err="Erreur d insertion dans  bon2: "+e.getMessage()+" \n";

                                }

                                @Override
                                public void before() {

                                }

                                @Override
                                public void After() {
                                    Log.e("bon_succ_pr","Produit inséré");
                                }
                            });
                            */
                            record_b2++;

                        }
                        //endregion

                        Accueil.BDsql.commitTRansact();

                        //region supprimer les facture exporté en succée ..
                        Accueil.BDsql.es.execute(new Runnable() {
                            @Override
                            public void run() {
                            if(!Accueil.BDsql.transactErr){

                                /*
                                Bon b=new Bon(FactForDelet.get(indexDelete));
                                b.chnageEtatArchive("exporté");
                                //b.supprimerBon();
                                indexDelete++;
                                */

                                b.chnageEtatArchive("exporté");
                            }else {
                                Accueil.BDsql.transactErr=false;
                            }
                            }
                        });
                        //endregion

                        record_b1++;
                    }
                    //endregion

                    //region exporter les bons de commande ..
                        Cursor rr=Accueil.bd.read("select c.*,cast(-strftime('%s','1900-01-01') + strftime('%s',strftime('%Y-%m-%d 00:00:00', `date_comm`)) as double) / (3600 * 24)  as date_commm ,strftime('%H:%M', `date_comm`) as heure from bon_commande c where sync='0'");
                        while (rr.moveToNext()){
                            Accueil.BDsql.beginTRansact();
                            CommForDelet.add(rr.getString(rr.getColumnIndex("num_comm")));
                            Accueil.BDsql.write("insert into bon1 (RECORDID,NUM_BON,BLOCAGE ,CODE_CLIENT , CODE_DEPOT,CODE_VENDEUR,LATITUDE,LONGITUDE,DATE_BON,HEURE,TYPE_BON) values('" + record_b1 + "','" + (DeviceConfig.id_device + "_c_" + rr.getString(rr.getColumnIndex("num_comm"))) + "','F','" + rr.getString(rr.getColumnIndex("code_clt")) + "','" + rr.getString(rr.getColumnIndex("code_camion")) + "','" + rr.getString(rr.getColumnIndex("code_vendeur")) + "','" + rr.getString(rr.getColumnIndex("latitude")) + "','" + rr.getString(rr.getColumnIndex("longitude")) + "',CAST("+ rr.getDouble(rr.getColumnIndex("date_commm")) + " as datetime ),'" + rr.getString(rr.getColumnIndex("heure")) + "','COMMANDE')", new SqlServerBD.doAfterBeforeGettingData() {
                                @Override
                                public void echec(SQLException e) {
                                    Accueil.BDsql.transactErr=true;
                                    existeErrImportExport=true;
                                    bon1_err=bon1_err+"Erreur d insertion dans la table Bon1 (Commande): "+e.getMessage()+" \n";

                                }

                                @Override
                                public void before() {

                                }

                                @Override
                                public void After()  {



                                }
                            });

                            //region inséré les produit du commande dans Bon2 ..
                            Cursor r3=Accueil.bd.read("select pf.*,f.code_camion , p.codebar  from produit_commande pf inner join bon_commande f on f.num_comm=pf.num_comm inner join product_erp p on pf.code_pr=p.code_pr where pf.num_comm='"+rr.getString(rr.getColumnIndex("num_comm"))+"'");
                            while (r3.moveToNext()){

                                Accueil.BDsql.write("insert into bon2 (RECORDID,NUM_BON,CODE_BARRE,QTE,PV_HT,PRODUIT,CODE_DEPOT,BLOCAGE) values('" + record_b2 + "','" + (DeviceConfig.id_device + "_c_" + r3.getString(r3.getColumnIndex("num_comm"))) + "','" + r3.getString(r3.getColumnIndex("codebar")) + "','" + r3.getString(r3.getColumnIndex("quantity")) + "','" + r3.getString(r3.getColumnIndex("prix_v")) + "','" + r3.getString(r3.getColumnIndex("code_pr")) + "','"+ r3.getString(r3.getColumnIndex("code_camion")) + "','F')", new SqlServerBD.doAfterBeforeGettingData() {
                                    @Override
                                    public void echec(SQLException e) {
                                        Log.e("errBon2","I m here");
                                        existeErrImportExport=true;
                                        Accueil.BDsql.transactErr=true;
                                        bon2_err="Erreur d insertion dans  bon2 (Commande): "+e.getMessage()+" \n";

                                    }

                                    @Override
                                    public void before() {

                                    }

                                    @Override
                                    public void After() {
                                        Log.e("bon_succ_pr","Produit inséré");
                                    }
                                });
                                record_b2++;

                            }
                            //endregion

                            Accueil.BDsql.commitTRansact();

                            //region supprimer les bon exporté en succée ..
                            Accueil.BDsql.es.execute(new Runnable() {
                                @Override
                                public void run() {
                                    if(!Accueil.BDsql.transactErr){
                                        BonCommande b=new BonCommande(CommForDelet.get(indexDeleteComm));
                                        b.chnageEtatArchive("exporté");
                                        //b.supprimerBon();
                                        indexDeleteComm++;
                                    }else {
                                        Accueil.BDsql.transactErr=false;
                                    }
                                }
                            });
                            //endregion

                            record_b1++;


                        }

                        Accueil.BDsql.commitTRansact();

                    //endregion

                    //region exporter les bons de retour
                        rr=Accueil.bd.read("select br.*,cast(-strftime('%s','1900-01-01') + strftime('%s',strftime('%Y-%m-%d 00:00:00', `date_bon`)) as double) / (3600 * 24)  as date_bonn ,strftime('%H:%M', `date_bon`) as heure from bon_retour br where sync='0'");
                        while (rr.moveToNext()){
                            Accueil.BDsql.beginTRansact();
                            RetourForDelet.add(rr.getString(rr.getColumnIndex("num_bon")));
                            Accueil.BDsql.write("insert into bon1 (RECORDID,NUM_BON,BLOCAGE ,CODE_CLIENT , CODE_DEPOT,CODE_VENDEUR,LATITUDE,LONGITUDE,DATE_BON,HEURE,TYPE_BON) values('" + record_b1 + "','" + (DeviceConfig.id_device + "_r_" + rr.getString(rr.getColumnIndex("num_bon"))) + "','F','" + rr.getString(rr.getColumnIndex("code_clt")) + "','" + rr.getString(rr.getColumnIndex("code_camion")) + "','" + rr.getString(rr.getColumnIndex("code_vendeur")) + "','" + rr.getString(rr.getColumnIndex("latitude")) + "','" + rr.getString(rr.getColumnIndex("longitude")) + "',CAST("+ rr.getDouble(rr.getColumnIndex("date_bonn")) + " as datetime),'" + rr.getString(rr.getColumnIndex("heure")) + "','RETOUR')", new SqlServerBD.doAfterBeforeGettingData() {
                                @Override
                                public void echec(SQLException e) {
                                    Accueil.BDsql.transactErr=true;
                                    existeErrImportExport=true;
                                    bon1_err=bon1_err+"Erreur d insertion dans la table Bon1 (Retour): "+e.getMessage()+" \n";

                                }

                                @Override
                                public void before() {

                                }

                                @Override
                                public void After() {



                                }
                            });

                            //region inséré les produit du retour dans Bon2 ..
                            Cursor r3=Accueil.bd.read("select pf.*,f.code_camion , p.codebar  from produit_retour pf inner join bon_retour f on f.num_bon=pf.num_bon inner join product_erp p on pf.code_pr=p.code_pr where pf.num_bon='"+rr.getString(rr.getColumnIndex("num_bon"))+"'");
                            while (r3.moveToNext()){

                                Accueil.BDsql.write("insert into bon2 (RECORDID,NUM_BON,CODE_BARRE,QTE,PV_HT,PRODUIT,CODE_DEPOT,BLOCAGE) values('" + record_b2 + "','" + (DeviceConfig.id_device + "_r_" + r3.getString(r3.getColumnIndex("num_bon"))) + "','" + r3.getString(r3.getColumnIndex("codebar")) + "','" + r3.getString(r3.getColumnIndex("quantity")) + "','" + r3.getString(r3.getColumnIndex("prix_v")) + "','" + r3.getString(r3.getColumnIndex("code_pr")) + "','"+ r3.getString(r3.getColumnIndex("code_camion")) + "','F')", new SqlServerBD.doAfterBeforeGettingData() {
                                    @Override
                                    public void echec(SQLException e) {
                                        existeErrImportExport=true;
                                        Accueil.BDsql.transactErr=true;
                                        bon2_err="Erreur d insertion dans  bon2 (Retour): "+e.getMessage()+" \n";

                                    }

                                    @Override
                                    public void before() {

                                    }

                                    @Override
                                    public void After() {
                                        Log.e("bon_succ_pr","Produit inséré");
                                    }
                                });
                                record_b2++;

                            }
                            //endregion

                            Accueil.BDsql.commitTRansact();

                            //region supprimer les bon exporté en succée ..
                            Accueil.BDsql.es.execute(new Runnable() {
                                @Override
                                public void run() {
                                    if(!Accueil.BDsql.transactErr){
                                        BonRetour b=new BonRetour(RetourForDelet.get(indexDeleteRetour));
                                        b.chnageEtatArchive("exporté");
                                        //b.supprimerBon();
                                        indexDeleteRetour++;
                                    }else {
                                        Accueil.BDsql.transactErr=false;
                                    }
                                }
                            });
                            //endregion

                            record_b1++;


                        }

                        Accueil.BDsql.commitTRansact();


                    //endregion

                    afficherMsgErr();

                    //region supprimé l archive ..
                    try {
                        Accueil.bd.write("delete  from facture where julianday('now') - julianday('date_fact') > 60 and sync='1'  ");
                        Accueil.bd.write("delete  from produit_facture where num_fact not in(select num_fact from facture)  ");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    //endregion

                }
            });
        //endregion

        //endregion

    }

    private void afficherMsgErr(){

        Accueil.BDsql.es.execute(new Runnable() {
            @Override
            public void run() {

                Accueil.BDsql.es.execute(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progess.dismiss();
                                sqlerr=false;
                                isOnsync=false;


                                if(existeErrImportExport) {
                                    AlertDialog.Builder mb = new AlertDialog.Builder(Synchronisation.this); //c est l activity non le context ..

                                    View v = getLayoutInflater().inflate(R.layout.div_aff_msg_err, null);
                                    TextView msg = (TextView) v.findViewById(R.id.err_msg);
                                    Button ok = (Button) v.findViewById(R.id.ok);

                                    mb.setView(v);
                                    final AlertDialog add = mb.create();

                                    try {
                                        add.show();
                                        add.setCanceledOnTouchOutside(false); //ne pas fermer on click en dehors ..
                                        add.setCancelable(false); //désactiver le button de retour ..
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                    if (!code_clt_err.equals("")) {
                                        msg.setText("Erreur d insertion des client , code client en doublant: \n" + code_clt_err + " \n ");

                                    }

                                    if(!ImportationErr.equals("")){
                                        msg.setText(msg.getText()+"\n"+ImportationErr);
                                    }

                                    if (!prod_err.equals("")) {
                                        msg.setText(msg.getText() + "erreur d importation des produit: \n" + prod_err + " \n");
                                    }

                                    if (!rglmt_err.equals("")) {
                                        msg.setText(msg.getText() + "erreur d exportation des règlement: \n" + rglmt_err + " \n");
                                    }

                                    if(!camion_err.equals("")){
                                        msg.setText(msg.getText() + "erreur d importaion des camion: \n" + camion_err + " \n");

                                    }
                                    if(!vendeur_err.equals("")){
                                        msg.setText(msg.getText() + "erreur d importation des vendeurs: \n" + vendeur_err + " \n");

                                    }
                                    if(!bon1_err.equals("")){
                                        msg.setText(msg.getText() + "erreur d exportation des bons de vente: \n" + bon1_err + " \n");

                                    }
                                    if(!bon2_err.equals("")){
                                        msg.setText(msg.getText() + "erreur d exportation des produit associé a des bons de ventes: \n" + bon1_err + " \n");

                                    }
                                    if(!prod_erp_err.equals("")){
                                        msg.setText(msg.getText() + "erreur d importation des produit du serveur: \n" + prod_erp_err + " \n");

                                    }
                                    ok.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Log.e("ttt", "ana");
                                            add.dismiss();
                                        }
                                    });
                                    camion_err = "";
                                    vendeur_err = "";
                                    bon1_err = "";
                                    bon2_err = "";
                                    prod_err = "";
                                    clt_gps_err = "";
                                    rglmt_err = "";
                                    code_clt_err="";
                                    prod_erp_err="";
                                    ImportationErr="";
                                }else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.sync_succ),Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                existeErrImportExport=false;

                            }
                        });
                    }
                });
            }
        });
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

    public static void connecterSQL(AppCompatActivity c, final SqlServerBD.doAfterBeforeConnect d){
        //region connecter au sql server ..
        try {
            Accueil.BDsql = new SqlServerBD("","","","","","net.sourceforge.jtds.jdbc.Driver", new SqlServerBD.doAfterBeforeConnect() {
                @Override
                public void echec() {
                    d.echec();
                }

                @Override
                public void before() {
                    d.before();

                }

                @Override
                public void After() throws SQLException {
                    d.After();
                }
            });
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //endregion
    }

}
