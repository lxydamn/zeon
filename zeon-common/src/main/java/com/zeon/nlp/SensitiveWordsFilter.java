package com.zeon.nlp;

import com.zeon.trie.AhoCorasick;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 敏感词过滤器，基于 Aho-Corasick 算法实现
 * </p>
 *
 * @author lxy2914344878@163.com
 * @since 2025/9/19 17:20
 */
public class SensitiveWordsFilter {
    private final AhoCorasick ahoCorasick;

    public SensitiveWordsFilter(List<String> words) {
        this.ahoCorasick = AhoCorasick.build(words);
    }

    public static SensitiveWordsFilter build(List<String> words) {
        return new SensitiveWordsFilter(words);
    }

    /**
     * 根据起始和结束位置对文本进行掩码处理
     *
     * @param text 原始文本
     * @param start 起始位置
     * @param end 结束位置
     * @return 掩码后的文本
     */
    private String maskByBoundary(String text, int start, int end) {
        if (text == null || text.isBlank()) {
            return text;
        }

        if (start >= 0 && end <= text.length() && start < end) {
            return text.substring(0, start) + "*".repeat(end - start) + text.substring(end);
        } else {
            return text;
        }
    }

    /**
     * 合并重叠或相邻的区间
     *
     * @param list 区间列表
     * @return 合并后的区间列表
     */
    private List<Map.Entry<Integer, Integer>> mergeEdge(List<Map.Entry<Integer, Integer>> list) {
        if (list.isEmpty()) {
            return new ArrayList<>();
        }

        List<Map.Entry<Integer, Integer>> merge = new ArrayList<>();
        int l = list.get(0).getKey();
        int r = list.get(0).getValue();

        for (int i = 1; i < list.size(); i++) {
            Map.Entry<Integer, Integer> entry = list.get(i);
            if (entry.getKey() <= r) {
                r = Math.max(r, entry.getValue());
            } else {
                merge.add(new AbstractMap.SimpleEntry<>(l, r));
                l = entry.getKey();
                r = entry.getValue();
            }
        }

        merge.add(new AbstractMap.SimpleEntry<>(l, r));
        return merge;
    }

    /**
     * 对内容进行敏感词过滤
     *
     * @param content 待过滤的内容
     * @return 过滤后的内容
     */
    public String filter(String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }

        List<Map.Entry<Integer, Integer>> search = this.ahoCorasick.search(content);
        List<Map.Entry<Integer, Integer>> mergedRanges = this.mergeEdge(search);

        // 从后往前替换，避免索引偏移问题
        StringBuilder result = new StringBuilder(content);
        for (int i = mergedRanges.size() - 1; i >= 0; i--) {
            Map.Entry<Integer, Integer> range = mergedRanges.get(i);
            int start = range.getKey();
            int end = range.getValue();
            int length = end - start;
            result.replace(start, end, "*".repeat(length));
        }

        return result.toString();
    }
}
