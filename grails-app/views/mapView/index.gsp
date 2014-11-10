<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 11/2/14
  Time: 9:25 AM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Cities Map View</title>
    <meta name="layout" content="main"/>
    <style type="text/css">
    .mapContainer {
        width: 1024px;
        height: 512px;
        border: thin solid #255b17;
        margin: auto;
    }
    </style>
    <link href="http://openlayers.org/en/v3.0.0/css/ol.css" rel="stylesheet"/>
</head>

<body>
<div class="nav">
    <ul>
        <li><g:link class="home" uri="/">Home</g:link></li>
    </ul>
</div>

<div class="content">
    <h1>Cities</h1>

    <div class="mapContainer">
        <div id="map"></div>
    </div>
</div>
<script type="text/javascript" src="http://openlayers.org/en/v3.0.0/build/ol.js"></script>
<asset:javascript src="jquery"/>
<g:javascript>
    $( 'body' ).ready( function ()
    {
        var layers = [
            new ol.layer.Tile( {
                source: new ol.source.TileWMS( {
                    url: 'http://demo.opengeo.org/geoserver/wms',
                    params: {
                        'LAYERS': 'ne:NE1_HR_LC_SR_W_DR'
                    }
                } )
            } ),
/*
            new ol.layer.Tile( {
                source: new ol.source.TileWMS( {
                    url: "${createLink( controller: 'city', action: 'getTile' )}",
                    params: {
                        VERSION: '1.1.1',
                        SRS: 'EPSG:4326',
                        LAYERS: 'city'
                    }
                } ),
                extent: ol.extent.buffer( [-180, -90, 180, 90], 0 )
            } ),
*/
            new ol.layer.Tile( {
                source: new ol.source.TileWMS( {
                    url: "${createLink( controller: 'city', action: 'getHeatMapTile' )}",
                    params: {
                        VERSION: '1.1.1',
                        SRS: 'EPSG:4326',
                        LAYERS: 'city'
                    }
                } ),
                extent: ol.extent.buffer( [-180, -90, 180, 90], 0 )
            } )

        ];

        var map = new ol.Map( {
            controls: ol.control.defaults().extend( [
                new ol.control.ScaleLine( {
                    units: 'degrees'
                } )
            ] ),
            layers: layers,
            target: 'map',
            view: new ol.View( {
                projection: 'EPSG:4326',
                center: [0, 0],
                zoom: 2
            } )
        } );
    } );
</g:javascript>
</body>
</html>