import cities.City

import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.GeometryFactory
import com.vividsolutions.jts.geom.PrecisionModel
import org.geotools.factory.Hints

class BootStrap
{
  def messageSource

  def init = { servletContext ->
    if ( City.count() == 0 )
    {
      City.withTransaction {
        def csvFile = 'cities.csv' as File
        def columnNames = null

        def gf = new GeometryFactory( new PrecisionModel(), 4326 );

        csvFile.eachLine { line ->
          def tokens = line.split( ',' )

          if ( columnNames )
          {
            def city = new City(
                name: tokens[0],
                country: tokens[1],
                population: tokens[2].toInteger(),
                capital: tokens[3] == 'Y',
                longitude: tokens[4].toDouble(),
                latitude: tokens[5].toDouble()
            )

            city.location = gf.createPoint( new Coordinate( city.longitude, city.latitude ) )

            if ( !city.save() )
            {
              city.errors.allErrors.each { println messageSource.getMessage( it, null ) }
            }
          }
          else
          {
            columnNames = tokens
          }
        }
      }
    }

    Hints.putSystemDefault( Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE )
  }

  def destroy = {
  }
}
