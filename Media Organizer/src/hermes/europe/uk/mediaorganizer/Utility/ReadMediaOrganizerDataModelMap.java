package hermes.europe.uk.mediaorganizer.Utility;

import hermes.europe.uk.mediaorganizer.MediaOrganizerDataModel;

import java.beans.XMLDecoder;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

//reads MediaOrganizerMap for persistence when loading.
public class ReadMediaOrganizerDataModelMap {
    public HashMap<Integer, MediaOrganizerDataModel> readMediaOrganizerDataModelMap(String filePath) {
        FileInputStream inputStream = null;
        HashMap<Integer, MediaOrganizerDataModel> loadMediaOrganizerDataModelMap = new HashMap<>();
        try {
            inputStream = new FileInputStream(filePath);
            XMLDecoder decoder = new XMLDecoder(inputStream);
            loadMediaOrganizerDataModelMap = (HashMap<Integer, MediaOrganizerDataModel>) decoder.readObject();
            decoder.close();

        } catch (IOException e) {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            e.printStackTrace();

        } finally {
            return loadMediaOrganizerDataModelMap;
        }
    }

}
