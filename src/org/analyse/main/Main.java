/*
 * 05/15/2003 - 11:55:04
 * 
 * Main.java Copyright (C) 2003 Dreux Loic dreuxl@free.fr
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

package org.analyse.main;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.UIManager;
import org.analyse.core.gui.ParametrageWindow;
import org.analyse.core.gui.AboutWindow;
import org.analyse.core.gui.AnalyseFrame;
import org.analyse.core.gui.SplashScreen;
import org.analyse.core.gui.action.GlobalActionCollection;
import org.analyse.core.gui.statusbar.AnalyseStatusbar;
import org.analyse.core.modules.AnalyseModule;
import org.analyse.merise.main.MeriseModule;

import com.jgoodies.clearlook.ClearLookManager;
import com.jgoodies.plaf.Options;
import com.jgoodies.plaf.plastic.PlasticLookAndFeel;

public final class Main
{
	/**
	 * Don't let anyone instantiate this class.
	 */
	private Main() {}


    /** Répertoire personnel */
    public static final String SETTINGS_DIRECTORY = System
            .getProperty("user.home")
            + File.separator + ".analyseSI" + File.separator;

    /** Fichier des propriétés */
    public static final String USER_PROPS = SETTINGS_DIRECTORY + "analyseSI.properties";

    /** Numero du port du serveur Analyse. * */
    public static AnalyseFrame analyseFrame;

    /** Contient la barre de status */
    public static AnalyseStatusbar statusbar;

    /** Fenetre de présentation */
    public static SplashScreen splash;

    /** Fenetre A Propos */
    public static AboutWindow aboutWindow;
    public static ParametrageWindow parametrageWindow;
    
    /** Contient la liste des modules */
    public static Map<String, AnalyseModule> modules = new HashMap<String, AnalyseModule>();

    /** Toutes les Actions globales */
    public static GlobalActionCollection globalActionCollection;

    /**
     * Méthode principale
     */
    public static void main(String[] args)
    {
        /** ****************** */
        try {
            PlasticSettings settings = PlasticSettings.createDefault();

            Options.setDefaultIconSize(new java.awt.Dimension(16, 16));
            
            UIManager.put(Options.USE_SYSTEM_FONTS_APP_KEY, settings.isUseSystemFonts());
            Options.setGlobalFontSizeHints(settings.getFontSizeHints());
            Options.setUseNarrowButtons(settings.isUseNarrowButtons());
            
            Options.setTabIconsEnabled(settings.isTabIconsEnabled());
            ClearLookManager.setMode(settings.getClearLookMode());
            ClearLookManager.setPolicy(settings.getClearLookPolicyName());
            UIManager.put(Options.POPUP_DROP_SHADOW_ENABLED_KEY, settings.isPopupDropShadowEnabled());
            
            PlasticLookAndFeel.setMyCurrentTheme(settings.getSelectedTheme());
            PlasticLookAndFeel.setTabStyle(settings.getPlasticTabStyle());
            PlasticLookAndFeel.setHighContrastFocusColorsEnabled(settings.isPlasticHighContrastFocusEnabled());
            
            UIManager.setLookAndFeel(settings.getSelectedLookAndFeel());
            
        } catch (Exception e) {
        }
        /** ****************** */

        splash = new SplashScreen();

        splash.setProgress(0);

        globalActionCollection = new GlobalActionCollection();

        statusbar = new AnalyseStatusbar();
        splash.setProgress(10);

        AnalyseModule mod;
        mod = new MeriseModule();
        modules.put(mod.getID(), mod);
        splash.setProgress(20);

        analyseFrame = new AnalyseFrame();

        for (Iterator<Entry<String, AnalyseModule>> e = modules.entrySet().iterator(); e.hasNext();) {
            e.next().getValue().initGUI(analyseFrame);
        }
        
      //  analyseFrame.initGUI();

        splash.setProgress(90);
        aboutWindow = new AboutWindow(analyseFrame);
        parametrageWindow = new ParametrageWindow(analyseFrame);
        splash.setProgress(100);
        analyseFrame.setVisible(true);
        splash.setVisible(false);

        if (args.length > 0)
            analyseFrame.getAnalyseSave().open(args[0]);
    }

    public static AnalyseModule getModule(String id)
    {
        return modules.get(id);
    }
}