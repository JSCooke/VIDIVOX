package com.vidivox.controller;

import com.vidivox.Generators.FestivalSpeech;
import com.vidivox.Generators.ManifestController;
import com.vidivox.Generators.VideoController;
import com.vidivox.view.WarningDialogue;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
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
import java.net.URI;
import java.nio.file.Files;
import java.util.*;

/**
 * @author Matthew Canham, Jayden Cooke
 * This is the controller for all the buttons and controls in the VIDIVOX window.
 * This class is very large because it is the only class that can interact with the FXML file, and because VIDIVOX only has a single window.
 */
public class MainWindowController {

    //This field is legacy code in the beta version, to interact with Matthew's code to open the video.
    private File currentVideo;

    /*
     * Each of these FXML fields is separate so that the code can read the file.
     * They all represent a node on the main window.
     */
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
    private TextField mergePointArea;

    private List<Animation> playing = new ArrayList<Animation>();

    /**
     * Handles the code around opening a video.
     * Actually opening the video is delegated to the helper method, openNewVideo
     * This simply updates the project and creates the FileChooser window.
     */
    @FXML
    private void handleOpenVideoButton(){
        //Standard FileChooser window, for whatever platform this program is running on.
        final FileChooser fileChooser = new FileChooser();
        //Allows the option for only .mp4 files to be visible.
        FileChooser.ExtensionFilter mp4Filter = new FileChooser.ExtensionFilter("MP4 files (.mp4)", "*.mp4");
        fileChooser.getExtensionFilters().add(mp4Filter);
        //Allows the option for all files to be visible, as is standard.
        FileChooser.ExtensionFilter allFilter = new FileChooser.ExtensionFilter("All files", "*");
        fileChooser.getExtensionFilters().add(allFilter);
        //Gets the video file, by showing the FileChooser to the user.
        File file = fileChooser.showOpenDialog(new Stage());
        try {
            //Updates the manifest to reflect the new video.
            ManifestController manifest = new ManifestController(CurrentDirectory.getDirectory());
            manifest.setVideo(file.getName());
            //Calls the helper method to actually open the specified file.
            openNewVideo(file);
        }catch(NullPointerException | FileNotFoundException e){
            //These errors are thrown when no directory has been chosen for the project.
            //This is actually unreachable because of the GUI's design.
            new WarningDialogue("You must open a project before you can add a video to it.");
        }

    }

    /**
     * Helper method for opening the file.
     * Deals mainly with GUI components.
     * @param file - The video to open.
     */
    private void openNewVideo(File file){
        //Check the file exists.
        if (file != null) {
            try {
                //Get rid of the current video that is playing if there is one.
                if(mainMediaPlayer != null){
                    for (File f:CurrentDirectory.getDirectory().listFiles()){
                        if (f.equals(new File(new URI(mainMediaPlayer.getMedia().getSource())))){
                            Files.delete(f.toPath());
                        }
                    }
                    mainMediaPlayer.dispose();
                }
                //Copy the new video into the project.
                File destFile = new File(CurrentDirectory.getDirectory().getAbsolutePath().toString()+System.getProperty("file.separator")+file.getName());
                Files.copy(file.toPath(), destFile.toPath());
                currentVideo = file;
                //JavaFX MediaView requires a MediaPlayer object, which requires a Media object, which requires a File.
                mainMediaPlayer = new MediaPlayer(new Media(file.toURI().toString()));
                mainMediaViewer.setMediaPlayer(mainMediaPlayer);
                //Calls methods to set up various GUI effects, such as the slider, and the resizable window.
                initaliseResizeListener();
                initalisePlayEnvironment();
                //Enables video related buttons, which are disabled by default.
                addSpeechButton.setDisable(false);
                playPauseButton.setDisable(false);

            } catch(MediaException e) {
                //This occurs when a non-mp4 file is passed in, and notifies the user.
                if( e.getType() == MediaException.Type.MEDIA_UNSUPPORTED ){
                    new WarningDialogue("Sorry, we didn't recognise that file type. Currently VIDIVOX supports MP4 files.");
                }
            } catch(IOException e){
                //This shouldn't happen, because of the if statement at the beginning of this method.
                WarningDialogue.genericError("An error has occurred while copying files.");
            } catch (java.net.URISyntaxException e){
                //This is for debugging. The only code that can throw this is computer controlled.
                WarningDialogue.genericError("An error has occurred while deleting the old file.");
            }
        }
    }

    /**
     * Plays and pauses the video.
     * This is made easy by the MediaPlayer functions.
     */
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

    /**
     * Previews the speech entered in the speech area.
     * The complex code is wrapped in the FestivalSpeech class.
     * This method simply gets the text from the TextArea and passes it on.
     */
    @FXML
    private void handleSpeechPreviewButton(){
        String textToSay = mainSpeechTextArea.getText();
        FestivalSpeech festival = new FestivalSpeech(textToSay);
        festival.speak();
    }

    /**
     * This is a helper method, as most of the fading animations require this code.
     * This reduces code duplication.
     * @param t - The animation object.
     */
    private void playFadingAnimation(FadeTransition t){
        t.setFromValue(1.0);
        t.setToValue(0.0);
        t.playFromStart();
    }

    /**
     * Saves the text to speech to an MP3 file, in the destination of the user's choice.
     * Again, most of this task is done by the FestivalSpeech class.
     */
    @FXML
    private void handleSaveAudioButton(){
        final FileChooser fileChooser = new FileChooser();
        //Makes only mp3 files visible, as is the standard.
        FileChooser.ExtensionFilter mp3Filter = new FileChooser.ExtensionFilter("MP3 audio (.mp3)", "*.mp3");
        fileChooser.getExtensionFilters().add(mp3Filter);
        //Makes all files visible, as an option.
        FileChooser.ExtensionFilter allFilter = new FileChooser.ExtensionFilter("All files", "*");
        fileChooser.getExtensionFilters().add(allFilter);
        //This alters the heading in the FileChooser.
        fileChooser.setTitle("Save speech to mp3 file");
        //A default filename is set for the user.
        fileChooser.setInitialFileName("Dialogue.mp3");
        File file = fileChooser.showSaveDialog(new Stage());
        FestivalSpeech textToSpeak = new FestivalSpeech(mainSpeechTextArea.getText());
        textToSpeak.exportToMP3(file);
    }

    /**
     * Adds the audio to the video.
     */
    @FXML
    private void handleAddToVideoButton(){
        //Check if there is a video currently loaded
        if(currentVideo == null){
            new WarningDialogue("You must open a video from the file menu before you can add speech to it.");
            return;
        }
        WarningDialogue inputBox = new WarningDialogue("Please enter a valid filename for the new audio file: (A file extension will be added automatically.)","Audio",true);
        //Create new audio file from text in the textbox and export it to mp3, as a file in the project folder.
        File audioFile = new File(CurrentDirectory.getDirectory().getAbsolutePath()+System.getProperty("file.separator")+inputBox.getText()+".mp3");
        FestivalSpeech text = new FestivalSpeech(mainSpeechTextArea.getText());
        text.exportToMP3(audioFile);
        //Adds the new file to the list.
        ObservableList<String> audioFiles = audioList.getItems();
        audioFiles.add(audioFile.getName().toString());
        audioList.setItems(audioFiles);
        try {
            //Updates the manifest to reflect the new file.
            ManifestController manifest = new ManifestController(CurrentDirectory.getDirectory());
            manifest.addAudio(audioFile.getName().toString());
        }catch(FileNotFoundException e){
            //This shouldn't be reachable, as we create the file it refers to in the above statements.
            WarningDialogue.genericError("Audio file was not generated correctly.");
        }
    }

    /**
     * The method in charge of the fading out of the GUI while watching a video.
     * Every time the mouse moves, the fade is updated.
     */
    @FXML
    private void handleMouseMoved() {
        try {
            //Creates the fading animations.
            FadeTransition menuFT = new FadeTransition(Duration.millis(10000), mainMenuBar);
            FadeTransition videoFT = new FadeTransition(Duration.millis(10000), videoOptionBar);
            FadeTransition sliderFT = new FadeTransition(Duration.millis(10000), mainProgressSlider);
            //If the video isn't playing, or the editing bars are up, don't fade out.
            if (mainMediaPlayer.getStatus() == MediaPlayer.Status.PLAYING&&!audioOptionBar.isVisible()) {
                playing.add(menuFT);
                playFadingAnimation(menuFT);
                playing.add(videoFT);
                playFadingAnimation(videoFT);
                playing.add(sliderFT);
                playFadingAnimation(sliderFT);
            } else {
                //Stop all current animations.
                for (Animation a:playing){
                    a.stop();
                }
                mainProgressSlider.setOpacity(1.0);
                videoOptionBar.setOpacity(1.0);
                mainMenuBar.setOpacity(1.0);
        }
        }catch(NullPointerException e){
            //This means that no video is playing, so no MediaPlayer has been created yet.
            //Nothing has to be done here; this catch block is just to suppress error messages.
        }
    }

    /**
     * This method has little functionality, just makes the toolbars appear with a fade, rather than suddenly appearing.
     */
    @FXML
    private void handleAddSpeechButton() {
        //If the toolbar is already visible, do nothing.
        if (speechOptionBar.isVisible()){
            return;
        }
        //Stop all current animations.
        for (Animation a:playing){
            a.stop();
        }
        //The text listener handles code to do with text input. It isn't initialised until the toolbar is visible.
        initaliseTextToSpeechListener();
        speechOptionBar.setVisible(true);
        FadeTransition speechFT = new FadeTransition(Duration.millis(100), speechOptionBar);
        //Can't use the normal method here, its a fade in, not a fade out.
        speechFT.setFromValue(0.0);
        speechFT.setToValue(1.0);
        speechFT.playFromStart();
    }

    /**
     * This method has little functionality, just makes the toolbars appear with a fade, rather than suddenly appearing.
     */
    @FXML
    private void handleManageAudioButton() {
        //If the toolbar is already visible, do nothing.
        if (audioOptionBar.isVisible()){
            return;
        }
        //Stop all current animations.
        for (Animation a:playing){
            a.stop();
        }
        audioOptionBar.setVisible(true);
        initialiseMergePointListener();
        FadeTransition audioFT = new FadeTransition(Duration.millis(100), audioOptionBar);
        //Can't use the normal method here, its a fade in, not a fade out.
        audioFT.setFromValue(0.0);
        audioFT.setToValue(1.0);
        audioFT.playFromStart();
    }

    /**
     * Like the functions to make toolbars visible, this function simply makes the toolbar fade gradually, instead of sharply disappearing.
     */
    @FXML
    private void handleCloseSpeechButton() {
        //If the toolbar isn't visible, do nothing.
        if (!speechOptionBar.isVisible()){
            return;
        }
        //Fade out.
        FadeTransition speechFT = new FadeTransition(Duration.millis(100), speechOptionBar);
        playFadingAnimation(speechFT);
        //Change the visibility after the animation finishes.
        speechFT.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                speechOptionBar.setVisible(false);
            }
        });
    }

    /**
     * Like the functions to make toolbars visible, this function simply makes the toolbar fade gradually, instead of sharply disappearing.
     */
    @FXML
    private void handleCloseAudioButton() {
        //If the toolbar isn't visible, do nothing.
        if (!audioOptionBar.isVisible()){
            return;
        }
        //Makes the speech toolbar disappear too, if its visible.
        if (speechOptionBar.isVisible()){
            handleCloseSpeechButton();
        }
        //Fade out.
        FadeTransition speechFT = new FadeTransition(Duration.millis(100), audioOptionBar);
        playFadingAnimation(speechFT);
        //Change the visibility after the animation finishes.
        speechFT.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                audioOptionBar.setVisible(false);
            }
        });
    }

    /**
     * Helper method to handle the sliders for volume and progress.
     * This would be too large to fit in the method to open the video.
     */
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

    /**
     * Helper method to set up window resizing. This would be too large to fit in the method to open the video.
     */
    private void initaliseResizeListener(){
        //Sets MediaViewer to the size of the window on launch.
        mainMediaViewer.setFitWidth(mainWindow.getScene().getWidth());
        mainMediaViewer.setFitHeight(mainWindow.getScene().getHeight());
        //Listen for changes in the scene's width, and change the MediaView accordingly.
        mainWindow.getScene().widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                mainMediaViewer.setFitWidth(mainWindow.getScene().getWidth());
            }
        });
        //Listen for changes in the scene's height, and change the MediaView accordingly.
        mainWindow.getScene().heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                mainMediaViewer.setFitHeight(mainWindow.getScene().getHeight());
            }
        });
    }

    /**
     * This handles minor GUI functions, like disabling previews of empty strings and enforcing a word limit.
     * This is called whenever the TextArea becomes visible, but is too long for that method.
     */
    private void initaliseTextToSpeechListener() {
        mainSpeechTextArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                //If the text is empty, disable text-related buttons. If not, enable them.
                if (newValue.isEmpty()) {
                    speechPreviewButton.setDisable(true);
                    speechSaveButton.setDisable(true);
                } else {
                    speechPreviewButton.setDisable(false);
                    speechSaveButton.setDisable(false);
                }
                //If more than 20 words have been entered, don't allow further text entry, and inform the user.
                String[] words = newValue.trim().split(" ");
                if (Array.getLength(words) > 20) {
                    mainSpeechTextArea.setText(oldValue);
                    new WarningDialogue("You can't enter more than 20 words, please enter less.");
                }
            }
        });
    }

    /**
     * This creates the listener for the validation of the starting point text field.
     */
    private void initialiseMergePointListener() {
        mergePointArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed (ObservableValue < ?extends String > observableValue, String oldValue, String newValue){
                //If the string is an integer, its allowed. Otherwise, prevent text entry.
                //.matches code from http://stackoverflow.com/questions/5439529/determine-if-a-string-is-an-integer-in-java
                if (!newValue.matches("\\d+")){
                    mergePointArea.setText(oldValue);
                    new WarningDialogue("Please only enter a whole number of seconds.");
                }
            }
        });
    }
    /**
     * Closes the window when the close menu object is pressed.
     */
    @FXML
    private void handleCloseMenuButton() {
        Stage stage = (Stage) mainWindow.getScene().getWindow();
        stage.close();
    }

    /**
     * Allows the user to create a new project.
     * This entails navigating to a directory, and creating a folder, then setting that folder up as the CurrentDirectory.
     */
    @FXML
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
            CurrentDirectory.addName(inputBox.getText());
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


    /**
     * Adds audio chosen by the user to the list of audio files in the project.
     */
    @FXML
    private void handleAddAudioButton() {
        //Could move this to a setup method.
        //Will throw a NullPointerException if no project is open.
        ObservableList<String> audioFiles = FXCollections.observableArrayList();
        for (File f : CurrentDirectory.getDirectory().listFiles()) {
            if (f.getName().toString().endsWith(".mp3")) {
                audioFiles.add(f.getName().toString());
            }
        }
        //Gets the audio file.
        final FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter mp3Filter = new FileChooser.ExtensionFilter("MP3 files (.mp3)", "*.mp3");
        fileChooser.getExtensionFilters().add(mp3Filter);
        FileChooser.ExtensionFilter allFilter = new FileChooser.ExtensionFilter("All files", "*");
        fileChooser.getExtensionFilters().add(allFilter);
        File sourceFile = fileChooser.showOpenDialog(new Stage());
        try {
            //Copies source file into the project.
            String destName = CurrentDirectory.getDirectory().getAbsolutePath().toString()+System.getProperty("file.separator")+sourceFile.getName().toString();
            File destFile = new File(destName);
            Files.copy(sourceFile.toPath(), destFile.toPath());
            //Adds the copied file to the list visible to the user.
            audioFiles.add(sourceFile.getName().toString());
            audioList.setItems(audioFiles);
            //Updates the manifest to reflect the new file.
            ManifestController manifest = new ManifestController(CurrentDirectory.getDirectory());
            manifest.addAudio(sourceFile.getName().toString());
        }catch(NullPointerException e){//Both of these arise if the open operation is cancelled, such as by closing the FileChooser.
            new WarningDialogue("The operation was aborted.");
        }catch(IOException e){
            new WarningDialogue("You already added that file"); //This will need changing when I handle files at different times.
        }
    }

    /**
     * Removes audio files from the list, manifest and directory.
     */
    @FXML
    private void handleRemoveAudioButton(){
        //Get all the items on the list, and all the selected items.
        try {
            ObservableList<String> selected = audioList.getSelectionModel().getSelectedItems();
            ManifestController manifest = new ManifestController(CurrentDirectory.getDirectory());
            //Notify the user if they haven't selected an item, and do nothing else.
            if (selected.isEmpty()) {
                new WarningDialogue("Please select an item, then press Remove");
                return;
            }
            List<String> fileNames = manifest.getAudio();
            List<File> files = new LinkedList<>();
            //Creates a list of Files from the filenames in the manifest.
            for (String s:fileNames){
                files.add(new File(CurrentDirectory.getDirectory().getName()+System.getProperty("file.separator")+s));
            }
            //Delete the selected files from the project folder.
            try {
                for (File f : files) {
                    if (selected.contains(f.getName())) {
                        Files.deleteIfExists(f.toPath());
                    }
                }
            }catch(IOException e){
                new WarningDialogue("A file you were trying to delete was not found. This shouldn't affect your project.");
            }
            //Update the manifest.
            manifest.removeAudio(selected);
            //Update the list.
            ObservableList<String> audioFiles = manifest.getAudio();
            //If the list from the manifest isn't empty, update the ListView to show it.
            if (!audioFiles.isEmpty()) {
                audioList.setItems(audioFiles);
            //If it is empty, then clear the list.
            }else{
                audioList.getItems().clear();
            }
        }catch(FileNotFoundException e){
            WarningDialogue.genericError("A manifest error occurred.");
        }
    }

    /**
     * Opens a previously created project.
     * This could benefit from a SwingWorker thread (or, more precisely, the JavaFX equivalent), in case the loop needs to run many times.
     * A progress bar may also be an alternative.
     */
    @FXML
    private void handleOpenProjectButton() {
        //Ask the user to find the project.
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select project directory...");
        File loadDirectory = dirChooser.showDialog(new Stage());
        //Check if any of the files is a manifest.
        boolean hasManifest = false;
        for (File f:loadDirectory.listFiles()){
            if(f.getName().endsWith(".vvx")){
                hasManifest = true;
                break;
            }
        }
        //If all files are checked and no manifest is found, return.
        if (!hasManifest){
            new WarningDialogue("Sorry, that is not a valid project.\nOnly folders with manifests (.vvx) are valid.");
            return;
        }
        //Update the CurrentDirectory class.
        CurrentDirectory.setDirectory(loadDirectory);
        ManifestController manifest = new ManifestController(CurrentDirectory.getDirectory());
        try {
            File video = new File(CurrentDirectory.getDirectory() + System.getProperty("file.separator") + manifest.getVideo());
            openNewVideo(video);
        }catch(FileNotFoundException e){
            new WarningDialogue("No video was found for this project. Add one to get started!");
            //If there is no video, the program won't allow any audio. To avoid further errors, we terminate the method here.
            return;
        }
        try {
            List<String> audio = manifest.getAudio();
            //Populates the list with the files
            ObservableList<String> audioFiles = FXCollections.observableArrayList();
            for (File f : CurrentDirectory.getDirectory().listFiles()) {
                if (audio.contains(f.getName())) {
                    audioFiles.add(f.getName());
                }
            }
            audioList.setItems(audioFiles);

        }catch(FileNotFoundException e){
            //This means the manifest doesn't exist, and isn't reachable. (It would have caused an exception earlier)
            WarningDialogue.genericError("Manifest not found.");
            return;
        }
        //Enable video options.
        if (openVideoButton.isDisable()){
            openVideoButton.setDisable(false);
        }
    }

    /**
     * This method merges the audio in the selected audio files with the videos.
     * Due to difficulties in ffmpeg calls, this is currently functioning to assignment 3 standards.
     * This is a known, and serious, issue, and is being remedied.
     */
    @FXML
    private void handleMergeAudioButton() {
        try {
            new WarningDialogue("Beginning merge process...\nThis may take some time.");
            ManifestController manifest = new ManifestController(CurrentDirectory.getDirectory());
            ObservableList<String> selected = audioList.getSelectionModel().getSelectedItems();
            File videoFile = new File(CurrentDirectory.getDirectory().getName() + System.getProperty("file.separator") + manifest.getVideo());
            VideoController videoController = new VideoController(videoFile);
            for (String s : selected) {
                videoController.mergeAudio(new File(CurrentDirectory.getDirectory().getName() + System.getProperty("file.separator") + s),videoFile);
            }
        }catch (FileNotFoundException e) {
            //The way the GUI is designed, this is unreachable.
            WarningDialogue.genericError("No video file was found.");
        }
    }
}