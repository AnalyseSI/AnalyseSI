/*
 * 05/23/2003 - 10:18:41
 * 
 * HelpPanel.java - Copyright (C) 2003 Dreux Loic dreuxl@free.fr
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

package org.analyse.core.gui.panel;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Deque;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;

import org.analyse.core.gui.action.BasicAction;
import org.analyse.core.gui.menu.ClipboardPopupMenu;
import org.analyse.core.modules.AnalysePanel;
import org.analyse.core.modules.ClipboardInterface;
import org.analyse.core.util.Constantes;
import org.analyse.core.util.GUIUtilities;
import org.analyse.core.util.Utilities;
import org.analyse.main.Main;

public class HelpPanel extends AnalysePanel implements HyperlinkListener,
        ClipboardInterface
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 6619857514551235043L;

	private BasicAction goback, home;

    private JEditorPane editor;

    private JPopupMenu popup;

    private ActionHandler actionHandler;

    private Deque<URL> history;

    public HelpPanel()
    {

        super(Constantes.HELP);
        
        try {
            editor = new JEditorPane(Main.class
                    .getResource("help/whatsnew.html"));
            editor.setEditable(false);
            editor.addHyperlinkListener(this);

            popup = new ClipboardPopupMenu(false, true, false);
            editor.addMouseListener(new MouseHandler());
            actionHandler = new ActionHandler();
            history = new LinkedList<URL>();
            initAction();
            //initToolbar();

            this.setLayout(new BorderLayout());
            this.setBorder(BorderFactory.createEmptyBorder(3, 3, 0, 3));

            this.add(BorderLayout.CENTER, new JScrollPane(editor) {
                /**
				 * 
				 */
				private static final long serialVersionUID = 6256786451390655839L;

				{
                    JViewport vp = this.getViewport();
                    vp.add(editor);
                }
            });
            //this.add(BorderLayout.NORTH, toolbar);
        } catch (MalformedURLException e) {
            this.add(new JLabel( Utilities.getLangueMessage( Constantes.MESSAGE_CHARGEMENT_AIDE_ECHEC ) )  );
        } catch (IOException e) {
            this.add ( new JLabel( Utilities.getLangueMessage( Constantes.MESSAGE_CHARGEMENT_AIDE_ECHEC ) ) ) 
            ;
        }
    }


    private void initAction() {
    	
        goback = new BasicAction(
        		Utilities.getLangueMessage( Constantes.MESSAGE_PRECEDENT ) , 
        		Utilities.getLangueMessage( Constantes.MESSAGE_RETOUR_PAGE_PRECEDENTE) ,
                Constantes.BACK, GUIUtilities.getImageIcon(Constantes.FILE_PNG_LEFT), 0, null);
        
        goback.addActionListener(actionHandler);
        
        home = new BasicAction(
        		Utilities.getLangueMessage( Constantes.MESSAGE_HOME ) ,
        		Utilities.getLangueMessage( Constantes.MESSAGE_RETOUR_PAGE_PRINCIPALE ) , 
        		Constantes.HOME,
                GUIUtilities.getImageIcon(Constantes.FILE_PNG_HOME), 0, null);
        
        home.addActionListener(actionHandler);
    }

    public boolean undoEnabled() {
        return false;
    }

    public boolean redoEnabled()
    {
        return false;
    }

    public boolean copyEnabled()
    {
        return false;
    }

    public boolean pasteEnabled()
    {
        return false;
    }

    public void cut()
    {
        //System.out.println("Cut");
    }

    public void copy()
    {
        editor.copy();
    }

    public void paste()
    {
        //System.out.println("Paste");
    }

    public void hyperlinkUpdate(HyperlinkEvent e)
    {
    	
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            history.addFirst(editor.getPage());
            Cursor c = editor.getCursor();
            Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
            editor.setCursor(waitCursor);
            SwingUtilities.invokeLater(new PageLoader(e.getURL(), c));
        }
    }

    private class MouseHandler extends MouseAdapter
    {
        public void mouseReleased(MouseEvent e)
        {
            if (e.isPopupTrigger())
                popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    private class PageLoader implements Runnable
    {
        private URL url;

        private Cursor cursor;

        public PageLoader(URL url, Cursor cursor)
        {
            this.url = url;
            this.cursor = cursor;
        }

        public void run()
        {
            if (url == null) {
                editor.setCursor(cursor);
                Container parent = editor.getParent();
                parent.repaint();
                return;
            }

            Document doc = editor.getDocument();
            try {
                editor.setPage(url);
            } catch (IOException e) {
                editor.setDocument(doc);
                getToolkit().beep();
            } finally {
                url = null;
                SwingUtilities.invokeLater(this);
            }
        }
    }

    private class ActionHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            String action = e.getActionCommand();

            try {
                if (action.equals(Constantes.BACK)) {
                    if (!history.isEmpty())
                        editor.setPage(history.removeFirst());
                }
                if (action.equals(Constantes.HOME))
                    editor.setPage(Main.class
                            .getResource("help/whatsnew.html"));
            } catch (IOException exp) {
            }
        }
    }
}
