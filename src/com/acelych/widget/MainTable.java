package com.acelych.widget;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
public class MainTable extends JTable
{
    public MainTable(DefaultTableModel tableModel)
    {
        super(tableModel);

//        worldListTable.setBorder(null);
//        this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        this.setRowHeight(30);
        this.setGridColor(new Color(220, 220, 220));
//        this.setBorder(BorderFactory.createLineBorder(Color.GRAY));
//        this.setFillsViewportHeight(true); //不知道有什么用
        this.setRowSelectionAllowed(true); //整行选择//废话
        this.getTableHeader().setReorderingAllowed(false);
        this.getTableHeader().setResizingAllowed(false);
    }

    @Override
    public boolean isCellEditable(int row, int column)
    {
        return false;
    }
}
