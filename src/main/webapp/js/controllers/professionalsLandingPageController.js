hefest.controller('ProfessionalsLandingPageController', 
		['$scope', '$rootScope', '$location', '$modal', '$log', 
		 function($scope, $rootScope, $location, $modal, $log) {
    'use strict';
	$scope.openSignIn = function() {

		var modalInstance = $modal.open({
			templateUrl : 'partials/signInModal.html',
			controller : 'SignInCtrl'
		});

		modalInstance.result.then(function(message) {
			$log.info(message);
		}, function() {
			$log.info('Modal dismissed at: ' + new Date());
		});
	};
} ]);