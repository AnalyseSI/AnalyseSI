/*
 * 05/22/2003 - 10:15:01
 * 
 * MeriseModule.java - Copyright (C) 2003 Dreux Loic dreuxl@free.fr
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

package org.analyse.merise.main;

import javax.swing.JButton;

import org.analyse.core.gui.AnalyseFrame;
import org.analyse.core.gui.action.BasicAction;
import org.analyse.core.gui.action.NavigationActionFactory;
import org.analyse.core.modules.AnalyseModule;
import org.analyse.core.modules.AnalysePanel;
import org.analyse.core.util.Constantes;
import org.analyse.core.util.GUIUtilities;
import org.analyse.core.util.Utilities;
import org.analyse.merise.gui.panel.DictionnairePanel;
import org.analyse.merise.gui.panel.MCDPanel;
import org.analyse.merise.gui.panel.MLDPanel;
import org.analyse.merise.gui.panel.MPDPanel;
import org.analyse.merise.gui.panel.RapportPanel;
import org.analyse.merise.gui.panel.SQLPanel;
import org.analyse.merise.gui.table.DictionnaireTable;
import org.analyse.merise.mcd.composant.MCDComponent;
import org.analyse.merise.mcd.composant.MLDCommand;
import org.analyse.merise.mcd.composant.MLDComponent;
import org.analyse.merise.mcd.composant.MPDComponent;
import org.analyse.merise.rapport.MeriseRapport;
import org.analyse.merise.save.FiltreMeriseASI;
import org.analyse.merise.sql.SQLCommand;

public class MeriseModule extends AnalyseModule
{
    private AnalysePanel dictionnairePanel;

    private MCDPanel mcdPanel;
    private MLDPanel mldPanel;
    private MPDPanel mpdPanel;

    private SQLPanel sqlPanel;
    
    private RapportPanel rapportPanel;

    private DictionnaireTable dictionnaireTable;

    private MCDComponent mcdComponent;
    
    private MPDComponent mpdComponent;
    private MLDComponent mldComponent;
    private SQLCommand sqlCommand ;
    private MLDCommand mldCommand ;  
    
    private MeriseRapport meriseRapport;
    
    private BasicAction tableAction, mcdAction, mpdAction, mldAction, sqlAction, rapportAction;

    public MeriseModule()
    {
        super();
    }

    public void initGUI(AnalyseFrame analyseFrame)
    {
        sqlCommand = new SQLCommand();
        mldCommand = new MLDCommand();
        
        dictionnaireTable = new DictionnaireTable(sqlCommand.getTypes());
        dictionnairePanel = new DictionnairePanel(dictionnaireTable);

        mcdComponent = new MCDComponent(dictionnaireTable);
        mpdComponent = new MPDComponent();     
        mldComponent = new MLDComponent();
        
        meriseRapport = new MeriseRapport(this);
        
        mcdPanel = new MCDPanel(mcdComponent, mpdComponent, sqlCommand, mldComponent, mldCommand);        
        mpdPanel = new MPDPanel(mpdComponent);
     
        sqlPanel = new SQLPanel(sqlCommand);
        mldPanel = new MLDPanel(mldCommand);
        
        rapportPanel = new RapportPanel(meriseRapport);

        filtres.add(new FiltreMeriseASI(this));

        NavigationActionFactory factory = analyseFrame.getNavigationActionFactory();
         
        tableAction = factory.buildNavigationAction(GUIUtilities.getImageIcon(Constantes.FILE_PNG_TABLE), "Dictionnaire", Utilities.getLangueMessage(Constantes.MESSAGE_DICTIONNAIRE_DONNEES) , dictionnairePanel);
        mcdAction = factory.buildNavigationAction(GUIUtilities.getImageIcon(Constantes.FILE_PNG_MCD), "MCD", Utilities.getLangueMessage(Constantes.MESSAGE_TITRE_MCD), mcdPanel);
        mpdAction = factory.buildNavigationAction(GUIUtilities.getImageIcon(Constantes.FILE_PNG_MPD), "MPD", Utilities.getLangueMessage(Constantes.MESSAGE_TITRE_MPD), mpdPanel);
        sqlAction = factory.buildNavigationAction(GUIUtilities.getImageIcon(Constantes.FILE_PNG_SQL), "SQL", Utilities.getLangueMessage(Constantes.MESSAGE_TITRE_SQL), sqlPanel);
        mldAction = factory.buildNavigationAction(GUIUtilities.getImageIcon(Constantes.FILE_PNG_MLD), "MLDR", Utilities.getLangueMessage(Constantes.MESSAGE_TITRE_MLD), mldPanel);
        rapportAction = factory.buildNavigationAction(GUIUtilities.getImageIcon(Constantes.FILE_PNG_RAPPORT), "Rapport", Utilities.getLangueMessage(Constantes.MESSAGE_TITRE_RAPPORT), rapportPanel);
     
        /*
        JMenu menu = new JMenu("Merise");
        menu.setMnemonic('m');

        menu.add(new JMenuItem(tableAction));
        menu.add(new JMenuItem(mcdAction));
        menu.add(new JMenuItem(mpdAction));
        menu.add(new JMenuItem(sqlAction));
//Pas encore prêt      menu.add(new JMenuItem(rapportAction));
//Pas encore prêt        menu.addSeparator();
        menu.add(new JMenuItem(mcdPanel.getVerif()));
//Pas encore prêt        menu.add(new JMenuItem(mcdPanel.getBuildLMD()));
        //menu.add(new JMenuItem(mcdPanel.getBuildLMLD()));
        
        
        analyseFrame.addMenu(menu);
        */
        
        analyseFrame.addButton(new JButton(mcdPanel.getVerif()));
//Pas encore prêt        analyseFrame.addButton(new JButton(mcdPanel.getBuildLMD()));
        
        analyseFrame.addButton(new JButton(mcdPanel.getBuildLMD()));
        
        analyseFrame.addNavigateButton(tableAction);
        analyseFrame.addNavigateButton(mcdAction);
        analyseFrame.addNavigateButton(mpdAction);
        analyseFrame.addNavigateButton(sqlAction);
        analyseFrame.addNavigateButton(mldAction);
//Pas encore prêt        analyseFrame.addNavigateButton(rapportAction);
    }

    public String getID()
    {
        return "MERISE";
    }

    public String getName()
    {
        return "Merise";
    }

    public String getAuthor()
    {
        return "Dreux Loic";
    }

    public AnalysePanel getDictionnairePanel()
    {
        return dictionnairePanel;
    }

    public AnalysePanel getMCDPanel()
    {
        return mcdPanel;
    }

    public AnalysePanel getMPDPanel()
    {
        return mpdPanel;
    }

    public AnalysePanel getSQLPanel()
    {
        return sqlPanel;
    }

    public DictionnaireTable getDictionnaireTable()
    {
        return dictionnaireTable;
    }

    public MCDComponent getMCDComponent()
    {
        return mcdComponent;
    }

    public MPDComponent getMPDComponent()
    {
        return mpdComponent;
    }
    public MLDComponent getMLDComponent()
    {
        return mldComponent;
    }
    
    public SQLCommand getSQLCommand()
    {
        return sqlCommand;
    }

    public void clear()
    {
        mcdComponent.clear();
        mpdComponent.clear();
        sqlCommand.clear();
        mldCommand.clear();
        dictionnaireTable.clear();
    }
}