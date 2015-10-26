# VIDIVOX

This is VIDIVOX, a project that was worked on for the University of Auckland SOFTENG 206 2015 course.

VIDIVOX is a simple video player that allows the user to add generated speech or other audio to a video to create a virtual narration. 

## How to run
This java application was designed to run on Ubuntu 14.04 with JRE 1.8_X installed. The following packages are also assumed to be installed:

1. ffmpeg (latest version)
2. Festival (2.1 or greater)

To run this on a UG4 lab computer (University of Auckland specific requirement) navigate to the directory containing the Vidivox jar file and run the following command in the shell:
This is because the UG4 computers use Java 1.7 by default, while VIDIVOX requires the latest version of Java.

`/usr/lib/jvm/jre1.8.0_45/bin/java -jar VIDIVOX.jar`

## Authors
Jayden Cooke

Matthew Canham

## Known Bugs
There are some minor graphical issues that arise on the UG4 computers, but not on any other machines they have been tested on. 
As such, we have been unable to correct them by changing code.
These include:
-Resizing the window smaller, then larger will create a large blank space.
-The background colour, which should be black, follows the linux colour scheme.
-The file menu may sometimes flicker and not respond. A restart of VIDIVOX will correct this.