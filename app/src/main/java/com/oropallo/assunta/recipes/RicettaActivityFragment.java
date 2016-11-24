package com.oropallo.assunta.recipes;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oropallo.assunta.recipes.Adapter.AdapterIngredienti;

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
        RecyclerView recylerIngredienti= (RecyclerView) rootView.findViewById(R.id.recycler_ingredienti);
        recylerIngredienti.setHasFixedSize(true);

        //LayoutManager
        LinearLayoutManager llm = new LinearLayoutManager(rootView.getContext());
        recylerIngredienti.setLayoutManager(llm);
        AdapterIngredienti adapter= new AdapterIngredienti();
        recylerIngredienti.setAdapter(adapter);

        return rootView;
    }
}
