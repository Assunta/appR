package com.oropallo.assunta.recipes.domain;

import co.uk.rushorm.core.RushObject;

/**
 * Created by Assunta on 22/12/2016.
 */

public class Bookmark extends RushObject {
    String idRicetta;

    public Bookmark(){

    }

    public Bookmark(String idRicetta) {
        this.idRicetta = idRicetta;
    }

    public String getIdRicetta() {
        return idRicetta;
    }

    public void setIdRicetta(String idRicetta) {
        this.idRicetta = idRicetta;
    }

}
