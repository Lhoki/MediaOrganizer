package hermes.europe.uk.mediaorganizer.Utility;

import hermes.europe.uk.mediaorganizer.PlaylistDataModel;
import javafx.application.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.util.*;

/** Example of playing all audio files in a given directory. */
public class AudioPlaylist extends Application {
    //  private static final String MUSIC_DIR = "C:\\Users\\hisg472\\Desktop\\Media Organizer Media";
    public static final String TAG_COLUMN_NAME = "Tag";
    public static final String VALUE_COLUMN_NAME = "Value";
    public static final List<String> SUPPORTED_FILE_EXTENSIONS = Arrays.asList(".mp3", ".m4a");

    private HashMap<Integer, PlaylistDataModel> playlistMap;



    int playlistTrack = 0;


    public static final int FILE_EXTENSION_LEN = 3;

   // private String[] musicList = new String[3];

    private PlaylistDataModel playlistDataModel;



    final Label currentlyPlaying = new Label();
    final ProgressBar progress = new ProgressBar();
    final TableView<Map> metadataTable = new TableView<>();
    private ChangeListener<Duration> progressChangeListener;
    private MapChangeListener<String, Object> metadataChangeListener;



    public static void main(String[] args) throws Exception { launch(args); }

    private MediaPlayer createPlayer(HashMap<Integer, PlaylistDataModel> mediaSource) {

        final MediaPlayer player = new MediaPlayer(mediaSource.get(playlistTrack).getMedia());
        player.setOnError(new Runnable() {
            @Override public void run() {
                System.out.println("Media error occurred: " + player.getError());
            }
        });
        System.out.println(playlistMap.size()); //prints 3

        if (playlistMap.size() -1 == playlistTrack){
            playlistTrack = 0;
        } else {
            playlistTrack++;
        }
        return player;
    }

    public PlaylistDataModel getPlaylistDataModel() {
        return playlistDataModel;
    }

    public void setPlaylistDataModel(PlaylistDataModel playlistDataModel) {
        this.playlistDataModel = playlistDataModel;
    }


    public HashMap<Integer, PlaylistDataModel> getPlaylistMap() {
        return playlistMap;
    }

    public void setPlaylistMap(HashMap<Integer, PlaylistDataModel> playlistMap) {
        this.playlistMap = playlistMap;
    }



    public void start(final Stage stage) throws Exception {


        stage.setTitle("CMEDIA Audio Player");

        final ObservableList<MediaPlayer> players = FXCollections.observableArrayList();

        players.addAll(createPlayer(playlistMap));

        if (players.isEmpty()) {
            System.out.println("No audio found");
            Platform.exit();
            return;
        }

        // create a view to show the mediaplayers.
        final MediaView mediaView = new MediaView(players.get(0));
        final Button skip = new Button("Skip");
        final Button play = new Button("Pause");

        //play each audio file in turn.
        for (int i = 0; i < players.size(); i++) {
            final MediaPlayer player     = players.get(i);
            final MediaPlayer nextPlayer = players.get((i + 1) % players.size());
            player.setOnEndOfMedia(new Runnable() {
                @Override public void run() {
                    player.currentTimeProperty().removeListener(progressChangeListener);
                    player.getMedia().getMetadata().removeListener(metadataChangeListener);
                    player.stop();
                    mediaView.setMediaPlayer(nextPlayer);
                    nextPlayer.play();
                }
            });
        }

        //allow the user to skip a track.
        skip.setOnAction(actionEvent -> {
            final MediaPlayer curPlayer = mediaView.getMediaPlayer();
            curPlayer.currentTimeProperty().removeListener(progressChangeListener);
            curPlayer.getMedia().getMetadata().removeListener(metadataChangeListener);
            curPlayer.stop();


            MediaPlayer nextPlayer = createPlayer(playlistMap);
            mediaView.setMediaPlayer(nextPlayer);
        });

        play.setOnAction(actionEvent -> {
            if ("Pause".equals(play.getText())) {
                mediaView.getMediaPlayer().pause();
                play.setText("Play");
            } else {
                mediaView.getMediaPlayer().play();
                play.setText("Pause");
            }
        });

        // display the name of the currently playing track.
        mediaView.mediaPlayerProperty().addListener((observableValue, oldPlayer, newPlayer) -> setCurrentlyPlaying(newPlayer));

        // start playing the first track.
        mediaView.setMediaPlayer(players.get(0));
        mediaView.getMediaPlayer().play();
        setCurrentlyPlaying(mediaView.getMediaPlayer());

        // silly invisible button used as a template to get the actual preferred size of the Pause button.
        Button invisiblePause = new Button("Pause");
        invisiblePause.setVisible(false);
        play.prefHeightProperty().bind(invisiblePause.heightProperty());
        play.prefWidthProperty().bind(invisiblePause.widthProperty());

        // add a metadataTable for meta data display
        metadataTable.setStyle("-fx-font-size: 13px;");

        TableColumn<Map, String> tagColumn = new TableColumn<>(TAG_COLUMN_NAME);
        tagColumn.setPrefWidth(150);
        TableColumn<Map, Object> valueColumn = new TableColumn<>(VALUE_COLUMN_NAME);
        valueColumn.setPrefWidth(400);

        tagColumn.setCellValueFactory(new MapValueFactory<>(TAG_COLUMN_NAME));
        valueColumn.setCellValueFactory(new MapValueFactory<>(VALUE_COLUMN_NAME));



        // layout the scene.
        final StackPane layout = new StackPane();
        layout.setStyle("-fx-font-size: 20; -fx-padding: 20; -fx-alignment: center;");

        final HBox progressReport = new HBox(10);
        progressReport.setAlignment(Pos.CENTER);
        progressReport.getChildren().setAll(skip, play, progress, mediaView);

        final VBox content = new VBox(10);
        content.getChildren().setAll(
                currentlyPlaying,
                progressReport
        );

        layout.getChildren().addAll(
                invisiblePause,
                content
        );
        progress.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(progress, Priority.ALWAYS);

        Scene scene = new Scene(layout, 600, 100);
        stage.setScene(scene);
        stage.show();
    }

    /** sets the currently playing label to the label of the new media player and updates the progress monitor. */
    private void setCurrentlyPlaying(final MediaPlayer newPlayer) {
        newPlayer.seek(Duration.ZERO);

        progress.setProgress(0);
        progressChangeListener = new ChangeListener<Duration>() {
            @Override public void changed(ObservableValue<? extends Duration> observableValue, Duration oldValue, Duration newValue) {
                progress.setProgress(1.0 * newPlayer.getCurrentTime().toMillis() / newPlayer.getTotalDuration().toMillis());
            }
        };
        newPlayer.currentTimeProperty().addListener(progressChangeListener);

        String source = newPlayer.getMedia().getSource();
        source = source.substring(0, source.length() - FILE_EXTENSION_LEN);
        source = source.substring(source.lastIndexOf("/") + 1).replaceAll("%20", " ");
        currentlyPlaying.setText("Now Playing: " + source);

        setMetaDataDisplay(newPlayer.getMedia().getMetadata());
    }

    private void setMetaDataDisplay(ObservableMap<String, Object> metadata) {
        metadataTable.getItems().setAll(convertMetadataToTableData(metadata));
        metadataChangeListener = new MapChangeListener<String, Object>() {
            @Override
            public void onChanged(Change<? extends String, ?> change) {
                metadataTable.getItems().setAll(convertMetadataToTableData(metadata));
            }
        };
        metadata.addListener(metadataChangeListener);
    }

    private ObservableList<Map> convertMetadataToTableData(ObservableMap<String, Object> metadata) {
        ObservableList<Map> allData = FXCollections.observableArrayList();

        for (String key: metadata.keySet()) {
            Map<String, Object> dataRow = new HashMap<>();

            dataRow.put(TAG_COLUMN_NAME,   key);
            dataRow.put(VALUE_COLUMN_NAME, metadata.get(key));

            allData.add(dataRow);
        }

        return allData;
    }

    /** @return a MediaPlayer for the given source which will report any errors it encounters */

}