/*
 * 03/26/2002 - 21:19:50
 * 
 * MPDEntite - Copyright (C) 2002 Dreux Loic dreuxl@free.fr
 * 
 * Modifications : 
 * ---------------
 * @author : Bruno Dabo <bruno.dabo@lywoonsoftware.com>
 * @date : 2009 jan 22
 * @objet : enlever la limite sur le nombre de proprietes et de liens 
 * 
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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.analyse.core.gui.zgraph.ZElement;

public class MPDEntite extends ZElement
{

    /** Nombre d'identifiant */
    private int nbIdentifiant = 0;

    /** Nombre d'information sans les identifiants */
    private int nbInformation = 0;
    
    private boolean foreignKeyCanBeNull  ; 
    
    /** Nombre de MCDEntite */
    private static int nbMPDEntite;

    /** Vecteur contenant les informations de l'objet */
    protected List<String> informations = new ArrayList<String>() ;

    /** Vecteur contenant les identifiants de l'objet ==> Préparation des clés multiples */
    protected Hashtable<String,String> identifiants ;

    protected List<MPDLien> links = new ArrayList<MPDLien>() ;

    /** Nom de l'objet */
    private String name;
    private boolean porteuse ;
    private Font font;

    private FontMetrics fm;

    private Hashtable<String,String> foreignKeys;

    public MPDEntite(MPDComponent mpd, String name)
    {
        this(mpd, name, new ArrayList<String>(), new ArrayList<String>());
    }

    public MPDEntite(MPDComponent mpd, String name, List<String> identifiants,
    		List<String> infos)
    {
        super(mpd, 10, 10, 10, 10);

        foreignKeys = new Hashtable<String,String>();
        this.identifiants  = new Hashtable<String,String>();
        
        this.font = mpd.getFont();
        this.fm = mpd.getFontMetrics(font);
        this.setPorteuse (true ) ;

        this.name = name;

        addInformations(identifiants);
        addIdentifiant(infos);

        updateSize();

        nbMPDEntite++;
    }
    

    public boolean isPorteuse () {
    	return porteuse;  
    }


    public void setPorteuse ( boolean b ) {
    	porteuse = b ;  
    }

    public void setForeignKeyCanBeNull ( boolean bool ) {
    	foreignKeyCanBeNull = bool ; 
    }

    public boolean isForeignKeyCanBeNull () {
    	return foreignKeyCanBeNull  ; 
    }
    
    public void addForeignKey(String foreignKey, String table)
    {
        foreignKeys.put(foreignKey, table);
    }

    public Object getForeignKey( String key )
    {
        return foreignKeys.get ( key );
    }
    
    public void removeForeignKey(String foreignKey)
    {
        foreignKeys.remove(foreignKey);
    }

    public boolean foreignKeysIsEmpty()
    {
        return foreignKeys.isEmpty();
    }

    public String getTableForeignKey(String foreignKey)
    {
        return foreignKeys.get(foreignKey);
    }

    /** Affichage du MPDEntite */
    public void paint(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(font);
        updateSize();
        RoundRectangle2D r = new RoundRectangle2D.Double(getX(), getY(),
                getWidth(), getHeight(), 1, 1);
        Line2D l = new Line2D.Double(getX(), 25 + getY(), getWidth() + getX(),
                25 + getY());
        g2d.setColor(Color.white);
        g2d.fill(r);
        g2d.setColor(Color.blue);
        g2d.draw(r);
        g2d.draw(l);

        g2d.setColor(Color.black);
        g2d.drawString(getName(), getX() + 10, getY() + 15);

        for (int i = 0; i < informations.size(); i++)
            g2d.drawString(getCodeInformation(i), getX() + 10, getY() + 40 + i
                    * (fm.getMaxDescent() + 15));

        for (int i = 0; i < nbIdentifiant; i++) {
            String nom = getCodeInformation(i);
            l = new Line2D.Double(getX() + 10, 45 + getY() + i
                    * (fm.getMaxDescent() + 15), getX() + fm.stringWidth(nom)
                    + 10, 45 + getY() + i * (fm.getMaxDescent() + 15));
            g2d.draw(l);
        }
    }

    public boolean isChanged()
    {
        return false;
    }

    public String getName()
    {
        return name;
    }

    /** Calcul de la taille du MCDEntite */
    public void updateSize()
    {
        String info;
        int gw = fm.stringWidth(getName());

        for (Iterator<String> e = informations.iterator(); e.hasNext();) {
            info = e.next();
            gw = fm.stringWidth(info) < gw ? gw : fm.stringWidth(info);
        }

        int gh = (fm.getMaxDescent() + 15) * (informations.size() - 1);

        this.setWidth(gw + 20);
        this.setHeight(50 + gh);
    }

    /**
     * Calcul la position du MCDEntite suivant
     */
    public int nextPosition()
    {
        return getX() + getWidth() + 30;
    }

    /**
     * Reinitialise les paramètres static
     */
    public static void empty()
    {
        nbMPDEntite = 0;
    }

    /**
     * Ajouter plusieurs informations en indiquant leur code de la table dico
     * des infos dans un vecteur de String. Ces éléments sont ajoutés à la fin.
     */
    public void addInformations(List<String> list)
    {
    	for(String info : list)
            informations.add(info);
        //nbInformation += code.size();
        
        nbInformation = informations.size();  // source à problème
        updateSize();
    }

    /**
     * Ajout une information dans le vecteur informations.
     */
    public void addInformation(String code)
    {
        informations.add(code);
        nbInformation++;
        updateSize();
    }

    /**
     * Existe-t-il cette propriété ?
     */
    public boolean  existInformation(String code)
    {
        return informations.contains (code);
        
    }

    /**
     * Ajouter plusieurs informations en indiquant leur code de la table dico
     * des infos dans un vecteur de String. Cet élément est ajouté en tant
     * qu'identifiants.
     */
    public void addIdentifiant(List<String> codes)
    {
    	for(String code : codes) {
            informations.add(code);
            identifiants.put(code, code ) ;
    	}
    	
        //ajouterIdentifiant((String)(code.elementAt(i)));
        nbInformation += codes.size();
        updateSize();
    }


    public void removeIdentifiant( String code )
    {
    	identifiants.remove(code) ;
    }

    public Hashtable<String,String> getIdentifiants()
    {
    	return identifiants ;
    }
    
    public Object getIdentifiant(String code)
    {
    	return  identifiants.get ( code ); 
    		
    }

    /**
     * Ajoute un identifiant dans le vecteur informations. Attention cet élément
     * n'est pas ajouté a la fin mais dans la partie identifiant.
     */
    public void addIdentifiant(String code)
    {
        informations.add(nbIdentifiant, code);
        identifiants.put(code, code) ;  // ajout de la clé primaire --> Préparation des clés multiples / liens avec le MLD        
        nbIdentifiant++;
        updateSize();        
    }

    /**
     * @return le nombre d'informations et d'identifiants confondus.
     */
    public int sizeInformation()
    {
        return informations.size();
    }

    public int sizeIdentifiant()
    {
        return nbIdentifiant;
    }

    /**
     * @return le code d'une information.
     * @param i
     *            index de l'information à retourner dans le vecteur
     *            informations.
     */
    public String getCodeInformation(int i)
    {
        return informations.get(i);
    }

    public Iterator<String> elementsInformations()
    {
        return informations.iterator();
    }

    public String toString()
    {
        return name;
    }
}
