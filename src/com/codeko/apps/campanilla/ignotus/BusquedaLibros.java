/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codeko.apps.campanilla.ignotus;

import com.codeko.apps.campanilla.ignotus.sql.Conexion;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author campanilla
 */
public class BusquedaLibros extends IgnotusBean {
    //Vector<Libro> resultados=new Vector<Libro>();

    String ultimaConsulta = "";

    public void buscar(String texto) {
        texto = texto.trim();
        if (texto.equals(ultimaConsulta)) {
            return;
        } else {
            ultimaConsulta = texto;
        }
        firePropertyChange("nuevaBusqueda", null, true);
        Connection c = Conexion.getConexion();
        if (Conexion.isConectado()) {
            try {
                String sql = "SELECT * FROM libros WHERE fecha_borrado IS NULL AND ( ";
                sql += " (autor LIKE '" + texto + "%' OR autor LIKE '% " + texto + "%') ";
                sql += "OR (nombre LIKE '" + texto + "%' OR nombre LIKE '% " + texto + "%') ";
                sql += "OR (titulo LIKE '" + texto + "%' OR titulo LIKE '% " + texto + "%') ";
                sql += "OR (tags LIKE '" + texto + "%' OR tags LIKE '% " + texto + "%' OR tags LIKE '%," + texto + "%') ";
                sql += "OR (isbn = '" + texto + "' OR isbn = '*" + texto + "')";
                sql += " ) ORDER BY nombre LIMIT 1000";
                Statement st = c.createStatement();
                ResultSet res = st.executeQuery(sql);
                while (res.next()) {
                    Libro l = new Libro(res);
                    firePropertyChange("addLibro", null, l);
                }
            } catch (SQLException ex) {
                //TODO implementar el mensaje de error
                Logger.getLogger(BusquedaLibros.class.getName()).log(Level.SEVERE, null, ex);
            }
            firePropertyChange("busquedaTerminada", null,true);
        }
    }

    public void buscar(String campo, String texto) {
        //TODO Implementar
    }
}
