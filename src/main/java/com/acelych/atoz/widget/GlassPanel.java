package com.acelych.atoz.widget;

import javax.swing.*;
import java.awt.*;

public class GlassPanel extends JLabel
{
    public int deviateX, deviateY;
    public boolean isSwitchActive, isDialogActive;
    public Image image01, image02;
    private int direct;

    public GlassPanel()
    {
        super();
        direct = 0;
        deviateX = 0;
        deviateY = 0;
        isSwitchActive = false;
        isDialogActive = false;
        image01 = null;
        image02 = null;
    }

    public void setDirect(int direct)
    {
        if (direct < 0)
            direct = 0;
        else if (direct > 3)
            direct = 3;
        this.direct = direct;
    }

    public void reset()
    {
        direct = 0;
        deviateX = 0;
        deviateY = 0;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        if (isSwitchActive && image01 != null && image02 != null)
        {
            int image02defX = 0, image02defY = 0;

            switch (direct)
            {
                case 0: image02defY = -getHeight(); break;
                case 1: image02defY = getHeight(); break;
                case 2: image02defX = -getWidth(); break;
                case 3: image02defX = getWidth(); break;
                default: break;
            }

//            System.out.println(image02defX + ", " + deviateX + ", " + image02defY + ", " + deviateY);
            g.drawImage(image02, image02defX + deviateX, image02defY + deviateY, null);
            g.drawImage(image01, deviateX, deviateY, null);
//            System.out.println("Painting Completed!");
        }
    }
}