package com.codeko.apps.campanilla.ignotus;

import com.codeko.apps.campanilla.ignotus.util.Util;
import com.codeko.apps.campanilla.ignotus.sql.Conexion;
import com.codeko.swing.IObjetoTabla;
import com.codeko.util.Obj;
import com.codeko.util.Str;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

/**
 *
 * @author campanilla
 */
public class Libro implements IObjetoTabla {

    final static Logger log = Logger.getLogger(Libro.class.getName());
    HashMap<String, Object> datos = new HashMap<String, Object>();
    File archivo = null;
    ResourceMap resourceMap = getResourceMap();
    Prestamo prestamo = null;
    boolean modoSeguro = false;

    protected HashMap<String, Object> getDatos() {
        return datos;
    }

    public boolean isModoSeguro() {
        return modoSeguro;
    }

    public void setModoSeguro(boolean modoSeguro) {
        this.modoSeguro = modoSeguro;
    }

    public Prestamo getPrestamo() {
        if (prestamo == null) {
            setPrestamo(new Prestamo(getId()));
        }
        return prestamo;
    }

    public void setPrestamo(Prestamo prestamo) {
        this.prestamo = prestamo;
    }

    public File getArchivo() {
        return archivo;
    }

    public void setArchivo(File archivo) {
        this.archivo = archivo;
    }

    public Libro() {
    }

    public Libro(ResultSet datos) throws SQLException {
        cargar(datos);
        setPrestamo(new Prestamo(getId()));
    }

    public int getId() {
        return Util.getInt(getDato("id"));
    }

    public Object getDato(String dato) {
        Object d = datos.get(dato.toLowerCase());
        return d;
    }

    public String getDatoString(String dato) {
        Object res = getDato(dato.toLowerCase());
        if (res != null) {
            return res.toString();
        }
        return "";
    }

    public void setDato(String nombreDato, Object dato) {
        //Asignamos el datos en varios casos:
        //1- Si no estamos en modo seguro.
        //2- Si estamos en modo seguro y el dato actual es nulo o vacio
        //3- Si estamos en modo seguro y el dato nuevo contiene al actual
        if (!isModoSeguro() || (isModoSeguro() && (getDato(nombreDato) == null || getDatoString(nombreDato).equals("") || Str.noNulo(dato).contains(getDatoString(nombreDato))))) {
            datos.put(nombreDato.toLowerCase(), dato);
        }
    }

    /**
     * Aplica al libro actual todos los campos del libro pasado como parámetro
     * @param l Libro que provee de los datos a aplicar al libro actual
     */
    public void aplicarDatos(Libro l) {
        Iterator<String> keys = l.getDatos().keySet().iterator();
        while (keys.hasNext()) {
            String k = keys.next();
            setDato(k, l.getDato(k));
        }
    }

    public void cargar(ResultSet res) throws SQLException {
        ResultSetMetaData rsmd = res.getMetaData();
        int num = rsmd.getColumnCount();
        for (int i = 0; i < num; i++) {
            String nombre = rsmd.getColumnName(i + 1).toLowerCase();
            if (nombre.equals("foto")) {
                Blob blobImg = res.getBlob(i + 1);
                if (blobImg != null) {
                    if (blobImg.length() > Integer.MAX_VALUE) {
                        log.warning(resourceMap.getString("error.imagenDemasiadoGrande"));
                    }
                    //TODO Esto cascara con imagenes que tengan un tamaño mayor a Integer.MAXSIZE
                    ImageIcon img = new ImageIcon(blobImg.getBytes(1, (int) blobImg.length()));
                    setDato("foto", img);
                }
            } else {
                Object obj = res.getObject(i + 1);
                setDato(nombre, obj);
            }
        }
    }

    public int guardar() {
        Connection c = Conexion.getConexion();
        if (Conexion.isConectado()) {
            Statement st = null;
            verificarCampos();
            try {
                st = c.createStatement();
                boolean esInsertar = getDato("id") == null;
                String sql = "UPDATE libros SET nombre=?,propietario=?,observaciones=?,autor=?,autores_relacionados=?,cdu=?,descripcion=?,edicion=?,encuadernacion=?,isbn=?,idioma=?,libros_relacionados=?,materias=?,precio=?,publicacion=?,tags=?,titulo=?,coleccion=?,valoracion=? WHERE id=?";
                if (esInsertar) {
                    sql = "INSERT INTO libros(nombre,propietario,observaciones,autor,autores_relacionados,cdu,descripcion,edicion,encuadernacion,isbn,idioma,libros_relacionados,materias,precio,publicacion,tags,titulo,coleccion,valoracion) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                }
                PreparedStatement pSt = c.prepareStatement(sql);
                pSt.setString(1, getDatoString("nombre"));
                pSt.setString(2, getDatoString("propietario"));
                pSt.setString(3, getDatoString("observaciones"));
                pSt.setString(4, getDatoString("autor"));
                pSt.setString(5, getDatoString("autores_relacionados"));
                pSt.setString(6, getDatoString("cdu"));
                pSt.setString(7, getDatoString("descripcion"));
                pSt.setString(8, getDatoString("edicion"));
                pSt.setString(9, getDatoString("encuadernacion"));
                pSt.setString(10, getDatoString("isbn"));
                pSt.setString(11, getDatoString("idioma"));
                pSt.setString(12, getDatoString("libros_relacionados"));
                pSt.setString(13, getDatoString("materias"));
                pSt.setDouble(14, Util.getDouble(getDato("precio")));
                pSt.setString(15, getDatoString("publicacion"));
                pSt.setString(16, getDatoString("tags"));
                pSt.setString(17, getDatoString("titulo"));
                pSt.setString(18, getDatoString("coleccion"));
                pSt.setInt(19, Util.getInt(getDato("valoracion")));
                //Si es insertar tenemos que asignar la id del where que es la 20
                if (!esInsertar) {
                    pSt.setInt(20, Util.getInt(getDato("id")));
                }
                pSt.executeUpdate();
                if (esInsertar) {
                    ResultSet res = st.executeQuery("SELECT max(id) FROM libros");
                    if (res.next()) {
                        int id = res.getInt(1);
                        setDato("id", id);
                    }
                }
                st.close();
                if (getArchivo() != null) {
                    try {
                        pSt = c.prepareStatement("UPDATE libros SET foto=? WHERE id=?");
                        if (getArchivo().getName().equals("no_hay_foto")) {
                            pSt.setString(1, null);
                        } else {
                            pSt.setBinaryStream(1, new FileInputStream(getArchivo()), (int) getArchivo().length());
                        }
                        pSt.setInt(2, Util.getInt(getDato("id")));
                        pSt.execute();
                        pSt.close();
                        setArchivo(null);
                    } catch (FileNotFoundException ex) {
                        //TODO Poner texot a este error
                        Logger.getLogger(Libro.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                return Util.getInt(getDato("id"));
            } catch (SQLException ex) {
                //TODO Poner texto del error
                Logger.getLogger(Libro.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return Util.getInt(getDato("id"));
    }

    boolean borrar() {
        Connection c = Conexion.getConexion();
        if (Conexion.isConectado()) {
            try {
                PreparedStatement pSt = c.prepareStatement("UPDATE libros SET fecha_borrado=NOW() WHERE id=?");
                pSt.setInt(1, Util.getInt(getDato("id")));
                return pSt.executeUpdate() > 0;
            } catch (SQLException ex) {
                //TODO Poner texto del error
                Logger.getLogger(Libro.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    boolean existe() {
        return getDato("id") != null;
    }

    private ResourceMap getResourceMap() {
        return Application.getInstance().getContext().getResourceMap(Libro.class);
    }

    @Override
    public String toString() {
        Iterator<String> it = datos.keySet().iterator();
        StringBuilder sb = new StringBuilder("Libro[\n");
        while (it.hasNext()) {
            String k = it.next();
            sb.append(k);
            sb.append(":");
            sb.append(datos.get(k));
            sb.append("\n");
        }
        sb.append("]\n");
        return sb.toString();
    }

    private void verificarCampos() {
        if (getDato("nombre") == null || getDatoString("nombre").equals("")) {
            setDato("nombre", getDatoString("titulo"));
        }
    }

    @Override
    public int getNumeroDeCampos() {
        return 10;
    }

    @Override
    public Object getValueAt(int index) {
        Object val = null;
        switch (index) {
            case 0:
                val = getDato("nombre");
                break;
            case 1:
                val = getDato("titulo");
                break;
            case 2:
                val = getDato("propietario");
                break;
            case 3:
                val = getDato("autor");
                break;
            case 4:
                val = getDato("edicion");
                break;
            case 5:
                val = getDato("isbn");
                break;
            case 6:
                val = getDato("publicacion");
                break;
            case 7:
                val = getDato("tags");
                break;
            case 8:
                val = getDato("valoracion");
                break;
            case 9:
                val = getPrestamo().getPrestadoA();
                break;
        }
        return val;
    }

    @Override
    public String getTitleAt(int index) {
        String val = null;
        switch (index) {
            case 0:
                val = "Nombre";
                break;
            case 1:
                val = "Titulo";
                break;
            case 2:
                val = "Propietario";
                break;
            case 3:
                val = "Autor";
                break;
            case 4:
                val = "Edición";
                break;
            case 5:
                val = "I.S.B.N.";
                break;
            case 6:
                val = "Publicación";
                break;
            case 7:
                val = "Tags";
                break;
            case 8:
                val = "Valoración";
                break;
            case 9:
                val = "Prestado a...";
                break;
        }
        return val;
    }

    @Override
    public Class getClassAt(int index) {
        return Obj.noNull(getValueAt(index),"").getClass();
    }

    @Override
    public boolean setValueAt(int index, Object valor) {
        return false;
    }

    @Override
    public boolean esCampoEditable(int index) {
        return false;
    }
}
