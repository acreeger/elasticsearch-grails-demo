package org.cafeCentro

class Event {

    static hasMany = [artists:Artist]

    Date lastUpdated
    Date dateCreated

    Date startTime
    Venue venue
    String name

    static constraints = {
        startTime(min: new Date())
        name(blank: false)
    }
}
