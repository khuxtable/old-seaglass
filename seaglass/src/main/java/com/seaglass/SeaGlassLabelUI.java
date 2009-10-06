/*
 * Copyright (c) 2009 Kathryn Huxtable and Kenneth Orr.
 *
 * This file is part of the SeaGlass Pluggable Look and Feel.
 *
 * SeaGlass is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.

 * SeaGlass is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with SeaGlass.  If not, see
 *     <http://www.gnu.org/licenses/>.
 * 
 * $Id$
 */
package com.seaglass;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicLabelUI;
import javax.swing.plaf.synth.ColorType;
import javax.swing.plaf.synth.SynthConstants;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.text.View;

import sun.swing.plaf.synth.SynthUI;

/**
 * SeaGlassLabelUI.
 * 
 * Based on SynthLabelUI by Scott Violet.
 * 
 * @see javax.swing.plaf.synth.SynthLabelUI
 */
public class SeaGlassLabelUI extends BasicLabelUI implements SynthUI {
    private SeaGlassStyle style;

    /**
     * Returns the LabelUI implementation used for the skins look and feel.
     */
    public static ComponentUI createUI(JComponent c) {
        return new SeaGlassLabelUI();
    }

    protected void installDefaults(JLabel c) {
        updateStyle(c);
    }

    void updateStyle(JLabel c) {
        SeaGlassContext context = getContext(c, ENABLED);
        style = (SeaGlassStyle) SeaGlassLookAndFeel.updateStyle(context, this);
        context.dispose();
    }

    protected void uninstallDefaults(JLabel c) {
        SeaGlassContext context = getContext(c, ENABLED);

        style.uninstallDefaults(context);
        context.dispose();
        style = null;
    }

    public SeaGlassContext getContext(JComponent c) {
        return getContext(c, getComponentState(c));
    }

    private SeaGlassContext getContext(JComponent c, int state) {
        return SeaGlassContext.getContext(SeaGlassContext.class, c, SeaGlassLookAndFeel.getRegion(c), style, state);
    }

    private int getComponentState(JComponent c) {
        int state = SeaGlassLookAndFeel.getComponentState(c);
        if (SeaGlassLookAndFeel.selectedUI == this && state == SynthConstants.ENABLED) {
            state = SeaGlassLookAndFeel.selectedUIState | SynthConstants.ENABLED;
        }
        return state;
    }

    public int getBaseline(JComponent c, int width, int height) {
        if (c == null) {
            throw new NullPointerException("Component must be non-null");
        }
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Width and height must be >= 0");
        }
        JLabel label = (JLabel) c;
        String text = label.getText();
        if (text == null || "".equals(text)) {
            return -1;
        }
        Insets i = label.getInsets();
        Rectangle viewRect = new Rectangle();
        Rectangle textRect = new Rectangle();
        Rectangle iconRect = new Rectangle();
        viewRect.x = i.left;
        viewRect.y = i.top;
        viewRect.width = width - (i.right + viewRect.x);
        viewRect.height = height - (i.bottom + viewRect.y);

        // layout the text and icon
        SeaGlassContext context = getContext(label);
        FontMetrics fm = context.getComponent().getFontMetrics(context.getStyle().getFont(context));
        context.getStyle().getGraphicsUtils(context).layoutText(context, fm, label.getText(), label.getIcon(),
            label.getHorizontalAlignment(), label.getVerticalAlignment(), label.getHorizontalTextPosition(),
            label.getVerticalTextPosition(), viewRect, iconRect, textRect, label.getIconTextGap());
        View view = (View) label.getClientProperty(BasicHTML.propertyKey);
        int baseline;
        if (view != null) {
            baseline = BasicHTML.getHTMLBaseline(view, textRect.width, textRect.height);
            if (baseline >= 0) {
                baseline += textRect.y;
            }
        } else {
            baseline = textRect.y + fm.getAscent();
        }
        context.dispose();
        return baseline;
    }

    /**
     * Notifies this UI delegate that it's time to paint the specified
     * component. This method is invoked by <code>JComponent</code> when the
     * specified component is being painted.
     */
    public void update(Graphics g, JComponent c) {
        SeaGlassContext context = getContext(c);

        SeaGlassLookAndFeel.update(context, g);
        context.getPainter().paintLabelBackground(context, g, 0, 0, c.getWidth(), c.getHeight());
        paint(context, g);
        context.dispose();
    }

    public void paint(Graphics g, JComponent c) {
        SeaGlassContext context = getContext(c);

        paint(context, g);
        context.dispose();
    }

    protected void paint(SeaGlassContext context, Graphics g) {
        JLabel label = (JLabel) context.getComponent();
        Icon icon = (label.isEnabled()) ? label.getIcon() : label.getDisabledIcon();

        g.setColor(context.getStyle().getColor(context, ColorType.TEXT_FOREGROUND));
        g.setFont(style.getFont(context));
        context.getStyle().getGraphicsUtils(context).paintText(context, g, label.getText(), icon, label.getHorizontalAlignment(),
            label.getVerticalAlignment(), label.getHorizontalTextPosition(), label.getVerticalTextPosition(),
            label.getIconTextGap(), label.getDisplayedMnemonicIndex(), 0);
    }

    public void paintBorder(SynthContext context, Graphics g, int x, int y, int w, int h) {
        ((SeaGlassContext) context).getPainter().paintLabelBorder(context, g, x, y, w, h);
    }

    public Dimension getPreferredSize(JComponent c) {
        JLabel label = (JLabel) c;
        Icon icon = (label.isEnabled()) ? label.getIcon() : label.getDisabledIcon();
        SeaGlassContext context = getContext(c);
        Dimension size = context.getStyle().getGraphicsUtils(context).getPreferredSize(context,
            context.getStyle().getFont(context), label.getText(), icon, label.getHorizontalAlignment(),
            label.getVerticalAlignment(), label.getHorizontalTextPosition(), label.getVerticalTextPosition(),
            label.getIconTextGap(), label.getDisplayedMnemonicIndex());

        context.dispose();
        return size;
    }

    public Dimension getMinimumSize(JComponent c) {
        JLabel label = (JLabel) c;
        Icon icon = (label.isEnabled()) ? label.getIcon() : label.getDisabledIcon();
        SeaGlassContext context = getContext(c);
        Dimension size = context.getStyle().getGraphicsUtils(context).getMinimumSize(context, context.getStyle().getFont(context),
            label.getText(), icon, label.getHorizontalAlignment(), label.getVerticalAlignment(), label.getHorizontalTextPosition(),
            label.getVerticalTextPosition(), label.getIconTextGap(), label.getDisplayedMnemonicIndex());

        context.dispose();
        return size;
    }

    public Dimension getMaximumSize(JComponent c) {
        JLabel label = (JLabel) c;
        Icon icon = (label.isEnabled()) ? label.getIcon() : label.getDisabledIcon();
        SeaGlassContext context = getContext(c);
        Dimension size = context.getStyle().getGraphicsUtils(context).getMaximumSize(context, context.getStyle().getFont(context),
            label.getText(), icon, label.getHorizontalAlignment(), label.getVerticalAlignment(), label.getHorizontalTextPosition(),
            label.getVerticalTextPosition(), label.getIconTextGap(), label.getDisplayedMnemonicIndex());

        context.dispose();
        return size;
    }

    public void propertyChange(PropertyChangeEvent e) {
        super.propertyChange(e);
        if (SeaGlassLookAndFeel.shouldUpdateStyle(e)) {
            updateStyle((JLabel) e.getSource());
        }
    }
}
