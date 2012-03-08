/*
 * 02/25/2002 - 13:29:38
 * 
 * MCDEntite - Copyright (C) 2002 DreugetX() Loic dreuxl@free.fr
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
import java.util.List;

import org.analyse.merise.gui.table.DictionnaireTable;

public class MCDEntite extends MCDObjet
{
    /** Police de l'objet */
    private Font font;

    /** Permet de calculer la taille des objets */
    private FontMetrics fm;

    
    public MCDEntite(MCDComponent mcd)
    {
        this(mcd, "Entite " + (getIndex ()+ 1),
                getIndex () * 20 % 200,  getIndex () * 20 % 200);
    }
    
    public MCDEntite(MCDComponent mcd, int x, int y)
    {
    	
        this(mcd, "Entite " + (getIndex ()+ 1), x, y);
    }

    public MCDEntite(MCDComponent mcd, String name, int x, int y)
    {
        super(mcd, name, x, y, 10, 10);
    }

    /**
     * Affichage de l'objet.
     */
    public void paint(Graphics g)
    {
        if (fm == null) {
            this.font = mcd.getFont();
            this.fm = mcd.getFontMetrics(font);
        }

        updateSize();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(font);

        RoundRectangle2D r = new RoundRectangle2D.Double(getX(), getY(),
                getWidth(), getHeight(), 1, 1);
        //Rectangle2D r = new Rectangle2D.Double(getX(), getY(), getWidth(),
        // getHeight());
        Line2D l = new Line2D.Double(getX(), 25 + getY(), getWidth() + getX(),
                getY() + 25);
        g2d.setColor(Color.white);
        g2d.fill(r);
        g2d.setColor(Color.blue);
        g2d.draw(r);
        g2d.draw(l);

        g2d.setColor(Color.black);
        g2d.drawString(name, getX() + 10, getY() + 15);

        for (int i = 0; i < sizeInformation(); i++)
            g2d.drawString((String) (data.getValue(getCodeInformation(i),
                    DictionnaireTable.NAME)), getX() + 10, getY() + 40 + i
                    * (fm.getMaxDescent() + 15));

        if (sizeInformation() > 0) {
            String nom = (String) (data.getValue(getCodeInformation(0),
                    DictionnaireTable.NAME));
            l = new Line2D.Double(getX() + 10, 45 + getY(), getX()
                    + fm.stringWidth(nom) + 10, 45 + getY());
            g2d.draw(l);
        }
    }

    /**
     * Calcul la taille de l'entité à n'éxécuter que lors du repaint
     */
    public void updateSize()
    {
        int gw = fm.stringWidth(name);

        for (int i = 0; i < sizeInformation(); i++)
            gw = fm.stringWidth((String) (data.getValue(getCodeInformation(i),
                    DictionnaireTable.NAME))) < gw ? gw : fm
                    .stringWidth((String) (data.getValue(getCodeInformation(i),
                            DictionnaireTable.NAME)));

        int gh = (fm.getMaxDescent() + 15) * (sizeInformation() - 1);

        this.setWidth(gw + 20);
        this.setHeight(50 + gh);
    }

    /**
     * Change l'identifiant de l'entité. Déplace le nouvelIdentifiant en
     * première place du Vector information, il est alors pris comme
     * identifiant.
     * 
     * @param nouvelIdentifiant
     *            index du nouvel identifiant
     */
    public void changerIdentifiant(int nouvelIdentifiant)
    {
        moveInformations(0, nouvelIdentifiant);
    }

    public String toString()
    {
        return "MCDEntite : " + info();
    }
    
    public void setInformations( List<String> v ) {
    }
    
    /**
     * Revoie tous les codes des informations sans le premier, c'est à dire sans
     * l'identifiant.
     */
    public List<String> getInformations()
    {
    	List<String> tmp = new ArrayList<String>();
        for (int i = 1; i < informations.size(); i++)
            tmp.add(informations.get(i));
        return tmp;
    }

}
