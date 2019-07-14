package com.olegleinov.bigfilesort;

import java.io.File;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


/**
 * This program is reading amount of integers from a big file, sort them and write to file result.txt
 */
public class App {
    public static void main(String[] args) {
        LocalDateTime start = LocalDateTime.now();
        FileMergeSort newFileToSort = new FileMergeSort();
        File originalFile = new File(args[0]);
        newFileToSort.sort(originalFile);
        System.out.println("Execution time: " + ChronoUnit.SECONDS.between(start, LocalDateTime.now()) + " sec");
    }
}