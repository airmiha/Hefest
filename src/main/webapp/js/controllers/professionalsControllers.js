'use strict';

hefest.controller('ProfessionalsController', [
	'$scope', '$rootScope', 'Professional', 'Municipalities', 'Items', 'Utility', '$timeout', '$q', '$modal',
	function($scope, $rootScope, Professional, Municipalities, Items, Utility, $timeout, $q, $modal) {
		
		$scope.status = {};
		$scope.center = {long: 16.072404750000367, lat: 45.42321746787017, zoom: 10};
		$scope.professionals = [];
		$scope.params = {};
		$scope.results = {};
		
		$scope.highlight = [];
		$scope.mapControl = {};
		$scope.layerControl = {};
		
		$scope.selectedProfessional = null;
		
		$scope.orderBy = "score";
		
		if ($rootScope.professionals) {
			$scope.selectedMunicipality = $rootScope.professionals.municipality;
			$scope.selectedSearchItem = $rootScope.professionals.searchItem;
		}
		
		$scope.$on('itemSelected', function(event, item) {
			$scope.selectedSearchItem = item;
			getProfessionals(true);
		});
		
		$scope.$on('listReady', function(event) {
			$scope.isListReady = "ready";
			if ($scope.extent) {				
				$scope.getProfessionals();
			} else {
				$scope.isListReady = 'pending';
			}
		});	
		
		$scope.$on('listProfessionalSelected', function(event, professional) {
			$scope.selectedProfessional = professional;
			$scope.selectedProfessional.professionalid =  professional.professionalid + ".0";
			//TODO: Temporary fix
			var features = $scope.layerControl.getFeaturesByAttribute("professionalid", professional.professionalid + ".0");
			if(features.length > 0) {
				$scope.selectedFeature = features[0];
			} else {						
				$scope.center = {lat: professional.lat, long: professional.long, zoom: 14};
			}
		});

		$scope.$on('listProfessionalSelectionClear', function(event, professionalId) {
			$scope.selectedProfessional = null;
			$scope.selectedFeature = null;				
		});
		
		$scope.getMunicipalities = function(value) {
			return Municipalities.query({filter: value.trim()}).$promise.then(function(municipalities) {
				return municipalities;
			});				
		};
		
		$scope.getItems = function(query) {
			return Items.query({tags: query.trim().split(" ")}).$promise.then(function(items) {
				return items;
			});				
		};
		
		$scope.getProfessionals = function() {
			getProfessionals(true);
		};

		$scope.$watch('selectedFeature', function(feature) {					
			$scope.highlight = [];
			if (feature) {
				var newSelectedProfessionalId = feature.attributes.professionalid;				
				if ($scope.selectedProfessional === null || $scope.selectedProfessional.professionalid !== newSelectedProfessionalId) {				
					$scope.selectedProfessional = feature.attributes;					
					$scope.$broadcast('mapProfessionalSelected', feature.attributes.professionalid.substring(0, newSelectedProfessionalId.length -2));
				}
			} else {
				$scope.selectedProfessional = null;
				$scope.$broadcast('mapProfessionalUnSelected');
			}				
		});
		
		var getProfessionals = function() {						
			var params = {};
			params.minEmployees = $scope.filters.minEmployees.value;
			params.minYearsinBusiness = $scope.filters.minYearsinBusiness.value;
			params.acceptscreditcards= $scope.filters.acceptscreditcards;
			
			if ($scope.selectedMunicipality && $scope.selectedMunicipality !== '') {
				if (typeof $scope.selectedMunicipality === 'object') {				
					params.countyid  = $scope.selectedMunicipality.countyid;
					params.municipalityname = $scope.selectedMunicipality.name;
				} else {
					params.municipality = $scope.selectedMunicipality; 
					params.municipalityname = $scope.selectedMunicipality; 
				}
			}
			
			if ($scope.selectedSearchItem && $scope.selectedSearchItem !== '') {
				if (typeof $scope.selectedSearchItem === 'object') {
					if( $scope.selectedSearchItem.categorytype === 'professionals') {
						params.professionalid = $scope.selectedSearchItem.itemid;
						params.itemname = $scope.selectedSearchItem.name;							
						$scope.$broadcast('selectAllProfessions');
					} else if ($scope.selectedSearchItem.categorytype === 'professions') {									
						params.itemid = $scope.selectedSearchItem.itemid;
						params.itemname = $scope.selectedSearchItem.name;
						$scope.$broadcast('selectItem', $scope.selectedSearchItem);
					} else if ($scope.selectedSearchItem.categorytype === 'servicecategories') {						
						params.itemid = $scope.selectedSearchItem.itemid;
						params.itemname = $scope.selectedSearchItem.name;
						$scope.$broadcast('selectItem', $scope.selectedSearchItem);
					}
				} else {
					var tags= $scope.selectedSearchItem.trim().split(" ");
					params.itemname = $scope.selectedSearchItem;
					params.tags = tags;	
					$scope.$broadcast('selectAllProfessions');
				}	
			} else {
				$scope.$broadcast('selectAllProfessions');
			}
			
			$scope.$broadcast('getProfessionalsForList', params);
			
			$scope.params = params;		
			$scope.getProfessionalsForMap();							
		};			
		
		$scope.getProfessionalsForMap = function() {
			if(!$scope.isListReady) {				
				return;
			} else if ($scope.isListReady === 'pending') {
				$scope.isListReady = 'ready';
				$scope.getProfessionals();
				return;
			}
			
			$scope.loadingMapData = true;
			$scope.summaryClosed = false;
			var params = $scope.params;
			
			params.minLat = $scope.extent.bottom;
			params.minLong = $scope.extent.left;
			params.maxLat = $scope.extent.top;
			params.maxLong = $scope.extent.right;
			params.zoom = $scope.zoom;
					
			//TODO: Request cancelling with resource
//				if ($scope.mapCanceler != undefined) {
//					$scope.mapCanceler.resolve();
//					$scope.mapCanceler = $q.defer();
//				} else {
//					$scope.mapCanceler = $q.defer();
//				}
			
			Professional.query(params).$promise.then(			
				function(professionalsResponse) {										
					var features = Utility.getObjectsFromMetadata(professionalsResponse.metadata[0], professionalsResponse.data[0]);						 
					features = features.concat(Utility.getObjectsFromMetadata(professionalsResponse.metadata[1], professionalsResponse.data[1]));					
					$scope.professionals = features;	
					$scope.loadingMapData = false;
					setTimeout(function() {		
						if ($scope.selectedProfessional) {
							var features = $scope.layerControl.getFeaturesByAttribute("professionalid", $scope.selectedProfessional.professionalid);		
							if (features.length > 0) {
								$scope.selectedFeature = features[0];
								$scope.$apply();
							}								
						}							
					});				
				}
			);
		};
		
		$scope.getLabel = function (feature) {
			if(feature) {					
				return feature.attributes[$scope.selectedMetric.value];
			}				
		};
		
		$scope.getGraphic = function (feature, isSelect) {
			if(feature) {					
				return isSelect ?  "img/map/professional-select.png" : "img/map/" + getIconForMetric($scope.selectedMetric.value, feature.attributes[$scope.selectedMetric.value]);
			}				
		};
		
		$scope.onHover = function(e) {				
			if(e.feature && !e.feature.attributes.count) {
				$scope.mapControl.showPopup(e.feature, getPopupContent(e.feature));
			}
		};
	
		var getPopupContent = function(feature) {
			var attributes = feature.attributes;
			var content = '<div style="float: left"><img src=' + 'img/map/' + 'professionalleadimage.png' +
					' width=60 height=50 style="margin-right:20px; margin-top: 5px"></div>';
			/*content	+= '<span class="badge popupBadge">' + attributes.score + '</span></div>';*/
			content	+= '<span class="heading"><strong>' + attributes.name + '</strong></span></br>';
			content	+= '<span class="professions">' + attributes.professions + '</span></br>';
			content	+= '<span class="addressline"><strong>' + attributes.addressline + '</strong></span>';
			return content;
		};
		
		$scope.metrics = [
    	    {
    	    	label: 'professionals.orderby.yearsinbusiness',
    	    	value: 'yearsinbusiness'
    	    },
    	    {
    	    	label: 'professionals.orderby.reviews',
    	    	value: 'reviewcount'
    	    },
    	    {
    	    	label: 'professionals.orderby.endorsements',
    	    	value: 'endorsementcount'
    	    },
    	    {
    	    	label: 'professionals.orderby.projects',
    	    	value: 'projectcount'
    	    },
    	    {
    	    	label: 'professionals.orderby.hefestscore',
    	    	value: 'score'
    	    }
    	];
		
		$scope.onSelectedMetric = function(metric) {
			$scope.selectedMetric = metric;
			$scope.activeRange = metricColorsForRanges[$scope.selectedMetric.value];
			setTimeout(function() {
				$scope.layerControl.refresh();
			});
		};
		
		$scope.selectedMetric = $scope.metrics[0];
		
		var metricColorsForRanges = {
			yearsinbusiness: {
				middle: 2,
				top: 10
			},
			reviewcount: {
				middle: 20,
				top: 50
			},
			endorsementcount: {
				middle: 20,
				top: 50
			},
			projectcount: {
				middle: 20,
				top: 50
			}, 
			score: {
				middle: 200,
				top: 250
			}
		};	
		
		var getIconForMetric = function(metric, value) {
			var ranges = metricColorsForRanges[metric];
			if (!ranges) {
				return 'professional-default.png';
			}
			if (value < ranges.middle) {
				return 'professional-red.png';
			} else if (value >= ranges.middle && value < ranges.top) {
				return 'professional-orange.png';
			} else {
				return 'professional-green.png';
			}
		};
		
		$scope.activeRange = metricColorsForRanges[$scope.selectedMetric.value];
		
		$scope.openScoreDetails = function() {
			$modal.open({
				templateUrl : 'partials/professional/scoreDetails.html',
				controller : 'ScoreDetailsController'
			});
		};
				
		$scope.filters = {};
		
		$scope.distances = [
		    {
		    	label: '2 km',
		    	value: '2'
		    },
		    {
		    	label: '5 km',
		    	value: '5'
		    },
		    {
		    	label: '10 km',
		    	value: '10'
		    },
		    {
		    	label: '20 km',
		    	value: '20'
		    },		            	    
		    {
		    	label: '100 km',
		    	value: '100'
		    },
		    {
		    	label: '500 km',
		    	value: '500'
		    }
	    ];
		
		$scope.filters.distance = $scope.distances[0];
		
		$scope.yearsinBusinessFilter = [
            {
 		    	label: '-',
 		    	value: null
 		    },
		    {
		    	label: '2+',
		    	value: '2'
		    },
		    {
		    	label: '5+',
		    	value: '5'
		    },
		    {
		    	label: '10+',
		    	value: '10'
		    },
		    {
		    	label: '20+',
		    	value: '20'
		    }
		];
	
		
		$scope.filters.minYearsinBusiness = $scope.yearsinBusinessFilter[0];
		
		$scope.employeeFilter = [
             {
 		    	label: '-',
 		    	value: null
 		    },
		    {
		    	label: '2+',
		    	value: '2'
		    },
		    {
		    	label: '5+',
		    	value: '5'
		    },
		    {
		    	label: '10+',
		    	value: '10'
		    },
		    {
		    	label: '20+',
		    	value: '20'
		    },		            	    
		    {
		    	label: '50+',
		    	value: '50'
		    }
		];
				
		$scope.filters.minEmployees = $scope.employeeFilter[0];
		
		$scope.resetFilters = function() {	
			$scope.filters.distance = $scope.distances[0];
			$scope.filters.minYearsinBusiness = $scope.yearsinBusinessFilter[0];
			$scope.filters.minEmployees = $scope.employeeFilter[0];
			$scope.filters.centerPoint = '';
			$scope.filters.location = '';
			$scope.filters.acceptscreditcards = '';
		};
		
		
	} 
]);

hefest.controller('ScoreDetailsController', [ '$scope',  function($scope) {
	$scope.getWeight = function(magnitude) {
		var m = new Array(magnitude);
	    return m;
	};
}]);
