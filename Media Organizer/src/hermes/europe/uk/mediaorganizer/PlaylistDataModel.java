package hermes.europe.uk.mediaorganizer;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.media.Media;

import java.beans.ConstructorProperties;
import java.io.File;
import java.net.MalformedURLException;

public class PlaylistDataModel {

    StringProperty playlistName = new SimpleStringProperty();

    StringProperty playlistType = new SimpleStringProperty();

    StringProperty mediaItemPath = new SimpleStringProperty();

    private Media media;
    public PlaylistDataModel(){

    }

    public PlaylistDataModel(String mediaItemPath, String playlistName, String playlistType) throws MalformedURLException {
        this.mediaItemPath.set(mediaItemPath);
        this.playlistName.set(playlistName);
        this.playlistType.set(playlistType);
        String fullPath = mediaItemPath + "\\" +  playlistName + playlistType;

        setMedia(new Media(new File(fullPath).toURI().toURL().toString()));
    }


    //////BOILER


    public String getPlaylistName() {
        return playlistName.get();
    }

    public StringProperty playlistNameProperty() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName.set(playlistName);
    }

    public String getPlaylistType() {
        return playlistType.get();
    }

    public StringProperty playlistTypeProperty() {
        return playlistType;
    }

    public void setPlaylistType(String playlistType) {
        this.playlistType.set(playlistType);
    }

    public String getMediaItemPath() {
        return mediaItemPath.get();
    }

    public StringProperty mediaItemPathProperty() {
        return mediaItemPath;
    }

    public void setMediaItemPath(String mediaItemPath) {
        this.mediaItemPath.set(mediaItemPath);
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

}
