/*
 * 05/05/2002 - 22:26:20
 * 
 * ZLien - Copyright (C) 2002 Dreux Loic dreuxl@free.fr
 * 
 * Modifications : 
 * ---------------
 *   Auteur : Bruno Dabo <bruno.dabo@lywoonsoftware.com>
 *   Date   : 2009 jan 22
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

package org.analyse.core.gui.zgraph;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import org.analyse.core.util.Constantes;  

public abstract class ZLien
{
    
    /** premier <code>ZElement</code> à lier */
    protected ZElement elem1;

    /** deuxième <code>ZElement</code> à lier */
    protected ZElement elem2;
    protected ZElement elem3;  // bdabo janv 2009 
    
    /** Position x1 */
    protected int x1;

    /** Position x2 */
    protected int x2;

    /** Position y1 */
    protected int y1;

    /** Position y2 */
    protected int y2;

    /**
     * Définie un <code>ZLien</code> avec les deux <code>ZElement</code>
     * qu'il lie
     * 
     * @param elem1
     *            premier <code>ZElement</code> à lier
     * @param elem2
     *            deuxième <code>ZElement</code> à lier
     * @param elem3
     *            troisieme <code>ZElement</code> à lier
     */
    public ZLien(ZElement elem1, ZElement elem2, ZElement elem3)
    {
        this.elem1 = elem1;
        this.elem2 = elem2;
        this.elem3 = elem3;
    }

    /**
     * Définie un <code> ZLien </code> sans argument. Ce lien est destiné à être
     * lié par la suite dans le <code> ZGraphique </code>
     */
    public ZLien()
    {
        this(null, null, null);
    }

    /** Change l'élément 1, 2 ou 3 */
    public void setElement(ZElement elem, String index)
    {
        if (index == Constantes.MCDENTITE1 )
            this.elem1 = elem;
        else if (index == Constantes.MCDENTITE2 )
            this.elem2 = elem;
        else if (index == Constantes.MCDASSOCIATION)
            this.elem3 = elem;
        else
            throw new IllegalArgumentException("");
    }

    /** Retourne l'élément 1, 2 ou 3 */
    public ZElement getElement(String index)
    {
        if (index == Constantes.MCDENTITE1 )
            return elem1;
        else if (index == Constantes.MCDENTITE2 )
            return elem2;
        else if (index == Constantes.MCDASSOCIATION )
            return elem3;

        throw new IllegalArgumentException("");
    }

    /**
     * Cette méthode est appelée avant la suppression du lien.
     */
    public abstract void clearElement();

    /**
     * Redessine le composant dans un <code>ZGraphique</code> Attention : il
     * est important d'appeler une méthode de calcul des coordonnées des points :
     * soit calculPositionsDefaut() ou calculPositionsCentre().
     * 
     * @param g
     *            Graphics du <code>ZGraphique</code>
     */
    public abstract void paint(Graphics g);

    /**
     * <br>
     * Inverse les deux Elements : elem1 devient elem2 elem2 devient elem1</br>
     */
    public void inverseZElements()
    {
        ZElement elem = elem1;
        elem1 = elem2;
        elem2 = elem;
    }

    /**
     * Dessine le focus du composant s'il est sélectionné
     */
    public void paintFocus(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        Rectangle2D r;

        r = new Rectangle2D.Double(x1 - 1, y1 - 1, 4, 4);
        g2d.setColor(Color.black);
        g2d.fill(r);

        r = new Rectangle2D.Double(x2 - 2, y2 - 1, 4, 4);
        g2d.setColor(Color.black);
        g2d.fill(r);
    }

    /** Calcul des coordonnées des deux extrémités par défaut */
    protected void updateDefaultLocation()
    {
        //Récupération des centres
        int centrex1 = (elem1.getX() + elem1.getWidth() / 2);
        int centrey1 = (elem1.getY() + elem1.getHeight() / 2);
        int centrex2 = (elem2.getX() + elem2.getWidth() / 2);
        int centrey2 = (elem2.getY() + elem2.getHeight() / 2);

        int hauteur = centrex2 - centrex1;
        int largeur = centrey2 - centrey1;

        if (Math.abs(hauteur) > Math.abs(largeur)) {
            x1 = hauteur > 0 ? elem1.getX() + elem1.getWidth() : elem1.getX();
            y1 = elem1.getY() + elem1.getHeight() / 2;
            x2 = hauteur < 0 ? elem2.getX() + elem2.getWidth() : elem2.getX();
            y2 = elem2.getY() + elem2.getHeight() / 2;
        } else {
            x1 = elem1.getX() + elem1.getWidth() / 2;
            y1 = largeur > 0 ? elem1.getY() + elem1.getHeight() : elem1.getY();
            x2 = elem2.getX() + elem2.getWidth() / 2;
            y2 = largeur < 0 ? elem2.getY() + elem2.getHeight() : elem2.getY();
        }
    }

    /** Calcul des coordonnées par rapport au centre */
    public void updateLocation()
    {
        double theta, l1, l2, l3, diagonale;

        double thetas[] = new double[4];

        int pointx, pointy;

        //Récupération des centres
        int centrex1 = (elem1.getX() + elem1.getWidth() / 2);
        int centrey1 = (elem1.getY() + elem1.getHeight() / 2);
        int centrex2 = (elem2.getX() + elem2.getWidth() / 2);
        int centrey2 = (elem2.getY() + elem2.getHeight() / 2);

        // premier rectangle

        diagonale = Math.sqrt((elem1.getWidth()) * (elem1.getWidth())
                + (elem1.getHeight()) * (elem1.getHeight()));

        thetas[0] = Math.acos(elem1.getWidth() / diagonale);
        thetas[1] = Math.acos(-elem1.getWidth() / diagonale);
        thetas[2] = 2 * Math.PI - thetas[1];
        thetas[3] = 2 * Math.PI - thetas[0];

        l1 = centrex2 - centrex1;
        l2 = centrey2 - centrey1;
        l3 = Math.sqrt((centrex1 - centrex2) * (centrex1 - centrex2)
                + (centrey1 - centrey2) * (centrey1 - centrey2));

        theta = Math.acos(l1 / l3);

        if (centrey1 > centrey2)
            theta = 2 * Math.PI - theta;

        if (theta < thetas[0] || theta >= thetas[3]) {
            y1 = (int) (elem1.getY() + elem1.getHeight() / 2 + (((elem1
                    .getWidth() / 2) * l2) / l1));
            x1 = elem1.getX() + elem1.getWidth();
        } else if (theta >= thetas[1] && theta < thetas[2]) {
            y1 = (int) (elem1.getY() + elem1.getHeight() / 2 - (((elem1
                    .getWidth() / 2) * l2) / l1));
            x1 = elem1.getX();
        } else if (theta >= thetas[0] && theta < thetas[1]) {
            y1 = elem1.getY() + elem1.getHeight();
            x1 = (int) (elem1.getX() + elem1.getWidth() / 2 + (((elem1
                    .getHeight() / 2) * l1) / l2));
        } else {
            y1 = elem1.getY();
            x1 = (int) (elem1.getX() + elem1.getWidth() / 2 - (((elem1
                    .getHeight() / 2) * l1) / l2));
        }

        // deuxième rectangle

        theta = 2 * Math.PI - theta;

        diagonale = Math.sqrt((elem2.getWidth()) * (elem2.getWidth())
                + (elem2.getHeight()) * (elem2.getHeight()));

        thetas[0] = Math.acos(elem2.getWidth() / diagonale);
        thetas[1] = Math.acos(-elem2.getWidth() / diagonale);
        thetas[2] = 2 * Math.PI - thetas[1];
        thetas[3] = 2 * Math.PI - thetas[0];

        if (theta < thetas[0] || theta >= thetas[3]) {
            y2 = (int) (elem2.getY() + elem2.getHeight() / 2 - (((elem2
                    .getWidth() / 2) * l2) / l1));
            x2 = elem2.getX();
        } else if (theta >= thetas[1] && theta < thetas[2]) {
            y2 = (int) (elem2.getY() + elem2.getHeight() / 2 + (((elem2
                    .getWidth() / 2) * l2) / l1));
            x2 = elem2.getX() + elem2.getWidth();
        } else if (theta >= thetas[0] && theta < thetas[1]) {
            y2 = elem2.getY() + elem2.getHeight();
            x2 = (int) (elem2.getX() + elem2.getWidth() / 2 + (((elem2
                    .getHeight() / 2) * l1) / l2));
        } else {
            y2 = elem2.getY();
            x2 = (int) (elem2.getX() + elem2.getWidth() / 2 - (((elem2
                    .getHeight() / 2) * l1) / l2));
        }
    }

    public int getX1()
    {
        return x1;
    }

    public int getY1()
    {
        return y1;
    }

    public int getX2()
    {
        return x2;
    }

    public int getY2()
    {
        return y2;
    }

    public boolean isSelected(int mousex, int mousey)
    {
        double a, b, c, h, g;

        a = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        b = Math.sqrt((x1 - mousex) * (x1 - mousex) + (y1 - mousey)
                * (y1 - mousey));
        c = Math.sqrt((mousex - x2) * (mousex - x2) + (mousey - y2)
                * (mousey - y2));

        h = a / 2 - (b * b) / (2 * a) + c * c / (2 * a);

        g = Math.sqrt(c * c - h * h);
        return g < 10
                && (mousex > x1 - 10 && mousex < x2 + 10 || mousex < x1 + 10
                && mousex > x2 - 10)
                && (mousey > y1 - 10 && mousey < y2 + 10 || mousey < y1 + 10
                && mousey > y2 - 10);
    }

    public void notifyZLien()
    {
        elem1.changementLien();
        elem1.notifyZElement();
    }

    public String toString()
    {
        return "ZLien, " + info();
    }

    public String info()
    {
        return "Element 1 : {" + elem1 + "}, Element 2 : {" + elem2
                + "}, Element 3 : {" + elem3 + "}, Point1(" + x1 + ";" + y1 + "), Point2(" + x2 + ";" + y2
                + ")";
    }
}