package com.couchbase.btdownloader.config;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CouchbaseConfig {

  @Value("${couchbase.host}")
  private String host;

  @Value("${couchbase.username}")
  private String username;

  @Value("${couchbase.password}")
  private String password;

  @Value("${couchbase.bucket.exchange}")
  private String exchangeBucket;


  public @Bean
  Cluster getClusterConnection() {
    return Cluster.connect(host, username, password);
  }

  public @Bean(name= "exchangeBucket")
  Bucket getExchangeBucket() {
    return getClusterConnection().bucket(exchangeBucket);
  }

}