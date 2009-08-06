/*
 * IgnotusApp.java
 */
package com.codeko.apps.campanilla.ignotus;

import com.codeko.apps.campanilla.ignotus.sql.Conexion;
import com.codeko.swing.LookAndFeels;
import java.sql.SQLException;
import java.util.EventObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class IgnotusApp extends SingleFrameApplication {

    private static ResourceMap res = Application.getInstance(com.codeko.apps.campanilla.ignotus.IgnotusApp.class).getContext().getResourceMap(Idioma.class);

    public static ResourceMap getResourceMap() {
        return res;
    }

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        this.addExitListener(new ExitListener() {

            @Override
            public boolean canExit(EventObject arg0) {
                return true;
            }

            @Override
            public void willExit(EventObject arg0) {

                if (Conexion.isConectado()) {
                    try {
                        Conexion.getConexion().createStatement().execute("SHUTDOWN");
                    } catch (SQLException ex) {
                        //TODO ¿?Mensaje de errro o pasamos
                        Logger.getLogger(IgnotusApp.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        //Vamos a conectarnos a la base de datos
        Conexion.getConexion();
        if (!Conexion.isConectado()) {
            //TODO Aqui debe ser mas claro, ir traducido e intentar arreglar errores
            JOptionPane.showMessageDialog(null, "No se puede conectar a la base de datos");
            exit();
        }
        LookAndFeels.asignarLookAndFeelGuardado(this);
        show(new IgnotusView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
        root.setIconImage(getContext().getResourceMap(IgnotusApp.class).getImageIcon("Application.icon").getImage());
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of IgnotusApp
     */
    public static IgnotusApp getApplication() {
        return Application.getInstance(IgnotusApp.class);
    }

    public static IgnotusView getView() {
        return (IgnotusView) getApplication().getMainView();
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(IgnotusApp.class, args);
    }
}
