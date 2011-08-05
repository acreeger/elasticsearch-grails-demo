package org.cafeCentro

class VenueLocationService {

    static transactional = false

    Map venues = ["The Independent":"37.775286, -122.437611",
              "Rickshaw Stop":"37.776319, -122.420319",
              "Fox Theatre":"37.808252, -122.270480",
              "Fillmore":"37.784228, -122.432845",
              "Great American Music Hall":"37.785125, -122.418851",
              "Paramount Theatre":"37.809847, -122.268633",
              "Amnesia":"37.759298, -122.421462",
              "Cafe du nord":"37.766011, -122.430435",
              "The Grand Ballroom":"37.787729, -122.421682",
              "Hotel Utah":"37.779529,-122.397981",
              "Bowery Ballrom":"40.720760, -73.993868",
              "Brooklyn Bowl":"40.722285, -73.957461",
              "Webster Hall":"40.732329, -73.989863",
              "The 40/40 Club":"40.743159, -73.988891",
              "Rockwood Music Hall":"40.722779, -73.989071",
              "The Mercury Lounge":"40.722715, -73.986955"
]
    def getLocationForVenue(venueName) {
        return venues[venueName]
    }

}
