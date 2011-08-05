package org.cafeCentro

class ArtistController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [artistInstanceList: Artist.list(params), artistInstanceTotal: Artist.count()]
    }

    def create = {
        def artistInstance = new Artist()
        artistInstance.properties = params
        return [artistInstance: artistInstance]
    }

    def save = {
        def artistInstance = new Artist(params)
        if (artistInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'artist.label', default: 'Artist'), artistInstance.id])}"
            redirect(action: "show", id: artistInstance.id)
        }
        else {
            render(view: "create", model: [artistInstance: artistInstance])
        }
    }

    def show = {
        def artistInstance = Artist.get(params.id)
        if (!artistInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'artist.label', default: 'Artist'), params.id])}"
            redirect(action: "list")
        }
        else {
            [artistInstance: artistInstance]
        }
    }

    def edit = {
        def artistInstance = Artist.get(params.id)
        if (!artistInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'artist.label', default: 'Artist'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [artistInstance: artistInstance]
        }
    }

    def update = {
        def artistInstance = Artist.get(params.id)
        if (artistInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (artistInstance.version > version) {
                    
                    artistInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'artist.label', default: 'Artist')] as Object[], "Another user has updated this Artist while you were editing")
                    render(view: "edit", model: [artistInstance: artistInstance])
                    return
                }
            }
            artistInstance.properties = params
            if (!artistInstance.hasErrors() && artistInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'artist.label', default: 'Artist'), artistInstance.id])}"
                redirect(action: "show", id: artistInstance.id)
            }
            else {
                render(view: "edit", model: [artistInstance: artistInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'artist.label', default: 'Artist'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def artistInstance = Artist.get(params.id)
        if (artistInstance) {
            try {
                artistInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'artist.label', default: 'Artist'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'artist.label', default: 'Artist'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'artist.label', default: 'Artist'), params.id])}"
            redirect(action: "list")
        }
    }
}
