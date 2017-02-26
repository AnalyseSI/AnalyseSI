/*
 * 05/16/2003 - 10:59:02
 * 
 * Utilities.java - Copyright (C) 2003 Dreux Loic dreuxl@free.fr
 * Modifications : 
 *  Date : 2009 jan 22 / bruno.dabo@lywoonsoftware.com
 *  Date : 2009 avril 17 / bruno.dabo@lywoonsoftware.com  => multi-langage
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public final class Utilities
{
	/**
	 * Don't let anyone instantiate this class.
	 */
	private Utilities() {}


    static Locale currentLocale ;
	
	public static String getRelease () {
		return  Constantes.NOM_APPLICATION + " " + Constantes.RELEASE ;
	}

    /**
     * Charge un fichier texte interne au programme.
     * @param filename
     *            str du fichier texte
     */
    public static String getText(String filename, Class source)
    {
        int nb;
        char[] buffer = new char[1024];

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    source.getResource(filename).openStream()));
            StringBuffer res = new StringBuffer();

            while ((nb = reader.read(buffer, 0, 1024)) >= 0) {
                res.append(buffer, 0, nb);
            }

            return res.toString();
        } catch (IOException e) {
            System.err.println(e);
        } catch (NullPointerException e) {
            return null;
        }

        return null;
    }

    /**
     * Récupère l'extension d'un fichier.
     */
    public static String getExtension(String filename)
    {
        int i = filename.lastIndexOf('.');
        int j = filename.lastIndexOf(File.separatorChar);

        return (i > 0 && i > j && i < filename.length() - 1) ? filename
                .substring(i + 1).toLowerCase() : "";
    }

    public static String replaceExtension(String filename, String newExtention)
    {
        int i = filename.lastIndexOf('.');

        return (i > 0 && i < filename.length() - 1) ? filename.substring(0,
                i + 1)
                + newExtention : filename;
    }

    public static String addressFichier(String addresse, String str)
    {
        StringBuffer res = new StringBuffer();
        for (int i = 0; i < addresse.length() - str.length(); i++) {
            res.append(addresse.charAt(i));
        }
        return res.toString();
    }

    public static String normaliseString(String str, int upperLowerCase)
    {
    	// ## evolution UTF-8 ( multi-langue ) 
    	
        /*str = UnicodeUtils.decomposeToBasicLatin(str);
        String strRes = "";
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z'
                    || c >= '0' && c <= '9')
                strRes += c;
            else
                strRes += '_';
        }
        if (upperLowerCase == Constantes.LOWER)
            return strRes.toLowerCase();
        else if (upperLowerCase == Constantes.UPPER)
            return strRes.toUpperCase();
        return strRes;
        */

        return str.replaceAll(" ", "_") ; // Bug #622229
    }

    public static ResourceBundle getResourceBundle ( Locale currentLocale ) {
    	return  ResourceBundle.getBundle("langue/messages", currentLocale);
    }
    
    public static ResourceBundle getResourceBundle () { 
    	Utilities.currentLocale = new Locale ( System.getProperty("user.language") ) ;  
    	ResourceBundle resourceBundle ;
    	try {
    		resourceBundle = ResourceBundle.getBundle("langue/messages", Utilities.currentLocale );
    	} catch ( Exception e ) {
    		Utilities.currentLocale = new Locale ( "fr") ;
    		resourceBundle = ResourceBundle.getBundle("langue/messages", new Locale ( "fr" ) ) ;    		
    	}
    	
        return   resourceBundle ;
    }
    
    public static String getLangueMessage ( String key ) {
    	ResourceBundle resourceBundle = Utilities.getResourceBundle () ;
    	String str = key ;
    	
    	try {
    		str = resourceBundle.getString ( key ) ; 
    	} catch ( Exception e ){    		
    		str = "? - " + key ;
    		System.err.println(e) ;
    	}
    	
    	return str ; 
    }

    public static String getLangueMessageFormatter ( String key,  Object[] messageArguments ) {    	
    	String str = Utilities.getLangueMessage( key ) ; 
    	
        MessageFormat formatter = new MessageFormat("");      
        formatter.setLocale(Utilities.currentLocale);

        formatter.applyPattern(str);
        return formatter.format(messageArguments);
    }
    
    public static final String newLine()
    {
        return "\r\n";
    }
}