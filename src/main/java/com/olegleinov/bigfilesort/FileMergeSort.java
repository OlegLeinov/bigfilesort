package com.olegleinov.bigfilesort;

import java.io.*;
import java.util.*;

public class FileMergeSort {
    private static final String OUTPUT_TMP_FILE_TEMPLATE = "tmpbuffer";
    private static final String RESULT_FILE = "result.txt";
    private static final int ARRAY_SIZE_MB = 95;
    private static final int ARRAY_SIZE = ARRAY_SIZE_MB * 1024 * 1024 / 4;

    public void sort(File originalFile) {
        try {
            File folder = originalFile.getParentFile();

            List<File> sortedFiles = sortBatchToFiles(originalFile, folder);

            mergeSortedFiles(folder, sortedFiles);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    List<File> sortBatchToFiles(File originalFile, File folder) throws IOException {
        BufferedReader fileReader = new BufferedReader(new FileReader(originalFile));
        List<File> sortedFiles = sortBatchToFiles(fileReader, folder);
        fileReader.close();
        return sortedFiles;
    }

    void mergeSortedFiles(File folder, List<File> sortedFiles) throws IOException {
        BufferedWriter resultFileWriter = createResultFileWriter(folder);
        List<DataInputStream> tmpFileReaders = openTmpFileReaders(sortedFiles);
        mergeSortedFiles(tmpFileReaders, resultFileWriter);
        closeMergeStreams(tmpFileReaders, resultFileWriter);
    }

    List<File> sortBatchToFiles(BufferedReader fileReader, File folder) throws IOException {
        int[] tmpNumbers = new int[ARRAY_SIZE];
        List<File> files = new ArrayList<>();
        while (true) {
            System.out.println("Read " + ARRAY_SIZE_MB + " mb data");
            int actualSize = readFileChunkToArray(tmpNumbers, fileReader);
            if (actualSize == 0) {
                System.out.println(files.size() + " files was created");
                return files;
            }
            System.out.println("Sorting");
            Arrays.sort(tmpNumbers);
            System.out.println("Writing to the temp file number " + (files.size() + 1));
            File tmpFile = writeArrayToTmpFile(folder, tmpNumbers, actualSize);
            files.add(tmpFile);
            System.out.println("Temp file number " + files.size() + " was written successfully");
        }
    }

    int readFileChunkToArray(int[] tmpNumbers, BufferedReader fileReader) throws IOException {
        for (int i = 0; i < tmpNumbers.length; i++) {
            String newLine = fileReader.readLine();
            if (newLine == null) {
                return i;
            }
            tmpNumbers[i] = Integer.parseInt(newLine);
        }
        return tmpNumbers.length;
    }

    void mergeSortedFiles(List<DataInputStream> tmpFileReaders, BufferedWriter resultFileWriter) throws IOException {
        List<Integer> minValues = new ArrayList<Integer>(tmpFileReaders.size());

        for (DataInputStream reader : tmpFileReaders) {
            minValues.add(reader.readInt());
        }

        boolean notFirst = false;
        while (true) {
            Optional<Integer> min = minValues.stream().filter(Objects::nonNull).min(Integer::compareTo);
            if (min.isEmpty()) {
                return;
            }

            if (notFirst) {
                resultFileWriter.newLine();
            } else {
                notFirst = true;
            }

            int currentFileNumber = minValues.indexOf(min.get());
            resultFileWriter.write(minValues.get(currentFileNumber).toString());

            try {
                Integer nextValue = tmpFileReaders.get(currentFileNumber).readInt();
                minValues.set(currentFileNumber, nextValue);
            } catch (EOFException e) {
                System.out.println("Temp file number " + (currentFileNumber + 1) + " complete");
                minValues.set(currentFileNumber, null);
            }
        }
    }

    File writeArrayToTmpFile(File folder, int[] tmpNumbers, int actualSize) throws IOException {
        File newTmpFile = File.createTempFile(OUTPUT_TMP_FILE_TEMPLATE, null, folder);
        newTmpFile.deleteOnExit();
        DataOutputStream fileWriter = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(newTmpFile)));
        for (int i = 0; i < actualSize; i++) {
            fileWriter.writeInt(tmpNumbers[i]);
        }
        fileWriter.close();
        return newTmpFile;
    }

    BufferedWriter createResultFileWriter(File folder) throws IOException {
        File result = new File(folder, RESULT_FILE);
        if (!result.createNewFile()) {
            throw new RuntimeException("Can't create file result.txt");
        }
        return new BufferedWriter(new FileWriter(result));
    }

    List<DataInputStream> openTmpFileReaders(List<File> files) throws FileNotFoundException {
        List<DataInputStream> readers = new ArrayList<DataInputStream>(files.size());
        for (File file : files) {
            readers.add(new DataInputStream(new BufferedInputStream(new FileInputStream(file))));
        }
        return readers;
    }

    void closeMergeStreams(List<DataInputStream> tmpFileReaders, BufferedWriter resultFileWriter) throws IOException {
        for (DataInputStream reader : tmpFileReaders) {
            reader.close();
        }
        resultFileWriter.close();
    }
}
