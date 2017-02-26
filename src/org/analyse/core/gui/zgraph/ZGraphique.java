/*
 * 05/02/2002 - 20:15:18
 *
 * ZGraphique -
 * Copyright (C) 2002 Dreux Loic
 * dreuxl@free.fr
 *
 *  Modifications 
 *  -------------
 *  Date : 2009 janvier 22
 *  @auteur : Bruno Dabo <bruno.dabo@lywoonsoftware.com>
 *  @objet : enlever les limites sur les vecteurs
 *  
 *  Date : 2009 mars 13
 *  @auteur : Benjamin Gandon
 *  @objet : ajouter la sélection multiple
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

import java.awt.AWTEvent;
import java.awt.AWTEventMulticaster;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.JComponent;

import org.analyse.core.util.Constantes;

/**
 * ZGraphique est un composant permettant d'afficher des éléments graphiques, de
 * les déplacer, de les lier. <br>
 * <br>
 * Il est conseiller d'utiliser ce composant dans un <code>JScrollPane<code>.
 * 
 *<pre>
 * Container c = getContentPane();
 * c.setLayout(new BorderLayout());
 * c.add(BorderLayout.CENTER, new JScrollPane(new ZGraphique()));
 * </pre>
 */
public class ZGraphique extends JComponent implements MouseListener,
        MouseMotionListener, Observer
{
    /** Vecteur de <code>ZElement</code> */
    private List<ZElement> zelements = new ArrayList<ZElement>() ;

    /** Vecteur de <code>ZLien</code> */
    private List<ZLien> zliens = new ArrayList<ZLien>() ;

    /**
     * Cette variable sert pour le glisser-déposer. Lorsque l'on clic sur un
     * <code>ZElement</code> la variable est liée à ce <code>ZElement</code>
     * jusqu'à ce qu'on relache la souris
     */
    private ZElement elementPress = null;

    /**
     * Cette variable est liée vers un <code>ZElement</code> sur lequel on a
     * cliqué jusque l'on clique dans le vide ou sur un autre élément.
     */
    protected ZElement elementClic = null;
    /**
     * Cet {@link Set ensemble} contient les {@link ZElement}s de la sélection
     * courante.
     */
    protected Set<ZElement> selectionCourante = new LinkedHashSet<ZElement>();
    protected Set<ZElement> selectionTemporaire = new LinkedHashSet<ZElement>();

    /**
     * Cette variable est liée vers un <code>ZLien</code> sur lequel on a
     * cliqué jusque l'on clique dans le vide ou sur un autre élément.
     */
    protected ZLien lienClic = null;

    protected Point departCadreSelection = null;
    protected Point arriveeCadreSelection = null;

    /**
     * Cette variable est utilisée lors de la création d'un lien.
     */
    private ZLien lienTemp;

    /** Utilisé pour le déplacement des éléments. Contient la position relative par rapport à la souris. */
    private Map<ZElement, Point> positionsRelatives = new HashMap<ZElement, Point>();

    /** Possibilité d'intéragir sur le composant grâce à la souris. */
    private boolean enabled = true;

    /** Enclenche le mode 'création de lien' */
    private boolean creationLien = false;

    /** Indique si en mode 'création de lien', l'utilisateur a cliqué */
    private boolean creationLienClic = false;

    /** Gestion des événement */
    private ActionListener actionListener;

    /** Pour la position du lien */
    private int x1, y1, x2, y2;

    /**
     * Définie un <code>ZGraphique</code> avec les paramètres par défaut.
     */
    public ZGraphique()
    {
    	setEnabled(false);
    	addMouseListener(this);	
        setSize(getPreferredSize());
        setBackground(Color.white);
    }
	
    /**
     * Active ou désactive l'intéraction de l'utilisateur avec le graphique.
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

	/**
     * Retourne la variable enabled.
     */
    public boolean getEnabled()
    {
        return enabled;
    }

    /**
     * Retourne le nombre de <code>ZElement</code>.
     */
    public int sizeElements()
    {
        return zelements.size();
    }

    /**
     * Retourne le nombre de <code>ZElement</code> actuellement sélectionnés.
     */
    public int sizeSelection()
    {
        return selectionCourante.size();
    }

    /**
     * Retourne le nombre de liens.
     */
    public int sizeLien()
    {
        return zliens.size();
    }

    /**
     * Retourne une Enumeration des <code>ZElement</code>.
     */
    public Iterator<ZElement> enumElements()
    {
        return zelements.iterator();
    }

    /**
     * Retourne une énumération des <code>ZLien</code>
     */
    public Iterator<ZLien> enumLiens()
    {
        return zliens.iterator();
    }

    /**
     * Ajoute un <code>ZElement</code> dans le composant.
     */
    public void addElement(ZElement element)
    {
        zelements.add(element);
        element.majObserver(this);
        repaint();
    }

    /**
     * Ajoute plusieurs <code>ZElement</code> dans le composant.
     */
    public void addElements(List<ZElement> elements)
    {
        Iterator<ZElement> e = elements.iterator();
        while (e.hasNext())
            addElement(e.next());
    }

    /**
     * Supprime un <code>ZElement</code> du composant.
     */
    public void removeElement(ZElement element)
    {
        enleverFocus();

        Iterator<ZLien> e = zliens.iterator();
        List<ZLien> supp = new ArrayList<ZLien>();
        while (e.hasNext()) {
            ZLien l = e.next();
            if (l.getElement(Constantes.MCDENTITE1).equals(element)
                    || l.getElement(Constantes.MCDENTITE2).equals(element))
                supp.add(l);
        }
        removeLiens(supp);
        zelements.remove(element);
        repaint();
    }

    /**
     * Ajoute un <code>ZLien</code> dans le composant.
     */
    public void addLien(ZLien lien)  {
    	
        if (lien.getElement(Constantes.MCDENTITE1) != null && lien.getElement(Constantes.MCDENTITE2) != null 
        		&& lien.getElement(Constantes.MCDENTITE1) != lien.getElement(Constantes.MCDENTITE2)
                && !zliens.contains(lien))
    	
            zliens.add(lien);
        repaint();
    }

    /**
     * Ajoute plusieurs <code>ZLien</code> dans le composant.
     */
    public void addLiens(List<ZLien> liens)
    {
        Iterator<ZLien> e = liens.iterator();
        while (e.hasNext())
            addLien(e.next());
    }

    /**
     * Supprime un <code>ZLien</code> du composant.
     */
    public void removeLien(ZLien lien)
    {
        enleverFocus();
        zliens.remove(lien);
        repaint();
    }

    /**
     * Supprime plusieurs <code>ZLien</code> du composant.
     */
    public void removeLiens(List<ZLien> supp)
    {
        for (Iterator<ZLien> e = supp.iterator(); e.hasNext();)
            removeLien(e.next());
    }

    /**
     * Retourne un <code>ZLien</code> du composant.
     * 
     * @param i
     *            index du lien à retourner
     */
    public ZLien getLien(int i)
    {
        return zliens.get(i);
    }

    /**
     * Retourne le lien qui a le focus.
     */
    public ZLien getLienFocus()
    {
        return lienClic;
    }

    /**
     * Retourne une Enumeration des <code>ZLien</code>.
     */
    public Iterator<ZLien> elementsZLiens()
    {
        return zliens.iterator();
    }

    /**
     * Retourne un <code>ZElement</code> du composant.
     * 
     * @param i
     *            index du composant à retourner
     */
    public ZElement getElement(int i)
    {
        return zelements.get(i);
    }

    /**
     * Retourne le composant qui a le focus.
     */
    public ZElement getElementFocus()
    {
        return elementClic;
    }

    /**
     * Retourne une Enumeration des <code>ZElement</code>.
     */
    public Iterator<ZElement> elementsZElements()
    {
        return zelements.iterator();
    }

    /**
     * Retourne le premier {@link ZElement} se trouvant à la position x, y.
     */
    public ZElement chercheElement(int x, int y)
    {
        for (int i = zelements.size() - 1; i >= 0; i--)
            if (getElement(i).isSelected(x, y))
                return getElement(i);
        return null;
    }
    /**
     * Retourne le premier {@link ZLien} se trouvant à la position x, y.
     */
    public ZLien chercheLien(int x, int y)
    {
        for (int i = 0; i < zliens.size(); i++)
            if (getLien(i).isSelected(x, y))
                return getLien(i);
        return null;
    }
    /**
     * Retourne le premier composant se trouvant à la position x, y.
     */
    public Object getObjectFromLocation(int x, int y)
    {
        ZElement elem = chercheElement(x, y);
        if (elem != null)
            return elem;

        ZLien lien = chercheLien(x, y);
        if (lien != null)
            return lien;
        return null;
    }

    /**
     * Enlève le focus de tous les elements.
     */
    public void enleverFocus()
    {
        elementClic = null;
        selectionCourante.clear();
        selectionTemporaire.clear();
        lienClic = null;
    }

    /**
     * Enclenche le mode de "création de liens". Il est alors possible de liée
     * deux elements dans le graphe.
     */
    public void creerLien(ZLien lien)
    {
        enleverFocus();
        lienTemp = lien;
        lienTemp.setElement(null, Constantes.MCDENTITE1);
        lienTemp.setElement(null, Constantes.MCDENTITE2);
        if (zliens.contains(lien))
            removeLien(lien);
        creationLien = true;
        creationLienClic = false;
    }

    /**
     * Désenclenche le mode "création de liens".
     */
    public void annulerCreerLien()
    {
        enleverFocus();

        lienTemp = null;
        creationLien = false;
        creationLienClic = false;
    }

    /**
     * Cette méthode indique si l'on peut créer un lien entre deux éléments.
     */
    public boolean peutCreerLien(ZElement elem1, ZElement elem2)
    {
        ZLien lien;

        Iterator<ZLien> e = zliens.iterator();
        while (e.hasNext()) {
            lien = e.next();
            if (lien.getElement(Constantes.MCDENTITE1) == elem1
                    && lien.getElement(Constantes.MCDENTITE2) == elem2) {
                return false;
            }
        }
        return true;
    }

    public String infoZLiens()
    {
        String s = "{";
        for (Iterator<ZLien> e = elementsZLiens(); e.hasNext();) {
            ZLien lien = e.next();
            s += "\n" + lien;
        }
        return s + "\n}";
    }

    public void clearAll()
    {
        enleverFocus();
        zelements = new ArrayList<ZElement>();
        zliens = new ArrayList<ZLien>();
        repaint();
    }

    /**
     * Dessine le composant.
     */
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        Iterator<ZElement> e = zelements.iterator();
        while (e.hasNext())
            e.next().paint(g);
        
        Iterator<ZLien> e1 = zliens.iterator();
        while (e1.hasNext())
            e1.next().paint(g);

        for (ZElement elem : selectionCourante)
            elem.paintFocus(g);
        for (ZElement elem : selectionTemporaire)
            elem.paintFocus(g);
        if (lienClic != null)
            lienClic.paintFocus(g);

        if (departCadreSelection != null) {
            int x1 = (int)departCadreSelection.getX();
            int y1 = (int)departCadreSelection.getY();
            int x2 = (int)arriveeCadreSelection.getX();
            int y2 = (int)arriveeCadreSelection.getY();
            int w = x2 - x1;
            int h = y2 - y1;
            if (x2 < x1) {
                x1 = x2;
                w = -w;
            }
            if (y2 < y1) {
                y1 = y2;
                h = -h;
            }
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.green);
            g2d.draw(new Rectangle2D.Double(x1, y1, w, h));
        }

        if (creationLien && creationLienClic) {
            Graphics2D g2d = (Graphics2D) g;
            Rectangle2D r;
            g2d.setColor(Color.black);

            Line2D l = new Line2D.Double(x1, y1, x2, y2);
            g2d.draw(l);

            r = new Rectangle2D.Double(x1 - 1, y1 - 1, 4, 4);
            g2d.fill(r);

            r = new Rectangle2D.Double(x2 - 2, y2 - 1, 4, 4);
            g2d.fill(r);
        }
    }

    public Dimension getPreferredSize()
    {
        int x = 0, y = 0;
        for (int i = 0; i < zelements.size(); i++) {
            x = getElement(i).getX() + getElement(i).getWidth() > x ? getElement(
                    i).getX()
                    + getElement(i).getWidth()
                    : x;
            y = getElement(i).getY() + getElement(i).getHeight() > y ? getElement(
                    i).getY()
                    + getElement(i).getHeight()
                    : y;
        }
        return new Dimension(x + 30, y + 30);
    }

    public void update(Observable o, Object arg)
    {
        this.repaint();
    }

    public void addActionListener(ActionListener listener)
    {
        actionListener = AWTEventMulticaster.add(actionListener, listener);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
    }

    public void removeActionListener(ActionListener listener)
    {
        actionListener = AWTEventMulticaster.remove(actionListener, listener);
    }

    public void mouseClicked(MouseEvent e)
    {
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e)
    {  	
    	addMouseMotionListener(this);

        if (creationLien && enabled) {
            creationLienClic = true;
            int x = e.getX();
            int y = e.getY();

            x1 = e.getX();
            y1 = e.getY();

            x2 = e.getX();
            y2 = e.getY();

            ZElement departChoisi = chercheElement(x, y);
            if (departChoisi != null)
                lienTemp.setElement(departChoisi, Constantes.MCDENTITE1);

            repaint();

        } else if (enabled) {
            elementPress = null;
            elementClic = null;
            lienClic = null;

            int x = e.getX();
            int y = e.getY();

            ZElement nouvelleSelection = chercheElement(x, y);
            if (nouvelleSelection != null) {
                elementPress = nouvelleSelection;
                elementClic = nouvelleSelection;
                if ((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == 0
                        && !selectionCourante.contains(nouvelleSelection))
                    selectionCourante.clear();
                selectionCourante.add(nouvelleSelection);
                positionsRelatives.clear();
                for (ZElement elem : selectionCourante)
                    positionsRelatives.put(elem, new Point(x - elem.getX(), y - elem.getY()));
            }
            else {
                ZLien nouvelleSelectionLien = chercheLien(x, y);
                if (nouvelleSelectionLien != null) {
                    lienClic = nouvelleSelectionLien;
                    selectionCourante.clear();
                } else {
                    departCadreSelection = new Point(x, y);
                    arriveeCadreSelection = new Point(x, y);
                    if ((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == 0)
                        selectionCourante.clear();
                }
            }
        }
    }

    public void mouseReleased(MouseEvent e)
    {
        removeMouseMotionListener(this);

        if (creationLien && enabled) {
            creationLien = false;
            ZElement cibleLien = chercheElement(e.getX(), e.getY());
            if (cibleLien != null) {
                lienTemp.setElement(cibleLien, Constantes.MCDENTITE2);
                x2 = e.getX();
                y2 = e.getY();
            }
            if (peutCreerLien(lienTemp.getElement(Constantes.MCDENTITE1), lienTemp
                    .getElement(Constantes.MCDENTITE2))){
                addLien(lienTemp);
            }
            else
                lienTemp.clearElement();
            
            repaint();
        } else if (enabled) {
            elementPress = null;
            if (arriveeCadreSelection != null) {
                selectionCourante.addAll(selectionTemporaire);
                selectionTemporaire.clear();
                departCadreSelection = null;
                arriveeCadreSelection = null;
            }
            repaint();
        }
        if (actionListener != null)
            actionListener.actionPerformed(new ActionEvent(this,
                    ActionEvent.ACTION_PERFORMED, ""));

        /* Correction Bug Graphique */
        if (getPreferredSize().height > getSize().height
                || getPreferredSize().width > getSize().width
        /*
         * || getPreferredSize().height < getSize().height ||
         * getPreferredSize().width < getSize().width
         */)
            this.setSize(getPreferredSize());

    }

    public void mouseDragged(MouseEvent e)
    {
        Point point = new Point();
        
        if (creationLien && enabled) {
            x2 = e.getX();
            y2 = e.getY();
            repaint();
        } else if (enabled) {
            if (elementPress != null) {
                for (Map.Entry<ZElement, Point> entry : positionsRelatives.entrySet()) {
                    Point posRel = entry.getValue();
                    int xCentrage = (int) posRel.getX();
                    int yCentrage = (int) posRel.getY();
                    point.x = e.getX() - xCentrage < 0 ? 0 : e.getX() - xCentrage;
                    point.y = e.getY() - yCentrage < 0 ? 0 : e.getY() - yCentrage;

                    ZElement elem = entry.getKey();
                    elem.setPosition(point);
                }
                repaint();
            }
            else if (arriveeCadreSelection != null) {
                arriveeCadreSelection.x = e.getX();
                arriveeCadreSelection.y = e.getY();
                selectionTemporaire.clear();
                for (ZElement elem : zelements)
                    if (elem.isInside(departCadreSelection.x, departCadreSelection.y, arriveeCadreSelection.x, arriveeCadreSelection.y))
                        selectionTemporaire.add(elem);
                repaint();
            }
        }
    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
    }

    public void mouseMoved(MouseEvent e)
    {
    }
}