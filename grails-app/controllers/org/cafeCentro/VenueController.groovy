package org.cafeCentro

class VenueController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [venueInstanceList: Venue.list(params), venueInstanceTotal: Venue.count()]
    }

    def create = {
        def venueInstance = new Venue()
        venueInstance.properties = params
        return [venueInstance: venueInstance]
    }

    def save = {
        def venueInstance = new Venue(params)
        if (venueInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'venue.label', default: 'Venue'), venueInstance.id])}"
            redirect(action: "show", id: venueInstance.id)
        }
        else {
            render(view: "create", model: [venueInstance: venueInstance])
        }
    }

    def show = {
        def venueInstance = Venue.get(params.id)
        if (!venueInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'venue.label', default: 'Venue'), params.id])}"
            redirect(action: "list")
        }
        else {
            [venueInstance: venueInstance]
        }
    }

    def edit = {
        def venueInstance = Venue.get(params.id)
        if (!venueInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'venue.label', default: 'Venue'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [venueInstance: venueInstance]
        }
    }

    def update = {
        def venueInstance = Venue.get(params.id)
        if (venueInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (venueInstance.version > version) {
                    
                    venueInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'venue.label', default: 'Venue')] as Object[], "Another user has updated this Venue while you were editing")
                    render(view: "edit", model: [venueInstance: venueInstance])
                    return
                }
            }
            venueInstance.properties = params
            if (!venueInstance.hasErrors() && venueInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'venue.label', default: 'Venue'), venueInstance.id])}"
                redirect(action: "show", id: venueInstance.id)
            }
            else {
                render(view: "edit", model: [venueInstance: venueInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'venue.label', default: 'Venue'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def venueInstance = Venue.get(params.id)
        if (venueInstance) {
            try {
                venueInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'venue.label', default: 'Venue'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'venue.label', default: 'Venue'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'venue.label', default: 'Venue'), params.id])}"
            redirect(action: "list")
        }
    }
}
