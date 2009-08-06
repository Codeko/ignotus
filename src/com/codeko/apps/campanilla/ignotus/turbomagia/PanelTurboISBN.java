/*
 * PanelTurboMagia.java
 *
 * Created on 17 de abril de 2008, 21:15
 */

package com.codeko.apps.campanilla.ignotus.turbomagia;

import com.codeko.apps.campanilla.ignotus.Libro;
import com.codeko.apps.campanilla.ignotus.util.Util;
import java.awt.event.KeyEvent;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.jdesktop.application.Action;

/**
 *
 * @author  campa
 */
public class PanelTurboISBN extends javax.swing.JPanel {
    DefaultListModel modelo=new DefaultListModel();
    /** Creates new form PanelTurboMagia */
    public PanelTurboISBN() {
        initComponents();
        listaLibros.setCellRenderer(new PanelRendererLibroTurboISBN());
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        taAyuda = new javax.swing.JTextArea();
        lPropietario = new javax.swing.JLabel();
        lTags = new javax.swing.JLabel();
        lISBN = new javax.swing.JLabel();
        bIntroducir = new javax.swing.JButton();
        tfPropietario = new javax.swing.JTextField();
        tfTags = new javax.swing.JTextField();
        tfISBN = new javax.swing.JTextField();
        bSalir = new javax.swing.JButton();
        scrollLista = new javax.swing.JScrollPane();
        listaLibros = new javax.swing.JList();
        panelRendererLibroTurboISBN1 = new com.codeko.apps.campanilla.ignotus.turbomagia.PanelRendererLibroTurboISBN();

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        taAyuda.setColumns(20);
        taAyuda.setEditable(false);
        taAyuda.setLineWrap(true);
        taAyuda.setRows(5);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.codeko.apps.campanilla.ignotus.IgnotusApp.class).getContext().getResourceMap(PanelTurboISBN.class);
        taAyuda.setText(resourceMap.getString("taAyuda.text")); // NOI18N
        taAyuda.setWrapStyleWord(true);
        taAyuda.setName("taAyuda"); // NOI18N
        taAyuda.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(taAyuda, gridBagConstraints);

        lPropietario.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lPropietario.setText(resourceMap.getString("lPropietario.text")); // NOI18N
        lPropietario.setName("lPropietario"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(lPropietario, gridBagConstraints);

        lTags.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lTags.setText(resourceMap.getString("lTags.text")); // NOI18N
        lTags.setName("lTags"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(lTags, gridBagConstraints);

        lISBN.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lISBN.setText(resourceMap.getString("lISBN.text")); // NOI18N
        lISBN.setName("lISBN"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(lISBN, gridBagConstraints);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(com.codeko.apps.campanilla.ignotus.IgnotusApp.class).getContext().getActionMap(PanelTurboISBN.class, this);
        bIntroducir.setAction(actionMap.get("introducir")); // NOI18N
        bIntroducir.setName("bIntroducir"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        add(bIntroducir, gridBagConstraints);

        tfPropietario.setText(resourceMap.getString("tfPropietario.text")); // NOI18N
        tfPropietario.setName("tfPropietario"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        add(tfPropietario, gridBagConstraints);

        tfTags.setText(resourceMap.getString("tfTags.text")); // NOI18N
        tfTags.setName("tfTags"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        add(tfTags, gridBagConstraints);

        tfISBN.setText(resourceMap.getString("tfISBN.text")); // NOI18N
        tfISBN.setName("tfISBN"); // NOI18N
        tfISBN.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tfISBNKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        add(tfISBN, gridBagConstraints);

        bSalir.setAction(actionMap.get("salir")); // NOI18N
        bSalir.setName("bSalir"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        add(bSalir, gridBagConstraints);

        scrollLista.setName("scrollLista"); // NOI18N

        listaLibros.setModel(modelo);
        listaLibros.setName("listaLibros"); // NOI18N
        scrollLista.setViewportView(listaLibros);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(scrollLista, gridBagConstraints);

        panelRendererLibroTurboISBN1.setName("panelRendererLibroTurboISBN1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(panelRendererLibroTurboISBN1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void tfISBNKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfISBNKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            introducir();
        }
    }//GEN-LAST:event_tfISBNKeyPressed

    @Action
    public void salir() {
        SwingUtilities.getWindowAncestor(this).dispose();
    }

    @Action
    public void introducir() {
        String propietario=tfPropietario.getText().trim();
        String tags=tfTags.getText().trim();
        String isbn=Util.procesarISBN(tfISBN.getText().trim());
        if(Util.esISBNValido(isbn)){
            Libro l=new Libro();
            l.setDato("propietario", propietario);
            l.setDato("tags", tags);
            l.setDato("isbn", isbn);
            l.guardar();
            addLibro(l);
            tfISBN.setText("");
            tfISBN.requestFocus();
        }else{
            tfISBN.selectAll();
            tfISBN.requestFocus();
            //TODO I18N
            JOptionPane.showMessageDialog(this, "El I.S.B.N. introducido no es correcto.\nDebe tener 10 o 13 caracteres.","Error",JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addLibro(Libro l){
        modelo.insertElementAt(l,0);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bIntroducir;
    private javax.swing.JButton bSalir;
    private javax.swing.JLabel lISBN;
    private javax.swing.JLabel lPropietario;
    private javax.swing.JLabel lTags;
    private javax.swing.JList listaLibros;
    private com.codeko.apps.campanilla.ignotus.turbomagia.PanelRendererLibroTurboISBN panelRendererLibroTurboISBN1;
    private javax.swing.JScrollPane scrollLista;
    private javax.swing.JTextArea taAyuda;
    private javax.swing.JTextField tfISBN;
    private javax.swing.JTextField tfPropietario;
    private javax.swing.JTextField tfTags;
    // End of variables declaration//GEN-END:variables
    
}