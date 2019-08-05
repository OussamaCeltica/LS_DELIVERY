package com.ls.celtica.lsdelivryls;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.location.Location;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ls.celtica.lsdelivryls.Ventes.Bon;
import com.ls.celtica.lsdelivryls.Ventes.BonCommande;
import com.ls.celtica.lsdelivryls.Ventes.BonRetour;
import com.ls.celtica.lsdelivryls.Ventes.FaireVente;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by celtica on 03/10/18.
 */

public class User {
   public ServeurInfos serveur;
   public String code_camion,type_device,vendeur,mode,nom_company,nom_printer;
   public boolean isEdit;//prix is editable ..
    public boolean isRemise;
   public Camion camion=null;
   public Vendeur vendeurr=null;
   public Location pos;


    public User(String code_camion,String vendeur , String type_device,String mode) {
        this.code_camion = code_camion;
        this.type_device = getTypeDevice();
        getPrinterName();
        this.vendeur=vendeur;
        this.mode=mode;
        isEdit=isEditable();
        isRemise=isRemiseable();
        this.serveur=new ServeurInfos();
        if(!vendeur.equals(Login.me.getResources().getString(R.string.login_pseudoHint))){
            this.vendeurr=new Vendeur(vendeur);
        }
        if(!code_camion.equals(Login.me.getResources().getString(R.string.login_user_descCamion))){
            this.camion=new Camion(code_camion);
        }

    }

    public String getTypeDevice(){
        Cursor r=Accueil.bd.read("select * from admin");
        if (r.moveToNext()){
            nom_company=r.getString(r.getColumnIndex("company_name"))+"";
            return r.getString(r.getColumnIndex("type_device"));
        }
        return "";
    }

    public String getPrinterName(){
        Cursor r=Accueil.bd.read("select * from admin");
        if (r.moveToNext()){
            nom_printer=r.getString(r.getColumnIndex("nom_printer"))+"";
            return r.getString(r.getColumnIndex("nom_printer"));
        }
        return "";
    }

    public void formaterBD(){

        Accueil.bd.write("update reglement set sync='1' , etat='supprimé' where sync='0'");
        Accueil.bd.write("update bon_retour set sync='1' , etat='supprimé' where sync='0'");
        Accueil.bd.write("update bon_commande set sync='1' , etat='supprimé' where sync='0'");
        Accueil.bd.write("update facture set sync='1' , etat='supprimé' where sync='0'");

        Accueil.bd.write("delete from client");
        Accueil.bd.write("delete from camion");
        Accueil.bd.write("delete from produit");
        Accueil.bd.write("delete from product_erp");
        Accueil.bd.write("delete from vendeur");


    }

    public boolean isEditable(){
        Cursor r= Accueil.bd.read("select * from admin");
        if(r.moveToNext()){
            if(r.getString(r.getColumnIndex("isEditPrix")).equals("1")){
                return true;
            }
        }
        return false;

    }

    public void changeEditPricePermission(Boolean edit){
        if(edit){
            Accueil.bd.write("update admin set isEditPrix='1' ");
        }else {
            Accueil.bd.write("update admin set isEditPrix='0' ");
        }

        isEdit=edit;
    }

    public boolean isRemiseable(){
        Cursor r= Accueil.bd.read("select * from admin");
        if(r.moveToNext()){
            if(r.getString(r.getColumnIndex("isRemise")).equals("1")){
                return true;
            }
        }
        return false;

    }



    public void changeRemisePermission(Boolean isRemise){
        if(isRemise){
            Accueil.bd.write("update admin set isRemise='1' ");
        }else {
            Accueil.bd.write("update admin set isRemise='0' ");
        }
        this.isRemise=isRemise;
    }

    public static String formatPrix(double prix){
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);
        formatter.format(" %,.2f", prix);

        return sb.toString().replaceAll(","," ").replaceFirst(" ","");
    }

    public static String formatQt(double qt){
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);
        formatter.format(" %.2f", qt);

        return sb.toString().replaceAll(","," ").replaceFirst(" ","");
    }


    public static String convertNull(String s){

        if(s == null){
            return "";

        }else {
            return s;
        }

    }

    public static String convertNullDouble(String s){

        if(s == null){
            return "0";

        }else {
            return s;
        }

    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public  void openScannerCodeBarre(final AppCompatActivity c, final OnScanListener scan){

        if (Login.user.type_device.equals(Login.me.getResources().getString(R.string.login_device_type2))) {
            c.startActivityForResult(new Intent(c,CodeBarScanner.class),2);
        } else {
            AlertDialog.Builder mb = new AlertDialog.Builder(c); //c est l activity non le context ..

            View v= c.getLayoutInflater().inflate(R.layout.div_scanner,null);
            final EditText code=(EditText)v.findViewById(R.id.sacnner_code);
            code.requestFocus();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                code.setShowSoftInputOnFocus(false);
            }

            mb.setView(v);
            final AlertDialog ad=mb.create();
            ad.show();
            ad.setCanceledOnTouchOutside(false); //ne pas fermer on click en dehors ..


            code.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                    scan.OnScan(s.subSequence(0,s.length()-1).toString());
                    ad.dismiss();
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

        }
    }

    public void changeCompanyName(final String name){

        Accueil.bd.write2("update admin set company_name=?", new MyBD.SqlPrepState() {
            @Override
            public void putValue(SQLiteStatement stmt) {
                try {
                    stmt.bindString(1,name);
                    stmt.execute();
                    nom_company=name;
                }catch (android.database.SQLException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void changePrinterName(final String name){

        Accueil.bd.write2("update admin set nom_printer=?", new MyBD.SqlPrepState() {
            @Override
            public void putValue(SQLiteStatement stmt) {
                try {
                    stmt.bindString(1,name);
                    stmt.execute();
                    nom_printer=name;
                }catch (android.database.SQLException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public interface OnScanListener{
        void OnScan(String code);
    }
}
