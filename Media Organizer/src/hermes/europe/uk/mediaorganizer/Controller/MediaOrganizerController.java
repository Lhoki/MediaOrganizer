package hermes.europe.uk.mediaorganizer.Controller;

import hermes.europe.uk.mediaorganizer.PlaylistDataModel;
import hermes.europe.uk.mediaorganizer.MediaOrganizerDataModel;
import hermes.europe.uk.mediaorganizer.Utility.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.beans.XMLDecoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.ResourceBundle;

public class MediaOrganizerController implements Initializable {

    private RemoveDuplicatesFromObservableList removeDuplicatesFromObservableList = new RemoveDuplicatesFromObservableList();
    private MediaOrganizerDataModel currentTask = new MediaOrganizerDataModel();
    private ReadMediaOrganizerDataModelMap readMediaOrganizerDataModelMap = new ReadMediaOrganizerDataModelMap();
    private ReadDirectoryURL readDirectoryURL = new ReadDirectoryURL();
    private ReadCommentMap readCommentMap = new ReadCommentMap();

    private HashMap<Integer, MediaOrganizerDataModel> mediaOrganizerDataModelMap = new HashMap<>();
    private HashMap<String, String> commentsMap = new HashMap<>();
    private HashMap<Integer, PlaylistDataModel> playlistMap = new HashMap<>();
    private AudioPlaylist audioPlaylist = new AudioPlaylist();

    private ObservableList<String> playlistObservableList = FXCollections.observableArrayList();
    private ObservableList<String> listOfCategories = FXCollections.observableArrayList();
    private ObservableList<MediaOrganizerDataModel> mediaTableContentObservableList = FXCollections.observableArrayList();
    private ObservableList<MediaOrganizerDataModel> mediaTableContentObservableListFiltered = FXCollections.observableArrayList();

    private FilteredList<MediaOrganizerDataModel> mediaTableContentFilteredList = new FilteredList<>(mediaTableContentObservableList);

    private boolean mainDirectoryCheck = false;
    private int resultSetID = 0;
    private int playlistCount;
    private ArrayList<String> extensions = new ArrayList<String>();
    private String currentDirectoryURL;
    private MediaPlayer mediaPlayer;

    //////////////////////////INITIALIZE\\\\\\\\\\\\\\\\\\\\\\\\\\\
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //initialize the table columns
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeTableColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        categoryColumnTable.setCellValueFactory(new PropertyValueFactory<>("category"));

        nameTableColumn.setSortable(false);
        typeTableColumn.setSortable(false);
        categoryColumnTable.setSortable(false);

        //initializes playlist dropdown menu
        playlistDropDown.setItems(playlistObservableList);

        //initializes main media table
        mediaContentTable.setItems(mediaTableContentObservableListFiltered);

        //makes media table selectable and updates values based on selection
        mediaContentTable.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends
                MediaOrganizerDataModel> observable, MediaOrganizerDataModel oldValue, MediaOrganizerDataModel newValue) -> setCurrentTask(newValue));
    }

    /////////////////////Playlist\\\\\\\\\\\\\\\\\\\\\\\\\

    @FXML
    private ComboBox<String> playlistDropDown;

    @FXML
    private Button deleteFromPlaylistButton;

    @FXML
    private Button loadButton;

    @FXML
    private Button addSelectedToPlaylist;


    @FXML
    void playButtonAction(ActionEvent event) throws Exception {
        Stage musicPlayerStage = new Stage();
        audioPlaylist.setPlaylistMap(playlistMap);
        try {
            audioPlaylist.start(musicPlayerStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void deleteFromPlaylistButtonAction(ActionEvent event) {
        String removeMedia = playlistDropDown.getValue();
        ArrayList<Integer> indexToRemove = new ArrayList<Integer>();

        for (Map.Entry<Integer, PlaylistDataModel> mapResultSet : playlistMap.entrySet()) {
            if (mapResultSet.getValue().getPlaylistName().equals(removeMedia)){
                indexToRemove.add(mapResultSet.getKey());
            }
        }

        for (int i : indexToRemove){
            playlistMap.remove(i);
        }
        playlistObservableList.remove(removeMedia);
        playlistCount--;
    }


    @FXML
    void addSelectedToPlaylistAction(ActionEvent event) {
        try {
            playlistMap.put(playlistMap.size(), new PlaylistDataModel(currentDirectoryURL, currentTask.getName(), currentTask.getType()));
            playlistCount++;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        playlistObservableList.add(playlistMap.get(playlistMap.size()-1 ).getPlaylistName());
        playlistDropDown.setItems(playlistObservableList);
    }

    @FXML
    void savePlaylistButtonAction(ActionEvent event) {
        SaveDataMaps saveDataMaps = new SaveDataMaps();
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) gridPaneID.getScene().getWindow();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("playlist files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(stage);
        saveDataMaps.savePlaylistDataMap(getPlaylistMap(), file, ".xml");
    }


    @FXML
    void loadPlaylistButtonAction(ActionEvent event) throws FileNotFoundException, MalformedURLException {
        HashMap<Integer, PlaylistDataModel> temporaryPlaylistHashmap = new HashMap<>();
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) gridPaneID.getScene().getWindow();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("playlist files", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            playlistCount = 0;
            HashMap<Integer, PlaylistDataModel> loadPlaylistMap = new HashMap<>();
            playlistMap.clear();

            XMLDecoder decoder = new XMLDecoder(new FileInputStream(file.getAbsolutePath()));
            loadPlaylistMap = (HashMap<Integer, PlaylistDataModel>) decoder.readObject();
            setPlaylistMap(loadPlaylistMap);
            decoder.close();

            //loads the playlist map media
            for (Map.Entry<Integer, PlaylistDataModel> playlistEntry : playlistMap.entrySet()) {
                temporaryPlaylistHashmap.put(playlistCount, new PlaylistDataModel(playlistMap.get(playlistCount).getMediaItemPath(),
                        playlistMap.get(playlistCount).getPlaylistName(), playlistMap.get(playlistCount).getPlaylistType()));
                playlistObservableList.add(playlistMap.get(playlistCount).getPlaylistName());
                playlistCount ++;
            }

            playlistMap.clear();
            playlistMap.putAll(temporaryPlaylistHashmap);
            playlistDropDown.setItems(playlistObservableList);
        }
    }


    ///////////////////////CATEGORY\\\\\\\\\\\\\\\\\\\\\\\\\

    @FXML
    private ComboBox<String> categoryDropDown;


    @FXML
    void deleteSelectedFromCategoryAction(ActionEvent event) {
        int selectedID = mediaTableContentObservableListFiltered.get(currentTask.getId()).getId();
        int currentID = 0;

        mediaTableContentObservableListFiltered.set(selectedID, new MediaOrganizerDataModel(
                mediaTableContentObservableListFiltered.get(selectedID).getId(),
                mediaTableContentObservableListFiltered.get(selectedID).getName(),
                mediaTableContentObservableListFiltered.get(selectedID).getType(),
                ""));


        for (MediaOrganizerDataModel resultSet : mediaTableContentObservableList) {
            for (MediaOrganizerDataModel filteredResultSet : mediaTableContentObservableListFiltered) {
                if (resultSet.getName().equals(currentTask.getName())) {
                    resultSet.setCategory("");
                    filteredResultSet.setCategory("");
                }
            }
        }

        updatePersistenceMap(currentID);
    }

    @FXML
    void addCategoryAction(ActionEvent event) {
        Stage stage = (Stage) gridPaneID.getScene().getWindow();

        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        VBox dialogVbox = new VBox(5);
        dialogVbox.getChildren().add(new Text("Type A New Category"));
        TextField typeCategoryTextField = new TextField();
        typeCategoryTextField.setPromptText("Type here");
        Button addButton = new Button("Add Category");

        addButton.setOnAction(event1 -> {
            listOfCategories.add(typeCategoryTextField.getText());
            categoryDropDown.setItems(removeDuplicatesFromObservableList.removeDuplicates(listOfCategories));
            dialog.close();
        });

        dialogVbox.getChildren().add(typeCategoryTextField);
        dialogVbox.getChildren().add(addButton);


        Scene dialogScene = new Scene(dialogVbox, 200, 77);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    @FXML
    void addSelectedToCategoryButtonAction(ActionEvent event) {

        int selectedID = mediaTableContentObservableListFiltered.get(currentTask.getId()).getId();

        int currentID = 0;

        if (categoryDropDown.getValue() != null) {
            //replaces category in result set for selected item
            mediaTableContentObservableListFiltered.set(selectedID, new MediaOrganizerDataModel(
                    mediaTableContentObservableListFiltered.get(selectedID).getId(),
                    mediaTableContentObservableListFiltered.get(selectedID).getName(),
                    mediaTableContentObservableListFiltered.get(selectedID).getType(),
                    categoryDropDown.getValue()));

            for (MediaOrganizerDataModel resultSet : mediaTableContentObservableList) {
                for (MediaOrganizerDataModel filteredResultSet : mediaTableContentObservableListFiltered) {
                    if (resultSet.getName().equals(filteredResultSet.getName())) {
                        if (filteredResultSet.getCategory() != null) {
                            resultSet.setCategory(filteredResultSet.getCategory());
                        }
                    }
                }
            }

            updatePersistenceMap(currentID);
        }
    }


    ///////////////////////COMMENTS\\\\\\\\\\\\\\\\\\\\\\\\\

    @FXML
    private TextArea commentTextBox;

    @FXML
    void deleteCommentFromSelectedButtonAction(ActionEvent event) {
        if (commentsMap.get(currentTask.getName()) != null) {
            commentTextBox.clear();
            commentsMap.remove(currentTask.getName());
            commentTextBox.setDisable(false);
        }
    }

    @FXML
    void editSelectedCommentButtonAction(ActionEvent event) {
        if (commentsMap.get(currentTask.getName()) != null) {
            commentTextBox.setDisable(false);
        }
    }

    @FXML
    void addCommentToSelectedButtonAction(ActionEvent event) {
        commentsMap.put(currentTask.getName(), commentTextBox.getText());
        commentTextBox.clear();
        commentTextBox.appendText(commentsMap.get(currentTask.getName()));
        commentTextBox.setDisable(true);
    }

    ///////////////////////DIRECTORY\\\\\\\\\\\\\\\\\\\\\\\\

    public void setCurrentDirectoryURL(String directory) {
        currentDirectoryURL = directory;
    }

    @FXML
    private void openDirectoryButtonAction(ActionEvent event) {
        Stage stage = (Stage) gridPaneID.getScene().getWindow();

        if (mainDirectoryCheck) {
            directoryCheck(stage, openDirectoryButton);
            return;
        }

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(stage);

        if (file != null) {
            currentDirectoryURL = file.getAbsolutePath();

            mainDirectoryCheck = true;
            resultSetID = 0;

            commentsMap.clear();
            mediaTableContentObservableList.clear();
            mediaTableContentObservableListFiltered.clear();
            mediaTableContentFilteredList.clear();
            mediaOrganizerDataModelMap.clear();
            openDirectoryTextBox.clear();

            openDirectoryTextBox.appendText(file.getAbsolutePath());
            String currentDirectoryURL = file.getAbsolutePath();
            File[] listOfFiles = file.listFiles();

            for (File listOfFile : listOfFiles) {
                String fileName = listOfFile.getName();
                String fileType = listOfFile.getName();

                //gets the position of the "." before extension type
                int positionOfLastDotInString = fileName.lastIndexOf(".");

                //Parses string and splits into file name and type
                //if checks that an extension type exists and if it doesn't, print a blank extension
                //e.g for folders instead of files.
                if (positionOfLastDotInString > 0) {
                    fileName = fileName.substring(0, positionOfLastDotInString);
                    fileType = fileType.substring(positionOfLastDotInString, fileType.length());

                    //adds results to resultsSet which displays in the tableview
                    mediaTableContentObservableList.add(new MediaOrganizerDataModel(resultSetID, fileName, fileType));

                    //adds data to hashmap for data persistence use later.
                    mediaOrganizerDataModelMap.put(resultSetID, new MediaOrganizerDataModel(resultSetID, fileName, fileType));

                    String currentCategory = mediaTableContentObservableList.get(resultSetID).getCategory();
                    listOfCategories.addAll(currentCategory);
                } else {
                    mediaTableContentObservableList.add(new MediaOrganizerDataModel(resultSetID, fileName, ""));
                    mediaOrganizerDataModelMap.put(resultSetID, new MediaOrganizerDataModel(resultSetID, fileName, ""));
                }
                resultSetID++;
            }
            mediaTableContentObservableListFiltered.addAll(mediaTableContentObservableList);
            categoryDropDown.setItems(removeDuplicatesFromObservableList.removeDuplicates(listOfCategories));
        } else {
            if (!mediaTableContentObservableList.isEmpty()) {
                mainDirectoryCheck = true;
            }
        }
    }




    ///////////////////////////UTILITY//////////////////////////////////////////

    //This method is used to filter out certain extension types from user selected extension checkboxes.
    public void PredicateExtensionFilter() {
        mediaTableContentObservableListFiltered.clear();
        int newID = 0;

        for (String typeArray : extensions) {
            Predicate<MediaOrganizerDataModel> containsType = i -> i.getType().toLowerCase().contains(typeArray);
            Predicate<MediaOrganizerDataModel> filter = (containsType);
            mediaTableContentFilteredList.setPredicate(filter);
            mediaTableContentObservableListFiltered.addAll(mediaTableContentFilteredList);
        }

        //changes the map for persistence
        updatePersistenceMap(newID);
    }



    //save work warning when opening new directory or loading.
    public void directoryCheck(Stage stage, Button actionOnConfirmButton) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        VBox dialogVbox = new VBox(7);

        dialogVbox.getChildren().add(new Text("Are you sure?"));
        dialogVbox.getChildren().add(new Text("Any unsaved work will be lost.."));
        Scene dialogScene = new Scene(dialogVbox, 200, 105);

        Button confirmButton = new Button("Confirm");
        Button cancelButton = new Button("Cancel");

        confirmButton.setMinWidth(80);
        cancelButton.setMinWidth(80);

        confirmButton.setOnAction(event1 -> {
            mainDirectoryCheck = false;
            dialog.close();
            actionOnConfirmButton.fire();
        });

        cancelButton.setOnAction(event1 -> {
            dialog.close();
        });

        dialogVbox.getChildren().add(cancelButton);
        dialogVbox.getChildren().add(confirmButton);

        dialog.setScene(dialogScene);
        dialog.show();
    }

    //current task = current selected media in table
    private void setCurrentTask(MediaOrganizerDataModel selectedTask) {
        if (selectedTask != null) {
            currentTask.setId(selectedTask.getId());
            currentTask.setName(selectedTask.getName());
            currentTask.setType(selectedTask.getType());
            currentTask.setCategory(selectedTask.getCategory());
            if(currentTask.getType().equals(".mp3") || currentTask.getType().equals(".mp4") ||currentTask.getType().equals(".flv") ||currentTask.getType().equals(".aac")){
                addSelectedToPlaylist.setDisable(false);
                deleteFromPlaylistButton.setDisable(false);
                addSelectedToPlaylist.setText("Add Selected To Playlist");
                deleteFromPlaylistButton.setText("Delete From Playlist");
            } else {
                addSelectedToPlaylist.setDisable(true);
                deleteFromPlaylistButton.setDisable(true);
                addSelectedToPlaylist.setText("Unsupported Media");
                deleteFromPlaylistButton.setText("Unsupported Media");
            }
            commentTextBox.clear();
            commentTextBox.setDisable(false);
            if (commentsMap.get(currentTask.getName()) != null) {
                commentTextBox.clear();
                commentTextBox.setDisable(true);
                commentTextBox.appendText(commentsMap.get(currentTask.getName()));
            }
        } else {
            currentTask.setId(null);
            currentTask.setName("");
            currentTask.setType("");
        }
    }

    //This method runs through all the items in one list, and replaces them in the persistence map with the new values,
    //this is used when adding/removing categories from items to ensure the persistence is updated with category values.
    private void updatePersistenceMap(int newID) {
        for (MediaOrganizerDataModel resultSet : mediaTableContentObservableListFiltered) {
            for (Map.Entry<Integer, MediaOrganizerDataModel> mapResultSet : mediaOrganizerDataModelMap.entrySet()) {
                if (mapResultSet.getValue().getName().equals(resultSet.getName())) {
                    mediaOrganizerDataModelMap.replace(mapResultSet.getKey(), resultSet);
                }
            }
            resultSet.setId(newID);
            newID++;
        }
    }

    /////////////////////PERSISTENCE BUTTONS\\\\\\\\\\\\\\\\\\\\\\\\


    @FXML
    void loadButtonAction(ActionEvent event) throws FileNotFoundException {
        SetMediaOrganizerDataModelMap setMediaOrganizerDataModelMap = new SetMediaOrganizerDataModelMap();
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) gridPaneID.getScene().getWindow();
        if (mainDirectoryCheck) {
            directoryCheck(stage, loadButton);
            return;
        }

        //opens filechooser wit only the extensions .xml
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("xml files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(stage);

        //checks user selected file
        if (file != null) {

            //if user selected a file, clear all the current lists in preparation for new data.
            mainDirectoryCheck = true;
            mediaTableContentObservableList.clear();
            mediaTableContentObservableListFiltered.clear();
            mediaTableContentFilteredList.clear();
            mediaOrganizerDataModelMap.clear();

            String filePath = file.getAbsolutePath();
            int positionOfLastDotInString = filePath.lastIndexOf(".");
            filePath = filePath.substring(0, positionOfLastDotInString);


            //loads saved data map and puts data into current datamap
            setMediaOrganizerDataModelMap.setMediaOrganizerDataModelMap(
                    readMediaOrganizerDataModelMap.readMediaOrganizerDataModelMap(
                            file.getAbsolutePath()), mediaOrganizerDataModelMap, mediaTableContentObservableList, resultSetID);

            setCurrentDirectoryURL(readDirectoryURL.returnDirecoryURL(filePath));

            //adds all the data to observable lists
            mediaTableContentObservableListFiltered.addAll(mediaTableContentObservableList);
            setCommentsMap(readCommentMap.readCommentMap(filePath));
            setCategoryDropDownList();
            openDirectoryTextBox.appendText(currentDirectoryURL);
            System.out.println(currentDirectoryURL);

        } else {
            if (!mediaTableContentObservableList.isEmpty()) {
                mainDirectoryCheck = true;
            }
        }
    }

    @FXML
    void saveAsButtonAction(ActionEvent event) {
        SaveDataMaps saveDataMaps = new SaveDataMaps();
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) gridPaneID.getScene().getWindow();

        //Set extension filter for xml files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("xml files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(stage);

        saveDataMaps.saveMediaOraganizerDataMap(getMediaOrganizerDataModelMap(), file, ".xml");
        saveDataMaps.saveCommentDataMap(getCommentMap(), file, "-comments.txt");
        try {
            saveDataMaps.saveDirectoryURL(currentDirectoryURL, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ////////////////////////////BOILERPLATE\\\\\\\\\\\\\\\\\\\\\\\\\\\

    /////////////////////Media Content Table\\\\\\\\\\\\\\\\\\\\\\\\\

    @FXML
    private TableView<MediaOrganizerDataModel> mediaContentTable;

    @FXML
    private TableColumn<MediaOrganizerDataModel, String> nameTableColumn;

    @FXML
    private TableColumn<MediaOrganizerDataModel, String> typeTableColumn;


    @FXML
    private TableColumn<MediaOrganizerDataModel, String> categoryColumnTable;


    ///////////////////////Scene\\\\\\\\\\\\\\\\\\\\\\\\\

    @FXML
    private GridPane gridPaneID;

    ////////////////////Check Boxes\\\\\\\\\\\\\\\\\\\\\\\

    @FXML
    private CheckBox mp3TickBox;

    @FXML
    private CheckBox aacTicketBox;

    @FXML
    private CheckBox ogaTickBox;

    @FXML
    private CheckBox mpaTickBox;

    @FXML
    private CheckBox mp4TickBox;

    @FXML
    private CheckBox aviTickBox;

    @FXML
    private CheckBox webmTickBox;

    @FXML
    private CheckBox flvTickBox;

    @FXML
    private CheckBox oggTickBox;

    @FXML
    private CheckBox movTickBox;

    @FXML
    private CheckBox jpegTickBox;

    @FXML
    private CheckBox pngTickBox;

    @FXML
    private CheckBox gifTickBox;

    @FXML
    private CheckBox tiffTickBox;

    @FXML
    private Button openDirectoryButton;

    @FXML
    private TextField openDirectoryTextBox;

    public void setCommentsMap(HashMap<String, String> initialCommentsMap) {
        commentsMap.clear();
        commentsMap.putAll(initialCommentsMap);
    }

    public void setPlaylistMap(HashMap<Integer, PlaylistDataModel> initialPlaylistMap) {
        playlistMap.clear();
        playlistMap.putAll(initialPlaylistMap);
    }

    //sets the categories from already existing categories
    public void setCategoryDropDownList() {
        ObservableList<String> setCategoryDropDownList = FXCollections.observableArrayList();
        for (MediaOrganizerDataModel resultSet : mediaTableContentObservableListFiltered) {
            if (resultSet.getCategory() != null)
                setCategoryDropDownList.add(resultSet.getCategory());
        }
        categoryDropDown.setItems(removeDuplicatesFromObservableList.removeDuplicates(setCategoryDropDownList));
    }

    public HashMap<Integer, MediaOrganizerDataModel> getMediaOrganizerDataModelMap() {
        return mediaOrganizerDataModelMap;
    }

    public HashMap<Integer, PlaylistDataModel> getPlaylistMap() {
        return playlistMap;
    }


    public HashMap<String, String> getCommentMap() {
        return commentsMap;
    }

    @FXML
    void gifTickBoxAction(ActionEvent event) {
        if (gifTickBox.isSelected()) {
            extensions.add("gif");
            PredicateExtensionFilter();
        }
        if (!gifTickBox.isSelected()) {
            extensions.remove("gif");
            PredicateExtensionFilter();
        }
    }

    @FXML
    void jpegTickBoxAction(ActionEvent event) {
        if (jpegTickBox.isSelected()) {
            extensions.add("jpg");
            extensions.add("jpeg");
            PredicateExtensionFilter();
        }
        if (!jpegTickBox.isSelected()) {
            extensions.remove("jpg");
            extensions.remove("jpeg");
            PredicateExtensionFilter();
        }
    }

    @FXML
    void pngTickBoxAction(ActionEvent event) {
        if (pngTickBox.isSelected()) {
            extensions.add("png");
            PredicateExtensionFilter();
        }
        if (!pngTickBox.isSelected()) {
            extensions.remove("png");
            PredicateExtensionFilter();
        }
    }

    @FXML
    void webmTickBoxAction(ActionEvent event) {
        if (webmTickBox.isSelected()) {
            extensions.add("webm");
            PredicateExtensionFilter();
        }
        if (!webmTickBox.isSelected()) {
            extensions.remove("webm");
            PredicateExtensionFilter();
        }
    }


    @FXML
    void tiffTickBoxAction(ActionEvent event) {
        if (tiffTickBox.isSelected()) {
            extensions.add("tif");
            PredicateExtensionFilter();
        }
        if (!tiffTickBox.isSelected()) {
            extensions.remove("tif");
            PredicateExtensionFilter();
        }
    }

    @FXML
    void ogaTickBoxAction(ActionEvent event) {
        if (ogaTickBox.isSelected()) {
            extensions.add("oga");
            PredicateExtensionFilter();
        }
        if (!ogaTickBox.isSelected()) {
            extensions.remove("oga");
            PredicateExtensionFilter();
        }
    }

    @FXML
    void oggTickBoxAction(ActionEvent event) {
        if (oggTickBox.isSelected()) {
            extensions.add("ogg");
            PredicateExtensionFilter();
        }
        if (!oggTickBox.isSelected()) {
            extensions.remove("ogg");
            PredicateExtensionFilter();
        }
    }

    @FXML
    void movTickBoxAction(ActionEvent event) {
        if (movTickBox.isSelected()) {
            extensions.add("mov");
            PredicateExtensionFilter();
        }
        if (!movTickBox.isSelected()) {
            extensions.remove("mov");
            PredicateExtensionFilter();
        }
    }

    @FXML
    void mp3TickBoxAction(ActionEvent event) {
        if (mp3TickBox.isSelected()) {
            extensions.add("mp3");
            PredicateExtensionFilter();
        }
        if (!mp3TickBox.isSelected()) {
            extensions.remove("mp3");
            PredicateExtensionFilter();
        }
    }

    @FXML
    void mp4TickBoxAction(ActionEvent event) {
        if (mp4TickBox.isSelected()) {
            extensions.add("mp4");
            PredicateExtensionFilter();
        }
        if (!mp4TickBox.isSelected()) {
            extensions.remove("mp4");
            PredicateExtensionFilter();
        }
    }

    @FXML
    void mpaTickBoxAction(ActionEvent event) {
        if (mpaTickBox.isSelected()) {
            extensions.add("mpa");
            PredicateExtensionFilter();
        }
        if (!mpaTickBox.isSelected()) {
            extensions.remove("mpa");
            PredicateExtensionFilter();
        }
    }


    @FXML
    void flvTickBoxAction(ActionEvent event) {
        if (flvTickBox.isSelected()) {
            extensions.add("flv");
            PredicateExtensionFilter();
        }
        if (!flvTickBox.isSelected()) {
            extensions.remove("flv");
            PredicateExtensionFilter();
        }
    }


    @FXML
    void aviTickBoxAction(ActionEvent event) {
        if (aviTickBox.isSelected()) {
            extensions.add("avi");
            PredicateExtensionFilter();
        }
        if (!aviTickBox.isSelected()) {
            extensions.remove("avi");
            PredicateExtensionFilter();
        }
    }

    @FXML
    void aacTicketBoxAction(ActionEvent event) {
        if (aacTicketBox.isSelected()) {
            extensions.add("aac");
            PredicateExtensionFilter();
        }
        if (!aacTicketBox.isSelected()) {
            extensions.remove("aac");
            PredicateExtensionFilter();
        }
    }


}
