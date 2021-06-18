package com.cplauncher.items.matchers;

import com.cplauncher.items.DirectoryItem;
import com.cplauncher.items.ResultItemsList;
import com.cplauncher.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

public class BookmarksItemMatcher extends AbstractItemMatcher
{
    private List<Bookmark> bookmarks = new ArrayList<>();
    private String[] defaultUrlTags = new String[]{"url", "bookmark"};

    public BookmarksItemMatcher()
    {
        super("root", "url");
        refresh();
    }

    @Override
    public void match(DirectoryItem rootItem, String text, ResultItemsList resultList)
    {
        if (rootItem == null)
        {
            matchBookmarks(text, resultList);
        }
        else if (ArrayUtils.contains(rootItem.getTags(), "url"))
        {
            resultList.addItem(createOpenUrlItem((String)rootItem.getItemData()), false);
        }
    }

    private void matchBookmarks(String text, ResultItemsList resultList)
    {
        if (text.isEmpty())
        {
            return;
        }
        for (Bookmark bookmark : bookmarks)
        {
            if (bookmark.getBookmarkTextLower.contains(text))
            {
                DirectoryItem bookmarkItem = new DirectoryItem();
                bookmarkItem.setId("bookmark " + bookmark.guid);
                bookmarkItem.setText(bookmark.bookmarkText);
                bookmarkItem.setCompletion(bookmark.bookmarkText.toLowerCase());
                bookmarkItem.setSubtext(bookmark.bookmarkText);
                bookmarkItem.setTags(defaultUrlTags);
                bookmarkItem.setItemData(bookmark.url);
                //bookmarkItem.setExecutor((item) -> {
                //    String url = (String)item.getItemData();
                //    Utils.executeExternalApplication("open", url);
                //});
                resultList.addItem(bookmarkItem, false);
            }
        }
    }

    private DirectoryItem createOpenUrlItem(String url)
    {
        DirectoryItem openUrlItem = new DirectoryItem();

        openUrlItem.setId("open url:" + url);
        openUrlItem.setText("Open in default browser");
        openUrlItem.setItemData(url);
        //openUrlItem.setExecutor((item) -> {
        //    String _url = (String)item.getItemData();
        //    Utils.executeExternalApplication("open", _url);
        //});
        return openUrlItem;
    }

    private void refresh()
    {
        List<Bookmark> collectedBookmarks = new ArrayList<>();
        readGoogleChromeBookmarks(collectedBookmarks);
        bookmarks = collectedBookmarks;
    }

    private void readGoogleChromeBookmarks(List<Bookmark> collectedBookmarks)
    {
        String homeFolder = System.getProperty("user.home");
        File bookmarksFile = new File(homeFolder, "Library/Application Support/Google/Chrome/Default/Bookmarks").getAbsoluteFile();
        if (!bookmarksFile.exists())
        {
            return;
        }
        try
        {
            JsonNode rootNode = new ObjectMapper().readTree(bookmarksFile);
            for (JsonNode name : rootNode.get("roots"))
            {
                parseChromeJsonBookmarkItem(name, collectedBookmarks);
            }
        }
        catch (IOException e)
        {
            System.out.println("Error while reading chrome bookmarks");
        }
    }

    private void parseChromeJsonBookmarkItem(JsonNode jsonNode, List<Bookmark> collectedBookmarks)
    {
        String type = jsonNode.get("type").asText();
        if (type.equals("folder"))
        {
            for (JsonNode child : jsonNode.get("children"))
            {
                parseChromeJsonBookmarkItem(child, collectedBookmarks);
            }
        }
        else if (type.equals("url"))
        {
            String name = jsonNode.get("name").asText();
            String url = jsonNode.get("url").asText();
            String guid = jsonNode.get("guid").asText();
            collectedBookmarks.add(new Bookmark(url, name, name.toLowerCase(), guid));
        }
        else
        {
            System.out.println("Unknown chrome bookmark item type " + type);
        }
    }

    private static class Bookmark
    {
        String url;
        String bookmarkText;
        String getBookmarkTextLower;
        String guid;

        public Bookmark(String url, String bookmarkText, String getBookmarkTextLower, String guid)
        {
            this.url = url;
            this.bookmarkText = bookmarkText;
            this.getBookmarkTextLower = getBookmarkTextLower;
            this.guid = guid;
        }
    }
}
