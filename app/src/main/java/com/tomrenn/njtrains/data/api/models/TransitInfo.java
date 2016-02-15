package com.tomrenn.njtrains.data.api.models;

public class TransitInfo {
    long lastUpdated;
    String checksum;
    String url;

    public TransitInfo(long lastUpdated, String checksum, String url) {
        this.lastUpdated = lastUpdated;
        this.checksum = checksum;
        this.url = url;
    }

    public TransitInfo() {
        // for GSON
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public String getChecksum() {
        return checksum;
    }

    public String getUrl() {
        return url;
    }
}
