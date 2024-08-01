package com.vengat.bitcoin_service.cache.obsolete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import com.vengat.bitcoin_service.model.BitcoinPrice;

public class BitcoinBTree_Old {
    private BTreeNode root;
    private int t;

    public class BTreeNode {
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

    public BitcoinBTree_Old(int t) {
        this.t = t;
        root = new BTreeNode(true);
    }

    public BTreeNode search(BitcoinPrice k) {
        return search(root, k);
    }

    private BTreeNode search(BTreeNode node, BitcoinPrice k) {

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
            return null;
        } else {
            return search(node.children[i + 1], k);
        }

    }

    private void search_range(BTreeNode node, BitcoinPrice start, BitcoinPrice end, List<BitcoinPrice> result) {
        if (node == null) {
            return;
        }

        int i = 0;
        while (i < node.n && node.keys[i].compareTo(start) < 0) {
            i++;
        }

        if (node.isLeaf) {
            while (i < node.n && node.keys[i].compareTo(end) <= 0) {
                result.add(node.keys[i]);
                i++;
            }
        } else {
            while (i < node.n) {
                search_range(node.children[i], start, end, result);
                if (i < node.n && node.keys[i].compareTo(end) <= 0) {
                    result.add(node.keys[i]);
                }
                i++;
            }
            search_range(node.children[i], start, end, result);
        }
    }

    public List<BitcoinPrice> search_range(BitcoinPrice start, BitcoinPrice end) {
        List<BitcoinPrice> result = new ArrayList<>();
        search_range(root, start, end, result);
        return result;
    }

    public void insert(BTreeNode root, BitcoinPrice key) {
        insertNonFull(root, key);
    }

    private void insertNonFull(BTreeNode node, BitcoinPrice key) {
        int i = node.n - 1;

        if (node.isLeaf) {
            BTreeNode leafNode = node;

            while (i >= 0 && leafNode.keys[i].compareTo(key) > 0) {
                leafNode.keys[i + 1] = leafNode.keys[i];
                i--;
            }
            leafNode.keys[i + 1] = key;
            leafNode.n += 1;

        } else {

            while (i >= 0 && node.keys[i].compareTo(key) > 0) {
                i--;
            }
            i++;

            // int medianValue = -1;
            BitcoinPrice medianValue = null;
            if (node.children[i].n >= 2 * t - 1) {
                Stack<BTreeNode> stack = new Stack<>();
                medianValue = splitNode(node.children[i], node.children[i].isLeaf, stack);
                BTreeNode newNode = stack.pop();
                BTreeNode childNode = stack.pop();
                BTreeNode parent = node;

                int j = parent.n - 1;
                while (j >= 0 && parent.keys[j].compareTo(medianValue) > 0) {
                    parent.keys[j + 1] = parent.keys[j];
                    j--;
                }
                j++;
                parent.keys[j] = medianValue;
                parent.n += 1;

                int l = parent.n;
                while (l > j + 1) {
                    parent.children[l] = parent.children[l - 1];
                    l--;
                }
                parent.children[j + 1] = newNode;
                parent.children[j] = childNode;

            }

            insertNonFull(node.children[i], key);

        }
    }

    private BitcoinPrice splitNode(BTreeNode nodeToBeSplit, boolean isLeaf, Stack<BTreeNode> stack) {
        if (nodeToBeSplit.n <= 2 * t - 1)
            return null;

        BTreeNode newNode = new BTreeNode(isLeaf);

        // 0 1 2 3 4 5
        int medianIndex = nodeToBeSplit.n / 2;
        BitcoinPrice medianValue = nodeToBeSplit.keys[nodeToBeSplit.n / 2];
        // int medianValue = nodeToBeSplit.keys[nodeToBeSplit.n / 2];

        // copy keys from original node to balance
        int newIndex = 0;
        for (int i = nodeToBeSplit.n - 1; i >= t; i--) {
            if (i == medianIndex)
                continue;
            newNode.keys[newIndex] = nodeToBeSplit.keys[i];
            newNode.n += 1;
            newIndex++;
            if (newIndex >= t - 1)
                break;
        }
        Arrays.sort(newNode.keys);

        // Now rebalance the pointers to child nodes
        if (!nodeToBeSplit.isLeaf) {
            int childIndex = 0;
            for (int i = medianIndex + 1; i <= nodeToBeSplit.n; i++) {
                newNode.children[childIndex] = nodeToBeSplit.children[i];
                nodeToBeSplit.children[i] = null;
                childIndex++;
                if (childIndex >= t)
                    break;
            }
        }

        // Now remove the copied keys from the original node including the median to be
        // promoted
        for (int i = medianIndex; i < nodeToBeSplit.n - 1; i++) {
            nodeToBeSplit.keys[i] = nodeToBeSplit.keys[i + 1];
        }
        nodeToBeSplit.keys[nodeToBeSplit.n - 1] = null;
        nodeToBeSplit.n -= 1;

        // Now push the new node and the existing child node to the stack
        stack.push(nodeToBeSplit);
        stack.push(newNode);
        return medianValue;
    }

    public void insertList(List<BitcoinPrice> keys) {
        for (BitcoinPrice key : keys) {
            insert_new(key);
        }
    }

    public void insert_new(BitcoinPrice key) {
        BTreeNode r = root;
        if (r.n == 2 * t - 1) {
            BTreeNode s = new BTreeNode(false);
            root = s;
            s.children[0] = r;
            splitNode(s, 0, r);
            insertNonFull_New(s, key);
        } else {
            insertNonFull_New(r, key);
        }
    }

    private void splitNode(BTreeNode parent, int i, BTreeNode fullChild) {
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
                throw new IllegalStateException("Key cannot be null");
            }
            newNode.keys[j] = fullChild.keys[j + t];
            fullChild.keys[j + t] = null;
        }

        if (!fullChild.isLeaf) {
            for (int j = 0; j < t; j++) {
                if (fullChild.children[j + t] == null) {
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
            throw new IllegalStateException("Key cannot be null");
        }
        parent.keys[i] = fullChild.keys[t - 1];
        // Remove the reference of the middle key from the full child
        for (int j = t - 1; j < 2 * t - 2; j++) {
            fullChild.keys[j] = fullChild.keys[j + 1];
        }
        fullChild.keys[t - 1] = null;
        parent.n += 1;
        if (parent.n > 2 * t - 1) {
            throw new IllegalStateException("Parent node is full");
        }
    }

    private void insertNonFull_New(BTreeNode node, BitcoinPrice key) {
        int i = node.n - 1;

        if (node.isLeaf) {
            while (i >= 0 && node.keys[i].compareTo(key) > 0) {
                node.keys[i + 1] = node.keys[i];
                i--;
            }
            node.keys[i + 1] = key;
            node.n += 1;
        } else {
            while (i >= 0 && node.keys[i].compareTo(key) > 0) {
                i--;
            }
            i++;
            if (node.children[i].n == 2 * t - 1) {
                splitNode(node, i, node.children[i]);
                if (node.keys[i].compareTo(key) < 0) {
                    i++;
                }
            }
            insertNonFull_New(node.children[i], key);
        }
    }

}
