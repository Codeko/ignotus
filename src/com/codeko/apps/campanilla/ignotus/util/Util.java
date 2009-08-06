/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.codeko.apps.campanilla.ignotus.util;

import java.awt.Dimension;

/**
 *
 * @author campanilla
 */
public class Util {
    
    public static int getInt(Object obj){
        if(obj==null){
            return 0;
        }else if(obj instanceof Number){
            Number n=(Number)obj;
            return n.intValue();
        }else{
            try{
                Integer i=Integer.parseInt(obj.toString());
                return i;
            }catch(NumberFormatException ex){
                return 0;
            }
        }
    }
    
    public static double getDouble(Object obj){
        if(obj==null){
            return 0d;
        }else if(obj instanceof Number){
            Number n=(Number)obj;
            return n.doubleValue();
        }else{
            try{
                Double i=Double.parseDouble(obj.toString());
                return i;
            }catch(NumberFormatException ex){
                return 0d;
            }
        }
    }
    
    public static String subStringSeguro(String cadena,int desde,int hasta){
        try{
            return cadena.substring(desde,hasta);
        }catch(Exception e){
        
        }
        return "";
    }
    
    public static String subStringSeguro(String cadena,int desde){
        try{
            return cadena.substring(desde);
        }catch(Exception e){
        
        }
        return "";
    }
    
    public static Dimension getTamanoEscalado(Dimension tamanoReal,Dimension tamanoMaximo){
        Dimension d=new Dimension();
        //Para escalar primero tenemos que ver el tamano mas grande
        boolean escalarAncho=(tamanoReal.getWidth()>tamanoReal.getHeight());
        //Ahora vemos la proporcion de la medida
        double real1=escalarAncho?tamanoReal.getWidth():tamanoReal.getHeight();
        double real2=!escalarAncho?tamanoReal.getWidth():tamanoReal.getHeight();
        double max1=escalarAncho?tamanoMaximo.getWidth():tamanoMaximo.getHeight();
        double max2=!escalarAncho?tamanoMaximo.getWidth():tamanoMaximo.getHeight();
        //Si el tamaño maximo es mayor no hay que escalar nada
        if(max1>real1){
            return tamanoReal;
        }
        double proporcion=max1/real1;
        real1=real1*proporcion;
        real2=real2*proporcion;
        d.setSize(escalarAncho?real1:real2,!escalarAncho?real1:real2);
        return d;
    }
    
    public static String lPad(Object cad, char pad, int tam) {
		return lPad(cad, pad + "", tam);
	}

	public static String lPad(Object cadena, String pad, int tam) {
		String cad=String.valueOf(cadena);
		int dif = tam - cad.length();
		if (dif <= 0) {
			return cad;
		}
		String tmp = "";
		for (int i = 0; i < dif; i++) {
			tmp += pad;
		}
		return tmp + cad;
	}

	public static String rPad(String cad, char pad, int tam) {
		int dif = tam - cad.length();
		if (dif <= 0) {
			return cad;
		}

		for (int i = 0; i < dif; i++) {
			cad += pad;
		}
		return cad;
	}

	public static String recortar(String cadena, int tamano) {
		if (cadena != null) {
			if (cadena.length() > tamano) {
				cadena = cadena.substring(0, tamano);
			}
		}
		return cadena;
	}

	public static String recortar(String cadena, int tamano, String substitucion_recorte) {
		if (cadena != null) {
			if (cadena.length() > tamano) {
				cadena = cadena.substring(0, tamano) + substitucion_recorte;
			}
		}
		return cadena;
	}

	public static String getCadenaRepetida(String cadena, int cantidad) {
		String ret = "";
		for (int i = 0; i < cantidad; i++) {
			ret += cadena;
		}
		return ret;
	}
        
        public static String procesarISBN(String isbn){
            if(isbn!=null){
                isbn=isbn.replaceAll("-", "").replaceAll(":", "").trim();
            }
            return isbn;
        }
        
        public static Boolean esISBNValido(String isbn){
            isbn=procesarISBN(isbn);
            return isbn!=null && isbn.length()>0 && isbn.length()>9 && isbn.length()<14;
        }

}
