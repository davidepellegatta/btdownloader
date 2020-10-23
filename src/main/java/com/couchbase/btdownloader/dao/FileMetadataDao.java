package com.couchbase.btdownloader.dao;

import com.couchbase.btdownloader.model.FileMetadata;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.MutationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class FileMetadataDao {

  @Autowired
  @Qualifier("exchangeBucket")
  private Bucket exchangeBucket;


  public MutationResult save(String key, FileMetadata fileMetadata) {
    return exchangeBucket.defaultCollection().upsert(key, fileMetadata);
  }

  public FileMetadata get(String documentId) {

    GetResult result = exchangeBucket.defaultCollection().get(documentId);

    return result.contentAs(FileMetadata.class);
  }


}
