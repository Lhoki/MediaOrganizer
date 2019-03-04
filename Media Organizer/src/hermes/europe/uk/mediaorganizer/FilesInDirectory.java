package hermes.europe.uk.mediaorganizer;

import javafx.beans.property.SimpleStringProperty;

import java.util.Arrays;

public class FilesInDirectory {
    private final SimpleStringProperty fileName = new SimpleStringProperty();

    public FilesInDirectory(String fName) {
        setFileName(fName);
    }


    public String getFileName() {
        return fileName.get();
    }

    public SimpleStringProperty fileNameProperty() {
        return fileName;
    }

    public String fileNameString() {
        return fileName.get();
    }

    public void setFileName(String fName) {
        this.fileName.set(fName);
    }
}
