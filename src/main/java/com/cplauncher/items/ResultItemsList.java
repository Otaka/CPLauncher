package com.cplauncher.items;

import java.util.ArrayList;
import java.util.List;

public class ResultItemsList
{
    private String textToMatch;
    private List<AbstractItem> items = new ArrayList<>(50);

    public ResultItemsList(String textToMatch)
    {
        this.textToMatch = textToMatch;
    }

    public void addItem(AbstractItem item, boolean applyFilter)
    {
        if (applyFilter)
        {
            if (item.getText().toLowerCase().contains(textToMatch))
            {
                items.add(item);
            }
        }
        else
        {
            items.add(item);
        }
    }

    public List<AbstractItem> getItems()
    {
        return items;
    }
}
