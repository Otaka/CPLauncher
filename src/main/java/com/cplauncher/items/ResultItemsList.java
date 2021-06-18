package com.cplauncher.items;

import java.util.ArrayList;
import java.util.List;

public class ResultItemsList
{
    private String textToMatch;
    private List<DirectoryItem> items = new ArrayList<>(50);

    public ResultItemsList(String textToMatch)
    {
        this.textToMatch = textToMatch;
    }

    public void addItem(DirectoryItem item, boolean applyFilter)
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

    public List<DirectoryItem> getItems()
    {
        return items;
    }
}
