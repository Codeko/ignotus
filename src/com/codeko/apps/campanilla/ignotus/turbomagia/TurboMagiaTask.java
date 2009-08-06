/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codeko.apps.campanilla.ignotus.turbomagia;

import com.codeko.apps.campanilla.ignotus.IgnotusApp;
import com.codeko.apps.campanilla.ignotus.Libro;
import com.codeko.apps.campanilla.ignotus.aracnes.base.MagiaTask;
import com.codeko.apps.campanilla.ignotus.aracnes.base.ResultadoMagia;
import com.codeko.apps.campanilla.ignotus.sql.Conexion;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class TurboMagiaTask extends org.jdesktop.application.Task<Integer, Void> {

    boolean terminar = false;
    boolean libroTerminado = true;
    int campoActual = 0;
    int count = 0;
    int noEncontrados=0;
    String[] camposBusqueda = {"titulo", "autor"};

    public boolean isTerminar() {
        return terminar;
    }

    public void setTerminar(boolean terminar) {
        this.terminar = terminar;
    }

    public boolean isLibroTerminado() {
        return libroTerminado;
    }

    public void setLibroTerminado(boolean libroTerminado) {
        this.libroTerminado = libroTerminado;
        if (libroTerminado) {
            setMessage("Terminado.");
        }
    }

    public TurboMagiaTask(IgnotusApp app) {
        super(app);
    }

    @Override
    protected Integer doInBackground() {

        while (!isCancelled() && !isTerminar()) {
            try {
                if (isLibroTerminado()) {
                    rellenarLibro();
                } else {
                    Thread.sleep(500);
                }
            } catch (Exception ex) {
                Logger.getLogger(TurboMagiaTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return count;
    }

    @Override
    protected void succeeded(Integer result) {
        //TODO I18N
        //TODO Distinguir cuando no hay ningún libro más por rellenar y
        //Cuando se ha cancelado antes de que se rellene ninguno
        if (result > 0) {
            JOptionPane.showMessageDialog(((IgnotusApp) getApplication()).getMainFrame(), "He rellenado los datos de " + result + " libros.\nHe marcado para ignorar "+noEncontrados+" ISBNs de los que no he encontrado información.", "¡Terminado!", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(((IgnotusApp) getApplication()).getMainFrame(), "No he rellenado los datos de ningún libro.\nHe marcado para ignorar "+noEncontrados+" ISBNs de los que no he encontrado información.", "¡Estoy perezoso!", JOptionPane.INFORMATION_MESSAGE);
        }
        IgnotusApp.getView().turboMagiaTerminada();
    }

    private void rellenarLibro() throws SQLException, InterruptedException {
        String sql = "SELECT * FROM libros WHERE fecha_borrado IS NULL AND isbn IS NOT NULL AND isbn NOT LIKE '*%' AND RTRIM(LTRIM(isbn))<>'' AND (" + camposBusqueda[campoActual] + " IS NULL OR RTRIM(LTRIM(" + camposBusqueda[campoActual] + "))='') ORDER BY id DESC LIMIT 1";
        Connection c = Conexion.getConexion();
        if (c != null && Conexion.isConectado()) {
            Statement st = c.createStatement();
            ResultSet res = st.executeQuery(sql);
            if (res.next()) {
                final Libro l = new Libro(res);
                System.out.println(l);
                setLibroTerminado(false);
                setMessage("Haciendo Turbo Magia! al ISBN: " + l.getDatoString("isbn") + "...");
                MagiaTask m = new MagiaTask(getApplication(), l.getDatoString("isbn")) {

                    @Override
                    public void succeeded(ResultadoMagia result) {
                        if (result != null) {
                            if (result.getCodigo() > 0) {
                                count++;
                                l.aplicarDatos(result.getLibro());
                                l.guardar();
                            } else if (result.getCodigo() == 0) {
                                noEncontrados++;
                                setMensaje("No se ha encontrado ISBN, marcándolo para ignorar.");
                                l.setDato("isbn", "*" + l.getDato("isbn"));
                                l.guardar();
                            }
                            setLibroTerminado(true);
                        } else {
                            //TODO I18N
                            JOptionPane.showMessageDialog(((IgnotusApp) getApplication()).getMainFrame(), "Algo no ha funcionado como debería.\nLa Turbo Magia! se parará.", "Ooooops!", JOptionPane.ERROR_MESSAGE);
                            setTerminar(true);
                        }

                    }
                };
                getApplication().getContext().getTaskService().execute(m);
            } else {
                campoActual++;
                if (campoActual >= camposBusqueda.length) {
                    setTerminar(true);
                }
            }
        }
    }
}
