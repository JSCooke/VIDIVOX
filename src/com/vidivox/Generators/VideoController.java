package com.vidivox.Generators;

import com.vidivox.view.WarningDialogue;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Matthew Canham
 * Commented by Jayden Cooke.
 * Handles the ffmpeg calls for VIDIVOX.
 */
public class VideoController {
    //The current video.
    File videoFile;

    /**
     * Sets the current video when the object is instantiated.
     * @param videoFile - The video file.
     */
    public VideoController(File videoFile){
        this.videoFile = videoFile;
    }

    /**
     * Merges the audio file specified to the current video.
     * @param audioFile - the audio file to merge.
     * @param newVideoFile - the location of the new video.
     */
    public void mergeAudio(File audioFile, File newVideoFile){
        try {
            //Changes the path file separators.
            String audioFilePath = audioFile.toURI().toURL().getPath();
            audioFilePath = audioFilePath.replace("%20", "\\ ");
            String newVideoFilePath = newVideoFile.toURI().toURL().getPath();
            newVideoFilePath = newVideoFilePath.replace("%20", "\\ ");
            String videoFilePath = videoFile.toURI().toURL().getPath();
            videoFilePath = videoFilePath.replace("%20", "\\ ");
            //Makes any required directories. This was created before the project structure was implemented, and wouldn't be used in the current build.
            newVideoFile.getParentFile().mkdirs();
            //Calls ffmpeg for the actual merging.
            String process = "ffmpeg -i " + videoFilePath + " -i " + audioFilePath
                    + " -strict experimental -acodec aac -b:a 32k -vcodec copy -filter_complex \"[1:0]apad\" -shortest -y " + newVideoFilePath;
            ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", process);
            pb.start().waitFor();
        } catch (IOException e){
            WarningDialogue.genericError(e.getMessage());
        } catch (InterruptedException e) {
            WarningDialogue.genericError(e.getMessage());
        }
    }

    public void overlapAudio(List<File> audioFiles, File newAudioFile) {
        String process = "ffmpeg";
        int counter=0;
        for (File f:audioFiles){
            process+=" -i "+f.getAbsolutePath();
            counter++;
        }
        process+=" -filter_complex amix=inputs="+counter+":duration=first:dropout_transition=3 "+newAudioFile.getAbsolutePath();
        ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", process);
        try {
            pb.start().waitFor();
        } catch (InterruptedException e) {
            WarningDialogue.genericError(e.getMessage());
        } catch (IOException e) {
            WarningDialogue.genericError(e.getMessage());
        }
    }
}
