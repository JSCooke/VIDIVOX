package com.vidivox.controller;

import com.vidivox.Generators.ManifestController;
import com.vidivox.view.WarningDialogue;

import java.io.File;

/**
 * Created by Jayden Cooke on 7/10/2015.
 * Holds the currently open project, and details about the previous project for reloading purposes.
 */
public class CurrentDirectory {

    private static CurrentDirectory currentDirectory;
    private static File directory;
    private static String name;
    private static String path;
    private static String nameBackup;
    private static String pathBackup;

    /**
     * Singleton classes have blank constructors.
     */
    private CurrentDirectory() {}

    /**
     * Sets the current project directory.
     * This could replace the addPath and addName methods, but old code still uses them.
     * @param directory - The current project directory.
     */
    public static void setDirectory (File directory) {
        if (currentDirectory == null) {
            currentDirectory = new CurrentDirectory();
        }

        CurrentDirectory.directory = directory;
        //Updates fields and backups.
        CurrentDirectory.addName(directory.getName());
        //This monster of a call gets the path and trims the name.
        CurrentDirectory.addPath(directory.getPath().substring(0,directory.getPath().lastIndexOf(System.getProperty("file.separator"))));
    }

    /**
     * Updates the name and nameBackup variables based on user input.
     * @param name - the name of the new current directory.
     */
    public static void addName(String name) {
        if (currentDirectory == null) {
            currentDirectory = new CurrentDirectory();
        }

        CurrentDirectory.nameBackup=CurrentDirectory.name;
        CurrentDirectory.name = name;
    }

    /**
     * Updates the path and pathBackup variables based on user input.
     * @param path - the path of the new current directory.
     */
    public static void addPath(String path) {
        if (currentDirectory == null) {
            currentDirectory = new CurrentDirectory();
        }

        CurrentDirectory.pathBackup=CurrentDirectory.path;
        CurrentDirectory.path = path;
    }

    /**
     * Creates the directory and updates the directory variable.
     * This also involves checking that the directory does not already exist.
     */
    public static void makeDir() {
        String pathAndName = path+System.getProperty("file.separator")+name;//Platform independent file separator.
        directory=new File(pathAndName);

        if (!directory.exists()){
            directory.mkdir();
            ManifestController newMani = new ManifestController(directory);
            newMani.create();
        }else{
            new WarningDialogue("That directory already exists, please try again.");
            interrupted();
        }
    }

    /**
     * This occurs when the project needs to revert to its previous value, such as during an interrupted open operation.
     * It replaces the current fields with the backup fields.
     */
    public static void interrupted() {
        CurrentDirectory.addPath(pathBackup);
        CurrentDirectory.addName(nameBackup);
        String pathAndName = path+System.getProperty("file.separator")+name;//Platform independent file separator.
        directory=new File(pathAndName);
    }

    /**
     * Gets the current directory and returns it.
     * @return The current directory.
     */
    public static File getDirectory() {
        return directory;
    }

}

