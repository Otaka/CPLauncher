package com.cplauncher.items.matchers;

import com.cplauncher.items.DirectoryItem;
import com.cplauncher.items.ResultItemsList;
import com.cplauncher.platform.OsUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public class ShellCommandsMatcher extends AbstractItemMatcher
{
    private String[] tags = new String[]{"shell"};

    public ShellCommandsMatcher()
    {
        super("root", "shell");
    }

    @Override
    public void match(DirectoryItem rootItem, String text, ResultItemsList resultList)
    {
        if (rootItem == null)
        {
            if (text.isEmpty())
            {
                return;
            }
            String prefix = "shell";
            String textToMath = StringUtils.truncate(text, prefix.length());

            if (prefix.startsWith(textToMath))
            {
                String textToOutput;
                if (text.length() > prefix.length())
                {
                    textToOutput = text.substring(prefix.length()).trim();
                }
                else
                {
                    textToOutput = "";
                }
                DirectoryItem shellItem = createShellItem(textToOutput);
                resultList.addItem(shellItem, false);
            }
        }
        else if (ArrayUtils.contains(rootItem.getTags(), "shell"))
        {
            resultList.addItem(createShellItem(text), false);
        }
    }

    private DirectoryItem createShellItem(String textToOutput)
    {
        DirectoryItem shellItem = new DirectoryItem();
        shellItem.setTags(tags);
        shellItem.setPriority(1);
        shellItem.setCompletion("echo shell");
        shellItem.setText("Echo in Shell text [" + textToOutput + "]");
        shellItem.setItemData(textToOutput);
       // shellItem.setExecutor((item) -> {
       //     String escapedCommand = StringEscapeUtils.escapeJava("echo \"" + (String)item.getItemData() + "\"");
       //     OsUtils.get().executeShellCommand(escapedCommand);
       // });
        return shellItem;
    }
}
