hefest.controller('MapController', ['$scope', '$attrs', function($scope, $attrs) {     
	'use strict';
	this.layers = [];
	this.layersWithHover = [];
	this.layersWithSelect = [];
	
	this.setMap = function(map) {	
		this.map = map;
		map.addLayers(this.layers);
		for(var i=0, l = this.layers.length; i < l; i++) {
			map.setLayerIndex(this.layers[i], i);
		}
	};
	
	this.getMap = function() {
		return this.map;
	};
	
	this.addLayer = function(layer) {
		this.layers.push(layer);			 	
    };		
    
    this.addLayerToHoverCtrl = function(layer) {
    	this.layersWithHover.push(layer);
    };
    
    this.getLayersWithHover = function() {
    	return this.layersWithHover;
    };
    
    this.addLayerToSelectCtrl = function(layer) {
    	this.layersWithSelect.push(layer);
    };
    
    this.getLayersWithSelect = function() {
    	return this.layersWithSelect;
    };
}]);

hefest.directive('olMap', ['$window', function ($window) {
	'use strict';
    return {
    	require: '^olMap',
        restrict: 'A',
        scope: {
        	extent: '=',
        	center: '=',
        	updateSize: '=',
        	onHover: '&',
        	selectedFeature: '=',
        	mapControl: '=',
        	zoom: '=',
        	onMoveEnd: '&'
        },
        controller: 'MapController',
        link: function(scope, element, attrs, mapCtrl) {
        	scope.internalMapControl = scope.mapControl || {};
        	var map = new OpenLayers.Map(attrs.id, {
				projection : 'EPSG:3857',
					 
				layers : [new OpenLayers.Layer.Google("Google Streets", // the default
				{
					numZoomLevels : 20
				}),  new OpenLayers.Layer.Google("Google Physical", {
					type : google.maps.MapTypeId.TERRAIN
				}), new OpenLayers.Layer.Google("Google Hybrid", {
					type : google.maps.MapTypeId.HYBRID,
					numZoomLevels : 20
				}), new OpenLayers.Layer.Google("Google Satellite", {
					type : google.maps.MapTypeId.SATELLITE,
					numZoomLevels : 22
				}) ],
				center : new OpenLayers.LonLat(16.851311, 44.754535)
				// Google.v3 uses web mercator as projection, so we have to
				// transform our coordinates
				.transform('EPSG:4326', 'EPSG:3857'),
				zoom : 9
	    	});
        	
        	mapCtrl.setMap(map);
        	       
	    	angular.element($window).bind('resize',function(){					
				setTimeout( function() { map.updateSize();});
			});
	    	
	    	scope.$watch('updateSize', function() {
				setTimeout( function() { map.updateSize();});			
			 });
	    	
	    	setTimeout( function() { map.updateSize();});			
	    	
	    	map.addControl(new OpenLayers.Control.LayerSwitcher());
				    	
			var onHover = function(e) {				
				scope.onHover({event: e});
			};
			
			var afterHover = function(e) {
				var feature = e.feature;
				if (feature && feature.popup) {
					map.removePopup(feature.popup);
					feature.popup.destroy();
					feature.popup = null;
				}
			};
	    		    	
			var highlightCtrl = new OpenLayers.Control.SelectFeature(mapCtrl.getLayersWithHover(), {
				hover : true,
				highlightOnly : true,
				renderIntent : "temporary",
				eventListeners : {
					featurehighlighted : onHover,
					featureunhighlighted : afterHover
				}
			});

			scope.internalMapControl.showPopup = function (feature, content) {
				var size = new OpenLayers.Size(250, 80);
				var popup = new OpenLayers.Popup.FramedCloud("popup", new OpenLayers.LonLat(feature.attributes.long, feature.attributes.lat)
						.transform('EPSG:4326', 'EPSG:3857'), size, content, null, false, null);	
				feature.popup = popup;
				popup.autoSize = false;
				popup.maxSize = new OpenLayers.Size(300, 600);
				map.addPopup(popup, true);
			};

			map.addControl(highlightCtrl);
			highlightCtrl.activate();
			
			var beforeFeatureSelect = function(feature) {
				if (feature && feature.popup) {
					map.removePopup(feature.popup);
					feature.popup.destroy();
					feature.popup = null;
				}
				if (feature.attributes.count) {
					var z = map.getZoom();			
					var n = 0;
					if (z <= 8) { 
						n = 3; 
					} else if (z <= 12) { 
						n = 2; 
					} else { 
						n = 1; 
					} 
					map.setCenter(new OpenLayers.LonLat(feature.attributes.long, feature.attributes.lat).transform('EPSG:4326', 'EPSG:3857'), z + n, false, true);
				} else {				
					scope.selectedFeature = feature;
					setTimeout( function() {scope.$apply();});				
				}
			};	
			
			var onFeatureUnselect = function(feature) {
				scope.selectedFeature = null;
				setTimeout( function() {scope.$apply();});
			};
			
			var selectCtrl = new OpenLayers.Control.SelectFeature(mapCtrl.getLayersWithSelect(), {
				clickout : true,
				onBeforeSelect : beforeFeatureSelect,
				onUnselect : onFeatureUnselect
			});
			
			map.addControl(selectCtrl);
			selectCtrl.activate();
			
			scope.$watch('selectedFeature', function(feature) {
				selectCtrl.unselectAll();
				if (feature) {
					selectCtrl.select(feature);
				} 
			});
			
			scope.$watch('center', function(center) {
				if(center) {
					map.setCenter(new OpenLayers.LonLat(center.long, center.lat).transform('EPSG:4326', 'EPSG:3857'), center.zoom, false, true);
				}						
			});
				
			map.events.register("moveend", map, function() {
				scope.zoom = map.getZoom();		
				scope.extent = map.getExtent().transform('EPSG:3857', 'EPSG:4326');
				setTimeout(function() {				
					scope.$apply();
					scope.onMoveEnd();
				});				
			});	
        }
      };
}]);

hefest.directive('olLayer', function () {
	'use strict';
    return {
    	require: '^olMap',
        restrict: 'E',
        scope: {
        	name: '@',
        	hover: '@',
        	select: "@",
        	layerControl:'=',
        	features: '=',
        	label: '&',
        	graphic: '&'	
        },
        
        link: function(scope, element, attrs, mapCtrl) {          	
			var	styleMap = new OpenLayers.StyleMap({
				'default' : new OpenLayers.Style({
					graphicWidth : "${getGraphicWidth}",
					graphicHeight : "${getGraphicHeight}",
					graphicOpacity: "${getGraphicOpacity}",
					externalGraphic : "${getGraphic}",
					fontSize : "${getFontSize}",
					fontFamily : "Helvetica",
					fontWeight : "bold",
					fontColor : "white",
					label : attrs.label ? "${getLabel}" : "${" + attrs.label + "}",
					labelAlign: "${getLabelAlign}",
					fillColor: "green",
					labelXOffset : "${getLabelXOffset}",
					labelYOffset : "${getLabelYOffset}",
					labelOutlineColor : "blue",
//						labelOutlineWidth : 3
				}, {
					context : {							
						getLabel : function(feature) {							
							return feature.attributes.count ? feature.attributes.count : scope.label({feature: feature });
						},							
						getGraphic : function(feature) {
							return feature.attributes.count ? "img/map/cluster_default.png" : attrs.graphic ? scope.graphic({feature: feature }) : feature.attributes.graphic;
						},
						getGraphicOpacity : function(feature) {
							return feature.attributes.count ? 0.92 : 0.8;
						},							
						getGraphicWidth : function(feature) {
							return feature.attributes.count ? 30 + (feature.attributes.count.toString().length) * 20 : 65;
						},
						getGraphicHeight : function(feature) {
							return feature.attributes.count ? 30 + (feature.attributes.count.toString().length) * 20 : 27;
						},
						getLabelXOffset : function(feature) {
							return feature.attributes.count ? -1 : 5;
						},
						getLabelYOffset : function(feature) {
							return feature.attributes.count ? 0 : 0;
						},
						getLabelAlign: function (feature) {
							return feature.attributes.count ? "cm" : "cm";
						},
						getFontSize: function (feature) {
							return feature.attributes.count ? "15px" : "12px";
						}
					}
				}),
				'temporary' : new OpenLayers.Style({
					graphicWidth : "${getGraphicWidth}",
					graphicHeight : "${getGraphicHeight}",
					labelAlign: "cm",
					labelXOffset : "${getLabelXOffset}",
					cursor: "pointer"
				}, {
					context : {							
						getGraphicWidth : function(feature) {
							return feature.attributes.count ? 20 + (feature.attributes.count.toString().length) * 20 : 65;
						},
						getGraphicHeight : function(feature) {
							return feature.attributes.count ? 20 + (feature.attributes.count.toString().length) * 20 : 27;
						}, 
						getLabelXOffset : function(feature) {
							return feature.attributes.count ? -1 : 5;
						}
					}
				}),
				'select' : new OpenLayers.Style({
					externalGraphic : "${getGraphic}",
					labelAlign: "cm",
					cursor: "pointer",
					graphicOpacity: 0.9,
				}, {
					context : {			
						getGraphic : function(feature) {
							return feature.attributes.count ? "img/map/cluster_default.png" : attrs.graphic ? scope.graphic({feature: feature, isSelect: true }) : feature.attributes.graphic;
						}
					}
				})	
			});
			styleMap.extendDefault = "true";
			var vectorLayer = new OpenLayers.Layer.Vector(scope.name, {styleMap: styleMap});
        	
        	scope.internalLayerControl = scope.layerControl || {};
        	scope.internalLayerControl.getFeaturesByAttribute = function (attribute, value) {
        		return vectorLayer.getFeaturesByAttribute(attribute, value);
        	};

        	scope.internalLayerControl.refresh = function() {
        		vectorLayer.redraw();
        	};
        	
            mapCtrl.addLayer(vectorLayer);
            
            if (attrs.hover && attrs.hover === "true") {
            	mapCtrl.addLayerToHoverCtrl(vectorLayer);            	
            }
            
            if (attrs.select && attrs.select === "true") {
            	mapCtrl.addLayerToSelectCtrl(vectorLayer);            	
            }
            
	    	scope.$watch('features', function(items) {	 	
	            var map = mapCtrl.getMap();
	    		vectorLayer.removeAllFeatures();
	  	        while( map.popups.length ) {
  					map.removePopup(map.popups[0]);	  				
	  	        }
	    		var features = [];
	    		for(var i=0, l = items.length; i < l; i++){
	    		    var item = items[i];
	    		    var point = new OpenLayers.Geometry.Point(item.long, item.lat);
					point.transform('EPSG:4326', 'EPSG:3857');
					var feature = new OpenLayers.Feature.Vector(point);				
					feature.attributes = item;
					features.push(feature);		
	    		}	    		
	    		
	    		vectorLayer.addFeatures(features);		
	    	
	    		vectorLayer.refresh();        
			 }, true);	    
	    		
        }
    };  	
});