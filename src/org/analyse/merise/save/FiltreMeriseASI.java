/*
 * 02/05/2004 - 00:03:04
 *
 * FiltreMeriseASI.java - 
 * Copyright (C) 2004 Dreux Loic
 * dreuxl@free.fr
 * 
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

package org.analyse.merise.save;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;

import org.analyse.core.gui.zgraph.ZElement;
import org.analyse.core.gui.zgraph.ZLien;
import org.analyse.core.modules.SaveModule;
import org.analyse.core.save.asi.FilterASIModule;
import org.analyse.core.util.Constantes;
import org.analyse.merise.gui.table.DictionnaireTable;
import org.analyse.merise.main.MeriseModule;
import org.analyse.merise.mcd.composant.MCDComponent;
import org.analyse.merise.mcd.composant.MCDEntite;
import org.analyse.merise.mcd.composant.MCDLien;
import org.analyse.merise.mcd.composant.MCDObjet;
import org.analyse.merise.save.asi.ASIMeriseHandler;

/**
 * Filtre enregistrant la partie Merise en XML.
 */
public class FiltreMeriseASI extends FilterASIModule implements SaveModule
{
    private DictionnaireTable dico;

    private MCDComponent mcd;

    public FiltreMeriseASI(MeriseModule module)
    {
        super("ASI", module, new ASIMeriseHandler(module));
    }

    /**
     * Implémentation de la class SaveModule.
     */
    public void save(PrintStream out) throws IOException
    {
        dico = ((MeriseModule) module).getDictionnaireTable();
        mcd = ((MeriseModule) module).getMCDComponent();

        saveDictionnaire(out);
        saveMCD(out);
    }

    /**
     * Sauvegarde du dictionnaire.
     */
    private void saveDictionnaire(PrintStream out) throws IOException
    {
        out.println("<dictionnaire>");

        for (int i = 0; i < dico.getRowCount() - 1; i++) {
            out.println("<information code=\""
                    + dico.getID(i)
                    + "\" nom=\""
                    + dico.getValue(dico.getID(i), DictionnaireTable.NAME)
                    + "\" type=\""
                    + dico.getValue(dico.getID(i), DictionnaireTable.TYPE)
                    + "\" taille=\""
                    + dico.getValue(dico.getID(i), DictionnaireTable.SIZE)
                    + "\" utilise=\""
                    + (((Boolean) dico.getValue(dico.getID(i),
                            DictionnaireTable.USE)).booleanValue() ? "true"
                            : "false") + "\" />");
        }

        out.println("</dictionnaire>");
    }

    /**
     * Sauvegarde du MPD.
     */
    private void saveMCD(PrintStream out) throws IOException
    {
        out.println("<mcd>");

        for (Iterator<ZElement> e = mcd.enumElements(); e.hasNext();) {
            MCDObjet o = (MCDObjet) e.next();
            if (o instanceof MCDEntite)
                out.println("<entite nom=\"" + o.getName() + "\" x=\""
                        + o.getX() + "\" y=\"" + o.getY() + "\">");
            else
                out.println("<association nom=\"" + o.getName() + "\" x=\""
                        + o.getX() + "\" y=\"" + o.getY() + "\">");

            for (int i = 0; i < o.sizeInformation(); i++) {
                out.println("<information code=\"" + o.getCodeInformation(i)
                        + "\" />");
            }

            if (o instanceof MCDEntite)
                out.println("</entite>");
            else
                out.println("</association>");
        }

        for (Iterator<ZLien> e = mcd.enumLiens(); e.hasNext();) {
            MCDLien l = (MCDLien) e.next();
            out.println("<lien cardmin=\"" + l.getCardMin() + "\" cardmax=\""
                    + l.getCardMax() + "\" elem1=\""
                    + ((MCDObjet) l.getElement( Constantes.MCDENTITE1 )).getName() + "\" elem2=\""
                    + ((MCDObjet) l.getElement( Constantes.MCDENTITE2 )).getName() + "\" />");
        }

        out.println("</mcd>");
    }
}