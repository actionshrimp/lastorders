import sqlobject

sqlobject.sqlhub.processConnection = sqlobject.connectionForURI('mysql://dave@actionshrimp.com/latelondondrinks')

class Venue(sqlobject.SQLObject):
    class sqlmeta:
        fromDatabase = True

class VenuePrice(sqlobject.SQLObject):
    class sqlmeta:
        fromDatabase = True

    venue = sqlobject.ForeignKey('Venue')


class VenueTime(sqlobject.SQLObject):
    class sqlmeta:
        fromDatabase = True

    venue = sqlobject.ForeignKey('Venue')

class VenueType(sqlobject.SQLObject):
    class sqlmeta:
        fromDatabase = True

class Day(sqlobject.SQLObject):
    class sqlmeta:
        fromDatabase = True
