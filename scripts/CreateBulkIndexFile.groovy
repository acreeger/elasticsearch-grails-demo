import org.cafeCentro.Event
import org.hibernate.SessionFactory
import org.hibernate.FlushMode
import grails.converters.JSON
import groovyx.gpars.GParsPool
import grails.util.GrailsUtil

SessionFactory sessionFactory = ctx.getBean("sessionFactory")
def session = sessionFactory.getCurrentSession()
session.flushMode = FlushMode.NEVER

def eventCount = Event.count()

def outputFile = new File("event_dump_${new Date().format('yyyy-MM-dd_HHmmss')}.json")
println "Exporting $eventCount events to $outputFile"

println "Processing mappings..."



println "Processing events..."
// { "index" : { "_index" : "test", "_type" : "type1", "_id" : "1" } }

//get events in pagination
def offset = 0
def max = 10000

def failedEvents = []

while (offset < eventCount) {
    def sb = new StringBuffer()
    def stop = Math.min(offset + max,eventCount)
    println "Processing events $offset to $stop of $eventCount"
    def events = Event.list(offset:offset,max:max)

    //TODO: Gpars (but use StringBuffer
    GParsPool.withPool(5) {
        events.eachParallel {
            try {
                def indexObject = [index:['_index':'org.cafecentro','_type':'event',"_id" : it.id.toString()]]
                def indexCommandJSON = (indexObject as JSON).toString(false)
                def eventSource = (getEventSource(it) as JSON).toString(false)

                //get object representing source JSON

                def stringToAppend = "${indexCommandJSON}\n${eventSource}\n"

                sb << stringToAppend
            } catch(Exception ex) {
                println "Got an error while processing event ${it.id}: ${ex.message}"
                failedEvents << it.id
            }
        }
    }

    session.clear()
    outputFile << sb.toString()
    offset += max
}

private def getEventSource(event) {

    //Maybe add name?
    def venue = event.venue
    def result = [artists:[],'date':event.date.format("yyyy-MM-dd'T'HH:mm:ss.S'Z'"),
            venue:[
                    id:venue.id,
                    'class': 'org.cafeCentro.Venue',
                    city: venue.city,
                    coords: venue.coords,
                    name:venue.name
            ]
    ]

    event.artists.each { artist ->
        result.artists << [
                id:artist.id,
                'class': 'org.cafeCentro.Artist',
                name: artist.name
        ]
    }

    return result
}
//for each even set id on indexObject
//get json source
//write json followed by \n (prettyPrint:false)



