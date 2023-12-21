package reactor.cosmosdb.criteria.application;

import com.azure.cosmos.models.FeedResponse;
import com.azure.cosmos.util.CosmosPagedFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.cosmosdb.criteria.domain.TotalCountResponse;
import reactor.cosmosdb.criteria.domain.exception.AppCriteriaBuilderException;
import reactor.cosmosdb.criteria.infraestructure.ReactorCosmosDbQueryLauncher;
import reactor.cosmosdb.criteria.domain.ContainerCosmosDbInfo;
import reactor.cosmosdb.criteria.domain.PaginatedCriteria;
import reactor.cosmosdb.criteria.domain.ProjectionPaginated;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 *
 * @param <E> : Entity (ej: Testing)
 * @param <C> : Container (ej: TestingContainer)
 */
public class ReactorOperationsCosmosDbRepository<E,C> {

    private ReactorCosmosDbQueryLauncher<C> queryLauncher;

    private ReactorCosmosDbQueryLauncher<TotalCountResponse> queryCountLauncher;

    public ReactorOperationsCosmosDbRepository() {
    }

    public ReactorOperationsCosmosDbRepository(
            ReactorCosmosDbQueryLauncher<C> queryLauncher,
            ReactorCosmosDbQueryLauncher<TotalCountResponse> queryCountLauncher
    ) {
        this.queryLauncher = queryLauncher;
        this.queryCountLauncher = queryCountLauncher;
    }

    /**
     *
     * @param criteria : criteria to launch again target container
     * @param containerName : name of the target container
     * @return
     */
    public Mono<TotalCountResponse> findBySelectCountCriteria(PaginatedCriteria criteria,String containerName){
        if(!criteria.isCountQuery()){
            throw new AppCriteriaBuilderException("the criteria provided does not have countQuery enabled, please enabled it with selectCount() method");
        }
        CosmosPagedFlux<TotalCountResponse> dbResponse = queryCountLauncher.launch(criteria.getQuerySentence(),containerName,TotalCountResponse.class);
        return dbResponse.collectList().map(a-> !a.isEmpty() ? a.get(0) : new TotalCountResponse(0));
    }

    /**
     *
     * @param criteria : criteria to launch again target container
     * @param desiredPage : desired page that you want to ask for
     * @param pageSize : size of the result you want to retrieve
     * @param containerCosmosDbInfo : contains necessary attributes (name of the container and class type ) to launch the query against the target container
     * @param toEntity : entity where mapping the results obtained
     * @return
     */
    public Mono<ProjectionPaginated<E>> findByCriteriaPaginated(PaginatedCriteria criteria, int desiredPage, int pageSize, ContainerCosmosDbInfo<C> containerCosmosDbInfo, Function<C,E> toEntity) {
        CosmosPagedFlux<C> pagedIterable = queryLauncher
                .launch(criteria.getQuerySentence(), containerCosmosDbInfo.getName(), containerCosmosDbInfo.getContainerClass());

        Flux<FeedResponse<C>> feedResponse = pagedIterable.byPage(pageSize);

        var builder = ProjectionPaginated.<E>builder();

        var totalResult = feedResponse.collectList()
                .map(response -> (pageSize * (response.size() - 1)) + response.get(response.size() - 1).getResults().size());
        var monoHasNextChecker = feedResponse.elementAt(desiredPage + 1)
                .map(response->true)
                .onErrorReturn(IndexOutOfBoundsException.class,Boolean.FALSE);

        var monoResponse = feedResponse.elementAt(desiredPage)
                .flatMap(response->{
                    List<E> projectionList = response.getResults().stream()
                            .map(toEntity).collect(Collectors.toList());
                    return Mono.just(projectionList);
                });

        return Mono.zip(monoHasNextChecker,monoResponse,totalResult).map(tupple-> builder
                        .hasNextPage(tupple.getT1())
                        .data(tupple.getT2())
                        .totalResult(tupple.getT3())
                        .build()
                )
                .onErrorReturn(IndexOutOfBoundsException.class,builder.hasNextPage(false).data(List.of()).build());

    }
}
