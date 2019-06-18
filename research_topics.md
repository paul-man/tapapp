# Research Topics
This page is just a place for me to keep track of useful blog posts/stackoverflow answers/android docs/etcs...

### Fading a view in and out
- SO: https://stackoverflow.com/questions/16800716/android-fade-view-in-and-out
- Takeaway:
    ```
    private AlphaAnimation fadeViewAnim = new AlphaAnimation(1.0f, 0.0f);
    ...
    {
      ...
      fadeViewAnim.setDuration(1000);
      fadeViewAnim.setRepeatCount(NUM_REPEATS);
      fadeViewAnim.setRepeatMode(Animation.REVERSE);
      view.startAnimation(fadeViewAnim);
      ...
    }
    ```
_____

### Fancy Dialog Boxes
- Currently I am using [EZDialog](https://android-arsenal.com/details/1/7610) for my dialogs
  - Main issue is that I can only put a string inside the dialog content
- Let's try DialogPlus
  - It allows you to pass a view in to use as the content of the dialog
  - It also allows you to use animations for dialog opening/closing
  - [Github](https://github.com/orhanobut/dialogplus?utm_source=android-arsenal.com&utm_medium=referral&utm_campaign=1413)
  - [Android-arsenal](https://android-arsenal.com/details/1/1413)
  
_____

### Logging in Android Studio
The main options are:
  - DEBUG: Log.d
  - ERROR: Log.e
  - INFO: Log.i
  - VERBOSE: Log.v
  - WARN: Log.w
  
Examle: `Log.d("myTag", "This is my message");`

- https://stackoverflow.com/questions/16780294/how-to-print-to-the-console-in-android-studio

_____

### Difficulty curve
The difficulty curve refers to the grace period before and after the next expected tap.

First I implemented a static grace period that could go up and down based on the difficulty, but would be the same for all BPM in the same diffculty. This is clearly a bad idea because anything good for low BPM will be way to easy for higher BPM, and anything good for high bpm will be too hard for lower BPM.

Then I thought of making a linear relation. This also did not work for the same reason as above, but it was slightly better.

Currently I am thinking of using curves. Quatratic was the obvious choice just because I don't do math anymore and a parabola was the first curve I learned. But now I think I'll use an exponential.

To determine the curve, we need to pick min/max BPM and the grace period for each.

DIFFUCULTY: EASY

| BPM           | GRACE         |
| ------------- |:-------------:|
| 20            | 500           |
| 200           | 120           |

<br>DIFFUCULTY: NORMAL

| BPM           | GRACE         |
| ------------- |:-------------:|
| 20            | 400           |
| 200           | 80            |

<br>DIFFUCULTY: HARD

| BPM           | GRACE         |
| ------------- |:-------------:|
| 20            | 250           |
| 200           | 45            |

<br>DIFFUCULTY: EXTREME

| BPM           | GRACE         |
| ------------- |:-------------:|
| 20            | 190           |
| 200           | 20            |

The formula to determine the exponential curve we need 2 points. We can use BPM as `X` and GRACE as `Y`. Let's use the NORMAL difficulty as an example.<br>
p1 (x=20, y=400) , p2 (x=200, y=80)

y = ab<sup>x</sup>
We need to solve for `a` and `b`, let's plug in our points

400 = ab<sup>20</sup>
80 = ab<sup>200</sup>

Divide

400/80 = 5

ab<sup>20</sup> / ab<sup>200</sup> -> `a`'s cancel out -> 1/b<sup>180</sup>

5 = 1/b<sup>180</sup>

This then becomes b = the 180th root of 1/5, not sure how to write that in markdown so here is the java code: Math.pow(180, 1.0 / 5.0);

b = ~0.991

Now we plug in b to get a, and then we can use this formula to find any grace period (y) given and BPM (x)

400 = a(~0.991)<sup>20</sup>
400 = a(~0.836)
400/(~0.836) = 478.325 = a

a = 478.325
b = 0.991

so... y = 478.325(0.991)<sup>x</sup>

Now we can create a function that performs this whole operation at app runtime for each difficulty and store a and b.

Then each time the user changes the BPM, we plug it in as x and solve for y and that is the new grace period.

_____

### Playing sound on "Tap"
https://stackoverflow.com/questions/18459122/play-sound-on-button-click-android

https://freesound.org/search/?q=metronome&f=&s=duration+asc&advanced=0&g=1

_____

### Displaying duration based on milliseconds
https://repl.it/repls/KhakiShadowyConditionals

```
import java.util.concurrent.TimeUnit;
class Main {
  public static void main(String[] args) {
    long millis = 36350;
    String hms = String.format("%02dm:%02ds:%02dms",
            TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
            TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)),
            millis % 1000);
    System.out.println(hms); 
  }
}
```
