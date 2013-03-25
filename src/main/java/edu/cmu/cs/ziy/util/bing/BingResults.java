package edu.cmu.cs.ziy.util.bing;

import java.io.Reader;
import java.lang.reflect.Type;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

public class BingResults {

  public static BingResults readBingJsonResult(String json) {
    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'")
            .registerTypeAdapter(SafeInt.class, new SafeIntTypeAdapter()).create();
    return gson.fromJson(json, BingResults.class);
  }

  public static BingResults readBingJsonResult(Reader json) {
    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'")
            .registerTypeAdapter(SafeInt.class, new SafeIntTypeAdapter()).create();
    return gson.fromJson(json, BingResults.class);
  }

  @SerializedName("d")
  private D d;

  public BingMetaData getMetaData() {
    return d.getResults()[0].getMetaData();
  }

  public String getId() {
    return d.getResults()[0].getId();
  }

  public int getWebTotal() {
    return d.getResults()[0].getWebTotal();
  }

  public int getWebOffset() {
    return d.getResults()[0].getWebOffset();
  }

  public int getImageTotal() {
    return d.getResults()[0].getImageTotal();
  }

  public int getImageOffset() {
    return d.getResults()[0].getImageOffset();
  }

  public int getVideoTotal() {
    return d.getResults()[0].getVideoTotal();
  }

  public int getVideoOffset() {
    return d.getResults()[0].getVideoOffset();
  }

  public int getNewsTotal() {
    return d.getResults()[0].getNewsTotal();
  }

  public int getNewsOffset() {
    return d.getResults()[0].getNewsOffset();
  }

  public int getSpellingSuggestionTotal() {
    return d.getResults()[0].getSpellingSuggestionTotal();
  }

  public String getAlteredQuery() {
    return d.getResults()[0].getAlteredQuery();
  }

  public String getAlterationOverrideQuery() {
    return d.getResults()[0].getAlterationOverrideQuery();
  }

  public BingWebResult[] getWebResuls() {
    return d.getResults()[0].getWebResuls();
  }

  public BingImageResult[] getImageResults() {
    return d.getResults()[0].getImageResults();
  }

  public BingVideoResult[] getVideoResults() {
    return d.getResults()[0].getVideoResults();
  }

  public BingNewsResult[] getNewsResults() {
    return d.getResults()[0].getNewsResults();
  }

  public BingRelatedSearchResult[] getRelatedSearchResults() {
    return d.getResults()[0].getRelatedSearchResults();
  }

  public BingSpellingSuggestionResult[] getSpellingSuggestionResults() {
    return d.getResults()[0].getSpellingSuggestionResults();
  }

}

class D {

  @SerializedName("results")
  private BingResult[] results;

  public BingResult[] getResults() {
    return results;
  }

}

class BingResult {

  @SerializedName("__metadata")
  private BingMetaData metaData;

  @SerializedName("ID")
  private String id;

  private SafeInt webTotal;

  private SafeInt webOffset;

  private SafeInt imageTotal;

  private SafeInt imageOffset;

  private SafeInt videoTotal;

  private SafeInt videoOffset;

  private SafeInt newsTotal;

  private SafeInt newsOffset;

  private SafeInt spellingSuggestionTotal;

  private String alteredQuery;

  private String alterationOverrideQuery;

  @SerializedName("Web")
  private BingWebResult[] webResuls;

  @SerializedName("Image")
  private BingImageResult[] imageResults;

  @SerializedName("Video")
  private BingVideoResult[] videoResults;

  @SerializedName("News")
  private BingNewsResult[] newsResults;

  @SerializedName("RelatedSearch")
  private BingRelatedSearchResult[] relatedSearchResults;

  @SerializedName("SpellingSuggestions")
  private BingSpellingSuggestionResult[] spellingSuggestionResults;

  public BingMetaData getMetaData() {
    return metaData;
  }

  public String getId() {
    return id;
  }

  public int getWebTotal() {
    return webTotal.getValue();
  }

  public int getWebOffset() {
    return webOffset.getValue();
  }

  public int getImageTotal() {
    return imageTotal.getValue();
  }

  public int getImageOffset() {
    return imageOffset.getValue();
  }

  public int getVideoTotal() {
    return videoTotal.getValue();
  }

  public int getVideoOffset() {
    return videoOffset.getValue();
  }

  public int getNewsTotal() {
    return newsTotal.getValue();
  }

  public int getNewsOffset() {
    return newsOffset.getValue();
  }

  public int getSpellingSuggestionTotal() {
    return spellingSuggestionTotal.getValue();
  }

  public String getAlteredQuery() {
    return alteredQuery;
  }

  public String getAlterationOverrideQuery() {
    return alterationOverrideQuery;
  }

  public BingWebResult[] getWebResuls() {
    return webResuls;
  }

  public BingImageResult[] getImageResults() {
    return imageResults;
  }

  public BingVideoResult[] getVideoResults() {
    return videoResults;
  }

  public BingNewsResult[] getNewsResults() {
    return newsResults;
  }

  public BingRelatedSearchResult[] getRelatedSearchResults() {
    return relatedSearchResults;
  }

  public BingSpellingSuggestionResult[] getSpellingSuggestionResults() {
    return spellingSuggestionResults;
  }

}

class SafeInt {

  private int value;

  public SafeInt(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}

class SafeIntTypeAdapter implements JsonDeserializer<SafeInt>, JsonSerializer<SafeInt> {

  public SafeInt deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
          throws JsonParseException {
    int value;
    try {
      value = json.getAsInt();
    } catch (NumberFormatException e) {
      value = 0;
    }
    return new SafeInt(value);
  }

  public JsonElement serialize(SafeInt src, Type typeOfSrc, JsonSerializationContext context) {
    return new JsonPrimitive(src.getValue());
  }
}