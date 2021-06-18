package com.cplauncher.ui.inputselector;

import com.cplauncher.items.DirectoryItem;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

public class ListItemRenderer extends DefaultListCellRenderer
{
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
        JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        DirectoryItem item = (DirectoryItem)value;
        label.setIcon(item.getIcon());
        return label;
    }
}
