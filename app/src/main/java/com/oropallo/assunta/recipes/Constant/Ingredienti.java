package com.oropallo.assunta.recipes.Constant;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Assunta on 25/11/2016.
 */

public class Ingredienti {
    private String FILENAME="ingredienti.txt";
    List<String> ingredienti;

    public Ingredienti(Context context){
        ingredienti= new ArrayList<String>();
        ingredienti.add("farina");
        ingredienti.add("uova");
        ingredienti.add("sale");
        ingredienti.add("formaggio");
        ingredienti.add("formaggio pecorino");
        ingredienti.add("zucchero");
        //TODO rivedere
       /* try {
            InputStream inputStream= context.openFileInput(FILENAME);
            if(inputStream!=null){
                InputStreamReader reader= new InputStreamReader(inputStream);
                BufferedReader bufferedReader= new BufferedReader(reader);
                String valore="";
                while((valore=bufferedReader.readLine())!=null)
                    ingredienti.add(valore);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            ingredienti.add("farina");
            ingredienti.add("uova");
            ingredienti.add("sale");
            ingredienti.add("formaggio");
            ingredienti.add("formaggio pecorino");
            ingredienti.add("zucchero");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public void writeFirstTime(Context context){
        try {
            OutputStreamWriter writer= new OutputStreamWriter(context.openFileOutput(FILENAME, Context.MODE_APPEND));
            writer.write("farina\n");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public List<String> getIngredienti() {
        return ingredienti;
    }

    public String[] getArrayIngredienti(){
      String[] str= new String[ingredienti.size()];
        for(int i=0; i<ingredienti.size(); i++)
            str[i]=ingredienti.get(i);
        return str;
    }

    public void setIngredienti(List<String> ingredienti) {
        this.ingredienti = ingredienti;
    }

    public void addIngrediente(String i){
        ingredienti.add(i);
    }

    @Override
    public String toString() {
        return "Ingredienti{" +
                "ingredienti=" + ingredienti +
                '}';
    }
}
