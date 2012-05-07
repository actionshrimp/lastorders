#!/usr/bin/python
import xmlrpclib

server = xmlrpclib.Server("http://localhost:7080/")

lat = 51.51885
lon = -0.10812
print("What's near the fake location? ( " + str(lat) + ", " + str(lon) + " ) ")
results = server.get_locations('APIKEY', lat, lon)
print(str(results))
