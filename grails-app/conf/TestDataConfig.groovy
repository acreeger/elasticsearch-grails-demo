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

def rnd = new Random(new Date().time)

testDataConfig {
    sampleData {
        'org.cafeCentro.Venue' {
            name = {venues.keySet().toArray()[rnd.nextInt(venues.size())]}
        }
        'org.cafeCentro.Artist' {
            def artistNames = [
                    "Prince",
                    "Death Cab for Cutie",
                    "Chromeo",
                    "Geographer",
                    "Friendly Fires",
                    "Deadmau5",
                    "Kings of Leon",
                    "Muse",
                    "Arcade Fire",
                    "Sia"
            ]
            name = {artistNames[rnd.nextInt(artistNames.size())]}
        }

    }
}

/*
// sample for creating a single static value for the com.foo.Book's title property:
// title for all Books that we "build()" will be "The Shining", unless explicitly set

testDataConfig {
    sampleData {
        'com.foo.Book' {
            title = "The Shining"
        }
    }
}
*/

/*
// sample for creating a dynamic title for com.foo.Book, useful for unique properties:
// just specify a closure that gets called

testDataConfig {
    sampleData {
        'com.foo.Book' {
            def i = 1
            title = {-> "title${i++}" }   // creates "title1", "title2", ...."titleN"
        }
    }
}
*/

/*
// When using a closure, if your tests expect a particular value, you'll likely want to reset
// the build-test-data config in the setUp of your test, or in the test itself.  Otherwise if
// your tests get run in a different order you'll get different values

// (in test/integration/FooTests.groovy)

void setUp() {
    grails.buildtestdata.TestDataConfigurationHolder.reset()
}
*/


/*
// if you'd like to disable the build-test-data plugin in an environment, just set
// the "enabled" property to false

environments {
    production {
        testDataConfig {
            enabled = false
        }
    }
}
*/