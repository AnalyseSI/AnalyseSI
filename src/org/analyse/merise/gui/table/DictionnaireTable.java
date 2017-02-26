/*
 * 05/23/2003 - 16:41:45
 *
 * DictionnaireTable.java -
 * Copyright (C) 2003 Dreux Loic
 * dreuxl@free.fr
 *
 *  Modifications 
 *  -------------
 *  Date : 2009 janvier 22
 *  @auteur : Bruno Dabo <bruno.dabo@lywoonsoftware.com>
 *  
 *  Date : 2017 Février 04
 *  @auteur : Mehdi CHAABANI
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

package org.analyse.merise.gui.table;

import java.util.*;

import javax.swing.table.AbstractTableModel;

import org.analyse.core.gui.zgraph.ZElement;
import org.analyse.core.util.Constantes;
import org.analyse.core.util.Utilities;
import org.analyse.main.Main;
import org.analyse.merise.main.MeriseModule;
import org.analyse.merise.mcd.composant.MCDObjet;

/**
 * Cette table contient les informations utilisables par le MCD.
 */
public class DictionnaireTable extends AbstractTableModel
{
    public static final int UP = 0;

    public static final int DOWN = 1;

    public static final int NAME = 0;

    public static final int ID = 1;

    public static final int TYPE = 2;

    public static final int SIZE = 3;

    public static final int USE = 4;

    public static final int ENTITY = 5;

    /** Nom des 6 colonnes. */
    private final String[] columnNames = {
            Utilities.getLangueMessage (Constantes.MESSAGE_NOM),
            Utilities.getLangueMessage (Constantes.MESSAGE_ID),
            Utilities.getLangueMessage (Constantes.MESSAGE_TYPE),
            Utilities.getLangueMessage (Constantes.MESSAGE_TAILLE),
            Utilities.getLangueMessage (Constantes.MESSAGE_UTILISE),
            Utilities.getLangueMessage (Constantes.MESSAGE_ENTITE)  // Bug #712439
    };

    /** Données contenu dans la table. */
    private ArrayList<Object[]> rows = new ArrayList<Object[]>();

    /** Différents types de données possibles. */
    private List<String> types;

    private Observable observable;

    /**
     * Créer une nouvelle <code>DictionnaireTable</code>.
     *
     */
    public DictionnaireTable(List<String> list)
    {
        this.types = list;

        //data = new Object[0][5];

        //sizeRow = 0;
        addNewLine();

        observable = new Observable() {
            public void notifyObservers()
            {
                setChanged();
                super.notifyObservers();
            }

            public void notifyObservers(Object arg)
            {
                setChanged();
                super.notifyObservers(arg);
            }
        };
    }

    /**
     * Ajoute un observateur sur la table. Utilisé par les classes MCDObject.
     */
    public void addObserver(Observer obs)
    {
        observable.addObserver(obs);
    }

    /**
     * Supprime un observateur de la table.
     */
    public void deleteObserver(Observer obs)
    {
        observable.deleteObserver(obs);
    }

    /**
     * Ajoute une ligne vide dans le tableau
     */
    public void addNewLine()
    {
        //sizeRow++;

        /*if (sizeRow >= data.length) {
            Object[][] dataSv = new Object[sizeRow + 10][5];
            ;

            for (int i = 0; i < sizeRow - 1; i++)
                dataSv[i] = data[i];

            this.data = dataSv;
        }*/
        Object[] tab = new Object[6];

        tab[0] = "";
        tab[1] = "";
        tab[2] = types.get(0);
        tab[3] = new Integer(0);
        tab[4] = new Boolean(false);
        tab[5] = "";

        rows.add(tab);
        fireTableDataChanged();
    }

    /**
     * Ajoute une nouvelle donnée dans le tableau
     */
    public void addData(String code, String nom, String type, String taille,
                        String utilise)
    {
        Object[] tab = new Object[6];
        tab[0] = nom;
        tab[1] = code;
        tab[2] = type;

        //Bug #612891
        try {
            tab[3] = new Integer (taille) ;
        } catch (Exception e) {
            tab[3] = new Integer (0) ;
        }

        tab[4] = new Boolean(utilise);
        tab[5] = "";
        rows.set(rows.size()-1, tab);
        addNewLine();
    }

    public void addData(String nom, String type, String taille, String entity)
    {
        Integer tailleInt;

        try {
            tailleInt = new Integer(taille);
        } catch (NumberFormatException e) {
            tailleInt = new Integer(0);
        }

        if (!contains(Utilities.normaliseString(nom, Constantes.LOWER))) {
            Object[] tab = new Object[6];
            tab[0] = nom;
            tab[1] = Utilities.normaliseString(nom,Constantes.LOWER) + "_" + entity;
            tab[2] = type;
            tab[3] = tailleInt;
            tab[4] = new Boolean(false);
            tab[5] = entity;
            rows.set(rows.size()-1, tab);
            addNewLine();
        }
    }

    /**
     * Supprime plusieurs lignes.
     */
    public void delLines(int[] indexRows)
    {
        // modif bellier.l  -   merci pour le code
        //la fonction remove d'une arraylist effectue également un rétractage
        //indiceRow contient des indices "erronés" d'où l'intéret de delay
        for (int i = indexRows.length - 1; i >=0 ; i--) {
            observable.notifyObservers(new ArgObserverTable(
                    ArgObserverTable.DELETE, (String) rows.get(indexRows[i])[1]));
            rows.remove(indexRows[i]);
        }

        //Actualise le tableau
        fireTableDataChanged();
    }

    /**
     * Déplace une série de ligne dans la direction demandée.
     *
     * @param indexRows
     *            liste des index des lignes
     * @param direction
     *            direction bas ou haut
     */
    public void moveLines(int indexRows[], int direction){

        if(indexRows.length <= 0) return;
        int firstIndex = indexRows[0];
        int lastIndex = indexRows[indexRows.length-1];

        if(direction == UP){
            if(firstIndex == 0) return;
            rows.add(lastIndex, rows.remove(firstIndex-1));
        }
        else if(direction == DOWN){
            if(lastIndex == rows.size()-2) return;
            rows.add(firstIndex, rows.remove(lastIndex+1));

        }
        //Actualise le tableau
        fireTableDataChanged();
    }

    /**
     * Indique si une donnée est présente dans la table
     *
     * @param code
     *            code de la donnée.
     */
    public boolean contains(String code)
    {
        /*for (int i = 0; i < rows.size(); i++)
            if (code.equals((String) (rows.get(i)[1])))
                return true;*/
        return false;
    }

    /**
     * Retourne l'identifiant d'une information
     *
     * @param i
     *            index du code à retourner
     */
    public String getID(int i)
    {
        if (i < rows.size())
            return (String) rows.get(i)[1];
        return null;
    }

    /**
     * Retourne la valeur d'une case selon l'identifiant et la columns.
     */
    public Object getValue(String ID, int col)
    {
        return getValueAt(getIndex(ID), col);
    }

    /**
     * Retourne la valeur d'une case.
     *
     * @param row
     *            ligne de la case
     * @param col
     *            colone de la case
     */
    public Object getValueAt(int row, int col)
    {

        try {  // protéger un peu plus notre code - suite à un bug remonté
            if (col == ENTITY) {    // afficher l'entité dans laquelle la propriété est utilisée (Bug #712439).
                return getEntityNameOfProperty(row);
            } else {
                return rows.get(row)[col];
            }
        } catch (Exception $) {
            return "**NOT FOUND**" ;

        }

    }

    /**
     * Indique si une donnée est utilisé dans le MCD.
     */
    public boolean getUse(int row)
    {
        return ((Boolean) rows.get(row)[USE]).booleanValue();
    }

    /**
     * Modifie la valeur USE dans la table. <br>
     * Utilisé par le MCD
     */
    public void setUse(String ID, boolean use)
    {
        int row = getIndex(ID);
        if (row == -1)
            return;

        rows.get(row)[USE] = new Boolean(use);

    }

    /**
     *
     * Retourne le numéro de ligne d'une donnée.
     *
     * @param ID
     *            identifiant de la donnée
     */
    public int getIndex(String ID)
    {
        for (int i = 0; i < rows.size(); i++)
            if (ID.equals(rows.get(i)[1]))
                return i;
        return -1;
    }

    /**
     * Vérifie que toutes les informations sont dans le MCD.
     */
    public boolean allUse()
    {
        for (int i = 0; i < rows.size() - 1; i++)
            if (!((Boolean) rows.get(i)[4]).booleanValue())
                return false;
        return true;
    }

    /**
     * Retourne le nombre de colonnes.
     */
    public int getColumnCount()
    {
        return columnNames.length;
    }

    /**
     * Retourne le nombre de lignes.
     */
    public int getRowCount()
    {
        return rows.size();
    }

    /**
     * Retourne le nom d'une colonne.
     *
     * @param col
     *            index de la colonne
     */
    public String getColumnName(int col)
    {
        return columnNames[col];
    }

    /**
     * Retourne le type d'une information selon la colonne.
     *
     * @param col
     *            colonne
     */
    public Class<? extends Object> getColumnClass(int col)
    {
        return getValueAt(0, col).getClass();
    }

    /**
     * Retourne les différents types de données.
     */
    public List<String> getTypes()
    {
        return types;
    }

    public boolean verifySize(int i)    {
    	
    	/*
    	 * Reprendre la fonctionnalité de vérification des types
    	 * pour la v0.7
    	 */
        return true;
    	
    	/*
    	int size = ((Integer)data[i][DictionnaireTable.SIZE]).intValue();
    	
    	if(data[i][DictionnaireTable.TYPE].equals(Constantes.MYSQL_VARCHAR) && !(size > 0))
    		return false;
    	if(data[i][DictionnaireTable.TYPE].equals(Constantes.MYSQL_INT) && (size > 0))
    		return false;
    	if(data[i][DictionnaireTable.TYPE].equals("bigint") && (size > 0))
    		return false;
    	if(data[i][DictionnaireTable.TYPE].equals("bit") && (size > 0))
    		return false;
    	if(data[i][DictionnaireTable.TYPE].equals("datetime") && (size > 0))
    		return false;
    	if(data[i][DictionnaireTable.TYPE].equals("float") && (size > 0))
    		return false;
    	if(data[i][DictionnaireTable.TYPE].equals("tinyint") && (size > 0))
    		return false;
    	if(data[i][DictionnaireTable.TYPE].equals("date") && (size > 0))
    		return false;
    	if(data[i][DictionnaireTable.TYPE].equals("time") && (size > 0))
    		return false;
    	if(data[i][DictionnaireTable.TYPE].equals("bool") && (size > 0))
    		return false;
    	if(data[i][DictionnaireTable.TYPE].equals("int2") && (size > 0))
    		return false;
    	if(data[i][DictionnaireTable.TYPE].equals("int4") && (size > 0))
    		return false;
    	if(data[i][DictionnaireTable.TYPE].equals("int8") && (size > 0))
    		return false;
    	if(data[i][DictionnaireTable.TYPE].equals("float4") && (size > 0))
    		return false;
    	if(data[i][DictionnaireTable.TYPE].equals("float8") && (size > 0))
    		return false;
    	
    	*/

    }

    /**
     * Indique si une case est éditable ou pas.
     */
    public boolean isCellEditable(int row, int col)
    {
        return !(col == ID || col == USE || col == ENTITY);
    }

    /**
     * Modifie une cellule.
     */
    public void setValueAt(Object value, int row, int col)
    {
        // Sauvegarde l'ancien identifiant.
        String oldID = (String) rows.get(row)[1];

        // Vérifie que l'identifiant n'existe pas déjà.
        if ((col == NAME
                && contains(Utilities.normaliseString((String) value,
                Constantes.LOWER)) && getIndex(Utilities
                .normaliseString((String) value, Constantes.LOWER)) != row)
                || (col == NAME && value.equals(""))
                || (!isCellEditable(row, col)))
            return;

        // Transforme la chaine de caractère en Integer
        if (rows.get(0)[col] instanceof Integer && value instanceof String) {
            try {
                rows.get(row)[col] = new Integer(value.toString());
            } catch (NumberFormatException e) {
            }
        } else {
            rows.get(row)[col] = value;

            if (col == NAME) {
                rows.get(row)[ID] = Utilities.normaliseString(
                        (String) (rows.get(row)[NAME]), Constantes.LOWER);
                observable
                        .notifyObservers(new ArgObserverTable(
                                ArgObserverTable.RENAME, oldID,
                                (String) rows.get(row)[ID]));
                fireTableCellUpdated(row, ID);
            }

        }
        // Ajoute une ligne si row
        if (row == getRowCount() - 1 && rows.get(row)[NAME] != null)
            addNewLine();
        fireTableCellUpdated(row, col);
    }

    /**
     * Vide le tableau
     */
    public void clear()
    {
        rows.clear();
        addNewLine();

        observable.deleteObservers();

        fireTableDataChanged();
    }

    /**
     * Retourne le nom de l'entité dans laquelle la propriété (de la row) est utilisée.
     * (Bug #712439)
     *
     * @param row
     *             ligne de la case (propriété du dictionnaire)
     *
     * @return Le nom de l'entité dans laquelle la propriété (de la row) est utilisée.
     */
    private String getEntityNameOfProperty(int row)
    {
        MeriseModule meriseModule = (MeriseModule)Main.getModule("MERISE");

        for(Iterator<ZElement> e = meriseModule.getMCDComponent().enumElements(); e.hasNext();) {
            MCDObjet o = (MCDObjet)e.next();
            for (int i = 0; i < o.sizeInformation(); i++) {
                if (rows.get(row)[ID] != null && o.getCodeInformation(i).equals(rows.get(row)[ID])) {
                    return o.getName();
                }
            }
        }

        return "";
    }
}
