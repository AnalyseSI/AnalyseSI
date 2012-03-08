/*
 * 02/26/2002 - 13:43:47
 * 
 * MCDAssociation - Copyright (C) 2002 Dreux Loic dreuxl@free.fr
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
import java.util.List;

import org.analyse.merise.gui.table.DictionnaireTable;

public class MCDAssociation extends MCDObjet
{
    /** Font de l'association */
    private Font font;

    /** Permet de calculer la taille */
    private FontMetrics fm;

    public MCDAssociation(MCDComponent mcd)
    {
        this(mcd, "Association " + (getIndex() + 1), 
        		getIndex() * 20 % 200, getIndex() * 20 % 200);
    }

    public MCDAssociation(MCDComponent mcd, int x, int y)
    {
    	
        this(mcd, "Association " + (getIndex ()+ 1), x, y);
    }
    
    public MCDAssociation(MCDComponent mcd, String name, int x, int y)
    {
        super(mcd, name, x, y, 150, 50);
    }

    /**
     * Recalcule la taille de l'association.
     */
    public void updateSize()
    {
        int gw = fm.stringWidth(name);

        for (int i = 0; i < sizeInformation(); i++)
            gw = fm.stringWidth((String) (data.getValue(getCodeInformation(i),
                    DictionnaireTable.NAME))) < gw ? gw : fm
                    .stringWidth((String) (data.getValue(getCodeInformation(i),
                            DictionnaireTable.NAME)));

        int gh = (fm.getMaxDescent() + 15) * sizeInformation();

        this.setWidth(gw + 50);
        this.setHeight(40 + gh);
    }

    /**
     * Affichage de l'association.
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
                getWidth(), getHeight(), 35, 35);
        Line2D l = new Line2D.Double(getX(), getY() + 25, getX() + getWidth(),
                getY() + 25);
        g2d.setColor(Color.white);
        g2d.fill(r);
        g2d.setColor(Color.blue);
        g2d.draw(r);
        g2d.draw(l);

        g2d.setColor(Color.black);
        g2d.drawString(name, (getX() + getWidth() / 2) - fm.stringWidth(name)
                / 2, getY() + 15);

        for (int i = 0; i < sizeInformation(); i++)
            g2d.drawString((String) (data.getValue(getCodeInformation(i),
                    DictionnaireTable.NAME)), getX() + 10, getY() + 40 + i
                    * (fm.getMaxDescent() + 15));

    }

    /**
     * Revoie tous les codes des informations sans le premier, c'est à dire sans
     * l'identifiant.
     */
    public List<String> getInformations()
    {
        return informations;
    }
    public void setInformations( List<String> v ) {
        this.informations = v ; 
    }

    public String toString()
    {
        return "MCDAssociation : " + info();
    }

}
