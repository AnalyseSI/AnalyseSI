/*
 * 02/28/2002 - 22:19:42
 * 
 * MCDLien - Copyright (C) 2002 Dreux Loic dreuxl@free.fr
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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import org.analyse.core.gui.zgraph.ZElement;
import org.analyse.core.gui.zgraph.ZLien;
import org.analyse.core.util.Constantes;

public class MPDLien extends ZLien {
	private boolean doubleFleche;

	public MPDLien() {
		this(false);
	}

	public MPDLien(boolean doubleFleche) {
		super();
		this.doubleFleche = doubleFleche;
	}

	/**
	 * Lie l'objet passé en parametre.
	 */
	public void setElement(ZElement elem, String index) {
		super.setElement(elem, index);

/*		if (elem != null) {
			MPDEntite obj = (MPDEntite) elem;
		}
		*/
		
	}

	public MPDEntite getMPDObjet(String  index) {
		return (MPDEntite) (getElement(index));
	}

	public void clearElement() {
	}

	/**
	 * Affichage de l'objet.
	 */
	public void paint(Graphics g) {
		double x, y;

		Point2D p1, p2, p3, p4;

		updateLocation();
		Graphics2D g2d = (Graphics2D) g;

		// Les 2 extremites du lien
		// Attention p1=(x2,y2), et p2=(x1,x2) !
		p1 = new Point2D.Double(x2, y2);
		p2 = new Point2D.Double(x1, y1);
		
		int hauteur = x1 - x2;
		int largeur = y1 - y2;

		//Pythagore, calcul de l'hypothenuse, donc la longueur de [P1,P2]
		double lt = Math.sqrt(hauteur * hauteur + largeur * largeur);


		//Calcul du point sur le segment [P1,P2] qui va servir de point de depart
		//pour faire les 2 rotations qui donneront les 2 extremites de la fleche
		//Ce point ne sera jamais affiche, il va juste servir pour les 2 rotations
		x = x1
		    + (x2 > x1 ? (Math.abs(x2 - x1) * 15) / lt : -(Math
								   .abs(x2 - x1) * 15)
		       / lt);
		y = y1
		    + (y2 > y1 ? (Math.abs(y2 - y1) * 15) / lt : -(Math
								   .abs(y2 - y1) * 15)
		       / lt);


		p4 = new Point2D.Double(x, y);

		//On trace la ligne entre les 2 elements du MPD
		Line2D l = new Line2D.Double(p1, p2);
		g2d.setColor(Color.RED);
		g2d.draw(l);

		AffineTransform affineTransform = new AffineTransform();

		//Rotation de PI/6 (30D) autour de x1,y1 (p2,point au bout de la fleche)
		affineTransform.setToRotation(Math.PI/6, x1, y1);

		//On applique la transformation au point p4, ce qui donne le point p3
		p3 = affineTransform.transform(p4, null);
		//On trace le premier bout de la fleche (entre l'extremite p2 et le point obtenu p3)
		l = new Line2D.Double(p2, p3);
		g2d.draw(l);

		//Rotation de -PI/6 (-30D) (dans l'autre sens donc) autour de x1,y1 
		//(p2,point au bout de la fleche)
		affineTransform.setToRotation(-Math.PI/6, x1, y1);

		//On applique la transformation au point p4, ce qui donne le point p3
		p3 = affineTransform.transform(p4, null);
		//On trace le premier bout de la fleche (entre l'extremite p2 et le point obtenu p3)
		l = new Line2D.Double(p2, p3);
		g2d.draw(l);

		if (this.doubleFleche) {
		    x = x2
			+ (x2 < x1 ? (Math.abs(x2 - x1) * 15) / lt : -(Math
								       .abs(x2 - x1) * 15)
			   / lt);
		    y = y2
			+ (y2 < y1 ? (Math.abs(y2 - y1) * 15) / lt : -(Math
								       .abs(y2 - y1) * 15)
			   / lt);

		    p4 = new Point2D.Double(x, y);
		    
		    affineTransform = new AffineTransform();
		    affineTransform.setToRotation(Math.PI/6, x2, y2);
		    
		    g2d.setColor(Color.RED);
		    p3 = affineTransform.transform(p4, null);
		    l = new Line2D.Double(p1, p3);
		    g2d.draw(l);
		    
		    affineTransform.setToRotation(-Math.PI/6, x2, y2);
		    
		    p3 = affineTransform.transform(p4, null);
		    l = new Line2D.Double(p1, p3);
		    g2d.draw(l);
		}
	}

	/**
	 * Evite d'avoir des lignes de travers.
	 */
        //Ajout de condition sur y2 pour que la base de la flèche soit sur MCDENTITE2 et qu'elle ne soit pas au-dessus
        //MICHEL Arthur, ROUX Constant
	public void updateLocation() {
		super.updateLocation();
		if (getElement( Constantes.MCDENTITE2 ).getX() > getElement( Constantes.MCDENTITE1 ).getX()
				+ getElement( Constantes.MCDENTITE1 ).getHeight()
				|| getElement( Constantes.MCDENTITE2 ).getX() + getElement( Constantes.MCDENTITE2 ).getWidth() < getElement(Constantes.MCDENTITE1 ).getX()) {
			if (getElement( Constantes.MCDENTITE2 ).getY() < getElement( Constantes.MCDENTITE1 ).getY()
					+ getElement( Constantes.MCDENTITE1 ).getWidth()
					&& getElement( Constantes.MCDENTITE1 ).getY() < getElement( Constantes.MCDENTITE2 ).getY()
							+ getElement( Constantes.MCDENTITE2 ).getHeight()) {
				if (y2 > getElement( Constantes.MCDENTITE1 ).getY() + getElement( Constantes.MCDENTITE1 ).getHeight())
					y2 = getElement( Constantes.MCDENTITE1 ).getY()+ getElement( Constantes.MCDENTITE1 ).getHeight();
				else if (y2 < getElement( Constantes.MCDENTITE1 ).getY())
					y2 = getElement( Constantes.MCDENTITE1 ).getY();
				y1 = y2;
                                if(y2 < getElement( Constantes.MCDENTITE2 ).getY()){
                                    y2 = getElement( Constantes.MCDENTITE2 ).getY() + (getElement( Constantes.MCDENTITE2 ).getHeight()/2);
                                }
			}
                } else if (getElement( Constantes.MCDENTITE2 ).getY() > getElement( Constantes.MCDENTITE1 ).getY()
				+ getElement( Constantes.MCDENTITE1 ).getWidth()
				|| getElement( Constantes.MCDENTITE2 ).getY() + getElement( Constantes.MCDENTITE2 ).getHeight() < getElement(Constantes.MCDENTITE1 ).getY()) {
			if (getElement( Constantes.MCDENTITE2 ).getX() < getElement( Constantes.MCDENTITE1 ).getX()
					+ getElement( Constantes.MCDENTITE1 ).getHeight()
					&& getElement( Constantes.MCDENTITE1 ).getX() < getElement( Constantes.MCDENTITE2 ).getX()
							+ getElement( Constantes.MCDENTITE2 ).getWidth()) {
				if (x2 > getElement( Constantes.MCDENTITE1 ).getX() + getElement( Constantes.MCDENTITE1 ).getWidth())
					x2 = getElement( Constantes.MCDENTITE1 ).getX() + getElement( Constantes.MCDENTITE1 ).getWidth();
				else if (x2 < getElement( Constantes.MCDENTITE1 ).getX())
					x2 = getElement( Constantes.MCDENTITE1 ).getX();
				x1 = x2;
			}
		}
	}
      
	public String toString() {
		return "MCDLien, " + info();
	}
}
