
package com.codeko.apps.campanilla.ignotus.backup;

import com.codeko.apps.campanilla.ignotus.IgnotusApp;
import com.codeko.apps.campanilla.ignotus.backup.IngotusBackupFileFilter;
import com.codeko.apps.campanilla.ignotus.sql.Conexion;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

 public class ImportarCopiaTask extends org.jdesktop.application.Task<Object, Void> {
        public ImportarCopiaTask(org.jdesktop.application.Application app) {
            super(app);
        }
        @Override protected Object doInBackground() {
            String msg = null;
            JFileChooser jfc = new JFileChooser();
            jfc.removeChoosableFileFilter(jfc.getAcceptAllFileFilter());
            jfc.addChoosableFileFilter(new IngotusBackupFileFilter());
            //TODO I18N
            jfc.setSelectedFile(new File("CopiaDeSeguridad.ignotus"));
            int op = jfc.showOpenDialog(IgnotusApp.getApplication().getMainFrame());
            if(op==JFileChooser.APPROVE_OPTION){
                File copia=jfc.getSelectedFile();
                if(copia!=null && copia.exists()){
                    //Advertimos de que vamos a sobreescribir la base de datos actual 
                    //TODO I18N
                    op=JOptionPane.showConfirmDialog(IgnotusApp.getApplication().getMainFrame(), "Se va a sobreescribir la base de datos actual con los datos de la copia de seguridad.\nTodos los datos actuales se perderan.\n¿Continuar?","Advertencia",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(op==JOptionPane.OK_OPTION){
                        //Ahora descomprimimos el arhivo y verificamos que están todos los archivos y que 
                        //son de la misma base de datos
                        if(!Conexion.restaurarCopiaSeguridad(copia)){
                            //TODO I18N
                            msg="Error restaurando la copia de seguridad";
                        }else{
                            //TODO I18N
                            setMessage("Copia de seguridad restaurada con éxito");
                            msg="";
                        }
                    }
                }else{
                    //TODO I18N
                    msg="Archivo de copia de segúridad inválido";
                }
            }else{
                setMessage("Cancelado por el usuario");
            }
            return msg;  // return your result
        }
        @Override protected void succeeded(Object result) {
            if(result!=null){
                if(result.equals("")){
                     //TODO I18N
                    JOptionPane.showMessageDialog(IgnotusApp.getApplication().getMainFrame(), "Copia de seguridad restaurada con éxito", "Biennn!",JOptionPane.INFORMATION_MESSAGE);
                }else{
                    //TODO I18N
                    JOptionPane.showMessageDialog(IgnotusApp.getApplication().getMainFrame(), result, "Error",JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }