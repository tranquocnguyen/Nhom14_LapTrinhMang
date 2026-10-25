package com.nhom14.filesync.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Bản kế hoạch đồng bộ tổng thể sau khi đối chiếu metadata
 */
public class SyncPlan implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<SyncItem> items = new ArrayList<>();
    private long totalBytesToTransfer = 0;
    private long timestamp = System.currentTimeMillis();

    public SyncPlan() {
    }

    public void addItem(SyncItem item) {
        items.add(item);
        if (item.getAction() == SyncAction.UPLOAD && item.getClientMeta() != null) {
            totalBytesToTransfer += item.getClientMeta().getFileSize();
        } else if (item.getAction() == SyncAction.DOWNLOAD && item.getServerMeta() != null) {
            totalBytesToTransfer += item.getServerMeta().getFileSize();
        } else if (item.getAction() == SyncAction.CONFLICT && item.getServerMeta() != null) {
            totalBytesToTransfer += item.getServerMeta().getFileSize();
        }
    }

    public List<SyncItem> getItems() {
        return items;
    }

    public void setItems(List<SyncItem> items) {
        this.items = items;
    }

    public long getTotalBytesToTransfer() {
        return totalBytesToTransfer;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getActionCount(SyncAction action) {
        int count = 0;
        for (SyncItem item : items) {
            if (item.getAction() == action) {
                count++;
            }
        }
        return count;
    }

    public boolean hasActionableItems() {
        for (SyncItem item : items) {
            if (item.getAction() != SyncAction.UP_TO_DATE) {
                return true;
            }
        }
        return false;
    }
}