hefest.controller('UpdateUserProfileController', 
		['$scope', '$location', 'Professions', 'Localities', 'ServiceCategories', 'ServiceRequests',
	function($scope, $location, Professions, Localities, ServiceCategories, ServiceRequests) {
		'use strict';
		$scope.professions = {};
		$scope.serviceCategories = [];
		$scope.serviceRequests = [];
		$scope.selectedServices = [];
		
		$scope.request = {};
		
		$scope.service = {};
		$scope.service.currencies = ["€", "Kn"];
			
		$scope.isActive = function (viewLocation) { 
	        return viewLocation === $location.path();
	    };
	    
	    Professions.query().$promise.then(function(professions) {
			$scope.professions = professions;
			$scope.service.profession = $scope.professions['0']; 
		});	
	    
		$scope.selectProfession = function() {
			var profession = $scope.service.profession;
			if (profession.serviceCategories === undefined) {				
				ServiceCategories.query({professionId : profession.id})
		         .$promise.then(function(serviceCategories) {			        
					profession.serviceCategories = serviceCategories;
					$scope.serviceCategories = serviceCategories;
					$scope.service.serviceCategory = $scope.serviceCategories[0];
				});	
			} else {
				$scope.serviceCategories = profession.serviceCategories;
				$scope.service.serviceCategory = $scope.serviceCategories[0];
			}										
		};
		
		$scope.getLocalities = function(value) {
			return Localities.query({filter: value.trim()}).$promise.then(function(localities) {
				return localities;
			});				
		};
		
		$scope.minDate = new Date();
		
		$scope.open = function($event) {
			    $event.preventDefault();
			    $event.stopPropagation();

			    $scope.opened = true;
		};
		
		ServiceRequests.query().$promise.then(function(serviceRequests) {
			$scope.serviceRequests = serviceRequests;
		});	
		
		$scope.postServiceRequest = function (isValid) {
			if(!isValid) {
				return;
			}
			
			$scope.request.$save(function (value, responseHeaders) {
	               $scope.error = null;
	               $scope.success = 'OK';
	           },
	           function (httpResponse) {
	               $scope.success = null;
	               $scope.error = "ERROR";
	        });			
		};
		
		$scope.add = function(service) { 		
			if ($scope.selectedServices.indexOf(service) === -1) {
				$scope.selectedServices.push(service);
		     }     
		};
		
		$scope.remove = function(service) { 
			  var index = $scope.selectedServices.indexOf(service);
			  $scope.selectedServices.splice(index, 1);     
		};
	} 
]);