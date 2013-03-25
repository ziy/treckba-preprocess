package edu.cmu.cs.ziy.util.bing;

import com.google.gson.annotations.SerializedName;

class BingImageResult {
  
  @SerializedName("__metadata")
  private BingMetaData metaData;

  @SerializedName("ID")
  private String id;

  private String title;

  private String mediaUrl;

  private String sourceUrl;

  private String displayUrl;

  private int width;

  private int height;

  private long fileSize;

  private String contentType;

  // private Thumbnail thumbnail;

  public BingMetaData getMetaData() {
    return metaData;
  }

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getMediaUrl() {
    return mediaUrl;
  }

  public String getSourceUrl() {
    return sourceUrl;
  }

  public String getDisplayUrl() {
    return displayUrl;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public long getFileSize() {
    return fileSize;
  }

  public String getContentType() {
    return contentType;
  }

}