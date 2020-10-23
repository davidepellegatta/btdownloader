package com.couchbase.btdownloader.api;

import com.couchbase.btdownloader.model.FileMetadata;
import com.couchbase.btdownloader.model.FileMetadataSubmitResponse;
import com.couchbase.btdownloader.service.FileMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/torrent")
public class FileExchangeApi {

  @Autowired
  private FileMetadataService fileMetadataService;

  @PostMapping("/file/{businessUnit}")
  public FileMetadataSubmitResponse submitFile(@PathVariable("businessUnit") String businessUnit,
                                               @RequestBody FileMetadata fileMetadata) throws IOException {

    return fileMetadataService.prepareTransfer(businessUnit, fileMetadata);
  }

  @PostMapping("/torrent/{documentId}")
  public FileMetadata submitFile(@PathVariable("documentId") String documentId) {

    return fileMetadataService.startTransfer(documentId);
  }

}
