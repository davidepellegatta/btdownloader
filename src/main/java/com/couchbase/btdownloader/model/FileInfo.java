package com.couchbase.btdownloader.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileInfo {

  private String name;
  private String url;

}
