package nbspOpen.lukeslog.de.nbspopen.status;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import nbspOpen.lukeslog.de.nbspopen.model.Rss;

public class RssReader {

    String nbspUrl="http://nobreakspace.org/status/rss";
    URL url;
    static Rss rssFeed;

    public void createStatusListFromRss() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    url = new URL(nbspUrl);
                    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

                    String inputLine;
                    String tempf = "";
                    while ((inputLine = in.readLine()) != null) {
                        tempf = tempf + inputLine;
                    }

                    Serializer serializer = new Persister();

                    rssFeed = serializer.read(Rss.class, tempf);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
