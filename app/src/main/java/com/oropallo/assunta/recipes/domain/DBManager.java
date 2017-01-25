package com.oropallo.assunta.recipes.domain;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import co.uk.rushorm.android.RushBitmapFile;
import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushCore;
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
    public static RushBitmapFile getImageFile(String path){
        RushBitmapFile file= new RushSearch().whereEqual("directory",path).findSingle(RushBitmapFile.class);
        return file;
    }

    public static List<Bitmap> getAllImages(){
        List<RushBitmapFile> objects = new RushSearch().find(RushBitmapFile.class);
        List<Bitmap> images= new ArrayList<Bitmap>();
        for(RushBitmapFile file: objects){
        try {
            images.add(file.getImage());
        } catch (IOException e) { }
        }
        return images;
    }

    public static Map<String, Bitmap> getAllImagesWithName(){
        List<RushBitmapFile> objects = new RushSearch().find(RushBitmapFile.class);
        Map<String,Bitmap> images= new HashMap<String,Bitmap>();
        for(RushBitmapFile file: objects){
            try {
                images.put(file.getId(),file.getImage());
            } catch (IOException e) { }
        }
        return images;
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
                if(r2.getNome().toLowerCase().trim().contains(ingrediente.toLowerCase().trim()))
                    if(!result.contains(r))result.add(r);
            }
        }
        return result;
    }

    public static List<Ricetta> getBookmarks(){
        List<Bookmark> bookmarks= new RushSearch().find(Bookmark.class);
        List<Ricetta> ricette= new ArrayList<Ricetta>();
        for(Bookmark b :bookmarks){
            ricette.add(DBManager.getRicetta(b.getIdRicetta()));
        }
        return ricette;
    }

    public static void addBookmark(Bookmark b){
        b.save();
    }

    public static boolean isBookmarked(String id){
        Bookmark b= new RushSearch().whereEqual("idRicetta",id).findSingle(Bookmark.class);
        if(b!=null)
            return true;
        return false;
    }

    public static void removeBookmark(String id){
        Bookmark b= new RushSearch().whereEqual("idRicetta",id).findSingle(Bookmark.class);
        b.delete();
    }

    public static int getNumeroRicette(){
       return (int) new RushSearch().count(Ricetta.class);
    }

    public static String exportRicette(){
        List<Ricetta> list= getAllRicette();
        String jsonString= RushCore.getInstance().serialize(list);
        return jsonString;
    }

    public static List<Ricetta> importRicette(String jsonString){
        List<Ricetta> ricette= new ArrayList<Ricetta>();
        List<Rush>list=RushCore.getInstance().deserialize(jsonString);
        for(Rush item:list){
            Ricetta r= (Ricetta) item;
            ricette.add(r);
        }
        return ricette;
    }


}
