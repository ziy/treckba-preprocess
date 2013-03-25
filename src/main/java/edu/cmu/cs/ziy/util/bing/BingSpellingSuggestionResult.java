package edu.cmu.cs.ziy.util.bing;

import com.google.gson.annotations.SerializedName;

class BingSpellingSuggestionResult {
  
  @SerializedName("__metadata")
  private BingMetaData metaData;

  @SerializedName("ID")
  private String id;

  private String value;

  public BingMetaData getMetaData() {
    return metaData;
  }

  public String getId() {
    return id;
  }

  public String getValue() {
    return value;
  }

}