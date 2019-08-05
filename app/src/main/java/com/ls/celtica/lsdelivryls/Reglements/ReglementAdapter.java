package com.ls.celtica.lsdelivryls.Reglements;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ls.celtica.lsdelivryls.R;

import com.ls.celtica.lsdelivryls.Ventes.PanierAdapter;

import java.util.ArrayList;

/**
 * Created by celtica on 15/08/18.
 */

public class ReglementAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    AppCompatActivity c;
    public  static ArrayList<Reglement> reglements=new ArrayList<Reglement>() ;

    public ReglementAdapter(  AppCompatActivity c) {
        this.c = c;
        this.reglements = reglements;
    }

    public static class ReglementView extends RecyclerView.ViewHolder  {
        public TextView nom;
        public TextView montant;
        public TextView date;
        public TextView mode;

        public ReglementView(View v) {
            super(v);
            nom=(TextView)v.findViewById(R.id.reg_nomClt);
            montant=(TextView)v.findViewById(R.id.reg_montant);
            date=(TextView)v.findViewById(R.id.reg_date);
            mode=(TextView)v.findViewById(R.id.reg_mode);



        }
    }

    public static class ReglementExportView extends RecyclerView.ViewHolder  {
        public TextView nom;
        public TextView montant;
        public TextView date;
        public TextView mode;

        public ReglementExportView(View v) {
            super(v);
            nom=(TextView)v.findViewById(R.id.reg_nomClt_export);
            montant=(TextView)v.findViewById(R.id.reg_montant_export);
            date=(TextView)v.findViewById(R.id.reg_date_export);
            mode=(TextView)v.findViewById(R.id.reg_mode_export);



        }
    }

    public static class ReglementSuppView extends RecyclerView.ViewHolder  {
        public TextView nom;
        public TextView montant;
        public TextView date;
        public TextView mode;

        public ReglementSuppView(View v) {
            super(v);
            nom=(TextView)v.findViewById(R.id.reg_nomClt_supp);
            montant=(TextView)v.findViewById(R.id.reg_montant_supp);
            date=(TextView)v.findViewById(R.id.reg_date_supp);
            mode=(TextView)v.findViewById(R.id.reg_mode_supp);



        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case 1:{
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.div_reglem,parent,false);

                ReglementView vh = new  ReglementView(v);
                return vh;
            }
            case 2:{
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.div_reg_export,parent,false);

                ReglementExportView vh = new ReglementExportView(v);
                return vh;
            }
            default: {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.div_reg_export,parent,false);

                ReglementExportView vh = new ReglementExportView(v);
                return vh;
            }

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if(reglements.get(position) instanceof ReglementExport){
            ((ReglementExportView)holder).nom.setText(reglements.get(position).nom_client);
            ((ReglementExportView)holder).montant.setText(PanierAdapter.formatPrix(reglements.get(position).montant)+" DA");
            ((ReglementExportView)holder).date.setText(reglements.get(position).date);
            ((ReglementExportView)holder).mode.setText(reglements.get(position).mode);

        }else if(reglements.get(position) instanceof ReglementSupprime){
            ((ReglementSuppView)holder).nom.setText(reglements.get(position).nom_client);
            ((ReglementSuppView)holder).montant.setText(PanierAdapter.formatPrix(reglements.get(position).montant)+" DA");
            ((ReglementSuppView)holder).date.setText(reglements.get(position).date);
            ((ReglementSuppView)holder).mode.setText(reglements.get(position).mode);

        }else {
            ((ReglementView)holder).nom.setText(reglements.get(position).nom_client);
            ((ReglementView)holder).montant.setText(PanierAdapter.formatPrix(reglements.get(position).montant)+" DA");
            ((ReglementView)holder).date.setText(reglements.get(position).date);
            ((ReglementView)holder).mode.setText(reglements.get(position).mode);

        }



    }

    @Override
    public int getItemViewType(int position) {
        if(reglements.get(position) instanceof ReglementExport){
            return 2;
        }else if(reglements.get(position) instanceof ReglementExport){
            //je veut dire supprimé ..
            return 2;
        }else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return reglements.size();
    }
}
