package reactor.cosmosdb.criteria.domain;


import java.util.List;

/**
 *
 * @param <T> : DataResponse you expect to receive
 */
public class ProjectionPaginated<T> {
    private int total;
    private int totalResult;
    private boolean hasNextPage;
    private List<T> data;

    public ProjectionPaginated(int total, int totalResult, boolean hasNextPage, List<T> data) {
        this.total = total;
        this.totalResult = totalResult;
        this.hasNextPage = hasNextPage;
        this.data = data;
    }

    public ProjectionPaginated() {
    }

    public static <T> ProjectionPaginatedBuilder<T> builder() {
        return new ProjectionPaginatedBuilder<>();
    }

    public int getTotal() {
        return this.total;
    }

    public int getTotalResult() {
        return this.totalResult;
    }

    public boolean isHasNextPage() {
        return this.hasNextPage;
    }

    public List<T> getData() {
        return this.data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public static class ProjectionPaginatedBuilder<T> {
        private int total;
        private int totalResult;
        private boolean hasNextPage;
        private List<T> data;

        ProjectionPaginatedBuilder() {
        }

        public ProjectionPaginatedBuilder<T> total(int total) {
            this.total = total;
            return this;
        }

        public ProjectionPaginatedBuilder<T> totalResult(int totalResult) {
            this.totalResult = totalResult;
            return this;
        }

        public ProjectionPaginatedBuilder<T> hasNextPage(boolean hasNextPage) {
            this.hasNextPage = hasNextPage;
            return this;
        }

        public ProjectionPaginatedBuilder<T> data(List<T> data) {
            this.data = data;
            return this;
        }

        public ProjectionPaginated<T> build() {
            return new ProjectionPaginated<>(total, totalResult, hasNextPage, data);
        }

        public String toString() {
            return "ProjectionPaginated.ProjectionPaginatedBuilder(total=" + this.total + ", totalResult=" + this.totalResult + ", hasNextPage=" + this.hasNextPage + ", data=" + this.data + ")";
        }
    }
}
