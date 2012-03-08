/*
 * File        : SplashScreen2.java
 * Date      : 1 avr. 2004
 * Author   : loyl
 * 
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details ( see the LICENSE file ).
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.analyse.core.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

import org.analyse.core.util.Constantes;
import org.analyse.core.util.GUIUtilities;

/**
 * @author loyl
 */
public class SplashScreen extends JWindow
{

    private JProgressBar bar;

    public SplashScreen()
    {
        super();

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEtchedBorder());

        JPanel pBar = new JPanel(new FlowLayout()) {
            public void paintComponent(Graphics g)
            {
                super.paintComponent(g);

                g.setColor(Color.BLACK);
                g.drawLine(0, 0, this.getWidth(), 0);
            }
        };
        pBar.setBackground(Color.WHITE);
        bar = new JProgressBar(0, 100);
        bar.setForeground(new Color(150, 180, 200));
        bar.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        bar.setBackground(Color.WHITE);
        bar.setPreferredSize(new Dimension(100, 35));
        pBar.add(bar);

        p.add(new JLabel(GUIUtilities.getImageIcon(Constantes.FILE_PNG_LOGO)),
                BorderLayout.CENTER);
        p.add(pBar, BorderLayout.SOUTH);

        this.getContentPane().add(p);
        this.pack();
        GUIUtilities.centerComponent(this);
        this.setVisible(true);
    }

    public void setProgress(int progress)
    {
        bar.setValue(progress);
    }
}