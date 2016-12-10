package com.oropallo.assunta.recipes.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.oropallo.assunta.recipes.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Assunta on 24/11/2016.
 */

public class AdapterProcedimento extends RecyclerView.Adapter<AdapterProcedimento.ProcedimentoViewHolder>{
    private List<String> procedimento;
    private Context context;

    public AdapterProcedimento(ArrayList<String> procedimento) {
        this.procedimento=procedimento;
    }

    @Override
    public AdapterProcedimento.ProcedimentoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context=parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.row_procedimento, parent, false);
        AdapterProcedimento.ProcedimentoViewHolder ivh = new AdapterProcedimento.ProcedimentoViewHolder(v);
        return ivh;
    }

    @Override
    public void onBindViewHolder(AdapterProcedimento.ProcedimentoViewHolder holder, int position) {
        holder.procedimento_descrizione.setText(procedimento.get(position));
        holder.image_procedimento.setImageDrawable(null);
        // holder.nomeIngrediente.setText(ingredienti.get(position));
        //holder.quantita.setText(position+" g");
    }

    @Override
    public int getItemCount() {
        return procedimento.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class ProcedimentoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView procedimento_descrizione;
        ImageView image_procedimento;

        ProcedimentoViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(false);
            procedimento_descrizione=(TextView) itemView.findViewById(R.id.item_procedimento);
            image_procedimento=(ImageView) itemView.findViewById(R.id.imageView_item_procediemnto);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
