hefest.controller('ProfessionalsProfileController', [
	'$scope', '$rootScope', '$stateParams', 'Professional', 'Projects', 'Endorsements', 'Testimonials', 'Utility', '$timeout', '$q', '$modal',
	function($scope, $rootScope, $stateParams, Professional, Projects, Endorsements, Testimonials, Utility, $timeout, $q, $modal) {
		
		Professional.get({professionalId: $stateParams.professionalId}).$promise.then(function(professionalResponse) {
			$scope.professional = professionalResponse;
			$scope.serviceCategories = [];
			var serviceCategories = [];
			for (var id in $scope.professional.professionsAndServices) {
				if($scope.professional.professionsAndServices.hasOwnProperty(id)) {
					var selectedProfession = $scope.professional.professionsAndServices[id];
					for (var categoryId in selectedProfession.serviceCategories) {
						if(selectedProfession.serviceCategories.hasOwnProperty(categoryId)) {
							var serviceCategory = selectedProfession.serviceCategories[categoryId];
							serviceCategory.itemid = categoryId;
							serviceCategories.push(serviceCategory);
						}					
					}
				}
			}
			$scope.serviceCategories = serviceCategories;
			$scope.getProjects();
			$scope.getEndorsements();
			$scope.getTestimonials();
		});
		
		$scope.requestProposal = function() {
			
		};
		
		$scope.filterProjects= function(itemId) {					
	        $scope.projectParams.itemid = itemId;
	        $scope.getProjects();
		};

		$scope.isProjectFilterActive = function(itemId) { 		 
	      return $scope.projectParams.itemid === itemId;
		};
		
		$scope.filterEndorsements= function(role) {					
	        $scope.endorsementParams.role = role;
	        $scope.getEndorsements();
		};

		$scope.isEndorsementFilterActive = function(role) { 		 
	      return $scope.endorsementParams.role === role;
		};
			
		$scope.projectParams = {};
		$scope.endorsementParams = {};
		$scope.testimonialParams = {};
		
		$scope.projectSortOptions = [
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
    	    	label: 'orderby.datedesc',
    	    	value: 'dateperformed',
    	    	order: 'DESC'
    	    },
    	    {
    	    	label: 'orderby.dateasc',
    	    	value: 'dateperformed',
    	    	order: 'ASC'
    	    },  	    
    	    {
    	    	label: 'orderby.costasc',
    	    	value: 'cost',
    	    	order: 'ASC'
    	    },
    	    {
    	    	label: 'orderby.costasc',
    	    	value: 'cost',
    	    	order: 'DESC'
    	    },
    	    {
    	    	label: 'orderby.images',
    	    	value: 'imagecount'
    	    },
    	    {
    	    	label: 'orderby.comments',
    	    	value: 'commentcount'
    	    },
    	    {
    	    	label: 'orderby.likes',
    	    	value: 'likescount'
    	    },
    	];
      	
      	$scope.projectsSortedBy = $scope.projectSortOptions[0];
      	$scope.projectParams.orderbycolumn = $scope.projectSortOptions[0].value;
      	$scope.projectParams.order = $scope.projectSortOptions[0].order;
      	$scope.projectParams.limit = 12;
      	$scope.projectParams.professionalid = $stateParams.professionalId;
     
     	$scope.endorsementParams.limit = 12;
      	$scope.endorsementParams.professionalId = $stateParams.professionalId;
      	
      	$scope.testimonialParams.limit = 12;
      	$scope.testimonialParams.professionalId = $stateParams.professionalId;
      	
      	$scope.currentProjectPage = 1;
      	$scope.currentEndorsementPage = 1;
      	$scope.currentTestimonialPage = 1;
      	
      	$scope.$watch('currentProjectPage', function(newPage, oldPage){
      		if(newPage !== oldPage) {
      		   $scope.currentProjectPage = newPage;
      		   $scope.getProjects();
      		}
      	});
      	
      	$scope.$watch('currentEndorsementPage', function(newPage, oldPage){
      		if(newPage !== oldPage) {
      		   $scope.currentEndorsementPage = newPage;
      		   $scope.getEndorsements();
      		}
      	});
      	
      	$scope.$watch('currentTestimonialPage', function(newPage, oldPage){
      		if(newPage !== oldPage) {
      		   $scope.currentTestimonialPage = newPage;
      		   $scope.getTestimonials();
      		}
      	});
      	
    	$scope.sortProjectsBy = function(sortOption) {
    		$scope.projectsSortedBy = sortOption;
    		$scope.projectParams.orderbycolumn = sortOption.value;
    		$scope.projectParams.order = sortOption.order;
    		$scope.currentProjectPage = 1;
    		$scope.getProjects();
    	};
		              	
      	$scope.projectQueryParams = $scope.projectParams;
      	$scope.testimonialQueryParams = $scope.testimonialParams;
      	    	
    	$scope.getProjects = function () {
    		$scope.loadingProjects = true;
    		$scope.projects = [];
    		$scope.projectQueryParams = {};
    		angular.extend($scope.projectQueryParams, $scope.projectParams);
    		$scope.projectQueryParams.offset = $scope.currentProjectPage > 1 ? $scope.projectQueryParams.limit * ($scope.currentProjectPage-1) : "";
    		Projects.query($scope.projectQueryParams).$promise.then(				
    			function(projectsResponse) {					
    				$scope.projects = Utility.getObjectsFromMetadata(projectsResponse.metadata[1], projectsResponse.data[1]);				    		
    				$scope.projectResultsTotal = projectsResponse.total;
    				$scope.loadingProjects = false;
    			}
    		);
    	};
    	
    	$scope.getEndorsements = function () {
    		$scope.loadingEndorsements = true;
    		$scope.endorsements = [];
    		$scope.endorsementQueryParams = {};
    		angular.extend($scope.endorsementQueryParams, $scope.endorsementParams);
    		$scope.endorsementQueryParams.offset = $scope.currentEndorsementPage > 1 ? $scope.endorsementQueryParams.limit * ($scope.currentEndorsementPage-1) : "";
    		Endorsements.query($scope.endorsementQueryParams).$promise.then(				
    			function(endorsementsResponse) {					
    				$scope.endorsements = endorsementsResponse.data;				    		
    				$scope.endorsementResultsTotal = endorsementsResponse.total;
    				$scope.loadingEndorsements = false;
    			}
    		);
    	};
    	
    	$scope.getTestimonials = function () {
    		$scope.loadingTestimonials = true;
    		$scope.testimonials = [];
    		$scope.testimonialQueryParams = {};
    		angular.extend($scope.testimonialQueryParams, $scope.testimonialParams);
    		$scope.testimonialQueryParams.offset = $scope.currentTestimonialPage > 1 ? $scope.testimonialQueryParams.limit * ($scope.currentTestimonialPage-1) : "";
    		Testimonials.query($scope.testimonialQueryParams).$promise.then(				
    			function(testimonialsResponse) {					
    				$scope.testimonials = testimonialsResponse.data;				    		
    				$scope.testimonialResultsTotal = testimonialsResponse.total;
    				$scope.loadingTestimonials = false;
    			}
    		);
    	};
}]);