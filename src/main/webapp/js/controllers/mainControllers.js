hefest.controller('MainNavigationCtrl', ['$rootScope', '$scope', 'Session', '$location', '$modal', function($rootScope, $scope, Session, $location, $modal) {
	'use strict';
	$scope.openSignIn = function() {
		$modal.open({
			templateUrl : 'partials/account/signInModal.html',
			controller : 'SignInController'
		});
	};
	
	$scope.openProfessionalSignUp = function() {
		$rootScope.registrationType = 'professional';
		$scope.modalInstance = $modal.open({
			templateUrl : 'partials/account/signUpModal.html',
			controller : 'SignUpController'
		});
	};	
	
	$scope.openUserSignUp = function() {
		$rootScope.registrationType = 'user';
		$scope.modalInstance = $modal.open({
			templateUrl : 'partials/account/signUpModal.html',
			controller : 'SignUpController'
		});
	};
} ]);

hefest.controller('MainController', function($rootScope, $scope, $state, Municipalities, Items) {
	'use strict';
	
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
		$rootScope.professionals= {municipality: $scope.selectedMunicipality, searchItem: $scope.selectedSearchItem};	
		$state.go('majstori');
	};
});

hefest.controller('SignInController', [ '$scope', '$rootScope', '$modal', '$modalInstance', 'AuthenticationSharedService', function($scope, $rootScope, $modal, $modalInstance, AuthenticationSharedService) {
	'use strict';
	$scope.user = {};
	$scope.openSignUp = function() {
		$rootScope.registrationType = 'user';
		$modalInstance.close();
		$modal.open({
			templateUrl : 'partials/account/signUpModal.html',
			controller : 'SignUpController'
		});
	};
	
	$scope.signIn = function(isValid) {
		$scope.$broadcast('show-errors-check-validity');
		if(!isValid) {
			return;
		}
		AuthenticationSharedService.login({
             username: $scope.user.email,
             password: $scope.user.password,
             rememberMe: $scope.user.rememberMe
        });
	};
	
	// Call when the the client is confirmed
    $rootScope.$on('event:auth-loginConfirmed', function() {
    	$modalInstance.close();
    });
	
	$scope.forgotPassword = function() {
		$modalInstance.close();
		$modal.open({
			templateUrl : 'partials/account/forgotPasswordModal.html',
			controller : 'ForgotPasswordController'
		});
	};
} ]);

hefest.controller('ForgotPasswordController', [ '$scope', '$rootScope', '$modal', '$modalInstance', 'ForgotPassword', function($scope, $rootScope, $modal, $modalInstance, ForgotPassword) {
	'use strict';
	$scope.user = {};
	$scope.request = {};
	
	$scope.sendResetLink = function(isValid) {
		$scope.$broadcast('show-errors-check-validity');
		if(!isValid) {
			return;
		}
		$scope.request.email = $scope.user.email;
		ForgotPassword.save($scope.user.email,
           function (value, responseHeaders) {
               $scope.error = null;
               $scope.success = 'OK';
           },
           function (httpResponse) {
               $scope.success = null;
               $scope.error = "ERROR";
        });
	};
}]);

hefest.controller('ResetPasswordController', ['$scope', '$stateParams', 'ResetPassword',
    function ($scope, $stateParams, ResetPassword) {      
		'use strict';
		$scope.newPassword = {};
		$scope.newPassword.token = $stateParams.key;
        $scope.resetPassword = function (isValid) {
        	$scope.$broadcast('show-errors-check-validity');
    		if(!isValid) {
    			return;
    		}
	 		if ($scope.newPassword.password !== $scope.newPassword.repeat) {
	 			$scope.doNotMatch = "ERROR";
	 		} else {
	           $scope.doNotMatch = null;
	           ResetPassword.save($scope.newPassword,
	               function (value, responseHeaders) {
	                   $scope.error = null;
	                   $scope.success = 'OK';
	               },
	               function (httpResponse) {
	                   $scope.success = null;
	                   $scope.error = "ERROR";
	               }
	          );
 	       }
    };
}]);

hefest.controller('ChangePasswordController', ['$scope', 'Password',
   function ($scope, Password) {
	'use strict';
       $scope.changePassword = function (isValid) {
    	   $scope.$broadcast('show-errors-check-validity');
    	   if(!isValid) {
    		   return;
    	   }   	   
	       if ($scope.newPassword !== $scope.confirmPassword) {
	           $scope.doNotMatch = "ERROR";
	       } else {
	           $scope.doNotMatch = null;
	           var password = {};
	           password.newPassword = $scope.newPassword;
	           password.oldPassword = $scope.oldPassword;
	           Password.save(password,
	               function (value, responseHeaders) {
	                   $scope.error = null;
	                   $scope.success = 'OK';
	               },
	               function (httpResponse) {
	                   $scope.success = null;
	                   $scope.error = "ERROR";
	               });
	       }
   };
}]);

hefest.controller('SignUpController', ['$rootScope', '$scope', '$modal', '$modalInstance', 'AuthenticationSharedService', 'Professional', 'User',  function($rootScope, $scope, $modal, $modalInstance, AuthenticationSharedService, Professional, User) {
	'use strict';
	$scope.error = null;
	$scope.errorUserExists = null;
	$scope.doNotMatch = null;
	$scope.newUser = {name:'n/a'};
	$scope.repeat = {};

	if($rootScope.registrationType === 'professional') {
		$scope.isProfessional = true;
		$scope.newUser = {};
	}
	
	$scope.signUp = function (isValid) {
		$scope.$broadcast('show-errors-check-validity');
		if(!isValid) {
			return;
		}
		
		if ($scope.newUser.password !== $scope.repeat.repeatPassword) {
			 $scope.doNotMatch = "ERROR";
			 return;
		}
		
		if($scope.isProfessional) {
			Professional.save($scope.newUser,
	             function (value, responseHeaders) {
					onSuccess(value, responseHeaders);
	             },
	             function (httpResponse) {            	
	            	 onFailure(httpResponse);
	             }
	        );
		} else {
			User.save($scope.newUser,
	             function (value, responseHeaders) {
					onSuccess(value, responseHeaders);
	             },
	             function (httpResponse) {            	
	            	 onFailure(httpResponse);
	             }
	        );
		}
		
		var onSuccess = function (value, responseHeaders) {
			 $modalInstance.close();
			 $scope.error = null;
             $scope.errorUserExists = null;
         	 AuthenticationSharedService.login({
                username: $scope.newUser.email,
                password: $scope.newUser.password,
                rememberMe: true
           });
         };
         
         var onFailure = function (httpResponse) {            	
             if (httpResponse.status === 304) {
            	 $scope.error = null;
                 $scope.errorUserExists = "ERROR";
             } else {
            	 $scope.error = "ERROR";
                 $scope.errorUserExists = null;
             }
         };
	};

	$scope.openSignIn = function() {
		$modalInstance.close();
		$modal.open({
			templateUrl : 'partials/account/signInModal.html',
			controller : 'SignInController'
		});
	};
}]);

hefest.controller('LogoutController', ['$location', 'AuthenticationSharedService',
   function ($location, AuthenticationSharedService) {
	'use strict';   
	AuthenticationSharedService.logout();
}]);
   
hefest.controller('ActivationController', ['$scope', '$stateParams', 'Activate',
   function ($scope, $stateParams, Activate) {
	'use strict';   
	Activate.get({key: $stateParams.key},
           function (value, responseHeaders) {
               $scope.error = null;
               $scope.success = 'OK';
           },
           function (httpResponse) {
               $scope.success = null;
               $scope.error = "ERROR";
            }
       );
}]);

hefest.controller('ActivationController', ['$scope', '$modal', function ($scope, $modal) {
	'use strict';
	$scope.openProfessionalSignupModal = function() {	
		$scope.modalInstance = $modal.open({
			templateUrl : 'partials/account/signUpModal.html',
			controller : 'SignUpController'
		});
	};
}]);