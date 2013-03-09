package edu.cmu.cs.ziy.courses.expir.treckba.topics;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import org.wikipedia.Wiki;

import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

import edu.cmu.cs.ziy.wiki.WikipediaNamespacePredicate;
import edu.cmu.cs.ziy.wiki.entity.WikipediaEntity;

public class RedirectEntityExpander extends AbstractExternalEntityExpander implements
        WikipediaEntityExpander {

  @Override
  public Set<WikipediaEntity> generateEntities(String originalEntity, Wiki wiki) throws IOException {
    Set<WikipediaEntity> redirects = Sets.newHashSet();
    for (String redirect : Collections2.filter(
            Arrays.asList(wiki.whatLinksHere(originalEntity, true, Wiki.MAIN_NAMESPACE)),
            new WikipediaNamespacePredicate(wiki, Wiki.MAIN_NAMESPACE))) {
      redirects
              .add(WikipediaEntity.newInvalidInstance(redirect, WikipediaEntity.Relation.REDIRECT));
    }
    return redirects;
  }

  // TODO Check the reasons why multiple revisions are available for redirects, and why
  // sometimes no revision.
  // DONE Create WikipediaArticle for redirects (actually all the External expanded terms, e.g.
  // INLINK).
  // assert redirectRevisions.size() == 1;

}
