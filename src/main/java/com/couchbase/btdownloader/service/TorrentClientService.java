package com.couchbase.btdownloader.service;

import com.couchbase.btdownloader.config.BtDownloaderConfig;
import com.couchbase.btdownloader.model.FileMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class TorrentClientService {

  @Autowired
  private BtDownloaderConfig btDownloaderConfig;

  public void addTorrent(FileMetadata fileMetadata, byte[] torrentToEncode) {

    RestTemplate restTemplate = new RestTemplate();

    //first authenticate
    HttpHeaders headersLogin = new HttpHeaders();
    headersLogin.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headersLogin.set("Referer", btDownloaderConfig.getTorrentclientHost());

    ///api/v2/auth/login

    String loginData = "username=admin&password=adminadmin";

    HttpEntity<String> entity = new HttpEntity<>(loginData, headersLogin);

    HttpEntity<String> responseLogin = restTemplate.exchange(
        btDownloaderConfig.getTorrentclientHost() + "/api/v2/auth/login",
        HttpMethod.POST,
        entity,
        String.class);

    List<String> auth = responseLogin.getHeaders().get("set-cookie");

    String authCookie = auth.get(0).substring(0, auth.get(0).indexOf(";"));

    //Send the requestyui

    HttpHeaders headersUpload = new HttpHeaders();
    headersUpload.setContentType(MediaType.MULTIPART_FORM_DATA);
    headersUpload.add("Cookie", authCookie );


    // This nested HttpEntiy is important to create the correct
    // Content-Disposition entry with metadata "name" and "filename"
    MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
    ContentDisposition contentDisposition = ContentDisposition
        .builder("application/x-bittorrent")
        .name("file")
        .filename(fileMetadata.getFilename() + ".torrent")
        .build();
    fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
    HttpEntity<byte[]> fileEntity = new HttpEntity<>(torrentToEncode, fileMap);

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", fileEntity);

    HttpEntity<MultiValueMap<String, Object>> requestEntity =
        new HttpEntity<>(body, headersUpload);

    ResponseEntity<String> response = restTemplate.exchange(
        btDownloaderConfig.getTorrentclientHost() + "/api/v2/torrents/add",
        HttpMethod.POST,
        requestEntity,
        String.class);
  }
}
