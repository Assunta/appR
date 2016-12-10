package com.oropallo.assunta.recipes.domain;


import co.uk.rushorm.core.RushObject;

/**
 * Created by Assunta on 26/11/2016.
 */

public class IngredienteRicetta   extends RushObject {
    private String nome;
    private int quantita;
    private String unita;

    public IngredienteRicetta(){

    }
    public String getNome() {
        return nome;
    }

    public int getQuantita() {
        return quantita;
    }

    public String getUnita() {
        return unita;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    public void setUnita(String unita) {
        this.unita = unita;
    }
}
