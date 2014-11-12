package cities

class MapViewController
{
  def tileService

  def index() {}

  def getReferenceTile()
  {
    render contentType: 'image/png', file: tileService.getReferenceTile( params )
  }
}
