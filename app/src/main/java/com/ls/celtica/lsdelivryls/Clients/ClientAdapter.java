package com.ls.celtica.lsdelivryls.Clients;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ls.celtica.lsdelivryls.R;

import java.util.ArrayList;

/**
 * Created by celtica on 15/08/18.
 */

public class ClientAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    AppCompatActivity c;
    public static ArrayList<Client> clients=new ArrayList<Client>();
    public static int itemSelected;

    public ClientAdapter(  AppCompatActivity c) {
        this.c = c;

    }

    public static class CltView extends RecyclerView.ViewHolder  {
        public TextView codebar;
        public TextView nom;
        //public TextView prix;
        public CltView(View v) {
            super(v);
            codebar=(TextView)v.findViewById(R.id.codebar_clt);
            nom=(TextView)v.findViewById(R.id.nom_clt);
            //prix=(TextView)v.findViewById(R.id.div_pr_prix);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.div_clt,parent,false);

        CltView vh = new CltView(v);
        return vh;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        // Afficher les information d un client lors de click au dessus ..
        ((LinearLayout)(((CltView)holder).codebar).getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //l index de div qu on a clicker dans le arraylist ..
                itemSelected=position;
                c.startActivity(new Intent(c,UnClient.class));
                //Log.e("index",""+position);
               
            }
        });

        ((CltView)holder).codebar.setText(c.getResources().getString(R.string.div_clt_code)+clients.get(position).codebar);
        ((CltView)holder).nom.setText(clients.get(position).nom);

    }

    @Override
    public int getItemCount() {
        return clients.size();
    }
}
