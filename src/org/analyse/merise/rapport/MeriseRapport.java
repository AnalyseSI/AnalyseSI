/*
 * 14 fevr. 2005 - 10:14:27
 * 
 * MeriseRapport.java Copyright (C) 2004 Dreux Loic dreuxl@free.fr
 * 
 * Modifications 
 *   Date : 2009 janvier 22
 *   @auteur : Bruno Dabo <bruno.dabo@lywoonsoftware.com>
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
package org.analyse.merise.rapport;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.List;
import java.util.StringTokenizer;

import javax.imageio.stream.FileImageOutputStream;

import org.analyse.core.util.Utilities;
import org.analyse.merise.gui.table.DictionnaireTable;
import org.analyse.merise.main.MeriseModule;
import org.analyse.merise.mcd.composant.MCDComponent;
import org.analyse.merise.mcd.composant.MPDComponent;
import org.analyse.merise.sql.SQLCommand;
import org.analyse.core.util.Constantes ;

import com.sun.imageio.plugins.png.PNGImageWriter;

public class MeriseRapport
{
    private MeriseModule meriseModule;
    private File tempFile;
    private String tempDir;

    private DictionnaireTable dico;
    private MCDComponent mcd;
    private MPDComponent mpd;
    private SQLCommand sql;

    public MeriseRapport(MeriseModule meriseModule)
    {
        this.meriseModule = meriseModule;

        dico = meriseModule.getDictionnaireTable();
        mcd = meriseModule.getMCDComponent();
        mpd = meriseModule.getMPDComponent();
        sql = meriseModule.getSQLCommand();
    }

    public URL createRapport()
    {
        try {
            tempFile = File.createTempFile("rapport", ".html");
            tempDir = Utilities.addressFichier(tempFile.getPath(), tempFile
                    .getName());

            PrintStream out = new PrintStream(new FileOutputStream(tempFile));

            out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
            out.println("<html>");
            out.println("<head>");
            out
                    .println("<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />");
            out.println("<title>Rapport AnalyseSI</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Rapport AnalyseSI</h1>");

            out.println("<h2>Merise</h2>");

            saveDictionnaire(out);
            saveMCD(out);
            
			out.println("</body>");
			out.println("</html>");
			
			out.flush();
			out.close();

			return tempFile.toURL();
			
        } catch (IOException e) {
            System.err.println(e);
            
        }
        
        return null;
    }

    private void saveDictionnaire(PrintStream out) throws IOException
    {
        out.println("<h3>Dictionnaire des informations</h3>");

        out.println("<table class=\"cadre\">");
        
        out.println("<thead>");
        out.println("<tr>\n<td><B>Nom</B></td>");
        out.println("<td><B>Code</B></td>");
        out.println("<td><B>Type</B></td>");
        out.println("<td><B>Taille</B></td>\n</tr>");
        out.println("</thead>");
        
        if(dico.getRowCount() > 0) {
        	out.println("</tbody>");
        	for (int i = 0; i < dico.getRowCount() - 1; i++) {
        		out.println("<tr>");

        		out.print("<td>");
        		out.print(dico.getValue(dico.getID(i), DictionnaireTable.NAME));
        		out.print("</td>");

        		out.print("<td>");
        		out.print(dico.getID(i));
        		out.println("</td>");

        		out.print("<td>");
        		out.print(dico.getValue(dico.getID(i), DictionnaireTable.TYPE));
        		out.print("</td>");

        		out.print("<td>");
        		out.print(dico.getValue(dico.getID(i), DictionnaireTable.SIZE));
        		out.print("</td>");

        		out.println("</tr>");
        	}
        	out.println("</tbody>");
        }

        out.println("</table>");
    }

    private void saveMCD(PrintStream out) throws IOException
    {
        /* D�claration */
        File imageFile;
        FileImageOutputStream outputStream;
        BufferedImage img;
        Graphics g;
        Graphics2D g2d;
        Rectangle2D r;

        /* MCD */
        mcd.enleverFocus();
        mcd.repaint();
        mcd.setSize(mcd.getPreferredSize());
        
        imageFile = new File(tempDir + "MCD.png");
        img = new BufferedImage(
                (int) (mcd.getPreferredSize().getWidth()),
                (int) (mcd.getPreferredSize().getHeight()),
                BufferedImage.TYPE_INT_RGB);

        g = img.getGraphics();
        g2d = (Graphics2D) g;

        g2d.setColor(new Color(255, 255, 255));
        r = new Rectangle2D.Double(0, 0, img.getWidth(), img
                .getHeight());
        g2d.fill(r);

        mcd.paintComponent(g2d);

        PNGImageWriter writer = new PNGImageWriter(null);

        writer.setOutput(outputStream = new FileImageOutputStream(
                imageFile));
        writer.write(img);
        outputStream.close();
        writer.dispose();

        out.println("<br/><br/>");
		out.println("<h3>Modèle Conceptuel de données</h3>");
		out.println("<img border=\"0\" src=\"MCD.png\"");
        
        /* MPD */
		mcd.buildMPD(mpd, Constantes.HIDE_ALL);
		
        imageFile = new File(tempDir + "MPD.png");
        img = new BufferedImage(
                (int) (mpd.getPreferredSize().getWidth()),
                (int) (mpd.getPreferredSize().getHeight()),
                BufferedImage.TYPE_INT_RGB);

        g = img.getGraphics();
        g2d = (Graphics2D) g;

        g2d.setColor(new Color(255, 255, 255));
        r = new Rectangle2D.Double(0, 0, img.getWidth(), img
                .getHeight());
        g2d.fill(r);

        mpd.paintComponent(g2d);

        writer = new PNGImageWriter(null);
        writer.setOutput(outputStream = new FileImageOutputStream(imageFile));
        writer.write(img);
        outputStream.close();
        writer.dispose();

        out.println("<br/><br/>");
		out.println("<h3>Modèle physique de données</h3>");
		out.println("<img border=\"0\" src=\"Mpd.png\"");
    }
    
    private void saveSQL(PrintStream out) throws IOException
    {
        out.println("<br/><br/>");
        out.println("<h3>Script SQL</h3>");
        
        String name, str, text, textFinal, requete;

        List<String> keywords = sql.getKeywords();
        List<String> types = sql.getTypes();

        text = sql.getRequests();

        for (StringTokenizer st = new StringTokenizer(text, " (),<>;", true); st
                .hasMoreElements();) {
            str = st.nextToken();

            if (keywords.contains(str))
                out.println("<b style=\"color: blue;\">" + str + "</b>");
            else if (types.contains(str))
                out.println("<b style=\"color: red;\">" + str + "</b>");
            else if (str.equals("(") || str.equals(")"))
                out.println("<b>" + str + "</b>");
            else if (str.equals(";"))
                out.println(";<br/><br/>");
            else
                out.println(str);
        }
    }

    public void saveInNewDirectory(String directory)
    {
    	// TODO ?
    }
}
