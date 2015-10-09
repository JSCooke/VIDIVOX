package com.vidivox.controller;

import com.vidivox.Generators.FestivalSpeech;
import com.vidivox.Generators.ManifestController;
import com.vidivox.Generators.VideoController;
import com.vidivox.view.WarningDialogue;
import javafx.animation.FadeTransition;
import javafx.beans.property.ListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.util.Timer;
import java.util.TimerTask;

public class MainWindowController {

    private File currentVideoLocation;

    @FXML
    private MediaView mainMediaViewer = new MediaView();
    private MediaPlayer mainMediaPlayer;

    @FXML
    private TextArea mainSpeechTextArea = new TextArea();

    @FXML
    private ToolBar videoOptionBar = new ToolBar();

    @FXML
    private ToolBar speechOptionBar = new ToolBar();

    @FXML
    private MenuBar mainMenuBar = new MenuBar();

    @FXML
    private Slider mainProgressSlider;

    @FXML
    private BorderPane mainWindow;

    @FXML
    private Slider mainVolumeSlider;

    @FXML
    private Button speechPreviewButton;

    @FXML
    private Button speechSaveButton;

    @FXML
    private Button addSpeechButton;

    @FXML
    private Button playPauseButton;

    @FXML
    private ToolBar audioOptionBar;

    @FXML
    private ListView audioList;

    @FXML
    private MenuItem openVideoButton;

    @FXML
    private void handleOpenVideoButton(){
        final FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter mp4Filter = new FileChooser.ExtensionFilter("MP4 files (.mp4)", "*.mp4");
        FileChooser.ExtensionFilter allFilter = new FileChooser.ExtensionFilter("All files", "*");
        fileChooser.getExtensionFilters().add(mp4Filter);
        fileChooser.getExtensionFilters().add(allFilter);
        File file = fileChooser.showOpenDialog(new Stage());
        try {
            ManifestController manifest = new ManifestController(CurrentDirectory.getDirectory());
            manifest.setVideo(file.getName());
            openNewVideo(file);
        }catch(NullPointerException | FileNotFoundException e){
            new WarningDialogue("You must open a project before you can add a video to it.");
        }

    }

    private void openNewVideo(File file){
        if (file != null) {
            try {

                //Get rid of the current video that is playing if there is one
                if(mainMediaPlayer != null){
                    mainMediaPlayer.dispose();
                }

                currentVideoLocation = file;
                mainMediaPlayer = new MediaPlayer(new Media(file.toURI().toString()));
                mainMediaViewer.setMediaPlayer(mainMediaPlayer);
                initaliseResizeListener();
                initalisePlayEnvironment();
                addSpeechButton.setDisable(false);
                playPauseButton.setDisable(false);

            } catch(MediaException e) {
                if( e.getType() == MediaException.Type.MEDIA_UNSUPPORTED ){
                    new WarningDialogue("Sorry, we didn't recognise that file type. Currently VIDIVOX supports MP4 files.");
                }
            }
        }
    }

    @FXML
    private void handlePlayPauseButton(){
        try {
            if(mainMediaPlayer.getStatus() == MediaPlayer.Status.PLAYING){
                mainMediaPlayer.pause();
            } else {
                mainMediaPlayer.play();
            }
        } catch (NullPointerException e){
            new WarningDialogue("You need to open a video file before you can play anything");
        }
    }

    @FXML
    private void handleSpeechPreviewButton(){
        String textToSay = mainSpeechTextArea.getText();
        FestivalSpeech festival = new FestivalSpeech(textToSay);
        festival.speak();
    }

    private void playFadingAnimation(FadeTransition t){
        //Reduces code duplication by moving this repeated code here.
        t.setFromValue(1.0);
        t.setToValue(0.0);
        t.playFromStart();
    }

    @FXML
    private void handleSaveAudioButton(){
        final FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter mp3Filter = new FileChooser.ExtensionFilter("MP3 audio (.mp3)", "*.mp3");
        FileChooser.ExtensionFilter allFilter = new FileChooser.ExtensionFilter("All files", "*");
        fileChooser.getExtensionFilters().add(mp3Filter);
        fileChooser.getExtensionFilters().add(allFilter);
        fileChooser.setTitle("Save speech to mp3 file");
        fileChooser.setInitialFileName("Dialogue.mp3");
        File file = fileChooser.showSaveDialog(new Stage());
        FestivalSpeech textToSpeak = new FestivalSpeech(mainSpeechTextArea.getText());
        textToSpeak.exportToMP3(file);
    }

    @FXML
    private void handleAddToVideoButton(){

        //Check if there is a video currently loaded
        if(currentVideoLocation == null){
            new WarningDialogue("You must open a video from the file menu before you can add speech to it.");
            return;
        }

        //Select the location of the new video that will be created.
        final FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter mp4Filter = new FileChooser.ExtensionFilter("MP4 video (.mp4)", "*.mp4");
        FileChooser.ExtensionFilter allFilter = new FileChooser.ExtensionFilter("All files", "*");
        fileChooser.getExtensionFilters().add(mp4Filter);
        fileChooser.getExtensionFilters().add(allFilter);
        fileChooser.setTitle("Save video with speech");
        fileChooser.setInitialFileName("My_New_Video.mp4");
        File newVideoFile = fileChooser.showSaveDialog(new Stage());

        //Create new audio file from text in the textbox and export it to mp3.
        //Save the location where this is saved as a file.
        File audioFile = new File("temp/tempAudioFile.mp3");

        FestivalSpeech text = new FestivalSpeech(mainSpeechTextArea.getText());
        text.exportToMP3(audioFile);

        //Create new video controller class with the current video
        VideoController vc = new VideoController(currentVideoLocation);

        //Call the mergeAudio() method
        vc.mergeAudio(audioFile, newVideoFile);

        new WarningDialogue("Great, you will now need to open the new file that you saved from the file menu.");


    }

    @FXML
    private void handleMouseMoved() {
        try {
            FadeTransition menuFT = new FadeTransition(Duration.millis(10000), mainMenuBar);
            FadeTransition videoFT = new FadeTransition(Duration.millis(10000), videoOptionBar);
            FadeTransition sliderFT = new FadeTransition(Duration.millis(10000), mainProgressSlider);
            //If the video isn't playing, or the editing bars are up, don't fade out.
            if (mainMediaPlayer.getStatus() == MediaPlayer.Status.PLAYING&&!audioOptionBar.isVisible()) {
                playFadingAnimation(menuFT);
                playFadingAnimation(videoFT);
                playFadingAnimation(sliderFT);
            } else {
                menuFT.stop();
                videoFT.stop();
                sliderFT.stop();
                mainProgressSlider.setOpacity(1.0);
                videoOptionBar.setOpacity(1.0);
                mainMenuBar.setOpacity(1.0);
        }
        }catch(NullPointerException e){
            //This means that no video is playing, so no MediaPlayer has been created yet.
            //Nothing has to be done here; this catch block is just to suppress error messages.
        }
    }

    @FXML
    private void handleAddSpeechButton() {
        initaliseTextListener();
            if (speechOptionBar.isVisible()){
                return;
            }
        speechOptionBar.setVisible(true);
        FadeTransition speechFT = new FadeTransition(Duration.millis(100), speechOptionBar);
        //Can't use the normal method here, its a fade in, not a fade out.
        speechFT.setFromValue(0.0);
        speechFT.setToValue(1.0);
        speechFT.playFromStart();
    }

    @FXML
    private void handleManageAudioButton() {
        if (audioOptionBar.isVisible()){
            return;
        }
        audioOptionBar.setVisible(true);
        FadeTransition audioFT = new FadeTransition(Duration.millis(100), audioOptionBar);
        //Can't use the normal method here, its a fade in, not a fade out.
        audioFT.setFromValue(0.0);
        audioFT.setToValue(1.0);
        audioFT.playFromStart();
    }

    @FXML
    private void handleCloseSpeechButton() {
        if (!speechOptionBar.isVisible()){
            return;
        }
        FadeTransition speechFT = new FadeTransition(Duration.millis(100), speechOptionBar);
        playFadingAnimation(speechFT);
        speechFT.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                speechOptionBar.setVisible(false);
            }
        });
    }

    @FXML
    private void handleCloseAudioButton() {
        if (!audioOptionBar.isVisible()){
            return;
        }
        if (speechOptionBar.isVisible()){
            handleCloseSpeechButton();
        }
        FadeTransition speechFT = new FadeTransition(Duration.millis(100), audioOptionBar);
        playFadingAnimation(speechFT);
        speechFT.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                audioOptionBar.setVisible(false);
            }
        });
    }

    private void initalisePlayEnvironment(){

        mainMediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                mainProgressSlider.setValue(0);
                mainProgressSlider.setMin(0);
                mainProgressSlider.setMax(mainMediaPlayer.getTotalDuration().toMillis());

                //Add a timer to check the current position of the video
                TimerTask updateSliderPosition = new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            mainProgressSlider.setValue(mainMediaPlayer.getCurrentTime().toMillis());
                        } catch (NullPointerException e) {
                            //This likely means that the window was closed while the video was open.
                            this.cancel();
                        }
                    }
                };
                final Timer durationTimer = new Timer();
                durationTimer.schedule(updateSliderPosition, 0, 100);

                //Listen for changes made to the progress slider by the user
                mainProgressSlider.valueProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {

                        //Add a threshold that will stop the video skipping when the timer updates the slider position
                        if (Math.abs((double) oldValue - (double) newValue) > 150) {
                            mainMediaPlayer.seek(new Duration((Double) newValue));
                        }

                    }
                });
                //Set up volume slider
                mainVolumeSlider.setMax(1);
                mainVolumeSlider.setValue(1);
                mainMediaPlayer.setVolume(mainVolumeSlider.getValue());
                mainVolumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                        mainMediaPlayer.setVolume((double) newValue);
                    }
                });
            }
        });
    }

    private void initaliseResizeListener(){
        //Sets MediaViewer to the size of the window on launch.
        mainMediaViewer.setFitWidth(mainWindow.getScene().getWidth());
        mainMediaViewer.setFitHeight(mainWindow.getScene().getHeight());
        //Listen for changes in the scene's width, and change the mediaview accordingly.
        mainWindow.getScene().widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                mainMediaViewer.setFitWidth(mainWindow.getScene().getWidth());
            }
        });
        //Listen for changes in the scene's height, and change the mediaview accordingly.
        mainWindow.getScene().heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                mainMediaViewer.setFitHeight(mainWindow.getScene().getHeight());
            }
        });
    }
    private void initaliseTextListener() {
        mainSpeechTextArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                if (newValue.isEmpty()){
                    speechPreviewButton.setDisable(true);
                    speechSaveButton.setDisable(true);
                }else{
                    speechPreviewButton.setDisable(false);
                    speechSaveButton.setDisable(false);
                }
                //If more than 20 words have been entered, don't allow further text entry.
                String[] words = newValue.split(" ");
                if (Array.getLength(words)>20){
                    mainSpeechTextArea.setText(oldValue);
                    new WarningDialogue("You can't enter more than 20 words, please enter less.");
                }
            }
        });
    }

    @FXML
    private void handleCloseMenuButton() {
        Stage stage = (Stage) mainWindow.getScene().getWindow();
        stage.close();
    }

    @FXML
    /**
     * Allows the user to create a new project.
     * This entails navigating to a directory, and creating a folder, then setting that folder up as the CurrentDirectory.
     */
    private void handleNewProjectButton() {
        //Perhaps clear the current values in open, then call open at the end of this.
        try {
            //Call the directory chooser, which generates a window based on the user's OS.
            DirectoryChooser dirChooser = new DirectoryChooser();
            dirChooser.setTitle("Select project location...");
            File destDirectory = dirChooser.showDialog(new Stage());
            //Add the path of the current directory.
            CurrentDirectory.addPath(destDirectory.getAbsolutePath().toString());
            //Ask for user input.
            WarningDialogue inputBox = new WarningDialogue("Please enter a name for the project:", "Project", true);
            //Create the directory.
            CurrentDirectory.makeDir();
            //Enable video options.
            if (openVideoButton.isDisable()){
                openVideoButton.setDisable(false);
            }
        }catch(NullPointerException e1) {
            //This is thrown if the user cancels the directory choosing operation.
            CurrentDirectory.interrupted();
        }
    }

    @FXML
    /**
     * Adds audio chosen by the user to the list of audio files in the project.
     */
    private void handleAddAudioButton() {
        //Move this to a setup variable
        //Will throw a NullPointerException if no project is open.
        ObservableList<String> audioFiles = FXCollections.observableArrayList();
        for (File f : CurrentDirectory.getDirectory().listFiles()) {
            if (f.getName().toString().endsWith(".mp3")) {
                audioFiles.add(f.getName().toString());
            }
        }

        final FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter mp3Filter = new FileChooser.ExtensionFilter("MP3 files (.mp3)", "*.mp3");
        FileChooser.ExtensionFilter allFilter = new FileChooser.ExtensionFilter("All files", "*");
        fileChooser.getExtensionFilters().add(mp3Filter);
        fileChooser.getExtensionFilters().add(allFilter);
        try {
            File sourceFile = fileChooser.showOpenDialog(new Stage());
            String destName = CurrentDirectory.getDirectory().getAbsolutePath().toString()+System.getProperty("file.separator")+sourceFile.getName().toString();
            File destFile = new File(destName);
            Files.copy(sourceFile.toPath(), destFile.toPath());
            audioFiles.add(sourceFile.getName().toString());
            audioList.setItems(audioFiles);
            ManifestController manifest = new ManifestController(CurrentDirectory.getDirectory());
            manifest.addAudio(sourceFile.getName().toString());
        }catch(IOException | NullPointerException e){//Both of these arise if the open operation is cancelled, such as by closing the FileChooser.
            new WarningDialogue("The operation was aborted.");
        }
    }
}