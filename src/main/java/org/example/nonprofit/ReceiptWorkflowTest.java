package org.example.nonprofit;

public final class ReceiptWorkflowTest {
    public static void main(String[] args) {
        ReceiptWorkflow workflow = new ReceiptWorkflow();
        expect(ReceiptWorkflow.Decision.SEND_RECEIPT, workflow.decide(false, false, false));
        expect(ReceiptWorkflow.Decision.REMIND_VOLUNTEER, workflow.decide(true, false, false));
        expect(ReceiptWorkflow.Decision.REPORT_CAMPAIGN, workflow.decide(true, true, true));
        System.out.println("ReceiptWorkflowTest: PASS");
    }

    private static void expect(ReceiptWorkflow.Decision expected, ReceiptWorkflow.Decision actual) {
        if (expected != actual) throw new AssertionError("expected " + expected + " but got " + actual);
    }
}
