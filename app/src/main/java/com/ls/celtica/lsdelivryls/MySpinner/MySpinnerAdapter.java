package com.ls.celtica.lsdelivryls.MySpinner;

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
 * Created by celtica on 30/11/18.
 */

public class MySpinnerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    AppCompatActivity c;
    SpinnerConfig sc;
    public  ArrayList<SpinnerItem> AdapterItems=new ArrayList<SpinnerItem>() ;

    public MySpinnerAdapter( AppCompatActivity c,SpinnerConfig sc) {
        this.c = c;
        this.sc=sc;
    }

    public  class SpinnerItemView extends RecyclerView.ViewHolder  {
        public TextView codebar;

        public TextView key;
        public TextView value;
        public SpinnerItemView(View v) {
            super(v);
            key=(TextView)v.findViewById(R.id.spinner_item_key);
            value=(TextView)v.findViewById(R.id.spinner_item_value);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_item,parent,false);

        MySpinnerAdapter.SpinnerItemView vh = new MySpinnerAdapter.SpinnerItemView(v);
        return vh;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        ((MySpinnerAdapter.SpinnerItemView)holder).key.setText(AdapterItems.get(position).key);
        ((MySpinnerAdapter.SpinnerItemView)holder).value.setText(AdapterItems.get(position).value);

        ((LinearLayout)((MySpinnerAdapter.SpinnerItemView)holder).key.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sc.onChooseItem(position,AdapterItems.get(position));
            }
        });


    }

    @Override
    public int getItemCount() {
        return AdapterItems.size();
    }
    public interface SpinnerConfig {
        void onChooseItem(int pos, SpinnerItem item);

    }
}

