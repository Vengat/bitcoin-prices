package com.vengat.bitcoin_service.cache.obsolete;

import java.util.Arrays;
import java.util.Stack;

public class BTree {
    private BTreeNode root;
    private int t;

    public class BTreeNode {
        int[] keys;
        int n;
        BTreeNode[] children;
        boolean isLeaf;

        public BTreeNode(boolean isLeaf) {
            this.isLeaf = isLeaf;
            keys = new int[(2 * t - 1)];
            children = new BTreeNode[(2 * t)];
            n = 0;
        }
    }

    public BTreeNode search(BTreeNode node, int k) {

        if (node == null) {
            return null;
        }

        int i = node.n - 1;
        while (i >= 0 && node.keys[i] > k) {
            i--;
        }

        if (i >= 0 && node.keys[i] == k) {
            return node;
        }

        if (node.isLeaf) {
            return null;
        } else {
            return search(node.children[i + 1], k);
        }

    }

    public void insert(BTreeNode root, int key) {
        insertNonFull(root, key);
    }

    private void insertNonFull(BTreeNode node, int key) {
        int i = node.n - 1;

        if (node.isLeaf) {
            BTreeNode leafNode = node;
            while (i >= 0 && leafNode.keys[i] > key) {
                leafNode.keys[i + 1] = leafNode.keys[i];
                i--;
            }
            leafNode.keys[i + 1] = key;
            leafNode.n += 1;

        } else {

            while (i >= 0 && node.keys[i] > key) {
                i--;
            }
            i++;

            int medianValue = -1;
            if (node.children[i].n >= 2 * t - 1) {
                Stack<BTreeNode> stack = new Stack<>();
                medianValue = splitNode(node.children[i], node.children[i].isLeaf, stack);
                BTreeNode newNode = stack.pop();
                BTreeNode childNode = stack.pop();
                BTreeNode parent = node;

                int j = parent.n - 1;
                while (j >= 0 && parent.keys[j] > medianValue) {
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

    private int splitNode(BTreeNode nodeToBeSplit, boolean isLeaf, Stack<BTreeNode> stack) {
        if (nodeToBeSplit.n <= 2 * t - 1)
            return -1;

        BTreeNode newNode = new BTreeNode(isLeaf);

        // 0 1 2 3 4 5
        int medianIndex = nodeToBeSplit.n / 2;
        int medianValue = nodeToBeSplit.keys[nodeToBeSplit.n / 2];

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
        nodeToBeSplit.keys[nodeToBeSplit.n - 1] = 0;
        nodeToBeSplit.n -= 1;

        // Now push the new node and the existing child node to the stack
        stack.push(nodeToBeSplit);
        stack.push(newNode);
        return medianValue;
    }

    // public void delete(BTreeNode root, int key) {
    // Stack<BTreeNode> stack = new Stack<>();
    // stack.push(root);
    // deleteNonEmpty(root, key, stack);
    // }

    // private void deleteNonEmpty(BTreeNode node, int key, Stack<BTreeNode> stack)
    // {
    // int i = node.n - 1;

    // if (node.isLeaf) {
    // while (i >= 0 && node.keys[i] > key) {
    // i--;
    // }

    // if (i >= 0 && node.keys[i] == key) {
    // node.keys[i] = 0;
    // for (int j = i; j < node.n - 1; j++) {
    // node.keys[j] = node.keys[j + 1];
    // }
    // node.n--;
    // }
    // } else {

    // while (i >= 0 && node.keys[i] > key) {
    // i--;
    // }

    // if (i >= 0 && node.keys[i] == key) {
    // if (node.children[i] != null && node.children[i + 1] != null) {
    // node.keys[i] = getSuccessor(node.children[i], node.children[i + 1], key);
    // }

    // }
    // }
    // }

    // private int[] getPredecessor(BTreeNode leftChildNode, int key) {
    // int[] res = null;
    // }

    // private int[] getSuccessor(BTreeNode rightChildNode, int key) {
    // int[] res = null;
    // res[0] = -1;
    // if (rightChildNode == null)
    // return res;

    // if (rightChildNode != null) {
    // if (rightChildNode.n >= t) {
    // res[0] = rightChildNode.keys[0];
    // for (int i = 0; i < rightChildNode.n - 1; i++) {
    // rightChildNode.keys[i] = rightChildNode.keys[i + 1];
    // }
    // rightChildNode.keys[rightChildNode.n - 1] = 0;
    // rightChildNode.n--;
    // }
    // }
    // return res;
    // }

}
