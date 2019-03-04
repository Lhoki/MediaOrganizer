package hermes.europe.uk.mediaorganizer.Utility;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.HashSet;


public class RemoveDuplicatesFromObservableList {
    public ObservableList<String> removeDuplicates(ObservableList<String> list) {
        ObservableList<String> result = FXCollections.observableArrayList();
        HashSet<String> set = new HashSet<>();
        for (String item : list) {
            // If String is not in set, add it to the list and the set.
            if (!set.contains(item)) {
                result.add(item);
                set.add(item);
            }
        }
        return result;
    }
}
