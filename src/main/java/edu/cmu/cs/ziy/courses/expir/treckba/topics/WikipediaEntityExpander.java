package edu.cmu.cs.ziy.courses.expir.treckba.topics;

import java.io.IOException;
import java.util.Calendar;
import java.util.Set;

import org.wikipedia.Wiki;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;

import edu.cmu.cs.ziy.wiki.entity.WikipediaEntity;

public interface WikipediaEntityExpander {

  Set<WikipediaEntity> generateEntities(String originalEntity, Wiki wiki) throws IOException;

  RangeSet<Calendar> validateExistence(String originalEntity, String expandedEntity,
          Range<Calendar> period, Wiki wiki) throws IOException;

  Set<WikipediaEntity> generateAndValidateExistence(String originalEntity, Range<Calendar> period,
          Wiki wiki) throws IOException;

}
