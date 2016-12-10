package com.oropallo.assunta.recipes;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.oropallo.assunta.recipes.Adapter.AdapterIngredienti;
import com.oropallo.assunta.recipes.Adapter.AdapterProcedimento;

import com.oropallo.assunta.recipes.domain.DBManager;
import com.oropallo.assunta.recipes.domain.Ricetta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import co.uk.rushorm.android.RushBitmapFile;

/**
 * A placeholder fragment containing a simple view.
 */
public class RicettaActivityFragment extends Fragment {

    public RicettaActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =inflater.inflate(R.layout.fragment_ricetta, container, false);
        String id=getArguments().getString("Ricetta");
        Log.d("Debug","Visualizzo ricetta: "+id);
        //TODO controllo su id exception
        Ricetta r= DBManager.getRicetta(id);
        Log.d("DEBUG", r.toString());

        ArrayList<String> procedimento=new ArrayList<String>();
        TextView nome= (TextView)rootView.findViewById(R.id.name_ricetta);
        TextView categoria= (TextView) rootView.findViewById(R.id.name_category);
        TextView numPersone= (TextView) rootView.findViewById(R.id.num_persone_tetx_view);
        TextView nota= (TextView) rootView.findViewById(R.id.nota_text_view);
        if(r!=null){
            nome.setText(r.getNome());
            categoria.setText("Categoria: "+r.getCategoria());
            numPersone.setText("Numero persone/porzioni: "+ r.getNum_persone());
            nota.setText("Nota: "+ r.getNota());
            procedimento.add(r.getProcedimento());
        }
        else Log.d("Debug", "Ricetta nulla");

        //recyclerView ingredienti
        RecyclerView recylerIngredienti= (RecyclerView) rootView.findViewById(R.id.recycler_ingredienti);
        recylerIngredienti.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(rootView.getContext());
        recylerIngredienti.setLayoutManager(llm);
        AdapterIngredienti adapter= new AdapterIngredienti(r.getIngredientiList());
        recylerIngredienti.setAdapter(adapter);

        //recyclerView procediemnto
        RecyclerView recylerProcediemnto= (RecyclerView) rootView.findViewById(R.id.recycler_procedimento);
        recylerProcediemnto.setHasFixedSize(true);
        LinearLayoutManager llm2 = new LinearLayoutManager(rootView.getContext());
        recylerProcediemnto.setLayoutManager(llm2);
        AdapterProcedimento adapter2= new AdapterProcedimento(procedimento);
        recylerProcediemnto.setAdapter(adapter2);

        //imageview ricetta
        ImageView image_view= (ImageView) rootView.findViewById(R.id.imageView_ricetta);
        if(r.isHasImage()) {
            Bitmap image = DBManager.getImage(getContext().getFilesDir().getAbsolutePath().concat(r.getId()));
            image_view.setImageBitmap(image);
            }
        else {
            Log.d("Debug", "nessuna immagine trovata");
            image_view.setVisibility(View.INVISIBLE);
            image_view.getLayoutParams().height=5;
        }
        return rootView;
    }
}
