# msa2csv
msa2csv is a cross-platform command line program for converting Microsoft Access databases to flat CSV files. The program is written entirely in Java using the [Jackcess](http://jackcess.sourceforge.net/) library, and should therefore run on any system with Java version 1.7 or newer installed without needing Microsoft Access to be installed on the system as well.

## Dependencies
**jackcess:** used to read the data from Microsoft Access database files.

**commons-lang:** necessary as a dependency for Jackcess.

**commons-logging:** necessary as a dependency for Jackcess.

## Installation
msa2csv uses [Gradle](http://gradle.org/) to manage dependencies and constructs self-contained Jar archives.

A Jar archive containing msa2csv and all dependencies can be constructed by the command.
```
$ gradle build
```

If only the class files for msa2csv are needed use the following command.
```
$ gradle compileJava
```

Removal of all temporary files is done using the command.
```
$ gradle clean
```

## License
The program is licensed under version 3 of the GPL, and a copy of the license is bundled with the program.
