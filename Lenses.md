# Lenses #

My initial plan was to create an entry in the internal list of all lenses on the market. However, once I saw the number I was dealing with (hundreds!) I realised the list would be too long for the user to practically find and select one from.

Plan B, then, was to create a small set of theoretical lenses which between them cover the focal length and aperture ranges of all the lenses a user might be using. I used this page:

http://www.the-digital-picture.com/Reviews/

as my information source.

# F-Stops #

v0.90b of the software had a hardcoded set of F-stops which was used for each and every lens. As I realised that some lenses used full stops, or third stops, or quarter stops, or half stops, I just added those F-stops to the slider. Eventually the slider had dozens of positions in it, and it was unusable. It was too difficult to move it to say, f/1.8 because f/1.7 and f/2.0 where just too close.

Since most lenses (at least the ones I'm familiar with) use full F-stops, plus one other range, such as third F-stops, it became obvious that I didn't need to include half-stops or quarter-stops for all lenses.

v0.91b of the software gained the ability to define the range of F-stops that the aperture slider will move between. Each lens can have one or more of FULL, THIRD, HALF and/or QUARTER, as defined [here](http://en.wikipedia.org/wiki/F-number#Fractional_stops). The default lens definitions all have FULL and THIRD ranges.

# Adding More Lens Definitions #

To add more lenses, or to reset the list to just the exact lenses you use, edit _res/xml/lenses_. The format of the data file should be self explanatory. Remember to set one of them to _default="true"_, and if you get crashes on startup ensure all your lenses have starting focal lengths and apertures inside the defined range.

# Gaps #

If there are gaps in my list which mean a physical lens can't be represented with the default set of definitions, raise an issue giving details. I'll add it to the build.