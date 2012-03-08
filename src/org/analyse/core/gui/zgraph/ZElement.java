/*
 * 05/02/2002 - 20:15:59
 *
 * ZElement -
 * Copyright (C) 2002 Dreux Loic
 * dreuxl@free.fr
 *
 *  Modifications 
 *  -------------
 *  Date : 2009 mars 13
 *  @auteur : Benjamin Gandon
 *  @objet : ajouter isSelected() et isInside() pour la sélection multiple
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

package org.analyse.core.gui.zgraph;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.Observable;

/**
 * Cet objet est destiné à être utilise avec ZGraphique.
 */
public abstract class ZElement
{
    /** Position en X */
    private int x;

    /** Position en Y */
    private int y;

    /** largeur */
    private int width;

    /** hauteur */
    private int height;

    /** ZGraphique */
    protected ObservableZGraphique observable;

    /** Changement d'un lien */
    private boolean lien;

    public ZElement()
    {
        observable = new ObservableZGraphique();
    }

    /** Définie un <code>ZElement</code> avec les paramètres par défaut */
    public ZElement(ZGraphique zgraph)
    {
        this(zgraph, 0, 0, 10, 10);
    }

    /**
     * Définie un <code>ZElement</code> en le positionnant.
     * 
     * @param x
     *            position en x de l'élément à l'initialisation.
     * @param y
     *            position en y de l'élément à l'initialisation.
     * @param width
     *            largeur de l'élément.
     * @param height
     *            hauteur de l'élément.
     */
    public ZElement(ZGraphique zgraph, int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        observable = new ObservableZGraphique();
        majObserver(zgraph);
    }

    /** Modifie la position de l'élément */
    public void setPosition(Point p)
    {
        this.x = (int) p.getX();
        this.y = (int) p.getY();
        observable.notifyZElement();
    }

    /** Modifie la largeur de l'élément */
    public void setWidth(int width)
    {
        this.width = width;
        observable.notifyZElement();
    }

    /** Modifie la hauteur de l'élément */
    public void setHeight(int height)
    {
        this.height = height;
        observable.notifyZElement();
    }

    /** Retourne la position en x de l'élément */
    public int getX()
    {
        return x;
    }

    /** Retourne la position en y de l'élément */
    public int getY()
    {
        return y;
    }

    /** Retourne la largeur de l'élément */
    public int getWidth()
    {
        return width;
    }

    /** Retourne la hauteur de l'élément */
    public int getHeight()
    {
        return height;
    }

    public boolean isSelected(int x, int y)
    {
        return x > getX()
            && x < getX() + getWidth()
            && y > getY()
            && y < getY() + getHeight();
    }

    public boolean isInside(int x1, int y1, int x2, int y2)
    {
        int tmp = x1;
        if (x2 < x1) {
            x1 = x2;
            x2 = tmp;
        }
        tmp = y1;
        if (y2 < y1) {
            y1 = y2;
            y2 = tmp;
        }
        return (x1 < x && x < x2) && (y1 < y && y < y2)
            && (x1 < x+width && x+width < x2) && (y1 < y+height && y+height < y2);
    }

    public boolean getLien()
    {
        return lien;
    }

    public void setLien(boolean lien)
    {
        this.lien = lien;
    }

    /**
     * Redessine le composant dans un <code>ZGraphique</code>
     * 
     * @param g
     *            Graphics du <code>ZGraphique</code>
     */
    public abstract void paint(Graphics g);

    /**
     * Dessine le focus du composant s'il est séléctionné
     */
    public void paintFocus(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        Rectangle2D r;

        r = new Rectangle2D.Double(getX() - 1, getY() - 1, 4, 4);
        g2d.setColor(Color.black);
        g2d.fill(r);

        r = new Rectangle2D.Double(getX() + getWidth() - 2, getY() - 1, 4, 4);
        g2d.setColor(Color.black);
        g2d.fill(r);

        r = new Rectangle2D.Double(getX() + getWidth() - 2, getY()
                + getHeight() - 2, 4, 4);
        g2d.setColor(Color.black);
        g2d.fill(r);

        r = new Rectangle2D.Double(getX() - 1, getY() + getHeight() - 2, 4, 4);
        g2d.setColor(Color.black);
        g2d.fill(r);

    }

    public void majObserver(ZGraphique zgraph)
    {
        observable.addObserver(zgraph);
    }

    public void clearObservers()
    {
        observable.deleteObservers();
    }

    public void notifyZElement()
    {
        observable.notifyZElement();
    }

    public void changementLien()
    {
        lien = true;
    }

    public abstract boolean isChanged();

    public String toString()
    {
        return "ZElement, " + info();
    }

    public String info()
    {
        return "position(" + x + ";" + y + ") taille(" + width + ";" + height
                + ")";
    }

    private class ObservableZGraphique extends Observable
    {
        private int x, y, height, width;

        public ObservableZGraphique()
        {
            x = y = height = width = 0;
        }

        public void notifyZElement()
        {
            if (x != getX() || y != getY() || height != getHeight()
                    || width != getWidth() || getLien() || isChanged()) {
                setChanged();
                this.x = getX();
                this.y = getY();
                this.height = getHeight();
                this.width = getWidth();
                setLien(false);
            }
            super.notifyObservers();
        }
    }
}