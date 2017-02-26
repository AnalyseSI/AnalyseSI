/*
 * 10/21/2003 - 16:34:49
 *
 * AnalyseSave.java - 
 * Copyright (C) 2003 Dreux Loic
 * dreuxl@free.fr
 *  
 *
 * Modifications : 
 * ---------------
 *   @author : Bruno Dabo <bruno.dabo@lywoonsoftware.com>
 *   @date   : 2009 jan 23
 *   @Objet  : Gérer la popup de demande de sauvegarde
 *   
 *   Mars 2009 : Début timide du multi-langue
 *
 * 2009-12-03 bug #489249 : @author Bruno Dabo <bruno.dabo@lywoonsoftware.com>
 * 2009-12-03 bug #489249 : Extension "asi" lors de la sauvegarde
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.analyse.core.util.save;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.analyse.core.gui.AnalyseFrame;
import org.analyse.core.modules.AnalyseModule;
import org.analyse.core.save.FiltreASI;
import org.analyse.core.util.Constantes;
import org.analyse.core.util.GUIUtilities;
import org.analyse.core.util.Utilities;
import org.analyse.main.Main;

/**
 * Cette class contient toute les méthodes pour gérer la sauvegarde des données :
 * boite de dialogue, gestion des différents filtres de sauvegardes, ...
 */
public class AnalyseSave {

    private static final String[] options = { 
	    	Utilities.getLangueMessage(Constantes.MESSAGE_OUI) , 
	    	Utilities.getLangueMessage(Constantes.MESSAGE_NON), 
	    	Utilities.getLangueMessage( Constantes.MESSAGE_ANNULER) 
    	};

    private boolean isSave;

    public boolean isSave() {
		return isSave;
	}

	public void setSave(boolean isSave) {
		this.isSave = isSave;
	}

	private String fileName;
	private boolean newOption = false ; 

    private JFileChooser chooser;

    private AnalyseFrame frame;

    private List<FiltreASI> filtres;

    public AnalyseSave(AnalyseFrame frame)
    {
        this.frame = frame;
        this.setSave ( true ) ; 

        initFilter();
        initFileChooser();
    }
    
    public int  closeProgram()
    {
    	
    	return ( this.popupExit ()  ) ;  
    	
    }

    /**
     * Sauvegarde les données dans un fichier, si le document a déjà été
     * sauvegardé une fois, le nom du fichier ne change pas, sinon, la méthode
     * saveAs est lancée.
     */
    public void save() {
    	
    	if ( this.fileName == null ) {
    		 saveAs();
        	return;
        
    	}
    	
        if ( ! this.isSave () ) {
        	 saveAs();
             return;
        }
        
        this.setSave ( true );
        
        AnalyseFilter af = getAnalyseFilter();
        if (af != null && af.canSave()) {
        	
        	// Bug #352415
        	
        	File file = new File(this.fileName) ;
        	int sauvegarder = JOptionPane.YES_OPTION ; 
        	if ( file.exists() ) {
        		sauvegarder = this.popupFichierExiste () ;
        	} 
        	
        	if ( sauvegarder == JOptionPane.YES_OPTION ) {
        		((Save) af).save( file );
        		msgSave ( Utilities.getLangueMessage( Constantes.MESSAGE_SAUVEGARDE_EFFECTUEE )) ;
        		frame.setTitle(Utilities.getRelease() + " - " + this.fileName);
        		
        	}
        
        }
        
        
    }

    /**
     * Sauvegarde les données dans un nouveau fichier, si le nouveau fichier
     * existe, celui-ci sera écrasé.
     */
    
    public void saveAs() {
    	
        String fileName;
        fileName = chooseFile( Constantes.SAVE );
        if (fileName == null)
            return;

        this.setSave ( true );

        AnalyseFilter af = getAnalyseFilter();

        if (Utilities.getExtension(fileName).equals(""))
            this.fileName = fileName + "." + af.getExtension();
        else
            this.fileName = fileName;

     // Bug #352415
    	
    	File file = new File(this.fileName) ;
    	int sauvegarder = JOptionPane.YES_OPTION ; 
    	if ( file.exists() ) {
    		sauvegarder = this.popupFichierExiste () ;
    	} 
    	
    	if ( sauvegarder == JOptionPane.YES_OPTION ) {
    		((Save) af).save( file );    		
    		msgSave ( Utilities.getLangueMessage( Constantes.MESSAGE_SAUVEGARDE_EFFECTUEE ) ) ;     		
    	}
    	
        frame.setTitle(Utilities.getRelease() + " - " + this.fileName);
        
    }

    private  void   msgSave ( String msg ) {      	
    
    	GUIUtilities.messageHTML("<b style=\"color: blue;\">" + msg + "</b>", true, 300, 250);
    }
    
    /**
     * Récupère les données depuis un fichier et les chargent dans l'application.
     */
    public void open() {
    	
    	int choix = this.popupSauvegarde () ;  
    	if ( choix == JOptionPane.CANCEL_OPTION )   return  ;
    	
        String fileName = null;
        fileName = chooseFile(Constantes.OPEN);

        open(fileName);
    }

    public void open(String fileName)
    {
        if (fileName == null)
            return;

        AnalyseModule mod;
        for (Iterator<Entry<String, AnalyseModule>> e = Main.modules.entrySet().iterator(); e.hasNext();) {
            mod = e.next().getValue();
            mod.clear();
        }

        this.setSave ( true ) ; 

        this.fileName = fileName;

        AnalyseFilter af = getAnalyseFilter();
        if (af != null && af.canOpen())
            ((Open) af).open(new File(this.fileName));
        frame.setTitle(Utilities.getRelease() + " - " + this.fileName);
    }

    private  int  popupExit () {
	    
    	int choix ;
    	    	
    	choix = GUIUtilities.question_YES_NO ( Utilities.getLangueMessage( Constantes.MESSAGE_FERMER_LOGICIEL )) ;

    	if ( choix ==  JOptionPane.YES_OPTION ) {
		
    		if ( this.fileName != null ) {
	    		choix = GUIUtilities.question_YES_NO_CANCEL ( Utilities.getLangueMessage( Constantes.MESSAGE_SAUVEGARDER_FICHIER_ENCOURS) ) ;
	
	    	    if (choix == JOptionPane.YES_OPTION)
	    	        save();
    		}
    	}
        else {
            /*
			 *  exprès pour la fonction appelante NO dans ce cas est équivalent à CANCEL
			 */
            choix = JOptionPane.CANCEL_OPTION ;
        }
    	
	    return choix ; 
    }
        
    private  int  popupSauvegarde () {
	    
    	if ( this.fileName == null ) return JOptionPane.CLOSED_OPTION ; 
    	
    	int choix = GUIUtilities.question_YES_NO_CANCEL (Utilities.getLangueMessage( Constantes.MESSAGE_SAUVEGARDER_FICHIER_ENCOURS )) ;
    	
	    if (choix == JOptionPane.YES_OPTION)
	        save();
	    
	    return choix ; 
    }
    
    private  int  popupFichierExiste () {	    
    	int choix = GUIUtilities.question_YES_NO ( Utilities.getLangueMessage( Constantes.MESSAGE_FICHIER_EXISTANT )) ;    
	    return choix ; 
    }
    
    /**
     * Efface toutes les données précédentes. Propose de sauvegarder les données
     * avant l'effacement.
     */
    public void clear() {
    	
    	if ( ! this.isNewOption() )
    		this.popupSauvegarde () ;  

        AnalyseModule mod;
        for (Iterator<Entry<String, AnalyseModule>>  e = Main.modules.entrySet().iterator(); e.hasNext();) {
            mod = e.next().getValue();
            mod.clear();
        }

        this.setSave ( false ) ;
        frame.setTitle( Utilities.getRelease() + " - sans nom");
    }

    /**
     * Initialise les filtres de sauvegardes.
     */
    private void initFilter()
    {
        filtres = new ArrayList<FiltreASI>();
        filtres.add(new FiltreASI());
    }

    /**
     * Modifie les filtres selon le mode.
     */
    private void initFileFilter(String mode)
    {
        AnalyseFilter af;
        FileChooserFilter cf;
        chooser.resetChoosableFileFilters();
        for (Iterator<FiltreASI> e = filtres.iterator(); e.hasNext();) {
            af = e.next();
            if ( ( mode.equals ( Constantes.OPEN ) && af.canOpen() ) || 
            	 ( mode.equals ( Constantes.SAVE ) && af.canSave() ) )  {
                cf = new FileChooserFilter(af.getID());
                cf.setExtension(af.getExtension());
                cf.setDescription(af.getDescription());
                chooser.addChoosableFileFilter(cf);
            }
        }

    }

    /**
     * Retourne l'objet <code>AnalyseFilter</code> correspondant à la
     * description du FileChooserFilter courant.
     */
    private AnalyseFilter getAnalyseFilter()
    {
        AnalyseFilter af;
        String ext = Constantes.FILE_EXTENSION ;   // bug #489240
        String ID = "";
        FileChooserFilter ff;

        if (chooser.getFileFilter() instanceof FileChooserFilter) {
            ff = (FileChooserFilter) (chooser.getFileFilter());
            ID = ff.getID();
        }
        if (fileName != null)
            ext = Utilities.getExtension(fileName);

        for (Iterator<FiltreASI> e = filtres.iterator(); e.hasNext();) {
            af = e.next();
            if (af.getID().equals(ID) || af.getExtension().equals(ext))
                return af;
        }

        return null;
    }

    /**
     * Initialise la boite de dialogue <code>JFileChooser</code>.
     */
    private void initFileChooser()
    {
        chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileHidingEnabled(true);
    }

    /**
     * Retourne la boite de dialogue <code>JFileChooser</code>.
     * 
     * @param mode
     *            paramètre indiquant le type de boite : ouverture ou
     *            sauvegarde.
     */
    private JFileChooser getFileChooser(String  mode)
    {
        initFileFilter(mode);
        if ( mode.equals ( Constantes.SAVE )  || mode.equals ( Constantes.RAPPORT ) ) {
            chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        } else {
            chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        }

        return chooser;
    }

    private String chooseFile(String  mode)
    {
        JFileChooser chooser = getFileChooser(mode);

        if (chooser.showDialog(org.analyse.main.Main.analyseFrame, null) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath();
        }

        return null;
    }

	public void setNewOption(boolean newOption) {
		this.newOption = newOption;
	}

	public boolean isNewOption() {
		return newOption;
	}
}