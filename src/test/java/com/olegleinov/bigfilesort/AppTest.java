package com.olegleinov.bigfilesort;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class AppTest {
    @Test
    public void main() {
        String[] params = new String[]{"first parameter"};
        FileMergeSort newFileToSort = mock(FileMergeSort.class);

        App.main(params);

        verify(newFileToSort).sort(params[0]);
    }
}