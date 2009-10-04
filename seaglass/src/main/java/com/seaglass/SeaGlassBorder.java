/*
 * @(#)SeaGlassBorder.java	1.15 06/11/30
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.seaglass;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.synth.Region;
import javax.swing.text.JTextComponent;

import sun.swing.plaf.synth.SynthUI;

/**
 * SeaGlassBorder is a border that delegates to a Painter. The Insets are
 * determined at construction time.
 * 
 * @version 1.15, 11/30/06
 * @author Scott Violet
 */
class SeaGlassBorder extends AbstractBorder implements UIResource {
    private SynthUI ui;
    private Insets  insets;

    SeaGlassBorder(SynthUI ui, Insets insets) {
        this.ui = ui;
        this.insets = insets;
    }

    SeaGlassBorder(SynthUI ui) {
        this(ui, null);
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        JComponent jc = (JComponent) c;
        SeaGlassContext context = (SeaGlassContext) ui.getContext(jc);
        SeaGlassStyle style = (SeaGlassStyle) context.getStyle();
        if (style == null) {
            assert false : "SeaGlassBorder is being used outside after the UI " + "has been uninstalled";
            return;
        }
        ui.paintBorder(context, g, x, y, width, height);
        context.dispose();
    }

    /**
     * This default implementation returns a new <code>Insets</code> instance
     * where the <code>top</code>, <code>left</code>, <code>bottom</code>, and
     * <code>right</code> fields are set to <code>0</code>.
     * 
     * @param c
     *            the component for which this border insets value applies
     * @return the new <code>Insets</code> object initialized to 0
     */
    public Insets getBorderInsets(Component c) {
        return getBorderInsets(c, null);
    }

    /**
     * Reinitializes the insets parameter with this Border's current Insets.
     * 
     * @param c
     *            the component for which this border insets value applies
     * @param insets
     *            the object to be reinitialized
     * @return the <code>insets</code> object
     */
    public Insets getBorderInsets(Component c, Insets insets) {
        if (this.insets != null) {
            if (insets == null) {
                insets = new Insets(this.insets.top, this.insets.left, this.insets.bottom, this.insets.right);
            } else {
                insets.top = this.insets.top;
                insets.bottom = this.insets.bottom;
                insets.left = this.insets.left;
                insets.right = this.insets.right;
            }
        } else if (insets == null) {
            insets = new Insets(0, 0, 0, 0);
        } else {
            insets.top = insets.bottom = insets.left = insets.right = 0;
        }
        if (c instanceof JComponent) {
            Insets margin = null;
            Region region = SeaGlassLookAndFeel.getRegion((JComponent) c);
            if ((region == Region.ARROW_BUTTON || region == Region.BUTTON || region == Region.CHECK_BOX
                    || region == Region.CHECK_BOX_MENU_ITEM || region == Region.MENU || region == Region.MENU_ITEM
                    || region == Region.RADIO_BUTTON || region == Region.RADIO_BUTTON_MENU_ITEM || region == Region.TOGGLE_BUTTON)
                    && (c instanceof AbstractButton)) {
                margin = ((AbstractButton) c).getMargin();
            } else if ((region == Region.EDITOR_PANE || region == Region.FORMATTED_TEXT_FIELD || region == Region.PASSWORD_FIELD
                    || region == Region.TEXT_AREA || region == Region.TEXT_FIELD || region == Region.TEXT_PANE)
                    && (c instanceof JTextComponent)) {
                margin = ((JTextComponent) c).getMargin();
            } else if (region == Region.TOOL_BAR && (c instanceof JToolBar)) {
                margin = ((JToolBar) c).getMargin();
            } else if (region == Region.MENU_BAR && (c instanceof JMenuBar)) {
                margin = ((JMenuBar) c).getMargin();
            }
            if (margin != null) {
                insets.top += margin.top;
                insets.bottom += margin.bottom;
                insets.left += margin.left;
                insets.right += margin.right;
            }
        }
        return insets;
    }

    /**
     * This default implementation returns false.
     * 
     * @return false
     */
    public boolean isBorderOpaque() {
        return false;
    }
}
