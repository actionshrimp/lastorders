#!/usr/bin/python
import _mysql as mysql
import time, urllib, urllib2, json
#One-off latitude-longitude grabber from postcodes for any items in the DB.

def get_postcodes_from_db():
	#Password needs to be extracted out. Here for now for testing purposes
	db = mysql.connect(host='localhost', user='lld', passwd='PASSWORD', db='latelondondrinks')

	db.query('''SELECT id, postcode FROM venue WHERE NOT (geo_postcode <=> postcode)''')
	dbresults = db.store_result()

	results = {}

	for r in dbresults.fetch_row(maxrows=0):
		print r
		results[r[0]] = {'postcode': r[1]}

	db.close()

	return results

def get_latlon_from_google(postcode):
	address = urllib.quote_plus(postcode + ', UK')
	url = 'http://maps.googleapis.com/maps/api/geocode/json?address=' + address + '&sensor=false'	

	io = urllib2.urlopen(url)
	data = json.load(io)

	latlon = data['results'][0]['geometry']['location']
	print latlon
	return (str(latlon['lat'] * 1000000), str(latlon['lng'] * 1000000))

def update_latlon_in_db(locations):
	#Password needs to be extracted out. Here for now for testing purposes
	db = mysql.connect(host='localhost', user='lld', passwd='PASSWORD', db='latelondondrinks')

	for i, l in locations.iteritems():
		print i, r
		q = "UPDATE venue SET geo_postcode = '" + l['geo_postcode'] + "', latitude = " + l['lat'] + ", longitude = " + l['lon'] + " WHERE id = " + i
		db.query(q)
		print 'Updated ' + i + ' in DB: (' + l['geo_postcode'] + ', ' + l['lat'] + ', ' + l['lon'] + ')'

	db.close()

if __name__ == '__main__':
	results = get_postcodes_from_db()
	locations = {}
	for i, r in results.iteritems():
		try:
			(lat, lon) = get_latlon_from_google(r['postcode'])
			locations[i] = {'lat': lat, 'lon': lon, 'geo_postcode': r['postcode']}
			print locations[i]['geo_postcode'] + ' translated to: (' + locations[i]['lat'] + ', ' + locations[i]['lon'] + ')'
			time.sleep(2)
		except:
			print "Couldn't translate: " + r['postcode']
			pass

	update_latlon_in_db(locations)
