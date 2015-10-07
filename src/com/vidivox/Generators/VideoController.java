package com.vidivox.Generators;

import com.vidivox.view.WarningDialogue;

import java.io.File;
import java.io.IOException;

/**
 * Created by matthewcanham on 24/09/15.
 */
public class VideoController {

    File videoFile;

    public VideoController(File videoFile){
        this.videoFile = videoFile;
    }

    public void mergeAudio(File audioFile, File newVideoFile){
        try {

            String audioFilePath = audioFile.toURI().toURL().getPath();
            audioFilePath = audioFilePath.replace("%20", "\\ ");

            String newVideoFilePath = newVideoFile.toURI().toURL().getPath();
            newVideoFilePath = newVideoFilePath.replace("%20", "\\ ");

            String videoFilePath = videoFile.toURI().toURL().getPath();
            videoFilePath = videoFilePath.replace("%20", "\\ ");

            newVideoFile.getParentFile().mkdirs();

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
}
