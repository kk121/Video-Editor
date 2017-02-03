package net.video.trimmer.util;


import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

public class Util {

    public static String toFormattedTime(int time) {
        int remainingTime = time;

        int hours = remainingTime / (1000 * 60 * 60);
        remainingTime -= hours * (1000 * 60 * 60);

        int minutes = remainingTime / (1000 * 60);
        remainingTime -= minutes * (1000 * 60);

        int seconds = remainingTime / 1000;

        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds)
                : String.format("%02d:%02d", minutes, seconds);
    }

    public static String getTargetFileName(String inputFileName) {
        final File file = new File(inputFileName).getAbsoluteFile();
        final String fileName = file.getName();

        String[] filenames = file.getParentFile().list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename != null && filename.startsWith("trimmed-") && filename.endsWith(fileName);
            }
        });

        int count = 0;
        String targetFileName;
        List<String> fileList = Arrays.asList(filenames);

        do {
            targetFileName = "trimmed-" + String.format("%03d", count++)+ "-" + fileName;
        } while(fileList.contains(targetFileName));

        return new File(file.getParent(), targetFileName).getPath();
    }
}
