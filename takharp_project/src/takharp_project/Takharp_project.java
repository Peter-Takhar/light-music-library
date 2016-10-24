
package takharp_project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider; 
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import static javafx.scene.control.TableView.UNCONSTRAINED_RESIZE_POLICY;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * This class implements the interface of the music player/library and its 
 * logic. The user will be to play music, add music, update the contents of 
 * music, delete music, and create play lists. 
 * @author ParminderTakhar
 */
public class Takharp_project extends Application {
    
    //used across most methods in the project
    //only one mediaPlayer is created, the reference is changed depending on 
    //circumstance
    static MediaPlayer mediaPlayer;
    //main Library file where the main library data is stored, playlist
    //directory is stored and music files are copied to
    static File mainDir = new File("MusicLibrary");
    //directory where playlists data are stored
    static File playlistDir = new File(mainDir.getPath() + "\\PlayList");
    //stores playlists in the GUI
    static ComboBox<PlayList> comboList = new ComboBox<>();
    //used to store music in the tableview; it is mainly used by changing its 
    //reference depending on circumstance
    static ObservableList<Music> mainList;
    //like the observablist and mediaplayer, its reference is changed 
    //depending on circumstance
    static Media media;

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        //creates directory if not there already
        mainDir.mkdir();    
        playlistDir.mkdir();

        BorderPane main = createMainScreen();

        Scene scene = new Scene(main, 800, 500);

        primaryStage.setTitle("Music Player/Library");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    /**
     * Used to create the GUI for the music library/player
     * @return
     * @throws FileNotFoundException
     * @throws ParseException
     * @throws IOException 
     */
    public static BorderPane createMainScreen() throws FileNotFoundException, ParseException, IOException {
        BorderPane main = new BorderPane();
        TableView<Music> table = new TableView<>();

        Label label = new Label("Song List");

        table.setEditable(true);
        
        //created columns for the tableview, and added listener to detect edits
        //to the columns
        TableColumn<Music, String> tcName = new TableColumn<>("Song Name");
        tcName.setCellValueFactory(
                new PropertyValueFactory<>("name"));
        tcName.setCellFactory(TextFieldTableCell.<Music>forTableColumn());
        tcName.setOnEditCommit((CellEditEvent<Music, String> ev) -> {
            ev.getTableView().getItems().get(ev.getTablePosition().getRow()).setName(ev.getNewValue());
            System.out.println(ev.getTableView().getItems().get(ev.getTablePosition().getRow()).getName());

            try {
                storeTableData(table);
            } catch (IOException ex) {
                ex.toString();
            }
        });

        TableColumn<Music, String> tcArtist = new TableColumn<>("Artist");
        tcArtist.setCellValueFactory(
                new PropertyValueFactory<>("artist"));
        tcArtist.setCellFactory(TextFieldTableCell.<Music>forTableColumn());
        tcArtist.setOnEditCommit((CellEditEvent<Music, String> ev) -> {
            ev.getTableView().getItems().get(ev.getTablePosition().getRow()).setArtist(ev.getNewValue());

            try {
                storeTableData(table);
            } catch (IOException ex) {
                ex.toString();
            }
        });

        TableColumn<Music, String> tcAlbum = new TableColumn<>("Album");
        tcAlbum.setCellValueFactory(
                new PropertyValueFactory<>("album"));
        tcAlbum.setCellFactory(TextFieldTableCell.<Music>forTableColumn());
        tcAlbum.setOnEditCommit((CellEditEvent<Music, String> ev) -> {
            ev.getTableView().getItems().get(ev.getTablePosition().getRow()).setAlbum(ev.getNewValue());

            try {
                storeTableData(table);
            } catch (IOException ex) {
                ex.toString();
            }
        });

        TableColumn<Music, String> tcComment = new TableColumn<>("Comment");
        tcComment.setCellValueFactory(
                new PropertyValueFactory<>("comment"));
        tcComment.setCellFactory(TextFieldTableCell.<Music>forTableColumn());
        tcComment.setOnEditCommit((CellEditEvent<Music, String> ev) -> {
            ev.getTableView().getItems().get(ev.getTablePosition().getRow()).setComment(ev.getNewValue());

            try {
                storeTableData(table);
            } catch (IOException ex) {
                ex.toString();
            }
        });
        //made a column for checkboxes - used to determine what to add to playlist
        //and what to delete
        TableColumn<Music, Boolean> tcSelected = new TableColumn<>("Selection");
        tcSelected.setMinWidth(80);
        tcSelected.setCellValueFactory(e -> {
            //listener for checkbox
            return ((Music) e.getValue()).getBooleanProperty();
        });

        tcSelected.setCellFactory(CheckBoxTableCell.<Music>forTableColumn(tcSelected));
        
        //without this line of code, tableview adds an extra empty column, which
        //has unlimited size
        table.setColumnResizePolicy(UNCONSTRAINED_RESIZE_POLICY);

        Slider slVolume = new Slider();
        slVolume.setMaxWidth(150);
        slVolume.setMinWidth(30);
        slVolume.setValue(50);

        try {
            //this whole block initializes the content of the tableview and mediaplayer
            mainList = FXCollections.observableList(readLibraryData());

            table.setItems(mainList);

            comboList.getItems().add(new PlayList("Library", readLibraryData()));
            
            //using selectfirst is a failsafe, if a user presses play without 
            //selecting a song, it will play the first song and not cause a null
            //pointer exceptoin
            comboList.getSelectionModel().selectFirst();
            if (mainList != null && !mainList.isEmpty()) {
                media = new Media(mainList.get(0).getURI());
                mediaPlayer = new MediaPlayer(media);

                table.getSelectionModel().selectFirst();

            }
            readPlayListData();
            
            //this catch block runs if it finds no files or the JSON parser 
            //throws an error
        } catch (FileNotFoundException | ParseException ex) {
            mainList = FXCollections.observableArrayList();
            readPlayListData();
            addPlaylist("Library", mainList);
            comboList.getSelectionModel().select(0);
            table.setItems(mainList);
            System.out.println("FileNotFound");
        }

        //this block sets the size of the columns
        tcSelected.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        tcName.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        tcAlbum.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        tcArtist.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        tcComment.prefWidthProperty().bind(table.widthProperty().multiply(0.3));

        table.getColumns().addAll(tcSelected, tcName, tcArtist, tcAlbum, tcComment);

        VBox vbox = new VBox(label, table);

        Button btnPlay = new Button("Play");
        Button btnStop = new Button("Stop");
        Button btnAdd = new Button("Add");
        Button btnDelete = new Button("Delete");
        Button btnCreateList = new Button("Create PlayList");
        Label lblPlayList = new Label("Playlist Zone");
        Label lblVolume = new Label("Volume: ");

        lblPlayList.setTextAlignment(TextAlignment.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setAlignment(Pos.CENTER);
        label.setWrapText(true);
        label.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
        lblPlayList.setFont(Font.font("Verdana", FontWeight.BOLD, 12));

        HBox hbox = new HBox(btnAdd, btnPlay, btnStop, btnDelete, slVolume);
        HBox vlHbox = new HBox(lblVolume, slVolume);
        VBox hboxControls = new VBox(hbox, vlHbox);
        hbox.setAlignment(Pos.CENTER);
        vlHbox.setAlignment(Pos.CENTER);

        VBox playListVbox = new VBox(lblPlayList, btnCreateList, comboList);
        
        //delete button listner
        btnDelete.setOnAction(e -> {
            try {
                deleteMusic(table);
                mediaPlayer.stop();
            } catch (IOException ex) {
                System.out.println(ex.toString());
            } catch (NullPointerException ignored) {

            }
        });

        //adding button listner
        btnAdd.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open MP3 file");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("MP3 Files", "*.mp3");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showOpenDialog(new Stage());

            if (file != null) {
                try {
                    File overwriteFile = new File(mainDir.getPath() + "\\" + file.getName());
                    Files.copy(file.toPath(), overwriteFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                } catch (IOException ex) {
                    System.out.println("Failed to copy");
                    System.out.println(ex.toString());
                }

                AddNewMusicStage addInfo = new AddNewMusicStage(file);
                addInfo.showAndWait();

                if (addInfo.getMusic() != null) {
                    table.getItems().add(addInfo.getMusic());

                    if (comboList.getValue() != null) {
                        //this if statement addes to library
                        if (comboList.getValue().getName().compareTo("Library") == 0) {
                            try {
                                storeTableData(table);
                            } catch (IOException ex) {
                                System.out.println(ex.toString());
                            }
                        }
                    } else {
                        try {
                            //if comboList is null, still will store to the library
                            storeTableData(table);
                        } catch (IOException ex) {
                            System.out.println(ex.toString());
                        }
                    }

                }
            }

        }
        );
        
        //play button listener
        btnPlay.setOnAction(e
                -> {
            try {
                Music music = table.getSelectionModel().getSelectedItem();

                if (music == null && table.getItems().size() > 0) {
                    music = table.getItems().get(0);
                }
                
                //this if statemetn will run if the user presses the play button again 
                //with no change in song
                if (media != null && music.getURI().compareTo(media.getSource()) == 0) {
                    switch (mediaPlayer.getStatus()) {
                        case PAUSED:
                            btnPlay.setText("Play");
                            mediaPlayer.play();
                            break;
                        case READY:
                            mediaPlayer.play();
                            break;
                        case PLAYING:
                            btnPlay.setText("Paused");
                            mediaPlayer.pause();
                            break;
                        case STOPPED:
                            mediaPlayer.play();
                            break;
                        default:
                            mediaPlayer.play();
                            break;

                    }
                } else {
                    //used when new song is selected
                    media = new Media(music.getURI());

                    if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                        mediaPlayer.stop();
                    }
                    mediaPlayer = new MediaPlayer(media);
                    mediaPlayer.volumeProperty().bind(slVolume.valueProperty().divide(100));
                    btnPlay.setText("Play");
                    mediaPlayer.play();

                    mediaPlayer.setOnEndOfMedia(new Runnable() {
                        @Override
                        public void run() {
                            mediaPlayer.stop();
                            if (mainList.iterator().hasNext()) {
                                table.getSelectionModel().selectNext();
                                Music music = table.getSelectionModel().getSelectedItem();
                                Media media = new Media(music.getURI());
                                mediaPlayer = new MediaPlayer(media);
                                mediaPlayer.play();
                            }

                        }
                    });
                }
            } catch (NullPointerException ignored) {
                //used to ignore if mediaplayer is not initialized
            }

        }
        );
        
        //stop button listener
        btnStop.setOnAction(e
                -> {
            try {
                
                mediaPlayer.stop();
                btnPlay.setText("Play");
            } catch (NullPointerException ignored) {

            }

        }
        );
        
        //create playlist listener
        btnCreateList.setOnAction(e
                -> {
            mainList = createPlayList(table);
            System.out.println(mainList);
            if (mainList != null && mainList.size() > 0) {

                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Creating Playlist");
                dialog.setHeaderText("Creating Playlist");
                dialog.setContentText("Enter the name of your Playlist");

                Optional<String> result = dialog.showAndWait();

                if (result.isPresent() && result.get().length() > 0) {
                    try {
                        //stores playlist 
                        addPlaylist(result.get(), mainList);
                        PlayList testList = new PlayList(result.get(), new ArrayList<>(mainList));
                        comboList.getItems().set(0, new PlayList("Library", readLibraryData()));
                        comboList.getItems().add(testList);

                    } catch (IOException ex) {
                        System.out.println(ex.toString());
                    }
                } else {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.initModality(Modality.APPLICATION_MODAL);
                    alert.setTitle("Warning");
                    alert.setHeaderText("Playlist cannot be created.");
                    alert.setContentText("Playlist must have a name.");
                    alert.show();
                }
            } else {
                //forces user validation by forcing a name to be inputted
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setTitle("Warning");
                alert.setHeaderText("Playlist cannot be created.");
                alert.setContentText("You much have atleast one song in the library.");
                alert.show();
            }

        }
        );
        
        //comboList listener
        comboList.setOnAction(
                (ActionEvent e) -> {
                    mainList = FXCollections.observableList(comboList.getValue().getMusicList());

                    if (comboList.getValue().getName().compareTo("Library") != 0) {

                        btnAdd.setDisable(true);
                        btnDelete.setDisable(true);
                        btnCreateList.setDisable(true);
                    } else {
                        try {
                            mainList = FXCollections.observableList(readLibraryData());
                        } catch (IOException ex) {
                            System.out.println(ex.toString());
                        }
                        btnAdd.setDisable(false);
                        btnDelete.setDisable(false);
                        btnCreateList.setDisable(false);
                    }
                    table.setItems(mainList);
                }
        );

        main.setLeft(playListVbox);

        main.setCenter(vbox);

        main.setTop(hboxControls);

        vbox.setPadding(new Insets(10));
        hboxControls.setPadding(new Insets(10, 0, 0, 0));

        playListVbox.setPadding(new Insets(15, 10, 0, 10));

        return main;
    }
    
    /**
     * This method is used to take the observable list in the table view and store
     * it in the main library text file in the Music Library directory
     * @param table
     * @throws IOException 
     */
    public static void storeTableData(TableView table) throws IOException {
        ObservableList<Music> newlist = table.getItems();
        System.out.println(table.getItems());
        JSONArray musicList = new JSONArray();
        int i = 0;
        for (Music key : newlist) {
            JSONObject music = new JSONObject();
            music.put("song_name", key.getName());
            music.put("artist", key.getArtist());
            music.put("album", key.getAlbum());
            music.put("pathName", key.getPathName());
            music.put("pathName", key.getPathName());
            music.put("comment", key.getComment());
            musicList.add(music);
        }

        JSONObject root = new JSONObject();
        root.put("musicFiles", musicList);
        String path = mainDir.getPath() + "\\library.txt";

        File library = new File(path);

        try (FileWriter output = new FileWriter(library)) {
            output.write(root.toJSONString());
        }

    }

    /**
     * Used to read the main library text located in the music library directory.
     * A arraylist of music is returned to be either converted to an observablelist
     * for the tableview or for creating a playlist object.
     * @return
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static ArrayList<Music> readLibraryData() throws FileNotFoundException, IOException {
        ArrayList<Music> musicList = new ArrayList<>();

        File library = new File(mainDir.getPath() + "\\library.txt");

        if (!library.exists() && !library.isDirectory()) {
            library.createNewFile();
        }

        Scanner fileInput = new Scanner(library);

        StringBuilder jsonBuilder = new StringBuilder();
        while (fileInput.hasNextLine()) {
            jsonBuilder.append(fileInput.nextLine());
        }
        try {
            JSONParser parser = new JSONParser();

            JSONObject rootObject = (JSONObject) parser.parse(jsonBuilder.toString());

            JSONArray musicArray = (JSONArray) rootObject.get("musicFiles");

            for (int i = 0; i < musicArray.size(); i++) {
                JSONObject obj = (JSONObject) musicArray.get(i);

                String name = (String) obj.get("song_name");
                String artist = (String) obj.get("artist");
                String album = (String) obj.get("album");
                String pathName = (String) obj.get("pathName");
                String comment = (String) obj.get("comment");

                musicList.add(new Music(name, artist, album, pathName, comment));
            }
        } catch (ParseException ex) {

        }

        return musicList;

    }
    
    /**
     * Used to create a playlist object. It does not create the file, only an 
     * observable. It is primarily used to create a list depending on which
     * checkboxes were clicked in the table view
     * @param table
     * @return 
     */
    private static ObservableList<Music> createPlayList(TableView table) {

        ArrayList<Music> list = new ArrayList<>(table.getItems());
        ObservableList<Music> mList = FXCollections.observableArrayList();

        for (Music key : list) {
            if (key.getSelected()) {
                mList.add(key);
            }
        }

        return mList;
    }

    /**
     * This method simply stores a playlist object and its arraylist of music to
     * a text file using JSON.
     * @param name
     * @param list
     * @throws IOException 
     */
    private static void addPlaylist(String name, ObservableList<Music> list) throws IOException {
        ObservableList<Music> mlist = list;
        JSONArray playlistList = new JSONArray();
        int i = 0;
        for (Music key : mlist) {
            JSONObject music = new JSONObject();
            music.put("song_name", key.getName());
            music.put("artist", key.getArtist());
            music.put("album", key.getAlbum());
            music.put("pathName", key.getPathName());
            music.put("pathName", key.getPathName());
            music.put("comment", key.getComment());
            playlistList.add(music);
        }

        JSONObject root = new JSONObject();
        root.put("name", name);
        root.put("musicFiles", playlistList);

        File library = new File(mainDir.getPath() + "\\PlayList\\" + name + ".txt");

        try (FileWriter output = new FileWriter(library)) {
            output.write(root.toJSONString());
        }

    }

    /**
     * This method is the one that reads the playlist files stored in the playlist
     * directory. It also sets the combo box and its values.
     * @throws FileNotFoundException
     * @throws ParseException 
     */
    public static void readPlayListData() throws FileNotFoundException, ParseException {
        File[] playListListings = playlistDir.listFiles();
        System.out.println(Arrays.toString(playListListings));

        for (File child : playListListings) {
            Scanner fileInput = new Scanner(child);

            StringBuilder jsonBuilder = new StringBuilder();
            while (fileInput.hasNextLine()) {
                jsonBuilder.append(fileInput.nextLine());
            }

            JSONParser parser = new JSONParser();

            JSONObject rootObject = (JSONObject) parser.parse(jsonBuilder.toString());

            String namePlayList = (String) rootObject.get("name");

            JSONArray musicArray = (JSONArray) rootObject.get("musicFiles");

            ArrayList<Music> musicList = new ArrayList<>();

            for (int i = 0; i < musicArray.size(); i++) {
                JSONObject obj = (JSONObject) musicArray.get(i);

                String name = (String) obj.get("song_name");
                String artist = (String) obj.get("artist");
                String album = (String) obj.get("album");
                String pathName = (String) obj.get("pathName");
                String comment = (String) obj.get("comment");

                musicList.add(new Music(name, artist, album, pathName, comment));

            }

            PlayList playList = new PlayList(namePlayList, child.getPath(), musicList);

            if (namePlayList.compareTo("Library") != 0) {
                comboList.getItems().add(playList);
            }
        }

    }
    /**
     * Pretty self explanatory. Based on what was checked in the tableview, it will
     * "delete" music, by returning an observable list with the non selected elments.
     * @param table
     * @return
     * @throws IOException 
     */
    private static ObservableList<Music> deleteMusic(TableView table) throws IOException {
        // could not just use the array list directly, as it would cuase a concurrency 
        //exception as were modifying its files while it was in use (mediaPlayer being
        //initialized with a media object)
        ArrayList<Music> list = new ArrayList<>(table.getItems());
        ObservableList<Music> mList = FXCollections.observableArrayList();

        for (Music key : list) {
            if (!key.getSelected()) {
                mList.add(key);
            }
        }
        table.setItems(mList);
        comboList.getValue().setMusicList(new ArrayList<>(mList));
        if (comboList.getValue().getName().compareTo("Library") == 0) {
            storeTableData(table);
        } else {
            addPlaylist(comboList.getValue().getName(), mList);
        }

        return mList;

    }

}
