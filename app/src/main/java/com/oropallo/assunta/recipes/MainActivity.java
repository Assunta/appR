package com.oropallo.assunta.recipes;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.RadioGroup;

import com.oropallo.assunta.recipes.Adapter.AdapterAddIngrediente;
import com.oropallo.assunta.recipes.Adapter.AdapterMain;
import com.oropallo.assunta.recipes.domain.DBManager;
import com.oropallo.assunta.recipes.domain.IngredienteRicetta;
import com.oropallo.assunta.recipes.domain.Ricetta;

import java.util.ArrayList;
import java.util.List;

import co.uk.rushorm.android.AndroidInitializeConfig;
import co.uk.rushorm.android.RushBitmapFile;
import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushCore;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private  List<Ricetta> list;
    private  RecyclerView recyclerView;

    @Override
    protected void onResume() {
        super.onResume();
        //refresh list
        list= DBManager.getAllRicetteWithLimit(10);
        final AdapterMain adapterMain= new AdapterMain(list);
        recyclerView.setAdapter(adapterMain);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Floating button add ricetta
        FloatingActionButton floatingActionButton= (FloatingActionButton) findViewById(R.id.fabAddRicetta);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(v.getContext(), ActivityPageSlidingAddRicetta.class);
                startActivity(i);
            }
        });

        // Rush is initialization
        List<Class<? extends Rush>> classes= new ArrayList<>();
        //add class to be saved
        classes.add(Ricetta.class);
        classes.add(IngredienteRicetta.class);
        classes.add(RushBitmapFile.class);
        AndroidInitializeConfig config = new AndroidInitializeConfig(getApplicationContext());
        config.setClasses(classes);
        RushCore.initialize(config);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        list= DBManager.getAllRicetteWithLimit(10);
        recyclerView= (RecyclerView) findViewById(R.id.recycler_main);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        final AdapterMain adapterMain= new AdapterMain(list);
        recyclerView.setAdapter(adapterMain);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        ComponentName cn= new ComponentName(this, SearchResultActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Ricetta r=getRandomRicetta();
            Intent i= new Intent(this, RicettaActivity.class);
            i.putExtra("Ricetta", r.getId());
            startActivity(i);
        } else if (id == R.id.nav_gallery) {
            advancedSearch();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private Ricetta getRandomRicetta() {
        return DBManager.getRandomRicetta();
    }

    private void advancedSearch(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater =getLayoutInflater();
        final View v_view= inflater.inflate(R.layout.dialog_advanced_research, null);
        final CheckBox checkPrimo= (CheckBox) v_view.findViewById(R.id.checkBoxPrimo);
        final CheckBox checkSecondo= (CheckBox)v_view.findViewById(R.id.checkBox2Secondo);
        final CheckBox checkDolce= (CheckBox) v_view.findViewById(R.id.checkBoxDolce);
        final CheckBox checkAperitivo= (CheckBox) v_view.findViewById(R.id.checkBoxAperitivo);

        //create dialog
        builder.setView(v_view)
                // Add action buttons
                .setPositiveButton(R.string.search, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        boolean primo= checkPrimo.isChecked();
                        boolean secondo= checkSecondo.isChecked();
                        boolean dolce= checkDolce.isChecked();
                        boolean aperitivo= checkAperitivo.isChecked();

                        Intent i= new Intent(v_view.getContext(),SearchResultActivity.class);
                        i.putExtra("primo",primo);
                        i.putExtra("secondo",secondo);
                        i.putExtra("dolce",dolce);
                        i.putExtra("aperitivo",aperitivo);
                        startActivity(i);
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert=builder.create();
        alert.show();

    }


}
