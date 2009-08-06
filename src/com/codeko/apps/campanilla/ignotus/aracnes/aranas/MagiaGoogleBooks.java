/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codeko.apps.campanilla.ignotus.aracnes.aranas;

import com.codeko.apps.campanilla.ignotus.Libro;
import com.codeko.apps.campanilla.ignotus.aracnes.base.MagiaAbstracta;
import com.codeko.apps.campanilla.ignotus.aracnes.base.ResultadoMagia;
import com.google.gdata.client.books.BooksService;
import com.google.gdata.client.books.VolumeQuery;
import com.google.gdata.data.Person;
import com.google.gdata.data.books.VolumeEntry;
import com.google.gdata.data.books.VolumeFeed;
import com.google.gdata.data.dublincore.Creator;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 *
 * @author Opikanoba
 */
public class MagiaGoogleBooks extends MagiaAbstracta {

    @Override
    public ResultadoMagia iniciar() {
        //TODO I18N
        ResultadoMagia res = new ResultadoMagia(-1, "Error indeterminado");
        try {
            String isbn = getIsbn();//"9788477024507";
            BooksService service = new BooksService("Ignotus");

            VolumeQuery query = new VolumeQuery(new URL("http://www.google.es/books/feeds/volumes"));
            query.setFullTextQuery("isbn:" + isbn);
            query.setMaxResults(1);

            VolumeFeed volumeFeed = service.query(query, VolumeFeed.class);
            Libro libro = null;
            for (VolumeEntry entry : volumeFeed.getEntries()) {
                libro = getLibroBase();
                libro.setDato("titulo", entry.getTitles().get(0).getValue());

                StringBuilder sb = new StringBuilder();
                boolean primero = true;
                for (Person p : entry.getAuthors()) {
                    if (primero) {
                        primero = false;
                    } else {
                        sb.append(" / ");
                    }
                    sb.append(p.getName());
                }
                for (Creator p : entry.getCreators()) {
                    if (primero) {
                        primero = false;
                    } else {
                        sb.append(" / ");
                    }
                    sb.append(p.getValue());
                }
                libro.setDato("autor", sb.toString());

                libro.setDato("isbn", isbn);
                if (entry.hasDescriptions()) {
                    libro.setDato("descripcion", entry.getDescriptions().get(0).getValue());
                }
                if (entry.hasLanguages()) {
                    libro.setDato("idioma", entry.getLanguages().get(0).getValue());
                }
                if (entry.hasFormats()) {
                    libro.setDato("encuadernacion", entry.getFormats().get(0).getValue());
                }
                //TODO Esto suele ser multilinea
                if (entry.hasDescriptions()) {
                    libro.setDato("descripcion", entry.getDescriptions().get(0).getValue());
                }
                if (entry.hasPublishers()) {
                    libro.setDato("publicacion", entry.getPublishers().get(0).getValue());
                } else {
                    libro.setDato("publicacion", "");
                }
                if (entry.hasDates()) {
                    libro.setDato("publicacion", libro.getDato("publicacion") + " " + entry.getDates().get(0).getValue());
                }
                if (entry.hasSubjects()) {
                    String tags = libro.getDatoString("tags");
                    if (tags == null) {
                        tags = "";
                    }
                    libro.setDato("tags", tags + " " + entry.getSubjects().get(0).getValue());
                }

                //TODO Añadir todos los datos que se puedan. Formatear fechas
                System.out.println(entry.getContributors());
                
                if (entry.getThumbnailLink() != null && libro.getArchivo() == null && libro.getDato("foto") == null) {
                    String imgHref = entry.getThumbnailLink().getHref().replace("zoom=5", "zoom=1");
                    HttpClient cliente = new HttpClient();
                    GetMethod g = new GetMethod(imgHref);
                    cliente.executeMethod(g);
                    BufferedImage img = ImageIO.read(g.getResponseBodyAsStream());
                    File tmp = File.createTempFile("ignotus", ".img");
                    ImageIO.write(img, "jpg", tmp);
                    libro.setArchivo(tmp);
                    g.releaseConnection();
                }
            }
            if (libro == null) {
                res = new ResultadoMagia(-3, "El libro no existe");
            } else {
                res = new ResultadoMagia(1, "Datos del libro recuperados correctamente", libro);
            }
        } catch (Exception ex) {
            Logger.getLogger(MagiaWorldCat.class.getName()).log(Level.SEVERE, null, ex);
            res = new ResultadoMagia(-2, "Error recuperando datos de Google Books");
            res.setMensajeExtendido(ex.getMessage());
        }
        return res;
    }

    @Override
    public String getNombre() {
        return "Google Books";
    }
}