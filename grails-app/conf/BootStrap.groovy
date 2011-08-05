import org.cafeCentro.Venue
import org.cafeCentro.Artist
import groovy.time.TimeCategory
import org.cafeCentro.Event
import java.security.SecureRandom

class BootStrap {

    def artists = [
            "Prince",
            "Death Cab for Cutie",
            "Chromeo",
            "Geographer",
            "Friendly Fires",
            "Deadmau5",
            "Kings of Leon",
            "Muse",
            "Arcade Fire",
            "Sia",
            "Empire Of The Sun",
            "Megadeth",
            "Metallica",
            "Radiohead",
            "Adele",
            "Kate Nash",
            "Hot Chip",
            "Blondie",
            "Red Hot Chilli Peppers",
            "Girl Talk",
            "MGMT",
            "TV on the Radio",
            "The National"
    ]


    def venues = [
            [name: "The Independent",coords: "37.775286, -122.437611", city: "San Francisco"],
            [name: "Rickshaw Stop",coords: "37.776319, -122.420319", city: "San Francisco"],
            [name: "Fox Theatre",coords: "37.808252, -122.270480", city: "Oakland"],
            [name: "Fillmore",coords: "37.784228, -122.432845", city: "San Francisco"],
            [name: "Great American Music Hall",coords: "37.785125, -122.418851", city: "San Francisco"],
            [name: "Paramount Theatre",coords: "37.809847, -122.268633", city: "Oakland"],
            [name: "Amnesia",coords: "37.759298, -122.421462", city: "San Francisco"],
            [name: "Cafe du nord",coords: "37.766011, -122.430435", city: "San Francisco"],
            [name: "The Grand Ballroom",coords: "37.787729, -122.421682", city: "San Francisco"],
            [name: "Hotel Utah",coords: "37.779529,-122.397981", city: "San Francisco"],
            [name: "Bowery Ballroom",coords: "40.720760, -73.993868", city: "New York"],
            [name: "Brooklyn Bowl",coords: "40.722285, -73.957461", city: "Brooklyn"],
            [name: "Webster Hall",coords: "40.732329, -73.989863", city: "New York"],
            [name: "The 40/40 Club",coords: "40.743159, -73.988891", city: "New York"],
            [name: "Rockwood Music Hall",coords: "40.722779, -73.989071", city: "New York"],
            [name: "The Mercury Lounge",coords: "40.722715, -73.986955", city: "New York"]
    ]

    def init = { servletContext ->
        buildVenues()

        buildArtists()

        def rnd = new SecureRandom()
        1000.times {
            def date
            use (TimeCategory) {
                date = ((1 + rnd.nextInt(730)) * 24).hours.from.now
            }

            def eventArtists = []

            (1 + rnd.nextInt(3)).times {
                def added = false
                while (!added) {
                    def artist = Artist.findByName(artists[rnd.nextInt(artists.size())])
                    if (!eventArtists.contains(artist)) {
                        eventArtists << artist
                        added = true
                    }
                }
            }

            def props = [
                venue : Venue.findByName(venues[rnd.nextInt(venues.size())].name),
                artists : eventArtists,
                date : date
            ]

            new Event(props).save(failOnError:true)
        }

    }

    private def buildArtists() {
        artists.each {
            if (!Artist.findByName(it)) {
                new Artist(name: it).save(failOnError: true)
            }
        }
    }

    private def buildVenues() {
        venues.each {venueProps ->
            if (!Venue.findByName(venueProps.name)) {
                new Venue(venueProps).save(failOnError: true)
            }
        }
    }

    def destroy = {
    }
}
