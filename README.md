# fRead
fRead is an Android app that aims to improve the reading experience on mobile. 
It creates a clean reading environment for those who love reading web articles but hate all the clutter. 

https://play.google.com/store/apps/details?id=com.freadapp.fread

## Video Demo
- https://youtu.be/kyvVIaDHxsI

## Read In Peace
- Share a web browser URL with the fRead Mode Activity and watch all of the clutter disappear. It's like magic.
- No time to read the entire article? No problem. Click on the bookmark icon to save the article to your account for future reading.
- Words are great, but sometimes we don't know the definitions of them. Use the Dictionary tab to find the definition of a word via the Oxford dictionary.
- Add tags to your article for easy filtering. You can also create, edit and delete your tags. 
- Offline? Even without an internet connection, you can read your saved articles with fRead.

## Development
- fRead does it de-cluttering magic by using Aylien's Text Analysis API. More here; https://aylien.com/text-api/
- The Retrofit library was used to connect the Aylien API to the Java classes for a type-safe environment. 
- For data persistence, I connected fRead to Google's Firebase Suite to utilize the Realtime Firebase Database
- Users can authenticate through a Google, Facebook or an Email account. This was implemented for easy user onboarding. 
- Primary data models are Articles, Tags, and Users. These were reflected in the Realtime Database Rules set. 
- For the dictionary functionality, I used the Oxford API. An AsynTask was invoked, the JSON was parsed and then displayed on the UI thread. 

## Screen Shots
- https://imgur.com/a/4PDc3bQ
