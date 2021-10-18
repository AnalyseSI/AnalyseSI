/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.analyse.core.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

/**
 *
 * ROUX Constant, MICHEL Arthur
 */
public class windowfocus implements WindowFocusListener, MouseListener{
    private AnalyseFrame af;
    private boolean activated;
    
    public windowfocus(AnalyseFrame af){
        this.af = af;
        this.af.addWindowFocusListener(this);
        this.af.addMouseListener(this);
    }
    @Override
    public void windowGainedFocus(WindowEvent e) {
    }

    @Override
    public void windowLostFocus(WindowEvent e) {
        this.setActivated(false);
    }
    
    public void setActivated(boolean b){
        this.activated = b;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        this.setActivated(true);
        this.af.requestFocus();
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
