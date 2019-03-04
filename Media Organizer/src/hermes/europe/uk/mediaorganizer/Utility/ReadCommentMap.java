package hermes.europe.uk.mediaorganizer.Utility;

import java.beans.XMLDecoder;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;



public class ReadCommentMap {

    HashMap<String, String> loadCommentMap = new HashMap<>();

    public ReadCommentMap() {
    }

    public HashMap<String, String> readCommentMap (String filePath){
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath + "-comments.txt");
            XMLDecoder decoder = new XMLDecoder(inputStream);
            loadCommentMap = (HashMap<String, String>) decoder.readObject();
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
            return loadCommentMap;
        }
    }
}
