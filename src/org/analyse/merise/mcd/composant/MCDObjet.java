/*
 * 02/26/2002 - 12:12:38
 * 
 * MCDObjet - Copyright (C) 2002 Dreux Loic dreuxl@free.fr
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

import java.awt.Font;
import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.analyse.core.gui.zgraph.ZElement;
import org.analyse.merise.gui.table.ArgObserverTable;
import org.analyse.merise.gui.table.DictionnaireTable;

public abstract class MCDObjet extends ZElement implements Observer
{
    /** Vecteur contenant les informations de l'objet */
    protected List<String> informations = new ArrayList<String>();

    protected List<MCDLien> links = new ArrayList<MCDLien>();

    /**
     * Numéro qui permet de donner un nom temporaire au MCDEntité et
     * MCDAssociation
     */
    private static int index = 0 ;

    /** Nom de l'objet */
    protected String name;

    /** Sauvegarde du nom */
    private String nameSave;

    private int sizeSave;

    /** Font de l'association */
    protected Font font;

    /** Permet de calculer la taille */
    protected FontMetrics fm;

    /** Table dicos des infos */
    protected DictionnaireTable data;

    /** MCDComponent */
    protected MCDComponent mcd;

    public MCDObjet(MCDComponent mcd, String name, int x, int y, int height,
            int width)
    {
        super(mcd, x, y, width, height);
        this.name = name;
        this.nameSave = name;
        this.mcd = mcd;
        this.data = mcd.getData();
        index++;
        sizeSave = 0;
    }

    /**
     * Calcule la taille de l'objet.
     */
    public abstract void updateSize();

    /**
     * Retourne le MCD contenant le <code>MCDObjet</code>.
     */
    public MCDComponent getMCD()
    {
        return mcd;
    }

    /**
     * Vérifie si le nom à changer
     */
    public boolean isChanged()
    {
        if (name != nameSave || sizeInformation() != sizeSave) {
            nameSave = name;
            sizeSave = sizeInformation();
            return true;
        }
        return false;
    }

    /**
     * Ajoute un lien passé en paramètre.
     */
    void addLink(MCDLien link)
    {
        links.add(link);
    }

    /**
     * Supprime le lien passé en paramètre.
     */
    void delLink(MCDLien link)
    {
        links.remove(link);
    }

    /**
     * Retourne une <code>Enumeration</code> des liens.
     */
    public Iterator<MCDLien> links()
    {
        return links.iterator();
    }

    /**
     * retourne le nombre de lien de l'objet
     */
    public int sizeLink()
    {
        return links.size();
    }

    /**
     * Retourne le numéro de l'objet <br>
     * Utilisé pour numéroté les objets.
     */
    public static int getIndex()
    {
        return index;
    }

    /**
     * Ajouter plusieurs informations en indiquant leur code provenant du
     * dictionnaire des informations.
     * 
     * @param code
     *            Vecteur de string contenant les différents codes.
     */
    public void addInformations(String[] code)
    {
        for (int i = 0; i < code.length; i++)
            addInformation(code[i]);
        updateSize();
        notifyZElement();
    }

    /**
     * Ajouter une information en indiquant son code provenant du dictionnaire
     * des informations.
     * 
     * @param code
     *            code de l'information.
     */
    public void addInformation(String code)
    {
        informations.add(code);
        notifyZElement();
    }

    /**
     * Change de place les deux éléments du <I>Vector informations </I>.
     * 
     * @param info1
     *            première information à déplacer
     * @param info2
     *            deuxième information à déplacer
     */
    public void moveInformations(int info1, int info2)
    {
        if (info1 < informations.size() && info2 < informations.size()) {
        	String temp = informations.get(info1);
            informations.set(info1, informations.get(info2));
            informations.set(info2, temp);
        }
        notifyZElement();
    }

    /**
     * Libère les informations du <code>MCDObjet</code>.
     */
    public void clearInformations()
    {
        for (Iterator<String> e = informations.iterator(); e.hasNext();) {
            data.setUse(e.next(), false);
        }
        informations.clear();
        notifyZElement();
    }

    /**
     * Supprime tous les informations dont leurs codes sont passés en
     * paramètres.
     * 
     * @param code
     *            codes des informations à supprimer.
     */
    public void deleteInformations(String[] code)
    {
        for (int i = 0; i < code.length; i++)
            deleteInformation(code[i]);
        updateSize();
        notifyZElement();
    }

    /**
     * Supprime l'information dont son code est passé en paramètre.
     * 
     * @param code
     *            codes des informations à supprimer.
     */
    public void deleteInformation(String code)
    {
        data.setUse(code, false);
        if (informations.remove(code))
            notifyZElement();
    }

    /**
     * retourne le nombre d'informations dans l'objet.
     */
    public int sizeInformation()
    {
        return informations.size();
    }

    /**
     * retourne le code d'une informations.
     * 
     * @param i
     *            index de l'information à retourner
     */
    public String getCodeInformation(int i)
    {
        return informations.get(i);
    }

    public abstract List<String> getInformations();
    public abstract void setInformations(  List<String> v );
    /**
     * Renvoie toutes les infos.
     */
    public Iterator<String> elementsInformations()
    {
        return informations.iterator();
    }

    /**
     * Retourne le nom du <code>MCDObjet</code>
     */
    public String getName()
    {
        return name;
    }

    /**
     * Modifie le nom du <code>MCDObjet</code>
     */
    public void setName(String name)
    {
        this.name = name;
        notifyZElement();
    }

    /**
     * Initialise les numéros des MCDObjet à 1 lorsqu'un nouveau fichier est
     * créé
     */
    public static void clearIndex()
    {
        index = 0;
    }

    public String toString()
    {
        return "MCDObjet : " + info();
    }

    public String info()
    {
        return name + ", " + super.info();
    }

    public void update(Observable o, Object argObject)
    {
        if (argObject == null)
            return;

        ArgObserverTable arg = (ArgObserverTable) argObject;

        if (arg.getType() == ArgObserverTable.RENAME) {
            for (int i = 0; i < informations.size(); i++)
                if (informations.get(i).equals(arg
                        .getOldName()))
                    informations.set(i, arg.getNewName());
        } else if (arg.getType() == ArgObserverTable.DELETE) {
            deleteInformation(arg.getName());
        }
    }
}
