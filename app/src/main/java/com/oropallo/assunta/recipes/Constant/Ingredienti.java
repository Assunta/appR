package com.oropallo.assunta.recipes.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Assunta on 25/11/2016.
 */

public class Ingredienti {
    List<String> ingredienti;

    public Ingredienti(){
        //TODO gestire diversamente, con una costante o con una lettura sul db
        ingredienti= new ArrayList<String>();
        ingredienti.add("farina");
        ingredienti.add("uova");
        ingredienti.add("sale");
        ingredienti.add("formaggio");
        ingredienti.add("formaggio pecorino");
        ingredienti.add("zucchero");
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
