package com.couchbase.btdownloader.service;

import static com.couchbase.btdownloader.constants.KeysPrefixes.*;

import com.couchbase.btdownloader.config.BtDownloaderConfig;
import com.couchbase.btdownloader.dao.FileMetadataDao;
import com.couchbase.btdownloader.model.FileMetadata;
import com.couchbase.btdownloader.model.FileMetadataSubmitResponse;
import com.couchbase.client.java.kv.MutationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

@Service
public class FileMetadataService {

  @Autowired
  private FileMetadataDao fileMetadataDao;

  @Autowired
  private TorrentExtensionService torrentExtensionService;

  @Autowired
  private TorrentClientService torrentClientService;

  @Autowired
  private BtDownloaderConfig btDownloaderConfig;


  public FileMetadataSubmitResponse prepareTransfer(final String businessUnit, final FileMetadata fileMetadata) throws IOException {

    // create key
    final StringBuffer key = new StringBuffer(META_FILE_KEY)
        .append(SEPARATOR)
        .append(businessUnit)
        .append(SEPARATOR)
        .append(fileMetadata.getFilename());

    // store transfer request metadata

    File torrentFile = new File(btDownloaderConfig.getDownloadPath() + fileMetadata.getFilename() + ".torrent");

    torrentExtensionService
        .createTorrent(
            torrentFile,
            new File(btDownloaderConfig.getDownloadPath() + fileMetadata.getFilename()),
            btDownloaderConfig.getAnnounceUrl());


    byte[] torrentToEncode = FileCopyUtils.copyToByteArray(torrentFile);

    fileMetadata.setTorrentFile(Base64.getEncoder().encodeToString(torrentToEncode));

    MutationResult saveResult = fileMetadataDao.save(key.toString(), fileMetadata);

    // pass file to BT tracker
    torrentClientService.addTorrent(fileMetadata, torrentToEncode);

    return new FileMetadataSubmitResponse(key.toString());
  }


  public FileMetadata startTransfer(String documentId) {

    // check metadata
    FileMetadata torrentToStart = fileMetadataDao.get(documentId);

    //extractTorrentFile
    byte[] torrentFile = Base64.getDecoder().decode(torrentToStart.getTorrentFile());

    //pass it to the client
    torrentClientService.addTorrent(torrentToStart, torrentFile);

    return torrentToStart;
  }

}
