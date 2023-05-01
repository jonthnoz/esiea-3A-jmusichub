# esiea-3A-jmusichub

### Music library project in java following a client-server structure
### *Maven* project with executable unit tests and doc generation
#### Audio .wav files are in a files folder in the resources and the library is defined by 3 xml files: albums, playlists, elements.
see [*report.pdf*](https://github.com/jonthnoz/esiea-3A-jmusichub/blob/2de282e9939f5aa9540a2148a102b0606b7def4e/complementary/Report.pdf) in *complentary* for more information.
___

###### V1.0.0

Very first artifact. Only one jar file is created and can be executed on a client or a server side. 
One must pass an argument to the jar or the main function exectution :
+ **server** to start a server instance to accept further client connexions
+ **client** to launch the client program
+ **serverConsole** to start a admin console on a server side with access to the resources files

It is not currently possible to read external files from the jar execution so one would directly use the musichub.main.Main class from the **classes** directory in the **target** folder generated to use this feature.
