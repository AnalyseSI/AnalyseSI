/*
 * 02/03/2004 - 15:08:06
 *
 * FiltreASI.java -
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

package org.analyse.core.save;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.analyse.core.modules.AnalyseModule;
import org.analyse.core.modules.FilterModule;
import org.analyse.core.modules.SaveModule;
import org.analyse.core.save.asi.ASIHandler;
import org.analyse.core.util.Constantes;
import org.analyse.core.util.GUIUtilities;
import org.analyse.core.util.save.AnalyseFilter;
import org.analyse.core.util.save.Open;
import org.analyse.core.util.save.Save;
import org.analyse.main.Main;

import com.microstar.xml.XmlException;
import com.microstar.xml.XmlParser;

/**
 * La classe <code>FiltreASI</code> représente un filtre qui permet de
 * sauvegarder et de récupérer les données depuis un fichier externe de type
 * XML.
 */
public class FiltreASI extends AnalyseFilter implements Save, Open, Runnable
{	
    private static final boolean OPEN = true;

    private static final boolean SAVE = false;

    private File file;

    private boolean action;

    /**
     * Créer un nouveau Filtre propre à l'application AnalyseSI. Ce filtre gère
     * les fichiers XML.
     */
    public FiltreASI()
    {
        super("ASI", "asi");
    }

    public void run()
    {
        if (action == SAVE) {
            try {
                PrintStream out = new PrintStream(new GZIPOutputStream(
                        new FileOutputStream(file)), false, Constantes.ASI_ENCODING);

                out.println("<?xml version=\"1.0\" encoding=\"" + Constantes.ASI_ENCODING + "\"?>");
                out.println("<analyse>");

                out.println("<about>");

                out.println("<release release=\""
                        + Constantes.RELEASE + "\" />");
                out.println("<company>");
                out.println ("<name>"+ Constantes.COMPANY + "</name>" ) ;
                out.println ("<email>"+ Constantes.CONTACT_EMAIL + "</email>" ) ;
                out.println("</company>");


                out.println("</about>");

                AnalyseModule mod;
                FilterModule fm;
                
                Iterator<Entry<String, AnalyseModule>> e = Main.modules.entrySet().iterator();
                while ( e.hasNext() ) {
                    mod = e.next().getValue();

                    out.println("<module id=\"" + mod.getID().toLowerCase()
                            + "\">");

                    fm = mod.getFiltre(ID);
                    if (fm != null && fm.canSave())
                        ((SaveModule) fm).save(out);

                    out.println("</module>");

                }
                
                out.println("</analyse>");

                out.flush();
                out.close();
            } catch (IOException e) {
                GUIUtilities.error("Impossible de sauvegarder le fichier "
                        + file.getName());
            }
        } else //if (action == OPEN)

        {
            try {
            	InputStream inStream = new GZIPInputStream(new FileInputStream(file));
                BufferedReader in = new BufferedReader(new InputStreamReader(inStream, Constantes.ASI_ENCODING));

                ASIHandler handler = new ASIHandler();
                XmlParser parser = new XmlParser();
                parser.setHandler(handler);
                try {
                    parser.parse(null, null, in);
                } catch (XmlException e) {
                    System.err.println(e);
                } catch (Exception e) {
                    // Should NEVER happend !
                    e.printStackTrace();
                }

                in.close();
            } catch (IOException e) {
                GUIUtilities.error("Impossible d'ouvrir le fichier \""
                        + file.getName() + "\"");
            }
        }
    }

    /**
     * Sauvegarde le fichier dans un fichier XML.
     */
    public void save(File file)
    {
        this.file = file;
        this.action = SAVE;

        //Thread t = new Thread(this);
        //t.start();
        run();
    }

    /**
     * Charge le fichier depuis un fichier XML.
     */
    public void open(File file)
    {
        this.file = file;
        this.action = OPEN;

        //Thread t = new Thread(this);
        //t.start();
        run();
    }

    /**
     * Retourne une brève description pour le type de fichier
     */
    public String getDescription()
    {
        return "ASI File";
    }
}