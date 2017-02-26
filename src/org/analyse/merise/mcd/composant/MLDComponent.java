/*
 * 03/26/2002 - 21:14:14
 * 
 * MPD - Copyright (C) 2002 Dreux Loic zgoblin@linuxfreemail.com
 * 
 *  Modifications 
 *  -------------
 *  Date : 2009 janvier 22
 *  @auteur : Bruno Dabo <bruno.dabo@lywoonsoftware.com>
 *   
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package org.analyse.merise.mcd.composant;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Hashtable;
import java.util.Iterator;

import org.analyse.core.gui.zgraph.ZElement;
import org.analyse.core.gui.zgraph.ZGraphique;
import org.analyse.core.util.Constantes;
import org.analyse.core.util.Utilities;

public class MLDComponent extends ZGraphique {
	private static final long serialVersionUID = -464810270266166396L;
	private double width;
    protected Hashtable<String, String> foreignKeys;

    public MLDComponent() {
        super();

        foreignKeys = new Hashtable<String, String>();

        setEnabled(false);
        setBackground(Color.white);
    }

    /**
     * @return la taille du composant
     */
    public Dimension getPreferredSize() {
        int x = 0, y = 0;
        for (Iterator<ZElement> e = enumElements(); e.hasNext();) {
            MPDEntite o = (MPDEntite) e.next();
            x = o.getX() + o.getWidth() > x ? o.getX() + o.getWidth() : x;
            y = o.getY() + o.getHeight() > y ? o.getY() + o.getHeight() : y;
        }
        return new Dimension(x + 30, y + 30);
    }

    /**
     * @return un MPDEntite
     * @param nom
     *            Nom de l'entité à retourner
     */
    public MPDEntite getMPDEntite(String name) {
        for (Iterator<ZElement> e = enumElements(); e.hasNext();) {
            MPDEntite o = (MPDEntite) e.next();
            if (o.getName().equals(name)) {
                return o;
            }
        }
        return null;
    }

    public MPDEntite getMPDEntite(int i) {
        return (MPDEntite) (getElement(i));
    }

    /**
     * Modifie la largeur du <code>MPDComponent</code>
     */
    public void setWidth(double width) {
        this.width = width;
    }

    /**
     * Ajoute une entité au vecteur.
     */
    public void addMPDEntite(MPDEntite ent) {
        addElement(ent);

        repaint();
    }

    public void addForeignKey(String cleEtrangere, String table) {
        foreignKeys.put(cleEtrangere, table);
    }

    public String toString() {
        String res = "{\n";
        for (Iterator<ZElement> e = enumElements(); e.hasNext();) {
            res += e.next() + "\n";
        }
        return "}" + res;
    }

    /**
     * Construit les requêtes SQL.
     */
    public void buildMLD (MPDComponent mpdComponent, MLDCommand mld) {
        String text, info;
        MPDEntite ent, oldEnt ;
        int cmp, nbId;
        
        boolean isFirst;
  
        mld.clear();

        text = "# Modèle créé le : " + new java.util.Date() + " ;";
        mld.addRequest(text);
        oldEnt = null ; 
        for (Iterator<ZElement> e = mpdComponent.enumElements(); e.hasNext();) {
        	
        	ent = (MPDEntite) (e.next());
        	if ( ! ent.equals (oldEnt) ) {        		
        		oldEnt = ent ; 
        		isFirst = true ; 
        	} else 
        		isFirst = false ; 
        		
            text = Utilities.normaliseString( ent.getName(), Constantes.LOWER) + " (";
            
            for (Iterator<String> e2 = ent.elementsInformations(); e2.hasNext() ; ) {            	
                info = e2.next();
                info = Utilities.normaliseString(info, Constantes.LOWER);  
                if (ent.identifiants.containsKey(info) )
                	text += "<u>" + info +"</u>";
                else
                	if (ent.getForeignKey(info) != null)
                		text += "#" + info ;
                	else 
                		text += info ; 
                text += ", " ;
            }
           
            text += ") ;";
            
            text=text.replace (", )", ")") ;
            
            mld.addRequest(text);

        }
    }

    /**
     *  
     */
    public void clear() {
        foreignKeys.clear();
        super.clearAll();
    }
}
