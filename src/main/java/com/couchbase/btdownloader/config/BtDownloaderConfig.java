package com.couchbase.btdownloader.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Data
public class BtDownloaderConfig {

  @Value("${torrent.announceUrl}")
  private String announceUrl;

  @Value("${torrent.downloadPath}")
  private String downloadPath;

  @Value("${torrent.clientHost}")
  private String torrentclientHost;
}

@Component
class ServerPortCustomizer
    implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

  @Override
  public void customize(ConfigurableWebServerFactory factory) {
    factory.setPort(8086);
  }
}