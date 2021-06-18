package com.cplauncher.items.matchers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MatchersManager
{
    private List<AbstractItemMatcher> allMatchers = new ArrayList<>();
    private Map<String, List<AbstractItemMatcher>> tagToItemMatchers = new HashMap<>();

    public void initMatchers()
    {
        addMatcher(new FilesItemMatcher(new File("/Applications/"), new File("/Users/dmitry/Documents/books/")));
        addMatcher(new ShellCommandsMatcher());
        addMatcher(new BookmarksItemMatcher());
    }

    private void addMatcher(AbstractItemMatcher matcher)
    {
        for (String tag : matcher.getMatchableTags())
        {
            List<AbstractItemMatcher> tags = tagToItemMatchers.computeIfAbsent(tag, (t) -> new ArrayList<>());
            if (!tags.contains(matcher))
            {
                tags.add(matcher);
            }
        }
        allMatchers.add(matcher);
    }

    public List<AbstractItemMatcher> getMatchersByTags(String... tags)
    {
        Set<AbstractItemMatcher> matchers = new HashSet<>();
        for (String tag : tags)
        {
            List<AbstractItemMatcher> matchersForTag = tagToItemMatchers.get(tag);
            if (matchersForTag != null)
            {
                matchers.addAll(matchersForTag);
            }
        }
        return new ArrayList<>(matchers);
    }
}
