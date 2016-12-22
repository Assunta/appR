package com.oropallo.assunta.recipes;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import com.oropallo.assunta.recipes.domain.Bookmark;
import com.oropallo.assunta.recipes.domain.DBManager;
import com.oropallo.assunta.recipes.domain.IngredienteRicetta;
import com.oropallo.assunta.recipes.domain.Ricetta;

import org.w3c.dom.Text;

import co.uk.rushorm.android.RushBitmapFile;

public class RicettaActivity extends AppCompatActivity {

    //TODO fare item ingredienti movibili
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ricetta);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        if(id == R.id.action_home) {
            finish();
        }
        if(id == R.id.action_add_bookmark) {
            FragmentManager fragmentManager= getSupportFragmentManager();
            RicettaActivityFragment f=(RicettaActivityFragment) fragmentManager.findFragmentById(R.id.fragment_container_ricetta);
            Ricetta r= f.getRicetta();
            if(!DBManager.isBookmarked(r.getId()))
            DBManager.addBookmark(new Bookmark(r.getId()));
            else DBManager.removeBookmark(r.getId());
            f.restartActivty();
        }
        if(id== R.id.action_delete){
            final Context context=this;
            AlertDialog.Builder builder= new AlertDialog.Builder(this);
            builder.setTitle("Eliminazione");
            builder.setMessage("Sei davver sicuro di voler eliminare questa ricetta?");
            builder.setCancelable(true);
            builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    FragmentManager fragmentManager= getSupportFragmentManager();
                    RicettaActivityFragment f=(RicettaActivityFragment) fragmentManager.findFragmentById(R.id.fragment_container_ricetta);
                    Ricetta r= f.getRicetta();
                    if(r.isHasImage()){
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
            AlertDialog alert= builder.create();
            alert.show();

        }
        return super.onOptionsItemSelected(item);
    }


}
