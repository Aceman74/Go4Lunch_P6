package com.aceman.go4lunch.fragments.listView;

import com.aceman.go4lunch.api.PlacesApi;
import com.aceman.go4lunch.data.places.nearby_search.Result;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Lionel JOFFRAY - on 27/06/2019.
 */
public class ListViewFragmentTest {
    List<String> mByName = new ArrayList<>();
    List<Integer> mByDistance = new ArrayList<>();


    @Before
    public void setUp() {
        mByName.add(0,"Yoda");
        mByName.add(1,"Leon");
        mByName.add(2,"Batman");
        mByDistance.add(0,8);
        mByDistance.add(1,0);
        mByDistance.add(2,3);
    }

    /**
     * Test the custom sort by name method, reverse too.
     */
    @Test
    public void sortByName() {
        Collections.sort(mByName, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
    });
        assertEquals(mByName.get(0),"Batman");
        assertEquals(mByName.get(2),"Yoda");

        Collections.reverse(mByName);
        assertEquals(mByName.get(0),"Yoda");
        assertEquals(mByName.get(2),"Batman");
    }
    /**
     * Test the custom sort by distance method, reverse too.
     */
    @Test
    public void sortByDistance() {
        Collections.sort(mByDistance, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });
        assertEquals(mByDistance.get(0).intValue(),0);
        assertEquals(mByDistance.get(2).intValue(),8);


        Collections.reverse(mByDistance);
        assertEquals(mByDistance.get(0).intValue(),8);
        assertEquals(mByDistance.get(2).intValue(),0);
    }
}