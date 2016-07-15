hefest.controller('UpdateProfessionalProfileController', 
		['$scope', 'ProfessionalAccount', 'ProfessionsAsTree', 'Counties', '$location',
	function($scope, ProfessionalAccount, ProfessionsAsTree, Counties, $location) {
		'use strict';
		$scope.localities = {};
		
		$scope.isActive = function (viewLocation) { 
	        return viewLocation === $location.path();
	    };
		
		$scope.yearsOfEstablishment = [];
		for (var i = 1965; i < 2015; i++) {
			$scope.yearsOfEstablishment.push(""+ i);
		}
		
		Counties.query().$promise.then(function(counties) {
			$scope.counties = counties;
		});	
		
		ProfessionsAsTree.get().$promise.then(function(professionsTree) {
			var professions = {};
			$scope.professionsTree = professionsTree;	
			for (var key in professionsTree) {	
				if(professionsTree.hasOwnProperty(key)) {				   		
					professions[key] = {name : professionsTree[key].name};	
				}
			 }			
			$scope.professions = professions;
		});	
		
		ProfessionalAccount.get().$promise.then(function(professional) {		
			professional.leadImage = "img/defaultcontractorimage.png";
			$scope.professional = professional;			
			for (var id in $scope.professional.professionsAndServices) {
				if($scope.professional.professionsAndServices.hasOwnProperty(id)) {
					var profession = $scope.professions[id];
					profession.expanded = true;
					$scope.initializeProfession($scope.professions[id], id);				
					var selectedProfession = $scope.professional.professionsAndServices[id];
					for (var categoryId in selectedProfession.serviceCategories) {
						if(selectedProfession.serviceCategories.hasOwnProperty(categoryId)) {
							var selectedServiceCategory = selectedProfession.serviceCategories[categoryId];
							var serviceCategory = profession.serviceCategories[categoryId];
							serviceCategory.checked = true;
							serviceCategory.description = selectedServiceCategory.description;
						}					
					}
					profession.color = {"color":"blue"};
				}
			}
		});
		
		$scope.updateProfessionalProfile = function (isValid) {
			if(!isValid) {
				return;
			}
			
			var selectedItems = [];
			for (var professionid in $scope.professions) {
				if($scope.professions.hasOwnProperty(professionid)) {
					var profession = $scope.professions[professionid];
					var selectedServiceCategoryItems = [];
					for (var categoryId in profession.serviceCategories) {
						if(profession.serviceCategories.hasOwnProperty(categoryId)) {
							var serviceCategory = profession.serviceCategories[categoryId];
							if(serviceCategory.checked) {
								selectedServiceCategoryItems.push(categoryId);
								var description = serviceCategory.description ? serviceCategory.description : "";
								selectedItems.push(categoryId + "," + description);
							}
						}
					} 
					if (selectedServiceCategoryItems.length > 0) {
						selectedItems.push(professionid + ",");
					}
				}
			}
			
			$scope.professional.items = selectedItems;

			$scope.professional.$save(function (value, responseHeaders) {
	               $scope.error = null;
	               $scope.successProfile = 'OK';
	           },
	           function (httpResponse) {
	               $scope.successProfile = null;
	               $scope.error = "ERROR";
	        });			
		};
		
		$scope.checkAllServiceCategories = function(professional){
			for(var id in professional.serviceCategories) {
				if(professional.serviceCategories.hasOwnProperty(id)) {
					professional.serviceCategories[id].checked = true;
				}
			}
		};
		
		$scope.uncheckAllServiceCategories = function(professional){
			for(var id in professional.serviceCategories) {
				if(professional.serviceCategories.hasOwnProperty(id)) {
					professional.serviceCategories[id].checked = false;
				}				
			}
		};
		
		$scope.initializeProfession = function(profession, id) {
			if(!profession.hasOwnProperty('serviceCategories')) {
				profession.serviceCategories = $scope.professionsTree[id].serviceCategories;
			}
		};
	} 
]);

hefest.controller('ProfilePicturesController', 
		['$scope', 'Professional', 
	function($scope, Professional) {
	
	}
]);