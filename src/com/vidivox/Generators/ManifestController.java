package com.vidivox.Generators;

import com.vidivox.view.WarningDialogue;

import java.io.*;
import java.util.Scanner;

/**
 * Created by Jayden on 9/10/2015.
 * This class keeps all the methods relating to the manifest file in one place.
 * The manifest is structured as follows:
 * Video File
 * Audio files, each on a new line
 */
//The project folder probably needs this too.
public class ManifestController {
    File manifest;
    public ManifestController(File projectDir){
        this.manifest = new File(projectDir.getAbsolutePath()+System.getProperty("file.separator")+"Manifest.vvx");
    }

    public void create() {
        try {
            manifest.createNewFile();
        }catch(IOException e){
            //This should be unreachable in the current build.
            WarningDialogue.genericError("A manifest file already exists for this project");
        }
    }

     public void setVideo(String videoName) throws FileNotFoundException {
         PrintWriter w = new PrintWriter(manifest);
         w.println(videoName);
         w.close();
     }

    public String getVideo() throws FileNotFoundException {
        Scanner r = new Scanner(manifest);
        return r.nextLine();
    }

    public void addAudio(String audioName) throws FileNotFoundException {
        PrintWriter w = new PrintWriter(manifest);
        w.append(audioName);
        w.close();
    }

}
