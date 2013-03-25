package edu.cmu.cs.ziy.util.bing;

import com.google.gson.annotations.SerializedName;

class BingVideoResult {
  
  @SerializedName("__metadata")
  private BingMetaData metaData;

  @SerializedName("ID")
  private String id;

  private String title;

  private String mediaUrl;

  private String displayUrl;

  private int runTime;

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

  public String getDisplayUrl() {
    return displayUrl;
  }

  public int getRunTime() {
    return runTime;
  }

}