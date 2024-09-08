package org.teamy.backend.model;

public enum fundingApplicationStatus {
    Submitted,
    Reviewed,
    Approved,
    Rejected;
    public static fundingApplicationStatus fromString(String status) {
        switch (status) {
            case "submitted":
                return Submitted;
            case "reviewed":
                return Reviewed;
            case "approved":
                return Approved;
            case "rejected":
                return Rejected;
            default:
                throw new IllegalArgumentException("Unknown status: " + status);
        }
    }
}
