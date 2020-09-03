package com.redhead.y14.womandictionary;

import android.support.p003v7.widget.RecyclerView.Adapter;
import android.support.p003v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;

public class CustomAdapter extends Adapter<MyViewHolder> {
    Boolean check = Boolean.valueOf(false);
    private ArrayList<DictObjectModel> dataSet;

    public static class MyViewHolder extends ViewHolder {
        RelativeLayout expandable;
        TextView meaning;
        TextView word;

        public MyViewHolder(View view) {
            super(view);
            this.expandable = (RelativeLayout) view.findViewById(C0351R.C0353id.expandableLayout);
            this.word = (TextView) view.findViewById(C0351R.C0353id.wordtext);
            this.meaning = (TextView) view.findViewById(C0351R.C0353id.meaningtext);
        }
    }

    public CustomAdapter(ArrayList<DictObjectModel> arrayList) {
        this.dataSet = arrayList;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(C0351R.layout.card_view_row, viewGroup, false);
        final MyViewHolder myViewHolder = new MyViewHolder(inflate);
        inflate.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (!CustomAdapter.this.check.booleanValue()) {
                    myViewHolder.expandable.animate().alpha(0.0f).setDuration(500);
                    myViewHolder.expandable.setVisibility(8);
                    CustomAdapter.this.check = Boolean.valueOf(true);
                    return;
                }
                myViewHolder.expandable.setVisibility(0);
                myViewHolder.expandable.animate().alpha(1.0f).setDuration(500);
                CustomAdapter.this.check = Boolean.valueOf(false);
            }
        });
        return myViewHolder;
    }

    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        TextView textView = myViewHolder.word;
        TextView textView2 = myViewHolder.meaning;
        textView.setText(((DictObjectModel) this.dataSet.get(i)).getWord());
        textView2.setText(((DictObjectModel) this.dataSet.get(i)).getMeaning());
    }

    public int getItemCount() {
        return this.dataSet.size();
    }
}
