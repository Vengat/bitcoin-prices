package com.vengat.bitcoin_service.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vengat.bitcoin_service.model.BitcoinPrice;


public class BitcoinBtree implements java.io.Serializable {

    private final static Logger logger = LoggerFactory.getLogger(BitcoinBtree.class);
    
    private BTreeNode root;
    private int t;
    private static transient final String FILENAME = "btree.ser";
    private static transient final long serialVersionUID = 1L;

    private transient  ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private transient Lock readLock = readWriteLock.readLock();
    private transient Lock writeLock = readWriteLock.writeLock();

    private transient ExecutorService executorService = Executors.newFixedThreadPool(4);

    // Ensure locks are initialized after deserialization
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        readWriteLock = new ReentrantReadWriteLock();
        readLock = readWriteLock.readLock();
        writeLock = readWriteLock.writeLock();
        executorService = Executors.newFixedThreadPool(4);
    }

    public class BTreeNode implements java.io.Serializable {
        BitcoinPrice[] keys;
        int n;
        BTreeNode[] children;
        boolean isLeaf;
        public BTreeNode(boolean isLeaf) {
            this.isLeaf = isLeaf;
            keys = new BitcoinPrice[(2 * t - 1)];
            children = new BTreeNode[(2 * t)];
            n = 0;
        }
    }

    public BitcoinBtree(int t) {
        this.t = t;
        root = new BTreeNode(true);
        // this.filename = "btree.ser";
    }

    public static BitcoinBtree initializeOrLoad(int t) {
        // String filename = "btree.ser";
        File file = new File(FILENAME);
        if (file.exists()) {
            return deserializeFromFile(FILENAME);
        } else {
            BitcoinBtree newTree = new BitcoinBtree(t);
            newTree.serializeToFile();
            // newTree.writeToDiskAsync();
            return newTree;
        }
    }

    private static BitcoinBtree deserializeFromFile(String filename) {
        try (FileInputStream fileIn = new FileInputStream(filename);
                ObjectInputStream in = new ObjectInputStream(fileIn)) {
            return (BitcoinBtree) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Error deserializing BTree from file", e);
            return new BitcoinBtree(6); // Return a new tree if deserialization fails
        }
    }

    public void deserializeFromFile() {
        readLock.lock();
        try (FileInputStream fileIn = new FileInputStream(FILENAME);
                ObjectInputStream in = new ObjectInputStream(fileIn)) {
            BitcoinBtree loadedTree = (BitcoinBtree) in.readObject();
            this.root = loadedTree.root;
            this.t = loadedTree.t;
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Error deserializing BTree from file", e);
            this.root = new BTreeNode(true);
        } finally {
            readLock.unlock();
        }
    }

    private void serializeToFile() {
        writeLock.lock();
        try (
                FileOutputStream fileOut = new FileOutputStream(FILENAME);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);) {
            out.writeObject(this);
        } catch (IOException e) {
            logger.error("Error serializing BTree to file", e);
        } finally {
            writeLock.unlock();
        }
    }

    private void logInsert(BitcoinPrice key) {
        executorService.execute(() -> {
            writeLock.lock();
            try (FileWriter fw = new FileWriter("btree.log", true);
                    PrintWriter pw = new PrintWriter(fw)) {
                pw.println(key);
            } catch (IOException e) {
                logger.error("Error writing to log file", e);
                e.printStackTrace();
            } finally {
                writeLock.unlock();
            }
        });
    }

    private void writeToDiskAsync() {
        // new Thread(() -> serializeToFile()).start();
        logger.info("Writing to disk asynchronously");
        CompletableFuture.runAsync(() -> serializeToFile(), executorService);
    }

    public boolean contains(BitcoinPrice key) {
        readLock.lock();
        logger.info("Checking if key exists in BTree");
        try {
            return search(key) != null;
        } finally {
            readLock.unlock();
        }       
    }

    public BTreeNode search(BitcoinPrice k) {
        readLock.lock();
        logger.info("Searching for key in BTree");
        try {
            return search(root, k);
        } finally {
            readLock.unlock();
        }
    }

    private BTreeNode search(BTreeNode node, BitcoinPrice k) {
        logger.info("Searching for key in BTree node");
        if (node == null) {
            return null;
        }

        int i = node.n - 1;
        while (i >= 0 && node.keys[i].compareTo(k) > 0) {
            i--;
        }

        if (i >= 0 && node.keys[i].compareTo(k) == 0) {
            return node;
        }

        if (node.isLeaf) {
            logger.info("Key not found in BTree node");
            return null;
        } else {
            return search(node.children[i + 1], k);
        }

    }

    private void search_range(BTreeNode node, Date start, Date end, List<BitcoinPrice> result) {
        if (node == null) {
            return;
        }

        logger.info("Searching for keys in BTree node in range {} to {}", start, end);
        int i = 0;
        while (i < node.n && node.keys[i].getDate().before(start)) {
            i++;
        }

        if (node.isLeaf) {
            while (i < node.n && !node.keys[i].getDate().after(end)) {
                result.add(node.keys[i]);
                i++;
            }
        } else {
            while (i < node.n) {
                search_range(node.children[i], start, end, result);
                if (i < node.n && !node.keys[i].getDate().after(end)) {
                    result.add(node.keys[i]);
                }
                i++;
            }
            search_range(node.children[i], start, end, result);
        }
    }

    public List<BitcoinPrice> search_range(Date start, Date end) {
        readLock.lock();
        logger.info("Searching for keys in BTree in range {} to {}", start, end);
        try {
            List<BitcoinPrice> result = new ArrayList<>();
            search_range(root, start, end, result);
            BitcoinPrice.markMaxMinInRange(result, start, end);
            return result;
        } finally {
            readLock.unlock();
        }
    
    }



    public void insertList(BitcoinPrice[] keys) {
        writeLock.lock();
        try {
            for (BitcoinPrice key : keys) {
                insertIfNotExists(key);
            }
        } finally {
            writeLock.unlock();
        }
    }

    public void insertList(List<BitcoinPrice> keys) {
        writeLock.lock();
        try {
            for (BitcoinPrice key : keys) {
                insertIfNotExists(key);
            }
        } finally {
            writeLock.unlock();
        }
    }

    public void insertIfNotExists(BitcoinPrice key) {
        writeLock.lock();
        try {
            if (!contains(key)) {
                insert(key);
                logInsert(key);
                writeToDiskAsync();
            }
        } finally {
            writeLock.unlock();
        }
    }

    public void insert(BitcoinPrice key) {
        writeLock.lock();
        try {
            BTreeNode r = root;
            if (r.n == 2 * t - 1) {
                BTreeNode s = new BTreeNode(false);
                root = s;
                s.children[0] = r;
                splitNode(s, 0, r);
                insertNonFull(s, key);
            } else {
                insertNonFull(r, key);
            }
        } finally {
            writeLock.unlock();
        }
    }

    private void splitNode(BTreeNode parent, int i, BTreeNode fullChild) {
        writeLock.lock();
        try {
            if (parent == null || fullChild == null) {
                throw new IllegalArgumentException("Parent or fullChild node cannot be null");
            }
    
            if (i < 0 || i > parent.n) {
                throw new IndexOutOfBoundsException("Invalid index for parent node");
            }
    
            if (fullChild.n != 2 * t - 1) {
                throw new IllegalStateException("Child node is not full");
            }
    
            BTreeNode newNode = new BTreeNode(fullChild.isLeaf);
            newNode.n = t - 1;
    
            for (int j = 0; j < t - 1; j++) {
                if (fullChild.keys[j + t] == null) {
                    logger.error("Key cannot be null");
                    throw new IllegalStateException("Key cannot be null");
                }
                newNode.keys[j] = fullChild.keys[j + t];
                fullChild.keys[j + t] = null;
            }
    
            if (!fullChild.isLeaf) {
                for (int j = 0; j < t; j++) {
                    if (fullChild.children[j + t] == null) {
                        logger.error("Child node cannot be null");
                        throw new IllegalStateException("Child node cannot be null");
                    }
                    newNode.children[j] = fullChild.children[j + t];
                    fullChild.children[j + t] = null;
                }
            }
    
            fullChild.n = t - 1;
    
            for (int j = parent.n; j >= i + 1; j--) {
                parent.children[j + 1] = parent.children[j];
            }
            parent.children[i + 1] = newNode;
    
            for (int j = parent.n - 1; j >= i; j--) {
                parent.keys[j + 1] = parent.keys[j];
            }
    
            if (fullChild.keys[t - 1] == null) {
                logger.error("Key cannot be null");
                throw new IllegalStateException("Key cannot be null");
            }
            parent.keys[i] = fullChild.keys[t - 1];
            // Remove the reference of the middle key from the full child
            fullChild.keys[t - 1] = null;
            parent.n += 1;
            if (parent.n > 2 * t - 1) {
                logger.error("Parent node is full");
                throw new IllegalStateException("Parent node is full");
            }
        } finally {
            writeLock.unlock();
        }
    }

    private void insertNonFull(BTreeNode node, BitcoinPrice key) {
        int i = node.n - 1;

        if (node.isLeaf) {
            writeLock.lock();
            try {
                while (i >= 0 && node.keys[i].compareTo(key) > 0) {
                    node.keys[i + 1] = node.keys[i];
                    i--;
                }
                node.keys[i + 1] = key;
                node.n += 1;
            } finally {
                writeLock.unlock();
            }
        } else {
            while (i >= 0 && node.keys[i].compareTo(key) > 0) {
                i--;
            }
            i++;
            writeLock.lock();
            try {
                if (node.children[i].n == 2 * t - 1) {
                    splitNode(node, i, node.children[i]);
                    if (node.keys[i].compareTo(key) < 0) {
                        i++;
                    }
                }
            } finally {
                writeLock.unlock();
            }
            insertNonFull(node.children[i], key);
        }
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException ex) {
            logger.error("Error shutting down executor service", ex);
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
