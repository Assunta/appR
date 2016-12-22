package com.oropallo.assunta.recipes;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.oropallo.assunta.recipes.Adapter.AdapterMain;
import com.oropallo.assunta.recipes.Constant.Ingredienti;
import com.oropallo.assunta.recipes.domain.Bookmark;
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

    private Menu menu;
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
        classes.add(Bookmark.class);
        AndroidInitializeConfig config = new AndroidInitializeConfig(getApplicationContext());
        config.setClasses(classes);
        RushCore.initialize(config);


        //show tutorial
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        if(sharedPrefs.getBoolean("prefShowTutorial",true)==true)
        addTap();

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
        this.menu=menu;
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
            Intent i = new Intent(this, UserSettingsActivity.class);
            startActivity(i);
            return true;
        }
        if(id==R.id.action_filtra_category)
        {
            advancedSearch();
        }
        if(id==R.id.action_filtra_bookmark)
        {
            bookmarkSearch();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Ricetta r = getRandomRicetta();
            Intent i = new Intent(this, RicettaActivity.class);
            i.putExtra("Ricetta", r.getId());
            startActivity(i);
        }else if (id == R.id.search_ingrediente) {
            searchIngrediente();
        }else if(id==R.id.parser){
            Intent i = new Intent(this, ParserRicetta.class);
            startActivity(i);
        }else if(id==R.id.addMano){
            Intent i=new Intent(this, ActivityPageSlidingAddRicetta.class);
            startActivity(i);
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
                        i.putExtra("TYPE", "SEARCH_CATEGORY");
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

    private void searchIngrediente(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater =getLayoutInflater();
        final View v_view= inflater.inflate(R.layout.dialog_search_ingrediente, null);
        final AutoCompleteTextView text= (AutoCompleteTextView) v_view.findViewById(R.id.autoCompleteTextSearchIngrediente);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(v_view.getContext(),android.R.layout.simple_dropdown_item_1line, new Ingredienti().getArrayIngredienti());
        text.setAdapter(adapter);
        builder.setView(v_view)
                // Add action buttons
                .setPositiveButton(R.string.search, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                       String ingrediente = text.getText().toString();
                        Intent i= new Intent(v_view.getContext(),SearchResultActivity.class);
                        i.putExtra("TYPE", "SEARCH_INGREDIENTE");
                        i.putExtra("ingrediente",ingrediente);
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


    private void addTap(){
       final Activity activity=this;
        TapTargetView.showFor(this,                 // `this` is an Activity
        TapTarget.forView(findViewById(R.id.fabAddRicetta), "Aggiungi una nuova ricetta", "ogni volta che vuoi aggiungere una nuova ricetta clicca qui")
                // All options below are optional
                .outerCircleColor(R.color.colorPrimaryDark)      // Specify a color for the outer circle
                .targetCircleColor(R.color.md_white_1000)   // Specify a color for the target circle
                .titleTextSize(25)                  // Specify the size (in sp) of the title text
                .titleTextColor(R.color.md_white_1000)      // Specify the color of the title text
                .descriptionTextSize(15)            // Specify the size (in sp) of the description text
                .dimColor(R.color.md_black_1000)            // If set, will dim behind the view with 30% opacity of the given color
                .drawShadow(true)                   // Whether to draw a drop shadow or not
                .cancelable(true)                  // Whether tapping outside the outer circle dismisses the view
                .tintTarget(true)                   // Whether to tint the target view's color
                .transparentTarget(true)           // Specify whether the target is transparent (displays the content underneath)
                .targetRadius(90),                  // Specify the target radius (in dp)
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetDismissed(TapTargetView view, boolean userInitiated) {
                        super.onTargetDismissed(view, userInitiated);
                        TapTargetView.showFor(activity,                 // `this` is an Activity
                                TapTarget.forView(findViewById(R.id.buttonHide), "Ricerca ricetta", "inserisci il nome, o parte di esso, della ricetta che vuoi cercare")
                                        // All options below are optional
                                        .outerCircleColor(R.color.colorPrimaryDark)      // Specify a color for the outer circle
                                        .targetCircleColor(R.color.md_white_1000)   // Specify a color for the target circle
                                        .titleTextSize(25)                  // Specify the size (in sp) of the title text
                                        .titleTextColor(R.color.md_white_1000)      // Specify the color of the title text
                                        .descriptionTextSize(15)            // Specify the size (in sp) of the description text
                                        .dimColor(R.color.md_black_1000)            // If set, will dim behind the view with 30% opacity of the given color
                                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                                        .cancelable(true)                  // Whether tapping outside the outer circle dismisses the view
                                        .tintTarget(true)                   // Whether to tint the target view's color
                                        .transparentTarget(true)           // Specify whether the target is transparent (displays the content underneath)
                                        .targetRadius(40)                  // Specify the target radius (in dp)
                               );

                    }
                });

    }

    private void bookmarkSearch(){
        Intent i= new Intent(this.getApplicationContext(),SearchResultActivity.class);
        i.putExtra("TYPE", "SEARCH_BOOKMARK");
        startActivity(i);
    }
}



