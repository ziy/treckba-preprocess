package edu.cmu.cs.ziy.courses.expir.treckba.topics;

import java.io.IOException;
import java.util.Calendar;
import java.util.Set;

import org.wikipedia.Wiki;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;

import edu.cmu.cs.ziy.wiki.entity.WikipediaEntity;

public abstract class AbstractWikipediaEntityExpanderImpl implements WikipediaEntityExpander {

  @Override
  public abstract Set<WikipediaEntity> generateEntities(String originalEntity, Wiki wiki)
          throws IOException;

  @Override
  public abstract RangeSet<Calendar> validateExistence(String originalEntity,
          String expandedEntity, Range<Calendar> period, Wiki wiki) throws IOException;

  protected boolean containsLink(String contentText, String expandedEntity) {
    String regex = ".*\\[{2}" + expandedEntity + "(?:\\]{2}|\\|).*";
    return contentText.matches(regex);
  }

  public Set<WikipediaEntity> generateAndValidateExistence(String originalEntity,
          String expandedEntity, Range<Calendar> period, Wiki wiki) throws IOException {
    Set<WikipediaEntity> entities = generateEntities(originalEntity, wiki);
    for (WikipediaEntity entity : entities) {
      entity.addValidPeriods(validateExistence(originalEntity, expandedEntity, period, wiki));
    }
    return entities;
  }

}
