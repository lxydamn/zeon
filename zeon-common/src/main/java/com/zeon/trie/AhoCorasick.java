package com.zeon.trie;

import java.util.*;

/**
 * <p>
 * Aho-Corasick 字符串匹配算法实现，用于多模式字符串匹配
 * </p>
 *
 * @author lxy2914344878@163.com
 * @since 2025/9/19 17:20
 */
public class AhoCorasick {
    private final Node root = new Node(0);
    private final List<String> words = new ArrayList<>();

    /**
     * 构建 Aho-Corasick 自动机
     *
     * @param words 模式串数组
     * @return 构建好的 Aho-Corasick 自动机实例
     */
    public static AhoCorasick build(String... words) {
        return build(Arrays.asList(words));
    }

    /**
     * 构建 Aho-Corasick 自动机
     *
     * @param words 模式串集合
     * @return 构建好的 Aho-Corasick 自动机实例
     */
    public static AhoCorasick build(Collection<String> words) {
        AhoCorasick ahoCorasick = new AhoCorasick();

        // 构建 Trie 树
        for (String word : words) {
            Node cur = ahoCorasick.root;

            for (int i = 0; i < word.length(); ++i) {
                char c = word.charAt(i);
                if (!cur.containsKey(c)) {
                    Node node = new Node(cur.depth + 1);
                    cur.put(c, node);
                }

                cur = cur.get(c);
            }

            cur.isEnd = true;
            ahoCorasick.words.add(word);
            cur.outputs.add(ahoCorasick.words.size() - 1);
        }

        // 构建失败指针
        buildFailPointer(ahoCorasick.root);
        return ahoCorasick;
    }

    /**
     * 构建失败指针
     *
     * @param root Trie 树根节点
     */
    private static void buildFailPointer(Node root) {
        Queue<Node> queue = new LinkedList<>();

        // 初始化第一层节点的失败指针指向根节点
        for (Map.Entry<Character, Node> entry : root.children.entrySet()) {
            entry.getValue().fail = root;
            queue.offer(entry.getValue());
        }

        // BFS 遍历构建失败指针
        while (!queue.isEmpty()) {
            Node cur = queue.poll();

            for (Map.Entry<Character, Node> entry : cur.children.entrySet()) {
                Node child = entry.getValue();
                char ch = entry.getKey();

                // 查找失败指针
                Node fail = cur.fail;
                while (fail != null && !fail.containsKey(ch)) {
                    fail = fail.fail;
                }

                // 设置失败指针
                if (fail == null) {
                    child.fail = root;
                } else {
                    child.fail = fail.get(ch);
                }

                // 合并输出集合
                if (child.fail != null) {
                    child.outputs.addAll(child.fail.outputs);
                }

                queue.offer(child);
            }
        }
    }

    /**
     * 在文本中搜索所有匹配的模式串
     *
     * @param text 待搜索的文本
     * @return 匹配结果列表，每个元素为<起始位置, 结束位置>的映射
     */
    public List<Map.Entry<Integer, Integer>> search(String text) {
        List<Map.Entry<Integer, Integer>> result = new ArrayList<>();
        Node cur = this.root;

        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);

            // 根据失败指针跳转直到找到匹配或回到根节点
            while (cur != this.root && !cur.containsKey(c)) {
                cur = cur.fail;
            }

            if (cur.containsKey(c)) {
                cur = cur.get(c);
            }

            // 收集所有匹配的结果
            for (int pid : cur.outputs) {
                String word = this.words.get(pid);
                int start = i - word.length() + 1;
                result.add(new AbstractMap.SimpleEntry<>(start, i + 1));
            }
        }

        return result;
    }

    /**
     * Trie 树节点类
     */
    private static class Node {
        public Map<Character, Node> children = new HashMap<>();
        public boolean isEnd = false;
        public Node fail = null;
        public int depth;
        public List<Integer> outputs = new ArrayList<>();

        public Node(int depth) {
            this.depth = depth;
        }

        public Node get(char c) {
            return this.children.get(c);
        }

        public void put(char c, Node node) {
            this.children.put(c, node);
        }

        public void remove(char c) {
            this.children.remove(c);
        }

        public boolean containsKey(char c) {
            return this.children.containsKey(c);
        }
    }
}
