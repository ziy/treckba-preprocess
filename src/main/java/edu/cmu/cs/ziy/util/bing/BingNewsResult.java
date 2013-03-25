package edu.cmu.cs.ziy.util.bing;

import java.util.Date;

import com.google.gson.annotations.SerializedName;

class BingNewsResult {
  
  @SerializedName("__metadata")
  private BingMetaData metaData;

  @SerializedName("ID")
  private String id;

  private String title;

  private String url;

  private String source;

  private String description;

  private Date date;

  public BingMetaData getMetaData() {
    return metaData;
  }

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getUrl() {
    return url;
  }

  public String getSource() {
    return source;
  }

  public String getDescription() {
    return description;
  }

  public Date getDate() {
    return date;
  }

}