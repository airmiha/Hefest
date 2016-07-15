hefest.controller('ServicesFilterController', ['$scope', 'Professions', 'ServiceCategories', 'ServiceCategory',
	function($scope, Professions, ServiceCategories, ServiceCategory) {
		'use strict';
		$scope.professions = {};
	
		var rootItem = {};
		rootItem.itemid = -1;
		rootItem.name = "";
		
		$scope.selectedProfession = {};
		$scope.selectedItem = rootItem;
		$scope.items = [];
		$scope.level = 0;
		
		$scope.$on('selectItem', function(event, item) {
			if(!$scope.professionsArrayR) {
				$scope.selectedItem = item;
			} else if($scope.selectedItem.itemid !== item.itemid) {
				$scope.selectItem(item, false);
			}
		});
		
		$scope.$on('selectAllProfessions', function() {
			$scope.selectAllProfessions(false);
		});
	
		$scope.selectAllProfessions = function(emitEvent) {
			$scope.items = $scope.professionsArray;
			$scope.level = 0;
			$scope.selectedItem = rootItem;
			if(emitEvent) {
				$scope.$emit('itemSelected', rootItem);
			}						
		};
		
		$scope.selectProfession = function(item) {
			var profession = $scope.professions[item.itemid];
			if (profession.serviceCategories === undefined) {
				$scope.items = [];
				$scope.loadingItems = true;
				ServiceCategories.query({professionId : profession.id})
		         .$promise.then(function(serviceCategories) {			        
					profession.serviceCategories = serviceCategories;
					$scope.items = profession.serviceCategories;
					$scope.loadingItems = false;
				});	
			} else {
				$scope.items = profession.serviceCategories;
			}
			$scope.level = 1;
			$scope.selectedItem = profession;
			profession.categorytype = 'professions';
			$scope.selectedProfession = profession;											
		};
		
		$scope.selectServiceCategory = function(item) {
			$scope.selectedItem = item;
			$scope.loadingItems = true;
			ServiceCategory.get({serviceCategoryId : item.itemid}).$promise.then(function(serviceCategory) {			        
				var profession = $scope.professions[serviceCategory.professionid];
				$scope.selectedProfession = profession;
				$scope.items = serviceCategory.services;
				$scope.level = 2;
				$scope.loadingItems = false;
			});	
		};
		
		$scope.selectTreeItem = function(item) {
			if($scope.level === 0) {
				item.categorytype = 'professions';					
			} else if ($scope.level === 1) {
				item.categorytype = 'servicecategories';				
			}
			$scope.selectItem(item, true);
		};
				
		$scope.selectItem = function(item, emitEvent) {
			if(item.categorytype === 'professions') {
				$scope.selectProfession(item);					
			} else if (item.categorytype === 'servicecategories') {
				$scope.selectServiceCategory(item);					
			}
			if (emitEvent) {
				$scope.$emit('itemSelected', item);
			}
		};
				
		var getProfessions = function() {
			$scope.loadingItems = true;
			Professions.query().$promise.then(function(professions) {
				for(var i=0, l = professions.length; i < l; i++) {
					$scope.professions[professions[i].itemid] = professions[i];
				}
				$scope.professionsArray = professions;
				$scope.items = $scope.professionsArray;
				$scope.loadingItems = false;
				if($scope.selectedItem) {
					$scope.selectItem($scope.selectedItem, false);
				}
			});
		};
		
		getProfessions();
} ]);