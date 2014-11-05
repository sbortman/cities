package cities

class CityController
{
  def scaffold = true
  def tileService

  def getTile()
  {
    render contentType: 'image/png', file: tileService.getTile( params )
  }
}
