package com.justorder.backend.performance;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * IMPORTANT: START THE BACKEND SERVER BEFORE RUNNING THIS TEST
 *
 * After running, ContiPerf writes an HTML report to:
 *   ->>>> backend/target/contiperf-report/index.html <<<<-
 */
public class RestaurantSearchPerformanceTest {

    private static final String SEARCH_URL = "http://localhost:8080/api/restaurants/search";
    private static final Duration HTTP_TIMEOUT = Duration.ofSeconds(5);

    private static HttpClient http;

    /** intercepts test methods. When JUnit is about to run a @Test, reads the @PerfTest and @Required and generates report HTML */
    @Rule
    public ContiPerfRule contiPerfRule = new ContiPerfRule();

    /**if I can't even establish a TCP connection in 5 seconds, give up */
    @BeforeClass
    public static void setUpHttpClient() {
        http = HttpClient.newBuilder()
                .connectTimeout(HTTP_TIMEOUT)
                .build();
    }

    /**
     * Hits the search endpoint once and asserts the response is OK.
     */
    private void hitSearchEndpoint() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SEARCH_URL))
                .timeout(HTTP_TIMEOUT)
                .GET()
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("Expected 200 OK from " + SEARCH_URL, 200, response.statusCode());
        assertNotNull("Response body should not be null", response.body());
    }

    /**
     * Passes when:
     *   - no single call takes more than 1500 ms
     *   - average call stays under 200 ms
     *   - the whole batch finishes within 30 seconds
     *   - throughput is at least 20 ops/sec
     */
    @Test
    @PerfTest(invocations = 200, threads = 5)/**40 runs per thread APROX */
    @Required(max = 1500, average = 200, totalTime = 30_000, throughput = 20)/**must finish within 30,000 ms (30 seconds), 20 at least per call */
    public void searchEndpoint_handlesNormalLoad() throws Exception {
        hitSearchEndpoint();
    }

    /**
     * Same workload, but with an unrealistic 1 ms average target.
     * THIS IS MEANT TO FAIL. 
     */
    @Test
    @PerfTest(invocations = 100, threads = 5)/**HTTP call to a real backend (typical: 5-15 ms) */
    @Required(average = 1)
    public void searchEndpoint_failsWhenTargetIsImpossible() throws Exception {
        hitSearchEndpoint();
    }

    /**
     * Executes the search endpoint repeatedly for a specific duration.
     * Passes when throughput is at least 10 ops/sec over a 10 second period.
     */
    @Test
    @PerfTest(duration = 10000, threads = 2)/**keep running for 10,000 ms (10 seconds), as many invocations as you can fit, on 2 threads */
    @Required(throughput = 10)
    public void searchEndpoint_runsForSpecificDuration() throws Exception {
        hitSearchEndpoint();
    }
}