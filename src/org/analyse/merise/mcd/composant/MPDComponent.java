/*
 * 03/26/2002 - 21:14:14
 * 
 * MPD - Copyright (C) 2002 Dreux Loic zgoblin@linuxfreemail.com
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
import java.awt.Dimension;
import java.util.Hashtable;
import java.util.Iterator;

import org.analyse.core.gui.zgraph.ZElement;
import org.analyse.core.gui.zgraph.ZGraphique;
import org.analyse.core.util.Constantes;
import org.analyse.core.util.Utilities;
import org.analyse.main.Main;
import org.analyse.merise.gui.panel.SQLPanel;
import org.analyse.merise.gui.table.DictionnaireTable;
import org.analyse.merise.main.MeriseModule;
import org.analyse.merise.sql.SQLCommand;

public class MPDComponent extends ZGraphique {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8221026150395069033L;
	private double width;
    protected Hashtable<String, String> foreignKeys;

    public MPDComponent() {
        super();

        foreignKeys = new Hashtable<String, String>();

        setEnabled(false);
        setBackground(Color.white);
    }

    /**
     * @return la taille du composant
     */
    public Dimension getPreferredSize() {
        int x = 0, y = 0;
        for (Iterator<ZElement> e = enumElements(); e.hasNext();) {
            MPDEntite o = (MPDEntite) e.next();
            x = o.getX() + o.getWidth() > x ? o.getX() + o.getWidth() : x;
            y = o.getY() + o.getHeight() > y ? o.getY() + o.getHeight() : y;
        }
        return new Dimension(x + 30, y + 30);
    }

    /**
     * @return un MPDEntite
     * @param name
     *            Nom de l'entité à retourner
     */
    public MPDEntite getMPDEntite(String name) {
        for (Iterator<ZElement> e = enumElements(); e.hasNext();) {
            MPDEntite o = (MPDEntite) e.next();
            if (o.getName().equals(name)) {
                return o;
            }
        }
        return null;
    }

    public MPDEntite getMPDEntite(int i) {
        return (MPDEntite) (getElement(i));
    }

    /**
     * Modifie la largeur du <code>MPDComponent</code>
     */
    public void setWidth(double width) {
        this.width = width;
    }

    /**
     * Ajoute une entité au vecteur.
     */
    public void addMPDEntite(MPDEntite ent) {
        addElement(ent);

        repaint();
    }

    public void addForeignKey(String cleEtrangere, String table) {
        foreignKeys.put(cleEtrangere, table);
    }

    public String toString() {
        String res = "{\n";
        for (Iterator<ZElement> e = enumElements(); e.hasNext();) {
            res += e.next() + "\n";
        }
        return "}" + res;
    }

    /**
     * Construit les requêtes SQL.
     */
    public void buildSQL(DictionnaireTable data, SQLCommand sql) {
        String text, info ;
        MPDEntite ent;
        int cmp, nbId;

        // SQL syntax.
        MeriseModule meriseModule = (MeriseModule) Main.getModule("MERISE");
        String sqlSyntax = ((SQLPanel)meriseModule.getSQLPanel()).getSQLSyntax();

        sql.clear();

        for (Iterator<ZElement> e = enumElements(); e.hasNext();) {
            ent = (MPDEntite) (e.next());
            text = "DROP TABLE IF EXISTS " + Utilities.normaliseString(ent.getName(), Constantes.LOWER) + " ;";            
            sql.addRequest(text);
            
            text = "CREATE TABLE " + Utilities.normaliseString(ent.getName(), Constantes.LOWER) + " (";
            
            cmp = 0;
            nbId = ent.sizeIdentifiant();

            /*
             * bruno
             * date : mars 2009
             * je rajoute une surcouche pour la gestion des relations reflexives. 
             * Code à améliorer progressivement lors de la refonte  
             */
            Boolean premierefois = true, premier_auto_increment = true;
            String defautType = "";
            String defautSize = "";
            String type = "";

            for (Iterator<String> e2 = ent.elementsInformations(); e2.hasNext(); cmp++) {

                info = (String) (e2.next());

                if (premierefois) {  // ajout des relations reflexives
                    premierefois = false;
                    defautType = (String) data.getValue(info, DictionnaireTable.TYPE);

                    if (((Integer) data.getValue(info, DictionnaireTable.SIZE)).intValue() != 0
                            && !sql.getTypesWithoutSize().contains(data.getValue(info, DictionnaireTable.TYPE))) {
                        defautSize = String.valueOf(data.getValue(info, DictionnaireTable.SIZE));
                    }
                }

                try {
                    type = (String) data.getValue(info, DictionnaireTable.TYPE);
                } catch (Exception ex) {
                    type = defautType;
                }

                // PostgreSQL specific auto increment: SERIAL
                if (premier_auto_increment && sqlSyntax.equals(SQLCommand.SQLsyntax.PostgreSQL.toString())) {
                    if(type.equals(Constantes.INT)) {
                        type = Constantes.INT_AUTO_INCREMENT_POSTGRESQL;
                    } else if(type.equals(Constantes.BIGINT)) {
                        type = Constantes.BIGINT_AUTO_INCREMENT_POSTGRESQL;
                    }
                }

                // PostgreSQL: convert DATETIME to TIMESTAMP
                if (sqlSyntax.equals(SQLCommand.SQLsyntax.PostgreSQL.toString())) {

                    if (type.equals(Constantes.DATETIME)) {
                        type = Constantes.TIMESTAMP_POSTGRESQL;
                    }
                }

                if (premier_auto_increment) {
                	/* 
                	 * traiter le cas des relations ternaires   
                	 */
                	
                    if ("INT_AUTO_INCREMENT".equals(type)) {
                        type = Constantes.INT_AUTO_INCREMENT ;  // Bug #567501
                    }
                    if ("BIGINT_AUTO_INCREMENT".equals(type)) {
                        type = Constantes.BIGINT_AUTO_INCREMENT;  // Bug #567501
                    }
                } else {
                    if ("BIGINT_AUTO_INCREMENT".equals(type)) {
                        type = "BIGINT";
                    }   // evite de se retrouver avec AUTO_INCREMENT sur une clé étrangère
                    if ("INT_AUTO_INCREMENT".equals(type)) {
                        type = "INT";
                    }   // evite de se retrouver avec AUTO_INCREMENT sur une clé étrangère
                }
                
                info = Utilities.normaliseString(info, Constantes.LOWER);  // Bug #622229
                text += info + " " + type;

                try {
                    if (((Integer) data.getValue(info, DictionnaireTable.SIZE)).intValue() != 0
                            && !sql.getTypesWithoutSize().contains(data.getValue(info, DictionnaireTable.TYPE))) {
                        text += "(" + data.getValue(info, DictionnaireTable.SIZE) + ")";
                    }
                } catch (Exception ex) {

                    if (!defautSize.equals("")) {
                        text += "(" + defautSize + ")";
                    }
                }

                if (premier_auto_increment) {
                    // Only for MySQL syntax.
                    if (sqlSyntax.equals(SQLCommand.SQLsyntax.MySQL.toString())) {
                        text += " AUTO_INCREMENT";
                    }
                }

                if (cmp < nbId) {
                    text += " NOT NULL";
                } else {
                    if (!ent.isForeignKeyCanBeNull()) {

                        if (ent.getForeignKey(info) != null) // on a affaire à une clé étrangère
                        {
                            text += " NOT NULL";
                        }
                    }
                }

                if (e2.hasNext()) {
                    text += ",";
                    premier_auto_increment = false;
                }
            }


            nbId = ent.sizeIdentifiant();
            //liaison reflexive
            //  if ( ent.informations.size() != nbId )
            //	  continue ;

            text += ",PRIMARY KEY (";
            
            cmp = 0;
            nbId = ent.sizeIdentifiant();

            for (Iterator<String> e2 = ent.elementsInformations(); e2.hasNext()
                    && cmp < nbId;) {

                text += Utilities.normaliseString((String) (e2.next()), Constantes.LOWER);  // Bug #622229
                cmp++;
                if (cmp < nbId) {
                    text += ", ";
                }
            }

            text += "))";

            // MySQL syntax
            if (sqlSyntax.equals(SQLCommand.SQLsyntax.MySQL.toString()))
            {
                text += " ENGINE=InnoDB;";
            }
            else {  // other syntax (PostgreSQL, ...)
                text += ";";
            }

            sql.addRequest(text);
        }


        info = "";
        for (Iterator<ZElement> e = enumElements(); e.hasNext();) {
           
            ent = (MPDEntite) (e.next());

            if (!ent.foreignKeysIsEmpty()) {
                for (Iterator<String> e2 = ent.elementsInformations(); e2.hasNext();) {

                    info = (String) (e2.next());

                    if (ent.getTableForeignKey(info) != null) {
                    		
                                // Bug #622229
                                text = "ALTER TABLE "
                                        + Utilities.normaliseString(ent.getName(),
                                        Constantes.LOWER) + " ADD CONSTRAINT FK_"
                                        + Utilities.normaliseString(ent.getName(),
                                        Constantes.LOWER) + "_" + Utilities.normaliseString(info, Constantes.LOWER)
                                        + " FOREIGN KEY (" + Utilities.normaliseString(info, Constantes.LOWER) + ") REFERENCES "
                                        + ent.getTableForeignKey(info) + " (" + Utilities.normaliseString( this.getMPDEntite ( ent.getTableForeignKey(info) ).getCodeInformation(0), Constantes.LOWER)
                                        + ");";
                        sql.addRequest(text);
                    }
                }
            }
        }
    }

    /**
     *  
     */
    public void clear() {
        foreignKeys.clear();
        super.clearAll();
    }
}
