package com.codeko.apps.campanilla.ignotus.aracnes.aranas;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.HTMLElementName;
import au.id.jericho.lib.html.Source;
import com.codeko.apps.campanilla.ignotus.Libro;
import com.codeko.apps.campanilla.ignotus.aracnes.base.MagiaAbstracta;
import com.codeko.apps.campanilla.ignotus.aracnes.base.ResultadoMagia;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Opikanoba
 */
public class MagiaWorldCat extends MagiaAbstracta {
    
    @Override
    public ResultadoMagia iniciar() {
        //TODO I18N
        ResultadoMagia res = new ResultadoMagia(-1, "Error indeterminado");
        try {
            String isbn = getIsbn();//"9788477024507";
            String surl = "http://www.worldcat.org/isbn/" + isbn + "?lang=es&tab=details#tabs";

            URL url = new URI(surl).toURL();
            Source source = new Source(url);
            
            List l = source.findAllElements("id", "div-item-summary", true);
            if (l.isEmpty()) {
                res = new ResultadoMagia(-3, "El libro no existe");
            } else {
                Libro libro = getLibroBase();
                libro.setDato("isbn", isbn);
                Element el = (Element) l.iterator().next();
                l = el.findAllElements("class", "item-title", true);
                if (!l.isEmpty()) {
                    Element titulo = (Element) l.iterator().next();
                    String sTiti = titulo.getContent().toString();
                    libro.setDato("titulo", sTiti);
                }
                /*l = el.findAllElements("class", "item-author", true);
                if (!l.isEmpty()) {
                    Element superAuto = (Element) l.iterator().next();
                    l = superAuto.findAllElements(HTMLElementName.A);
                    if (!l.isEmpty()) {
                        Element enlaceAutor = (Element) l.iterator().next();
                        String sAutor = enlaceAutor.getContent().toString();
                        libro.setDato("autor", sAutor);
                    }
                }*/
                //Ahora recorremos todos los labels y buscamos el tipo
                l = source.findAllElements(HTMLElementName.TD);
                for (Iterator it = l.iterator(); it.hasNext();) {
                    Element label = (Element) it.next();
                    Object cl = label.getAttributeValue("class");
                    if (cl != null && cl.equals("label")) {
                        String sLabel = (label.getContent().toString());
                        
                        if (sLabel.equals("Tipo:")) {
                            Element sig = (Element) it.next();
                        String sCon = (sig.getContent().toString()).trim();
                            libro.setDato("idioma", sCon.substring(sCon.indexOf(";")+1).trim());
                        } else if (sLabel.equals("Editorial:")) {
                            Element sig = (Element) it.next();
                        String sCon = (sig.getContent().toString()).trim();
                            libro.setDato("publicacion", sCon.trim());
                        } else if (sLabel.equals("Edición:")) {
                            Element sig = (Element) it.next();
                        String sCon = (sig.getContent().toString()).trim();
                            libro.setDato("edicion", sCon.trim());
                        }
                    }
                }
                //Por último vemos los detalles
                //recorremos los Stron en busca de unos en concreto
                l = source.findAllElements(HTMLElementName.LI);
                for (Iterator it = l.iterator(); it.hasNext();) {
                    Element li = (Element) it.next();
                    List strongs = li.findAllElements(HTMLElementName.STRONG);
                    if (!strongs.isEmpty()) {
                        Element strong = (Element) strongs.iterator().next();
                        String sCon = (strong.getContent().toString()).trim();
                        if (sCon.equals("Tipo de material:")) {
                            String s2=li.getTextExtractor().toString();
                            libro.setDato("tags", s2.substring(s2.indexOf(":")+1).trim());
                        } else if (sCon.equals("Descripción:")) {
                            String s2=li.getTextExtractor().toString();
                            libro.setDato("descripcion",  s2.substring(s2.indexOf(":")+1).trim());
                        } else if (sCon.equals("Título de la serie:")) {
                            String s2=li.getTextExtractor().toString();
                            libro.setDato("coleccion",  s2.substring(s2.indexOf(":")+1).trim());
                        }else if (sCon.equals("Responsabilidad:")) {
                            String s2=li.getTextExtractor().toString();
                            libro.setDato("autor",  s2.substring(s2.indexOf(":")+1).trim());
                        }
                    }

                }
                res = new ResultadoMagia(1, "Datos del libro recuperados correctamente", libro);
            }

        } catch (Exception ex) {
            Logger.getLogger(MagiaWorldCat.class.getName()).log(Level.SEVERE, null, ex);
            res = new ResultadoMagia(-2, "Error recuperando datos de WorldCat");
            res.setMensajeExtendido(ex.getMessage());
        }
        return res;
    }

    @Override
    public String getNombre() {
        return "WorldCat";
    }
}

    
