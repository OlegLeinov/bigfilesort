package com.olegleinov.bigfilesort;

import java.io.*;
import java.util.*;

public class FileMergeSort {
    private static final String OUTPUT_TMP_FILE_TEMPLATE = "tmpbuffer";
    private static final String RESULT_FILE = "result.txt";
    private static final int ARRAY_SIZE_MB = 95;
    private static final int ARRAY_SIZE = ARRAY_SIZE_MB * 1024 * 1024 / 4;

    public void sort(String originalFileName) {
        try {
            File originalFile = getFileByName(originalFileName);
            File folder = originalFile.getParentFile();
            List<File> sortedFiles = sortBatchToFiles(originalFile, folder);
            mergeSortedFiles(sortedFiles, folder);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    File getFileByName(String originalFileName) {
        return new File(originalFileName);
    }

    List<File> sortBatchToFiles(File originalFile, File folder) {
        int[] tmpNumbers = new int[ARRAY_SIZE];
        List<File> files = new ArrayList<File>();
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(originalFile));
            boolean endOfFileReached = false;
            while (!endOfFileReached) {
                File newTmpFile = File.createTempFile(OUTPUT_TMP_FILE_TEMPLATE, null, folder);
                newTmpFile.deleteOnExit();
                files.add(newTmpFile);
                System.out.println("Starting to read and sort " + ARRAY_SIZE_MB + " mb data");
                int actualSize;
                for (actualSize = 0; actualSize < tmpNumbers.length; actualSize++) {
                    String newLine = fileReader.readLine();
                    if (newLine == null) {
                        endOfFileReached = true;
                        break;
                    }
                    tmpNumbers[actualSize] = Integer.parseInt(newLine);
                }
                Arrays.sort(tmpNumbers);
                DataOutputStream fileWriter = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(newTmpFile)));
                System.out.println("Writing to the temp file number " + files.size());
                for (int i = 0; i < actualSize; i++) {
                    fileWriter.writeInt(tmpNumbers[i]);
                }
                fileWriter.close();
                System.out.println("Temp file number " + files.size() + " was written successfully");
            }
            fileReader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(files.size() + " files was created");
        return files;
    }

    void mergeSortedFiles(List<File> files, File folder) {
        List<Integer> minValues = new ArrayList<Integer>(files.size());
        List<DataInputStream> readers = new ArrayList<DataInputStream>(files.size());
        try {
            File result = new File(folder, RESULT_FILE);
            if (!result.createNewFile()) {
                throw new RuntimeException("Can't create result.txt");
            }
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(result));
            for (File file : files) {
                readers.add(new DataInputStream(new BufferedInputStream(new FileInputStream(file))));
            }
            for (DataInputStream reader : readers) {
                minValues.add(reader.readInt());
            }
            boolean notFirst = false;
            while (true) {
                Optional<Integer> min = minValues.stream().filter(Objects::nonNull).min(Integer::compareTo);
                if (min.isEmpty()) {
                    break;
                }
                int indexOfMin = minValues.indexOf(min.get());
                if (notFirst) {
                    fileWriter.newLine();
                } else {
                    notFirst = true;
                }
                fileWriter.write(minValues.get(indexOfMin).toString());
                Integer nextValue;
                try {
                    nextValue = readers.get(indexOfMin).readInt();
                } catch (EOFException e) {
                    System.out.println("Temp file number " + (indexOfMin + 1) + " complete");
                    nextValue = null;
                }
                minValues.set(indexOfMin, nextValue);
            }
            for (DataInputStream reader : readers) {
                reader.close();
            }
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
