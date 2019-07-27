## Big file merge sort
This program is reading amount of integers from a big file, sort them using temp files and write to file **result.txt**

Path to original txt file with amount of integers should be passed in cmd argument. Temp files and **result.txt** will be created in the folder where original file is stored.

Given with a line separated text file of integers ranging anywhere from Integer.MIN to Integer.MAX of size 1024MB, the program should be able to produce line separated text file which has the sorted content of the input file.

Following preconditions:
  The program should be able to run with a memory constraint of 100MB (-Xmx100m);
  The file can have duplicate integers;
  The text in the file has only integers which are line separated and no other characters.
