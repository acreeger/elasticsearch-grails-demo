package org.cafeCentro

class EventController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def search = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        def criteria = Event.createCriteria()
        def q = params.q ? "%${params.q}%" : null
        def location = params.location ? "%${params.location}%" : null
        def resultIds = criteria.list {
            order("date","ASC")
            projections {
                distinct("id")
                property("date")
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
            def offset = params.offset ? params.int('offset')  : 0
            def endValue = Math.min(offset + params.max - 1,resultIds.size() - 1)
//            println "offset: $offset, endValue: $endValue"
            def pagedResultIds = resultIds[offset..endValue]

            results = Event.createCriteria().list {
                order("date", "ASC")
                inList("id",pagedResultIds)
            }
        }

        def paginationParams = [:]
        if (params.q) paginationParams.q = params.q
        if (params.location) paginationParams.location = params.location

        def model = [eventInstanceList:results, eventInstanceTotal:resultIds.size(),paginationParams:paginationParams]
        render(view:"list",model: model)
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
