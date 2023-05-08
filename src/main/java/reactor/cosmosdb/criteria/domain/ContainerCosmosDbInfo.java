package reactor.cosmosdb.criteria.domain;
public class ContainerCosmosDbInfo<C> {
    private String name;
    private Class<C> containerClass;

    public ContainerCosmosDbInfo(String name, Class<C> containerClass) {
        this.name = name;
        this.containerClass = containerClass;
    }

    public String getName() {
        return this.name;
    }

    public Class<C> getContainerClass() {
        return this.containerClass;
    }
}
