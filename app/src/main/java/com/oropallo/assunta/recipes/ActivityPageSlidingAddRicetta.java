package com.oropallo.assunta.recipes;

import android.Manifest;
import android.app.ActionBar;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.oropallo.assunta.recipes.Adapter.SampleFragmentPagerAdapter;
import com.oropallo.assunta.recipes.domain.Ricetta;

public class ActivityPageSlidingAddRicetta  extends AppCompatActivity {

    private Ricetta ricetta;
    private  final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE=0;
    protected void onCreate(Bundle savedInstanceState) {
        ricetta=new Ricetta();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_sliding_add_ricetta);

        //per non visualizzare la tastiera
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //controllo se ho il permesso di lettura foto
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager()));

        // Give the PagerSlidingTabStrip the ViewPager
        final PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);
        tabsStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                if(position==2){
                    //collezione le informazioni del primo e del secondo fragment
                    SampleFragmentPagerAdapter adapter= (SampleFragmentPagerAdapter)viewPager.getAdapter();
                    AddRicettaFragment1 f1= (AddRicettaFragment1) adapter.getFragment(0);
                    f1.getInfo();
                    AddRicettaFragment2 f2= (AddRicettaFragment2) adapter.getFragment(1);
                    f2.getInfo();
                }
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(ActivityPageSlidingAddRicetta.this,
                            "Non potrai aggiungere foto alle tue ricette", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public Ricetta getRicetta(){
        return ricetta;
    }

    public void setRicetta(Ricetta r){
        this.ricetta=r;
    }
}
