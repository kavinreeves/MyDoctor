package com.example.mydoctor.mydoctor.Pagers;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mydoctor.mydoctor.Login.Login;
import com.example.mydoctor.mydoctor.R;
import com.viewpagerindicator.CirclePageIndicator;

public class Main_Pager extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_pager_layout);


        CirclePageIndicator circle = (CirclePageIndicator) findViewById(R.id.titles);
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        Button btnSkip = (Button) findViewById(R.id.btnSkip);


        FragAdapter fragAdapter = new FragAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragAdapter);
        circle.setViewPager(viewPager);
        circle.setStrokeColor(Color.parseColor("#FFFFFF"));
        circle.setRadius(15);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Main_Pager.this, Login.class));
            }
        });

        circle.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 3) {

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public class FragAdapter extends FragmentPagerAdapter {


        public FragAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new Pager1();
                case 1:
                    return new Pager2();
                case 2:
                    return new Pager3();
                case 3:
                    return new Pager4();
                default:
                    return new Pager1();
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
