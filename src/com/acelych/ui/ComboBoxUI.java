package com.acelych.ui;

import com.acelych.data.MediaHandler;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;

public class ComboBoxUI extends BasicComboBoxUI
{
    private JButton arrowButton;

    @Override
    protected JButton createArrowButton()
    {
        arrowButton = new JButton(new ImageIcon(MediaHandler.imageDown));

        arrowButton.setOpaque(false);
        arrowButton.setBorderPainted(false);
        arrowButton.setContentAreaFilled(false);
        arrowButton.setPressedIcon(new ImageIcon(MediaHandler.imageDownHL));
        arrowButton.setPreferredSize(new Dimension(MediaHandler.imageDown.getWidth(), MediaHandler.imageDown.getHeight()));

        return arrowButton;
    }

    @Override
    public void paint(Graphics g, JComponent c)
    {
        Color GrayType01 = new Color(220, 220, 220); //paint line default
        Color GrayType00 = new Color(119, 119, 119); //paint selection
        Rectangle r = new Rectangle(1, comboBox.getHeight() - 5, comboBox.getWidth() - arrowButton.getWidth() - 2, 2);

        g.setColor(comboBox.getBackground());
        g.fillRect(0, 0, comboBox.getWidth(), comboBox.getHeight());

        g.setColor(GrayType01);
        g.fillRect(r.x, r.y, r.width, r.height);

        JLabel label = new JLabel((String) comboBox.getSelectedItem());
        label.setForeground(GrayType00);
        label.setFont(new Font("Consolas", Font.BOLD, 12));

        currentValuePane.paintComponent(g, label, comboBox,
                ((comboBox.getWidth() - arrowButton.getWidth()) - label.getPreferredSize().width) / 2,
                (comboBox.getHeight() - label.getPreferredSize().height) / 2 + 2,
                label.getPreferredSize().width, label.getPreferredSize().height, true);
    }
}
