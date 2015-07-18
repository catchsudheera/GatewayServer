# GatewayServer
Gateway server which is built using netty facilitates multiple client requests queued through to a single server through a single channel

The Gateway is written based on netty 4.0. Gateway maintains a single static connection to the backend server. when the connection lost with the back end
the gateway tries to reconnect instantly. Multiple client channels can connect to gateway using the front end port. All the requests are queued through the gateway
to the backend server. The implementation supports only requests that contain certain format in the payload. A string which is preceds 4-character lengh string and 
followed by a string which have the specified length. Anyone can alter this to accept different format by modifing the classes inside codecs package.
