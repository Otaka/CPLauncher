package com.cplauncher.items.matchers;

import com.cplauncher.items.DirectoryItem;
import com.cplauncher.items.ResultItemsList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CompoundItemMatcher extends AbstractItemMatcher
{
    private List<AbstractItemMatcher> childMatchers = new ArrayList<>();

    public CompoundItemMatcher(List<AbstractItemMatcher> childMatchers)
    {
        super(collectMatchableTags(childMatchers));
        this.childMatchers.addAll(childMatchers);
    }

    @Override
    public void match(DirectoryItem rootItem, String text, ResultItemsList resultList)
    {
        for (AbstractItemMatcher matcher : childMatchers)
        {
            matcher.match(rootItem, text, resultList);
        }
    }

    @Override
    public void onMatcherActivated()
    {
        for (AbstractItemMatcher matcher : childMatchers)
        {
            matcher.onMatcherActivated();
        }
    }

    @Override
    public void onMatcherDeactivated()
    {
        for (AbstractItemMatcher matcher : childMatchers)
        {
            matcher.onMatcherDeactivated();
        }
    }

    private static String[] collectMatchableTags(List<AbstractItemMatcher> childMatchers)
    {
        Set<String> tags = new HashSet<>();
        for (AbstractItemMatcher matcher : childMatchers)
        {
            Collections.addAll(tags, matcher.getMatchableTags());
        }
        return tags.toArray(new String[0]);
    }
}
