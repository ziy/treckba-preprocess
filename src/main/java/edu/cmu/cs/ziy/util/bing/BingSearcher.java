package edu.cmu.cs.ziy.util.bing;

import java.io.IOException;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class BingSearcher {

  private HttpClient httpclient;

  private String accountKeyEnc;

  public enum Source {
    Web, Image, Video, News, RelatedSearch, SpellingSuggestions
  }

  public BingSearcher(String accountKey) {
    httpclient = new DefaultHttpClient();
    byte[] accountKeyBytes = Base64.encodeBase64((":" + accountKey).getBytes());
    accountKeyEnc = new String(accountKeyBytes);
  }

  private String query(String query, Source[] sources, int top, int skip, String format)
          throws ClientProtocolException, IOException {
    List<NameValuePair> pairs = Lists.newArrayList();
    pairs.add(new BasicNameValuePair("$top", String.valueOf(top)));
    pairs.add(new BasicNameValuePair("$skip", String.valueOf(skip)));
    pairs.add(new BasicNameValuePair("$format", String.valueOf(format)));
    pairs.add(new BasicNameValuePair("Query", "'" + query + "'"));
    pairs.add(new BasicNameValuePair("Sources", "'" + Joiner.on('+').join(sources) + "'"));

    String url = "https://api.datamarket.azure.com/Bing/Search/Composite?"
            + URLEncodedUtils.format(pairs, "utf-8");
    System.out.println(url);
    HttpGet httpget = new HttpGet(url);
    httpget.setHeader("Authorization", "Basic " + accountKeyEnc);
    ResponseHandler<String> responseHandler = new BasicResponseHandler();
    String responseBody = httpclient.execute(httpget, responseHandler);
    return responseBody;
  }

  public String query(String query, Source[] sources, int top, String format)
          throws ClientProtocolException, IOException {
    return query(query, sources, top, 0, format);
  }

  public String query(String query, Source[] sources, int top) throws ClientProtocolException,
          IOException {
    return query(query, sources, top, 0, "json");
  }

  public void close() {
    httpclient.getConnectionManager().shutdown();
  }

}
