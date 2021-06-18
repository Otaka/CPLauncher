package com.cplauncher.items.matchers;

import com.cplauncher.items.DirectoryItem;
import com.cplauncher.items.ResultItemsList;

public abstract class AbstractItemMatcher
{
    private String[] matchableTags;

    public AbstractItemMatcher(String... matchableTags)
    {
        this.matchableTags = matchableTags;
    }

    protected void setMatchableTags(String[] matchableTags)
    {
        this.matchableTags = matchableTags;
    }

    public String[] getMatchableTags()
    {
        return matchableTags;
    }

    public void match(DirectoryItem rootItem, String text, ResultItemsList resultList)
    {

    }

    public void onMatcherActivated()
    {

    }

    public void onMatcherDeactivated()
    {

    }
}
