package org.example.nonprofit;

public final class NonprofitAgentService {
    private final ReceiptWorkflow workflow;
    private final InfraiErrorClient errors;

    public NonprofitAgentService(ReceiptWorkflow workflow, InfraiErrorClient errors) {
        this.workflow = workflow;
        this.errors = errors;
    }

    public ReceiptWorkflow.Decision run(String donorId, String campaignId, boolean receiptRecorded,
                                       boolean volunteerConfirmed, boolean campaignClosed) {
        try {
            return workflow.decide(receiptRecorded, volunteerConfirmed, campaignClosed);
        } catch (RuntimeException failure) {
            errors.capture(workflow.exceptionPayload(donorId, campaignId, failure));
            throw failure;
        }
    }
}
