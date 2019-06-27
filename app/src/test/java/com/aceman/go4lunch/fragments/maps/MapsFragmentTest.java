package com.aceman.go4lunch.fragments.maps;

import com.aceman.go4lunch.utils.DateSetter;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;

/**
 * Created by Lionel JOFFRAY - on 27/06/2019.
 */
public class MapsFragmentTest {
    int mDistanceRounded;
    int mDistanceBefore;
    Double mRating;
    int mStarNumber;
    String mBaseDate;
    String mTest;
    int hour = 11;

    @Before
    public void setUp() {
        mDistanceBefore = 1488;
        mRating = 3.4;
        mBaseDate = DateSetter.getFormattedDate();

    }

    /**
     * Test the distance and rating custom method calculation.
     */
    @Test
    public void setDistanceAndRating() {
        mDistanceRounded = Math.round(mDistanceBefore);
        mDistanceRounded = ((mDistanceRounded + 4) / 5) * 5;    //  Round distance to 5m

        assertNotEquals(mDistanceBefore, mDistanceRounded);
        assertEquals(mDistanceRounded, 1490);


        if (mRating != null) {
            if (mRating >= 1 && mRating <= 2.4) {
                mStarNumber = 1;
            }
            if (mRating >= 2.5 && mRating <= 3.9) {
                mStarNumber = 2;
            }
            if (mRating >= 4 && mRating <= 5) {
                mStarNumber = 3;
            }
        }
        assertNotEquals(1, mStarNumber);
        assertNotEquals(3, mStarNumber);
        assertEquals(2, mStarNumber);
    }

    /**
     * Test the hour checking method.
     */
    @Test
    public void isEatingHereHourCheck() {
        String date = DateSetter.getFormattedDate();
        if (mBaseDate.equals(date) && hour < 13) {  // Hour is set to 11

            mTest = "Before 1PM";
        } else {
            mTest = "After 1PM";
        }
        assertSame("Before 1PM", mTest);

        hour = 14;
        if (mBaseDate.equals(date) && hour < 13) {  // Hour is forced to 14

            mTest = "Before 1PM";
        } else {
            mTest = "After 1PM";
        }
        assertSame("After 1PM", mTest);
    }
}