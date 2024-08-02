package com.vengat.bitcoin_service.cache.obsolete;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import com.vengat.bitcoin_service.cache.BitcoinBtree;
import com.vengat.bitcoin_service.cache.BitcoinBtree.BTreeNode;

public class PageManager {

    private Map<Long, BTreeNode> buffer;
    private int bufferSize;
    private String directory;

    public PageManager(int bufferSize, String directory) {
        this.buffer = new LinkedHashMap<Long, BTreeNode>(bufferSize, 0.75f, true) {
            private static final long serialVersionUID = 1L;
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, BTreeNode> eldest) {
                return size() > bufferSize;
            }
        };
        this.bufferSize = bufferSize;
        this.directory = directory;
    }

    public BTreeNode loadPage(long pageId) {
        BTreeNode node = buffer.get(pageId);
        if (node == null) {
            node = loadPageFromDisk(pageId);
            if (node != null) {
                buffer.put(pageId, node);
            }
        }
        return node;
    }

    public void savePage(long pageId, BTreeNode node) {
        buffer.put(pageId, node);
        savePageToDisk(pageId, node);
    }

    private void savePageToDisk(long pageId, BTreeNode node) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getFilePath(pageId)))) {
            oos.writeObject(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BTreeNode loadPageFromDisk(long pageId) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(getFilePath(pageId)))) {
            return (BTreeNode) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getFilePath(long pageId) {
        return directory + File.separator + "page_" + pageId + ".ser";
    }
}