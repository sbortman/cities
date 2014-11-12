package cities

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

import geoscript.geom.Bounds
import geoscript.process.Process
import geoscript.render.Map as GeoScriptMap
import geoscript.style.ColorMap
import geoscript.workspace.Directory
import geoscript.workspace.Workspace
import static geoscript.style.Symbolizers.*

class TileService
{
  static transactional = false
  def dataSource

  enum RenderMode {
    BLANK, GEOSCRIPT
  }

  def getTile(def params)
  {
    println params

    def width = params['WIDTH'].toInteger()
    def height = params['HEIGHT'].toInteger()
    def format = params['FORMAT'].split( '/' )[-1]
    def srs = params['SRS']
    def bbox = params['BBOX'].split( ',' )*.toDouble() as Bounds

    def renderMode = RenderMode.GEOSCRIPT
    def buffer = new ByteArrayOutputStream()

    switch ( renderMode )
    {
    case RenderMode.BLANK:
      def image = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB )

      ImageIO.write( image, format, buffer )
      break
    case RenderMode.GEOSCRIPT:
      def postgis = Workspace.getWorkspace(
          dbtype: 'postgis',
          host: '',
          port: '5432',
          user: '',
          password: '',
          database: '',
          "Data Source": dataSource
      )

      def cities = postgis[params['LAYERS']]

      cities.style = shape( type: "star", size: 10, color: "#FF0000" )

      def map = new GeoScriptMap(
          width: width,
          height: height,
          type: format,
          proj: srs,
          bounds: bbox,
          layers: [cities]
      )
      map.render( buffer )
      map.close()
      postgis.close()
      break
    }

    return buffer.toByteArray()
  }

  def getHeatMapTile(def params)
  {
    println params

    def width = params['WIDTH'].toInteger()
    def height = params['HEIGHT'].toInteger()
    def format = params['FORMAT'].split( '/' )[-1]
    def srs = params['SRS']
    def bbox = params['BBOX'].split( ',' )*.toDouble() as Bounds

    bbox.setProj( srs )

    def renderMode = RenderMode.GEOSCRIPT
    def buffer = new ByteArrayOutputStream()

    switch ( renderMode )
    {
    case RenderMode.BLANK:
      def image = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB )

      ImageIO.write( image, format, buffer )
      break
    case RenderMode.GEOSCRIPT:
      def postgis = Workspace.getWorkspace(
          dbtype: 'postgis',
          host: '',
          port: '5432',
          user: '',
          password: '',
          database: '',
          "Data Source": dataSource
      )

      def cities = postgis[params['LAYERS']]
      def proc = new Process( "vec:Heatmap" )

      def raster = proc.execute(
          data: cities,
          radiusPixels: 20,
          weightAttr: 'population',
          pixelsPerCell: 1,
          outputBBOX: bbox,
          outputWidth: width,
          outputHeight: height
      )?.result

      raster.style = new ColorMap( [
          [color: "#FFFFFF", quantity: 0, label: "nodata", opacity: 0],
          [color: "#FFFFFF", quantity: 0.02, label: "nodata", opacity: 0],
          [color: "#4444FF", quantity: 0.1, label: "nodata"],
          [color: "#FF0000", quantity: 0.5, label: "values"],
          [color: "#FFFF00", quantity: 1.0, label: "values"]
      ] ).opacity( 0.25 )

      def map = new GeoScriptMap(
          width: width,
          height: height,
          type: format,
          proj: srs,
          bounds: bbox,
          layers: [raster]
      )
      map.render( buffer )
      map.close()
      postgis.close()
      break
    }

    return buffer.toByteArray()
  }

  def getReferenceTile(def params)
  {
    println params

    def width = params['WIDTH'].toInteger()
    def height = params['HEIGHT'].toInteger()
    def format = params['FORMAT'].split( '/' )[-1]
    def srs = params['SRS']
    def bbox = params['BBOX'].split( ',' )*.toDouble() as Bounds
    def buffer = new ByteArrayOutputStream()
    def shpDir = new Directory( 'data' )
    def countries = shpDir['world_adm0']
    def states = shpDir['statesp020']
    def style = fill(opacity: 0) + stroke(color: '#00FF00')

    countries.style = style
    states.style = style

    def map = new GeoScriptMap(
        width: width,
        height: height,
        type: format,
        proj: srs,
        bounds: bbox,
        layers: [countries, states]
    )
    map.render( buffer )
    map.close()

    shpDir.close()

    return buffer.toByteArray()

  }
}
