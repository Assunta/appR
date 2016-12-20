package com.oropallo.assunta.recipes.domain;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import co.uk.rushorm.android.RushBitmapFile;
import co.uk.rushorm.core.RushSearch;
import co.uk.rushorm.core.search.RushWhere;

/**
 * Created by Assunta on 02/12/2016.
 */

public class DBManager {

    public static Ricetta getRicetta(String id) {
        Ricetta ricetta = new RushSearch().whereId(id).findSingle(Ricetta.class);
        return ricetta;
    }

    public static List<Ricetta> getAllRicette() {
        /* Get all objects */
        List<Ricetta> objects = new RushSearch().find(Ricetta.class);
        return objects;
    }
    public static List<Ricetta> getAllRicetteWithLimit(int limit) {
        /* Get all objects */
        List<Ricetta> objects = new RushSearch().setLimit(limit).orderDesc("rush_updated").find(Ricetta.class);
        return objects;
    }

    public static Ricetta getRandomRicetta(){
        List<Ricetta> objects = new RushSearch().find(Ricetta.class);
        int random= new Random().nextInt(objects.size());
        Log.d("DEBUG", "Random "+random);
        Ricetta r= (Ricetta) objects.get(random);
        return r;
    }

    public static List<IngredienteRicetta> getAllIngredienti(){
        List<IngredienteRicetta> objects = new RushSearch().find(IngredienteRicetta.class);
        return objects;
    }

    public static Bitmap getImage(String path){
        RushBitmapFile file= new RushSearch().whereEqual("directory",path).findSingle(RushBitmapFile.class);
        try {
            return file.getImage();
        } catch (IOException e) {
            return null;
        }
    }

    public static Ricetta getRicettaByName(String name){
        Ricetta ricetta=new RushSearch().whereEqual("nome",name).findSingle(Ricetta.class);
        return ricetta;
    }

    public static List<Ricetta> getRicetteByName(String name){
        List<Ricetta> ricette= new RushSearch().whereContains("nome", name).find(Ricetta.class);
        return ricette;
    }

    public static List<Ricetta> getRicetteByCategory(String category){
        List<Ricetta> ricette= new RushSearch().whereContains("categoria", category).find(Ricetta.class);
        return ricette;
    }

    public static List<Ricetta> getRicettaByIngrediente(String ingrediente){
        List<Ricetta> allRicette= DBManager.getAllRicette();
        List<Ricetta> result= new ArrayList<Ricetta>();
        for(Ricetta r: allRicette){
            for(IngredienteRicetta r2: new RushSearch().whereChildOf(r, "ingredientiList").find(IngredienteRicetta.class)) {
                if (r2.getNome().equalsIgnoreCase(ingrediente))
                    result.add(r);
            }
        }
        return result;
    }



}
