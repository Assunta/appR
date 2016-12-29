package com.oropallo.assunta.recipes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.EditText;
import android.widget.TextView;


import com.oropallo.assunta.recipes.Adapter.AdapterIngredienti;
import com.oropallo.assunta.recipes.domain.Bookmark;
import com.oropallo.assunta.recipes.domain.DBManager;
import com.oropallo.assunta.recipes.domain.IngredienteRicetta;
import com.oropallo.assunta.recipes.domain.Ricetta;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import co.uk.rushorm.android.RushBitmapFile;

public class RicettaActivity extends AppCompatActivity {

    //TODO fare item ingredienti movibili
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ricetta);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (findViewById(R.id.fragment_container_ricetta) != null) {
            if (savedInstanceState != null) {
                return;
            }
            // Create a new Fragment to be placed in the activity layout
            RicettaActivityFragment firstFragment = new RicettaActivityFragment();
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container_ricetta, firstFragment).commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.menu_ricetta, menu);
        // Associate searchable configuration with the SearchView
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == android.R.id.home) {
            finish();
        }
        if (id == R.id.action_add_bookmark) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            RicettaActivityFragment f = (RicettaActivityFragment) fragmentManager.findFragmentById(R.id.fragment_container_ricetta);
            Ricetta r = f.getRicetta();
            if (!DBManager.isBookmarked(r.getId()))
                DBManager.addBookmark(new Bookmark(r.getId()));
            else DBManager.removeBookmark(r.getId());
            f.restartActivty();
        }
        if (id == R.id.action_calculate) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            RicettaActivityFragment f = (RicettaActivityFragment) fragmentManager.findFragmentById(R.id.fragment_container_ricetta);
            final Ricetta r = f.getRicetta();
            if (r.getNum_persone() == null || r.getNum_persone().equals('0') || r.getNum_persone().equals("")) {
                AlertDialog dialog = new AlertDialog.Builder(this).create();
                dialog.setTitle("Impossibile eseguire l'operazione");
                dialog.setMessage("impossibile calcolare gli ingredienti per un nuomero di porzioni differente; numero di porzioni non specificato per questa ricetta");
                dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inflater = getLayoutInflater();
                final View v_view = inflater.inflate(R.layout.dialog_calculate, null);
                final EditText editText = (EditText) v_view.findViewById(R.id.editText_calculate);
                builder.setView(v_view)
                        // Add action buttons
                        .setPositiveButton(R.string.calculate_string, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                int n = Integer.parseInt(editText.getText().toString().trim());
                                Ricetta newRicetta=calculateNuovoNumPorzioni(r, n);
                                dialog.cancel();
                                AlertDialog.Builder builder = new AlertDialog.Builder(v_view.getContext());
                                LayoutInflater inflater = getLayoutInflater();
                                final View v_view = inflater.inflate(R.layout.dialog_show_result_calculate, null);
                                RecyclerView recylerIngredienti= (RecyclerView)v_view.findViewById(R.id.recycler_ingredienti_calculate);
                                recylerIngredienti.setHasFixedSize(true);
                                LinearLayoutManager llm = new LinearLayoutManager(v_view.getContext());
                                recylerIngredienti.setLayoutManager(llm);
                                AdapterIngredienti adapter= new AdapterIngredienti(newRicetta.getIngredientiList());
                                recylerIngredienti.setAdapter(adapter);
                                builder.setView(v_view).
                                setTitle("Risultato per "+n+" porzioni")
                                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();

                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }
        }
        if (id == R.id.action_delete) {
            final Context context = this;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Eliminazione");
            builder.setMessage("Sei davver sicuro di voler eliminare questa ricetta?");
            builder.setCancelable(true);
            builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    RicettaActivityFragment f = (RicettaActivityFragment) fragmentManager.findFragmentById(R.id.fragment_container_ricetta);
                    Ricetta r = f.getRicetta();
                    if (r.isHasImage()) {
                        //check delete elimina davver??
                        RushBitmapFile file = new RushBitmapFile(context.getFilesDir().getAbsolutePath().concat(r.getId()));
                        file.delete();
                    }
                    r.delete();
                    dialog.dismiss();
                    finish();
                }
            })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        }
        return super.onOptionsItemSelected(item);
    }
    private Ricetta calculateNuovoNumPorzioni(Ricetta r, int n){
        List<IngredienteRicetta> ingredienti= r.getIngredientiList();
        List<IngredienteRicetta> newIngredienti=new ArrayList<IngredienteRicetta>();
        double old= Double.parseDouble(r.getNum_persone().trim());
        for(IngredienteRicetta i: ingredienti){
            double nuova_quantita=i.getQuantita()*n/old;
            IngredienteRicetta newIngrediente= new IngredienteRicetta(i.getNome(), nuova_quantita, i.getUnita());
            newIngredienti.add(newIngrediente);
        }
        Ricetta ricetta= r;
        ricetta.setIngredientiList(newIngredienti);
        return ricetta;
    }


}
