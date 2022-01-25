package com.natwest.integration;

import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.time.Duration;

@ActiveProfiles("test")
class DockerContainer {

  static {
    new DockerComposeContainer(new File("src/test/resources/docker-compose-test.yml"))
        .withExposedService(
            "kafka", 9092, Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)))
        .withExposedService(
            "mariadb", 3306, Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)))
        .withExposedService(
            "zookeeper", 2181, Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)))
        .waitingFor("mariadb", new HostPortWaitStrategy())
        .waitingFor("kafka", new HostPortWaitStrategy())
        .waitingFor("zookeeper", new HostPortWaitStrategy())
        .withLocalCompose(true)
        .start();
  }
}
