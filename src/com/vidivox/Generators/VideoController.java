package com.vidivox.Generators;

import com.vidivox.controller.CurrentDirectory;
import com.vidivox.view.WarningDialogue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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
     * @param oldVideoFile - the current video.
     */
    public File mergeAudio(File audioFile, File oldVideoFile) {
        try {
            File newVideoFile = new File(CurrentDirectory.getDirectory().getAbsolutePath()+System.getProperty("file.separator")+"output.mp4");
            //Calls ffmpeg for the actual merging, and creates the new video file.
            //FFMPEG from http://superuser.com/questions/712862/ffmpeg-add-background-audio-to-video-but-not-completely-muting-the-original-audi
            String process = "ffmpeg -i "+audioFile.getName()+" -i "+oldVideoFile.getName()+" -filter_complex \"[0:a][1:a]amerge,pan=stereo:c0<c0+c2:c1<c1+c3[out]\" -map 1:v -map \"[out]\" -c:v copy -c:a libfdk_aac output.mp4\n";
            ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", process);
            pb.start().waitFor();
            return newVideoFile;
        } catch (IOException | InterruptedException e) {
            WarningDialogue.genericError(e.getMessage());
            return oldVideoFile;
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
            //FFMPEG call from http://superuser.com/questions/579008/add-1-second-of-silence-to-audio-through-ffmpeg
            String process1 = "ffmpeg -f lavfi -i aevalsrc=0:0:0:0:0:0::duration="+timeToAdd+" silence.mp3";
            //Concept from http://stackoverflow.com/questions/7333232/concatenate-two-mp4-files-using-ffmpeg
            String process2 = "touch list.txt";
            String process3 = "echo \"file '"+CurrentDirectory.getDirectory().getAbsolutePath()+ System.getProperty("file.separator")+"silence.mp3'\" >> list.txt";
            String process4 = "echo \"file '"+audioFile.getAbsolutePath()+"'\" >> list.txt";
            String process5 = "ffmpeg -f concat -i list.txt -c copy "+padded.getName();
            ProcessBuilder pb1 = new ProcessBuilder("/bin/sh", "-c", process1);
            pb1.directory(CurrentDirectory.getDirectory());
            pb1.start();
            ProcessBuilder pb2 = new ProcessBuilder("/bin/sh", "-c", process2);
            pb2.directory(CurrentDirectory.getDirectory());
            pb2.start();
            ProcessBuilder pb3 = new ProcessBuilder("/bin/sh", "-c", process3);
            pb3.directory(CurrentDirectory.getDirectory());
            pb3.start();
            ProcessBuilder pb4 = new ProcessBuilder("/bin/sh", "-c", process4);
            pb4.directory(CurrentDirectory.getDirectory());
            pb4.start();
            ProcessBuilder pb5 = new ProcessBuilder("/bin/sh", "-c", process5);
            pb5.directory(CurrentDirectory.getDirectory());
            pb5.start();
            //Delete temporary files made during the combining process.
            /*
            File listFile = new File(CurrentDirectory.getDirectory()+System.getProperty("file.separator")+"list.txt");
            listFile.delete();
            */
            File silence = new File(CurrentDirectory.getDirectory()+System.getProperty("file.separator")+"silence.mp3");
            Files.delete(silence.toPath());
            File list = new File(CurrentDirectory.getDirectory()+System.getProperty("file.separator")+"list.txt");
            Files.delete(list.toPath());
            return padded;
        } catch (Exception e) {
            //Occurs when invalid files are passed in. There is no way for the user to actually do this through the GUI.
            WarningDialogue.genericError(e.getMessage());
            //Returning the original file unaltered will cause erroneous output, but won't terminate the program.
            return audioFile;
        }
    }
}
