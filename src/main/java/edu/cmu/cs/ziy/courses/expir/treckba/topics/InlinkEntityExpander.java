package edu.cmu.cs.ziy.courses.expir.treckba.topics;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import org.wikipedia.Wiki;

import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

import edu.cmu.cs.ziy.wiki.WikipediaNamespacePredicate;
import edu.cmu.cs.ziy.wiki.entity.WikipediaEntity;

public class InlinkEntityExpander extends AbstractExternalEntityExpander implements
        WikipediaEntityExpander {

  @Override
  public Set<WikipediaEntity> generateEntities(String originalEntity, Wiki wiki) throws IOException {
    Set<WikipediaEntity> inlinks = Sets.newHashSet();
    for (String inlink : Collections2.filter(
            Arrays.asList(wiki.whatLinksHere(originalEntity, Wiki.MAIN_NAMESPACE)),
            new WikipediaNamespacePredicate(wiki, Wiki.MAIN_NAMESPACE))) {
      inlinks.add(WikipediaEntity.newInvalidInstance(inlink, WikipediaEntity.Relation.INLINK));
    }
    return inlinks;
  }

}
