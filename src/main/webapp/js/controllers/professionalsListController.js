'use strict';

hefest.controller('ListController', [ '$scope', 'Professional', 'Utility', '$log', '$q', function($scope, Professional, Utility, $log, $q) {

	$scope.detailsShown = false;
	
	$scope.params = {};
	$scope.filters = {};

	$scope.currentProfessional = '';

	$scope.$on('mapProfessionalSelected', function(event, professionalid) {
		if($scope.currentProfessional.professionalid !== professionalid) {
			openProfessionalDetails(professionalid, -1);
		}
	});
	
	$scope.$on('mapProfessionalUnSelected', function(event) {
		$scope.detailsShown = false;
	});
	
	$scope.$on('getProfessionalsForList', function(event, filters) {
		$scope.filters = {};
		angular.extend($scope.filters, filters);
		$scope.currentPage = 1;
		$scope.currentProfessional = '';
		$scope.detailsShown = false;
		$scope.getProfessionals();
	});
	
	$scope.getProfessionals = function () {
		$scope.loadingData = true;
		if ($scope.listCanceler !== undefined) {
			$scope.listCanceler.resolve();
			$scope.listCanceler = $q.defer();
		} else {
			$scope.listCanceler = $q.defer();
		}
		
		$scope.professionals = [];
		$scope.queryParams = {};
		angular.extend($scope.queryParams, $scope.params);
		angular.extend($scope.queryParams, $scope.filters);
		$scope.queryParams.offset = $scope.currentPage > 1 ? $scope.queryParams.limit * ($scope.currentPage-1) : "";
		Professional.query($scope.queryParams).$promise.then(				
			function(professionalsResponse) {					
				$scope.professionals = Utility.getObjectsFromMetadata(professionalsResponse.metadata[1], professionalsResponse.data[1]);				
				$scope.resultsTotal = professionalsResponse.total;
				$scope.$parent.results.total = professionalsResponse.total;
				if ($scope.currentProfessional !== '') {
					if($scope.currentProfessional.index === 0) {						
						$scope.currentProfessional = $scope.professionals[0];
					} else if ($scope.currentProfessional.index === -1) {
						$scope.currentProfessional = $scope.professionals[$scope.professionals.length - 1];
						$scope.currentProfessional.index = $scope.professionals.length - 1;
					}				
					$scope.selectProfessional($scope.currentProfessional);
				}
				$scope.loadingData = false;
			}
		);
	};
	
	$scope.sortBy = function(sortOption) {
		$scope.sortedBy = sortOption;
		$scope.params.orderbycolumn = sortOption.value;
		$scope.params.order = sortOption.order;
		$scope.currentPage = 1;
		$scope.getProfessionals();
	};

	$scope.$watch('detailsShown', function(newValue, oldValue) {
		if (newValue === false) {
			$scope.$emit('listProfessionalSelectionClear', $scope.currentProfessional.professionalid);
			$scope.currentProfessional = '';
		}
	});

	$scope.next = function() {
		var nextIndex = $scope.currentProfessional.index + 1;
		if(nextIndex < $scope.professionals.length) {
			$scope.selectProfessional($scope.professionals[nextIndex]);
		} else if ($scope.numPages === 1){
			$scope.selectProfessional($scope.professionals[0]);
		} else {
			$scope.currentProfessional.index = 0;
			$scope.currentPage = $scope.currentPage < $scope.numPages ? $scope.currentPage + 1 : 1;
		}
	};
 
	$scope.previous = function() {
		var previousIndex = $scope.currentProfessional.index - 1; 
		if (previousIndex > -1 ) {
			$scope.selectProfessional($scope.professionals[previousIndex]);
		} else if ($scope.numPages === 1){
			$scope.selectProfessional($scope.professionals[$scope.professionals.length - 1]);
		} else {
			$scope.currentProfessional.index = -1;
			$scope.currentPage = $scope.currentPage - 1 > 0 ? $scope.currentPage -1 : $scope.numPages;
		}
	};
	
	$scope.selectProfessional = function(professional) {
		$scope.currentProfessional = professional;
		openProfessionalDetails(professional.professionalid, professional.index);
		$scope.$emit('listProfessionalSelected', professional);
	};

	var openProfessionalDetails = function(professionalId, professionalCurrentIndex) {
		$scope.loadingData = true;
		Professional.get({professionalId: professionalId}).$promise.then(function(professionalResponse) {		
			$scope.detailsShown = true;
			$scope.currentProfessional = professionalResponse;
			$scope.currentProfessional.index = professionalCurrentIndex;
			$scope.loadingData = false;
		});
	};
		
	$scope.sortOptions = [
  	    {
  	    	label: 'orderby.nameasc',
  	    	value: 'name',
  	    	order: 'ASC'
  	    },
  	    {
  	    	label: 'orderby.namedesc',
  	    	value: 'name',
  	    	order: 'DESC'
  	    },
  	    {
  	    	label: 'professionals.orderby.hefestscore',
  	    	value: 'score',
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
  	    	label: 'professionals.orderby.reviews',
  	    	value: 'avgreview'
  	    }
  	];
	
	$scope.sortedBy = $scope.sortOptions[0];
	$scope.params.orderbycolumn = $scope.sortOptions[0].value;
	$scope.params.order = $scope.sortOptions[0].order;
	$scope.params.limit = 20;
	
	$scope.currentPage = 1;
	
	$scope.$watch('currentPage', function(newPage, oldPage){
		if(newPage !== oldPage) {
		   $scope.currentPage = newPage;
		   $scope.getProfessionals();
		}
	  });
	
	$scope.queryParams = $scope.params;
	
	$scope.$emit('listReady');
	
	$scope.openRequestForProposal = function() {
		
	};
}]);