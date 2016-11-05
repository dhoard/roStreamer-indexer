package com.aquafrog.rostreamer.util;

import com.rostreamer.util.NaturalOrderComparator;
import junit.framework.TestCase;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Doug on 10/7/2016.
 */
public class NaturalOrderComparatorTestCase extends TestCase {

    public void test() {
        String [] strings = new String [] {
                "Rambo.jpg",
                "z.jpg",
                "b.jpg",
                "Rambo 4.jpg",
                "Rambo - First Blood Part II.jpg",
                "Rambo - First Blood.jpg",
                "Red 1.jpg",
                "Red 2.jpg"
        };

        List<Object> list = new ArrayList<Object>();

        for (int i=0; i<strings.length; i++)
        {
            list.add(new File(strings[i]));
        }

        Collections.shuffle(list);
        Collections.sort(list, new NaturalOrderComparator());

//        for (Object string : list)
//        {
//            System.out.println(string);
//        }
    }

    public void test2() {
        String [] strings = new String [] {
                "Person Of Interest",
                "Property Brothers",
                "Quantico",
                "Reign",
                "Scandal",
                "Sherlock",
                "Supernatural",
                "The 100",
                "The Bible",
                "The Blacklist",
                "The Flash",
                "Arrow",
                "Blindspot",
                "Call The Midwife",
                "Containment",
                "Downton Abbey",
                "FPU",
                "Game Of Silence",
                "Misc",
                "Mr. Selfridge",
                "Outlander",
                "Outsiders",
                "The Goldbergs",
                "The Paradise",
                "Vampire Diaries",
                "War and Peace",
                "When Calls The Heart",
                "Z-Nation"
        };

        List<File> list = new ArrayList<File>();

        for (int i=0; i<strings.length; i++) {
            list.add(new File(strings[i]));
        }

        Collections.shuffle(list);
        Collections.sort(list, new NaturalOrderComparator());

//        for (Object string : list)
//        {
//            System.out.println(string);
//        }
    }
}
