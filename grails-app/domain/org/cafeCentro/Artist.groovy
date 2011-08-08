package org.cafeCentro

class Artist {

    static searchable = {
        root false
    }

    String name;

    static constraints = {
        name(blank: false)
    }

    public String toString() {
        name
    }
}
