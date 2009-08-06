package com.codeko.apps.campanilla.ignotus;

import org.jdesktop.application.AbstractBean;

/**
 * Copyright Codeko Informática 2008
 * www.codeko.com
 * @author Codeko
 */
public class IgnotusBean extends AbstractBean {

    @Override
    public void firePropertyChange(String nombre, Object antiguo, Object nuevo) {
        super.firePropertyChange(nombre, antiguo, nuevo);
    }
}
