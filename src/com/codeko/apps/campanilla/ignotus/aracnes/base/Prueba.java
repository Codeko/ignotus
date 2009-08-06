/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codeko.apps.campanilla.ignotus.aracnes.base;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.HTMLElementName;
import au.id.jericho.lib.html.Source;
import com.codeko.apps.campanilla.ignotus.util.Util;
import com.codeko.apps.campanilla.ignotus.Libro;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.html.HTMLDocument;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 *
 * @author Opikanoba
 */
public class Prueba {

//    public static void main(String[] args) {
//        try {
//            HttpClient cliente = new HttpClient();
//            //tenemos que empezar por la página inicial o da error
//            GetMethod get = new GetMethod("http://www.mcu.es/webISBN/tituloSimpleFilter.do?cache=init&prev_layout=busquedaisbn&layout=busquedaisbn&language=es");
//            int ret = cliente.executeMethod(get);
//            get.releaseConnection();
//            //Y ya podemos hacer la búsqueda
//            PostMethod post = new PostMethod("http://www.mcu.es/webISBN/tituloSimpleDispatch.do");
//            post.addParameter("action", "Buscar");
//            post.addParameter("language", "es");
//            post.addParameter("layout", "busquedaisbn");
//            post.addParameter("params.cdispo", "A");
//            post.addParameter("params.cisbnExt", "9788497931267");
//            post.addParameter("params.forzaQuery", "N");
//            post.addParameter("params.liConceptosExt[0].texto", "");
//            post.addParameter("params.orderByFormId", "1");
//            post.addParameter("prev_layout", "busquedaisbn");
//            ret = cliente.executeMethod(post);
//            if (ret == 200) {
//                //Ahora de este contenido nos interesa el primer titulo
//                String body = post.getResponseBodyAsString();
//                post.releaseConnection();
//                int pos = body.indexOf("href=\"/webISBN/tituloDetalle.do?");
//                if (pos > -1) {
//                    String url = body.substring(pos + 6, body.indexOf('"', pos + 8));
//                    url = "http://www.mcu.es" + url;
//                    url = url.replace("&amp;", "&");
//                    get = new GetMethod(url);
//                    ret = cliente.executeMethod(get);
//                    BufferedReader in = new BufferedReader(new InputStreamReader(get.getResponseBodyAsStream(), Charset.forName("ISO-8859-1")));
//                    String lin = null;
//                    while ((lin = in.readLine()) != null) {
//                        lin = lin.trim();
//                        if (lin.equals("<tr><th scope=\"row\">T&iacute;tulo:</th><td><strong>")) {
//                            lin = in.readLine();
//                            getLibro().setDato("titulo", lin);
//                        //tfTitulo.setText(lin);
//                        } else if (lin.equals("<tr><th scope=\"row\">Autor:</th><td>")) {
//                            lin = in.readLine().trim();
//                            String sAutor = "";
//
//                            //Ahora vamos a seguir leyendo hasta encontrar el cierre de autores
//                            //que es una linea en la que solo pone </span>
//                            //Todas las lineas que no empiecen por < se consideran datos
//                            //Esto luego se ha ido ampliando para diferente formateos de datos
//                            //demasiado dificiles de explicar
//                            boolean primerAutor = true;
//                            //Se considera que los autores terminan cuando no encontramos el cierre de lienea de tabla
//                            while (!lin.equals("</td></tr>")) {
//                                //Quitamos el span inicial, en algunos casos hay datos despues, en algunos no
//                                lin = lin.replaceAll("<span>", "").trim();
//
//                                //Si lo que queda una vez quitados el <span> es otra etiqueta ignoramos la linea
//                                if (!lin.startsWith("<")) {
//                                    //Si no es una etiqueta sacamos los datos hasta la primera etiqueta que nos encontremos
//                                    String tmpAutor = Util.subStringSeguro(lin, 0, lin.indexOf("<")) + " ";
//                                    //Ahora tenemos que ver si hay que ponerle la barra separadora
//                                    //Si es el primer autor nunca se la ponemos
//                                    if (!primerAutor) {
//                                        //En el caso de que empiece por ( es la fecha del autor de la linea anterior
//                                        //por lo que no le ponemos cierre de barra (se lo pondrá la siguiente linea si hay)
//                                        //Si es un tr. es la indicacion de que la linea anterior es un traductor por lo que tampoco hay que
//                                        //ponerle el cierre
//                                        if (!tmpAutor.trim().startsWith("(") && !tmpAutor.trim().equals("tr.")) {
//                                            sAutor += "/ ";
//                                        }
//                                    }
//                                    //Añadimos el autor a la lista
//                                    sAutor += tmpAutor;
//                                    primerAutor = false;
//                                }
//                                lin = in.readLine().trim();
//                            }
//                            //lin = in2.readLine().trim();
//                            getLibro().setDato("autor", sAutor);
//                        //tfAutor.setText(sAutor);
//
//                        } else if (lin.equals("<tr><th scope=\"row\">Lengua:</th><td>")) {
//                            lin = in.readLine().trim();
//
//                            //Aqui a veces viene el idioma origina y siempre el de publicacion
//                            String strPub = "<span>publicaci&oacute;n:";
//                            String publicacion = Util.subStringSeguro(lin, strPub.length(), lin.indexOf("</span>"));
//                            //Ahora tenemos que ver si hay idioma original
//                            String strTrad = "traducida del:";
//                            int posTraducida = lin.indexOf(strTrad);
//                            if (posTraducida > -1) {
//                                String traduccion = Util.subStringSeguro(lin, posTraducida + strTrad.length(), lin.indexOf("</span>", posTraducida + strTrad.length()));
//                                publicacion = "Publicación:" + publicacion + " / Original:" + traduccion;
//                            }
//                            getLibro().setDato("idioma", publicacion);
//                        //tfIdioma.setText(publicacion);
//                        } else if (lin.equals("<tr><th scope=\"row\">Edici&oacute;n:</th><td>")) {
//                            lin = in.readLine().trim();
//
//                            getLibro().setDato("edicion", Util.subStringSeguro(lin, 6, lin.indexOf("</span>")));
//                        //tfEdicion.setText();
//                        } else if (lin.equals("<tr><th scope=\"row\">Publicaci&oacute;n:</th><td>")) {
//                            //En al primera linea está la ciudad
//                            lin = in.readLine().trim();
//
//                            String pub = Util.subStringSeguro(lin, 6);
//                            //Luego la editorial
//                            lin = in.readLine().trim();
//                            lin = in.readLine().trim();
//                            pub += " " + Util.subStringSeguro(lin, 0, lin.indexOf("</A>"));
//                            //Y luego la fecha
//                            //TODO A veces no viene la fecha
//                            lin = in.readLine().trim();
//                            if (lin.length() > 13) {
//                                pub += " " + Util.subStringSeguro(lin, 7, lin.indexOf("</span>"));
//                            }
//                            getLibro().setDato("publicacion", pub);
//                        //tfPublicacion.setText(pub);
//                        } else if (lin.equals("<tr><th scope=\"row\">Descripci&oacute;n:</th><td>")) {
//
//                            lin = in.readLine().trim();
//                            getLibro().setDato("descripcion", Util.subStringSeguro(lin, 6, lin.indexOf("</span>")));
//                        //tfDescripcion.setText(Util.subStringSeguro(lin,6, lin.indexOf("</span>")));
//                        } else if (lin.equals("<tr><th scope=\"row\">Encuadernaci&oacute;n:</th><td>")) {
//
//                            lin = in.readLine().trim();
//                            getLibro().setDato("encuadernacion", Util.subStringSeguro(lin, 6, lin.indexOf("</span>")));
//                        //tfEncuadernacion.setText(Util.subStringSeguro(lin,6, lin.indexOf("</span>")));
//                        } else if (lin.equals("<tr><th scope=\"row\">Materias:</th><td>")) {
//
//                            lin = in.readLine().trim();
//                            lin = in.readLine().trim();
//                            getLibro().setDato("materias", Util.subStringSeguro(lin, 0, lin.indexOf("</A>")));
//                        //tfMaterias.setText(Util.subStringSeguro(lin,0, lin.indexOf("</A>")));
//                        } else if (lin.equals("<tr><th scope=\"row\">CDU:</th><td><span>")) {
//                            lin = in.readLine().trim();
//                            getLibro().setDato("cdu", Util.subStringSeguro(lin, 0, lin.indexOf("</span>")));
//                        //tfCDU.setText(Util.subStringSeguro(lin,0, lin.indexOf("</span>")));
//                        } else if (lin.equals("<tr><th scope=\"row\">Precio:</th><td>")) {
//                            lin = in.readLine().trim();
//                            getLibro().setDato("precio", Util.subStringSeguro(lin, 6, lin.indexOf("&euro;")).replaceAll(",", "."));
//                        //tfPrecio.setText(Util.subStringSeguro(lin,6, lin.indexOf("&euro;")).replaceAll(",","."));
//                        } else if (lin.equals("<tr><th scope=\"row\">Colecci&oacute;n:</th><td>")) {
//                            lin = in.readLine().trim();
//                            getLibro().setDato("coleccion", Util.subStringSeguro(lin, 6, lin.indexOf("</span>")).replaceAll("&nbsp;", " "));
//                        //tfColeccion.setText(Util.subStringSeguro(lin,6, lin.indexOf("</span>")).replaceAll("&nbsp;", " "));
//                        }
//                    }
//
//                }
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    public void jor() {
        ResultadoMagia res = new ResultadoMagia(-1, "Error indeterminado");
        try {
            String isbn = "9788477024507";
            String surl = "http://www.worldcat.org/isbn/" + isbn + "?tab=details#tabs";

            HTMLDocument doc = new HTMLDocument();

            doc.putProperty("IgnoreCharsetDirective", new Boolean(true));

            URL url = new URI(surl).toURL();
            Source source = new Source(url);
            List l = source.findAllElements("id", "div-item-summary", true);
            if (l.isEmpty()) {
                res = new ResultadoMagia(-3, "El libro no existe");
            } else {
                Libro libro = new Libro();
                libro.setDato("isbn", isbn);
                Element el = (Element) l.iterator().next();
                l = el.findAllElements("class", "item-title", true);
                if (!l.isEmpty()) {
                    Element titulo = (Element) l.iterator().next();
                    String sTiti = titulo.getContent().toString();
                    libro.setDato("titulo", sTiti);
                }
                l = el.findAllElements("class", "item-author", true);
                if (!l.isEmpty()) {
                    Element superAuto = (Element) l.iterator().next();
                    l = superAuto.findAllElements(HTMLElementName.A);
                    if (!l.isEmpty()) {
                        Element enlaceAutor = (Element) l.iterator().next();
                        String sAutor = enlaceAutor.getContent().toString();
                        libro.setDato("autor", sAutor);
                    }
                }
                //Ahora recorremos todos los labels y buscamos el tipo
                l = source.findAllElements(HTMLElementName.TD);
                for (Iterator it = l.iterator(); it.hasNext();) {
                    Element label = (Element) it.next();
                    Object cl = label.getAttributeValue("class");
                    if (cl != null && cl.equals("label")) {
                        String sLabel = (label.getContent().toString());
                        Element sig = (Element) it.next();
                        String sCon = (sig.getContent().toString());
                        if (sLabel.equals("Type:")) {
                            libro.setDato("idioma", sCon.substring(sCon.indexOf(";")));
                        } else if (sLabel.equals("Publisher:")) {
                            libro.setDato("publicacion", sCon.trim());
                        } else if (sLabel.equals("Edition:")) {
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
                        if (sCon.equals("Material Type:")) {
                            String s2 = li.getTextExtractor().toString();
                            libro.setDato("tags", s2.substring(s2.indexOf(":") + 1).trim());
                        } else if (sCon.equals("Description:")) {
                            String s2 = li.getTextExtractor().toString();
                            libro.setDato("descripcion", s2.substring(s2.indexOf(":") + 1).trim());
                        } else if (sCon.equals("Series Title:")) {
                            String s2 = li.getTextExtractor().toString();
                            libro.setDato("coleccion", s2.substring(s2.indexOf(":") + 1).trim());
                        }
                    }

                }
                res = new ResultadoMagia(1, "Datos del libro recuperados correctamente", libro);

            }

        } catch (Exception ex) {
            Logger.getLogger(Prueba.class.getName()).log(Level.SEVERE, null, ex);
            res = new ResultadoMagia(-2, "Error recuperando datos de WorldCat");
            res.setMensajeExtendido(ex.getMessage());
        }
        System.out.println(res);
    }
}
