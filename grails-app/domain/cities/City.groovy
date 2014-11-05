package cities

import com.vividsolutions.jts.geom.Point
import org.hibernate.spatial.GeometryType

class City
{
// CITY_NAME,COUNTRY,POP,CAP,LONGITUDE,LATITUDE
  String name
  String country
  Integer population
  Boolean capital
  Double longitude
  Double latitude
  Point location

  static constraints = {
    name()
    country()
    population()
    capital()
    longitude()
    latitude()
    location()
  }

  static mapping = {
    location type: GeometryType, sqlType: 'geometry(POINT, 4326)'
  }
}
