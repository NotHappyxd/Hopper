package me.happy.license.server;

public class License {

    private String license;
    private String clientName;
    private long addedAt;
    private long expiresAt;

    public License(String license, String clientName, long addedAt, long expiresAt) {
        this.license = license;
        this.clientName = clientName;
        this.addedAt = addedAt;
        this.expiresAt = expiresAt;
    }

    public boolean hasExpired() {
        if (expiresAt == Long.MAX_VALUE) return false;

        return System.currentTimeMillis() - addedAt >= expiresAt;
    }

    public boolean isPermanent() {
        return expiresAt == Long.MAX_VALUE;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public long getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(long addedAt) {
        this.addedAt = addedAt;
    }

    public long getExpiresIn() {
        return expiresAt;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresAt = expiresIn;
    }
}
