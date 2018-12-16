package com.github.mayaming.demo_repo.hbase.comments;

import java.util.*;

public class Utils {
    private static final Random r = new Random();
    private static final int NAME_LEN = 6;

    public static ArrayList<String> generateNames(int n) {
        Set<String> names = new HashSet<>();
        char[] chs = new char[10];
        while (names.size() < n) {
            for (int i = 0; i < NAME_LEN; ++i) {
                chs[i] = (char)('a' + r.nextInt(26));
            }
            names.add(new String(chs, 0, NAME_LEN));
        }
        return new ArrayList<>(names);
    }

    public static String generateComment(int n) {
        StringBuffer sb = new StringBuffer();
        while (n-- > 0) {
            sb.append(UUID.randomUUID().toString());
        }
        return sb.toString();
    }

    public static void generateComments(int n, Queue<Comment> q, ArrayList<String> names) {
        int namesLen = names.size();
        while (n-- > 0) {
            String name = names.get(r.nextInt(namesLen));
            String comment = generateComment(r.nextInt(16));
            q.offer(new Comment(name, comment));
        }
    }
}
