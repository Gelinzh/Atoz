package com.acelych.widget;

import com.acelych.MainFrame;
import com.acelych.data.MediaHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class DialogFactory
{
    public static OptionDialog createOptionalDialog(MainFrame frame, JPanel outerPanel, String titleStr, String describeStr)
    {
        OptionDialog result = new OptionDialog(frame, outerPanel);
        result.dialogTitle.setText(titleStr);
        result.dialogDescribe.setText(describeStr);
        return result;
    }

    public static ConfirmationDialog createConfirmationDialog(MainFrame frame, JPanel outerPanel, String titleStr)
    {
        ConfirmationDialog result = new ConfirmationDialog(frame, outerPanel);
        result.dialogTitle.setText(titleStr);
        return result;
    }

    public static class OptionDialog extends NormalizedDialog
    {
        public boolean result = false;
        JLabel dialogDescribe;
        JButton dialogYes, dialogNo;

        OptionDialog(MainFrame frame, JPanel outerPanel)
        {
            super(frame, "Warning", outerPanel);

            dialogDescribe = new JLabel();
            dialogYes = new JButton(new ImageIcon(MediaHandler.imageContinue));
            dialogNo = new JButton(new ImageIcon(MediaHandler.imageCancel));

            dialogDescribe.setFont(MainFrame.consolas01);
            dialogDescribe.setForeground(new Color(120, 120, 120));

            dialogPanel.add(dialogDescribe);
            dialogPanel.add(dialogYes);
            dialogPanel.add(dialogNo);

            dialogYes.setPressedIcon(new ImageIcon(MediaHandler.imageContinueHL));
            dialogYes.setBorderPainted(false);
            dialogYes.setFocusPainted(false);
            dialogYes.setContentAreaFilled(false);
            dialogYes.addActionListener(e -> dialogClose(true));
            dialogYes.addKeyListener(new KeyAdapter()
            {
                @Override
                public void keyReleased(KeyEvent e)
                {
                    if (e.getKeyCode() == 10)
                        dialogClose(true);
                    else if (e.getKeyCode() == 27)
                        dialogClose(false);
                }
            });

            dialogNo.setPressedIcon(new ImageIcon(MediaHandler.imageCancelHL));
            dialogNo.setBorderPainted(false);
            dialogNo.setFocusPainted(false);
            dialogNo.setContentAreaFilled(false);
            dialogNo.addActionListener(e -> dialogClose(false));
            dialogNo.addKeyListener(new KeyAdapter()
            {
                @Override
                public void keyPressed(KeyEvent e)
                {
                    if (e.getKeyCode() == 10 || e.getKeyCode() == 27)
                        dialogClose(false);
                }
            });

            dialogLayout.putConstraint(SpringLayout.NORTH, dialogTitle, this.getHeight() / 5, SpringLayout.NORTH, dialogPanel);
            dialogLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, dialogDescribe, 0, SpringLayout.HORIZONTAL_CENTER, dialogPanel);
            dialogLayout.putConstraint(SpringLayout.NORTH, dialogDescribe, 2, SpringLayout.SOUTH, dialogTitle);
            dialogLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, dialogYes, -this.getWidth() / 6, SpringLayout.HORIZONTAL_CENTER, dialogPanel);
            dialogLayout.putConstraint(SpringLayout.NORTH, dialogYes, 10, SpringLayout.SOUTH, dialogDescribe);
            dialogLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, dialogNo, this.getWidth() / 6, SpringLayout.HORIZONTAL_CENTER, dialogPanel);
            dialogLayout.putConstraint(SpringLayout.NORTH, dialogNo, 10, SpringLayout.SOUTH, dialogDescribe);

            dialogClose(false);
        }

        void dialogClose(boolean isDelete)
        {
            this.result = isDelete;
            this.setVisible(false);
        }
    }

    public static class ConfirmationDialog extends NormalizedDialog
    {
        JButton dialogOk;

        ConfirmationDialog(MainFrame frame, JPanel outerPanel)
        {
            super(frame, "Error", outerPanel);

            dialogOk = new JButton(new ImageIcon(MediaHandler.imageContinue));

            dialogPanel.add(dialogOk);

            dialogOk.setPressedIcon(new ImageIcon(MediaHandler.imageContinueHL));
            dialogOk.setBorderPainted(false);
            dialogOk.setFocusPainted(false);
            dialogOk.setContentAreaFilled(false);
            dialogOk.addActionListener(e -> setVisible(false));
            dialogOk.addKeyListener(new KeyAdapter()
            {
                @Override
                public void keyReleased(KeyEvent e)
                {
                    if (e.getKeyCode() == 10 || e.getKeyCode() == 27)
                        setVisible(false);
                }
            });

            dialogLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, dialogOk, 0, SpringLayout.HORIZONTAL_CENTER, dialogPanel);
            dialogLayout.putConstraint(SpringLayout.NORTH, dialogOk, 15, SpringLayout.SOUTH, dialogTitle);
        }
    }
    
    private static abstract class NormalizedDialog extends JDialog
    {
        MainFrame parent;

        SpringLayout dialogLayout;
        JPanel dialogPanel;
        public JLabel dialogTitle;
        
        JPanel outerPanel;

        NormalizedDialog(MainFrame frame, String string, JPanel outerPanel)
        {
            super(frame, string, true);
            this.setSize(new Dimension(250, 120));
            this.setUndecorated(true);
            this.parent = frame;
            this.outerPanel = outerPanel;

            dialogLayout = new SpringLayout();
            dialogPanel = new JPanel(dialogLayout);
            this.setContentPane(dialogPanel);
            dialogPanel.setBackground(MainFrame.GrayType03);
            dialogPanel.setBorder(BorderFactory.createLineBorder(MainFrame.GrayType01));

            dialogTitle = new JLabel();
            dialogTitle.setFont(MainFrame.consolas03);

            dialogPanel.add(dialogTitle);

            dialogLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, dialogTitle, 0, SpringLayout.HORIZONTAL_CENTER, dialogPanel);
            dialogLayout.putConstraint(SpringLayout.NORTH, dialogTitle, this.getHeight() / 4, SpringLayout.NORTH, dialogPanel);
        }

        @Override
        public void setVisible(boolean b)
        {
            this.setLocation(
                    parent.getX() + outerPanel.getX() + outerPanel.getWidth() / 2 - this.getWidth() / 2,
                    parent.getY() + outerPanel.getY() + outerPanel.getHeight() / 2 - this.getHeight() / 2);
            super.setVisible(b);
        }
    }
}
