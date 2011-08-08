package org.cafeCentro

class Venue {

    static searchable = {
        root false
    }
    def venueLocationService

    String name
    String coords
    String city

    static constraints = {
    }

    public String toString() {"$name, $city"}

//    def beforeInsert = {
//       coords = venueLocationService.getLocationForVenue(name)
//    }
}
