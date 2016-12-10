package com.oropallo.assunta.recipes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.oropallo.assunta.recipes.domain.Ricetta;

public class AddRicettaActivity extends AppCompatActivity {
    private Ricetta ricetta;
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ricetta= new Ricetta();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ricetta);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            AddRicettaFragment1 firstFragment = new AddRicettaFragment1();
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment, "FIRST").commit();
        }

    }

    public Ricetta getRicetta(){
        return ricetta;
    }

    public void setRicetta(Ricetta r){
        this.ricetta=r;
    }

}
