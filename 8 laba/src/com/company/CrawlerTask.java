package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class CrawlerTask implements Runnable {

    final static int ADepth = 0;

    private URLPool Pool;
    // у префикса нету слэша для поддержки https
    private String Prefix = "http";

    @Override
    public void run() {
        try {
            Scan();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public CrawlerTask(URLPool pool) {
        Pool = pool;
    }

    private  void  Scan() throws IOException, InterruptedException {
        while (true) {
            Process(Pool.get());

        }
    }

    private void Process(URLDepthPair pair) throws IOException{
        // устанавливает соединение и следует перенаправлению
        URL url = new URL(pair.getURL());
        URLConnection connection = url.openConnection();

        String redirect = connection.getHeaderField("Location");
        if (redirect != null) {
            connection = new URL(redirect).openConnection();
        }

        Pool.addProcessed(pair);
        if (pair.getDepth() == 0) return;

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String input;
        while ((input = reader.readLine()) != null) {
            while (input.contains("a href=\"" + Prefix)) {
                input = input.substring(input.indexOf("a href=\"" + Prefix) + 8);
                String link = input.substring(0, input.indexOf('\"'));
                if(link.contains(" "))
                    link = link.replace(" ", "%20");
                // избегает использование одной и той же ссылки
                if (Pool.getNotProcessed().contains(new URLDepthPair(link, ADepth)) ||
                        Pool.getProcessed().contains(new URLDepthPair(link, ADepth))) continue;
                Pool.addNotProcessed(new URLDepthPair(link, pair.getDepth() - 1));
            }
        }
        reader.close();

    }

}