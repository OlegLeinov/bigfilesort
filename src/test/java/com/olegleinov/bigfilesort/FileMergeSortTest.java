package com.olegleinov.bigfilesort;

import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class FileMergeSortTest {
    private FileMergeSort fileMergeSort = spy(new FileMergeSort());

    @Test
    public void sort() {
        File originalFile = mock(File.class);
        File folder = mock(File.class);
        List<File> sortedFiles = List.of();
        doReturn(originalFile).when(fileMergeSort).getFileByName(anyString());
        doReturn(folder).when(originalFile).getParentFile();
        doReturn(sortedFiles).when(fileMergeSort).sortBatchToFiles(any(), any());
        String fileName = "fileName.txt";

        fileMergeSort.sort(fileName);

        verify(fileMergeSort).getFileByName(fileName);
        verify(fileMergeSort).sortBatchToFiles(originalFile, folder);
        verify(fileMergeSort).mergeSortedFiles(sortedFiles, folder);
    }

    @Test
    public void sortException() {
        doThrow(new RuntimeException()).when(fileMergeSort).getFileByName(anyString());
        String fileName = "fileName.txt";

        fileMergeSort.sort(fileName);

        verify(fileMergeSort).getFileByName(fileName);
    }

    @Test
    public void getFileByName() {
        String fileName = "fileName.txt";

        File fileByName = fileMergeSort.getFileByName(fileName);

        assertEquals(fileName, fileByName.getName());
    }

    @Test
    public void sortBatchToFiles() {

    }

    @Test
    public void mergeSortedFiles() {
    }
}