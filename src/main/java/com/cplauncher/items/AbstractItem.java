package com.cplauncher.items;

import javax.swing.Icon;

public class AbstractItem
{
    private String id;
    /**
     * text that shows in item
     */
    private String text;
    /**
     * optional description that is shown in lowercase below the main text
     */
    private String subtext;
    /**
     * search engine use this field for actual search
     */
    private String completion;

    private Icon icon;

    /**
     * defines the sort order. The bigger the value - the closer to top of list item will be
     */
    private int priority;
    /**
     * Field that item matcher can set, and get freely. Engine just pass it without changes
     */
    private Object itemData;

    /**
     * tags are used to search all child items and actions
     */
    private String[] tags;

    public String[] getTags()
    {
        return tags;
    }

    public void setTags(String[] tags)
    {
        this.tags = tags;
    }

    public void setCompletion(String completion)
    {
        this.completion = completion;
    }

    public String getCompletion()
    {
        return completion;
    }

    public void setItemData(Object itemData)
    {
        this.itemData = itemData;
    }

    public Object getItemData()
    {
        return itemData;
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
    }

    public int getPriority()
    {
        return priority;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public Icon getIcon()
    {
        return icon;
    }

    public void setIcon(Icon icon)
    {
        this.icon = icon;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getSubtext()
    {
        return subtext;
    }

    public void setSubtext(String subtext)
    {
        this.subtext = subtext;
    }

    @Override
    public String toString()
    {
        return text;
    }
}
