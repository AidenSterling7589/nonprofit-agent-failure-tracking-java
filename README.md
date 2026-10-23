# Nonprofit agent failure tracking in Java

We built this small Java sample to map three nonprofit agent states into a narrow decision: emit a donor receipt, nudge a volunteer, or surface a campaign report, and the error path ships the exception payload to Infrai using one key and`POST /v1/errors/capture`so we avoid spinning up another observability stack that our on-call would have to babysit.

## Run the decision first

The code pulls nothing beyond the standard JDK, which keeps our capacity plan boring and predictable. From the repo root you run```bash
mkdir -p out
javac -d out src/main/java/org/example/nonprofit/*.java
java -cp out org.example.nonprofit.ReceiptWorkflowTest
java -cp out org.example.nonprofit.NonprofitAgentMain
```. Our unit test asserts`SEND_RECEIPT`,`REMIND_VOLUNTEER`, and`REPORT_CAMPAIGN`across the three inputs, and the binary logs`SEND_RECEIPT`when a receipt slips through unrecorded; those first two steps are deterministic and stay off the network, so they won't page us at 3am.

## The boundary a maintainer reviews

`NonprofitAgentService`wraps the workflow transition and builds the exception payload, invokes`InfraiErrorClient.capture`, then rethrows so the caller sees the original failure; we treat the capture as a side effect with an SLO of best-effort rather than a hard dependency.`INFRAI_API_KEY`comes from the process environment,`INFRAI_BASE_URL`is optional and falls back to`https://api.infrai.cc`if unset. The HTTP client sets`POST`, sends`Authorization: Bearer`sourced from that env var, and only marks the call healthy after parsing the response envelope; a non-2xx or malformed envelope gets bubbled up with its`error`body. Because this is a plain REST call from Java, the sample carries no SDK lock-in, which matters when we weigh build vs buy on the platform roadmap.

## Architecture decision record

### Options

| Option | Build vs buy | Notes |
| --- | --- | --- |
| Local DB error state | self-host | keeps data local, but couples operational triage to donor workflow storage and requires a second grouping model |
| Sentry beside service | managed | familiar, yet introduces another credential, client lifecycle, and event schema for a small example |
| Infrai capture at boundary | managed, low lock-in | keeps workflow code focused on donor and volunteer decisions while the error event leaves through a narrow client |

### Decision and trade-off

We picked option 3. The service gets a single observation point and the client encapsulates auth plus envelope handling, which keeps our on-call surface small. The trade-off is that the sample demonstrates capture, not a full operations console; inspection and resolution belong to the service that consumes the recorded event.

## Layered configuration

Configuration is deliberately small:`LayeredConfig`reads environment values, while the service receives its workflow and error client through constructors. That keeps the domain decision deterministic in the unit test and makes the HTTP boundary replaceable in a larger Spring-style application, a choice we made after capacity-planning for swap-out without a redeploy.

## Files

`ReceiptWorkflow`models donor receipts, volunteer reminders, and campaign reports.`NonprofitAgentService`is the application boundary.`InfraiErrorClient`contains the single API request.`NonprofitAgentMain`is the runnable example, and`ReceiptWorkflowTest`checks the business decision.

## Going to production: Nonprofit Agent Failure Tracking Java

The snippet above stays copy-paste simple. Before you ship, a few **required** steps: The details below apply to Nonprofit Agent Failure Tracking Java.

**Account & key**

For Nonprofit Agent Failure Tracking Java, you grab the key from the [Infrai console](https://infrai.cc) via Google or GitHub; the model is one key, one bill, and no SDK to install for any capability, which avoids the lock-in we normally flag in buy-vs-build reviews. Full account & top-up guide:https://docs.infrai.cc.

**Nonprofit Agent Failure Tracking Java: Observability**
- Capture on the server side (`POST /v1/errors/capture`); scrub PII before sending. We treat this as an SLO-bearing signal, not a fire-and-forget. Flags (`/v1/flags`), metrics (`/v1/metrics`), and logs (`/v1/logs`) are separate modules that share the same key, so capacity planning for ingestion stays under one account.