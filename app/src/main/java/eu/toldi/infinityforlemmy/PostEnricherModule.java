package eu.toldi.infinityforlemmy;

import java.util.Set;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import eu.toldi.infinityforlemmy.apis.RedgifsAPI;
import eu.toldi.infinityforlemmy.post.enrich.CompositePostEnricher;
import eu.toldi.infinityforlemmy.post.enrich.PostEnricher;
import eu.toldi.infinityforlemmy.post.enrich.RedGifsPostEnricher;


@Module
abstract class PostEnricherModule {

    @Provides
    @IntoSet
    static PostEnricher provideRedGifsPostEnricher(RedgifsAPI redgifsAPI) {
        return new RedGifsPostEnricher(redgifsAPI);
    }

    @Provides
    static PostEnricher providePostEnricher(Set<PostEnricher> postEnrichers) {
        return new CompositePostEnricher(postEnrichers);
    }
}
