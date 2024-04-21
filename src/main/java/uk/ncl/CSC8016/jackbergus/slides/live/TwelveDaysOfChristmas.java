package uk.ncl.CSC8016.jackbergus.slides.live;

import java.util.*;

public class TwelveDaysOfChristmas {
    public volatile static String giftList;

    public static void putGift(String giftId) {
        giftList += (giftId + "\n");
    }

    public volatile static int startWith = 3;
    public static synchronized void putGift2 ( String giftId ) {
        do {
            if ( giftId . startsWith ( startWith + "" ) ) {
                giftList += ( giftId + "\n" ) ;
                startWith --;
                TwelveDaysOfChristmas . class . notifyAll () ;
                return ;
            } else {
                try { TwelveDaysOfChristmas . class . wait () ;
                } catch ( InterruptedException e ) { }
            }
        } while ( true ) ;
    }

    public static void main(String[] args) throws InterruptedException {
        String gifts = "1 Partridge in a Pear Tree,2 Turtle Doves,3 French Hens";
        String[] giftArray = gifts.split(",");
        Set<String> setOfPossibleOutputs = new HashSet<>();
        for (int j = 0; j < 200; j++) {
            List<Thread> givers = new ArrayList<>(giftArray.length);
            giftList = "On a random day of Christmas \nmy true love sent to me :\n ";
            for (int i = 0; i < giftArray.length; i++) {
                final var Integer = i;
                givers.add(new Thread(() -> putGift2(giftArray[Integer]), " Donor - " + i));
            }
            Collections.shuffle(givers);
            givers.forEach(Thread::start);
            for (Thread t : givers) t.join();
            setOfPossibleOutputs.add(giftList);
            startWith = 3;
        }
        setOfPossibleOutputs.forEach(System.out::println);
    }
}