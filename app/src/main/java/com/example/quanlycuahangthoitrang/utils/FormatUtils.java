package com.example.quanlycuahangthoitrang.utils;

public class FormatUtils {
    public static String formatPrice(int price) {
        return String.format("%,dđ", price).replace(',', '.');
    }

    public static String removeVietnameseAccents(String str) {
        if (str == null) return "";
        try {
            String temp = java.text.Normalizer.normalize(str, java.text.Normalizer.Form.NFD);
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("").replace('đ', 'd').replace('Đ', 'D');
        } catch (Exception e) {
            return str;
        }
    }

    public static String normalizeSearchText(String input) {
        if (input == null) return "";
        return removeVietnameseAccents(input).toLowerCase().trim().replaceAll("\\s+", " ");
    }

    public static boolean matchesSearch(String query, String... fields) {
        if (query == null || query.trim().isEmpty()) return true;
        
        String normalizedQuery = normalizeSearchText(query);
        String[] queryTokens = normalizedQuery.split(" ");

        for (String field : fields) {
            if (field == null) continue;
            String normalizedField = normalizeSearchText(field);
            
            if (normalizedField.equals(normalizedQuery)) return true;
            if (normalizedField.startsWith(normalizedQuery)) return true;

            String[] fieldTokens = normalizedField.split(" ");
            boolean allTokensMatched = true;

            for (String qt : queryTokens) {
                boolean tokenMatched = false;
                for (String ft : fieldTokens) {
                    if (ft.startsWith(qt)) {
                        tokenMatched = true;
                        break;
                    }
                }
                if (!tokenMatched) {
                    allTokensMatched = false;
                    break;
                }
            }
            if (allTokensMatched) return true;
        }
        return false;
    }
}
