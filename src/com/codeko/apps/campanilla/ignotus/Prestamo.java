/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.codeko.apps.campanilla.ignotus;

import com.codeko.apps.campanilla.ignotus.sql.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author campanilla
 */
public class Prestamo {
    
    private String prestadoA="";
    private String observaciones="";
    private GregorianCalendar fechaPrestamo=null;
    private GregorianCalendar fechaDevolucion=null;
    private int id=0;
    private int libro=0;

    public int getLibro() {
        return libro;
    }

    public void setLibro(int libro) {
        this.libro = libro;
    }
    
    
    public Prestamo(){
        setFechaPrestamo(new GregorianCalendar());
    }
    
    public Prestamo(int idLibro){
        Connection c=Conexion.getConexion();
        if(Conexion.isConectado()){
            try {
                String sql = "SELECT * FROM prestamos WHERE libro=" + idLibro + " AND fecha_devolucion IS NULL ";
                Statement st = c.createStatement();
                ResultSet res = st.executeQuery(sql);
                if (res.next()) {
                    cargarDatos(res);
                }
                setLibro(idLibro);
            } catch (SQLException ex) {
                //TODO Implementar mensaje de error
                Logger.getLogger(Prestamo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public Prestamo(ResultSet datos) throws SQLException{
            cargarDatos(datos);
    }
    
    private void cargarDatos(ResultSet datos) throws SQLException{
        if(datos!=null){
            setId(datos.getInt("ID"));
            setLibro(datos.getInt("LIBRO"));
            setPrestadoA(datos.getString("PRESTADO_A"));
            setObservaciones(datos.getString("OBSERVACIONES"));
            Timestamp tPrestamo=datos.getTimestamp("FECHA_PRESTAMO");
            Timestamp tDevolucion=datos.getTimestamp("FECHA_DEVOLUCION");
            if(tPrestamo!=null){
                GregorianCalendar cal=new GregorianCalendar();
                cal.setTime(tPrestamo);
                setFechaPrestamo(cal);
            }
            if(tDevolucion!=null){
                GregorianCalendar cal=new GregorianCalendar();
                cal.setTime(tDevolucion);
                setFechaDevolucion(cal);
            }
        }
    }
    
    public boolean estaPrestado(){
        return getFechaPrestamo()!=null&&getFechaDevolucion()==null;
    }
    
    public void guardar(){
        Connection c = Conexion.getConexion();
        if (Conexion.isConectado()) {
            try {
                String sql = "UPDATE prestamos SET prestado_a=?,fecha_prestamo=?,fecha_devolucion=?,observaciones=? WHERE id=?";
                if(getId()==0){
                    sql="INSERT INTO prestamos(prestado_a,fecha_prestamo,fecha_devolucion,observaciones,libro) VALUES(?,?,?,?,?)";
                }
                PreparedStatement pSt = c.prepareStatement(sql);
                pSt.setString(1,getPrestadoA());
                pSt.setTimestamp(2,new Timestamp(getFechaPrestamo().getTimeInMillis()));
                if(getFechaDevolucion()==null){
                    pSt.setTimestamp(3,null);
                }else{
                    pSt.setTimestamp(3,new Timestamp(getFechaDevolucion().getTimeInMillis()));
                }  
                pSt.setString(4,getObservaciones());
                if(getId()==0){
                    pSt.setInt(5,getLibro());
                }else{
                    pSt.setInt(5,getId());
                }
                pSt.executeUpdate();
                pSt.close();
                if(getId()==0){
                    Statement st=c.createStatement();
                    ResultSet res=st.executeQuery("SELECT max(id) FROM prestamos");
                    if(res.next()){
                        int nuevaId= res.getInt(1);
                        setId(nuevaId);
                    }
                    st.close();
                }
            } catch (SQLException ex) {
                //TODO Mensaje de error
                Logger.getLogger(Prestamo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static Vector<Prestamo> getPrestamosLibro(int id){
        Vector<Prestamo> pres=new Vector<Prestamo>();
        Connection c=Conexion.getConexion();
        if(Conexion.isConectado()){
            try {
                String sql = "SELECT * FROM prestamos WHERE libro=" + id + " AND fecha_devolucion IS NOT NULL ORDER BY fecha_prestamo ";
                Statement st = c.createStatement();
                ResultSet res = st.executeQuery(sql);
                while (res.next()) {
                    Prestamo l = new Prestamo(res);
                    pres.addElement(l);
                }
            } catch (SQLException ex) {
                //TODO Implementar mensaje de error
                Logger.getLogger(Prestamo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return pres;
    }
    
    public GregorianCalendar getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(GregorianCalendar fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public GregorianCalendar getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(GregorianCalendar fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getPrestadoA() {
        return prestadoA;
    }

    public void setPrestadoA(String prestadoA) {
        this.prestadoA = prestadoA;
    }
    
    public Vector getVectorDatos(){
        Vector<Object> v=new Vector<Object>();
        v.addElement(this);
        v.addElement(getFechaPrestamo());
        v.addElement(getFechaDevolucion());
        v.addElement(getObservaciones());
        return v;
    }
    
    public String toString(){
        return getPrestadoA();
    }
    
}
