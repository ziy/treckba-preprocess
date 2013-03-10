package edu.cmu.cs.ziy.courses.expir.treckba.topics;

import java.io.IOException;
import java.util.Calendar;
import java.util.Set;
import java.util.regex.Pattern;

import org.wikipedia.Wiki;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.Sets;

import edu.cmu.cs.ziy.wiki.entity.WikipediaEntity;

public abstract class AbstractWikipediaEntityExpander implements WikipediaEntityExpander {

  @Override
  public abstract Set<WikipediaEntity> generateEntities(String originalEntity, Wiki wiki)
          throws IOException;

  @Override
  public abstract RangeSet<Calendar> validateExistence(String originalEntity,
          String expandedEntity, Range<Calendar> period, Wiki wiki) throws IOException;

  @Override
  public Set<WikipediaEntity> generateAndValidateExistence(String originalEntity,
          Range<Calendar> period, Wiki wiki) throws IOException {
    Set<WikipediaEntity> entities = generateEntities(originalEntity, wiki);
    for (WikipediaEntity entity : entities) {
      RangeSet<Calendar> periods = validateExistence(originalEntity, entity.getText(), period, wiki);
      entity.addValidPeriods(periods);
    }
    // DONE Remove entity with zero period
    return Sets.newHashSet(Collections2.filter(entities, new Predicate<WikipediaEntity>() {

      @Override
      public boolean apply(WikipediaEntity input) {
        return !input.getValidPeriods().isEmpty();
      }
    }));
  }

  protected boolean containsLink(String contentText, String expandedEntity) {
    String regex = "\\[{2}" + Pattern.quote(expandedEntity) + "(?:\\]{2}|\\|)";
    return Pattern.compile(regex).matcher(contentText).find();
  }

}
