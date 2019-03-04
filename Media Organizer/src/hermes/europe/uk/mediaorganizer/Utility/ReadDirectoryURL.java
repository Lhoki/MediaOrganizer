package hermes.europe.uk.mediaorganizer.Utility;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ReadDirectoryURL {

    private String directoryURL;

    public ReadDirectoryURL() {
    }

    //reads directory url from text file for persistence in playlists.
    public String returnDirecoryURL(String filePath) throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader(filePath + "-DirectoryURL.txt"));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                line = br.readLine();
                directoryURL = line;
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directoryURL;
    }

}
