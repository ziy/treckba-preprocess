package edu.cmu.cs.ziy.courses.expir.treckba.judgments.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JudgmentStatAnalyzer {

  private List<Judgment> judgments = new ArrayList<Judgment>();

  public JudgmentStatAnalyzer(File judgmentFile) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(judgmentFile));
    String line;
    while ((line = br.readLine()) != null) {
      if (line.startsWith("#")) {
        continue;
      }
      judgments.add(new Judgment(line));
    }
    br.close();
  }

  public void filterJudgments() {
    List<Judgment> newJudgments = new ArrayList<Judgment>();
    Map<StreamIdEntityPair, Integer> streamEntity2judgment = new HashMap<StreamIdEntityPair, Integer>();
    Map<StreamIdEntityPair, Boolean> streamEntity2mention = new HashMap<StreamIdEntityPair, Boolean>();
    for (Judgment judgment : judgments) {
      StreamIdEntityPair pair = new StreamIdEntityPair(judgment.streamId, judgment.entity);
      if (!streamEntity2judgment.containsKey(pair)
              || streamEntity2judgment.get(pair) > judgment.judgment) {
        streamEntity2judgment.put(pair, judgment.judgment);
        streamEntity2mention.put(pair, judgment.mention);
      }
    }
    for (StreamIdEntityPair streamEntity : streamEntity2judgment.keySet()) {
      newJudgments.add(new Judgment(streamEntity.streamId, streamEntity.entity,
              streamEntity2judgment.get(streamEntity), streamEntity2mention.get(streamEntity)));
    }
    judgments = newJudgments;
  }

  public void printJudgmentStat() {
    int[][][] stat = new int[2][2][4]; // [train/test][mention/zm][g/n/r/c]
    for (Judgment judgment : judgments) {
      stat[judgment.streamId.epochTick < 1325376000 ? 0 : 1][judgment.mention ? 0 : 1][judgment.judgment + 1]++;
    }
    System.out.println("train");
    System.out.println("\tG\tN\tR\tC");
    System.out.println("M\t" + stat[0][0][0] + "\t" + stat[0][0][1] + "\t" + stat[0][0][2] + "\t"
            + stat[0][0][3]);
    System.out.println("ZM\t" + stat[0][1][0] + "\t" + stat[0][1][1] + "\t" + stat[0][1][2] + "\t"
            + stat[0][1][3]);
    System.out.println();
    System.out.println("test");
    System.out.println("\tG\tN\tR\tC");
    System.out.println("M\t" + stat[1][0][0] + "\t" + stat[1][0][1] + "\t" + stat[1][0][2] + "\t"
            + stat[1][0][3]);
    System.out.println("ZM\t" + stat[1][1][0] + "\t" + stat[1][1][1] + "\t" + stat[1][1][2] + "\t"
            + stat[1][1][3]);
    System.out.println();
  }

  public void printPerEntityJudgmentStat(int relevanceThreshold) {
    int[][][] stat = new int[2][29][2]; // [train/test][entity][pos/neg]
    List<String> entities = new ArrayList<String>();
    for (Judgment judgment : judgments) {
      if (!entities.contains(judgment.entity)) {
        entities.add(judgment.entity);
      }
      stat[judgment.streamId.epochTick < 1325376000 ? 0 : 1][entities.indexOf(judgment.entity)][judgment.judgment >= relevanceThreshold ? 0
              : 1]++;
    }
    System.out.println("\tTrain POS\tTrain NEG\tTest POS\tTest NEG");
    for (int i = 0; i < entities.size(); i++) {
      System.out.println(entities.get(i) + "\t" + stat[0][i][0] + "\t" + stat[0][i][1] + "\t"
              + stat[1][i][0] + "\t" + stat[1][i][1]);
    }
    System.out.println();
  }

  public static void main(String[] args) throws IOException {
    File file = new File("data/trec-kba-ccr-2012-scorer-and-full-annotation/"
            + "trec-kba-ccr-2012-judgments-2012JUN22-final.filter-run.txt");
    JudgmentStatAnalyzer jsa = new JudgmentStatAnalyzer(file);
    jsa.printJudgmentStat();
    jsa.filterJudgments();
    jsa.printJudgmentStat();
    jsa.printPerEntityJudgmentStat(Judgment.CENTRAL);
  }
}

class Judgment {

  public static final int GARBAGE = -1;

  public static final int NEUTRAL = 0;

  public static final int RELEVANT = 1;

  public static final int CENTRAL = 2;

  public StreamId streamId;

  public String entity;

  public int judgment;

  public boolean mention;

  public Judgment(StreamId streamId, String entity, int judgment, boolean mention) {
    super();
    this.streamId = streamId;
    this.entity = entity;
    this.judgment = judgment;
    this.mention = mention;
  }

  public Judgment(String trecJudgmentLine) {
    String[] fields = trecJudgmentLine.split("\t");
    streamId = new StreamId(fields[2]);
    entity = fields[3];
    judgment = Integer.parseInt(fields[5]);
    mention = fields[6].equals("1") ? true : false;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((entity == null) ? 0 : entity.hashCode());
    result = prime * result + judgment;
    result = prime * result + (mention ? 1231 : 1237);
    result = prime * result + ((streamId == null) ? 0 : streamId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Judgment other = (Judgment) obj;
    if (entity == null) {
      if (other.entity != null)
        return false;
    } else if (!entity.equals(other.entity))
      return false;
    if (judgment != other.judgment)
      return false;
    if (mention != other.mention)
      return false;
    if (streamId == null) {
      if (other.streamId != null)
        return false;
    } else if (!streamId.equals(other.streamId))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Judgment [streamId=" + streamId + ", entity=" + entity + ", judgment=" + judgment
            + ", mention=" + mention + "]";
  }

}

class StreamIdEntityPair implements Comparable<StreamIdEntityPair> {

  public StreamId streamId;

  public String entity;

  public StreamIdEntityPair(StreamId streamId, String entity) {
    super();
    this.streamId = streamId;
    this.entity = entity;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((entity == null) ? 0 : entity.hashCode());
    result = prime * result + ((streamId == null) ? 0 : streamId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    StreamIdEntityPair other = (StreamIdEntityPair) obj;
    if (entity == null) {
      if (other.entity != null)
        return false;
    } else if (!entity.equals(other.entity))
      return false;
    if (streamId == null) {
      if (other.streamId != null)
        return false;
    } else if (!streamId.equals(other.streamId))
      return false;
    return true;
  }

  @Override
  public int compareTo(StreamIdEntityPair o) {
    if (!streamId.equals(o.streamId)) {
      return streamId.compareTo(o.streamId);
    } else {
      return entity.compareTo(o.entity);
    }
  }
}

class StreamId implements Comparable<StreamId> {

  public int epochTick;

  public String hash;

  public StreamId(String streamId) {
    super();
    String[] pair = streamId.split("-", 2);
    assert pair.length == 2;
    epochTick = Integer.parseInt(pair[0]);
    hash = pair[1];
  }

  @Override
  public String toString() {
    return epochTick + "-" + hash;
  }

  @Override
  public int compareTo(StreamId o) {
    if (epochTick != o.epochTick) {
      return Integer.compare(epochTick, o.epochTick);
    } else {
      return hash.compareTo(o.hash);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + epochTick;
    result = prime * result + ((hash == null) ? 0 : hash.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    StreamId other = (StreamId) obj;
    if (epochTick != other.epochTick)
      return false;
    if (hash == null) {
      if (other.hash != null)
        return false;
    } else if (!hash.equals(other.hash))
      return false;
    return true;
  }

}