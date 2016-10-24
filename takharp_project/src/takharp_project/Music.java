package takharp_project;

import java.io.File;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * This is class is a representation of a music file. It contains a song name,
 * artist, album, file path and a comment about the song.
 *
 * @author ParminderTakhar
 */
public class Music {

    //propreties were used as data members inorder to take advantage of the
    //observable interface; when adding listeners on a tableview object, it is 
    //easier to implement dynamic editing on the table
    private SimpleStringProperty name;
    private SimpleStringProperty artist;
    private SimpleStringProperty album;
    private SimpleStringProperty pathName;
    private SimpleStringProperty comment;
    private SimpleBooleanProperty isSelected;

    public Music(String name, String artist, String album, String path, String comment) {
        this.name = new SimpleStringProperty(name);
        this.artist = new SimpleStringProperty(artist);
        this.album = new SimpleStringProperty(album);
        pathName = new SimpleStringProperty(path);
        this.comment = new SimpleStringProperty(comment);
        isSelected = new SimpleBooleanProperty(false);
    }

    /**
     * Returns music name
     *
     * @return name
     */
    public String getName() {
        return name.get();
    }

    /**
     * Returns music artist
     *
     * @return artist
     */
    public String getArtist() {
        return artist.get();
    }

    /**
     * Returns file path of music object
     *
     * @return path
     */
    public String getPathName() {
        return pathName.get();
    }
    
    /**
     * Returns album of music object
     * @return album
     */
    public String getAlbum() {
        return album.get();
    }
    
    /**
     * Returns music object comment
     * @return comment
     */
    public String getComment() {
        return comment.get();
    }
    
    /**
     * Returns URI of music object as a String. Used primarily as a parameter
     * when initializing media objects
     * @return URI 
     */
    public String getURI() {
        return new File(pathName.get()).toURI().toString();
    }
    
    /**
     * Checks if the check box in the TableView is checked.
     * @return isSelected
     */
    public boolean getSelected() {
        return isSelected.get();
    }
    
    /**
     * Used for checking the isSelected status of a music object in a TableView
     * object
     * @return isSelected 
     */
    public SimpleBooleanProperty getBooleanProperty() {
        return isSelected;
    }
    
    /**
     * Sets the name of the music object
     * @param name 
     */
    public void setName(String name) {
        this.name.set(name);
    }
    
    /**
     * Sets the artist of the music object
     * @param artist 
     */
    public void setArtist(String artist) {
        this.artist.set(artist);
    }
    
    /**
     * Sets the music object's album
     * @param album 
     */
    public void setAlbum(String album) {
        this.album.set(album);
    }
    
    /**
     * Sets the music object's comment
     * @param comment 
     */
    public void setComment(String comment) {
        this.comment.set(comment);
    }
    
    /**
     * String representation of music object
     * @return toString
     */
    @Override
    public String toString() {
        return String.format("Music file: %s, Artist: %s, Album: %s", name.get(), artist.get(), album.get());
    }

}
