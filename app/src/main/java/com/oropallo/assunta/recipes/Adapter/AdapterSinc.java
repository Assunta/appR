package com.oropallo.assunta.recipes.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.oropallo.assunta.recipes.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Assunta on 29/01/2017.
 */

public class AdapterSinc extends RecyclerView.Adapter<AdapterSinc.SincViewHolder> {
    private Context mContext;
    private List<Integer> items;

    public AdapterSinc(){
        items= new ArrayList<Integer>();
        items.add(1);
        items.add(2);
    }

    @Override
    public SincViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext=parent.getContext();
        View v = LayoutInflater.from(mContext).inflate(R.layout.row_sinc_activity, parent, false);
        AdapterSinc.SincViewHolder ivh = new AdapterSinc.SincViewHolder(v);
        return ivh;
    }

    @Override
    public void onBindViewHolder(SincViewHolder holder, int position) {
        int type= items.get(position);
        switch (type){
            case 1:
                holder.title.setText("IMPORTA");
                holder.descr.setText("Permette di importare le ricette sincronizzate con il tuo account google drive");
                holder.button.setText("IMPORTA");
                //holder.image.setImageResource(stat_sys_download);
                break;
            case 2:
                holder.title.setText("ESPORT");
                holder.descr.setText("Permette di esportare le ricette per sincronizzate con il tuo account google drive");
                holder.button.setText("ESPORTA");
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class SincViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private TextView descr;
        private Button button;
        private ImageView image;

        public SincViewHolder(View itemView) {
            super(itemView);
            title=(TextView) itemView.findViewById(R.id.textView_sinc_title);
            descr=(TextView) itemView.findViewById(R.id.textView_sinc_descr);
            button=(Button) itemView.findViewById(R.id.button_sinc);
            image=(ImageView) itemView.findViewById(R.id.imageView_sinc);
        }
    }
}
