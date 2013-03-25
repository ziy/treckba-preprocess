package edu.cmu.cs.ziy.util.bing;

import com.google.gson.annotations.SerializedName;

class BingWebResult {
  
  @SerializedName("__metadata")
  private BingMetaData metaData;

  @SerializedName("ID")
  private String id;

  private String title;

  private String description;

  private String url;

  private String displayUrl;

  public BingMetaData getMetaData() {
    return metaData;
  }

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String getUrl() {
    return url;
  }

  public String getDisplayUrl() {
    return displayUrl;
  }

}