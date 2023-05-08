package reactor.cosmosdb.criteria.infraestructure;

import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.CosmosAsyncDatabase;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.SqlQuerySpec;
import com.azure.cosmos.util.CosmosPagedFlux;


public abstract class ReactorCosmosDbQueryLauncher<T> {
    public String databaseName;
    public CosmosAsyncClient clientAsync;

    public String getDatabaseName(){
        return this.databaseName;
    }
    public CosmosAsyncClient getClientAsync(){
        return this.clientAsync;
    }

    public CosmosPagedFlux<T> launch(String querySentence, String containerName, Class<T> targetClass) {
        SqlQuerySpec querySpec = new SqlQuerySpec(querySentence);
        CosmosAsyncDatabase database = getClientAsync().getDatabase(getDatabaseName());
        CosmosAsyncContainer container = database.getContainer(containerName);
        return container.queryItems(querySpec, new CosmosQueryRequestOptions(), targetClass);
    }
}
