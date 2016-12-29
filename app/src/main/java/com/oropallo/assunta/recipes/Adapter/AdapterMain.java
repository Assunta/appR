package com.oropallo.assunta.recipes.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.oropallo.assunta.recipes.R;
import com.oropallo.assunta.recipes.RicettaActivity;
import com.oropallo.assunta.recipes.domain.DBManager;
import com.oropallo.assunta.recipes.domain.Ricetta;
import com.ramotion.foldingcell.FoldingCell;

import org.w3c.dom.Text;

import java.util.List;

import cn.refactor.library.ShapeImageView;

import static java.security.AccessController.getContext;

/**
 * Created by Assunta on 06/12/2016.
 */

public class AdapterMain  extends RecyclerView.Adapter<AdapterMain.MainViewHolder> {
    private Context mContext;

   private  List<Ricetta> list;

    // Constructor
    public AdapterMain( List<Ricetta> list){

        this.list=list;
    }


    @Override
    public AdapterMain.MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext=parent.getContext();
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_row_main, parent, false);
        AdapterMain.MainViewHolder ivh = new AdapterMain.MainViewHolder(v);
        return ivh;
    }

    @Override
    public void onBindViewHolder(final AdapterMain.MainViewHolder holder, int position) {
       final Ricetta ricetta= list.get(position);
        holder.textView.setText(ricetta.getNome());
        holder.textCategory.setText("Categoria: "+ricetta.getCategoria());
        //holder.textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        //holder.textView.setSelected(true);
        if(ricetta.isHasImage()){
            Bitmap image = DBManager.getImage(mContext.getFilesDir().getAbsolutePath().concat(ricetta.getId()));
            holder.imageV.setImageBitmap(image);
            holder.imageF.setImageBitmap(image);
        }else{
            holder.imageV.setVisibility(View.INVISIBLE);
            ViewGroup.LayoutParams params= (ViewGroup.LayoutParams) holder.imageV.getLayoutParams();
            params.height=2;
            holder.imageV.setLayoutParams(params);

            if(ricetta.getCategoria().equalsIgnoreCase("dolce"))
                holder.imageF.setImageResource(R.mipmap.ic_dolce);
            else if(ricetta.getCategoria().equalsIgnoreCase("primo"))
                holder.imageF.setImageResource(R.mipmap.ic_primo);
            else if(ricetta.getCategoria().equalsIgnoreCase("secondo"))
                holder.imageF.setImageResource(R.mipmap.ic_secondo);
            else if(ricetta.getCategoria().equalsIgnoreCase("aperitivo"))
                holder.imageF.setImageResource(R.mipmap.ic_aperitivo);
        }
        holder.textNumPersone.setText("Numero persone/porzioni: "+ricetta.getNum_persone());
        holder. ingredienti.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        holder.ingredienti.setLayoutManager(llm);
        AdapterIngredienti adapter= new AdapterIngredienti(ricetta.getIngredientiList());
        holder.ingredienti.setAdapter(adapter);
        holder. buttonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(mContext, RicettaActivity.class);
                intent.putExtra("Ricetta", ricetta.getId());
                mContext.startActivity(intent);
            }
        });
        String nome=ricetta.getNome();
        if(nome.length()>17)
            nome=nome.subSequence(0,15).toString()+"...";
        holder.textView2.setText(nome);
        holder.fc.fold(true);
        // attach click listener to folding cell
        holder.fc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.fc.toggle(false);
            }
        });


    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView textView;
        private TextView textCategory;
        private ImageView imageV;
        private TextView textNumPersone;
        private RecyclerView ingredienti;
        private TextView textView2;
        private  Button buttonInfo;
        private ShapeImageView imageF;
        private FoldingCell fc;

        public MainViewHolder(View itemView) {
            super(itemView);
            //Unfolded
            textView= (TextView) itemView.findViewById(R.id.textViewUnfolded);
            textCategory= (TextView) itemView.findViewById((R.id.textViewUnfoldedCategory));
            imageV= (ImageView) itemView.findViewById(R.id.imageView2Unf);

            textNumPersone = (TextView) itemView.findViewById(R.id.num_persone_unf);

            ingredienti= (RecyclerView) itemView.findViewById(R.id.recycler_ingredienti_unf);
             buttonInfo= (Button) itemView.findViewById(R.id.imageButton_unf);

            fc  = (FoldingCell) itemView.findViewById(R.id.folding_cell);

            //Folded
            textView2= (TextView) itemView.findViewById(R.id.textViewFolded);

           imageF = (ShapeImageView) itemView.findViewById(R.id.imageView2);


        }

        @Override
        public void onClick(View v) {

        }
    }


}

