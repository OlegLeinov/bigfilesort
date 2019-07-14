package com.olegleinov.bigfilesort;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

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
        File originalFile = mock(File.class);
        File folder = mock(File.class);
        FileReader fReader = mock(FileReader.class);
        BufferedReader fileReader = mock(BufferedReader.class);
        List<File> sortedFiles = List.of(mock(File.class));
        whenNew(BufferedReader.class).withArguments(any(FileReader.class)).thenReturn(fileReader);
        whenNew(FileReader.class).withArguments(any(File.class)).thenReturn(fReader);
        doReturn(sortedFiles).when(fileMergeSort).sortBatchToFiles(any(BufferedReader.class), any());

        List<File> files = fileMergeSort.sortBatchToFiles(originalFile, folder);

        verifyNew(FileReader.class).withArguments(originalFile);
        verifyNew(BufferedReader.class).withArguments(fReader);
        verify(fileMergeSort).sortBatchToFiles(fileReader, folder);
        verify(fileReader).close();
        assertEquals(sortedFiles, files);
    }

    @Test
    public void mergeSortedFiles() {
    }

    @Test
    public void sortBatchToFiles1() {
    }

    @Test
    public void readFileChunkToArray() {
    }

    @Test
    public void mergeSortedFiles1() {
    }

    @Test
    public void writeArrayToTmpFile() {
    }

    @Test
    public void createResultFileWriter() {
    }

    @Test
    public void openTmpFileReaders() {
    }

    @Test
    public void closeMergeStreams() {
    }
}