/*
 * IgnotusView.java
 */
package com.codeko.apps.campanilla.ignotus;

import com.codeko.apps.campanilla.ignotus.aracnes.base.MagiaTask;
import com.codeko.apps.campanilla.ignotus.aracnes.base.ResultadoMagia;
import com.codeko.apps.campanilla.ignotus.util.Fechas;
import com.codeko.apps.campanilla.ignotus.util.Util;
import com.codeko.apps.campanilla.ignotus.backup.CopiaDeSeguridadTask;
import com.codeko.apps.campanilla.ignotus.backup.ImportarCopiaTask;
import com.codeko.apps.campanilla.ignotus.turbomagia.PanelTurboISBN;
import com.codeko.apps.campanilla.ignotus.turbomagia.TurboMagiaTask;
import com.codeko.swing.LookAndFeels;
import com.toedter.calendar.JDateChooser;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.ActionMap;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 * The application's main frame.
 */
public class IgnotusView extends FrameView {

    final static Logger log = Logger.getLogger(IgnotusView.class.getName());
    ResourceMap resourceMap = getResourceMap();
    Libro libro = null;
    File archivo = null;
    boolean prestamosCargados = false;
    TurboMagiaTask turboMagia = null;

    public boolean isPrestamosCargados() {
        return prestamosCargados;
    }

    public void setPrestamosCargados(boolean prestamosCargados) {
        this.prestamosCargados = prestamosCargados;
    }

    public File getArchivo() {
        return archivo;
    }

    public void setArchivo(File archivo) {
        this.archivo = archivo;
        if (archivo != null) {
            ImageIcon img = new ImageIcon(archivo.getAbsolutePath());
            mostrarFoto(img);
        }
    }

    private void mostrarFoto() {
        if (getLibro().getArchivo() != null) {
            try {
                mostrarFoto(new ImageIcon(getLibro().getArchivo().toURI().toURL()));
            } catch (MalformedURLException ex) {
                //TODO I18N
                Logger.getLogger(IgnotusView.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            mostrarFoto((ImageIcon) getLibro().getDato("foto"));
        }
    }

    private void mostrarFoto(ImageIcon img) {
        if (img != null) {
            Dimension max = lFoto.getSize();
            Dimension real = new Dimension(img.getIconWidth(), img.getIconHeight());
            Dimension escalada = Util.getTamanoEscalado(real, max);
            img = new ImageIcon(img.getImage().getScaledInstance((int) escalada.getWidth(), (int) escalada.getHeight(), Image.SCALE_SMOOTH));
        }
        lFoto.setIcon(img);
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibroSinCargar(Libro l) {
        this.libro = l;

    }

    public void setLibro(Libro l) {
        setLibroSinCargar(l);
        cargarLibro(l);
    }

    public IgnotusView(SingleFrameApplication app) {
        super(app);

        initComponents();
        limpiar();
        // status bar initialization - message timeout, idle icon and busy animation, etc

        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }

        for (int i = 0; i < valoracionIcon.length; i++) {
            valoracionIcon[i] = resourceMap.getIcon("valoracion.iconos[" + i + "]");
        }
        for (int i = 0; i < valoracionTexto.length; i++) {
            valoracionTexto[i] = resourceMap.getString("valoracion.texto[" + i + "]");
        }
        lIcoValoracion.setIcon(valoracionIcon[slValoracion.getValue()]);
        lIcoValoracion.setText(valoracionTexto[slValoracion.getValue()]);
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            @Override
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
        //TODO Esto esta aquí porque al puto netbeans le sale de los cojones, deberíamos cambiarlo cuando nos pasemos al eclipse

        TableCellRenderer renderer = new DefaultTableCellRenderer() {

            DateFormat formatter;

            @Override
            public void setValue(Object value) {
                if (formatter == null) {
                    formatter = SimpleDateFormat.getDateInstance();
                }
                if (value instanceof GregorianCalendar) {
                    value = ((GregorianCalendar) value).getTime();
                }
                setText((value == null) ? "" : formatter.format(value));
            }
        };
        tablaPrestamos.getColumnModel().getColumn(1).setCellRenderer(renderer);
        tablaPrestamos.getColumnModel().getColumn(2).setCellRenderer(renderer);
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = IgnotusApp.getApplication().getMainFrame();
            aboutBox = new IgnotusAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        IgnotusApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        barraHerramientas = new javax.swing.JToolBar();
        bNuevo = new javax.swing.JButton();
        bBorrar = new javax.swing.JButton();
        bGuardar = new javax.swing.JButton();
        bBuscar = new javax.swing.JButton();
        bMagia = new javax.swing.JButton();
        bPrestar = new javax.swing.JButton();
        bDevolver = new javax.swing.JButton();
        bTurboMagia = new javax.swing.JToggleButton();
        lCodigo = new javax.swing.JLabel();
        lNombre = new javax.swing.JLabel();
        tfCodigo = new javax.swing.JTextField();
        lPropietario = new javax.swing.JLabel();
        tfPropietario = new javax.swing.JTextField();
        tfNombre = new javax.swing.JTextField();
        lFoto = new javax.swing.JLabel();
        panelPestanas = new javax.swing.JTabbedPane();
        panelISBN = new javax.swing.JPanel();
        lISBN = new javax.swing.JLabel();
        lTitulo = new javax.swing.JLabel();
        lAutor = new javax.swing.JLabel();
        lIdioma = new javax.swing.JLabel();
        lEdicion = new javax.swing.JLabel();
        lPublicacion = new javax.swing.JLabel();
        lDescripcion = new javax.swing.JLabel();
        lEncuadernacion = new javax.swing.JLabel();
        lPrecio = new javax.swing.JLabel();
        lMaterias = new javax.swing.JLabel();
        lCDU = new javax.swing.JLabel();
        tfEncuadernacion = new javax.swing.JTextField();
        tfISBN = new javax.swing.JTextField();
        tfTitulo = new javax.swing.JTextField();
        tfAutor = new javax.swing.JTextField();
        tfIdioma = new javax.swing.JTextField();
        tfEdicion = new javax.swing.JTextField();
        tfPublicacion = new javax.swing.JTextField();
        tfDescripcion = new javax.swing.JTextField();
        tfPrecio = new javax.swing.JTextField();
        tfMaterias = new javax.swing.JTextField();
        tfCDU = new javax.swing.JTextField();
        lColeccion = new javax.swing.JLabel();
        tfColeccion = new javax.swing.JTextField();
        panelValoracion = new javax.swing.JPanel();
        lAutoresRelacionaos = new javax.swing.JLabel();
        lLibrosRelacionaos = new javax.swing.JLabel();
        lObservaciones = new javax.swing.JLabel();
        lValoracion = new javax.swing.JLabel();
        slValoracion = new javax.swing.JSlider();
        tfAutoresRelacionaos = new javax.swing.JTextField();
        tfLibrosRelacionaos = new javax.swing.JTextField();
        spObservaciones = new javax.swing.JScrollPane();
        taObservaciones = new javax.swing.JTextArea();
        lIcoValoracion = new javax.swing.JLabel();
        panelPrestamos = new javax.swing.JPanel();
        lPrestadoA = new javax.swing.JLabel();
        tfPrestadoA = new javax.swing.JTextField();
        lFechaPrestamo = new javax.swing.JLabel();
        lTituloTablaPrestamos = new javax.swing.JLabel();
        lHacePrestamo = new javax.swing.JLabel();
        lObservacionesPrestamo = new javax.swing.JLabel();
        tfObservacionesPrestamo = new javax.swing.JTextField();
        tfFechaPrestamo = new com.toedter.calendar.JDateChooser();
        scrollTablaPrestamos = new javax.swing.JScrollPane();
        tablaPrestamos = new javax.swing.JTable();
        lTags = new javax.swing.JLabel();
        tfTags = new javax.swing.JTextField();
        bAsignarFoto = new javax.swing.JButton();
        bBorrarPortada = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        fileMenu.add(LookAndFeels.getMenuCambioLookAndFeel(this.getFrame(), IgnotusApp.getApplication()));
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        menuUtilidades = new javax.swing.JMenu();
        menuHaceCopia = new javax.swing.JMenuItem();
        menuImportaCopia = new javax.swing.JMenuItem();
        menuTurboMagia = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        menuDedicatoria = new javax.swing.JMenuItem();
        menuWebIgnotus = new javax.swing.JMenuItem();
        menuLicencia = new javax.swing.JMenuItem();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        mainPanel.setName("mainPanel"); // NOI18N

        barraHerramientas.setFloatable(false);
        barraHerramientas.setRollover(true);
        barraHerramientas.setName("barraHerramientas"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(com.codeko.apps.campanilla.ignotus.IgnotusApp.class).getContext().getActionMap(IgnotusView.class, this);
        bNuevo.setAction(actionMap.get("nuevo")); // NOI18N
        bNuevo.setFocusable(false);
        bNuevo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bNuevo.setName("bNuevo"); // NOI18N
        bNuevo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        barraHerramientas.add(bNuevo);

        bBorrar.setAction(actionMap.get("borrar")); // NOI18N
        bBorrar.setFocusable(false);
        bBorrar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bBorrar.setName("bBorrar"); // NOI18N
        bBorrar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        barraHerramientas.add(bBorrar);

        bGuardar.setAction(actionMap.get("guardar")); // NOI18N
        bGuardar.setFocusable(false);
        bGuardar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bGuardar.setName("bGuardar"); // NOI18N
        bGuardar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        barraHerramientas.add(bGuardar);

        bBuscar.setAction(actionMap.get("buscar")); // NOI18N
        bBuscar.setFocusable(false);
        bBuscar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bBuscar.setName("bBuscar"); // NOI18N
        bBuscar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        barraHerramientas.add(bBuscar);

        bMagia.setAction(actionMap.get("magia")); // NOI18N
        bMagia.setFocusable(false);
        bMagia.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bMagia.setName("bMagia"); // NOI18N
        bMagia.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        barraHerramientas.add(bMagia);

        bPrestar.setAction(actionMap.get("prestar")); // NOI18N
        bPrestar.setFocusable(false);
        bPrestar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bPrestar.setName("bPrestar"); // NOI18N
        bPrestar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        barraHerramientas.add(bPrestar);

        bDevolver.setAction(actionMap.get("devolver")); // NOI18N
        bDevolver.setFocusable(false);
        bDevolver.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bDevolver.setName("bDevolver"); // NOI18N
        bDevolver.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        barraHerramientas.add(bDevolver);

        bTurboMagia.setAction(actionMap.get("turboMagia")); // NOI18N
        bTurboMagia.setFocusable(false);
        bTurboMagia.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bTurboMagia.setName("bTurboMagia"); // NOI18N
        bTurboMagia.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        barraHerramientas.add(bTurboMagia);

        lCodigo.setName("lCodigo"); // NOI18N

        lNombre.setName("lNombre"); // NOI18N

        tfCodigo.setEditable(false);
        tfCodigo.setName("tfCodigo"); // NOI18N

        lPropietario.setName("lPropietario"); // NOI18N

        tfPropietario.setName("tfPropietario"); // NOI18N

        tfNombre.setName("tfNombre"); // NOI18N

        lFoto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lFoto.setName("lFoto"); // NOI18N

        panelPestanas.setName("panelPestanas"); // NOI18N

        panelISBN.setName("panelISBN"); // NOI18N

        lISBN.setName("lISBN"); // NOI18N

        lTitulo.setName("lTitulo"); // NOI18N

        lAutor.setName("lAutor"); // NOI18N

        lIdioma.setName("lIdioma"); // NOI18N

        lEdicion.setName("lEdicion"); // NOI18N

        lPublicacion.setName("lPublicacion"); // NOI18N

        lDescripcion.setName("lDescripcion"); // NOI18N

        lEncuadernacion.setName("lEncuadernacion"); // NOI18N

        lPrecio.setName("lPrecio"); // NOI18N

        lMaterias.setName("lMaterias"); // NOI18N

        lCDU.setName("lCDU"); // NOI18N

        tfEncuadernacion.setName("tfEncuadernacion"); // NOI18N

        tfISBN.setName("tfISBN"); // NOI18N

        tfTitulo.setName("tfTitulo"); // NOI18N

        tfAutor.setName("tfAutor"); // NOI18N

        tfIdioma.setName("tfIdioma"); // NOI18N

        tfEdicion.setName("tfEdicion"); // NOI18N

        tfPublicacion.setName("tfPublicacion"); // NOI18N

        tfDescripcion.setName("tfDescripcion"); // NOI18N

        tfPrecio.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        tfPrecio.setName("tfPrecio"); // NOI18N

        tfMaterias.setName("tfMaterias"); // NOI18N

        tfCDU.setName("tfCDU"); // NOI18N

        lColeccion.setName("lColeccion"); // NOI18N

        tfColeccion.setName("tfColeccion"); // NOI18N

        javax.swing.GroupLayout panelISBNLayout = new javax.swing.GroupLayout(panelISBN);
        panelISBN.setLayout(panelISBNLayout);
        panelISBNLayout.setHorizontalGroup(
            panelISBNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelISBNLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelISBNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lEncuadernacion)
                    .addComponent(lISBN)
                    .addComponent(lTitulo)
                    .addComponent(lAutor)
                    .addComponent(lIdioma)
                    .addComponent(lEdicion)
                    .addComponent(lPublicacion)
                    .addComponent(lDescripcion)
                    .addComponent(lMaterias)
                    .addComponent(lColeccion))
                .addGap(15, 15, 15)
                .addGroup(panelISBNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelISBNLayout.createSequentialGroup()
                        .addComponent(tfMaterias, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lCDU)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfCDU, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelISBNLayout.createSequentialGroup()
                        .addComponent(tfISBN, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lPrecio)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tfPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tfTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                    .addComponent(tfAutor, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                    .addComponent(tfIdioma, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                    .addComponent(tfEdicion, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                    .addComponent(tfPublicacion, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                    .addComponent(tfDescripcion, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                    .addComponent(tfEncuadernacion, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                    .addComponent(tfColeccion, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelISBNLayout.setVerticalGroup(
            panelISBNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelISBNLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelISBNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lISBN)
                    .addComponent(tfPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lPrecio)
                    .addComponent(tfISBN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelISBNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lTitulo)
                    .addComponent(tfTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelISBNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lAutor)
                    .addComponent(tfAutor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelISBNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lIdioma)
                    .addComponent(tfIdioma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelISBNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lEdicion)
                    .addComponent(tfEdicion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelISBNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lPublicacion)
                    .addComponent(tfPublicacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelISBNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lDescripcion)
                    .addComponent(tfDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelISBNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lEncuadernacion)
                    .addComponent(tfEncuadernacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelISBNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lMaterias)
                    .addComponent(tfCDU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lCDU)
                    .addComponent(tfMaterias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelISBNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lColeccion)
                    .addComponent(tfColeccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(119, Short.MAX_VALUE))
        );

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.codeko.apps.campanilla.ignotus.IgnotusApp.class).getContext().getResourceMap(IgnotusView.class);
        panelPestanas.addTab(resourceMap.getString("panelISBN.TabConstraints.tabTitle"), panelISBN); // NOI18N

        panelValoracion.setName("panelValoracion"); // NOI18N

        lAutoresRelacionaos.setName("lAutoresRelacionaos"); // NOI18N

        lLibrosRelacionaos.setName("lLibrosRelacionaos"); // NOI18N

        lObservaciones.setName("lObservaciones"); // NOI18N

        lValoracion.setName("lValoracion"); // NOI18N

        slValoracion.setMajorTickSpacing(1);
        slValoracion.setMaximum(10);
        slValoracion.setMinorTickSpacing(1);
        slValoracion.setPaintLabels(true);
        slValoracion.setPaintTicks(true);
        slValoracion.setSnapToTicks(true);
        slValoracion.setValue(4);
        slValoracion.setName("slValoracion"); // NOI18N
        slValoracion.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slValoracionStateChanged(evt);
            }
        });

        tfAutoresRelacionaos.setName("tfAutoresRelacionaos"); // NOI18N

        tfLibrosRelacionaos.setName("tfLibrosRelacionaos"); // NOI18N

        spObservaciones.setName("spObservaciones"); // NOI18N

        taObservaciones.setColumns(20);
        taObservaciones.setLineWrap(true);
        taObservaciones.setRows(5);
        taObservaciones.setWrapStyleWord(true);
        taObservaciones.setName("taObservaciones"); // NOI18N
        spObservaciones.setViewportView(taObservaciones);

        lIcoValoracion.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lIcoValoracion.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lIcoValoracion.setName("lIcoValoracion"); // NOI18N

        javax.swing.GroupLayout panelValoracionLayout = new javax.swing.GroupLayout(panelValoracion);
        panelValoracion.setLayout(panelValoracionLayout);
        panelValoracionLayout.setHorizontalGroup(
            panelValoracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelValoracionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelValoracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(spObservaciones, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                    .addComponent(lLibrosRelacionaos, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelValoracionLayout.createSequentialGroup()
                        .addComponent(lAutoresRelacionaos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelValoracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfAutoresRelacionaos, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                            .addComponent(tfLibrosRelacionaos, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelValoracionLayout.createSequentialGroup()
                        .addGroup(panelValoracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lValoracion)
                            .addComponent(lObservaciones))
                        .addGap(42, 42, 42)
                        .addGroup(panelValoracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lIcoValoracion, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
                            .addComponent(slValoracion, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE))))
                .addContainerGap())
        );
        panelValoracionLayout.setVerticalGroup(
            panelValoracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelValoracionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelValoracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lAutoresRelacionaos)
                    .addComponent(tfAutoresRelacionaos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelValoracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lLibrosRelacionaos)
                    .addComponent(tfLibrosRelacionaos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelValoracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelValoracionLayout.createSequentialGroup()
                        .addComponent(slValoracion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lIcoValoracion, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelValoracionLayout.createSequentialGroup()
                        .addComponent(lValoracion)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lObservaciones)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spObservaciones, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelPestanas.addTab(resourceMap.getString("panelValoracion.TabConstraints.tabTitle"), panelValoracion); // NOI18N

        panelPrestamos.setName("panelPrestamos"); // NOI18N
        panelPrestamos.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                panelPrestamosComponentShown(evt);
            }
        });

        lPrestadoA.setName("lPrestadoA"); // NOI18N

        tfPrestadoA.setName("tfPrestadoA"); // NOI18N

        lFechaPrestamo.setName("lFechaPrestamo"); // NOI18N

        lTituloTablaPrestamos.setName("lTituloTablaPrestamos"); // NOI18N

        lHacePrestamo.setName("lHacePrestamo"); // NOI18N

        lObservacionesPrestamo.setName("lObservacionesPrestamo"); // NOI18N

        tfObservacionesPrestamo.setName("tfObservacionesPrestamo"); // NOI18N

        tfFechaPrestamo.setName("tfFechaPrestamo"); // NOI18N

        scrollTablaPrestamos.setName("scrollTablaPrestamos"); // NOI18N

        tablaPrestamos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {
                "Prestado A", "Fecha Prestamo", "Fecha Devolucion", "Observaciones"
            }
        ));
        tablaPrestamos.setName("tablaPrestamos"); // NOI18N
        scrollTablaPrestamos.setViewportView(tablaPrestamos);

        javax.swing.GroupLayout panelPrestamosLayout = new javax.swing.GroupLayout(panelPrestamos);
        panelPrestamos.setLayout(panelPrestamosLayout);
        panelPrestamosLayout.setHorizontalGroup(
            panelPrestamosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrestamosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPrestamosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrestamosLayout.createSequentialGroup()
                        .addGroup(panelPrestamosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lObservacionesPrestamo)
                            .addComponent(lPrestadoA)
                            .addComponent(lFechaPrestamo))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelPrestamosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelPrestamosLayout.createSequentialGroup()
                                .addComponent(tfFechaPrestamo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lHacePrestamo, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE))
                            .addComponent(tfObservacionesPrestamo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)
                            .addComponent(tfPrestadoA, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)))
                    .addComponent(scrollTablaPrestamos, javax.swing.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                    .addComponent(lTituloTablaPrestamos, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelPrestamosLayout.setVerticalGroup(
            panelPrestamosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrestamosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPrestamosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lPrestadoA)
                    .addComponent(tfPrestadoA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrestamosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lFechaPrestamo)
                    .addComponent(tfFechaPrestamo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lHacePrestamo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrestamosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lObservacionesPrestamo)
                    .addComponent(tfObservacionesPrestamo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lTituloTablaPrestamos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollTablaPrestamos, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelPestanas.addTab(resourceMap.getString("panelPrestamos.TabConstraints.tabTitle"), panelPrestamos); // NOI18N

        lTags.setName("lTags"); // NOI18N

        tfTags.setName("tfTags"); // NOI18N

        bAsignarFoto.setAction(actionMap.get("ponerPortada")); // NOI18N
        bAsignarFoto.setName("bAsignarFoto"); // NOI18N

        bBorrarPortada.setName("bBorrarPortada"); // NOI18N
        bBorrarPortada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bBorrarPortadaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(barraHerramientas, javax.swing.GroupLayout.DEFAULT_SIZE, 759, Short.MAX_VALUE)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lCodigo)
                            .addComponent(lNombre)
                            .addComponent(lTags))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfNombre, javax.swing.GroupLayout.DEFAULT_SIZE, 698, Short.MAX_VALUE)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(tfCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lPropietario)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfPropietario, javax.swing.GroupLayout.DEFAULT_SIZE, 539, Short.MAX_VALUE))
                            .addComponent(tfTags, javax.swing.GroupLayout.DEFAULT_SIZE, 698, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addComponent(panelPestanas, javax.swing.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(bAsignarFoto)
                                .addGap(18, 18, 18)
                                .addComponent(bBorrarPortada))
                            .addComponent(lFoto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(barraHerramientas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lCodigo)
                    .addComponent(tfCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lPropietario)
                    .addComponent(tfPropietario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lNombre)
                    .addComponent(tfNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lTags)
                    .addComponent(tfTags, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(lFoto, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE)
                        .addGap(5, 5, 5)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(bBorrarPortada)
                            .addComponent(bAsignarFoto)))
                    .addComponent(panelPestanas, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE))
                .addContainerGap())
        );
        resourceMap.injectComponents(mainPanel);

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setName("fileMenu"); // NOI18N

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        menuUtilidades.setName("menuUtilidades"); // NOI18N

        menuHaceCopia.setAction(actionMap.get("copiaDeSeguridad")); // NOI18N
        menuHaceCopia.setName("menuHaceCopia"); // NOI18N
        menuUtilidades.add(menuHaceCopia);

        menuImportaCopia.setAction(actionMap.get("importarCopia")); // NOI18N
        menuImportaCopia.setName("menuImportaCopia"); // NOI18N
        menuUtilidades.add(menuImportaCopia);

        menuTurboMagia.setAction(actionMap.get("isbnMogollon")); // NOI18N
        menuTurboMagia.setName("menuTurboMagia"); // NOI18N
        menuUtilidades.add(menuTurboMagia);

        menuBar.add(menuUtilidades);

        helpMenu.setName("helpMenu"); // NOI18N

        menuDedicatoria.setAction(actionMap.get("mostrarDedicatoria")); // NOI18N
        menuDedicatoria.setName("menuDedicatoria"); // NOI18N
        helpMenu.add(menuDedicatoria);

        menuWebIgnotus.setAction(actionMap.get("irWeb")); // NOI18N
        menuWebIgnotus.setName("menuWebIgnotus"); // NOI18N
        helpMenu.add(menuWebIgnotus);

        menuLicencia.setAction(actionMap.get("mostrarLicencia")); // NOI18N
        menuLicencia.setName("menuLicencia"); // NOI18N
        helpMenu.add(menuLicencia);

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);
        resourceMap.injectComponents(menuBar);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 759, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusPanelLayout.createSequentialGroup()
                .addContainerGap(599, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
                .addGap(186, 186, 186))
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );
        resourceMap.injectComponents(statusPanel);

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
        setToolBar(barraHerramientas);
    }// </editor-fold>//GEN-END:initComponents
    private void bBorrarPortadaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bBorrarPortadaActionPerformed
        lFoto.setIcon(null);
        setArchivo(new File("no_hay_foto"));
    }//GEN-LAST:event_bBorrarPortadaActionPerformed

    private void slValoracionStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slValoracionStateChanged
        lIcoValoracion.setIcon(valoracionIcon[slValoracion.getValue()]);
        lIcoValoracion.setText(valoracionTexto[slValoracion.getValue()]);
    }//GEN-LAST:event_slValoracionStateChanged

    private void panelPrestamosComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_panelPrestamosComponentShown
        cargarPrestamos();
    }//GEN-LAST:event_panelPrestamosComponentShown

    @Action
    public void nuevo() {
        setLibro(null);
        limpiar();
        tfNombre.requestFocus();
    }

    @Action
    public boolean validar() {
        boolean ret = true;
        boolean fatal = false;
        String isbn = Util.procesarISBN(tfISBN.getText().trim());
        tfISBN.setText(isbn);
        //isbn=tfISBN.getText();
        StringBuilder msg = new StringBuilder("");
        if (tfNombre.getText().trim().equals("") && !tfTitulo.getText().trim().equals("")) {
            tfNombre.setText(tfTitulo.getText());
        } else if (tfNombre.getText().trim().equals("")) {
            msg.append(resourceMap.getString("validacion.nombreVacio"));
            msg.append("\n");
            ret = false;
            fatal = true;
        }

        if (isbn.trim().equals("") && !Util.esISBNValido(isbn)) {
            msg.append(resourceMap.getString("validacion.isbnMal"));
            msg.append("\n");
            ret = false;
        }
        if (!ret) {
            JOptionPane.showMessageDialog(this.getFrame(), msg.toString(), resourceMap.getString("validacion.title"), !fatal ? JOptionPane.ERROR_MESSAGE : JOptionPane.WARNING_MESSAGE);
        }
        return !fatal;
    }

    @Action
    public void borrar() {
        if (getLibro() != null) {
            int op = JOptionPane.showConfirmDialog(this.getFrame(), resourceMap.getString("advertenciaBorrado.text"), resourceMap.getString("advertenciaBorrado.title"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (op == JOptionPane.YES_OPTION) {
                if (getLibro().borrar()) {
                    limpiar();
                }
            }
        }
    }

    @Action
    public void guardar() {
        if (validar()) {
            Libro l = getLibro();
            if (l == null) {
                l = new Libro();
            }
            l.setDato("nombre", tfNombre.getText());
            l.setDato("propietario", tfPropietario.getText());
            l.setDato("titulo", tfTitulo.getText());
            l.setDato("observaciones", taObservaciones.getText());
            l.setDato("autor", tfAutor.getText());
            l.setDato("autores_relacionados", tfAutoresRelacionaos.getText());
            l.setDato("cdu", tfCDU.getText());
            l.setDato("descripcion", tfDescripcion.getText());
            l.setDato("edicion", tfEdicion.getText());
            l.setDato("encuadernacion", tfEncuadernacion.getText());
            l.setDato("isbn", tfISBN.getText());
            l.setDato("idioma", tfIdioma.getText());
            l.setDato("libros_relacionados", tfLibrosRelacionaos.getText());
            l.setDato("materias", tfMaterias.getText());
            l.setDato("precio", tfPrecio.getText());
            l.setDato("publicacion", tfPublicacion.getText());
            l.setDato("tags", tfTags.getText());
            l.setDato("coleccion", tfColeccion.getText());
            l.setDato("valoracion", slValoracion.getValue());
            //Según el origen del libro puede ser que el archivo este asignado al libro
            //TODO No me convence mucho esta forma de asignar los archivos
            if (getArchivo() != null) {
                l.setArchivo(getArchivo());
            } else {
                setArchivo(l.getArchivo());
            }
            l.guardar();
            if (l.getPrestamo() != null && l.getPrestamo().getId() > 0) {
                Prestamo p = l.getPrestamo();
                GregorianCalendar cal = new GregorianCalendar();

                if (tfFechaPrestamo.getDate() != null) {
                    cal.setTime(tfFechaPrestamo.getDate());
                    p.setFechaPrestamo(cal);
                }
                p.setPrestadoA(tfPrestadoA.getText());
                p.setObservaciones(tfObservacionesPrestamo.getText());
                p.guardar();
            }
            setArchivo(null);
            tfCodigo.setText(l.getDatoString("id"));
            setLibroSinCargar(l);
            cargarPrestamoActual();
        }
    }

    @Action
    public void buscar() {
        JDialog busqueda = new JDialog(this.getFrame());
        busqueda.setTitle(resourceMap.getString("ventanaBuscar.title"));
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int ancho = 600;
        int alto = 450;
        busqueda.setBounds(((int) (screenSize.getWidth() / 2) - (ancho / 2)), (int) ((screenSize.getHeight() / 2) - (alto / 2)), ancho, alto);
        busqueda.setLayout(new BorderLayout());
        busqueda.add(new PanelBusquedas(), BorderLayout.CENTER);
        busqueda.setVisible(true);
    }

    @Action(block = Task.BlockingScope.APPLICATION)
    public Task magia() {
        Libro l = getLibro();
        if (l == null) {
            l = new Libro();
        }
        if (tfISBN.getText().trim().equals("")) {
            String str = JOptionPane.showInputDialog(getFrame(), "Introduce el ISBN del libro");
            if (str != null) {
                tfISBN.setText(str);
            } else {
                return null;
            }
        }
        l.setDato("isbn", tfISBN.getText().trim());
        setLibro(l);
        return new MagiaTask(getApplication(), l.getDatoString("isbn")) {

            @Override
            public void succeeded(ResultadoMagia result) {
                if (result != null) {
                    if (result.getCodigo() > 0) {
                        getLibro().aplicarDatos(result.getLibro());
                        getLibro().guardar();
                        ((IgnotusView) IgnotusApp.getApplication().getMainView()).setLibro(result.getLibro());
                    } else {
                        JOptionPane.showMessageDialog(IgnotusApp.getApplication().getMainFrame(), result.getMensaje(), "Error", JOptionPane.ERROR_MESSAGE);
                    }


                }
            }
        };
    }

    @Action
    public void prestar() {
        if (getLibro() != null && getLibro().existe()) {
            //Si está prestado no hacemos nada
            if (getLibro().getPrestamo().estaPrestado()) {
                return;
            }
            //TODO Traducir
            Object quien = JOptionPane.showInputDialog(getFrame(), "¿A quien le prestas el libro?", "Prestar libro", JOptionPane.QUESTION_MESSAGE);
            if (quien != null) {
                getLibro().getPrestamo().setFechaPrestamo(new GregorianCalendar());
                getLibro().getPrestamo().setPrestadoA(quien.toString());
                getLibro().getPrestamo().guardar();
                cargarPrestamoActual();
            }
        }
    }

    private void cargarPrestamoActual() {
        Prestamo p = getLibro().getPrestamo();
        if (p.estaPrestado()) {
            tfPrestadoA.setText(p.getPrestadoA());
            tfFechaPrestamo.setDate(p.getFechaPrestamo().getTime());
            tfObservacionesPrestamo.setText(p.getObservaciones());
            //TODO I18N
            lHacePrestamo.setText("Hace " + Fechas.calcularDiferencia(p.getFechaPrestamo(), new GregorianCalendar(), true, false, false));
        } else {
            tfPrestadoA.setText("");
            tfFechaPrestamo.setDate(null);
            tfObservacionesPrestamo.setText("");
            lHacePrestamo.setText("");
        }
        ActionMap actionMap = org.jdesktop.application.Application.getInstance(com.codeko.apps.campanilla.ignotus.IgnotusApp.class).getContext().getActionMap(IgnotusView.class, this);
        actionMap.get("prestar").setEnabled(!p.estaPrestado());
        actionMap.get("devolver").setEnabled(p.estaPrestado());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bAsignarFoto;
    private javax.swing.JButton bBorrar;
    private javax.swing.JButton bBorrarPortada;
    private javax.swing.JButton bBuscar;
    private javax.swing.JButton bDevolver;
    private javax.swing.JButton bGuardar;
    private javax.swing.JButton bMagia;
    private javax.swing.JButton bNuevo;
    private javax.swing.JButton bPrestar;
    private javax.swing.JToggleButton bTurboMagia;
    private javax.swing.JToolBar barraHerramientas;
    private javax.swing.JLabel lAutor;
    private javax.swing.JLabel lAutoresRelacionaos;
    private javax.swing.JLabel lCDU;
    private javax.swing.JLabel lCodigo;
    private javax.swing.JLabel lColeccion;
    private javax.swing.JLabel lDescripcion;
    private javax.swing.JLabel lEdicion;
    private javax.swing.JLabel lEncuadernacion;
    private javax.swing.JLabel lFechaPrestamo;
    private javax.swing.JLabel lFoto;
    private javax.swing.JLabel lHacePrestamo;
    private javax.swing.JLabel lISBN;
    private javax.swing.JLabel lIcoValoracion;
    private javax.swing.JLabel lIdioma;
    private javax.swing.JLabel lLibrosRelacionaos;
    private javax.swing.JLabel lMaterias;
    private javax.swing.JLabel lNombre;
    private javax.swing.JLabel lObservaciones;
    private javax.swing.JLabel lObservacionesPrestamo;
    private javax.swing.JLabel lPrecio;
    private javax.swing.JLabel lPrestadoA;
    private javax.swing.JLabel lPropietario;
    private javax.swing.JLabel lPublicacion;
    private javax.swing.JLabel lTags;
    private javax.swing.JLabel lTitulo;
    private javax.swing.JLabel lTituloTablaPrestamos;
    private javax.swing.JLabel lValoracion;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem menuDedicatoria;
    private javax.swing.JMenuItem menuHaceCopia;
    private javax.swing.JMenuItem menuImportaCopia;
    private javax.swing.JMenuItem menuLicencia;
    private javax.swing.JMenuItem menuTurboMagia;
    private javax.swing.JMenu menuUtilidades;
    private javax.swing.JMenuItem menuWebIgnotus;
    private javax.swing.JPanel panelISBN;
    private javax.swing.JTabbedPane panelPestanas;
    private javax.swing.JPanel panelPrestamos;
    private javax.swing.JPanel panelValoracion;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JScrollPane scrollTablaPrestamos;
    private javax.swing.JSlider slValoracion;
    private javax.swing.JScrollPane spObservaciones;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JTextArea taObservaciones;
    private javax.swing.JTable tablaPrestamos;
    private javax.swing.JTextField tfAutor;
    private javax.swing.JTextField tfAutoresRelacionaos;
    private javax.swing.JTextField tfCDU;
    private javax.swing.JTextField tfCodigo;
    private javax.swing.JTextField tfColeccion;
    private javax.swing.JTextField tfDescripcion;
    private javax.swing.JTextField tfEdicion;
    private javax.swing.JTextField tfEncuadernacion;
    private com.toedter.calendar.JDateChooser tfFechaPrestamo;
    private javax.swing.JTextField tfISBN;
    private javax.swing.JTextField tfIdioma;
    private javax.swing.JTextField tfLibrosRelacionaos;
    private javax.swing.JTextField tfMaterias;
    private javax.swing.JTextField tfNombre;
    private javax.swing.JTextField tfObservacionesPrestamo;
    private javax.swing.JTextField tfPrecio;
    private javax.swing.JTextField tfPrestadoA;
    private javax.swing.JTextField tfPropietario;
    private javax.swing.JTextField tfPublicacion;
    private javax.swing.JTextField tfTags;
    private javax.swing.JTextField tfTitulo;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private final Icon[] valoracionIcon = new Icon[11];
    private final String[] valoracionTexto = new String[11];
    private int busyIconIndex = 0;
    private JDialog aboutBox;

    private void limpiar() {
        taObservaciones.setText("");
        tfAutor.setText("");
        tfAutoresRelacionaos.setText("");
        tfCDU.setText("");
        tfCodigo.setText("");
        tfDescripcion.setText("");
        tfEdicion.setText("");
        tfEncuadernacion.setText("");
        tfISBN.setText("");
        tfIdioma.setText("");
        tfLibrosRelacionaos.setText("");
        tfMaterias.setText("");
        tfNombre.setText("");
        tfPrecio.setText("");
        tfPropietario.setText("");
        tfPublicacion.setText("");
        tfTags.setText("");
        tfTitulo.setText("");
        slValoracion.setValue(5);
        lFoto.setIcon(null);
        tfColeccion.setText("");
        limpiarPrestamos();
    }

    private void cargarLibro(Libro l) {
        limpiar();
        if (l == null) {
            return;
        }
        taObservaciones.setText(l.getDatoString("observaciones"));
        tfAutor.setText(l.getDatoString("autor"));
        tfAutoresRelacionaos.setText(l.getDatoString("autores_relacionados"));
        tfCDU.setText(l.getDatoString("cdu"));
        tfCodigo.setText(l.getDatoString("id"));
        tfDescripcion.setText(l.getDatoString("descripcion"));
        tfEdicion.setText(l.getDatoString("edicion"));
        tfEncuadernacion.setText(l.getDatoString("encuadernacion"));
        tfISBN.setText(l.getDatoString("isbn"));
        tfIdioma.setText(l.getDatoString("idioma"));
        tfLibrosRelacionaos.setText(l.getDatoString("libros_relacionados"));
        tfMaterias.setText(l.getDatoString("materias"));
        tfNombre.setText(l.getDatoString("nombre"));
        tfPrecio.setText(l.getDatoString("precio"));
        tfPropietario.setText(l.getDatoString("propietario"));
        tfPublicacion.setText(l.getDatoString("publicacion"));
        tfTags.setText(l.getDatoString("tags"));
        tfTitulo.setText(l.getDatoString("titulo"));
        slValoracion.setValue(Util.getInt(l.getDato("valoracion")));
        mostrarFoto();
        tfColeccion.setText(l.getDatoString("coleccion"));
        //TODO Esto es un chapú para que no permita prestar libros todavía no creados (se ha cargado desde magia)
        if (l.existe()) {
            //Si estamos mostrando la pestaña de prestamos la cargamos directamente
            if (panelPestanas.getSelectedComponent() == panelPrestamos) {
                cargarPrestamos();
            }
            cargarPrestamoActual();
        }
    }

    private void cargarPrestamos() {
        //Si no hemos cargado ya los prestamos los cargamos
        if (!isPrestamosCargados() && getLibro() != null && getLibro().existe()) {
            setPrestamosCargados(true);
            Vector<Prestamo> p = Prestamo.getPrestamosLibro(getLibro().getId());
            for (Prestamo pres : p) {
                ((DefaultTableModel) tablaPrestamos.getModel()).addRow(pres.getVectorDatos());
            }
            tablaPrestamos.updateUI();
        }

    }

    private void limpiarPrestamos() {
        setPrestamosCargados(false);
        ((DefaultTableModel) tablaPrestamos.getModel()).getDataVector().clear();
        tablaPrestamos.updateUI();
        tfPrestadoA.setText("");
        tfFechaPrestamo.setDate(null);
        tfObservacionesPrestamo.setText("");
        lHacePrestamo.setText("");
        ActionMap actionMap = org.jdesktop.application.Application.getInstance(com.codeko.apps.campanilla.ignotus.IgnotusApp.class).getContext().getActionMap(IgnotusView.class, this);
        actionMap.get("prestar").setEnabled(false);
        actionMap.get("devolver").setEnabled(false);
    }

    @Action
    public void devolver() {
        if (getLibro() != null) {
            //Si no está prestado no hacemos nada
            if (!getLibro().getPrestamo().estaPrestado()) {
                return;
            }
            //TODO Traducir
            //TODO Estaría bien poder ponerle un texto de ayer, hoy, el miercoles...
            //Object cuando=JOptionPane.showInputDialog(getFrame(), "¿Cuando te ha devuelto el libro?","Devolver libro", JOptionPane.QUESTION_MESSAGE,null,null,new GregorianCalendar());
            JDateChooser fecha = new JDateChooser(new Date());
            //TODO I18N
            int op = JOptionPane.showConfirmDialog(this.getFrame(), fecha, "Fecha de devolución", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (op == JOptionPane.OK_OPTION) {
                if (fecha.getDate() != null) {
                    //TODO Recuperar fecha de devolucion
                    GregorianCalendar fechaDev = new GregorianCalendar();
                    fechaDev.setTime(fecha.getDate());
                    getLibro().getPrestamo().setFechaDevolucion(fechaDev);
                    getLibro().getPrestamo().guardar();
                    //Reseteamos el prestamo actual porque este ya lo ha devuelto
                    getLibro().setPrestamo(null);
                    limpiarPrestamos();
                    cargarPrestamoActual();
                    cargarPrestamos();
                }
            }
        }
    }

    @Action
    public void mostrarDedicatoria() {
        //TODO I18N
        JOptionPane.showMessageDialog(this.getFrame(), new PanelDedicatoria(), "Dedicatoria", JOptionPane.PLAIN_MESSAGE);

    }

    @Action
    public void irWeb() {
        try {
            Desktop.getDesktop().browse(new URI("http://ignotus.thelittlemolewoman.es"));
        } catch (Exception ex) {
            //TODO Implementar error y I18N
            Logger.getLogger(IgnotusView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Action
    public void mostrarLicencia() {
        //TODO I18N
        JOptionPane.showMessageDialog(this.getFrame(), new PanelLicencia(), "Licencia", JOptionPane.PLAIN_MESSAGE);
    }

    @Action
    public Task copiaDeSeguridad() {
        return new CopiaDeSeguridadTask(getApplication());
    }

    @Action
    public Task importarCopia() {
        //Lo primero que hacemos es quitar la ficha actual para evitar problemas una vez restaurada la copia
        nuevo();
        return new ImportarCopiaTask(getApplication());
    }

    @Action
    public void isbnMogollon() {
        //TODO I18N
        PanelTurboISBN ptm = new PanelTurboISBN();
        JDialog dlg = new JDialog(this.getFrame(), "¡¡I.S.B.N. a mogollón!!", ModalityType.MODELESS);

        dlg.getContentPane().add(ptm, BorderLayout.CENTER);
        dlg.pack();
        Point loc = this.getFrame().getLocation();
        Dimension dim = this.getFrame().getSize();
        Dimension dlgSize = dlg.getSize();
        dlg.setLocation((int) (loc.getX() + ((dim.getWidth() / 2) - (dlgSize.getWidth() / 2))), (int) (loc.getY() + ((dim.getHeight() / 2) - (dlgSize.getHeight() / 2))));
        dlg.setVisible(true);
    }

    @Action(block = Task.BlockingScope.APPLICATION)
    public Task ponerPortada() {
        return new PonerPortadaTask(getApplication());
    }

    private class PonerPortadaTask extends org.jdesktop.application.Task<File, Void> {

        int operacion = 0;
        static final int GOOGLE = 1;
        static final int ARCHIVO = 2;
        String texto = "";

        PonerPortadaTask(org.jdesktop.application.Application app) {
            super(app);
            int op = JOptionPane.showOptionDialog(getFrame(), "¿Como quieres poner la portada?", "Poner portada", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Dimelo tu", "Desde archivo", "Cancelar"}, "Dimelo tu");
            if (op == JOptionPane.YES_OPTION) {
                operacion = GOOGLE;
            } else if (op == JOptionPane.NO_OPTION) {
                operacion = ARCHIVO;
            }
            texto = tfTitulo.getText().trim();
        }

        @Override
        protected File doInBackground() {
            File f = null;
            if (operacion > 0) {
                try {
                    switch (operacion) {
                        case GOOGLE:
                            f = magiaImagen(texto);
                            break;
                        case ARCHIVO:
                            JFileChooser jfc = new JFileChooser();
                            int op = jfc.showOpenDialog(getFrame());
                            if (op == JFileChooser.APPROVE_OPTION) {
                                f = (jfc.getSelectedFile());
                            }
                            break;
                    }
                } catch (MalformedURLException ex) {
                    Logger.getLogger(IgnotusView.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(IgnotusView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return f;
        }

        @Override
        protected void succeeded(File result) {
            if (result != null && getLibro() != null) {
                getLibro().setArchivo(result);
                mostrarFoto();
            } else {
                setMessage("No se ha encontrado ninguna portada :(");
            }
        }

        private int confirmarImagen(String urlImagen) throws MalformedURLException {
            //TODO Habria que poner en algun sitio la opcion de carga la imagen por el ISBN
            URL url = new URL(urlImagen);
            ImageIcon img = new ImageIcon(url);
            Dimension max = new Dimension(400, 400);
            Dimension real = new Dimension(img.getIconWidth(), img.getIconHeight());
            Dimension escalada = Util.getTamanoEscalado(real, max);
            img = new ImageIcon(img.getImage().getScaledInstance((int) escalada.getWidth(), (int) escalada.getHeight(), Image.SCALE_SMOOTH));
            JLabel l = new JLabel(img, JLabel.CENTER);
            return JOptionPane.showConfirmDialog(IgnotusApp.getApplication().getMainFrame(), l, resourceMap.getString("magia.seleccionarPortada.title"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        }

        private File magiaImagen(String titulo) throws MalformedURLException, IOException {
            titulo = URLEncoder.encode("\"" + titulo + "\"", "UTF-8");
            //TODO I18N
            setMessage("Buscando portada en google...");
            URL url = new URL("http://images.google.com/images?q=" + titulo + "&imgsz=small|medium|large|xlarge");
            setMessage("Buscando portada en google: conectando...");
            URLConnection urlCon = url.openConnection();
            //con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlCon.setRequestProperty("User-Agent", "MyBNavigator");
            BufferedReader in = new BufferedReader(new InputStreamReader(urlCon.getInputStream(), Charset.forName("ISO-8859-1")));
            String inputLine;
            // boolean encontrado = false;
            StringBuilder sb = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
            }
            inputLine = sb.toString();
            //De cada linea tenemos que seguir hasta encontrar "<a href="/imgres?imgurl=http://"
            String busqueda = "<a href=/imgres?imgurl=";
            setMessage("Buscando portada en google: analizando...");
            while (inputLine.indexOf(busqueda) != -1) {
                int posBusqueda = inputLine.indexOf(busqueda) + busqueda.length();
                int posFinal = inputLine.indexOf("&", posBusqueda);
                String urlImagen = inputLine.substring(posBusqueda, posFinal);
                //Una vez que tenemos la imagen la mostramos y vemos si es valida
                switch (confirmarImagen(urlImagen)) {
                    case JOptionPane.YES_OPTION:
                        setMessage("Descargando imagen...");
                        URL urlImg = new URL(urlImagen);
                        //URLConnection urlConImg = urlImg.openConnection();
                        String ext = urlImagen.substring(urlImagen.lastIndexOf(".") + 1);
                        File f = File.createTempFile("Ignotus", "." + ext);
                        BufferedImage image = ImageIO.read(urlImg);
                        FileOutputStream outer = new FileOutputStream(f);
                        ImageIO.write(image, ext, outer);
                        outer.close();
                        in.close();
                        return f;
                    case JOptionPane.CANCEL_OPTION:
                        in.close();
                        return null;
                    default:
                        inputLine = inputLine.substring(posBusqueda + busqueda.length());
                }
            }
            return null;
        }
    }

    @Action
    public Task turboMagia() {
        if (turboMagia != null && !turboMagia.isDone()) {

            turboMagia.setTerminar(true);

        } else {
            turboMagia = new TurboMagiaTask(IgnotusApp.getApplication());
            return turboMagia;
        }
        return null;
    }

    public void turboMagiaTerminada() {
        bTurboMagia.setSelected(false);
    }
}
