package com.cplauncher.items;

public class ActionItem extends AbstractItem
{
    private boolean defaultItem;

    private ItemExecutor executor;

    public void setDefaultItem(boolean defaultItem)
    {
        this.defaultItem = defaultItem;
    }

    public boolean isDefaultItem()
    {
        return defaultItem;
    }

    public void setExecutor(ItemExecutor executor)
    {
        this.executor = executor;
    }

    public ItemExecutor getExecutor()
    {
        return executor;
    }
}
