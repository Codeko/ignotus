/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.codeko.apps.campanilla.ignotus.aracnes.base;

import com.codeko.apps.campanilla.ignotus.Libro;

/**
 *
 * @author Opikanoba
 */
public abstract class MagiaAbstracta {
    String isbn="";
    MagiaTask magia=null;
    Libro libroBase=null;

    public Libro getLibroBase() {
        if(libroBase==null){
            libroBase=new Libro();
        }
        return libroBase;
    }

    public void setLibroBase(Libro libroBase) {
        this.libroBase = libroBase;
    }
   
    
    public MagiaTask getMagia() {
        return magia;
    }

    public void setMagia(MagiaTask magia) {
        this.magia = magia;
    }
    
    
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    public abstract ResultadoMagia iniciar();
    
    public abstract String getNombre();

}
