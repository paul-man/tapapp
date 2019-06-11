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
  
