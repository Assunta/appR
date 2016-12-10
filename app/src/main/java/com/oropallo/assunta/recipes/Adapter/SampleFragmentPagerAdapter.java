package com.oropallo.assunta.recipes.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.oropallo.assunta.recipes.AddRicettaFragment1;
import com.oropallo.assunta.recipes.AddRicettaFragment2;
import com.oropallo.assunta.recipes.AddRicettaFragment3;
import com.oropallo.assunta.recipes.domain.Ricetta;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Assunta on 02/12/2016.
 */

public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Ricetta", "Ingredienti", "Procediemnto" };
    private Map<Integer, Fragment> map;

    public SampleFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        map= new TreeMap<Integer, Fragment>();
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0) {
            Fragment f= new AddRicettaFragment1();
            map.put(position, f);
            return f;
        }
        else if(position==1) {
            Fragment f= AddRicettaFragment2.newInstance(position + 1);
            map.put(position, f);
            return f;
        }
        else {
            Fragment f= new AddRicettaFragment3();
            map.put(position, f);
            return f;
        }
    }
    public Fragment getFragment(int position){
        return map.get(position);
    }
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}