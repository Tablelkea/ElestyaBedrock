package org.elestya.bedrock.interfaces;

import java.util.List;

public final class FormPages {

    public static final int PAGE_SIZE = 25;

    private FormPages() {
    }

    public static <T> Page<T> of(List<T> values, int requestedPage) {
        int pageCount = Math.max(1, (values.size() + PAGE_SIZE - 1) / PAGE_SIZE);
        int page = Math.max(0, Math.min(requestedPage, pageCount - 1));
        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, values.size());
        return new Page<>(List.copyOf(values.subList(start, end)), page, pageCount);
    }

    public record Page<T>(List<T> values, int number, int count) {

        public boolean hasPrevious() {
            return number > 0;
        }

        public boolean hasNext() {
            return number + 1 < count;
        }
    }
}
