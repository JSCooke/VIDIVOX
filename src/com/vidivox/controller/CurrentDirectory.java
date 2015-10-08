package com.vidivox.controller;

import com.vidivox.view.WarningDialogue;

import java.io.File;

/**
 * Created by Jayden Cooke on 7/10/2015.
 * Holds the currently open project, and details about the previous project for reloading purposes.
 */
//Jayden note (Disregard this comment if I forgot to take this out) - this needs to be more modular
public class CurrentDirectory {
    private static CurrentDirectory currentDirectory;

    public static File getDirectory() {
        return directory;
    }

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
}

