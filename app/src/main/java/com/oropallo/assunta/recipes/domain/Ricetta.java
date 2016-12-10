package com.oropallo.assunta.recipes.domain;



import android.graphics.Bitmap;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import co.uk.rushorm.core.RushObject;
import co.uk.rushorm.core.annotations.RushList;

/**
 * Created by Assunta on 27/11/2016.
 */


public class Ricetta extends RushObject {

    private String nome;
    private String categoria;
    private String num_persone;
    private String nota;
    private Bitmap image;
    //TODO mettere un oggetto procedimento..
    private String procedimento;
    @RushList(classType = IngredienteRicetta.class)
    private List<IngredienteRicetta> ingredientiList;
    private boolean hasImage;


    public Ricetta(){

        ingredientiList= new ArrayList<IngredienteRicetta>();
        image=null;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getNum_persone() {
        return num_persone;
    }

    public void setNum_persone(String num_persone) {
        this.num_persone = num_persone;
    }

    public String getProcedimento() {
        return procedimento;
    }

    public void setProcedimento(String procedimento) {
        this.procedimento = procedimento;
    }

    public List<IngredienteRicetta> getIngredientiList() {
        return ingredientiList;
    }

    public void setIngredientiList(List<IngredienteRicetta> ingredientiList) {
        this.ingredientiList = ingredientiList;
    }
    public void setImage(Bitmap image) {
        this.image=image;
    }

    public Bitmap getImage(){
        return image;
    }


    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    @Override
    public String toString() {
        return "Ricetta{" +
                "id="+getId()+'\'' +
                "categoria='" + categoria + '\'' +
                ", nome='" + nome + '\'' +
                ", num_persone='" + num_persone + '\'' +
                ", nota='" + nota + '\'' +
                ", procedimento='" + procedimento + '\'' +
                ", ingredientiList=" +ingredientiList +
                ",image= "+hasImage+
                '}';
    }
}
