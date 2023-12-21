package reactor.cosmosdb.criteria.domain;

/**
 *
 * @param <C> class of the target container (ej: TestingContainer.class)
 */
public class ContainerCosmosDbInfo<C> {
    private final String name;
    private final Class<C> containerClass;

    /**
     *
     * @param name : name of the target container
     * @param containerClass : class of the target container (Ej: TestingContainer.class)
     */
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
