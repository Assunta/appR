package com.oropallo.assunta.recipes.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oropallo.assunta.recipes.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Assunta on 23/11/2016.
 */

public class AdapterIngredienti extends RecyclerView.Adapter<AdapterIngredienti.IngredientiViewHolder>{
    private List<String> ingredienti;
    private Context context;

    public AdapterIngredienti() {
        ingredienti = new ArrayList<String>();
        for(int i=0; i<7; i++)
            ingredienti.add("ingrediente "+i);
    }

    @Override
    public AdapterIngredienti.IngredientiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context=parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.row_ingrediente, parent, false);
        IngredientiViewHolder ivh = new IngredientiViewHolder(v);
        return ivh;
    }

    @Override
    public void onBindViewHolder(AdapterIngredienti.IngredientiViewHolder holder, int position) {
        holder.nomeIngrediente.setText(ingredienti.get(position));
        holder.quantita.setText(position+" g");
    }

    @Override
    public int getItemCount() {
        return ingredienti.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class IngredientiViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView nomeIngrediente;
        TextView quantita;

        IngredientiViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(false);
            nomeIngrediente=(TextView) itemView.findViewById(R.id.item_name_ingrediente);
            quantita= (TextView) itemView.findViewById(R.id.quantita);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
