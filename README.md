# Realtime-Limiter
A guide to setting up a rate limiter server and configuring and rate limiting customers. For a more
detailed explanation of how it works consult the [design document](https://docs.google.com/a/twitter.com/document/d/1v1hS9_31ufuHcQ-W6V7dxH9WZ0twLEpSvzgTISY67N4/edit?usp=sharing).

- Ashwin Madavan @amadavan
- Boaz Avital @boaz

## Deploy

1. Modify the system settings in ```deploy.sh```
    - Select a unique artifact
    - Select an aurora cluster
    - Select an environment
2. Run ```realtime-limiter/deploy.sh```

## Tooling
The rate limiter server requires that customers be properly provisioned in ZooKeeper before it can
start rate limiting them. The purpose of the ```./dist/limiter.pex``` file is to simplify the
process of creating and updating customer configurations in ZooKeeper.

    PKEY_READ("eventual pkey reads"), LKEY_READ("eventual lkey reads"), WRITE("eventual writes"),
    STRONG_PKEY_READ("strong pkey reads"), STRONG_LKEY_READ("strong lkey reads"),
    STRONG_WRITE("strong writes"), STRONG_MIX("strong CAS or increments"),
    /**
     * the number of requests coordinators publish to the distributed log, which is a total count
     * of all the strong operations.
     */
    RLOG_PUBLISH("rlog publishes");
```
# Retrieves the customer configuration
./limiter.pex fetch /customer/id

# Lists all children of the specified customer
./limiter.pex ls /customer

# Removes the customer and all its children
./limiter.pex rm /customer/id

# Creates or modifies the configuration of the specified customer
./limiter.pex config /customer -q 1000 -b 100 -p strict

# Performs a load test.
./limiter.pex trafficker -i 10 -n 100 -f 100
```

For further information about configuration options and their implications, please consult the
design document linked in the project overview.

## Client
The rate limiter client is relatively straight-forward to include. Simply add a dependency on
```realtime-limiter/limiter-core```, construct a rate limiter, and start rate limiting!

```
final RateLimiterClient client = new RateLimiterClient.Builder("test")
            .withArtifact("mhlimiter-adar")
            .withCluster("atla")
            .withEnvironment("devel")
            .withReportInterval(Duration.ofMillis(100))
            .withLimitingExpiration(Duration.ofSeconds(1))
            .withCustomerExpiration(Duration.ofSeconds(30))
            .build();

client.verify('/test/application', 1);
client.verify('/test/application', 10);
```

## Metrics
- default(0, ts(avg, ${ROLE}.${ENVIRONMENT}.${ARTIFACT}, members($source), <customerId>/usage.sum)) / 60: Recorded requests.
- default(0, ts(avg, ${ROLE}.${ENVIRONMENT}.${ARTIFACT}, members($source), <customerId>/rate.avg)) / 60: Estimated rate.
- default(0, ts(avg, ${ROLE}.${ENVIRONMENT}.${ARTIFACT}, members($source), <customerId>/limit.avg)) / 60: Average rejection rate.
- default(0, ts(avg, ${ROLE}.${ENVIRONMENT}.${ARTIFACT}, members($source), publish/size.avg)) / 60: Average number of reports.
- default(0, ts(avg, ${ROLE}.${ENVIRONMENT}.${ARTIFACT}, members($source), publish/latency.avg)) / 60: Average report processing latency.



## Benchmarks
All benchmarks are wrong, but some benchmarks are useful.

```
./pants binary realtime-limiter/limiter-perf
java -jar ./dist/benchmarks.jar -rf csv
```
Alternatively, consider running benchmarks in IntelliJ using the
[JMH Plugin](https://github.com/artyushov/idea-jmh-plugin). It allows benchmarks to be run
directly from the IDE using a similar interface as JUnit/TestNG unit tests.
