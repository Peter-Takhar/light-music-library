package takharp_project;

import java.util.ArrayList;

/**
 * This class is used as a representation of a play list containing music. It 
 * holds an array list of music objects.
 * @author ParminderTakhar
 */
public class PlayList {

    private String name;
    private String path;
    private ArrayList<Music> musicList;

    
    public PlayList(String name, String path, ArrayList<Music> musicList) {
        this(name, musicList);
        this.path = path;
    }

    public PlayList(String name, ArrayList<Music> musicList) {
        this.name = name;
        this.musicList = musicList;
    }
    
    /**
     * Returns the name of the PlayList. Used primarily for giving unique
     * identifiers for playlists stored in the Playlist directory.
     * @return name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of the playlist
     * @param name 
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Used to get the filePath of the playList since JSON is used to store 
     * each unique playlist in its own file.
     * @return 
     */
    public String getPath() {
        return path;
    }

    /**
     * This sets the file path of the playList. 
     * @param path 
     */
    public void setPath(String path) {
        this.path = path;
    }
    
    /**
     * Used to get the list of music stored inside the playList object.
     * @return 
     */
    public ArrayList<Music> getMusicList() {
        return musicList;
    }
    
    /**
     * Used to set a list of music objects for a playlist
     * @param musicList 
     */
    public void setMusicList(ArrayList<Music> musicList) {
        this.musicList = musicList;
    }

    /** A string representation of the the playList object.
     * 
     * @return 
     */
    @Override
    public String toString() {
        return name;
    }

}
