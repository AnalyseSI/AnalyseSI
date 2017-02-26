/*
 * 02/06/2004 - 16:17:34
 * 
 * ASIMeriseHandler.java - Copyright (C) 2004 Dreux Loic dreuxl@free.fr
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

package org.analyse.merise.save.asi;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import org.analyse.core.save.asi.ASIModuleHandler;
import org.analyse.core.util.Constantes;
import org.analyse.merise.gui.table.DictionnaireTable;
import org.analyse.merise.main.MeriseModule;
import org.analyse.merise.mcd.composant.MCDAssociation;
import org.analyse.merise.mcd.composant.MCDComponent;
import org.analyse.merise.mcd.composant.MCDEntite;
import org.analyse.merise.mcd.composant.MCDLien;
import org.analyse.merise.mcd.composant.MCDObjet;

public class ASIMeriseHandler extends ASIModuleHandler
{
    private DictionnaireTable dictionnaireTable;

    private MCDComponent mcd;

    private int etape;

    private Map<String, String> hashtable;

    private MCDObjet lastObj;

    private static final int DICTIONNAIRE = 0;

    private static final int MCD = 1;

    public ASIMeriseHandler(MeriseModule module)
    {
        super(module);

        hashtable = new HashMap<String, String>();

        dictionnaireTable = module.getDictionnaireTable();
        mcd = module.getMCDComponent();
    }

    public void attribute(String aname, String value, boolean isSpecified)
    {
        hashtable.put(aname, value);
    }

    public void startElement(String name)
    {
        if ("DICTIONNAIRE".equalsIgnoreCase(name))
            etape = DICTIONNAIRE;
        else if ("MCD".equalsIgnoreCase(name))
            etape = MCD;

        if (etape == DICTIONNAIRE)
            return;

        if ("ENTITE".equalsIgnoreCase(name)) {
            lastObj = new MCDEntite(mcd, hashtable.get("nom"),
                    (new Integer(hashtable.get("x"))).intValue(),
                    (new Integer(hashtable.get("y"))).intValue());
            lastObj.setPosition(new Point(new Integer(hashtable
                    .get("x")).intValue(), new Integer(hashtable
                    .get("y")).intValue()));
            mcd.addObjet(lastObj);

        } else if ("ASSOCIATION".equalsIgnoreCase(name)) {
            lastObj = new MCDAssociation(mcd, hashtable.get("nom"),
                    (new Integer(hashtable.get("x"))).intValue(),
                    (new Integer(hashtable.get("y"))).intValue());
            lastObj.setPosition(new Point(new Integer(hashtable
                    .get("x")).intValue(), new Integer(hashtable
                    .get("y")).intValue()));
            mcd.addObjet(lastObj);
        }
    }

    public void endElement(String name)
    {
        switch (etape) {
            case DICTIONNAIRE:
                if ("INFORMATION".equalsIgnoreCase(name)) {
                    dictionnaireTable.addData(hashtable.get("code"),
                            hashtable.get("nom"), hashtable
                                    .get("type"), hashtable
                                    .get("taille"), hashtable
                                    .get("utilise"));
                }
                break;
            case MCD:
                if ("INFORMATION".equalsIgnoreCase(name) && lastObj != null)
                    lastObj.addInformation(hashtable.get("code"));

                else if ("LIEN".equalsIgnoreCase(name)) {
                    MCDLien lien = new MCDLien();

                    lien.setElement(mcd.getElement(hashtable
                            .get("elem1")),  Constantes.MCDENTITE1 );
                    lien.setElement(mcd.getElement(hashtable
                            .get("elem2")),  Constantes.MCDENTITE2 );

                    lien.setCardMin(hashtable.get("cardmin"));
                    lien.setCardMax(hashtable.get("cardmax"));

                    lien.updateLocation();

                    mcd.addLien(lien);
                } else if ("MCD".equalsIgnoreCase(name)) {
                    mcd.setPreferredSize(mcd.getPreferredSize());
                }
                break;
        }
    }
}