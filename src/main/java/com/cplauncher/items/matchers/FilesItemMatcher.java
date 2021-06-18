package com.cplauncher.items.matchers;

import com.cplauncher.items.DirectoryItem;
import com.cplauncher.items.ResultItemsList;
import com.cplauncher.utils.Utils;
import com.cplauncher.platform.OsUtils;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileView;
import org.apache.commons.lang3.ArrayUtils;

public class FilesItemMatcher extends AbstractItemMatcher
{
    private List<File> directoriesToScan = new ArrayList<>();
    private List<FileAndNameCacheItem> filesCache = new ArrayList<>();

    private static final String[] regularFileTags = new String[]{"file"};
    private FileView fileView;

    public FilesItemMatcher(File... directoriesToScan)
    {
        super("root", "file", "executable");
        JFileChooser fileChooser = new JFileChooser();
        fileView = fileChooser.getUI().getFileView(fileChooser);

        Collections.addAll(this.directoriesToScan, directoriesToScan);
        resetCache();
    }

    private Icon getFileIcon(File file)
    {
        return fileView.getIcon(file);
    }

    @Override
    public void match(DirectoryItem rootItem, String text, ResultItemsList resultList)
    {
        if (rootItem == null)
        {
            if (!text.isEmpty())
            {
                //just list the files
                matchFileList(text, resultList);
            }
        }
        else
        {
            //request to execute
            if (ArrayUtils.contains(rootItem.getTags(), "file"))
            {
                createExecuteItems(rootItem, resultList);
            }
        }
    }

    private void createExecuteItems(DirectoryItem fileItem, ResultItemsList resultList)
    {
        DirectoryItem executeItem = new DirectoryItem();
        executeItem.setText("Execute");
        executeItem.setId(fileItem.getId());
        executeItem.setPriority(10);
        executeItem.setItemData(fileItem.getItemData());
        //executeItem.setExecutor((item) -> {
        //    File fileToExecute = (File)item.getItemData();
        //    System.out.println("Execute application " + fileToExecute.getAbsolutePath());
        //    Utils.executeExternalApplication("open", fileToExecute.getAbsolutePath());
        //});
        resultList.addItem(executeItem, true);

        DirectoryItem showFileItem = new DirectoryItem();
        showFileItem.setText("Show file");
        showFileItem.setCompletion(showFileItem.getText().toLowerCase());
        showFileItem.setId(fileItem.getId());
        showFileItem.setItemData(fileItem.getItemData());
        showFileItem.setPriority(1);
        //showFileItem.setExecutor((item) -> {
        //    File fileToExecute = (File)item.getItemData();
        //    System.out.println("Show file " + fileToExecute.getAbsolutePath());
        //    OsUtils.get().openFileInFileManager(fileToExecute.getAbsolutePath());
        //});
        resultList.addItem(showFileItem, true);
    }

    private void matchFileList(String text, ResultItemsList resultList)
    {
        int count = 0;
        for (FileAndNameCacheItem cacheItem : filesCache)
        {
            if (isMatched(cacheItem, text))
            {
                DirectoryItem item = new DirectoryItem();
                item.setId("file_item:" + cacheItem.file.getAbsolutePath());
                item.setText(cacheItem.file.getName().toLowerCase());
                item.setCompletion(item.getText());
                item.setItemData(cacheItem.file);
                item.setSubtext(item.getId());
                item.setTags(regularFileTags);
                item.setIcon(getFileIcon(cacheItem.file));
                resultList.addItem(item, false);

                count++;
                if (count > 50)
                {
                    break;
                }
            }
        }
    }

    private boolean isMatched(FileAndNameCacheItem cacheItem, String text)
    {
        //TODO search starting from tokenPositions, not just contains
        return cacheItem.file.getName().toLowerCase().contains(text);
    }

    public void resetCache()
    {
        filesCache = rereadFileSystem();
    }

    protected List<FileAndNameCacheItem> rereadFileSystem()
    {
        System.out.println("Start reread filesystem");
        List<FileAndNameCacheItem> result = new ArrayList<>(filesCache.size() / 2);
        for (File directory : directoriesToScan)
        {
            rereadDirectory(directory, result);
        }
        System.out.println("Found " + result.size() + " files");
        return result;
    }

    private void rereadDirectory(File directory, List<FileAndNameCacheItem> result)
    {
        File[] files = directory.listFiles();
        if (files == null)
        {
            return;
        }

        for (File file : files)
        {
            if (file.isDirectory() && !isExecutableDirectory(file))
            {
                rereadDirectory(file, result);
            }
            else
            {
                result.add(createCacheItem(file));
            }
        }
    }

    private boolean isExecutableDirectory(File file)
    {
        return file.getName().endsWith(".app");
    }

    private FileAndNameCacheItem createCacheItem(File file)
    {
        String name = file.getName();
        boolean executable;
        if (OsUtils._getOS() == OsUtils.OS.WINDOWS)
        {
            executable = name.toLowerCase().endsWith(".exe");
        }
        else
        {
            executable = Files.isExecutable(Paths.get(file.getAbsolutePath()));
        }

        return new FileAndNameCacheItem(file, executable);
    }

    private static class FileAndNameCacheItem
    {
        File file;
        boolean executable;

        public FileAndNameCacheItem(File file, boolean executable)
        {
            this.file = file;
            this.executable = executable;
        }

        @Override
        public String toString()
        {
            return Objects.toString(file);
        }
    }
}
