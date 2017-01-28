package com.oropallo.assunta.recipes.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oropallo.assunta.recipes.R;
import com.oropallo.assunta.recipes.RicettaActivity;
import com.oropallo.assunta.recipes.domain.Bookmark;
import com.oropallo.assunta.recipes.domain.DBManager;
import com.oropallo.assunta.recipes.domain.Ricetta;
import com.zhukic.sectionedrecyclerview.SectionedRecyclerAdapter;

import java.util.List;

/**
 * Created by Assunta on 28/01/2017.
 */

public class AdapterMainElenco extends SectionedRecyclerAdapter<AdapterMainElenco.SubheaderViewHolder,AdapterMainElenco.ItemViewHolder> {

    private List<Ricetta> list;


    // Constructor
    public AdapterMainElenco( List<Ricetta> list){
        this.list=list;
    }


    private Context mContext;
    public  class SubheaderViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewHeader;
        public SubheaderViewHolder(View itemView) {
            super(itemView);
            textViewHeader= (TextView) itemView.findViewById(R.id.textViewHeader);
        }
    }
    public  class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView textViewRicetta;
        private LinearLayout view;
        private ImageView bookmark;
        public ItemViewHolder(View itemView) {
            super(itemView);
            textViewRicetta= (TextView) itemView.findViewById(R.id.textViewNomeRicettaElenco);
            view= (LinearLayout) itemView.findViewById(R.id.linear_layout_item_elenco);
            bookmark=(ImageView) itemView.findViewById(R.id.imageViewBookmark_elenco);
        }
        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public boolean onPlaceSubheaderBetweenItems(int itemPosition, int nextItemPosition) {
        final Ricetta ricetta1 = list.get(itemPosition);
        final Ricetta ricetta2 = list.get(nextItemPosition);
        //The subheader will be placed between two neighboring items if the initial letter is different
        return ricetta1.getNome().toUpperCase().charAt(0)!=(ricetta2.getNome().toUpperCase().charAt(0));
    }

    @Override
    public SubheaderViewHolder onCreateSubheaderViewHolder(ViewGroup parent, int viewType) {
        mContext=parent.getContext();
        View v = LayoutInflater.from(mContext).inflate(R.layout.header_adapter_main_elenco, parent, false);
        SubheaderViewHolder ivh = new SubheaderViewHolder(v);
        return ivh;
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        mContext=parent.getContext();
        View v = LayoutInflater.from(mContext).inflate(R.layout.row_main_elenco, parent, false);
        ItemViewHolder ivh = new ItemViewHolder(v);

        return ivh;
    }

    @Override
    public void onBindSubheaderViewHolder(SubheaderViewHolder subheaderHolder, int nextItemPosition) {
        final Ricetta ricetta= list.get(nextItemPosition);
        subheaderHolder.textViewHeader.setText(ricetta.getNome().charAt(0)+"");
    }

    @Override
    public void onBindItemViewHolder(ItemViewHolder holder, int itemPosition) {
        final Ricetta ricetta= list.get(itemPosition);
        holder.textViewRicetta.setText(ricetta.getNome());
        if(DBManager.isBookmarked(ricetta.getId()))
            holder.bookmark.setVisibility(View.VISIBLE);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(mContext, RicettaActivity.class);
                intent.putExtra("Ricetta", ricetta.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
