package com.couchbase.btdownloader.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileMetadata {

  private String filename;
  private String fsPath;
  private String fileType;
  private List<String> destinations;
  private String documentTitle;
  private String documentAbstract;
  private String torrentFile;
}
