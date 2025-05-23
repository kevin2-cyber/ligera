package com.ligera.app.network.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * Response model for pagination information
 */
public class PageInfo {
    @SerializedName("page")
    private int page;
    
    @SerializedName("size")
    private int size;
    
    @SerializedName("totalElements")
    private long totalElements;
    
    @SerializedName("totalPages")
    private int totalPages;
    
    @SerializedName("hasNext")
    private boolean hasNext;
    
    @SerializedName("hasPrevious")
    private boolean hasPrevious;
    
    public PageInfo() {
    }
    
    public int getPage() {
        return page;
    }
    
    public void setPage(int page) {
        this.page = page;
    }
    
    public int getSize() {
        return size;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
    
    public long getTotalElements() {
        return totalElements;
    }
    
    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
    
    public int getTotalPages() {
        return totalPages;
    }
    
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
    
    public boolean isHasNext() {
        return hasNext;
    }
    
    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }
    
    public boolean isHasPrevious() {
        return hasPrevious;
    }
    
    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }
}

