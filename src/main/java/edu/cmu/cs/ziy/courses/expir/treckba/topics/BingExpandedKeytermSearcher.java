package edu.cmu.cs.ziy.courses.expir.treckba.topics;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.apache.http.client.ClientProtocolException;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

import com.google.common.collect.Sets;
import com.google.common.io.Files;

import edu.cmu.cs.ziy.util.bing.BingSearcher;
import edu.cmu.cs.ziy.util.bing.BingSearcher.Source;

public class BingExpandedKeytermSearcher {

  private BingSearcher[] searchers;

  private int flag = 0;

  private SqlJetDb db;

  private static final String CREATE_CACHE_TABLE = "CREATE TABLE searches "
          + "(query TEXT NOT NULL PRIMARY KEY, json_result TEXT, query_time INTEGER NOT NULL)";

  // Constructor for loading searcher
  public BingExpandedKeytermSearcher(File dbFile) throws SqlJetException, IOException {
    openDatabase(dbFile);
  }

  // Constructor for creating searcher
  public BingExpandedKeytermSearcher(File accountKeysFile, File dbFile) throws SqlJetException,
          IOException {
    List<String> accountKeys = Files.readLines(accountKeysFile, Charset.defaultCharset());
    searchers = new BingSearcher[accountKeys.size()];
    for (int i = 0; i < accountKeys.size(); i++) {
      searchers[i] = new BingSearcher(accountKeys.get(i));
    }
    if (!dbFile.exists()) {
      createDatabase(dbFile);
      createTable();
    } else {
      openDatabase(dbFile);
    }
  }

  public BingExpandedKeytermSearcher(String[] accountKeys, File dbFile) throws SqlJetException {
    searchers = new BingSearcher[accountKeys.length];
    for (int i = 0; i < accountKeys.length; i++) {
      searchers[i] = new BingSearcher(accountKeys[i]);
    }
    if (!dbFile.exists()) {
      createDatabase(dbFile);
      createTable();
    }
  }

  public String search(String query) throws ClientProtocolException, IOException, SqlJetException {
    String jsonResult;
    if ((jsonResult = lookupRecord(query)) != null) {
      return jsonResult;
    }
    if (searchers != null) {
      jsonResult = searchers[flag].query(query, new Source[] { Source.Web, Source.News }, 50);
      insertRecord(query, jsonResult);
      flag = (flag + 1) % searchers.length;
    }
    return jsonResult;
  }

  private String lookupRecord(String query) throws SqlJetException {
    db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
    try {
      ISqlJetTable table = db.getTable("searches");
      ISqlJetCursor cursor = table.lookup(null, query);
      if (!cursor.eof()) {
        return cursor.getString(1);
      } else {
        return null;
      }
    } finally {
      db.commit();
    }
  }

  private void insertRecord(String query, String jsonResult) throws SqlJetException {
    db.beginTransaction(SqlJetTransactionMode.WRITE);
    try {
      ISqlJetTable table = db.getTable("searches");
      table.insert(query, jsonResult, Calendar.getInstance().getTimeInMillis());
    } finally {
      db.commit();
    }
  }

  private void createTable() throws SqlJetException {
    try {
      db.createTable(CREATE_CACHE_TABLE);
    } finally {
      db.commit();
    }
  }

  private void createDatabase(File dbFile) throws SqlJetException {
    db = SqlJetDb.open(dbFile, true);
    db.getOptions().setAutovacuum(true);
    db.beginTransaction(SqlJetTransactionMode.WRITE);
    try {
      db.getOptions().setUserVersion(1);
    } finally {
      db.commit();
    }
  }

  private void openDatabase(File dbFile) throws SqlJetException {
    db = SqlJetDb.open(dbFile, true);
  }

  public void close() throws SqlJetException {
    for (BingSearcher searcher : searchers) {
      searcher.close();
    }
    db.close();
  }

  public static void example() throws Throwable, IOException {
    BingExpandedKeytermSearcher searcher = new BingExpandedKeytermSearcher(new File(
            "noupload/bing-account-keys.txt"), new File(
            "bing-cache/bing-expanded-keyterm-searches.db3"));
    String result = searcher.search("Lithuanian Jews Aharon Barak");
    System.out.println(result);
    searcher.close();
  }

  public static void main(String[] args) throws Throwable, IOException {
    BingExpandedKeytermSearcher searcher = new BingExpandedKeytermSearcher(new File(
            "noupload/bing-account-keys.txt"), new File(
            "bing-cache/bing-expanded-keyterm-searches.db3"));
    List<String> lines = Files.readLines(new File("src/main/resources/expanded-keyterms.tsv"),
            Charset.defaultCharset());
    int lineno = 0;
    Set<String> queries = Sets.newHashSet();
    for (String line : lines) {
      lineno++;
      String[] fields = line.split("\t");
      if (fields.length < 4) {
        continue;
      }
      String keyterm = fields[0];
      String original = fields[1];
      keyterm = keyterm.replaceAll("Category:", "");
      keyterm = keyterm.replaceAll("List of ", "");
      keyterm = keyterm.replaceAll("\\s*\\(.*?\\)\\s*", "");
      System.out.println(keyterm + " " + original);
      queries.add(keyterm);
      searcher.search(keyterm);
      queries.add(original);
      searcher.search(original);
      queries.add(keyterm + " " + original);
      searcher.search(keyterm + " " + original);
      System.out.println(lineno + " lines " + queries.size() + " unique queries");
    }
    searcher.close();
  }
}
