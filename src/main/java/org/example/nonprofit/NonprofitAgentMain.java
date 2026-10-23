package org.example.nonprofit;

public final class NonprofitAgentMain {
    private NonprofitAgentMain() {}

    public static void main(String[] args) {
        LayeredConfig config = LayeredConfig.fromEnvironment();
        NonprofitAgentService service = new NonprofitAgentService(
                new ReceiptWorkflow(), new InfraiErrorClient(config));
        ReceiptWorkflow.Decision decision = service.run("donor-17", "spring-drive",
                false, false, false);
        System.out.println(decision);
    }
}
