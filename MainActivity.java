package com.example.cp_cop_0621;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class MainActivity extends Activity {

    public static boolean isStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isStart) {
            Intent i = new Intent(MainActivity.this, MenuActivity.class);
            i.setAction(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        } else {

            isStart = true;
            Intent i = new Intent(this, WeatherService.class);
            startService(i);

            final LinearLayout logo1 = findViewById(R.id.layout_logo1);
            final LinearLayout logo2 = findViewById(R.id.layout_logo2);
            final ProgressBar progress = findViewById(R.id.progress_bar);
            ValueAnimator vm = ValueAnimator.ofFloat(0f, 3f);
            vm.setDuration(2500);
            vm.setInterpolator(new AccelerateInterpolator());
            vm.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    logo1.setAlpha((Float) animation.getAnimatedValue());
                    logo2.setAlpha((Float) animation.getAnimatedValue() / 2);
                    progress.setAlpha((Float) animation.getAnimatedValue() / 3);
                }
            });
            vm.start();

            final ValueAnimator vm3 = ValueAnimator.ofFloat(3f, 0f);
            vm3.setDuration(900);
            vm3.setInterpolator(new AccelerateInterpolator());
            vm3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    logo1.setAlpha((Float) animation.getAnimatedValue());
                    logo2.setAlpha((Float) animation.getAnimatedValue() / 2);
                    progress.setAlpha((Float) animation.getAnimatedValue() / 3);
                    if ((Float) animation.getAnimatedValue() == 0) {
                        Intent i = new Intent(MainActivity.this, MenuActivity.class);
                        i.setAction(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                    }
                }
            });

            ValueAnimator vm2 = ValueAnimator.ofInt(0, 100);
            vm2.setDuration(3000);
            vm2.setInterpolator(new AccelerateInterpolator());
            vm2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    progress.setProgress((Integer) animation.getAnimatedValue());
                    if ((Integer) animation.getAnimatedValue() == 100) vm3.start();
                }
            });
            vm2.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
