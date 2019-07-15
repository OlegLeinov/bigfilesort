package com.olegleinov.bigfilesort;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static junit.framework.TestCase.assertTrue;
import static org.apache.commons.io.FileUtils.contentEquals;


public class FileMergeSortIntegrationTest {
    @Before
    public void setUp() {
        ClassLoader classLoader = getClass().getClassLoader();
        File actualResult = new File(classLoader.getResource("result.txt").getFile());
        actualResult.delete();
    }

    @Test
    public void sort() throws IOException {
        FileMergeSort fileMergeSort = new FileMergeSort(1);
        ClassLoader classLoader = getClass().getClassLoader();
        File original = new File(classLoader.getResource("original.txt").getFile());
        fileMergeSort.sort(original);
        File expectedResult = new File(classLoader.getResource("expectedResult.txt").getFile());
        File actualResult = new File(classLoader.getResource("result.txt").getFile());
        assertTrue(contentEquals(expectedResult, actualResult));
    }
}