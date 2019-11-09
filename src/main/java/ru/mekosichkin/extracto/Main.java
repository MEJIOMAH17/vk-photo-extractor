package ru.mekosichkin.extracto;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws Exception {
        AtomicInteger counter= new AtomicInteger();
        File file = new File(args[0]);
        new File("test").mkdir();
        new BufferedReader(new FileReader(file))
                .lines()
                .filter(e -> !e.isEmpty())
                .flatMap(e -> {
                    Pattern pattern = Pattern.compile("(https.*?(\"|$|\\s))");
                    Matcher matcher = pattern.matcher(e);
                    List<String> list = new ArrayList<>();
                    while (matcher.find()) {
                        if (!matcher.group(1).contains("sticker")) {
                            list.add(matcher.group(1).replace("\"", ""));
                        }
                    }
                    return list.stream();
                })
                .parallel()
                .filter(e->e.contains("jpg"))
                .forEach(e -> {
                    try {
                        URL url = new URL(e);
                        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
                        FileOutputStream fileOutputStream = new FileOutputStream("test/" + e.
                                substring(e.lastIndexOf("/"))
                                .replace("&", "")
                                .replace("?", "")
                                .replace(":", ""));
                          fileOutputStream.getChannel()
                                .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                        System.out.println(counter.incrementAndGet());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });


    }
}
