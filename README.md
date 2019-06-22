# ViewPagerSlideShow
A Utility that given a ViewPager can create and start a slide show or auto next items.

## Usage: 
```
val slideShowManger = SlideShowManager()
slideShowMangaer.setupWithViewPager(viewPager=vpager, exitOnInteraction=true, stopAfter=4)
slideShowManager.startPresentation()
```

To Stop or pause
`slideShowManager.stopPresentation()`

It is recommended to enable `exitOnInteraction=true` this will stop presentation when user tries to interactive with viewpager.
ViewPager Animation speed is also changed when user tries to interact with it.

Once All the item have been slided slideshow is stopped and moved to first item. You may tweek code for infinite slideshow by passing a viewpager with circular/cyclic adapter. Only one thing to remeber that `stopAfter=0` must be set in this case.

`stopAfter=4` stop sliding after 4 items. When set to 0 it is *ignored* and slideshow continues till this end.
