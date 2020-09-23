package com.acelych.atoz.widget;

import com.acelych.atoz.MainFrame;
import com.acelych.atoz.data.MediaHandler;

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

    public static InformationDialog createInformationDialog(MainFrame frame, JPanel outerPanel, String titleStr)
    {
        InformationDialog result = new InformationDialog(frame, outerPanel);
        result.dialogTitle.setText(titleStr);
        return result;
    }

    public static class InformationDialog extends NormalizedDialog
    {
        public JLabel dialogRange, dialogPhonetic, dialogTrans, dialogWarning;
        JButton dialogOk;

        InformationDialog(MainFrame frame, JPanel outerPanel)
        {
            super(frame, "Info", outerPanel);
            this.setSize(new Dimension(300, 200));

            dialogRange = new JLabel();
            dialogPhonetic = new JLabel();
            dialogTrans = new JLabel();
            JLabel dialogSeparator = new JLabel();
            dialogWarning = new JLabel("Couldn't Get Translation Info !");
            dialogOk = new JButton(new ImageIcon(MediaHandler.imageSearch));

            dialogSeparator.setOpaque(true);
            dialogSeparator.setBackground(MainFrame.GrayType01);
            dialogSeparator.setPreferredSize(new Dimension(260, 2));
            dialogWarning.setFont(MainFrame.consolas01);
            dialogWarning.setForeground(MainFrame.GrayType00);
            dialogWarning.setVisible(false);
            dialogRange.setFont(MainFrame.yahei01);
            dialogRange.setForeground(MainFrame.GrayType00);
            dialogPhonetic.setFont(new Font("Calibri", Font.BOLD, 15));
            dialogPhonetic.setForeground(MainFrame.BlackType02);
            dialogTrans.setFont(MainFrame.yahei02);
            dialogTrans.setForeground(MainFrame.BlackType02);
            dialogTrans.setSize(this.getWidth() - 30, 0);

            dialogPanel.add(dialogSeparator);
            dialogPanel.add(dialogWarning);
            dialogPanel.add(dialogRange);
            dialogPanel.add(dialogPhonetic);
            dialogPanel.add(dialogTrans);
            dialogPanel.add(dialogOk);

            dialogOk.setPressedIcon(new ImageIcon(MediaHandler.imageSearchHL));
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

            // modify title
            dialogTitle.setFont(new Font("Consolas", Font.BOLD, 25));
            dialogLayout.putConstraint(SpringLayout.WEST, dialogTitle, 20, SpringLayout.WEST, dialogPanel);
            dialogLayout.putConstraint(SpringLayout.NORTH, dialogTitle, 20, SpringLayout.NORTH, dialogPanel);

            dialogLayout.putConstraint(SpringLayout.EAST, dialogOk, -4, SpringLayout.EAST, dialogPanel);
            dialogLayout.putConstraint(SpringLayout.VERTICAL_CENTER, dialogOk, 0, SpringLayout.VERTICAL_CENTER, dialogTitle);
            dialogLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, dialogSeparator, 0, SpringLayout.HORIZONTAL_CENTER, dialogPanel);
            dialogLayout.putConstraint(SpringLayout.NORTH, dialogSeparator, 15, SpringLayout.SOUTH, dialogTitle);
            dialogLayout.putConstraint(SpringLayout.WEST, dialogPhonetic, 0, SpringLayout.WEST, dialogTitle);
            dialogLayout.putConstraint(SpringLayout.NORTH, dialogPhonetic, 10, SpringLayout.SOUTH, dialogSeparator);
            dialogLayout.putConstraint(SpringLayout.WEST, dialogTrans, 0, SpringLayout.WEST, dialogTitle);
            dialogLayout.putConstraint(SpringLayout.NORTH, dialogTrans, 6, SpringLayout.SOUTH, dialogPhonetic);
            dialogLayout.putConstraint(SpringLayout.WEST, dialogRange, 0, SpringLayout.WEST, dialogTitle);
            dialogLayout.putConstraint(SpringLayout.SOUTH, dialogRange, -10, SpringLayout.SOUTH, dialogPanel);

            dialogLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, dialogWarning, 0, SpringLayout.HORIZONTAL_CENTER, dialogPanel);
            dialogLayout.putConstraint(SpringLayout.VERTICAL_CENTER, dialogWarning, 35, SpringLayout.VERTICAL_CENTER, dialogPanel);
        }

        @Override
        public void setVisible(boolean b)
        {
            this.setSize(300, 200);
            if (this.dialogTrans.getPreferredSize().height > 40)
                this.setSize(this.getWidth(), this.getHeight() + this.dialogTrans.getPreferredSize().height - 40);
            if (this.dialogPhonetic.getPreferredSize().height > 19)
                this.setSize(this.getWidth(), this.getHeight() + this.dialogPhonetic.getPreferredSize().height - 19);
            this.dialogTrans.setSize(this.getWidth() - 30, 0);
            super.setVisible(b);
        }
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

        /**
         * Basic Atoz Dialog Format
         * @param frame frame to block
         * @param string dialog TitleBox title
         * @param outerPanel panel to align
         */
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
                    parent.getY() + outerPanel.getY() + outerPanel.getHeight() / 2 - this.getHeight() / 2 + 10);
            super.setVisible(b);
        }
    }
}
