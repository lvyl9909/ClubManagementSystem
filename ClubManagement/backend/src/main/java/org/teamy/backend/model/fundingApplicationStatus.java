package org.teamy.backend.model;

public enum fundingApplicationStatus {
    submitted,
    reviewed,
    approved,
    rejected;
    public static fundingApplicationStatus fromString(String status) {
        switch (status) {
            case "submitted":
                return submitted;
            case "reviewed":
                return reviewed;
            case "approved":
                return approved;
            case "rejected":
                return rejected;
            default:
                throw new IllegalArgumentException("Unknown status: " + status);
        }
    }
}
