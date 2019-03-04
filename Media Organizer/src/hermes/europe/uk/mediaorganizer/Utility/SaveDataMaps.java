package hermes.europe.uk.mediaorganizer.Utility;

import hermes.europe.uk.mediaorganizer.PlaylistDataModel;
import hermes.europe.uk.mediaorganizer.MediaOrganizerDataModel;

import java.beans.XMLEncoder;
import java.io.*;
import java.util.HashMap;

//class used for saving hashmaps to disk.
public class SaveDataMaps {
    FileOutputStream mediaDataMap = null;
    FileOutputStream commentsDataMap = null;
    FileOutputStream playListDataMap = null;
    public HashMap<Integer, MediaOrganizerDataModel> saveMediaOraganizerDataMap(HashMap<Integer, MediaOrganizerDataModel> saveHashmap, File file, String extension) {

        String filePath = file.getAbsolutePath();
        int positionOfLastDotInString = filePath.lastIndexOf(".");
        filePath = filePath.substring(0, positionOfLastDotInString);

        try {
            mediaDataMap = new FileOutputStream(filePath + extension);
            XMLEncoder encoder = new XMLEncoder(mediaDataMap);
            encoder.writeObject(saveHashmap);
            encoder.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return saveHashmap;
    }


    public HashMap<Integer, PlaylistDataModel> savePlaylistDataMap(HashMap<Integer, PlaylistDataModel> savePlaylistHashMap, File file, String extension) {
        String filePath = file.getAbsolutePath();
        int positionOfLastDotInString = filePath.lastIndexOf(".");
        filePath = filePath.substring(0, positionOfLastDotInString);

        try {
            playListDataMap = new FileOutputStream(filePath + extension);
            XMLEncoder encoder = new XMLEncoder(playListDataMap);
            encoder.writeObject((Object)savePlaylistHashMap);
            encoder.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return savePlaylistHashMap;
    }







    public String saveURL(String directoryURL){
        return directoryURL;
    }

    public void saveDirectoryURL(String directoryURL, File file) throws IOException {
        String filePath = file.getAbsolutePath();
        int positionOfLastDotInString = filePath.lastIndexOf(".");
        filePath = filePath.substring(0, positionOfLastDotInString);

        FileWriter fileWriter = new FileWriter(filePath + "-DirectoryURL.txt" );
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.print(directoryURL);
        printWriter.close();
    }


    public HashMap<String, String> saveCommentDataMap(HashMap<String, String> saveHashmap, File file, String extension) {
        String filePath = file.getAbsolutePath();
        int positionOfLastDotInString = filePath.lastIndexOf(".");
        filePath = filePath.substring(0, positionOfLastDotInString);

        try {
            commentsDataMap = new FileOutputStream(filePath + extension);
            XMLEncoder encoder = new XMLEncoder(commentsDataMap);
            encoder.writeObject(saveHashmap);
            encoder.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return saveHashmap;
    }


}
