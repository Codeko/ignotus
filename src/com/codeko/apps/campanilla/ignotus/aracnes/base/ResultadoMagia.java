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
public class ResultadoMagia {
    int codigo=0;
    String mensaje="";
    String mensajeExtendido="";
    Libro libro=null;

    public String getMensajeExtendido() {
        return mensajeExtendido;
    }

    public void setMensajeExtendido(String mensajeExtendido) {
        this.mensajeExtendido = mensajeExtendido;
    }

    
    
    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
    public ResultadoMagia(int codigo,String mensaje){
        setCodigo(codigo);
        setMensaje(mensaje);
    }
    
    public ResultadoMagia(int codigo,String mensaje,Libro libro){
        setCodigo(codigo);
        setMensaje(mensaje);
        setLibro(libro);
    }
    
    @Override
    public String toString(){
        return "COD:"+getCodigo()+" MSG:"+getMensaje()+" MSG.EXT: "+getMensajeExtendido()+" LIBRO:\n"+getLibro();
    }
}
