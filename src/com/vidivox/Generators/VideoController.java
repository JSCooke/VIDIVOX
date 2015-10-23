package com.vidivox.Generators;

import com.vidivox.controller.CurrentDirectory;
import com.vidivox.view.WarningDialogue;

import java.io.File;
import java.io.IOException;

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
     *
     * @param videoFile - The video file.
     */
    public VideoController(File videoFile) {
        this.videoFile = videoFile;
    }

    /**
     * Merges the audio file specified to the current video.
     *
     * @param audioFile    - the audio file to merge.
     * @param newVideoFile - the location of the new video.
     */
    public void mergeAudio(File audioFile, File newVideoFile) {
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
        } catch (IOException e) {
            WarningDialogue.genericError(e.getMessage());
        } catch (InterruptedException e) {
            WarningDialogue.genericError(e.getMessage());
        }
    }

    /**
     * Adds silence to the start of the audio, so that the sound the user has added appears at the point they specified.
     * @param timeToAdd - The amount of seconds before the audio starts
     * @param audioFile - The file to be extended
     */
    public File padAudio(int timeToAdd, File audioFile) {
        try {
            //Creates an object representing the file the ffmpeg calls will make.
            File padded = new File(CurrentDirectory.getDirectory().getAbsolutePath() + System.getProperty("file.separator") + "pad" + timeToAdd + audioFile.getName());
            String process1 = "ffmpeg -f lavfi -i aevalsrc=0:0:0:0:0:0::duration=1 silence.mp3";
            String process2 = "ffmpeg -i concat:\"silence.mp3|" + audioFile.getName() + "\" -codec copy " + padded.getName();
            ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", process1);
            pb.directory(CurrentDirectory.getDirectory());
            pb.start().waitFor();
            pb = new ProcessBuilder("/bin/sh", "-c", process2);
            pb.start().waitFor();
            return padded;
        } catch (IOException | InterruptedException e) {
            //Occurs when invalid files are passed in. There is no way for the user to actually do this through the GUI.
            WarningDialogue.genericError(e.getMessage());
            //Returning the original file unaltered will cause erroneous output, but won't terminate the program.
            return audioFile;
        }
    }
}
