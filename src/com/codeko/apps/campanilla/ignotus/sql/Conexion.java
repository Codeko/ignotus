/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codeko.apps.campanilla.ignotus.sql;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

/**
 *
 * @author campanilla
 */
public class Conexion {
    private static final int BUFFER = 2048;
    public static final String NOMBRE_BD="conocimientvs";
    public static final String DATOS="./datos/";
    public static Logger log = Logger.getLogger(Conexion.class.toString());
    private static Connection conexion = null;
    private static ResourceMap resourceMap = Application.getInstance(com.codeko.apps.campanilla.ignotus.IgnotusApp.class).getContext().getResourceMap(Conexion.class);

    private static String getDriver() {
        //return "com.mysql.jdbc.Driver";
        return "org.hsqldb.jdbcDriver";
    }

    private static String getURL() {
        //return "jdbc:mysql://localhost/ignotus";
        return "jdbc:hsqldb:file:"+DATOS+NOMBRE_BD;
    }

    private static Connection realizarConexion() {
        try {
            DriverManager.registerDriver((Driver) Class.forName(getDriver()).newInstance());
            String sCon = getURL();
            return DriverManager.getConnection(sCon);
        } catch (Exception ex) {
            //TODO Deberíamos diferenciar los distintos tipos de errores 
            log.log(Level.SEVERE, resourceMap.getString("Conexion.error.noConecta"), ex);
        }
        return null;

    }
    /**
     * Realiza un volcado de la base de datos en fichero
     * Esta funcion es exclusiva de HSQLDB
     * */
    public static boolean checkpoint(){
        try {
            PreparedStatement ps = Conexion.getConexion().prepareStatement("CHECKPOINT");
            ps.execute();
        } catch (SQLException ex) {
            //TODO I18N
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    
    /**
     * Apaga la base de datos
     * Esta funcion es exclusiva de HSQLDB
     * */
    public static void apargarBD(){
        try {
            PreparedStatement ps = Conexion.getConexion().prepareStatement("SHUTDOWN");
            ps.execute();
            conexion=null;
        } catch (SQLException ex) {
            //TODO I18N
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Connection getConexion() {
        if (conexion == null) {
            conexion = realizarConexion();
            //Ahora verificamos que existan las tablas
            verificarTablas();
        }
        return conexion;
    }

    public static boolean isConectado() {
        try {
            if (conexion == null || conexion.isClosed()) {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, resourceMap.getString("Conexion.error.noConecta"), ex);
            return false;
        }
        return true;
    }
    //TODO Todo esto debería estar en una clase aparte
    public static boolean copiaSeguridad(File destino){
        
        try {
            
            if(!destino.toString().endsWith(".ignotus")){
                destino=new File(destino.toString()+".ignotus");
            }
            FileOutputStream dest = new FileOutputStream(destino);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            File datos=new File(DATOS);
            //ZipEntry en=new ZipEntry(datos.)
            for(File arch:datos.listFiles()){
                //El archivo de bloqueo lo omitimos
                if(arch.getName().endsWith(".lck")){
                    continue;
                }
                ZipEntry entry = new ZipEntry(arch.getName());            
                out.putNextEntry(entry);
                FileInputStream fi = new FileInputStream(arch);
                BufferedInputStream origin = new BufferedInputStream(fi, BUFFER);
                int count;
                byte data[] = new byte[BUFFER];
                while((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
            
            out.close();
        } catch (IOException ex) {
            //TODO Implementar error
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
    //TODO Todo esto debería ir en una clase aparte
    public static boolean restaurarCopiaSeguridad(File copia){
        boolean retorno=true;
        //Primero descomprimirmos el archivo
        try {
            BufferedOutputStream dest = null;
            FileInputStream fis = new FileInputStream(copia);
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
            int count;
            byte data[] = new byte[BUFFER];
            ZipEntry entry;
            String destino=DATOS;
            //Los archivos que debe haber son dbname.script, dbname.backup, dbname.properties y dbname.log
            //El .backup pude que no esté.
            //TODO Habría que verificar que están todos antes de machacar nada. Asi como de que corresponden con el nombre
            //de la base de datos.
            //Apagamos la base de datos
            Conexion.apargarBD();
            //Escribimos los archivos
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    String entryName = entry.getName();
                    String destFN = destino + entry.getName();
                    FileOutputStream fos = new FileOutputStream(destFN);
                    dest = new BufferedOutputStream(fos, BUFFER);
                    while ((count = zis.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                    dest.close();
                }
            }
            zis.close();
            //Y reconectamos
            Conexion.getConexion();
            retorno=Conexion.isConectado();
        } catch (Exception ex) {
            //TODO Implementar error
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            retorno=false;
        }
        return retorno;
    }
    
    private static void verificarTablas() {
        if (isConectado()) {
            try {
                Statement st = conexion.createStatement();
                try {
                    st.executeQuery("SELECT * FROM libros");
                } catch (SQLException ex) {

                    String create = "CREATE TABLE libros (" +
                            "id INTEGER  NOT NULL IDENTITY PRIMARY KEY," +
                            "isbn VARCHAR_IGNORECASE(13) default NULL," +
                            "titulo VARCHAR_IGNORECASE NOT NULL," +
                            "autor VARCHAR_IGNORECASE," +
                            "idioma VARCHAR_IGNORECASE(255) default NULL," +
                            "edicion VARCHAR_IGNORECASE(255) default NULL," +
                            "publicacion VARCHAR_IGNORECASE(255) default NULL," +
                            "descripcion VARCHAR_IGNORECASE(255) default NULL," +
                            "encuadernacion VARCHAR_IGNORECASE(255) default NULL," +
                            "precio decimal(10,2)  default 0 NOT NULL," +
                            "materias VARCHAR_IGNORECASE(255) default NULL," +
                            "cdu VARCHAR_IGNORECASE(255) default NULL," +
                            "autores_relacionados VARCHAR_IGNORECASE," +
                            "libros_relacionados VARCHAR_IGNORECASE," +
                            "tags VARCHAR_IGNORECASE," +
                            "observaciones VARCHAR_IGNORECASE," +
                            "propietario VARCHAR_IGNORECASE(255) default NULL," +
                            "valoracion INTEGER  default 5 NOT NULL," +
                            "fecha_alta timestamp default CURRENT_TIMESTAMP NOT NULL," +
                            "fecha_borrado datetime default NULL, " +
                            "nombre VARCHAR_IGNORECASE NOT NULL," +
                            "coleccion VARCHAR_IGNORECASE," +
                            "foto LONGVARBINARY" +
                            ")";
                    st.executeUpdate(create);
                    String createIndex = "CREATE INDEX libros_fecha_borrado ON libros(fecha_borrado)";
                    st.executeUpdate(createIndex);
                    String createPrestamos="CREATE TABLE prestamos (" +
                            "id INT IDENTITY," +
                            "libro INT NOT NULL, " +
                            "fecha_prestamo DATETIME NOT NULL," +
                            "fecha_devolucion DATETIME DEFAULT NULL," +
                            "prestado_a VARCHAR_IGNORECASE NOT NULL," +
                            "observaciones VARCHAR_IGNORECASE," +
                            "FOREIGN KEY (libro) REFERENCES libros(id) ON DELETE cascade ON UPDATE cascade" +
                            ")";
                    st.executeUpdate(createPrestamos);
                    String createIndexPrestamos="CREATE INDEX prestamos_libro ON prestamos(libro)";
                    st.executeUpdate(createIndexPrestamos);
                }
                st.close();
            } catch (SQLException ex) {
                System.out.println(ex.getErrorCode());
                //TODO Implementar mensaje de error
                Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
