package de.turing85.camel.attachment.bug;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;

import org.apache.camel.attachment.AttachmentMessage;
import org.apache.camel.builder.RouteBuilder;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.timer;

@SuppressWarnings("unused")
public class MyTimerRoute extends RouteBuilder {
  @Override
  public void configure() {
    // @formatter:off
    from(
        timer("my-timer")
            .fixedRate(true)
            .period(Duration.ofSeconds(1).toMillis()))
        .process(exchange -> {
          AttachmentMessage message = exchange.getMessage(AttachmentMessage.class);
          Path filePath = Files.createTempFile("attachment", "tmp");
          message.addAttachment("foo", new DataHandler(new FileDataSource(filePath.toFile())));
          message.removeAttachment("foo");
          boolean hasAttachments = message.hasAttachments();
          exchange.setProperty("hasAttachments", hasAttachments);
          exchange.setProperty("numAttachments", message.getAttachments().size());
        })
        .log("hasAttachment: ${exchangeProperty.hasAttachments}")
        .log("# attachments: ${exchangeProperty.numAttachments}");
    // @formatter:on
  }
}
