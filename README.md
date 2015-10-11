# VIDIVOX

This is a prototype of Vidivox, a project that was worked on for the University of Auckland Softeng 206 2015 course.

Vidivox is a simple video player that allows the user to add generated speech to a video to create a virtual narration. This is currently a beta and lacks some key audio editing features, and contains some known bugs, detailed at the end of the readme.

## How to run
This java application was designed to run on Ubuntu 14.04 with JRE 1.8_X installed. The following packages are also assumed to be installed:

1. ffmpeg (latest version)
2. Festival (2.1 or greater)

To run this on a UG4 lab computer (University of Auckland specific requirement) navigate to the directory containing the Vidivox jar file and run the following command in the shell:

`/usr/lib/jvm/jre1.8.0_45/bin/java -jar VIDIVOX.jar`

## Authors
Jayden Cooke

Matthew Canham

## Known Bugs
Minor issues relating to the progress slider - can be unresponsive, and holding down the play button will cause issues.
The program may not terminate on close.
The filechooser defaults to different places on different operating systems.
Quickly opening and closing toolbars may cause the fading animations to play on top of one another.
The 20 word limit can be circumvented by entering multiple blank spaces.
Normally unreachable exceptions can be thrown by changing the project folder outside of Vidivox.
While it functioned under testing, the File menu has been known to function incorrectly in UG4.
Resizing the window may cause the video to shift to one side if the window is very small.
The merge audio feature conforms to Assignment 3, not Assignment 4, specifications, as the code was unfixable in the time frame.
-Cannot retain the original video's audio.
-Cannot overlap multiple audio files.
-Cannot remove a particular audio file.
-May shorten the video, rather than pad the audio, and other changes may be made to the video during conversion.