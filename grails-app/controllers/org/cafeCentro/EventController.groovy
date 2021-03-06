package org.cafeCentro

import org.grails.plugins.elasticsearch.util.GXContentBuilder
import org.apache.commons.lang.time.StopWatch

class EventController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def search = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        def offset = params.offset ? params.int('offset') : 0
        Map searchResults
        boolean useES = params.useES?.toLowerCase() == "true"
        def stopwatch = new StopWatch()
        stopwatch.start()
        if (useES) {
            searchResults = performSearchUsingElasticSearch(params.q, params.location,offset, params.max)
        }   else {
            searchResults = performSearchUsingGORM(params.q, params.location,offset,params.max)
        }
        stopwatch.stop()
        def searchTime = stopwatch.time
        def results = searchResults.results
        def totalResultCount = searchResults.totalCount
        def paginationParams = [:]
        if (params.q) paginationParams.q = params.q
        if (params.location) paginationParams.location = params.location
        if (useES) paginationParams.useES = useES

        def start = offset + 1
        def stop = Math.min(offset+params.max, totalResultCount)

        def model = [eventInstanceList:results, eventInstanceTotal:totalResultCount,paginationParams:paginationParams,searchTime:searchTime,start:start,stop:stop]
        render(view:"list",model: model)
    }

    private def performSearchUsingElasticSearch(q,location,offset, max) {
        def coords = location ? getCoordsForLocation(location) : null

        def esQuery = {
            filtered {
                query {
                    if (q) {
                        query_string {
                            fields = ["venue.name","artists.name"]
                            query = q.encodeAsElasticSearchQuery()
                            default_operator = "AND"
                        }
                    } else {
                        match_all {}
                    }
                }
                filter {
                    if (coords) {
                        geo_distance("venue.coords":coords,distance:"20mi")
                    } else {
                        match_all {}
                    }
                }
            }
        }

//        def builder = new GXContentBuilder()
//
//        println "Generated Query: ${builder.buildAsString(esQuery)}"

        Map searchResults = Event.search(esQuery,[offset:offset,max:max,sort:"date"])

        return [results:searchResults.searchResults, totalCount:searchResults.total]
    }

    private def performSearchUsingGORM(q, location, offset, max) {
        //Using projections here as listDistinct (necessary because of the join to artists) messes up pagination
        q = q ? "%${q}%" : null
        location = location ? "%${location}%" : null
        def criteria = Event.createCriteria()
        def resultIds = criteria.list {
            order("date", "ASC")
            projections {
                distinct("id")
                property("date") //SQLite doesn't like the query without this.
            }
            if (q) {
                or {
                    "venue" {
                        ilike("name", q)
                    }
                    "artists" {
                        ilike("name", q)
                    }
                }
            }
            if (location) {
                "venue" {
                    ilike("city",location)
                }
            }
        }.collect{it[0]}

        def results = []

        if (resultIds) {
            def endValue = Math.min(offset + max, resultIds.size()) - 1
            def pagedResultIds = resultIds[offset..endValue]

            results = Event.createCriteria().list {
                order("date", "ASC")
                inList("id",pagedResultIds)

                join 'venue'
                fetchMode "artists", org.hibernate.FetchMode.SELECT
            }

        }

        def totalResultCount = resultIds.size()
        return [results:results, totalCount:totalResultCount]
    }

    private getCoordsForLocation(String location) {
        def locs = [
            "san francisco":"37.7793, -122.4192",
            "new york":"40.716667, -74"
        ]

        return locs[location.toLowerCase()]
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [eventInstanceList: Event.list(params), eventInstanceTotal: Event.count()]
    }

    def create = {
        def eventInstance = new Event()
        eventInstance.properties = params
        return [eventInstance: eventInstance]
    }

    def save = {
        def eventInstance = new Event(params)
        if (eventInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'event.label', default: 'Event'), eventInstance.id])}"
            redirect(action: "show", id: eventInstance.id)
        }
        else {
            render(view: "create", model: [eventInstance: eventInstance])
        }
    }

    def show = {
        def eventInstance = Event.get(params.id)
        if (!eventInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'event.label', default: 'Event'), params.id])}"
            redirect(action: "list")
        }
        else {
            [eventInstance: eventInstance]
        }
    }

    def edit = {
        def eventInstance = Event.get(params.id)
        if (!eventInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'event.label', default: 'Event'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [eventInstance: eventInstance]
        }
    }

    def update = {
        def eventInstance = Event.get(params.id)
        if (eventInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (eventInstance.version > version) {
                    
                    eventInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'event.label', default: 'Event')] as Object[], "Another user has updated this Event while you were editing")
                    render(view: "edit", model: [eventInstance: eventInstance])
                    return
                }
            }
            eventInstance.properties = params
            if (!eventInstance.hasErrors() && eventInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'event.label', default: 'Event'), eventInstance.id])}"
                redirect(action: "show", id: eventInstance.id)
            }
            else {
                render(view: "edit", model: [eventInstance: eventInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'event.label', default: 'Event'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def eventInstance = Event.get(params.id)
        if (eventInstance) {
            try {
                eventInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'event.label', default: 'Event'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'event.label', default: 'Event'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'event.label', default: 'Event'), params.id])}"
            redirect(action: "list")
        }
    }
}
