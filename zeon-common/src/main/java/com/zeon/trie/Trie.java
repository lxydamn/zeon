package com.zeon.trie;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Trie {
    private TrieNode root = new TrieNode();
    private long count;

    private void put(String word) {
        if (word != null && !word.isEmpty()) {
            TrieNode node = this.root;

            for (char c : word.toCharArray()) {
                if (!node.contains(c)) {
                    node.put(c);
                }

                node = node.get(c);
            }

            if (!node.word) {
                node.word = true;
                ++this.count;
            }

        }
    }

    public void put(String... words) {
        for (String word : words) {
            this.put(word);
        }

    }

    public void put(Collection<String> words) {
        if (words != null && !words.isEmpty()) {
            for (String word : words) {
                this.put(word);
            }

        }
    }

    public boolean contains(String word) {
        TrieNode node = this.root;

        for (char c : word.toCharArray()) {
            if (!node.contains(c)) {
                return false;
            }

            node = node.get(c);
        }

        return node != null && node.word;
    }

    public boolean startsWith(String prefix) {
        TrieNode node = this.root;

        for (char c : prefix.toCharArray()) {
            if (!node.contains(c)) {
                return false;
            }

            node = node.get(c);
        }

        return node != null;
    }

    public boolean remove(String word) {
        boolean removed = this.remove(this.root, word, 0);
        if (removed) {
            --this.count;
        }

        return removed;
    }

    private boolean remove(TrieNode node, String word, int index) {
        if (index == word.length()) {
            if (!node.word) {
                return false;
            } else {
                node.word = false;
                return node.children.isEmpty();
            }
        } else {
            char c = word.charAt(index);
            if (!node.contains(c)) {
                return false;
            } else {
                TrieNode child = node.get(c);
                boolean shouldRemoveChild = this.remove(child, word, index + 1);
                if (!shouldRemoveChild) {
                    return false;
                } else {
                    node.remove(c);
                    return !node.word && node.children.isEmpty();
                }
            }
        }
    }

    public TrieNode getRoot() {
        return this.root;
    }

    public long getCount() {
        return this.count;
    }

    public void setRoot(TrieNode root) {
        this.root = root;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Trie)) {
            return false;
        } else {
            Trie other = (Trie) o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getCount() != other.getCount()) {
                return false;
            } else {
                Object this$root = this.getRoot();
                Object other$root = other.getRoot();
                if (this$root == null) {
                    if (other$root != null) {
                        return false;
                    }
                } else if (!this$root.equals(other$root)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof Trie;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $count = this.getCount();
        result = result * 59 + (int) ($count >>> 32 ^ $count);
        Object $root = this.getRoot();
        result = result * 59 + ($root == null ? 43 : $root.hashCode());
        return result;
    }

    public String toString() {
        String var10000 = String.valueOf(this.getRoot());
        return "Trie(root=" + var10000 + ", count=" + this.getCount() + ")";
    }

    private static class TrieNode {
        public Character val;
        public Map<Character, TrieNode> children;
        public boolean word;

        public TrieNode() {
            this.children = new HashMap();
            this.word = false;
        }

        public TrieNode(char c) {
            this.val = c;
            this.children = new HashMap();
            this.word = false;
        }

        public TrieNode get(char c) {
            return this.children == null ? null : (TrieNode) this.children.get(c);
        }

        public boolean contains(char c) {
            return this.children != null && this.children.containsKey(c);
        }

        public void remove(char c) {
            if (this.children != null) {
                this.children.remove(c);
            }

        }

        public void put(char c) {
            if (this.children == null) {
                this.children = new HashMap();
            }

            this.children.put(c, new TrieNode(c));
        }
    }
}
