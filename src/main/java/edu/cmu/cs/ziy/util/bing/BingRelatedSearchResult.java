package edu.cmu.cs.ziy.util.bing;

import com.google.gson.annotations.SerializedName;

class BingRelatedSearchResult {
  
  @SerializedName("__metadata")
  private BingMetaData metaData;

  @SerializedName("ID")
  private String id;

  private String title;

  private String bingUrl;

  public BingMetaData getMetaData() {
    return metaData;
  }

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getBingUrl() {
    return bingUrl;
  }

}