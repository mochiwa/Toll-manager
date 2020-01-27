package graphic.components.searchPane;

import javafx.collections.ObservableList;

import java.util.Collection;

public class SearchableElementQuery {
    private Collection collection;

    public SearchableElementQuery() {
    }

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }
}
