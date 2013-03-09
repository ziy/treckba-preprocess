package edu.cmu.cs.ziy.courses.expir.treckba.topics;

import java.io.IOException;
import java.util.Set;

import org.wikipedia.Wiki;

import com.google.common.collect.Sets;

import edu.cmu.cs.ziy.wiki.entity.WikipediaEntity;

public class CategoryEntityExpander extends AbstractInternalEntityExpander implements
        WikipediaEntityExpander {

  @Override
  public Set<WikipediaEntity> generateEntities(String originalEntity, Wiki wiki)
          throws IOException {
    Set<WikipediaEntity> categories = Sets.newHashSet();
    for (String category : wiki.getCategories(originalEntity)) {
      categories.add(WikipediaEntity
              .newInvalidInstance(category, WikipediaEntity.Relation.CATEGORY));
    }
    return categories;
  }

}
