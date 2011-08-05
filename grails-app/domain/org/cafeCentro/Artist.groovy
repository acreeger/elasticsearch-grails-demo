package org.cafeCentro

class Artist {

    String name;

    static constraints = {
        name(blank: false)
    }

    public String toString() {
        name
    }
}
