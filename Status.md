# v0.92beta Released #

This update fixes [issue 1](https://code.google.com/p/android-depth-of-field-gpl/issues/detail?id=1), which saw a crash if the user deleted a tab then created a new one with the same name, and [issue 2](https://code.google.com/p/android-depth-of-field-gpl/issues/detail?id=2), which saw a crash when one of the spinners in the new tab dialog was dropped, then the phone rotated.

Both issues are Android bugs and both have been fixed with crude workarounds. I'm hoping the workarounds work in all cases.

# v0.91beta Released #

This update fixes the issues of accuracy caused by using general f-stop numbers instead of the exact values based on sqrt(2). I also fixed the rounding error in the calculation code.

I had a go at improving the usability of the aperture slider. It's better and now supports full, quarter, third and half stops.

# v0.9beta Released #

The project is in beta, and isn't currently in the Android Marketplace. I'd like to iron any bugs out before it goes there, and perhaps get some feedback on my choice of [bodies](Bodies.md), [lenses](Lenses.md) and [ranges](Ranges.md).

Instructions to pull the source into Eclipse and build it for your phone are [here](BuildInstructions.md).