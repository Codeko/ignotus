package com.codeko.apps.campanilla.ignotus.aracnes.aranas;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.Source;
import com.codeko.apps.campanilla.ignotus.aracnes.base.MagiaAbstracta;
import com.codeko.apps.campanilla.ignotus.aracnes.base.ResultadoMagia;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * Copyright Codeko Informática 2008
 * www.codeko.com
 * @author Codeko
 */
public class MagiaMCU extends MagiaAbstracta {

    @Override
    public ResultadoMagia iniciar() {
        ResultadoMagia res = new ResultadoMagia(-1, "Error indeterminado");
        try {
            String isbn = getIsbn();
            HttpClient cliente = new HttpClient();
            //tenemos que empezar por la página inicial o da error
            GetMethod get = new GetMethod("http://www.mcu.es/webISBN/tituloSimpleFilter.do?cache=init&prev_layout=busquedaisbn&layout=busquedaisbn&language=es");
            int ret = cliente.executeMethod(get);
            get.releaseConnection();
            //Y ya podemos hacer la búsqueda
            PostMethod post = new PostMethod("http://www.mcu.es/webISBN/tituloSimpleDispatch.do");
            post.addParameter("action", "Buscar");
            post.addParameter("language", "es");
            post.addParameter("layout", "busquedaisbn");
            post.addParameter("params.cdispo", "A");
            post.addParameter("params.cisbnExt", isbn);
            post.addParameter("params.forzaQuery", "N");
            post.addParameter("params.liConceptosExt[0].texto", "");
            post.addParameter("params.orderByFormId", "1");
            post.addParameter("prev_layout", "busquedaisbn");
            ret = cliente.executeMethod(post);
            if (ret == 200) {
                //Ahora de este contenido nos interesa el primer titulo
                String body = post.getResponseBodyAsString();
                post.releaseConnection();
                int pos = body.indexOf("href=\"/webISBN/tituloDetalle.do?");
                if (pos > -1) {
                    String url = body.substring(pos + 6, body.indexOf('"', pos + 8));
                    url = "http://www.mcu.es" + url;
                    url = url.replace("&amp;", "&");
                    get = new GetMethod(url);
                    ret = cliente.executeMethod(get);
                    Source s = new Source(get.getResponseBodyAsStream());
                    List l = s.findAllElements("tr");
                    if (l.isEmpty()) {
                        res = new ResultadoMagia(-3, "El libro no existe");
                    } else {
                        getLibroBase().setDato("isbn", isbn);
                        Iterator it = l.iterator();
                        String edicion = "";
                        while (it.hasNext()) {
                            Element el = (Element) it.next();
                            //De cada tr tenemos que sacar el th
                            String tag = (((Element) el.findAllElements("th").iterator().next()).getContent().toString());
                            String val = (((Element) el.findAllElements("td").iterator().next()).getTextExtractor().toString());
                            
                            if (tag.equals("Título:")) {
                                getLibroBase().setDato("titulo", val);
                            } else if (tag.equals("Autor/es:")) {
                                getLibroBase().setDato("autor", val.replace("[Ver títulos]", " / "));
                            } else if (tag.equals("Lengua/s de traducción:")) {
                                getLibroBase().setDato("idioma", val);
                            } else if (tag.equals("Edición:")) {
                                edicion += val + " ";
                            } else if (tag.equals("Fecha Edición:")) {
                                edicion += " Edición: "+val + " ";
                            } else if (tag.equals("Fecha Impresión:")) {
                                edicion += " Impresión: " + val + " ";
                            } else if (tag.equals("Publicación:")) {
                                getLibroBase().setDato("publicacion", val);
                            } else if (tag.equals("Descripción:")) {
                                getLibroBase().setDato("descripcion", val);
                            } else if (tag.equals("Encuadernación:")) {
                                getLibroBase().setDato("encuadernacion", val);
                            } else if (tag.equals("Materia/s:")) {
                                getLibroBase().setDato("materias", val);
                            } else if (tag.equals("Precio:")) {
                                getLibroBase().setDato("precio", val.replace("Euros", ""));
                            }
                        }
                        getLibroBase().setDato("edicion", edicion);
                    }
                    get.releaseConnection();
                    s.clearCache();
                }
            }
            res = new ResultadoMagia(1, "Datos del libro recuperados correctamente", getLibroBase());
        } catch (Exception ex) {
            Logger.getLogger(MagiaMCU.class.getName()).log(Level.SEVERE, null, ex);
            res = new ResultadoMagia(-2, "Error recuperando datos del Ministerio de Cultura");
            res.setMensajeExtendido(ex.getMessage());
        }
        return res;
    }

    @Override
    public String getNombre() {
        return "Ministerio de Cultura";
    }
}
