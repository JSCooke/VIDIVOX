package com.vidivox.Generators;

import com.vidivox.view.WarningDialogue;

import java.io.*;
import java.lang.reflect.Field;

/**
 * 
 * @author Matthew Canham
 *
 */
public class FestivalSpeech {
	
	private String text;
	private int pid;
	private Process p;
	
	/**
	 * 
	 * @param textToSay
	 * The text that should be said.
	 */
	public FestivalSpeech(String textToSay) {
		this.text = textToSay;
	}
	
	/**
	 * Begin speaking the text that was entered when creating this object
	 */
	public void speak() {
		sayText();
	}
	
	/**
	 * Stop the speaking voice at any point
	 */
	public void stopSpeak(){
		pid = getFestivalPID(p);
		ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", "kill " + pid);
		try {
			pb.start();
		} catch (IOException e) {
			//ignore
		}
	}

	/**
	 * Export the entered text to an MP3 file specified by the function argument.
	 *
	 * @param file
	 * A file object representing the location and name of the exported mp3 file.
	 * The directory does not have to exist prior to calling this function.
	 * Example argument: new File("Music_Folder/myBeats.mp3");
	 *
	 */
	public void exportToMP3(File file) {
		try {

			//Make the specified directory if it does not already exist
			file.getParentFile().mkdirs();

			String fileLink = file.toURI().toURL().getPath();
			fileLink = fileLink.replace("%20", "\\ ");

			String process = "echo \"" + text + "\" | text2wave -o temp.wav";
			process += " && ffmpeg -i temp.wav -f mp3 -y " + fileLink;
			process += " && rm temp.wav";

			ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", process);
			pb.start().waitFor();

		} catch (IOException e) {
			WarningDialogue.genericError(e.getMessage());
		} catch (InterruptedException e) {
			WarningDialogue.genericError(e.getMessage());
		}


	}
	
	/**
	 * Call festival and actually say the text stored in the 'text' field.
	 * Note that this method is private and is called by the FestivalSpeech class internally
	 */
	private void sayText() {
		ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", "echo \"" + text + "\" | festival --tts");
		try {			
			p = pb.start();
		} catch (Exception e){
			WarningDialogue.genericError(e.getMessage());
		}
		
	}
	/**
	 * Note that this method is private and is called by the FestivalSpeech class internally
	 * 
	 * @param p
	 * This is the main bash process in which festival was called in a subprocess
	 * @return
	 * Returns the process id (PID) of the process that is making the festival noises
	 */
	private int getFestivalPID(Process p){
		
		if( p.getClass().getName().equals("java.lang.UNIXProcess") ){
			try {
				//Do some reflection to get the pid of the process that was input to the function
				Field f;
				f = p.getClass().getDeclaredField("pid");
				f.setAccessible(true); // pid is private in UNIXProcess
				int processPID = f.getInt(p);
				
				//Now use this process pid to find the pid of the actual festival process
				//Note that the process we need to kill is a sub process of the java process and that is
				//why we have to go to all of this trouble to find and kill the correct process.
				ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", "pstree -p " + processPID);
				Process tempP = pb.start();
				InputStream stdout = tempP.getInputStream();
				BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
				
				String tree = "";
				String currentLine;
				while ((currentLine = stdoutBuffered.readLine()) != null){
					tree += currentLine;
				}
				
				//Extract just the part of the tree that we need
				int pos = tree.indexOf("play"); //Returns -1 if not found
				String pidString = "";
				while (pos != -1 && tree.charAt(pos) != ')'){
					if(Character.isDigit(tree.charAt(pos))){
						pidString += tree.charAt(pos);
					}
					pos++;
				}
				
				//We now have the pid of the process that is actually making the fesival sound. Return it.
				if( !pidString.isEmpty() ){
					return Integer.parseInt(pidString);
				}
				
			} catch (NoSuchFieldException e) {
				WarningDialogue.genericError(e.getMessage());
			} catch (SecurityException e) {
				WarningDialogue.genericError(e.getMessage());
			} catch (IllegalArgumentException e) {
				WarningDialogue.genericError(e.getMessage());
			} catch (IllegalAccessException e) {
				WarningDialogue.genericError(e.getMessage());
			} catch (IOException e) {
				WarningDialogue.genericError(e.getMessage());
			}
		}
		return -1;
	}
}
