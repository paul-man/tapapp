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
