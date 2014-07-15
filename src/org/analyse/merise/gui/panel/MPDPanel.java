/*
 * 10/17/2003 - 13:44:37
 * 
 * MPDPanel.java - Copyright (C) 2003 Dreux Loic dreuxl@free.fr
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

package org.analyse.merise.gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import org.analyse.core.gui.action.BasicAction;
import org.analyse.core.modules.AnalysePanel;
import org.analyse.core.util.GUIUtilities;
import org.analyse.core.util.Utilities;
import org.analyse.core.util.Constantes;
import org.analyse.core.util.save.AnalyseFilter;
import org.analyse.core.util.save.FileChooserFilter;
import org.analyse.main.Main;
import org.analyse.merise.mcd.composant.MPDComponent;

import com.sun.imageio.plugins.png.PNGImageWriter;

public class MPDPanel extends AnalysePanel
{
    private MPDComponent mpdComponent;

    private JFileChooser chooser;

    private ActionHandler actionHandler;

    private BasicAction saveGraphic;

    private JPopupMenu popupSaveGraphic;

    private JPanel toolbar;

    public MPDPanel(MPDComponent mpdComponent)
    {
        super("MPD");
        
        this.mpdComponent = mpdComponent;
        this.actionHandler = new ActionHandler();

        initAction();
        initToolbar();
        initPopup();

        mpdComponent.addMouseListener(new MouseHandler());

        chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileHidingEnabled(true);

        AnalyseFilter af;
        FileChooserFilter cf;
        chooser.resetChoosableFileFilters();
        cf = new FileChooserFilter(Constantes.PNG);
        cf.setExtension(Constantes.PNG_MINUSCULE);
        cf.setDescription(Constantes.STR_IMAGE_PNG);
        chooser.addChoosableFileFilter(cf);
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);

        this.setBorder(BorderFactory.createEmptyBorder(3, 3, 0, 3));

        this.setLayout(new BorderLayout());
        this.add(BorderLayout.CENTER, new JScrollPane(mpdComponent));
        this.add(BorderLayout.NORTH, toolbar);
    }

    private void initToolbar()
    {
        toolbar = new JPanel();
        toolbar.setLayout(new FlowLayout(FlowLayout.LEFT));

        toolbar.add(new JButton(saveGraphic) {
            {
                addMouseListener(Main.statusbar.getHandler());
            }
        });
    }

    private void initPopup()
    {
        popupSaveGraphic = new JPopupMenu();
        popupSaveGraphic.add(new JMenuItem(saveGraphic) {
            {
                addMouseListener(Main.statusbar.getHandler());
            }
        });
    }

    private void initAction()
    {
        saveGraphic = new BasicAction(null,
                Utilities.getLangueMessage(Constantes.MESSAGE_SAUVEGARDER_FICHIER_PNG), "SAVE_GRAPH",
                GUIUtilities.getImageIcon(Constantes.FILE_PNG_SAVE), 0, null);
        saveGraphic.addActionListener(actionHandler);
    }

    public boolean undoEnabled()
    {
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

    private String chooseFile()
    {
        if (chooser.showDialog(org.analyse.main.Main.analyseFrame, null) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath();
        }

        return null;
    }

    private class ActionHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            String action = e.getActionCommand();
            if (action.equals("SAVE_GRAPH")) {
                String fileName = chooseFile();
                if (fileName == null)
                    return;

                if (!Utilities.getExtension(fileName).equals("png")
                        && !Utilities.getExtension(fileName).equals("PNG"))
                    fileName = fileName + ".png";

                try {
                    File imageFile;
                    FileImageOutputStream outputStream;
                    BufferedImage img;
                    Graphics g;
                    Graphics2D g2d;
                    Rectangle2D r;

                    imageFile = new File(fileName);
                    img = new BufferedImage(
                            (int) (mpdComponent.getPreferredSize().getWidth()),
                            (int) (mpdComponent.getPreferredSize().getHeight()),
                            BufferedImage.TYPE_INT_RGB);

                    g = img.getGraphics();
                    g2d = (Graphics2D) g;

                    g2d.setColor(new Color(255, 255, 255));
                    r = new Rectangle2D.Double(0, 0, img.getWidth(), img
                            .getHeight());
                    g2d.fill(r);

                    mpdComponent.paintComponent(g2d);

                    PNGImageWriter writer = new PNGImageWriter(null);

                    writer.setOutput(outputStream = new FileImageOutputStream(
                            imageFile));
                    writer.write(img);

                    outputStream.close();

                    writer.dispose();

                } catch (IOException err) {
                    GUIUtilities.error("Impossible de sauvegarder le fichier "
                            + fileName);
                }
            }
        }
    }

    private class MouseHandler extends MouseAdapter
    {
        public void mouseReleased(MouseEvent e)
        {
            //if (e.isPopupTrigger()) Ne marche pas avec le JDK d'IBM
            if (e.getButton() == MouseEvent.BUTTON3) {
                popupSaveGraphic.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
}