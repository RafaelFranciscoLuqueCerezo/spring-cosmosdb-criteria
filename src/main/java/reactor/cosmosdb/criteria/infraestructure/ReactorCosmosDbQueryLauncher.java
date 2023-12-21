package reactor.cosmosdb.criteria.infraestructure;

import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.CosmosAsyncDatabase;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.SqlQuerySpec;
import com.azure.cosmos.util.CosmosPagedFlux;


/**
 *
 * @param <T> : Response object that you are going to retrieve directly from database
 */
public interface ReactorCosmosDbQueryLauncher<T> {
    String getDatabaseName();
    CosmosAsyncClient getClientAsync();

    default CosmosPagedFlux<T> launch(String querySentence, String containerName, Class<T> targetClass) {
        SqlQuerySpec querySpec = new SqlQuerySpec(querySentence);
        CosmosAsyncDatabase database = getClientAsync().getDatabase(getDatabaseName());
        CosmosAsyncContainer container = database.getContainer(containerName);
        return container.queryItems(querySpec, new CosmosQueryRequestOptions(), targetClass);
    }

    default CosmosAsyncContainer getContainerInstance(String containerName){
        CosmosAsyncDatabase database = getClientAsync().getDatabase(getDatabaseName());
        return database.getContainer(containerName);
    }
}
