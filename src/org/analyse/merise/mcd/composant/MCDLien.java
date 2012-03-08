/*
 * 02/28/2002 - 22:19:42
 * 
 * MCDLien - Copyright (C) 2002 Dreux Loic dreuxl@free.fr
 * 
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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import org.analyse.core.util.Constantes ;
import org.analyse.core.gui.zgraph.ZElement;
import org.analyse.core.gui.zgraph.ZLien;

public class MCDLien extends ZLien
{
    /** Cardinalité minimale : soit "0" ou "1" */
    private String cardMin;

    /** Cardinalité maximale : soit "1" ou "N" */
    private String cardMax;

    public MCDLien()
    {
        super();
        cardMin = "0";
        cardMax = "N";
    }

    /**
     * Lie l'objet passé en parametre. Le MCDAssociation est toujours l'élément
     * 0 et Le MCDObjet est toujours l'élément 1.
     */
    public void setElement(ZElement elem, String index)
    {
        super.setElement(elem, index);

        if (elem != null) {
            MCDObjet obj = (MCDObjet) elem;
            obj.addLink(this);
        }

        if (elem1 instanceof MCDObjet && elem2 instanceof MCDAssociation)
            inverseZElements();
    }

    public MCDObjet getMCDObjet(String  index)
    {
        return (MCDObjet) (getElement(index));
    }

    /**
     * méthode appelée avant la suppression d'un lien.
     */
    public void clearElement()
    {
        MCDObjet obj;
        obj = getMCDObjet ( Constantes.MCDENTITE1 );

        if (obj != null)
            obj.delLink(this);

        obj = getMCDObjet( Constantes.MCDENTITE2 );
        if (obj != null)
            obj.delLink(this);
    }

    /**
     * Affichage de l'objet.
     */
    public void paint(Graphics g)
    {
        updateLocation();
        Graphics2D g2d = (Graphics2D) g;
        Line2D l = new Line2D.Double(x1, y1, x2, y2);

        g2d.setColor(Color.black);
        g2d.draw(l);

        if (Math.abs(x1 - x2) > Math.abs(y1 - y2))
            g2d.drawString(cardMin + ", " + cardMax, (x1 + x2) / 2,
                    (y1 + y2 + 25) / 2);
        else
            g2d.drawString(cardMin + ", " + cardMax, (x1 + x2 + 10) / 2,
                    (y1 + y2) / 2);
    }

    /** @return la cardinalité minimale */
    public String getCardMin()
    {
        return cardMin;
    }

    /** @return la cardinalité maximale */
    public String getCardMax()
    {
        return cardMax;
    }

    /** Modifie la cardinalité minimale. */
    public void setCardMin(String cardMin)
    {
        this.cardMin = cardMin;
        notifyZLien();
    }

    /** Modifie la cardinalité maximale. */
    public void setCardMax(String cardMax)
    {
        this.cardMax = cardMax;
        notifyZLien();
    }

    public String toString()
    {
        return "MCDLien, " + info();
    }
}
