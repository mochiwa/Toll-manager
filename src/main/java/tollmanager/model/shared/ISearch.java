package tollmanager.model.shared;

import java.util.Collection;
import java.util.Set;

public interface ISearch<T> {
    Set<T> search(Collection<T> collection,String search);
}
