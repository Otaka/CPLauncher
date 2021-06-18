package com.cplauncher.ui.inputselector;

import javax.swing.DefaultListModel;
import javax.swing.JList;

public class JCpList<T> extends JList<T>
{
    private DefaultListModel<T> listModel;

    public JCpList()
    {
        super(new DefaultListModel<T>());
        listModel = (DefaultListModel<T>)getModel();
    }

    public int getItemsCount()
    {
        return listModel.getSize();
    }

    public boolean isEmpty()
    {
        return listModel.isEmpty();
    }

    public void clear()
    {
        listModel.clear();
    }

    public void addItem(T item)
    {
        listModel.addElement(item);
    }

    public T getLastItem()
    {
        return listModel.lastElement();
    }

    public int getLastElementIndex()
    {
        return listModel.getSize() - 1;
    }

    public T removeItem(int index)
    {
        return listModel.remove(index);
    }

    public T removeItem(T item)
    {
        listModel.removeElement(item);
        return item;
    }
}
