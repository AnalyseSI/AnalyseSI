/*
 * 05/16/2003 - 10:29:03
 * 
 * GUIUtilities.java - Copyright (C) 2003 Dreux Loic dreuxl@free.fr
 * Modifications : 
 * janvier 2009 : B. DABO 
 * Message d'erreur en français si exception lors de la création de la font 
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

package org.analyse.core.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.analyse.core.gui.HtmlWindow;

public class GUIUtilities
{
	// Hashtable contenant les images déjà chargées
	// Permet de ne pas recharger les images à chaque
	// fois dans un soucis de performance.
    private static Map<String, ImageIcon> hashtable = new HashMap<String, ImageIcon>();
	// Hashtable contenant les fonts déjà chargées
	private static Map<String, Font> fonts = new HashMap<String, Font>();

    /**
     * Centre un composant au mileu de l'écran
     * 
     * @param compo
     *            composant à centrer
     */
    public static void centerComponent(Component compo)
    {
        compo.setLocation(new Point((getScreenDimension().width - compo
                .getSize().width) / 2, (getScreenDimension().height - compo
                .getSize().height) / 2));
    }

    /**
     * Détermine la taille de l'écran grace au toolkit
     * 
     * @return résolution de l'écran
     */
    public static Dimension getScreenDimension()
    {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    public static void error(String text)
    {
        JOptionPane.showMessageDialog(org.analyse.main.Main.analyseFrame, text,
        		Utilities.getLangueMessage ("analysesi"), JOptionPane.ERROR_MESSAGE);
    }

    public static void message(String text)
    {
        JOptionPane.showMessageDialog(org.analyse.main.Main.analyseFrame, text,
        		Utilities.getLangueMessage ("analysesi"), JOptionPane.INFORMATION_MESSAGE);
    }
    
	public static int  question_YES_NO_CANCEL (String question) {
		return JOptionPane.showConfirmDialog(org.analyse.main.Main.analyseFrame, question, Utilities.getLangueMessage ("analysesi"), JOptionPane.YES_NO_CANCEL_OPTION)  ;
	}
	
	public static int  question_YES_NO (String question) {
		return JOptionPane.showConfirmDialog(org.analyse.main.Main.analyseFrame, question, Utilities.getLangueMessage ("analysesi"), JOptionPane.YES_NO_OPTION)  ;
	}
	
    public static void messageHTML(String text)
    {
    	HtmlWindow f = new HtmlWindow(text, 500, 400,
                true, false);
    }
    
    public static void messageHTML(String text, boolean closeAfter2sec)
    {
    	HtmlWindow f = new HtmlWindow(text, 500, 400,
                true, closeAfter2sec);
    }
    
    public static void messageHTML(String text, boolean closeAfter2sec, int width, int height)
    {
    	HtmlWindow f = new HtmlWindow(text, width, height,
                true, closeAfter2sec);
    }   

    /**
     * Charge une image
     */
    public static ImageIcon getImageIcon(String name)
    {
    	Toolkit toolkit = Toolkit.getDefaultToolkit();
    	
        URL url;

        /*if (hashtable.containsKey(name) == true)
            return (ImageIcon) hashtable.get(name);

        ImageIcon icon = new ImageIcon(toolkit.getImage(AnalyseFrame.class.getResource("images/" + name)));
        
        hashtable.put(name, icon);
        
        return icon;
        */

        url = ClassLoader.getSystemResource("org/analyse/core/gui/images/" + name);        
        if (url == null) {
            url = ClassLoader.getSystemResource("org/analyse/core/gui/images/home.png");
        }

        ImageIcon icon = new ImageIcon(url);

        hashtable.put(name, icon);

        return icon;
    }
	/**
	 * Ouvre une police de caractères à partir d'un fichier TrueType.
	 * Ces polices sont stockées dans le package org.fia.fonts.<br>
	 * <i>Ne pas utiliser</i>
	 * @param name nom de la police de caractères (ex : americana.ttf).
	 * @return la police demandée.
	 * @see getFont(String name)
	 */
	public static Font openFont(String name)
	{
		if(fonts.containsKey(name))
			return (Font) fonts.get(name);
		
		Font font = null;
		InputStream is = ClassLoader.getSystemResourceAsStream("org/analyse/core/gui/fonts/" + name);
		
		if (is == null)
		{
			System.err.println("Utilisation de la Fonte impossible : " + name + " ...");
			System.exit(1);
		}
		
		try {
			 font = Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (Exception e) {
			System.err.println("Problème lors de la création de la fonte : " + name + " ..." );
			
			System.exit(1);
		}
		return font;
	}
	
	/**
	 * Retourne une police de caractères. Par défaut, la taille est de 12.
	 * @param name nom de la police de caractères.
	 * @return la police demandée.
	 * @see openFont(String name)
	 */
	public static Font getFont(String name)
	{
		return getFont(name, Font.PLAIN, 12);
	}
	
	/**
	 * Retourne une police de caractères avec une taille demandée.
	 * @param name nom de la police de caractères.
	 * @param size taille de la police de caractères.
	 * @return la police demandée.
	 * @see openFont(String name)
	 */
	public static Font getFont(String name, float size)
	{
		return getFont(name, Font.PLAIN, size);
	}
	
	/**
	 * Retourne une police de caractères avec une taille et un style demandés.
	 * @param name nom de la police de caractères.
	 * @param style taille de la police de caractères.
	 * @param size style de la police de caractères.
	 * @return la police demandée.
	 * @see openFont(String name)
	 */
	public static Font getFont(String name, int style, float size)
	{
		return openFont(name).deriveFont(style, size);
	}
}
