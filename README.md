## SBSJ - Simple Backup System in Java


### Introduction
This is a school project on which I would like to learn the Java language. It will be a simple backup system 
that will in its simplest form take care of mirroring directories to one or more backup locations specified by the user. 

It will start off as a very basic console application that will be able to take a few arguments from the user 
and perform backups on demand. Then I would like to add a simple GUI for ease of use.

Please, note that this project was mainly created for educational purposes
so it may contain a lot of bugs and unresolved issues.
If you're looking for some serious backup software, you should seek it elsewhere.
However, you are more than welcome to look around and I will be glad for any comments, questions or suggestions regarding the project.

For more details see the project's [Wiki](https://github.com/martin-sicho/SBSJ/wiki "SBSJ Wiki").

### Current State of the Project
I am happy to announce that I finally finished the GUI (you can check the release [here](https://github.com/martin-sicho/SBSJ/releases "SBSJ v2.0")).

For now I will call this a finished product even though there are still some things that might have been done better.
Some I am aware of and some I am not.
I also have a few other ideas to make the app more practical, but my time is unfortunately limited.
Look at the issues tagged [discussion](https://github.com/martin-sicho/SBSJ/issues?labels=discussion&state=open) 
for more information on this.

### Used Libraries
To parse command line arguments the utility uses the "Python like" argument parser [argparse4j](http://argparse4j.sourceforge.net/ "argparse4j").

To make the GUI design as painless as possible I used the *UIDesigner* plugin included with the **IntelliJ IDEA** IDE
(which is a great product by the way and you can even get the Community Edition for free [here](http://www.jetbrains.com/idea/download/)).
