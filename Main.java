package org.example;

import java.util.*;
import java.io.*;

/**********************************************************/
/* Feed                                                   */
class Feed {
    private String _title;
    private String _desc;

    public Feed() {
    }

    public Feed(String a, String b) {
        _title = a;
        _desc = b;
    }

    public void setTitle(String a) {
        _title = a;
    }

    public void setDesc(String a) {
        _desc = a;
    }

    public String getTitle() {
        return _title;
    }

    public String getDesc() {
        return _desc;
    }

    public String toString() {
        return _title + " " + _desc;
    }
}

/**********************************************************/
/* NewsFeed				                  */

/**********************************************************/

class NewsFeed {
    private ArrayList<Feed> _newsFeed;

    public NewsFeed() {
        _newsFeed = new ArrayList<Feed>();
    }

    public NewsFeed(String fileName) throws IOException {
        File dataFile = new File(fileName);
        _newsFeed = new ArrayList<Feed>();
        try {
            Scanner infile = new Scanner(dataFile);
            while (infile.hasNextLine()) {
                String data = infile.nextLine();
                String[] feedItems = data.split(";");
                Feed data2 = new Feed(feedItems[0], feedItems[1]);
                _newsFeed.add(data2);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Feed getRandomFeed() {
        Random random = new Random();
        int randomNumber = random.nextInt(_newsFeed.size());
        return _newsFeed.get(randomNumber);
    }
}
/**********************************************************/
/* Strategy Pattern Interface/Classes                     */

/**********************************************************/

// Provided: Strategy Interface 
interface AnalysisBehavior {
    double analyze(String[] words, String searchWord);
}

// Task: Complete Class CountIfAnalysis
class CountIfAnalysis implements AnalysisBehavior {
    public double analyze(String[] words, String searchWord) {
        int idx = -1;
        int i = 0;
        String target = searchWord.toLowerCase();
        while (i < words.length && idx == -1) {
            if (words[i].toLowerCase().equals(target)) {
                return 1.0;
            }
            i++;
        }
        return 0.0;
    }
}

// Task: Complete Class CountAllAnalysis
class CountAllAnalysis implements AnalysisBehavior {
    public double analyze(String[] words, String searchWord) {
        int count = 0;
        String target = searchWord.toLowerCase();
        for (int i = 0; i < words.length; i++) {
            if (words[i].toLowerCase().equals(target)) {
                count++;
            }
        }
        return count;
    }
}

/**********************************************************/
/* Observer Pattern Interface/Classes                     */

/**********************************************************/

interface Subject {  // Notifying about state changes 
    void subscribe(Observer obs);

    void unsubscribe(Observer obs);

    void notifyObservers(Feed f);
}

interface Observer {  // Waiting for notification of state changes 
    void update(Feed f, String platformName);
}


abstract class SocialMediaPlatform implements Subject {
    private String _name;
    private ArrayList<Feed> _feed;
    private ArrayList<Observer> _observers;
    private int _updateRate;

    public SocialMediaPlatform(String n, int x) {
        _name = n;
        _feed = new ArrayList<Feed>();
        _observers = new ArrayList<Observer>();
        _updateRate = x;
    }

    public void addFeed(Feed f) {
        _feed.add(f);
    }

    public Feed getFeed(int i) {
        return _feed.get(i);
    }

    public int getRate() {
        return _updateRate;
    }

    public String getName() {
        return _name;
    }

    public int size() {
        return _feed.size();
    }

    public void subscribe(Observer obs) {
        _observers.add(obs);
    }

    public void unsubscribe(Observer obs) {
        _observers.remove(obs);
    }

    public void notifyObservers(Feed f) {
        for (Observer observer : _observers)
            observer.update(f, _name);
    }

    public void generateFeed(NewsFeed nf) {
        Random random = new Random();
        int randomFeed = random.nextInt(100);
        if (randomFeed <= _updateRate) {
            Feed feed = nf.getRandomFeed();
            addFeed(feed);
            notifyObservers(feed);
        }
    }

    public double analyzeFeed(String w, AnalysisBehavior ab) {
        String[] words = toString().toLowerCase().split("\\W+");
        return ab.analyze(words, w.toLowerCase());
    }

    public String toString() {
        String s = "";
        for (Feed f : _feed)
            s = s + f.getTitle() + ", " + f.getDesc() + "\n";
        return s;
    }
}

// Concrete Social Media Platforms 
class Instagram extends SocialMediaPlatform {
    public Instagram() {
        super("Instagram", 30);  // 30% update rate 
    }
}

class Facebook extends SocialMediaPlatform {
    public Facebook() {
        super("Facebook", 20);
        //Facebook pushes frequent content but not as much as TikTok which explains why its lower
        // than both insta and tiktok
    }
}

class Tiktok extends SocialMediaPlatform {
    public Tiktok() {
        super("Tiktok", 40);
        // Tiktok feeds updates rapidly in real life which explains why its % is higher than insta and facebook
    }
}

class User implements Observer {
    private String _name;
    private ArrayList<SocialMediaPlatform> _myfeeds;

    public User() {
        _myfeeds = new ArrayList<SocialMediaPlatform>();
    }

    public User(String s) {
        _name = s;
        _myfeeds = new ArrayList<SocialMediaPlatform>();
    }

    public void addPlatform(SocialMediaPlatform smp) {
        _myfeeds.add(smp);
    }

    public void update(Feed f, String s) {
        for (int i = 0; i < _myfeeds.size(); i++) {
            SocialMediaPlatform smp = _myfeeds.get(i);
            if (smp.getName().equals(s))
                _myfeeds.get(i).addFeed(f);
        }
    }

    public String toString() {
        String s = "";
        for (SocialMediaPlatform smp : _myfeeds) {
            for (int i = 0; i < smp.size(); i++) {
                Feed f = smp.getFeed(i);
                s = s + f.getTitle() + " " + f.getDesc() + "\n";
            }
        }
        return s;
    }
}

/**********************************************************/
/* Factory Pattern Interface/Classes                      */

/**********************************************************/

// Factory Creator Interface 
interface SMPFactory {
    SocialMediaPlatform createPlatform();
}

// Concrete Factory classes for each platform 
class InstagramFactory implements SMPFactory {
    public SocialMediaPlatform createPlatform() {
        return new Instagram();
    }
}

class FacebookFactory implements SMPFactory {
    public SocialMediaPlatform createPlatform() {
        return new Facebook();
    }
}

class TiktokFactory implements SMPFactory {
    public SocialMediaPlatform createPlatform() {
        return new Tiktok();
    }
}


public class Main {
    public static void main(String[] args) throws IOException {
        // Create main newsfeed from file 
        NewsFeed nf = new NewsFeed("data.txt");

        // Create SMP factories 
        SMPFactory instagramFactory = new InstagramFactory();
        SMPFactory facebookFactory = new FacebookFactory();
        SMPFactory tiktokFactory = new TiktokFactory();

        // Create the platforms container and add SMPs 
        ArrayList<SocialMediaPlatform> platforms = new ArrayList<>();
        platforms.add(instagramFactory.createPlatform());
        platforms.add(facebookFactory.createPlatform());
        platforms.add(tiktokFactory.createPlatform());

        // Create Users and subscribe


        User user1 = new User("Alice");
        User user2 = new User("Bob");
        User user3 = new User("Chris");
        user1.addPlatform(instagramFactory.createPlatform());
        user2.addPlatform(facebookFactory.createPlatform());
        user3.addPlatform(tiktokFactory.createPlatform());
        for (SocialMediaPlatform platform : platforms) {
            platform.subscribe(user1);
            platform.subscribe(user2);
            platform.subscribe(user3);
        }


        // Run a simulation to generate random feeds for the SMPs 
        for (int i = 0; i < 20; i++) {
            for (SocialMediaPlatform platform : platforms) {
                platform.generateFeed(nf);
            }
        }

        // Perform analysis
        AnalysisBehavior ab = new CountAllAnalysis();
        AnalysisBehavior ab2 = new CountIfAnalysis();

        SocialMediaPlatform insta = platforms.get(0);
        SocialMediaPlatform face = platforms.get(1);
        SocialMediaPlatform tiktok = platforms.get(2);
        System.out.println("Analysis for word: 'and' ");
        System.out.println("Does the word 'and' exist in instagram: " + insta.analyzeFeed("and", ab2));
        System.out.println("How many times does the word 'and' appear: " + insta.analyzeFeed("and", ab));

        System.out.println("Does the word 'and' exist in facebook: " + face.analyzeFeed("and", ab2));
        System.out.println("How many times does the word 'and' appear: " + face.analyzeFeed("and", ab));

        System.out.println("Does the word 'and' exist in tiktok: " + tiktok.analyzeFeed("and", ab2));
        System.out.println("How many times does the word 'and' appear: " + tiktok.analyzeFeed("and", ab));


        // Print Users' Contents
        System.out.println("---------------------------------");
        System.out.println("User contents: ");
        System.out.println("Alice: " + user1);
        System.out.println("Bob: " + user2);
        System.out.println("Chris: " + user3);
    }

} 
