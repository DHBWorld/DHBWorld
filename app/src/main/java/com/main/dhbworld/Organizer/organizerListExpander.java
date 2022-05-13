package com.main.dhbworld.Organizer;

import android.app.Activity;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.main.dhbworld.Navigation.NavigationUtilities;
import com.main.dhbworld.R;

public class organizerListExpander{
    Activity activity;

    public organizerListExpander(Activity activity){
        this.activity = activity;
    }

    public void createView(){
        activity.setContentView(R.layout.organizer_layout);

        ViewPager2 viewPager2 = activity.findViewById(R.id.organizerViewPager);
        ScreenSlidePagerAdapter adapter = new ScreenSlidePagerAdapter((FragmentActivity) activity);



    }


}
