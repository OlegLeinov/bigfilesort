package com.olegleinov.bigfilesort;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.*;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FileMergeSortTest {
    @Spy
    private FileMergeSort fileMergeSort;

    @Test
    public void sort() throws IOException {
        File originalFile = mock(File.class);
        File folder = mock(File.class);
        List<File> files = List.of(mock(File.class));
        doReturn(files).when(fileMergeSort).sortBatchToFiles(any(File.class), any());
        doReturn(folder).when(originalFile).getParentFile();
        doNothing().when(fileMergeSort).mergeSortedFiles(any(File.class), any());

        fileMergeSort.sort(originalFile);

        verify(fileMergeSort).sortBatchToFiles(originalFile, folder);
        verify(fileMergeSort).mergeSortedFiles(folder, files);
    }

    @Test
    public void sortException() {
        File originalFile = mock(File.class);
        doThrow(new RuntimeException()).when(originalFile).getParentFile();

        fileMergeSort.sort(originalFile);

        verify(originalFile).getParentFile();
    }

    @Test
    public void sortBatchToFiles() throws Exception {
        BufferedReader fileReader = mock(BufferedReader.class);
        File folder = mock(File.class);
        doReturn(200).doReturn(200).doReturn(130).doReturn(0).when(fileMergeSort).readFileChunkToArray(any(), any());
        doReturn(mock(File.class)).when(fileMergeSort).writeArrayToTmpFile(any(), any(int[].class), anyInt());

        List<File> files = fileMergeSort.sortBatchToFiles(fileReader, folder);

        verify(fileMergeSort, times(4)).readFileChunkToArray(any(), eq(fileReader));
        verify(fileMergeSort, times(2)).writeArrayToTmpFile(eq(folder), any(), eq(200));
        verify(fileMergeSort).writeArrayToTmpFile(eq(folder), any(), eq(130));
        assertEquals(3, files.size());
    }

    @Test
    public void readFileChunkToArrayFullSize() throws IOException {
        BufferedReader fileReader = mock(BufferedReader.class);
        int[] tmpNumbers = new int[3];
        doReturn("1").when(fileReader).readLine();

        int actualSize = fileMergeSort.readFileChunkToArray(tmpNumbers, fileReader);

        assertEquals(3, actualSize);
        verify(fileReader, times(3)).readLine();
    }

    @Test
    public void readFileChunkToArrayPartSize() throws IOException {
        BufferedReader fileReader = mock(BufferedReader.class);
        int[] tmpNumbers = new int[3];
        doReturn("1").doReturn(null).when(fileReader).readLine();

        int actualSize = fileMergeSort.readFileChunkToArray(tmpNumbers, fileReader);

        assertEquals(1, actualSize);
        verify(fileReader, times(2)).readLine();
    }

    @Test
    public void mergeSortedFiles() throws IOException {
        DataInputStream tmpFileReader1 = mock(DataInputStream.class);
        DataInputStream tmpFileReader2 = mock(DataInputStream.class);
        List<DataInputStream> tmpFileReaders = List.of(tmpFileReader1, tmpFileReader2);
        BufferedWriter resultFileWriter = mock(BufferedWriter.class);
//        -4 1 3 7 EOF
        doReturn(-4).doReturn(1).doReturn(3).doReturn(7).doThrow(EOFException.class).when(fileMergeSort).readInt(tmpFileReader1);
//        -1 1 1 5 8 EOF
        doReturn(-1).doReturn(1).doReturn(1).doReturn(5).doReturn(8).doThrow(EOFException.class).when(fileMergeSort).readInt(tmpFileReader2);

        fileMergeSort.mergeSortedFiles(tmpFileReaders, resultFileWriter);

//        -4 -1 1 1 1 3 5 7 8
        verify(resultFileWriter).write("-4");
        verify(resultFileWriter).write("-1");
        verify(resultFileWriter, times(3)).write("1");
        verify(resultFileWriter).write("3");
        verify(resultFileWriter).write("5");
        verify(resultFileWriter).write("7");
        verify(resultFileWriter).write("8");
        verify(resultFileWriter, times(8)).newLine();
    }

    @Test
    public void closeMergeStreams() throws IOException {
        List<DataInputStream> tmpFileReaders = List.of(mock(DataInputStream.class), mock(DataInputStream.class));
        BufferedWriter resultFileWriter = mock(BufferedWriter.class);

        fileMergeSort.closeMergeStreams(tmpFileReaders, resultFileWriter);

        for (DataInputStream reader : tmpFileReaders) {
            verify(reader).close();
        }
        verify(resultFileWriter).close();
    }
}