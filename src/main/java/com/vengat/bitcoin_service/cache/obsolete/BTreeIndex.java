package com.vengat.bitcoin_service.cache;

import java.time.LocalDate;

public class BTreeIndex {

    class BTreeNode {
        int t; // Minimum degree
        int n; // Number of keys currently stored
        boolean leaf; // Whether it's a leaf node
        LocalDate[] keys; // Array of keys (dates)
        double[] values; // Array of values (prices)
        BTreeNode[] children; // Array of references to child nodes

        public BTreeNode(int t, boolean leaf) {
            this.t = t;
            this.leaf = leaf;
            this.keys = new LocalDate[2 * t - 1];
            this.values = new double[2 * t - 1];
            this.children = new BTreeNode[2 * t];
        }

        // Search for a key in the subtree rooted with this node
        public LocalDate search(LocalDate key) {
            int i = 0;
            while (i < n && key.compareTo(keys[i]) > 0) {
                i++;
            }

            if (i < n && key.compareTo(keys[i]) == 0) {
                return keys[i];
            }

            if (leaf) {
                return null;
            }

            return children[i].search(key);
        }

        // Insert a new key-value pair into the subtree rooted with this node
        public void insertNonFull(LocalDate key, double value) {
            // Initialize index as the rightmost element
            int i = n - 1;

            // If this is a leaf node
            if (leaf) {
                // Find the location of the new key to be inserted
                while (i >= 0 && key.compareTo(keys[i]) < 0) {
                    keys[i + 1] = keys[i];
                    values[i + 1] = values[i];
                    i--;
                }

                // Insert the new key at the appropriate location
                keys[i + 1] = key;
                values[i + 1] = value;
                n++;
            } else {
                // Find the child which is going to have the new key
                while (i >= 0 && key.compareTo(keys[i]) < 0) {
                    i--;
                }

                // If the child is full, split it
                if (children[i + 1].n == 2 * t - 1) {
                    splitChild(i + 1, children[i + 1]);

                    // After the split, the middle key of children[i] goes up and children[i] is
                    // split into two.
                    // The key that is now at i is the middle key of children[i]
                    // Decide which of the two children is going to have the new key
                    if (key.compareTo(keys[i + 1]) > 0) {
                        i++;
                    }
                }

                children[i + 1].insertNonFull(key, value);
            }
        }

        // Split the child node at index i
        public void splitChild(int i, BTreeNode y) {
            // Create a new node which is going to store (t-1) keys of y
            BTreeNode z = new BTreeNode(y.t, y.leaf);
            z.n = t - 1;

            // Copy the last (t-1) keys of y to z
            for (int j = 0; j < t - 1; j++) {
                z.keys[j] = y.keys[j + t];
                z.values[j] = y.values[j + t];
            }

            // Copy the last t children of y to z
            if (!y.leaf) {
                for (int j = 0; j < t; j++) {
                    z.children[j] = y.children[j + t];
                }
            }

            // Reduce the number of keys in y
            y.n = t - 1;

            // Since this node is going to have a new child, create space for the new child
            for (int j = n; j >= i + 1; j--) {
                children[j + 1] = children[j];
            }

            // Link the new child to this node
            children[i + 1] = z;

            // A key of y will move to this node. Find the location of the new key and move
            // all greater keys one space ahead
            for (int j = n - 1; j >= i; j--) {
                keys[j + 1] = keys[j];
                values[j + 1] = values[j];
            }

            // Copy the middle key of y to this node
            keys[i] = y.keys[t - 1];
            values[i] = y.values[t - 1];

            // Increment the number of keys in this node
            n++;
        }

        // Delete a key from the subtree rooted with this node
        public void delete(LocalDate key) {
            int i = 0;
            while (i < n && key.compareTo(keys[i]) > 0) {
                i++;
            }

            if (i < n && key.compareTo(keys[i]) == 0) {
                if (leaf) {
                    removeFromLeaf(i);
                } else {
                    removeFromNonLeaf(i);
                }
            } else {
                if (leaf) {
                    System.out.println("The key " + key + " does not exist in the tree");
                    return;
                }

                boolean flag = i == n;

                if (children[i].n < t) {
                    fill(i);
                }

                if (flag && i > n) {
                    children[i - 1].delete(key);
                } else {
                    children[i].delete(key);
                }
            }
        }

        // Remove the key present at index from the leaf node
        public void removeFromLeaf(int index) {
            for (int i = index + 1; i < n; i++) {
                keys[i - 1] = keys[i];
                values[i - 1] = values[i];
            }

            n--;
        }

        // Remove the key present at index from the non-leaf node
        public void removeFromNonLeaf(int index) {
            LocalDate key = keys[index];

            if (children[index].n >= t) {
                LocalDate pred = getPredecessor(index);
                keys[index] = pred;
                children[index].delete(pred);
            } else if (children[index + 1].n >= t) {
                LocalDate succ = getSuccessor(index);
                keys[index] = succ;
                children[index + 1].delete(succ);
            } else {
                merge(index);
                children[index].delete(key);
            }
        }

        // Get the predecessor of the key present at index
        public LocalDate getPredecessor(int index) {
            BTreeNode cur = children[index];
            while (!cur.leaf) {
                cur = cur.children[cur.n];
            }

            return cur.keys[cur.n - 1];
        }

        // Get the successor of the key present at index
        public LocalDate getSuccessor(int index) {
            BTreeNode cur = children[index + 1];
            while (!cur.leaf) {
                cur = cur.children[0];
            }

            return cur.keys[0];
        }

        // Fill the child node present at index
        public void fill(int index) {
            if (index != 0 && children[index - 1].n >= t) {
                borrowFromPrev(index);
            } else if (index != n && children[index + 1].n >= t) {
                borrowFromNext(index);
            } else {
                if (index != n) {
                    merge(index);
                } else {
                    merge(index - 1);
                }
            }
        }

        // Borrow a key from the child at index-1 and insert it into the child at index

        public void borrowFromPrev(int index) {
            BTreeNode child = children[index];
            BTreeNode sibling = children[index - 1];

            for (int i = child.n - 1; i >= 0; i--) {
                child.keys[i + 1] = child.keys[i];
                child.values[i + 1] = child.values[i];
            }

            if (!child.leaf) {
                for (int i = child.n; i >= 0; i--) {
                    child.children[i + 1] = child.children[i];
                }
            }

            child.keys[0] = keys[index - 1];
            child.values[0] = values[index - 1];

            if (!child.leaf) {
                child.children[0] = sibling.children[sibling.n];
            }

            keys[index - 1] = sibling.keys[sibling.n - 1];

            child.n++;
            sibling.n--;
        }

        // Borrow a key from the child at index+1 and insert it into the child at index
        public void borrowFromNext(int index) {
            BTreeNode child = children[index];
            BTreeNode sibling = children[index + 1];

            child.keys[child.n] = keys[index];
            child.values[child.n] = values[index];

            if (!child.leaf) {
                child.children[child.n + 1] = sibling.children[0];
            }

            keys[index] = sibling.keys[0];

            for (int i = 1; i < sibling.n; i++) {
                sibling.keys[i - 1] = sibling.keys[i];
                sibling.values[i - 1] = sibling.values[i];
            }

            if (!sibling.leaf) {
                for (int i = 1; i <= sibling.n; i++) {
                    sibling.children[i - 1] = sibling.children[i];
                }
            }

            child.n++;
            sibling.n--;
        }

        // Merge the child at index with the child at index+1
        public void merge(int index) {
            BTreeNode child = children[index];
            BTreeNode sibling = children[index + 1];

            child.keys[t - 1] = keys[index];
            child.values[t - 1] = values[index];

            for (int i = 0; i < sibling.n; i++) {
                child.keys[i + t] = sibling.keys[i];
                child.values[i + t] = sibling.values[i];
            }

            if (!child.leaf) {
                for (int i = 0; i <= sibling.n; i++) {
                    child.children[i + t] = sibling.children[i];
                }
            }

            for (int i = index + 1; i < n; i++) {
                keys[i - 1] = keys[i];
                values[i - 1] = values[i];
            }

            for (int i = index + 2; i <= n; i++) {
                children[i - 1] = children[i];
            }

            child.n += sibling.n + 1;
            n--;
        }

        // Print the tree
        public void print() {
            for (int i = 0; i < n; i++) {
                if (!leaf) {
                    children[i].print();
                }
                System.out.print(keys[i] + " ");
            }

            if (!leaf) {
                children[n].print();
            }
        }

        // Constructor, search, insert, delete, splitChild methods
        // (Implementation details omitted for brevity)
    }

    private BTreeNode root;
    private int t;

    public BTreeIndex(int t) {
        this.t = t;
        root = new BTreeNode(t, true);
    }

    public LocalDate search(LocalDate key) {
        return root.search(key);
    }

    public void insert(LocalDate key, double value) {
        BTreeNode r = root;
        if (r.n == 2 * t - 1) {
            BTreeNode s = new BTreeNode(t, false);
            root = s;
            s.children[0] = r;
            s.splitChild(0, r);
            s.insertNonFull(key, value);
        } else {
            r.insertNonFull(key, value);
        }
    }

    public void delete(LocalDate key) {
        root.delete(key);

        if (root.n == 0) {
            if (root.leaf) {
                root = null;
            } else {
                root = root.children[0];
            }
        }
    }

    public void print() {
        if (root != null) {
            root.print();
        }
    }

}
