package com.couchbase.btdownloader.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Service
public class TorrentExtensionService {

  /* EXAMPLE

  {
       'announce': 'http://tracker.site1.com/announce',
       'info':
       {
           'files':
           [
               {'length': 111, 'path': ['111.txt']},
               {'length': 222, 'path': ['222.txt']}
           ],
           'name': 'directoryName',
           'piece length': 262144,
           'pieces': <binary SHA1 hashes>
       }
   }
   */

  private void encodeObject(Object o, OutputStream out) throws IOException {
    if (o instanceof String)
      encodeString((String)o, out);
    else if (o instanceof Map)
      encodeMap((Map)o, out);
    else if (o instanceof byte[])
      encodeBytes((byte[])o, out);
    else if (o instanceof Number)
      encodeLong(((Number) o).longValue(), out);
    else
      throw new Error("Unencodable type");
  }
  private void encodeLong(long value, OutputStream out) throws IOException {
    out.write('i');
    out.write(Long.toString(value).getBytes("US-ASCII"));
    out.write('e');
  }
  private void encodeBytes(byte[] bytes, OutputStream out) throws IOException {
    out.write(Integer.toString(bytes.length).getBytes("US-ASCII"));
    out.write(':');
    out.write(bytes);
  }
  private void encodeString(String str, OutputStream out) throws IOException {
    encodeBytes(str.getBytes("UTF-8"), out);
  }
  private void encodeMap(Map<String,Object> map, OutputStream out) throws IOException{
    // Sort the map. A generic encoder should sort by key bytes
    SortedMap<String,Object> sortedMap = new TreeMap<String, Object>(map);
    out.write('d');
    for (Map.Entry<String, Object> e : sortedMap.entrySet()) {
      encodeString(e.getKey(), out);
      encodeObject(e.getValue(), out);
    }
    out.write('e');
  }
  private byte[] hashPieces(File file, int pieceLength) throws IOException {
    MessageDigest sha1;
    try {
      sha1 = MessageDigest.getInstance("SHA");
    } catch (NoSuchAlgorithmException e) {
      throw new Error("SHA1 not supported");
    }
    InputStream in = new FileInputStream(file);
    ByteArrayOutputStream pieces = new ByteArrayOutputStream();
    byte[] bytes = new byte[pieceLength];
    int pieceByteCount  = 0, readCount = in.read(bytes, 0, pieceLength);
    while (readCount != -1) {
      pieceByteCount += readCount;
      sha1.update(bytes, 0, readCount);
      if (pieceByteCount == pieceLength) {
        pieceByteCount = 0;
        pieces.write(sha1.digest());
      }
      readCount = in.read(bytes, 0, pieceLength-pieceByteCount);
    }
    in.close();
    if (pieceByteCount > 0)
      pieces.write(sha1.digest());
    return pieces.toByteArray();
  }
  public void createTorrent(File file, File sharedFile, String announceURL) throws IOException {
    final int pieceLength = 512*1024;
    Map<String,Object> info = new HashMap<String,Object>();
    info.put("name", sharedFile.getName());
    info.put("length", sharedFile.length());
    info.put("piece length", pieceLength);
    info.put("pieces", hashPieces(sharedFile, pieceLength));
    Map<String,Object> metainfo = new HashMap<String,Object>();
    metainfo.put("announce", announceURL);
    metainfo.put("info", info);
    OutputStream out = new FileOutputStream(file);
    encodeMap(metainfo, out);
    out.close();
  }

  /*public static void main(String[] args) throws Exception {
    createTorrent(new File("/Users/davidepellegatta/Desktop/torrent-example3/test.torrent"), new File("/Users/davidepellegatta/Desktop/torrent-example3/DiscoveryMasterDeck.pptx"), "http://192.168.178.49:6969/announce");
  }*/

}
