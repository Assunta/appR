package com.oropallo.assunta.recipes.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.oropallo.assunta.recipes.domain.IngredienteRicetta;
import com.oropallo.assunta.recipes.R;


import java.util.List;

/**
 * Created by Assunta on 26/11/2016.
 */

public class AdapterAddIngrediente extends RecyclerView.Adapter<AdapterAddIngrediente.AddIngredientiViewHolder> {
    private Context context;
    private List<IngredienteRicetta> ingredienti;

    public AdapterAddIngrediente(List<IngredienteRicetta> ingredienti) {
        this.ingredienti = ingredienti;
    }

    @Override
    public AdapterAddIngrediente.AddIngredientiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context=parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.row_add_ingrediente, parent, false);
        AdapterAddIngrediente.AddIngredientiViewHolder ivh = new AdapterAddIngrediente.AddIngredientiViewHolder(v);
        return ivh;
    }

    @Override
    public void onBindViewHolder(AddIngredientiViewHolder holder, final int position) {
        holder.nomeIngrediente.setText(ingredienti.get(position).getNome());
        holder.quantita.setText( ingredienti.get(position).getQuantita()+"");
        holder.unita.setText(ingredienti.get(position).getUnita());
        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ingredienti.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,ingredienti.size());
            }
        });
    }


    @Override
    public int getItemCount() {
        return ingredienti.size();
    }

    public class AddIngredientiViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView nomeIngrediente;
        ImageButton del;
        TextView quantita;
        TextView unita;

        AddIngredientiViewHolder(View itemView) {
            super(itemView);
            nomeIngrediente= (TextView) itemView.findViewById(R.id.textView_add_ingrediente);
            quantita=(TextView)itemView.findViewById(R.id.add_quantita);
            unita=(TextView) itemView.findViewById(R.id.add_unita_textView);
            del= (ImageButton) itemView.findViewById(R.id.imageButton_delete);
            itemView.setClickable(false);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
