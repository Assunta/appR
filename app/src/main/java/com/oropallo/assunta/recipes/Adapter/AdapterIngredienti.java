package com.oropallo.assunta.recipes.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oropallo.assunta.recipes.R;
import com.oropallo.assunta.recipes.domain.IngredienteRicetta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Assunta on 23/11/2016.
 */

public class AdapterIngredienti extends RecyclerView.Adapter<AdapterIngredienti.IngredientiViewHolder>{
    private List<IngredienteRicetta> ingredienti;
    private Context context;

    public AdapterIngredienti(List<IngredienteRicetta> ingredientis) {
       ingredienti=ingredientis;
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
        holder.nomeIngrediente.setText(ingredienti.get(position).getNome());
        double qu= ingredienti.get(position).getQuantita();
        //per evitare di visualizzare decimali inutili:
        String formattedQuantita= formatQuantita(qu);
        if(qu==0)
            holder.quantita.setText(ingredienti.get(position).getUnita());
        else
            holder.quantita.setText(formattedQuantita+ingredienti.get(position).getUnita());
    }

    @Override
    public int getItemCount() {
        return ingredienti.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private String formatQuantita(double q){
        /*if(q==(long)q)
            return String.format("%.d",(long) q);
        else
            return String.format("%s",q);*/
        DecimalFormat df= new DecimalFormat("0.##");
        return df.format(q);
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
