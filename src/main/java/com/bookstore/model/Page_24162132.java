package com.bookstore.model;

import java.util.List;

public class Page_24162132<T> {
    private final List<T> items;
    private final int page, pageSize, totalItems;

    public Page_24162132(List<T> items, int page, int pageSize, int totalItems) {
        this.items = items; this.page = page; this.pageSize = pageSize; this.totalItems = totalItems;
    }
    /** Ép số trang vào khoảng hợp lệ [1, totalPages] để ?page=999 hay ?page=-1 không trả trang rỗng. */
    public static int clamp(int page, int totalItems, int size) {
        int pages = Math.max(1, (totalItems + size - 1) / size);
        return Math.min(Math.max(page, 1), pages);
    }
    public List<T> getItems() { return items; }
    public int getPage() { return page; }
    public int getPageSize() { return pageSize; }
    public int getTotalItems() { return totalItems; }
    public int getTotalPages() { return Math.max(1, (totalItems + pageSize - 1) / pageSize); }
}
