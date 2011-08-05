import org.cafeCentro.*

15.times {
def v = Venue.build()
println "${v.name}, ${v.coords}"

def a = Artist.build()
println a.name
}