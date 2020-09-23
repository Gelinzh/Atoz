package com.acelych.atoz.widget;

import javax.swing.*;
import java.awt.*;

public class SeparatorFactory
{
    public static final int LONG = 30;
    public static final int SHORT =  20;
    public static final int WIDTH = 1;

    public static JLabel builder(int type)
    {
        JLabel result = new JLabel();
        result.setOpaque(true);
        result.setBackground(new Color(220, 220, 220));
        result.setPreferredSize(new Dimension(1, type));
        return result;
    }
}
