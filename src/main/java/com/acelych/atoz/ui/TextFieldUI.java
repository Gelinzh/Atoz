package com.acelych.atoz.ui;

import javax.swing.plaf.basic.BasicTextFieldUI;
import java.awt.*;

public class TextFieldUI extends BasicTextFieldUI
{
    @Override
    protected void paintBackground(Graphics g)
    {
        super.paintBackground(g);
        Rectangle r = new Rectangle(1, getComponent().getHeight() - 5, getComponent().getWidth() - 2, 2);
        g.setColor(new Color(220, 220, 220));
        g.fillRect(r.x, r.y, r.width, r.height);
    }

    @Override
    protected void installDefaults()
    {
        super.installDefaults();
        getComponent().setBorder(null);
    }
}
