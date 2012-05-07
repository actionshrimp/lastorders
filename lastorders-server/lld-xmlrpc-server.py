#!/usr/bin/python
from twisted.web import xmlrpc, server

import time, datetime
import _mysql as mysql

class LateLondonDrinksXMLRPC(xmlrpc.XMLRPC):
	def xmlrpc_echo(self, x):
		return x

	def xmlrpc_get_locations(self, key, lat, lon, types, limit):
		print 'Kicked off'
		if key == 'APIKEY':
			current_day = int(datetime.datetime.now().strftime("%w"))
			if current_day == 0:
				current_day = 7

			#Again extract credentials out
			db = mysql.connect(host='localhost', user='lld', passwd='PASSWORD', db='latelondondrinks')
			query = '''SELECT 
				venue.id, 
				venue.name, 
				venue_type.shortname, 
				venue.latitude, 
				venue.longitude, 
				venue.distance,
				venue_price.price price,
				venue_time.time time
			FROM (
				SELECT 
				venue.id, 
				venue.name, 
				venue.type, 
				venue.latitude, 
				venue.longitude, 
				''' + self.sql_haversine(lat, lon, 'venue.latitude / 1000000.', 'venue.longitude / 1000000.') + ''' distance 
				FROM venue
			) venue 
			JOIN venue_type ON venue.type = venue_type.id 
			JOIN venue_price ON venue.id = venue_price.venue_id AND venue_price.day = ''' + str(current_day) + '''
			JOIN venue_time ON venue.id = venue_time.venue_id AND venue_time.day = ''' + str(current_day) + '''
			WHERE UPPER(venue_type.shortname) IN ("''' + '''", "'''.join(types + ['UNKNOWN']) + '''")
			ORDER BY venue.distance
			LIMIT ''' + str(limit)

			print query
			db.query(query)
			dbresults = db.store_result()

			results = {}

			for r in dbresults.fetch_row(maxrows=0):
				print r
				results[r[0]] = {'name': r[1], 'type': r[2], 'latitude': r[3], 'longitude': r[4], 'distance': r[5], 'price': r[6], 'time': r[7]}

			db.close()
			return results

	def sql_haversine(self, latA, lonA, latB, lonB):
		items = {'latA': latA, 'lonA':lonA, 'latB':latB, 'lonB':lonB}
		for item in items.keys():
			items[item] = "RADIANS(" + str(items[item]) + ")"

		return "6367.45 * ACOS(SIN(" + items['latA'] + ") * SIN(" + items['latB'] + ") + COS(" + items['latA'] + ") * COS(" + items['latB'] + ") * COS(" + items['lonA'] + " - " + items['lonB'] + "))";

if __name__ == '__main__':
	from twisted.internet import reactor
	r = LateLondonDrinksXMLRPC()
	reactor.listenTCP(7080, server.Site(r))
	reactor.run()
