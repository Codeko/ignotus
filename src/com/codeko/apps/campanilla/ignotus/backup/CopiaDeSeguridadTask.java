package com.codeko.apps.campanilla.ignotus.backup;

import com.codeko.apps.campanilla.ignotus.IgnotusApp;
import com.codeko.apps.campanilla.ignotus.sql.Conexion;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.jdesktop.application.Application;

public class CopiaDeSeguridadTask extends org.jdesktop.application.Task<Object, Void> {
        public CopiaDeSeguridadTask(Application app) {
            super(app);
        }
        @Override protected Object doInBackground() {
            String msg = null;
            //Volcamos a fichero los ultimos cambios
            if(Conexion.checkpoint()){
                //Pedimo el fichero donde vamos a guardar los datos
                JFileChooser jfc = new JFileChooser();
                //TODO I18N
                jfc.setSelectedFile(new File("CopiaDeSeguridad.ignotus"));
                int op = jfc.showSaveDialog(IgnotusApp.getApplication().getMainFrame());
                if (op == JFileChooser.APPROVE_OPTION) {
                    File f = jfc.getSelectedFile();
                    boolean continuar=true;
                    if(f.exists()){
                        continuar=false;
                        op=JOptionPane.showConfirmDialog(IgnotusApp.getApplication().getMainFrame(), "El archivo '"+f.toString()+"' ya existe.\n¿Sobreescribirlo?", "Advertencia", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                        if(op==JOptionPane.OK_OPTION){
                            continuar=true;
                        }else{
                            //TODO I18N
                            setMessage("Cancelado por el usuario");
                        }
                    }
                    if(continuar){
                        //TODO I18N
                        setMessage("Guardando copia de seguridad");
                        if (!Conexion.copiaSeguridad(f)) {
                            //TODO I18N
                            //TODO Debe ser un mensaje mas concreto
                            msg = "Error realizando la copia de seguridad.";
                        } else {
                            //TODO I18N
                            setMessage("Copia realizada con éxito");
                            return f;
                        }
                    }
                }else{
                    //TODO I18N
                    setMessage("Cancelado por el usuario");
                }
            }else{
                //TODO I18N
                msg="No se ha podido volcar la base de datos para la copia de seguridad.";
            }
            return msg;
            
        }

        @Override
        protected void succeeded(Object result) {
            if(result instanceof File){
                //TODO I18N
                JOptionPane.showMessageDialog(IgnotusApp.getApplication().getMainFrame(), "Copia de seguridad realizada con éxito", "Biennn!",JOptionPane.INFORMATION_MESSAGE);
            }else if(result!=null){
                //TODO I18N
                JOptionPane.showMessageDialog(IgnotusApp.getApplication().getMainFrame(), result, "Error",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
