package graphic.components.searchPane;

import graphic.components.cell.employee.AbstractCellFactory;
import graphic.components.event.QueryEvent;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import tollmanager.model.shared.ISearch;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;


public class SearchPane<T> extends VBox {
    private TextField searchField;
    private ListView<T> listView;
    private AbstractCellFactory<T> cellFactory;
    private ISearch<T> finder;
    private ObservableList<T> observableElements;
    private SimpleObjectProperty<T> selectedElement;

    public SearchPane(AbstractCellFactory<T> cellFactory, ISearch<T> finder) {
        selectedElement=new SimpleObjectProperty<>();
       // observableElements = FXCollections.observableArrayList(collection);
        this.cellFactory=cellFactory;
        this.finder=finder;
        initComponents();

        addEventFilter(RequestElementSearchable.SEARCHABLE_ELEMENT,e->{
            if(e.getQuery().getCollection()!=null) {
                observableElements = FXCollections.observableArrayList();
                observableElements.addAll(e.getQuery().getCollection());
            }
        });
    }

    private boolean isInputValid() {
        return searchField.getText().trim().length()>1;
    }

    private void search() {
        if(!isInputValid() || observableElements==null)
            return;

        Collection<T> collection=finder.search(new LinkedHashSet<>(observableElements), searchField.getText().trim());
        listView.setItems(FXCollections.observableArrayList(collection));
    }

    private void initComponents() {
        this.setId("searchPane");
        this.setFillWidth(true);

        initListView();
        initSearchField();

        AnchorPane.setLeftAnchor(this,0.0);
        AnchorPane.setRightAnchor(this,0.0);
        AnchorPane.setTopAnchor(this,0.0);
    }
    private void initListView() {
        listView = new ListView<>();
        listView.setCellFactory(cellFactory);
        listView.setId(getId()+"-listView");
        listView.getSelectionModel().selectedItemProperty().addListener((observableValue, t, selected) -> {
            selectedElement.set(selected);
        });
    }
    private void initSearchField() {
        searchField = new TextField();
        searchField.setId(getId()+"-searchInput");

        searchField.setPromptText("search");
        getChildren().add(searchField);

        searchField.textProperty().addListener(observable->search());
        searchField.textProperty().addListener((Observable->{
            if(!isInputValid())
                getChildren().remove(listView);
            else if(!this.getChildren().contains(listView) && isInputValid()) {
                fireEvent(new RequestElementSearchable());
                getChildren().add(listView);
            }
        }));
    }

    public SimpleObjectProperty<T> getSelectedElement() {
        return selectedElement;
    }


    public static class RequestElementSearchable extends QueryEvent<SearchableElementQuery> {
        public static final EventType<RequestElementSearchable> SEARCHABLE_ELEMENT = new EventType<>("REQUEST_SEARCHABLE_ELEMENT");
        public RequestElementSearchable() {
            super(SEARCHABLE_ELEMENT);
            setQuery(new SearchableElementQuery());
        }
    }
}
