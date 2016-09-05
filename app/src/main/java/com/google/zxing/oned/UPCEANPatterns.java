package com.google.zxing.oned;

public class UPCEANPatterns {
    static final int[] START_END_PATTERN = {1, 1, 1,};
    static final int[] MIDDLE_PATTERN = {1, 1, 1, 1, 1};
    static final int[] END_PATTERN = {1, 1, 1, 1, 1, 1};
    static final int[][] L_PATTERNS = {
            {3, 2, 1, 1}, // 0
            {2, 2, 2, 1}, // 1
            {2, 1, 2, 2}, // 2
            {1, 4, 1, 1}, // 3
            {1, 1, 3, 2}, // 4
            {1, 2, 3, 1}, // 5
            {1, 1, 1, 4}, // 6
            {1, 3, 1, 2}, // 7
            {1, 2, 1, 3}, // 8
            {3, 1, 1, 2}  // 9
    };
    static final int[][] L_AND_G_PATTERNS;

    static {
        L_AND_G_PATTERNS = new int[20][];
        System.arraycopy(L_PATTERNS, 0, L_AND_G_PATTERNS, 0, 10);
        for (int i = 10; i < 20; i++) {
            int[] widths = L_PATTERNS[i - 10];
            int[] reversedWidths = new int[widths.length];
            for (int j = 0; j < widths.length; j++) {
                reversedWidths[j] = widths[widths.length - j - 1];
            }
            L_AND_G_PATTERNS[i] = reversedWidths;
        }
    }
}
