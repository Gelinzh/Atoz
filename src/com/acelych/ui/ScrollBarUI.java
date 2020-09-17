package com.acelych.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class ScrollBarUI extends BasicScrollBarUI
{
    @Override
    protected void configureScrollBarColors()
    {
         thumbColor = new Color(220, 220, 220);
         trackColor = new Color(238, 238, 238);
         trackHighlightColor = new Color(225, 225, 225);
    }

    @Override
    public Dimension getPreferredSize(JComponent c)
    {
        c.setPreferredSize(new Dimension(15, 0));
        return super.getPreferredSize(c);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds)
    {
        if(thumbBounds.isEmpty() || !scrollbar.isEnabled())
            return;

        int w = thumbBounds.width;
        int h = thumbBounds.height;
        g.translate(thumbBounds.x, thumbBounds.y);

        g.setColor(thumbColor);
        g.fillRect(0, 0, w, h);
    }

    @Override
    protected JButton createIncreaseButton(int orientation)
    {
        return new EmptyButton();
    }

    @Override
    protected JButton createDecreaseButton(int orientation)
    {
        return new EmptyButton();
    }

    protected class EmptyButton extends JButton
    {
        EmptyButton()
        {
            super();
            this.setBorderPainted(false);
            this.setContentAreaFilled(false);
            this.setPreferredSize(new Dimension(0, 0));
        }
    }
}
