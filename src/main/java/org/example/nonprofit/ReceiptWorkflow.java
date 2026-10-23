package org.example.nonprofit;

public final class ReceiptWorkflow {
    public enum Decision { SEND_RECEIPT, REMIND_VOLUNTEER, REPORT_CAMPAIGN }

    public Decision decide(boolean receiptRecorded, boolean volunteerConfirmed, boolean campaignClosed) {
        if (!receiptRecorded) return Decision.SEND_RECEIPT;
        if (!volunteerConfirmed) return Decision.REMIND_VOLUNTEER;
        return Decision.REPORT_CAMPAIGN;
    }

    public String exceptionPayload(String donorId, String campaignId, RuntimeException failure) {
        String message = failure.getMessage() == null ? failure.getClass().getSimpleName() : failure.getMessage();
        return "{\"exception\":{\"type\":\"" + escape(failure.getClass().getName())
                + "\",\"message\":\"" + escape(message) + "\"}}";
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
