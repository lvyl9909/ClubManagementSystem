package org.teamy.backend.model;

public enum fundingApplicationStatus {
    Submitted,
    Reviewed,
    Approved,
    Cancelled,
    Rejected;

    public static fundingApplicationStatus fromString(String status) {
        switch (status) {
            case "Submitted":
                return Submitted;
            case "Reviewed":
                return Reviewed;
            case "Approved":
                return Approved;
            case "Rejected":
                return Rejected;
            case "Cancelled":
                return Cancelled;
            default:
                throw new IllegalArgumentException("Unknown status: " + status);
        }
    }
}
