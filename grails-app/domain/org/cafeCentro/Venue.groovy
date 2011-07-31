package org.cafeCentro

class Venue {

    String name
    String address1
    String address2
    String city
    String region
    String zipCode
    String coords

    static constraints = {
        zipCode(matches: /\d{5}/)
    }
}
