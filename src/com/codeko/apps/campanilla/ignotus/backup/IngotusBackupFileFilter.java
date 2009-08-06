/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.codeko.apps.campanilla.ignotus.backup;

import java.io.File;
import javax.swing.filechooser.FileFilter;



/**
 *
 * @author opik
 */

public class IngotusBackupFileFilter extends FileFilter {

    //Accept all directories and all gif, jpg, tiff, or png files.
    public boolean accept(File f) {       
        return f.toString().endsWith(".ignotus") || f.isDirectory() ;
    }

    //The description of this filter
    public String getDescription() {
        //TODO I18N
        return "Copia de seguridad de ignotus";
    }
}