package org.cafeCentro

class Venue {

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
