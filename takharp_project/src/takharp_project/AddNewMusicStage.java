package takharp_project;

import java.io.File;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * A class used to create a window used to take input from the user for
 * information from the song they have chosen using file chooser.
 * @author ParminderTakhar
 */
public class AddNewMusicStage extends Stage {

    private Music music;

    public AddNewMusicStage(File file) {
        super();
        initModality(Modality.APPLICATION_MODAL);
        createSetup(file);
    }
    /**
     * Sets up the scene for the stage. Able to input song name, artist, album,
     * and comments.
     * @param file 
     */
    private void createSetup(File file) {
        GridPane pane = new GridPane();

        TextField tfSongName = new TextField();
        TextField tfArtist = new TextField();
        TextField tfAlbum = new TextField();
        TextField tfComment = new TextField();

        Label lblHeader = new Label("Enter Song Name: ");
        Label lblName = new Label("Enter Artist: ");
        Label lblAlbum = new Label("Enter Album: ");
        Label lblComment = new Label("Enter Comment: ");

        Button btnAdd = new Button("Add Song");
        Button btnCancel = new Button("Cancel");
        
        //sets action for button, forces input validation for name
        btnAdd.setOnAction(e -> {
            if (tfSongName.getText().isEmpty() || tfSongName.getText() == null) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setTitle("Warning");
                alert.setHeaderText("Please Enter a name for the Song");
                alert.show();

            } else {
                String path = file.getPath();
                this.music = setNewMusic(tfSongName.getText(), tfArtist.getText(), tfAlbum.getText(), path, tfComment.getText());
                close();
            }
        });

        btnCancel.setOnAction(e -> {
            close();
        });


        pane.addRow(0, lblHeader, tfSongName);
        pane.addRow(1, lblName, tfArtist);
        pane.addRow(2, lblAlbum, tfAlbum);
        pane.addRow(3, lblComment, tfComment);
        pane.addRow(4, btnAdd, btnCancel);

        pane.setHgap(10);
        pane.setVgap(5);
        GridPane.setHalignment(btnAdd, HPos.RIGHT);
        pane.setPadding(new Insets(15));

        Scene scene = new Scene(pane);

        setScene(scene);
        setTitle("Add Song");

    }
    
    /**
     * Sets the music data member for the class.
     * @param name Name of song
     * @param artist Name of artist
     * @param album Name of album
     * @param path Path of music file
     * @param comment Comments of song
     * @return Music object
     */
    private Music setNewMusic(String name, String artist, String album, String path, String comment) {
        music = new Music(name, artist, album, path, comment);

        return music;
    }
    
    /**
     * Returns music from the class's data member
     * @return 
     */
    public Music getMusic() {
        return music;
    }

}
