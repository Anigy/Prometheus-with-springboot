package com.springbootexample.springbootprometheus;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.spring.boot.EnablePrometheusEndpoint;
import io.prometheus.client.spring.boot.EnableSpringBootMetricsCollector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController

@EnablePrometheusEndpoint
@EnableSpringBootMetricsCollector

public class SpringBootPrometheusApplication {

    // 只增不减的计数器
    static final Counter requests = Counter.build()
            .name("request_total")
            .help("Total number of request.").register();

    // 统计分布,自带buckets;可以计算比如,访问延迟的分布情况. 与Summary区别在于
    // Histogram在服务器端计算分位数而Summary在的分位数则是直接在客户端进行定义
    static final Histogram requestLatency = Histogram.build()
            .name("request_latency_seconds")
            .help("Request latency in seconds.")
            .register();

    // 可增可减的仪表盘
    static final Gauge inprogressRequests = Gauge.build()
            .name("in_progress_requests")
            .labelNames("methodName")
            .help("In_progress requests.")
            .register();

    @RequestMapping("/")
    String home() {
        requests.inc();
        inprogressRequests.labels("Home page gauge test").inc();

        Histogram.Timer requestTimer = requestLatency.startTimer();
        try{
            return "hello world";
        } finally {
            inprogressRequests.labels("Home page gauge test").dec();
            requestTimer.observeDuration();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootPrometheusApplication.class, args);
    }

}
