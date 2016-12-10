package com.oropallo.assunta.recipes;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.oropallo.assunta.recipes.Adapter.AdapterMain;
import com.oropallo.assunta.recipes.R;
import com.oropallo.assunta.recipes.domain.DBManager;
import com.oropallo.assunta.recipes.domain.Ricetta;

import java.util.ArrayList;
import java.util.List;

import cn.refactor.library.ShapeImageView;
import dmax.dialog.SpotsDialog;


public class SearchResultActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ShapeImageView image;
    TextView error;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        recyclerView= (RecyclerView) findViewById(R.id.recycler_search) ;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        final AdapterMain adapterMain= new AdapterMain(new ArrayList<Ricetta>());
        recyclerView.setAdapter(adapterMain);
        image=(ShapeImageView) findViewById(R.id.imageViewSearch);
        image.setVisibility(View.INVISIBLE);
        ViewGroup.LayoutParams params= (ViewGroup.LayoutParams) image.getLayoutParams();
        params.height=2;
        image.setLayoutParams(params);
        error=(TextView) findViewById(R.id.textViewSearch);
        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        //TODO migliorare la ricerca dando suggerimenti e quando si digita
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            List<Ricetta> ricette= DBManager.getRicetteByName(query);
            if(ricette!=null && ricette.size()>0){
                recyclerView.setAdapter(new AdapterMain(ricette));
            }
            else {
                Log.d("Debug", "Ricetta non trovata");
                setNoResult();
            }
        }
        else{
            final SpotsDialog dialog = new SpotsDialog(this);
            dialog.show();
            boolean primo= intent.getBooleanExtra("primo",false);
            boolean secondo= intent.getBooleanExtra("secondo", false);
            boolean dolce= intent.getBooleanExtra("dolce",false);
            boolean aperitivo= intent.getBooleanExtra("aperitivo", false);
            List<Ricetta> ricette= new ArrayList<Ricetta>();
            List<Ricetta> ricetteP = new ArrayList<Ricetta>();
            List<Ricetta> ricetteD = new ArrayList<Ricetta>();
            List<Ricetta> ricetteS = new ArrayList<Ricetta>();
            List<Ricetta> ricetteA = new ArrayList<Ricetta>();
            if(primo)
                ricetteP= DBManager.getRicetteByCategory("Primo");
            if(secondo)
                ricetteS= DBManager.getRicetteByCategory("Secondo");
            if(dolce)
                ricetteD= DBManager.getRicetteByCategory("Dolce");
            if(aperitivo)
                ricetteA= DBManager.getRicetteByCategory("Aperitivo");
            for(Ricetta r: ricetteP)
                ricette.add(r);
            for(Ricetta r: ricetteS)
                ricette.add(r);
            for(Ricetta r: ricetteD)
                ricette.add(r);
            for(Ricetta r: ricetteA)
                ricette.add(r);
            if(ricette.size()>0){
                recyclerView.setAdapter(new AdapterMain(ricette));
                Log.d("DEBUG", "ricette: "+ricette.size());
            }
            else {
                Log.d("Debug", "Ricetta non trovata");
                setNoResult();
            }
            dialog.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setNoResult(){
        error.setText("Nessun risultato trovato");
        ViewGroup.LayoutParams params= (ViewGroup.LayoutParams) image.getLayoutParams();
        params.height=300;
        params.width=300;
        image.setLayoutParams(params);
        image.setVisibility(View.VISIBLE);
    }

}
