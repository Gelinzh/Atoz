package com.acelych.atoz;

import java.awt.*;

public class Main
{
    private static void createGUI()
    {
        MainFrame frame = new MainFrame("Atoz");
        frame.setVisible(true);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(Thread.currentThread().getContextClassLoader().getResource("images/icon.png")));
        frame.initPanelImage();
    }

    public static void main(String[] args)
    {
        javax.swing.SwingUtilities.invokeLater(Main::createGUI);
    }
}
